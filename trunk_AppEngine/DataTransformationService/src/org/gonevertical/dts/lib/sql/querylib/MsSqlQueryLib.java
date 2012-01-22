package org.gonevertical.dts.lib.sql.querylib;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;


import org.apache.commons.lang3.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gonevertical.dts.data.DatabaseData;
import org.gonevertical.dts.data.StatData;
import org.gonevertical.dts.lib.StringUtil;
import org.gonevertical.dts.lib.sql.columnmulti.Run_Multi_Column_Test;


/**
 * under development
 * 
 * @author branflake2267
 *
 */
public class MsSqlQueryLib implements QueryLib {
	
  private static final Logger log = LoggerFactory.getLogger(MsSqlQueryLib.class);

	// keep track of what is going on
  private StatData stats;

	public MsSqlQueryLib() {
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
   * @param s
   * @return
   */
  public  String escape(String s) {
    if (s == null) {
      s = "";
    }
    // escape quotes
    s = StringUtil.escapeSql(s);
    s = s.trim();
    
    // when string looks like this column='value\' or column='value\\'
    char bs = "\\".charAt(0);
    if (s.matches(".*[" + bs + bs + "]") == true) {
      s = s.replaceAll("[" + bs + bs + "]+$", "\\\\\\\\");
    }
    return s;
  }
  

  /**
   * escape Integer - really of no use but consistency between writing values into sql
   * 
   * @param i
   * @return
   */
  public  String escape(int i) {
    return escape(Integer.toString(i));
  }
  
  /**
   * get result set size
   * 
   *   TODO - last time I checked this did not work on MS resultset
   * 
   * @param result
   * @return
   */
  public  int getResultSetSize(ResultSet result) {
  	System.out.println("~~~~~~ getResultSetSize(ResultSet result) won't work on ms sql ~~~~~~~~~ ");
    int size = -1;
    try {
      result.last();
      size = result.getRow();
      result.beforeFirst();
    } catch (SQLException e) {
      System.err.println("Error: getResultSetSize()");
      setTrackError(e.toString());
      e.printStackTrace();
    } 
    return size;
  }
  

  /**
   * query a boolean
   * 
   * @param dd
   * @param sql
   * @return
   */
  public  boolean queryBoolean(DatabaseData dd, String sql) {
  	setTrackSql(sql);
    boolean b = false;
    try {
      Connection conn = dd.getConnection();
      Statement select = conn.createStatement();
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
      log.warn("" + sql, e);
      System.err.println("Error: queryBoolean(): " + sql);
      setTrackError(e.toString());
      e.printStackTrace();
    } 
    return b;
  }

  /**
   * query a Integer (datasets are so large now, need to focus on larger numbers)
   * 
   *  TODO - use queryLong
   * 
   * @param location
   * @param sql
   * @return
   */
  public  int queryInteger(DatabaseData dd, String sql) {
  	setTrackSql(sql);
    int i = 0;
    try {
      Connection conn = dd.getConnection();
      Statement select = conn.createStatement();
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
      log.warn("" + sql, e);
      System.err.println("Error: queryInteger(): " + sql);
      setTrackError(e.toString());
      e.printStackTrace();
    } 
    return i;
  }
  
  public long queryLong(DatabaseData dd, String sql) {
  	setTrackSql(sql);
    long i = 0;
    try {
      Connection conn = dd.getConnection();
      Statement select = conn.createStatement();
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
      log.warn("" + sql, e);
      System.err.println("Error: queryInteger(): " + sql);
      setTrackError(e.toString());
      e.printStackTrace();
    } 
    return i;
  }

  /**
   * query a String
   * 
   * @param dd
   * @param sql
   * @return
   */
  public String queryString(DatabaseData dd, String sql) {
  	setTrackSql(sql);
    String s = null;
    try {
      Connection conn = dd.getConnection();
      Statement select = conn.createStatement();
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
      log.warn("" + sql, e);
      System.err.println("Error: queryString(): " + sql);
      setTrackError(e.toString());
      e.printStackTrace();
    } 
    return s;
  }

  /**
   * query a double
   * 
   * @param dd
   * @param sql
   * @return
   */
  public double queryDouble(DatabaseData dd, String sql) {
  	setTrackSql(sql);
    double d = 0.0;
    try {
      Connection conn = dd.getConnection();
      Statement select = conn.createStatement();
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
      log.warn("" + sql, e);
      System.err.println("Error: queryDouble(): " + sql);
      setTrackError(e.toString());
      e.printStackTrace();
    } 
    return d;
  }

  public BigDecimal queryBigDecimal(DatabaseData dd, String sql) {
  	setTrackSql(sql);
    BigDecimal bd = null;
    try {
      Connection conn = dd.getConnection();
      Statement select = conn.createStatement();
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
      log.warn("" + sql, e);
      System.err.println("Error: queryBigDecimal(): " + sql);
      setTrackError(e.toString());
      e.printStackTrace();
    } 
    return bd;
  }
  
  /**
   * 
   * @param dd
   * @param sql
   * @param delimiter
   * @return
   */
  public String queryIntegersToCsv(DatabaseData dd, String sql, char delimiter) {
  	setTrackSql(sql);
    String csv = null;
    try {
      Connection conn = dd.getConnection();
      Statement select = conn.createStatement();
      ResultSet result = select.executeQuery(sql);
      int size = getResultSetSize(result);
      int i = 0;
      if (size > 0) {
        csv = "";
      }
      while (result.next()) {
        int id = result.getInt(1);
        String sid = Integer.toString(id);
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
      log.warn("" + sql, e);
      System.err.println("Error: queryIntegersToCsv(): " + sql);
      setTrackError(e.toString());
      e.printStackTrace();
    } 
    if (csv == null | csv.length() == 0) {
      csv = "NULL";
    }
    return csv;
  }
  
  public String queryStringToCsv(DatabaseData dd, String sql, char delimiter) {
  	setTrackSql(sql);
    String csv = null;
    try {
      Connection conn = dd.getConnection();
      Statement select = conn.createStatement();
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
      log.warn("" + sql, e);
      System.err.println("Error: queryStringToCsv(): " + sql);
      setTrackError(e.toString());
      e.printStackTrace();
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
    sql = fixSyntax(sql);
    long id = 0;
    try {
      Connection conn = dd.getConnection();
      if (dd.getLoadBalance() == true) {
        conn.setReadOnly(false);
      }
      Statement update = conn.createStatement();
      
      if (sql.substring(0, 9).toLowerCase().matches("^([\040]+)?drop.*") == true ||
      		sql.substring(0, 9).toLowerCase().matches("^([\040]+)?alter.*") == true ||
      		sql.substring(0, 9).toLowerCase().matches("^([\040]+)?create.*") == true || 
      		sql.substring(0, 9).toLowerCase().matches("^([\040]+)?update.*") == true|| 
      		sql.substring(0, 9).toLowerCase().matches("^([\040]+)?delete.*") == true || 
      		sql.substring(0, 9).toLowerCase().matches("^([\040]+)?truncate.*") == true) { // ms sql is dumb, b/c it can't figure out how to get ID when updating
      	
      	update.execute(sql);
      	
      } else {
      	
      	update.executeUpdate(sql, Statement.RETURN_GENERATED_KEYS);
      	
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
      log.warn("" + sql, e);
      System.err.println("Error: update(): " + sql);
      setTrackError(e.toString());
      e.printStackTrace();
    } 
    return id;
  }
  
  public long update(DatabaseData dd, String sql, boolean getKey) {
  	setTrackSql(sql);
    if (sql == null) {
      return 0;
    }
    sql = fixSyntax(sql);
    long id = 0;
    try {
      Connection conn = dd.getConnection();
      if (dd.getLoadBalance() == true) {
        conn.setReadOnly(false);
      }
      Statement update = conn.createStatement();
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
      log.warn("" + sql, e);
      System.err.println("Error: update(): " + sql);
      setTrackError(e.toString());
      e.printStackTrace();
    } 
    return id;
  }
  
  public boolean queryStringAndConvertToBoolean(DatabaseData dd, String sql) {
  	setTrackSql(sql);
    String value = null;
    try {
      Connection conn = dd.getConnection();
      Statement select = conn.createStatement();
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
      log.warn("" + sql, e);
      System.err.println("Mysql Statement Error:" + sql);
      setTrackError(e.toString());
      e.printStackTrace();
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
    try {
      Connection conn = dd.getConnection();
      Statement select = conn.createStatement();
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
      log.warn("" + sql, e);
      System.err.println("Mysql Statement Error:" + sql);
      setTrackError(e.toString());
      e.printStackTrace();
    } 
    boolean b = false;
    if (l > 0) {
      b = true;
    }
    return b;
  }

  public String getType() {
    return "MsSql";
  }

  /**
   * fix sql syntax if needed
   * 
   * @param sql
   * @return
   */
  public String fixSyntax(String sql) {
  	
  	if ((sql.replaceAll("\r", "").replaceAll("\n", "").toLowerCase().trim().matches("^insert.*set.*") == true || 
  			sql.replaceAll("\r", "").replaceAll("\n", "").toLowerCase().trim().matches("^update.*set.*") == true)) {
  		sql = fixSyntax_InsertUpdate(sql);
  	}
  	
  	if (sql.toLowerCase().contains("now")) {
  		sql = fixNow(sql);
  	}
  	
  	return sql;
  }
  
  private String fixNow(String sql) {
  	sql = sql.replaceAll("(?i)NOW\\(\\)", "GETDATE()");
	  return sql;
  }


	/**
   * change from column=value to (columns,...) values (values,...)
   * 
   * @param sql
   * @return
   */
  private String fixSyntax_InsertUpdate(String sql) {
  	
  	// TODO test this later
  	if (sql.toLowerCase().matches("^update.*")) {
  		return sql;
  	}
  	
  	// get beginning
  	String beginning = StringUtil.getValue("(?i)^(.*?set)", sql);
  	sql = sql.replace(beginning, "");
  	sql = sql.replace(";", "");
  	beginning = StringUtil.getValue("(?i)^(.*?)set", beginning);
  	
  	// get end
  	String end = StringUtil.getValue("((i?)WHERE.*?)$", sql);
  	if (end == null) {
  		end = "";
  	} else {
  		sql = sql.replace(end, "");
  		if (end.contains(";")) {
  			end = StringUtil.getValue(";", end);
  		}
  	}
  	
  	String middle = fixSyntax_getMiddle(sql);
  	
  	//if (beginning.toLowerCase().contains("set") == false) {
  		//beginning = beginning + " SET ";
  	//}
  	
  	sql = beginning + middle + end;
  	
  	return sql;
  }
  
  private String fixSyntax_getMiddle(String sql) {
  	
  	String[] split = null;
  	if (sql.contains(",")) {
  	  //String regex = "(.*?=.*?'.+?'([\040]+)?)(,|$)";
      //split = StringUtil.getValues(regex, sql);
  	  split = StringUtil.readSqlSplit(sql);
  	} else {
  		split = new String[1];
  		split[0] = sql;
  	}
  	
  	if (split == null) {
  	  log.error("MsSqlQueryLib.fixSyntax_getMiddle(): Error, couldn't decipher SQL");
  	}
  	
  	String[] c = new String[split.length];
  	String[] v = new String[split.length];
  	for (int i=0; i < split.length; i++) {
  		String[] cv = split[i].trim().split("=");
  		if (cv == null) {
  		  log.warn("fixSyntax_getMiddle() Error On splitting =, indexOf: " + i + " sql:" + sql);
  		}
  		try {
        c[i] = cv[0];
      } catch (Exception e1) {
        log.warn("fixSyntax_getMiddle() column[i] error on split of =, indexOf: " + i + " sql:" + sql);
      }
  		try {
        v[i] = cv[1];
      } catch (Exception e) {
        v[i] = null;
        log.warn("fixSyntax_getMiddle() value[i] error on split of =, indexOf: " + i + " sql:" + sql);
      }
  	}
  	
  	String columns = "";
  	String values = "";
  	for (int i=0; i < split.length; i++) {
  		columns += c[i];
  		values += v[i];
  		if (i < split.length - 1) {
  			columns += ",";
  			values += ",";
  		}
  	}
  	
  	String middle = "(" + columns + ") VALUES (" + values + ") ";
  	
  	return middle;
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
      log.warn("" + sql, e);
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
