package org.gonevertical.dts.lib.sql;

import java.math.BigDecimal;
import java.sql.ResultSet;

import org.gonevertical.dts.data.DatabaseData;
import org.gonevertical.dts.lib.sql.querylib.MsSqlQueryLib;


/**
 * under development
 * 
 * @author branflake2267
 *
 */
public class MsSqlQueryUtil {

  public static String escape(String s) {
   return new MsSqlQueryLib().escape(s);
  }
  
  public static String escape(int i) {
   return new MsSqlQueryLib().escape(i);
  }
  
  public static int getResultSetSize(ResultSet result) {
    return new MsSqlQueryLib().getResultSetSize(result);
  }

  public static boolean queryBoolean(DatabaseData dd, String sql) {
    return new MsSqlQueryLib().queryBoolean(dd, sql);
  }

  public static int queryInteger(DatabaseData dd, String sql) {
    return new MsSqlQueryLib().queryInteger(dd, sql);
  }
  
  public static long queryLong(DatabaseData dd, String sql) {
    return new MsSqlQueryLib().queryLong(dd, sql);
  }

  public static String queryString(DatabaseData dd, String sql) {
    return new MsSqlQueryLib().queryString(dd, sql);
  }

  public static double queryDouble(DatabaseData dd, String sql) {
    return new MsSqlQueryLib().queryDouble(dd, sql);
  }

  public static BigDecimal queryBigDecimal(DatabaseData dd, String sql) {
    return new MsSqlQueryLib().queryBigDecimal(dd, sql);
  }
  
  public static String queryIntegersToCsv(DatabaseData dd, String sql, char delimiter) {
    return new MsSqlQueryLib().queryIntegersToCsv(dd, sql, delimiter);
  }
  
  public String queryStringToCsv(DatabaseData dd, String sql, char delimiter) {
    return new MsSqlQueryLib().queryStringToCsv(dd, sql, delimiter);
  }

  public static long update(DatabaseData dd, String sql) {
    return new MsSqlQueryLib().update(dd, sql);
  }
  
  public static long update(DatabaseData dd, String sql, boolean getKey) {
    return new MsSqlQueryLib().update(dd, sql, getKey);
  }
  
  public static boolean queryStringAndConvertToBoolean(DatabaseData dd, String sql) {
    return new MsSqlQueryLib().queryStringAndConvertToBoolean(dd, sql);
  }
  
  public static boolean queryLongAndConvertToBoolean(DatabaseData dd, String sql) {
    return new MsSqlQueryLib().queryLongAndConvertToBoolean(dd, sql);
  }

}
