package org.gonevertical.dts.lib.sql;

import static org.junit.Assert.assertEquals;

import org.gonevertical.dts.data.ColumnData;
import org.gonevertical.dts.data.DatabaseData;
import org.gonevertical.dts.lib.sql.querylib.MySqlQueryLib;
import org.gonevertical.dts.lib.sql.transformlib.MySqlTransformLib;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MsSqlQueryUtilTest_SqlEscape {

  private DatabaseData dd;

  @Before
  public void setUp() throws Exception {
    dd = new DatabaseData(DatabaseData.TYPE_MYSQL, "ark_home", "3306", "test", "test#", "test");
    
    new MySqlTransformLib().dropTable(dd, "test_escape");
    ColumnData columnData = new ColumnData("test_escape", "Value", "TEXT DEFAULT NULL");
    new MySqlTransformLib().createTable(dd, "test_escape", "TestId");
    new MySqlTransformLib().createColumn(dd, columnData);
    
  }

  @After
  public void tearDown() throws Exception {
    dd = null;
  }

  @Test
  public void testEscapeString1() {
    String value = "mystring\\";
    String s = new MySqlQueryLib().escape(value);
    String sql = "INSERT INTO test_escape SET Value='" + s + "'";
    long id = new MySqlQueryLib().update(dd, sql);
    sql = "SELECT Value FROM test_escape WHERE TestId='" + id + "';";
    String valueTest = new MySqlQueryLib().queryString(dd, sql);
    assertEquals(value, valueTest);
  }

  @Test
  public void testEscapeString2() {
    String value = "mystring\\\\ \\ "; 
    String s = new MySqlQueryLib().escape(value);
    String sql = "INSERT INTO test_escape SET Value='" + s + "'";
    long id = new MySqlQueryLib().update(dd, sql);
    sql = "SELECT Value FROM test_escape WHERE TestId='" + id + "';";
    String valueTest = new MySqlQueryLib().queryString(dd, sql);
    assertEquals(value, valueTest);
  }
  
  @Test
  public void testEscapeString3() {
    String value = "mystring\\\\ \\"; 
    String s = new MySqlQueryLib().escape(value);
    String sql = "INSERT INTO test_escape SET Value='" + s + "'";
    long id = new MySqlQueryLib().update(dd, sql);
    sql = "SELECT Value FROM test_escape WHERE TestId='" + id + "';";
    String valueTest = new MySqlQueryLib().queryString(dd, sql);
    assertEquals(value, valueTest);
  }
  
  @Test
  public void testEscapeString4() {
    String value = "value\\\\";
    String s = new MySqlQueryLib().escape(value);
    String sql = "INSERT INTO test_escape SET Value='" + s + "'";
    long id = new MySqlQueryLib().update(dd, sql);
    sql = "SELECT Value FROM test_escape WHERE TestId='" + id + "';";
    String valueTest = new MySqlQueryLib().queryString(dd, sql);
    assertEquals(value, valueTest);
  }
  
  @Test
  public void testEscapeString5() {
    String value = "MR O\\\'NEAL";
    String s = new MySqlQueryLib().escape(value);
    String sql = "INSERT INTO test_escape SET Value='" + s + "'";
    long id = new MySqlQueryLib().update(dd, sql);
    sql = "SELECT Value FROM test_escape WHERE TestId='" + id + "';";
    String valueTest = new MySqlQueryLib().queryString(dd, sql);
    assertEquals(value, valueTest);
  }
  
  @Test
  public void testEscapeString6() {
    String value = "MY\'S INTERNATIONAL"; 
    String s = new MySqlQueryLib().escape(value);
    String sql = "INSERT INTO test_escape SET Value='" + s + "'";
    long id = new MySqlQueryLib().update(dd, sql);
    sql = "SELECT Value FROM test_escape WHERE TestId='" + id + "';";
    String valueTest = new MySqlQueryLib().queryString(dd, sql);
    assertEquals(value, valueTest);
  }
  
  @Test
  public void testEscapeString7() {
    String value = "MR O'NEILL JR";
    String s = new MySqlQueryLib().escape(value);
    String sql = "INSERT INTO test_escape SET Value='" + s + "'";
    long id = new MySqlQueryLib().update(dd, sql);
    sql = "SELECT Value FROM test_escape WHERE TestId='" + id + "';";
    String valueTest = new MySqlQueryLib().queryString(dd, sql);
    assertEquals(value, valueTest);
  }
  
  @Test
  public void testEscapeString8() {
    String value = "single ' '' ''' '''' '''' '";
    String s = new MySqlQueryLib().escape(value);
    String sql = "INSERT INTO test_escape SET Value='" + s + "'";
    long id = new MySqlQueryLib().update(dd, sql);
    sql = "SELECT Value FROM test_escape WHERE TestId='" + id + "';";
    String valueTest = new MySqlQueryLib().queryString(dd, sql);
    assertEquals(value, valueTest);
  }
  
  @Test
  public void testEscapeString9() {
    String value = "double \" \" \" \" \" \"";
    String s = new MySqlQueryLib().escape(value);
    String sql = "INSERT INTO test_escape SET Value='" + s + "'";
    long id = new MySqlQueryLib().update(dd, sql);
    sql = "SELECT Value FROM test_escape WHERE TestId='" + id + "';";
    String valueTest = new MySqlQueryLib().queryString(dd, sql);
    assertEquals(value, valueTest);
  }
  
  @Test
  public void testEscapeString10() {
    String value = "lit\neral\\hi there\\";
    String s = new MySqlQueryLib().escape(value);
    String sql = "INSERT INTO test_escape SET Value='" + s + "'";
    long id = new MySqlQueryLib().update(dd, sql);
    sql = "SELECT Value FROM test_escape WHERE TestId='" + id + "';";
    String valueTest = new MySqlQueryLib().queryString(dd, sql);
    assertEquals(value, valueTest);
    System.out.println(valueTest);
  }
}
