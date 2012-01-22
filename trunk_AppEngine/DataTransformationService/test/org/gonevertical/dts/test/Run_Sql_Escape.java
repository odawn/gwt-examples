package org.gonevertical.dts.test;

import org.gonevertical.dts.data.ColumnData;
import org.gonevertical.dts.data.DatabaseData;
import org.gonevertical.dts.lib.sql.querylib.MySqlQueryLib;
import org.gonevertical.dts.lib.sql.transformlib.MySqlTransformLib;


public class Run_Sql_Escape {

  public static void main(String[] args) {
    
    DatabaseData databaseData = new DatabaseData(DatabaseData.TYPE_MYSQL, "ark", "3306", "test", "test#", "test");
    
    ColumnData columnData = new ColumnData("test_escape", "Value", "VARCHAR(100) DEFAULT NULL");
    
    new MySqlTransformLib().createTable(databaseData, "test_escape", "TestId");
    new MySqlTransformLib().createColumn(databaseData, columnData);
    
    String value = "mystring\\\\ \\ "; 
    
    String s = new MySqlQueryLib().escape(value);
    
    String sql = "INSERT INTO test_escape SET Value='" + s + "'";
    
    System.out.println("sql: " + sql);
    
    long id = new MySqlQueryLib().update(databaseData, sql);
    
    sql = "SELECT Value FROM test_escape WHERE TestId='" + id + "';";
    
    String valueTest = new MySqlQueryLib().queryString(databaseData, sql);
    
    
    
    
    
    System.out.println("value: " + value + " valueTest: " + valueTest);
    
    
  }
  
}
