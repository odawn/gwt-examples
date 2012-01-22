package org.gonevertical.dts.lib.sql.columnmulti;

import org.gonevertical.dts.data.DatabaseData;
import org.gonevertical.dts.lib.sql.columnlib.ColumnLib;
import org.gonevertical.dts.lib.sql.columnlib.OracleColumnLib;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class ColumnLibFactory {
  
  private static final Logger log = LoggerFactory.getLogger(ColumnLibFactory.class);
	
  public static ColumnLib getLib(int databaseType) {
    
    Injector ci = Guice.createInjector(new ColumnModule());
    ColumnMulti cm = ci.getInstance(ColumnMulti.class);
    
    ColumnLib cl = null;
    if (databaseType == DatabaseData.TYPE_MYSQL) {
      cl = cm.getColumnLib_MySql();
    } else if (databaseType == DatabaseData.TYPE_MSSQL) {
      cl = cm.getColumnLib_MsSql();
    } else if (databaseType == DatabaseData.TYPE_ORACLE) {
    	cl = cm.getColumnLib_Oracle();
    }
    
    return cl;
  }
  
}
