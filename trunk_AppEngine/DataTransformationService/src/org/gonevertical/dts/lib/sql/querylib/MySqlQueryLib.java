package org.gonevertical.dts.lib.sql.querylib;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gonevertical.dts.data.ColumnData;
import org.gonevertical.dts.data.DatabaseData;
import org.gonevertical.dts.data.StatData;
import org.gonevertical.dts.data.ColumnData.Type;
import org.gonevertical.dts.lib.StringUtil;


public class MySqlQueryLib implements QueryLib {

  private static final Logger log = LoggerFactory.getLogger(MySqlQueryLib.class);

	// keep track of what is going on
	private StatData stats;

	public MySqlQueryLib() {
	}

	public void setStats(StatData stats) {
		this.stats = stats;
	}

	private void setTrackSql(String sql) {
		if (stats == null) {
			return;
		}
		stats.setTrackSql(sql);
	}

	private void setTrackError(String error) {
		if (stats == null) {
			return;
		}
		stats.setTrackError(error);
	}

	/**
	 * escape string
	 * 
	 * fixes: Strings can come like this abcdefg\  and then when made into a statement 'abcdefg\'
	 * I used to trim here, but not a good idea b/c I want to replicate exactly what comes in.
	 *  
	 * @param s
	 * @return
	 */
	public String escape(String s) {
		if (s == null) {
			s = "";
		}
		// escape quotes
		s = StringUtil.escapeSql(s);

		// TODO - maybe I only need it at end of string????
		// when string looks like this column='value\' or column='value\\'
		//if (s.matches(".*[\\]") == true) {
		// TODO this needs to be fixed b/c it will send mess up \t\r\n
		s = s.replaceAll("\\\\", "\\\\\\\\");
		//}
		return s;
	}

	public String escape(int i) {
		return escape(Integer.toString(i));
	}

	public int getResultSetSize(ResultSet result) {
		int size = -1;
		try {
			result.last();
			size = result.getRow();
			result.beforeFirst();
		} catch (SQLException e) {
			System.err.println("Error: getResultSetSize()");
			setTrackError(e.toString());
			log.error("Error: resultSize: ", e);
			e.printStackTrace();
		} 
		return size;
	}

	public boolean queryBoolean(DatabaseData dd, String sql) {
		setTrackSql(sql);
		boolean b = false;
		Connection conn = null;
		Statement select = null;
		try {
			conn = dd.getConnection();
			select = conn.createStatement();
			ResultSet result = select.executeQuery(sql);
			while (result.next()) {
				b = result.getBoolean(1);
			}
			select.close();
			select = null;
			result.close();
			result = null;
			conn.close();
		} catch (SQLException e) {
			System.err.println("Error: queryBoolean(): " + sql);
			System.err.println("servletConn:" + dd.getConnetionByConext() + " Server: " + dd.getServer());
			setTrackError(e.toString());
			log.error("Error: " + sql + "\n", e);
			e.printStackTrace();
		} finally {
			conn = null;
			select = null;
		}
		return b;
	}

	public int queryInteger(DatabaseData dd, String sql) {
		setTrackSql(sql);
		int i = 0;
		Connection conn = null;
		Statement select = null;
		try {
			conn = dd.getConnection();
			select = conn.createStatement();
			ResultSet result = select.executeQuery(sql);
			while (result.next()) {
				i = result.getInt(1);
			}
			result.close();
			result = null;
			select.close();
			select = null;
			conn.close();
		} catch (SQLException e) {
			System.err.println("Error: queryInteger(): " + sql);
			System.err.println("servletConn:" + dd.getConnetionByConext() + " Server: " + dd.getServer());
			log.error("Error: " + sql + "\n", e);
			e.printStackTrace();
		} finally {
			conn = null;
			select = null;
		}
		return i;
	}

	public long queryLong(DatabaseData dd, String sql) {
		setTrackSql(sql);
		long i = 0;
		Connection conn = null;
		Statement select = null;
		try {
			conn = dd.getConnection();
			select = conn.createStatement();
			ResultSet result = select.executeQuery(sql);
			while (result.next()) {
				i = result.getLong(1);
			}
			result.close();
			result = null;
			select.close();
			select = null;
			conn.close();
		} catch (SQLException e) {
			System.err.println("Error: queryInteger(): " + sql);
			System.err.println("servletConn:" + dd.getConnetionByConext() + " Server: " + dd.getServer());
			setTrackError(e.toString());
			log.error("Error: " + sql + "\n", e);
			e.printStackTrace();
		} finally {
			conn = null;
			select = null;
		}
		return i;
	}

