package org.gonevertical.dts.lib.sql.transformmulti;

import org.gonevertical.dts.lib.sql.transformlib.TransformLib;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public class TransformMulti {

  private TransformLib transformLibMySql;
  private TransformLib transformLibMsSql;
	private TransformLib transformLibOracle;

  @Inject
  public void setMySqlEngine(@Named("MySql") TransformLib transformLib) {
      this.transformLibMySql = transformLib;
  }

  @Inject
  public void setMsSqlEngine(@Named("MsSql") TransformLib transformLib) {
      this.transformLibMsSql = transformLib;
  }
  
  @Inject
  public void setOracleEngine(@Named("Oracle") TransformLib transformLib) {
      this.transformLibOracle = transformLib;
  }

  public TransformLib getTransformLib_MySql() {
    return transformLibMySql;
  }
  
  public TransformLib getTransformLib_MsSql() {
      return transformLibMsSql;
  }

	public TransformLib getTransformLib_Oracle() {
	  return transformLibOracle;
  }
  
}
