package org.gonevertical.dts.lib.errorcheck;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gonevertical.dts.data.ColumnData;
import org.gonevertical.dts.data.DatabaseData;
import org.gonevertical.dts.lib.sql.columnlib.ColumnLib;
import org.gonevertical.dts.lib.sql.columnmulti.ColumnLibFactory;
import org.gonevertical.dts.lib.sql.querylib.QueryLib;
import org.gonevertical.dts.lib.sql.querymulti.QueryLibFactory;
import org.gonevertical.dts.lib.sql.transformlib.TransformLib;
import org.gonevertical.dts.lib.sql.transformmulti.TransformLibFactory;

public class CompareTablesThread implements Runnable {

  private static final Logger log = LoggerFactory.getLogger(CompareTablesThread.class);

	//source database settings
	private DatabaseData database_src = null;

	// destination database settings
	private DatabaseData database_des = null;

	private QueryLib ql_src = null;
	private TransformLib tl_src = null;
	private ColumnLib cl_src = null;
	private QueryLib ql_des = null;
	private TransformLib tl_des = null;
	private ColumnLib cl_des = null;

	private ColumnData[] columnData_src = null;
	private ColumnData[] columnData_des = null;

	private String tableLeft = null;
	private String tableRight = null;

	private String srcWhere = null;

	private ColumnData[] columnIdentities;

	private String sqlOrderBy;

	private boolean failure = false;
	private int failureCount = 0;

	private long index;

	private long offset;
	private long limit;

	private ColumnData[] columnSkip;

	public CompareTablesThread(DatabaseData database_src, DatabaseData database_dest) {
		this.database_src = database_src;
		this.database_des = database_dest;
		setSupportingLibraries();
	}

	/**
	 * guice injects the libraries needed for the database
	 */
	private void setSupportingLibraries() {
		ql_src = QueryLibFactory.getLib(database_src.getDatabaseType());
		cl_src = ColumnLibFactory.getLib(database_src.getDatabaseType());
		tl_src = TransformLibFactory.getLib(database_src.getDatabaseType());
		ql_des = QueryLibFactory.getLib(database_des.getDatabaseType());
		cl_des = ColumnLibFactory.getLib(database_des.getDatabaseType());
		tl_des = TransformLibFactory.getLib(database_des.getDatabaseType());
	}

	public void setData(String tableLeft, String tableRight, String srcWhere,
			ColumnData[] columnIdentities, ColumnData[] columnSkip) {
		this.tableLeft = tableLeft;
		this.tableRight = tableRight;
		this.srcWhere = srcWhere;
		this.columnIdentities = columnIdentities;
		this.columnSkip = columnSkip;
		setColumnData_All();
	}

	private void setColumnData_All() {
		columnData_src = tl_src.queryColumns(database_src, tableLeft, null);

		// skip these columns
		if (columnSkip != null) {
			columnData_src = cl_src.prune(columnData_src, columnSkip);
		}

		columnData_des = new ColumnData[columnData_src.length];
		for(int i=0; i < columnData_src.length; i++) {

			// skip these    	
			columnData_des[i] = new ColumnData();
			columnData_des[i] = (ColumnData) columnData_src[i].clone();
			columnData_des[i].setTable(tableRight);

			// use identities instead of primary key
			if (columnIdentities != null) {
				int index = cl_src.searchColumnByName_UsingComparator(columnIdentities, columnData_des[i]);
				if (index >= 0) {
					columnData_src[i].setIdentity(true);
				}
			}
		}

	}

	public void setOffset(long offset, long limit, long index) {
		this.index = index;
		this.offset = offset;
		this.limit = limit;
	}

	public void run() {
		processSrc();
	}

