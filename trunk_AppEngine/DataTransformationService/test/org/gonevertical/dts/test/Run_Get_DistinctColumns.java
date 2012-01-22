package org.gonevertical.dts.test;

import org.gonevertical.dts.data.ColumnData;
import org.gonevertical.dts.lib.sql.columnlib.MySqlColumnLib;

public class Run_Get_DistinctColumns {

  public static void main(String[] args) {
    
    ColumnData c1 = new ColumnData("table", "name", "VARCHAR(255)");
    ColumnData c2 = new ColumnData("table", "name", "VARCHAR(255)");
    ColumnData c3 = new ColumnData("table", "name3", "VARCHAR(255)");
    ColumnData c4 = new ColumnData("table", "col1", "VARCHAR(255)");
    ColumnData c5 = new ColumnData("table", "col2", "VARCHAR(255)");
    ColumnData c6 = new ColumnData("table", "name", "VARCHAR(255)");
    ColumnData c7 = new ColumnData("table", "name3", "VARCHAR(255)");
    
    ColumnData[] c = new ColumnData[7];
    c[0] = c1;
    c[1] = c2;
    c[2] = c3;
    c[3] = c4;
    c[4] = c5;
    c[5] = c6;
    c[6] = c7;
    
    ColumnData[] cb = new MySqlColumnLib().getColumns_Distinct(c);
    
    for (int i=0; i < cb.length; i++) {
      System.out.println("column: " + cb[i].getColumnName());
    }
    
    
    System.out.println("end");
  }
  
}
