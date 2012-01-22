package org.gonevertical.dts.run.example;

import java.io.File;

import org.gonevertical.dts.data.DatabaseData;
import org.gonevertical.dts.process.Export;


public class Run_Export_Test {

  public static void main(String[] args) {
    
    DatabaseData database_src = new DatabaseData(DatabaseData.TYPE_MYSQL, "192.168.10.79", "3306", "test", "test*7", "system");
    
    File desDir = new File("/home/branflake2267/test");
    
    String table = "states";
    String whereSql = null;
    String limitSql = "LIMIT 0,10";
    
    Export export = new Export(database_src, desDir);
    
    export.setTable(table, whereSql, limitSql);
    export.run(Export.EXPORTAS_CSV);
    
    export.setShowCreateTable(true);
    export.run(Export.EXPORTAS_SQL);
  }
  
}
