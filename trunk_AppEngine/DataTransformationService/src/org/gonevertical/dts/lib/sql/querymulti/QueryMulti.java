package org.gonevertical.dts.lib.sql.querymulti;

import org.gonevertical.dts.lib.sql.querylib.QueryLib;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class QueryMulti {

  private QueryLib queryLibMySql;
  private QueryLib queryLibMsSql;
	private QueryLib queryLibOracle;

  @Inject
  public void setMySqlEngine(@Named("MySql") QueryLib queryLib) {
      this.queryLibMySql = queryLib;
  }

  @Inject
  public void setMsSqlEngine(@Named("MsSql") QueryLib queryLib) {
      this.queryLibMsSql = queryLib;
  }
  
  @Inject
  public void setOracleEngine(@Named("Oracle") QueryLib queryLib) {
      this.queryLibOracle = queryLib;
  }

  public QueryLib getQueryLib_MySql() {
    return queryLibMySql;
  }
  
  public QueryLib getQueryLib_MsSql() {
      return queryLibMsSql;
  }

	public QueryLib getQueryLib_Oracle() {
	  return queryLibOracle;
  }
}
