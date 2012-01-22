package org.gonevertical.dts.data;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gonevertical.dts.lib.sql.columnlib.ColumnLib;
import org.gonevertical.dts.lib.sql.querylib.QueryLib;
import org.gonevertical.dts.lib.sql.transformlib.TransformLib;
import org.gonevertical.dts.v2.CsvProcessing;

public class StatData {
	
  private static final Logger log = LoggerFactory.getLogger(StatData.class);
	
	private ColumnLib cl;
	private QueryLib ql;
	private TransformLib tl;
	
	/**
	 * database connection
	 */
	private DatabaseData dd;
	
	/**
	 * working on this file
	 */
	private File srcFile;
	
	/**
	 * putting the data here
	 */
	private String dstTable;
	
	/**
	 * track micro time start
	 */
	private long startTime;
	
	/**
	 * track micro time end
	 */
	private long endTime;
	
	/**
	 * how many times sql statement was null (ERROR)
	 */
	private long sqlNullCount = 0;
	
	/**
	 * total sql statements used
	 */
	private long sqlTotalCount = 0;
	
	/**
	 * how many times insert was used
	 */
	private long sqlInsertCount = 0;
	
	/**
	 * how many times update was used
	 */
	private long sqlUpdateCount = 0;
	
	/**
	 * how many times alter used
	 */
	private long sqlAlterCount = 0;
	
	/**
	 * how many times the sql update/insert was done
	 */
	private long saveCount = 0;
	
	/**
	 * rows gone through
	 */
	private long rowCount = 0;
	
	/**
	 * how many lines in the file
	 */
	private long fileLineCount = 0;
	
	/**
	 * keep track of errors
	 */
	private ArrayList<String> errors = new ArrayList<String>();

	/**
	 * constructor
	 */
	public StatData() {
	}

	/**
	 * set supporting libraries (to save to table)
	 * 
	 * @param cl
	 * @param ql
	 * @param tl
	 */
	public void setSupportingLibraries(ColumnLib cl, QueryLib ql, TransformLib tl) {
		this.cl = cl;
		this.ql = ql;
		this.tl = tl;
  }
	
	/**
	 * start the stats - set the time
	 */
	public void start() {
		reset();
		startTime = new Date().getTime();
	}
	
	private void reset() {
		startTime = 0;
		endTime = 0;
		sqlNullCount = 0;
		sqlTotalCount = 0;
		sqlInsertCount = 0;
		sqlUpdateCount = 0;
		sqlAlterCount = 0;
		saveCount = 0;
		rowCount = 0;
		fileLineCount = 0;
		errors = new ArrayList<String>();
	}
	
	/**
	 * end the stats - set the end time
	 */
	public void end() {
		endTime = new Date().getTime();
	}
	
	/**
	 * track sql
	 * 
	 * @param sql
	 */
	public void setTrackSql(String sql) {
		
		if (sql == null) {
			sqlNullCount ++;
			return;
		}
		
		sqlTotalCount++;
		
		if (sql.toLowerCase().contains("insert") ==  true) {
			sqlInsertCount++;
		} else if (sql.toLowerCase().contains("update") == true) {
			sqlUpdateCount++;
		} else if (sql.toLowerCase().contains("alter") == true) {
			sqlAlterCount++;
		}
		
	}
	
	/**
	 * increment save count
	 */
	public void setSaveCount() {
		saveCount++;
	}
	
	/**
	 * increment row count
	 */
	public void setAddRowCount() {
		rowCount++;
	}
	
	/**
	 * save an error
	 * 
	 * @param error
	 */
	public void setTrackError(String error) {
		errors.add("rowIndex: " + rowCount + " :: " + error + "\n "+ this.toString());
		
		log.error("StatData().setTrackError:\n" + error + "\n " + this.toString());
	}
	
	/**
	 * how many lines in the file?
	 * 
	 * @param fileLineCount
	 */
	public void setFileLineCount(long fileLineCount) {
		this.fileLineCount = fileLineCount;
	}
	
	/**
	 * output to console
	 */
	public void print() {
		System.out.println(this.toString());
		log.info(this.toString());
	}
	