	private void processSrc() {

		ColumnData primKey = cl_src.getPrimaryKey_ColumnData(columnData_src);

		String where = "WHERE (1=1) ";

		String columnCsv = cl_src.getSql_Names_WSql(columnData_src, null);

		String sql = "";
		sql = "SELECT " + columnCsv + " FROM " + tableLeft + " ";
		sql += where;
		sql += getSrcWhere();
		sql += getOrderBy();
		sql += " LIMIT " + offset + ", " + limit + ";";

		log.info(sql);

		Connection conn = null;
		Statement select = null;
		try {
			conn = database_src.getConnection();
			select = conn.createStatement();
			ResultSet result = select.executeQuery(sql);
			while (result.next()) {

				// get values 
				for (int i=0; i < columnData_src.length; i++) {
					String value = result.getString(columnData_src[i].getColumnName());
					columnData_src[i].setValue(value);
				}
				process();
				index--;
			}
			result.close();
			select.close();
			conn.close();
			result = null;
			select = null;
			conn = null;
		} catch (SQLException e) {
			System.err.println("Mysql Statement Error:" + sql);
			e.printStackTrace();
		} finally {
			conn = null;
			select = null;
		}
	}

	private String getOrderBy() {
		String s = "";
		if (sqlOrderBy != null) {
			s = " " + sqlOrderBy + " ";
		}
		return s;
	}

	private void process() {

		getDestinationValuesForComparison();

		String s = "";
		boolean overallRowMatch = true;
		for (int i=0; i < columnData_src.length; i++) {

			String leftValue = columnData_src[i].getValue();
			String rightValue = columnData_des[i].getValue();

			boolean bmatch = false;
			if (leftValue == null && rightValue == null) {
				bmatch = true;

			} else if (leftValue == null | rightValue == null) {
				bmatch = false;

			} else if (leftValue.equals(rightValue) == true) {
				bmatch = true;

			} else {
				bmatch = false;
			}

			s += "(" + columnData_src[i].getName() + ":" + leftValue + "==" + rightValue + ":" + bmatch + "), ";

			if (bmatch == false) {
				overallRowMatch = false;
			}

		}

		log.info(index + ". Overall: " + overallRowMatch + " ::: " + s);

		if (overallRowMatch == false) {
			failure = true;
			failureCount++;

			// this is overkill
			//log.error("CompareTables.process(): Data is not matching. Overall: " + overallRowMatch + " ::: " + s);
		}

	}

	private void getDestinationValuesForComparison() {

		String srcPrimKeyValue = cl_src.getPrimaryKey_Value(columnData_src);
		String desPrimKeyColName = cl_des.getPrimaryKey_Name(columnData_des);

		String where = "";
		if (columnIdentities != null) {
			where = getSqlWhereForIdents();
		} else {
			where = "(" + desPrimKeyColName + "='" +  ql_src.escape(srcPrimKeyValue) + "')";
		}


		String columnCsv = cl_src.getSql_Names_WSql(columnData_src, null);

		String sql = "SELECT " + columnCsv + " FROM " + tableRight + " WHERE " + where;


		log.trace("RIGHT:" + sql);

		boolean b = false;
		Connection conn = null;
		Statement select = null;
		try {
			conn = database_des.getConnection();
			select = conn.createStatement();
			ResultSet result = select.executeQuery(sql);
			while (result.next()) {
				for (int i=0; i < columnData_des.length; i++) {
					String value = result.getString(columnData_des[i].getColumnName());
					columnData_des[i].setValue(value);
					b = true;
				}
			}
			result.close();
			select.close();
			conn.close();
			result = null;
			select = null;
			conn = null;
		} catch (SQLException e) {
			System.err.println("Mysql Statement Error:" + sql);
			e.printStackTrace();
		}

		// reset to nulls if found nothing
		if (b == false) {
			for (int i=0; i < columnData_des.length; i++) {
				String s = null;
				columnData_des[i].setValue(s);
			}
		}
	}

	private String getSrcWhere() {
		String s = "";
		if (srcWhere != null && srcWhere.length() > 0) {
			s = " AND " + srcWhere;
		}
		return s; 
	}

	private String getSqlWhereForIdents() {
		String sql = cl_src.getSql_IdentitiesWhere(columnData_src);
		return sql;
	}

	public int getFailureCount() {
		return failureCount;
	}

}
