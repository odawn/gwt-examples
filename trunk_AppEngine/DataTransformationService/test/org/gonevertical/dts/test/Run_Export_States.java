package org.gonevertical.dts.test;

import java.io.File;
import java.net.URISyntaxException;

import org.gonevertical.dts.data.DatabaseData;
import org.gonevertical.dts.process.Export;


public class Run_Export_States {

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
    String table = "states";
    String whereSql = null;
    String limitSql = null;
    
    // destination
    File desDir = new File(dir);
    
    // prune these columns
    //ColumnData[] pruneColumnData = new ColumnData[1];
    //pruneColumnData[0] = new ColumnData();
    
    Export export = new Export(database_src, desDir);
    
    //export.setPruneColumns(pruneColumnData);
    //export.setShowCreateTable(false);
    //export.setSkipPrimaryKey(true); // export the primaryKey

    export.setTable(table, whereSql, limitSql);
    export.run(Export.EXPORTAS_CSV);
    
    export.setShowCreateTable(true);
    export.run(Export.EXPORTAS_SQL);
  }
  
}
