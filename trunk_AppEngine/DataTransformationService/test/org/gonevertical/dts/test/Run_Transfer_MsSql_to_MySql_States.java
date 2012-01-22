package org.gonevertical.dts.test;

import org.gonevertical.dts.data.DatabaseData;
import org.gonevertical.dts.process.Transfer;

public class Run_Transfer_MsSql_to_MySql_States {

	public static void main(String[] args) {

		DatabaseData dd_src = new DatabaseData(DatabaseData.TYPE_MSSQL, "xp3", "1433", "sa", "test", "test", "dbo");
		DatabaseData dd_dst = new DatabaseData(DatabaseData.TYPE_MYSQL, "ark_home", "3306", "test", "test#", "test");

		String fromTable = "import_states_test";
		String toTable = "transfer_states_test_from_mssql";

		Transfer transfer = new Transfer(dd_src, dd_dst);
		transfer.setOffsetLimit(10);
		transfer.transferAllFields(fromTable, toTable);

	}

}
