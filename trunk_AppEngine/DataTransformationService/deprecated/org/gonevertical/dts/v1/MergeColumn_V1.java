package org.gonevertical.dts.v1;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.gonevertical.dts.data.DatabaseData;


public class MergeColumn_V1 extends SQLProcessing_V1 {
  
  public DatabaseData databaseData = null;
  
  // table to work in
  public String table = null;

  // table id field name
  private String idName = null;

  
  // column to copy data to
  private String srcColumn = null;
  public String desColumn = null;

  
  public MergeColumn_V1(DatabaseData databaseData) {
    this.databaseData = databaseData;
  }
  
  public void set(String table, String idFieldName, String srcColumn, String desColumn) {
    this.table = table;
    this.idName = idFieldName;
    this.srcColumn = srcColumn;
    this.desColumn = desColumn;
  }
  
  public void process() {
    
    String sql = "SELECT " + idName + ", " + srcColumn + " FROM " + table + ";";
    
    try {
      Connection conn = databaseData.getConnection();
      Statement select = conn.createStatement();
      ResultSet result = select.executeQuery(sql);
      int i = 0;
      while (result.next()) {

        int id = result.getInt(1);
        String value = result.getString(2);
        
        process(id, value);
        
        i++;
      }
      result.close();
      select.close();
    } catch (SQLException e) {
      System.err.println("Mysql Statement Error:" + sql);
      e.printStackTrace();
    }
    
  }

  private void process(int id, String value) {
    
    if (value == null | value.length() == 0) {
      return;
    }
    
    String sql = "UPDATE " + table + " SET " + desColumn + "='" + escapeForSql(value) + "' " +
    		" WHERE " + idName + "='"+id+"';";
    
    System.out.println("sql: " + sql);
    
    updateSql_v2(databaseData, sql);
    
  }

}