	public String queryString(DatabaseData dd, String sql) {
	  
	  if (sql == null || sql.length() == 0) {
	    return null;
	  }
	  
		setTrackSql(sql);
		String s = null;
		Connection conn = null;
		Statement select = null;
		try {
			conn = dd.getConnection();
			select = conn.createStatement();
			ResultSet result = select.executeQuery(sql);
			while (result.next()) {
				s = result.getString(1);
			}
			select.close();
			select = null;
			result.close();
			result = null;
			conn.close();
		} catch (SQLException e) {
			System.err.println("Error: queryString(): " + sql);
			System.err.println("servletConn:" + dd.getConnetionByConext() + " Server: " + dd.getServer());
			setTrackError(e.toString());
			log.error("Error: sql: " + sql + "\n", e);
			e.printStackTrace();
		} finally {
			conn = null;
			select = null;
		}
		return s;
	}

	public double queryDouble(DatabaseData dd, String sql) {
		setTrackSql(sql);
		double d = 0.0;
		Connection conn = null;
		Statement select = null;
		try {
			conn = dd.getConnection();
			select = conn.createStatement();
			ResultSet result = select.executeQuery(sql);
			while (result.next()) {
				d = result.getDouble(1);
			}
			result.close();
			result = null;
			select.close();
			select = null;
			conn.close();
		} catch (SQLException e) {
			System.err.println("Error: queryDouble(): " + sql);
			System.err.println("servletConn:" + dd.getConnetionByConext() + " Server: " + dd.getServer());
			setTrackError(e.toString());
			log.error("Error: " + sql + "\n", e);
			e.printStackTrace();
		} finally {
			conn = null;
			select = null;
		}
		return d;
	}

	public BigDecimal queryBigDecimal(DatabaseData dd, String sql) {
		setTrackSql(sql);
		BigDecimal bd = null;
		Connection conn = null;
		Statement select = null;
		try {
			conn = dd.getConnection();
			select = conn.createStatement();
			ResultSet result = select.executeQuery(sql);
			while (result.next()) {
				if (result.getString(1) != null) {
					bd = new BigDecimal(result.getString(1));
				} 
			}
			result.close();
			result = null;
			select.close();
			select = null;
			conn.close();
		} catch (SQLException e) {
			System.err.println("Error: queryBigDecimal(): " + sql);
			System.err.println("servletConn:" + dd.getConnetionByConext() + " Server: " + dd.getServer());
			setTrackError(e.toString());
			log.error("Error: " + sql + "\n", e);
			e.printStackTrace();
		} finally {
			conn = null;
			select = null;
		}
		return bd;
	}

	public String queryIntegersToCsv(DatabaseData dd, String sql, char delimiter) {
		setTrackSql(sql);
		String csv = null;
		Connection conn = null;
		Statement select = null;
		try {
			conn = dd.getConnection();
			select = conn.createStatement();
			ResultSet result = select.executeQuery(sql);
			int size = getResultSetSize(result);
			int i = 0;
			if (size > 0) {
				csv = "";
			}
			while (result.next()) {
				long id = result.getLong(1);
				String sid = Long.toString(id);
				csv += sid;
				if (i < size-1) { 
					csv += Character.toString(delimiter);
				}
				i++;
			}
			result.close();
			result = null;
			select.close();
			select = null;
			conn.close();
		} catch (SQLException e) {
			System.err.println("Error: queryIntegersToCsv(): " + sql);
			System.err.println("servletConn:" + dd.getConnetionByConext() + " Server: " + dd.getServer());
			setTrackError(e.toString());
			log.error("Error: " + sql + "\n", e);
			e.printStackTrace();
		} finally {
			conn = null;
			select = null;
		}
		if (csv == null | csv.length() == 0) {
			csv = "NULL";
		}
		return csv;
	}

	public String queryStringToCsv(DatabaseData dd, String sql, char delimiter) {
		setTrackSql(sql);
		String csv = null;
		Connection conn = null;
		Statement select = null;
		try {
			conn = dd.getConnection();
			select = conn.createStatement();
			ResultSet result = select.executeQuery(sql);
			int size = getResultSetSize(result);
			if (size > 0) {
				csv = "";
			}
			int i = 0;
			while (result.next()) {
				String v = result.getString(1);
				csv += "\"" + v + "\"";
				if (i < size-1) { 
					csv += Character.toString(delimiter);
				}
				i++;
			}
			result.close();
			result = null;
			select.close();
			select = null;
			conn.close();
		} catch (SQLException e) {
			System.err.println("Error: queryStringToCsv(): " + sql);
			System.err.println("servletConn:" + dd.getConnetionByConext() + " Server: " + dd.getServer());
			setTrackError(e.toString());
			log.error("Error: " + sql + "\n", e);
			e.printStackTrace();
		} finally {
			conn = null;
			select = null;
		}
		if (csv == null | csv.length() == 0) {
			csv = "NULL";
		}
		return csv;
	}

