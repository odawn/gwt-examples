package org.gonevertical.dts.test;

import org.gonevertical.dts.data.DatabaseData;
import org.gonevertical.dts.lib.errorcheck.CompareTables;

public class Run_CompareTableCounts {

	public static void main(String[] args) {
		
		 DatabaseData dd_src = new DatabaseData(DatabaseData.TYPE_MYSQL, "ark", "3306", "test", "test#", "test");
	   DatabaseData dd_dst = new DatabaseData(DatabaseData.TYPE_MYSQL, "ark", "3306", "test", "test#", "test");
		
	   String[] tables = {"","","",""};
	   
	   CompareTables ct = new CompareTables(dd_src, dd_dst);
	   ct.checkTablesCounts(tables);
	   
	}
	
}
