package org.gonevertical.dts.lib.sql.querylib;

import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.gonevertical.dts.data.ColumnData;
import org.gonevertical.dts.data.DatabaseData;
import org.gonevertical.dts.lib.sql.querymulti.QueryLibFactory;
import org.gonevertical.dts.lib.sql.transformlib.TransformLib;
import org.gonevertical.dts.lib.sql.transformmulti.TransformLibFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MsSqlQueryLibTest {

  private DatabaseData dd;
  private QueryLib ql;
  private TransformLib tl;

  @Before
  public void setUp() throws Exception {
    dd = new DatabaseData(DatabaseData.TYPE_MSSQL, "192.168.10.13", "3306", "test", "test#", "test");
    
    ql = QueryLibFactory.getLib(DatabaseData.TYPE_MYSQL);
    tl = TransformLibFactory.getLib(DatabaseData.TYPE_MYSQL);
  }

  @After
  public void tearDown() throws Exception {
    dd = null;
    ql = null;
    tl = null;
  }

  @Test
  public void testEscapeString() {
    String value = "\' \t lit\neral \\ mystring\\";
    String s = ql.escape(value);
    String sql = "INSERT INTO test_escape SET Value='" + s + "'";
    long id = ql.update(dd, sql);
    sql = "SELECT Value FROM test_escape WHERE TestId='" + id + "';";
    String valueTest = ql.queryString(dd, sql);
    assertEquals(value, valueTest);
  }

  @Test
  public void testEscapeInt() {
    String s = ql.escape(1);
    String sql = "INSERT INTO test_escape SET Value='" + s + "'";
    long id = ql.update(dd, sql);
    sql = "SELECT Value FROM test_escape WHERE TestId='" + id + "';";
    String valueTest = ql.queryString(dd, sql);
    assertEquals(Integer.toString(1), valueTest);
  }

  @Test
  public void testGetResultSetSize() {
    String sql = "SELECT COUNT(*) AS t FROM test_escape;";
    int count = ql.queryInteger(dd, sql);
    sql = "SELECT * FROM test_escape;";
    int resultSize = 0;
    Connection conn = null;
    Statement select = null;
    try {
      conn = dd.getConnection();
      select = conn.createStatement();
      ResultSet result = select.executeQuery(sql);
      resultSize = ql.queryInteger(dd, sql);
      result.close();
      result = null;
      select.close();
      select = null;
      conn.close();
    } catch (SQLException e) {
      System.err.println("Error: " + sql);
      e.printStackTrace();
    } finally {
      conn = null;
      select = null;
    }
    if (count == 0 | resultSize == 0) {
      count = -1;
    }
    assertEquals(count, resultSize);
  }

  @Test
  public void testQueryBoolean() {
    tl.dropTable(dd, "test_boolean");
    ColumnData columnData = new ColumnData("test_boolean", "Value", "BOOLEAN DEFAULT NULL");
    tl.createTable(dd, "test_boolean", "TestId");
    tl.createColumn(dd, columnData);
    String sql = "INSERT INTO test_boolean SET Value=1;";
    ql.update(dd, sql);
    sql = "SELECT Value From test_boolean";
    Boolean actual = ql.queryBoolean(dd, sql);
    assertEquals(true, actual);
  }

  @Test
  public void testQueryInteger() {
    tl.dropTable(dd, "test_integer");
    ColumnData columnData = new ColumnData("test_integer", "Value", "INTEGER DEFAULT 0");
    tl.createTable(dd, "test_integer", "TestId");
    tl.createColumn(dd, columnData);
    String sql = "INSERT INTO test_integer SET Value=123456789;";
    ql.update(dd, sql);
    sql = "SELECT Value From test_integer;";
    int actual = ql.queryInteger(dd, sql);
    assertEquals(123456789, actual);
  }

  @Test
  public void testQueryLong() {
    tl.dropTable(dd, "test_long");
    ColumnData columnData = new ColumnData("test_long", "Value", "BIGINT DEFAULT 0");
    tl.createTable(dd, "test_long", "TestId");
    tl.createColumn(dd, columnData);
    String sql = "INSERT INTO test_long SET Value=1234567890123456789;";
    ql.update(dd, sql);
    sql = "SELECT Value From test_long;";
    long actual = ql.queryLong(dd, sql);
    long expected = 1234567890123456789L;
    assertEquals(expected, actual);
  }

  @Test
  public void testQueryString() {
    tl.dropTable(dd, "test_string");
    ColumnData columnData = new ColumnData("test_string", "Value", "BIGINT DEFAULT 0");
    tl.createTable(dd, "test_string", "TestId");
    tl.createColumn(dd, columnData);
    String sql = "INSERT INTO test_string SET Value=1234567890123456789;";
    ql.update(dd, sql);
    sql = "SELECT Value From test_string;";
    long actual = ql.queryLong(dd, sql);
    long expected = 1234567890123456789L;
    assertEquals(expected, actual);
  }

  @Test
  public void testQueryDouble() {
    tl.dropTable(dd, "test_double");
    ColumnData columnData = new ColumnData("test_double", "Value", "DOUBLE DEFAULT 0.0");
    tl.createTable(dd, "test_double", "TestId");
    tl.createColumn(dd, columnData);
    String sql = "INSERT INTO test_double SET Value=54321.12345;";
    ql.update(dd, sql);
    sql = "SELECT Value From test_double";
    double actual = ql.queryDouble(dd, sql);
    double expected = 54321.12345;
    assertEquals(expected,actual, 0.00001);
  }

  @Test
  public void testQueryBigDecimal() {
    tl.dropTable(dd, "test_decimal");
    ColumnData columnData = new ColumnData("test_decimal", "Value", "DECIMAL(10,5) DEFAULT 0");
    tl.createTable(dd, "test_decimal", "TestId");
    tl.createColumn(dd, columnData);
    String sql = "INSERT INTO test_decimal SET Value=54321.12345;";
    ql.update(dd, sql);
    sql = "SELECT Value From test_decimal";
    BigDecimal actual = ql.queryBigDecimal(dd, sql);
    BigDecimal expected = new BigDecimal("54321.12345");
    assertEquals(expected, actual);
  }

  @Test
  public void testQueryIntegersToCsv() {
    tl.dropTable(dd, "test_inttocsv");
    ColumnData columnData = new ColumnData("test_inttocsv", "Value", "INTEGER DEFAULT 0");
    tl.createTable(dd, "test_inttocsv", "TestId");
    tl.createColumn(dd, columnData);
    String sql0 = "INSERT INTO test_inttocsv SET Value=1;";
    String sql1 = "INSERT INTO test_inttocsv SET Value=2;";
    String sql2 = "INSERT INTO test_inttocsv SET Value=3;";
    String sql3 = "INSERT INTO test_inttocsv SET Value=4;";
    String sql4 = "INSERT INTO test_inttocsv SET Value=5;";
    String sql5 = "INSERT INTO test_inttocsv SET Value=6;";
    ql.update(dd, sql0);
    ql.update(dd, sql1);
    ql.update(dd, sql2);
    ql.update(dd, sql3);
    ql.update(dd, sql4);
    ql.update(dd, sql5);
    String sqlq = "SELECT VALUE FROM test_inttocsv;";
    String actual = ql.queryIntegersToCsv(dd, sqlq, ",".charAt(0));
    String expected = "1,2,3,4,5,6";
    assertEquals(expected, actual);
  }

  @Test
  public void testQueryStringToCsv() {
    tl.dropTable(dd, "test_stringtocsv");
    ColumnData columnData = new ColumnData("test_stringtocsv", "Value", "VARCHAR(1) DEFAULT NULL");
    tl.createTable(dd, "test_stringtocsv", "TestId");
    tl.createColumn(dd, columnData);
    String sql0 = "INSERT INTO test_stringtocsv SET Value='a';";
    String sql1 = "INSERT INTO test_stringtocsv SET Value='b';";
    String sql2 = "INSERT INTO test_stringtocsv SET Value='c';";
    String sql3 = "INSERT INTO test_stringtocsv SET Value='d';";
    String sql4 = "INSERT INTO test_stringtocsv SET Value='e';";
    String sql5 = "INSERT INTO test_stringtocsv SET Value='f';";
    ql.update(dd, sql0);
    ql.update(dd, sql1);
    ql.update(dd, sql2);
    ql.update(dd, sql3);
    ql.update(dd, sql4);
    ql.update(dd, sql5);
    String sqlq = "SELECT VALUE FROM test_stringtocsv;";
    String actual = ql.queryStringToCsv(dd, sqlq, ",".charAt(0));
    String expected = "\"a\",\"b\",\"c\",\"d\",\"e\",\"f\"";
    assertEquals(expected, actual);
  }

  @Test
  public void testUpdate() {
    tl.dropTable(dd, "test_update");
    ColumnData columnData = new ColumnData("test_update", "Value", "VARCHAR(100) DEFAULT NULL");
    tl.createTable(dd, "test_update", "TestId");
    tl.createColumn(dd, columnData);
    String sql = "INSERT INTO test_update SET Value='inserting';";
    ql.update(dd, sql);
    sql = "SELECT Value From test_update;";
    String actual = ql.queryString(dd, sql);
    String expected = "inserting";
    assertEquals(expected, actual);
  }

  @Test
  public void testUpdateWithKey() {
    tl.dropTable(dd, "test_updatekey");
    ColumnData columnData = new ColumnData("test_updatekey", "Value", "VARCHAR(100) DEFAULT NULL");
    tl.createTable(dd, "test_updatekey", "TestId");
    tl.createColumn(dd, columnData);
    String sql = "INSERT INTO test_updatekey SET Value='inserting';";
    long id = ql.update(dd, sql);
    long expected = 1;
    assertEquals(expected, id);
  }

  @Test
  public void testQueryStringAndConvertToBoolean() {
    tl.dropTable(dd, "test_convertboolean");
    ColumnData columnData = new ColumnData("test_convertboolean", "Value", "VARCHAR(100) DEFAULT NULL");
    tl.createTable(dd, "test_convertboolean", "TestId");
    tl.createColumn(dd, columnData);
    String sql = "INSERT INTO test_convertboolean SET Value=1;";
    ql.update(dd, sql);
    sql = "SELECT Value FROM test_convertboolean;";
    boolean actual = ql.queryStringAndConvertToBoolean(dd, sql);
    boolean expected = true;
    assertEquals(expected, actual);
  }
  
  public void testQueryStringAndConvertToBoolean2() {
    tl.dropTable(dd, "test_convertboolean");
    ColumnData columnData = new ColumnData("test_convertboolean", "Value", "VARCHAR(100) DEFAULT NULL");
    tl.createTable(dd, "test_convertboolean", "TestId");
    tl.createColumn(dd, columnData);
    String sql = "INSERT INTO test_convertboolean SET Value=0;";
    ql.update(dd, sql);
    sql = "SELECT Value FROM test_convertboolean;";
    boolean actual = ql.queryStringAndConvertToBoolean(dd, sql);
    boolean expected = false;
    assertEquals(expected, actual);
  }

  @Test
  public void testQueryLongAndConvertToBoolean() {
    tl.dropTable(dd, "test_convertboolean");
    ColumnData columnData = new ColumnData("test_convertboolean", "Value", "BIGINT DEFAULT 0");
    tl.createTable(dd, "test_convertboolean", "TestId");
    tl.createColumn(dd, columnData);
    String sql = "INSERT INTO test_convertboolean SET Value=1;";
    ql.update(dd, sql);
    sql = "SELECT Value From test_convertboolean;";
    boolean actual = ql.queryLongAndConvertToBoolean(dd, sql);
    boolean expected = true;
    assertEquals(expected, actual);
    sql = "UPDATE test_convertboolean SET Value=0;";
    ql.update(dd, sql);
    sql = "SELECT Value FROM test_convertboolean;";
    actual = ql.queryLongAndConvertToBoolean(dd, sql);
    expected = false;
    assertEquals(expected, actual);
  }

  @Test
  public void testGetType() {
    // no need to run this one at least not yet
  }

}
