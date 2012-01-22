package org.gonevertical.dts.lib.sql.columnmulti;

import org.gonevertical.dts.lib.sql.columnlib.ColumnLib;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class ColumnMulti {

  private ColumnLib columnLibMySql;
  private ColumnLib columnLibMsSql;
  private ColumnLib columnLibOracle;
  
  @Inject
  public void setMySqlEngine(@Named("MySql") ColumnLib columnLib) {
      this.columnLibMySql = columnLib;
  }

  @Inject
  public void setMsSqlEngine(@Named("MsSql") ColumnLib columnLib) {
      this.columnLibMsSql = columnLib;
  }
  
  @Inject
  public void setOracleEngine(@Named("Oracle") ColumnLib columnLib) {
      this.columnLibOracle = columnLib;
  }

  public ColumnLib getColumnLib_MySql() {
    return columnLibMySql;
  }
  
  public ColumnLib getColumnLib_MsSql() {
      return columnLibMsSql;
  }
  
  public ColumnLib getColumnLib_Oracle() {
  	return columnLibOracle;
  }
  
}
