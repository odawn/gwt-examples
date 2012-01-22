package org.gonevertical.dts.lib.sql.querymulti;

import org.gonevertical.dts.data.DatabaseData;
import org.gonevertical.dts.lib.sql.querylib.QueryLib;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class QueryLibFactory {

  public static QueryLib getLib(int databaseType) {

    Injector injector = Guice.createInjector(new QueryModule());
    QueryMulti queryLib = injector.getInstance(QueryMulti.class);
    
    QueryLib ql = null;
    if (databaseType == DatabaseData.TYPE_MYSQL) {
      ql = queryLib.getQueryLib_MySql();
    } else if (databaseType == DatabaseData.TYPE_MSSQL) {
      ql = queryLib.getQueryLib_MsSql();
    } else if (databaseType == DatabaseData.TYPE_ORACLE) {
    	ql = queryLib.getQueryLib_Oracle();
    }

    return ql;
  }
  
}
