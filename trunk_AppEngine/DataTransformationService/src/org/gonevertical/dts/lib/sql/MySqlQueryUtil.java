package org.gonevertical.dts.lib.sql;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.util.Calendar;
import java.util.Date;

import org.gonevertical.dts.data.DatabaseData;
import org.gonevertical.dts.lib.sql.querylib.MySqlQueryLib;


public class MySqlQueryUtil {
 
  public MySqlQueryUtil() {
  }
  
  public static String escape(String s) {
    return new MySqlQueryLib().escape(s);
  }
  
  public static String escape(int i) {
    return new MySqlQueryLib().escape(i);
  }
  
  public static int getResultSetSize(ResultSet result) {
    return new MySqlQueryLib().getResultSetSize(result);
  }
  
  public static boolean queryBoolean(DatabaseData dd, String sql) {
    return new MySqlQueryLib().queryBoolean(dd, sql);
  }

  public static int queryInteger(DatabaseData dd, String sql) {
    return new MySqlQueryLib().queryInteger(dd, sql);
  }
  
  public static long queryLong(DatabaseData dd, String sql) {
    return new MySqlQueryLib().queryLong(dd, sql);
  }

  public static String queryString(DatabaseData dd, String sql) {
    return new MySqlQueryLib().queryString(dd, sql);
  }

  public static double queryDouble(DatabaseData dd, String sql) {
    return new MySqlQueryLib().queryDouble(dd, sql);
  }

  public static BigDecimal queryBigDecimal(DatabaseData dd, String sql) {
    return new MySqlQueryLib().queryBigDecimal(dd, sql);
  }
  
  public static String queryIntegersToCsv(DatabaseData dd, String sql, char delimiter) {
    return new MySqlQueryLib().queryIntegersToCsv(dd, sql, delimiter);
  }
  
  public static String queryStringToCsv(DatabaseData dd, String sql, char delimiter) {
    return new MySqlQueryLib().queryStringToCsv(dd, sql, delimiter);
  }

  public static long update(DatabaseData dd, String sql) {
    return new MySqlQueryLib().update(dd, sql);
  }
  
  public static long update(DatabaseData dd, String sql, boolean getKey) {
    return new MySqlQueryLib().update(dd, sql, getKey);
  }
  
  public static boolean queryStringAndConvertToBoolean(DatabaseData dd, String sql) {
    return new MySqlQueryLib().queryStringAndConvertToBoolean(dd, sql);
  }
  
  public static boolean queryLongAndConvertToBoolean(DatabaseData dd, String sql) {
    return new MySqlQueryLib().queryLongAndConvertToBoolean(dd, sql);
  }

	public static Date queryDate(DatabaseData dd, String sql, Calendar cal) {
	  return new MySqlQueryLib().queryDate(dd, sql, cal);
  }
  
  
}
