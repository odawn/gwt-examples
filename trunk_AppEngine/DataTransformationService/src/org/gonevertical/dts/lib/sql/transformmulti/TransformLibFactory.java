package org.gonevertical.dts.lib.sql.transformmulti;

import org.gonevertical.dts.data.DatabaseData;
import org.gonevertical.dts.lib.sql.transformlib.TransformLib;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class TransformLibFactory {

  public static TransformLib getLib(int databaseType) {
    
    Injector injector = Guice.createInjector(new TransformModule());
    TransformMulti tm = injector.getInstance(TransformMulti.class);
    
    TransformLib tl = null;
    if (databaseType == DatabaseData.TYPE_MYSQL) {
      tl = tm.getTransformLib_MySql();
    } else if (databaseType == DatabaseData.TYPE_MSSQL) {
      tl = tm.getTransformLib_MsSql();
    } else if (databaseType == DatabaseData.TYPE_ORACLE) {
    	tl = tm.getTransformLib_Oracle();
    }
    
    return tl;
  }
  
}
