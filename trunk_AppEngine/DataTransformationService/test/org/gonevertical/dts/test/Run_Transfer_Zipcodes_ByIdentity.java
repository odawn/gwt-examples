package org.gonevertical.dts.test;

import java.io.File;
import java.net.URISyntaxException;

import javax.naming.Context;

import org.gonevertical.dts.data.ColumnData;
import org.gonevertical.dts.data.DatabaseData;
import org.gonevertical.dts.lib.errorcheck.CompareTables;
import org.gonevertical.dts.lib.pooling.ManualSetupOfInitialPoolingContext;
import org.gonevertical.dts.lib.sql.MySqlQueryUtil;
import org.gonevertical.dts.process.Transfer;

/**
 * test transfer by transfering zipcodes from one table to another
 * 
 * @author branflake2267
 *
 */
public class Run_Transfer_Zipcodes_ByIdentity {

	public static void main(String[] args) {
		new Run_Transfer_Zipcodes_ByIdentity().run();
	}

	/**
	 * DBCP conection pooling context
	 */
	private Context context;

	private DatabaseData dd_src;
	private DatabaseData dd_dst;

	private String tableLeft;
	private String tableRight;

	private void run() {

		// set up pool context
		setUpPoolContext();

		dd_src = new DatabaseData(DatabaseData.TYPE_MYSQL, "localhost", "3306", "test", "test#", "test");
		dd_src.setServletContext(null, context, "jdbc/conn_test");

		// setup the destination database connection (could use teh same context name as 'jdbc/conn_test')
		dd_dst = new DatabaseData(DatabaseData.TYPE_MYSQL, "localhost", "3306", "test", "test#", "test");
		dd_dst.setServletContext(null, context, "jdbc/conn_test2");

		// from and to tables
		tableLeft = "import_zipcodes_test_all";
		tableRight = "import_zipcodes_test_transfer_byidentity";

		
		
		//transfer();
		
		
		testTable();

	}
	
	public void testTable() {

		String[] tables = {tableLeft, tableRight};

		CompareTables ct = new CompareTables(dd_src, dd_dst);
		ct.checkTablesCounts(tables);

		ct.compareTableData(tableLeft, tableRight);

	}

	private void transfer() {

		ColumnData[] columnIdentities = new ColumnData[3];
		columnIdentities[0] = new ColumnData(tableLeft, "zip_code", "varchar(255)");
		columnIdentities[1] = new ColumnData(tableLeft, "latitude", "varchar(255)");
		columnIdentities[2] = new ColumnData(tableLeft, "state_abbreviation", "varchar(255)");


		Transfer transfer = new Transfer(dd_src, dd_dst);
		transfer.setOffsetLimit(10); 
		transfer.setThreadsToSpawn(10);
		transfer.setIdentities(columnIdentities);
		transfer.transferAllFields(tableLeft, tableRight);

	}

	/**
	 * setup the inital context for the connection pooling
	 */
	private void setUpPoolContext() {

		File executionlocation = null;
		try {
			executionlocation = new File(Run_Transfer_Zipcodes_ByIdentity.class.getProtectionDomain().getCodeSource().getLocation().toURI());
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		String execPath = executionlocation.getParent();


		// the context.xml resource (datasource) settings - this defines the pool
		String path = execPath + "/war/META-INF/context.xml";  

		String contextXmlPath = path;

		// stores the datasource settings for the pool here
		String tmpPath = "/Users/branflake2267/tmp";

		// make my own context
		//ManualSetupInitialPoolingContext ic = new ManualSetupInitialPoolingContext(tmpPath);
		//ic.setContextXmlFileLocation(contextXmlPath);
		//ic.run();
		//context = ic.getInitialContext();

	}

	private void deleteDestRecords(DatabaseData dd) {

		String sql = "DELETE FROM " + tableRight;
		MySqlQueryUtil.update(dd, sql);
	}



}