	public String toString() {
		long diff = endTime - startTime;
		
		String s = "";
		s += "startTime: " + startTime + " endTime: " + endTime + " diffTime: " + diff + "\n";
		s += "fileLineCount: " + fileLineCount + "\n";
		s += "rowCount: " + rowCount+ "\n";
		s += "sqlStatementsTotalCount: " + sqlTotalCount + "\n";
		s += "sqlStatementAlterCount: " + sqlAlterCount + "\n";
		s += "sqlStatementInsertCount: " + sqlInsertCount + "\n";
		s += "sqlStatementUpdateCount: " + sqlUpdateCount + "\n";
		s += "sqlStatementNullCount: " + sqlNullCount + "\n";
		s += "saveStatementsCount: " + saveCount + "\n";
		s += "ErrorCount: " + errors.size();
		
		return s;
	}
	
	public void setRowCount(int rowCount) {
		this.rowCount = rowCount;
  }

	public long getFileLineCount() {
		return fileLineCount;
  }

	public void saveToTable(DatabaseData dd, boolean logToTable, String loggingTable) {
		if (logToTable == false || loggingTable == null) {
			return;
		}
		this.dd = dd;
		
		// create table and fields if needed
		createLoggingTable(loggingTable);
		
		// write the stuff to the table
		writeToTable(loggingTable);
  }

	private void createLoggingTable(String table) {
		String primaryKeyName = "Auto_LogId";
		tl.createTable(dd, table, primaryKeyName);
		
		ColumnData c1 = new ColumnData(table, "DateCreated", "DATETIME DEFAULT NULL");
		ColumnData c2 = new ColumnData(table, "SrcFile", "VARCHAR(255) DEFAULT NULL");
		ColumnData c3 = new ColumnData(table, "DestTable", "VARCHAR(255) DEFAULT NULL");
		ColumnData c4 = new ColumnData(table, "Description", "TEXT DEFAULT NULL");
		ColumnData c5 = new ColumnData(table, "Errors", "TEXT DEFAULT NULL");
		
		tl.createColumn(dd, c1);
		tl.createColumn(dd, c2);
		tl.createColumn(dd, c3);
		tl.createColumn(dd, c4);
		tl.createColumn(dd, c5);
	}
	
	private void writeToTable(String table) {
		
		String sql = "INSERT INTO " + table + " SET " +
				"DateCreated=NOW()," +
				"SrcFile=" + getSrcFile() + "," +
				"DestTable=" + getDestTable() + "," +
				"Description='" + ql.escape(toString()) + "'," +
				"Errors=" + getErrors() + ";";
		
		log.info(sql);
		
		ql.update(dd, sql);
	}
	
	private String getDestTable() {
	  String s = null;
	  if (dstTable != null) {
	  	s = "'" + ql.escape(dstTable) + "'";
	  }
	  return s;
  }

	private String getSrcFile() {
		String s = null;
		if (srcFile != null) {
			s = "'" + ql.escape(srcFile.getAbsolutePath()) + "'";
		}
	  return s;
  }

	private String getErrors() {
		
		String s = null;
		
		if (errors.size() > 0) {
			s += "'";
		}
		
		for (int i=0; i < errors.size(); i++) {
			s += ql.escape(errors.get(i));
			s += "~~~~~~~~~~~~~~~~~~\n";
		}
		
		if (errors.size() > 0) {
			s += "'";
		}
		
		return s;
	}
	
	public void setSourceFile(File file) {
		this.srcFile = file;
	}
	
	public void setDestTable(String table) {
		this.dstTable = table;
	}

	/**
	 * does the line count match the insert/update count
	 * @return
	 */
	public boolean doesLineCountMatchSaveCount(SourceData sd) {
		boolean b = false;
		
		long flc = fileLineCount;
		
		if (sd.getIgnoreFirstRow() == false && sd.getHeadersFile() == null) {
			flc--;
		} 
		
		if (sd.getIgnoreLastRow() == true) {
			flc--;
		}
		
		if (flc == saveCount) { 
			b = true;
		}
		
	  return b;
  }
	
	public boolean getHasErrors() {
		boolean b = false;
		if (errors != null && errors.size() > 0) {
			b = true;
		}
		return b;
	}
	
	public boolean hasUpdatesAndInserts() {
		boolean b = false;
		
		if (sqlInsertCount > 0) {
			b = true;
		}
		
		if (sqlUpdateCount > 0) {
			b = true;
		}
		
		return b;
	}

	public long getSaveCount() {
	  return saveCount;
  }
	
}
