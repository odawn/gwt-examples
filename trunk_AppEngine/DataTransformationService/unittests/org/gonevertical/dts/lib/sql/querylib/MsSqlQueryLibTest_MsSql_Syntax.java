package org.gonevertical.dts.lib.sql.querylib;

import org.gonevertical.dts.data.DatabaseData;
import org.gonevertical.dts.lib.sql.querymulti.QueryLibFactory;
import org.gonevertical.dts.lib.sql.transformlib.TransformLib;
import org.gonevertical.dts.lib.sql.transformmulti.TransformLibFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MsSqlQueryLibTest_MsSql_Syntax {

  private DatabaseData dd;
  private QueryLib ql;
  private TransformLib tl;
	private MsSqlQueryLib ms;

  @Before
  public void setUp() throws Exception {
    dd = new DatabaseData(DatabaseData.TYPE_MSSQL, "192.168.10.13\\SQLEXRESS", "1433", "test", "test#", "data");
    
    ql = QueryLibFactory.getLib(DatabaseData.TYPE_MYSQL);
    tl = TransformLibFactory.getLib(DatabaseData.TYPE_MYSQL);
    
    ms = new MsSqlQueryLib();
  }

  @After
  public void tearDown() throws Exception {
    dd = null;
    ql = null;
    tl = null;
    ms = null;
  }

	@Test
	public void testFixSyntax() {
		
		String sql = "UPDATE test_table SET [field1]='value1', [field2]='value2',[field3]='value3', field4='value4';";
		
		String newSql = ms.fixSyntax(sql);
		
		System.out.println(sql);
		System.out.println(newSql);
	}

}