	public long update(DatabaseData dd, String sql) {
		setTrackSql(sql);
		if (sql == null) {
			return 0;
		}
		long id = 0;
		Connection conn = null;
		Statement update = null;
		try {
			conn = dd.getConnection();
			if (dd.getLoadBalance() == true) {
				conn.setReadOnly(false);
			}
			update = conn.createStatement();
			update.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
			ResultSet result = update.getGeneratedKeys();
			if (result != null && result.next()) { 
				id = result.getLong(1);
			}
			result.close();
			result = null;
			update.close();
			update = null;
			conn.close();
		} catch (SQLException e) {
			System.err.println("Error: update(): " + sql);
			System.err.println("servletConn:" + dd.getConnetionByConext() + " Server: " + dd.getServer());
			setTrackError(e.toString());
			log.error("Error: " + sql + "\n", e);
			e.printStackTrace();
		} finally {
			conn = null;
			update = null;
		}
		return id;
	}

	public long update(DatabaseData dd, String sql, boolean getKey) {
		setTrackSql(sql);
		if (sql == null) {
			return 0;
		}
		long id = 0;
		Connection conn = null;
		Statement update = null;
		try {
			conn = dd.getConnection();
			if (dd.getLoadBalance() == true) {
				conn.setReadOnly(false);
			}
			update = conn.createStatement();
			update.executeUpdate(sql);

			if (getKey == true) {
				ResultSet result = update.getGeneratedKeys();
				if (result != null && result.next()) { 
					id = result.getLong(1);
				}
				result.close();
				result = null;
			}
			update.close();
			update = null;
			conn.close();
		} catch (SQLException e) {
			System.err.println("Error: update(): " + sql);
			System.err.println("servletConn:" + dd.getConnetionByConext() + " Server: " + dd.getServer());
			setTrackError(e.toString());
			log.error("Error: " + sql + "\n", e);
			e.printStackTrace();
		} finally {
			conn = null;
			update = null;
		}
		return id;
	}

	public boolean queryStringAndConvertToBoolean(DatabaseData dd, String sql) {
		setTrackSql(sql);
		String value = null;
		Connection conn = null;
		Statement select = null;
		try {
			conn = dd.getConnection();
			select = conn.createStatement();
			ResultSet result = select.executeQuery(sql);
			while (result.next()) {
				value = result.getString(1);
			}
			select.close();
			select = null;
			result.close();
			result = null;
			conn.close();
		} catch (SQLException e) {
			System.err.println("Mysql Statement Error:" + sql);
			System.err.println("servletConn:" + dd.getConnetionByConext() + " Server: " + dd.getServer());
			setTrackError(e.toString());
			log.error("Error: " + sql + "\n", e);
			e.printStackTrace();
		} finally {
			conn = null;
			select = null;
		}
		boolean b = false;
		if ((value != null && value.length() > 0) && 
				value.equals("0") == false | 
				value.toLowerCase().equals("false") == false) {
			b = true;
		}
		return b;
	}

	public boolean queryLongAndConvertToBoolean(DatabaseData dd, String sql) {
		setTrackSql(sql);
		long l = 0;
		Connection conn = null;
		Statement select = null;
		try {
			conn = dd.getConnection();
			select = conn.createStatement();
			ResultSet result = select.executeQuery(sql);
			while (result.next()) {
				l = result.getLong(1);
			}
			select.close();
			select = null;
			result.close();
			result = null;
			conn.close();
		} catch (SQLException e) {
			System.err.println("Mysql Statement Error:" + sql);
			System.err.println("servletConn:" + dd.getConnetionByConext() + " Server: " + dd.getServer());
			setTrackError(e.toString());
			log.error("Error: " + sql + "\n", e);
			e.printStackTrace();
		} finally {
			conn = null;
			select = null;
		}
		boolean b = false;
		if (l > 0) {
			b = true;
		}
		return b;
	}

	public String getType() {
		return "MySql";
	}

	public Date queryDate(DatabaseData dd, String sql, Calendar cal) {
		setTrackSql(sql);
		java.sql.Date sqlDate = null;
		Connection conn = null;
		Statement select = null;
		try {
			conn = dd.getConnection();
			select = conn.createStatement();
			ResultSet result = select.executeQuery(sql);
			while (result.next()) {
				sqlDate = result.getDate(1, cal);
			}
			select.close();
			select = null;
			result.close();
			result = null;
			conn.close();
		} catch (SQLException e) {
			System.err.println("Mysql Statement Error:" + sql);
			System.err.println("servletConn:" + dd.getConnetionByConext() + " Server: " + dd.getServer());
			setTrackError(e.toString());
			log.error("Error: " + sql + "\n", e);
			e.printStackTrace();
		} finally {
			conn = null;
			select = null;
		}

		Date date = null;
		if (sqlDate != null) {
			long time = sqlDate.getTime();
			date = new Date(time);
		}
		return date;
	}

	

}
