package org.gonevertical.dts.test;

import java.io.File;
import java.net.URISyntaxException;

import org.gonevertical.dts.data.ColumnData;
import org.gonevertical.dts.data.DatabaseData;
import org.gonevertical.dts.process.Export;


public class Run_Export_ZipCodes {
  
  public static void main(String[] args) {
    File executionlocation = null;
    try {
      executionlocation = new File(Run_Test_Import_v1.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    String execPath = executionlocation.getParent();
    
    String dir = execPath + "/data/export";  
  
    
    // source 
    DatabaseData database_src = new DatabaseData(DatabaseData.TYPE_MYSQL, "ark", "3306", "test", "test#", "system");
    String table = "zipcodes";
    String whereSql = null;
    String limitSql = null; // limitSql="LIMIT 0,101";
    
    // destination
    File desDir = new File(dir);
    
    ColumnData[] pruneColumnData = new ColumnData[2];
    pruneColumnData[0] = new ColumnData();
    pruneColumnData[1] = new ColumnData();
    pruneColumnData[0].setColumnName("DateCreated");
    pruneColumnData[1].setColumnName("DateUpdated");
    
    Export export = new Export(database_src, desDir);
    
    export.setPruneColumns(pruneColumnData);
    //export.setShowCreateTable(false);
    //export.setSkipPrimaryKey(true); // export the primaryKey
  
    export.setTable(table, whereSql, limitSql);
    export.run(Export.EXPORTAS_CSV);
    
    export.setShowCreateTable(true);
    export.run(Export.EXPORTAS_SQL);
  }
}
