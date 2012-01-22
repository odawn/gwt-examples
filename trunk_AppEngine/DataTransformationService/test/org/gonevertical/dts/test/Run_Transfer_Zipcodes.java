package org.gonevertical.dts.test;

import java.io.File;
import java.net.URISyntaxException;

import javax.naming.Context;

import org.gonevertical.dts.data.DatabaseData;
import org.gonevertical.dts.lib.pooling.ManualSetupOfInitialPoolingContext;
import org.gonevertical.dts.lib.sql.MySqlQueryUtil;
import org.gonevertical.dts.process.Transfer;

/**
 * test transfer by transfering zipcodes from one table to another
 * 
 * @author branflake2267
 *
 */
public class Run_Transfer_Zipcodes {
  
  public static void main(String[] args) {
    new Run_Transfer_Zipcodes().run();
  }
  
  /**
   * DBCP conection pooling context
   */
	private Context context;
	
	private String toTable;

  private void run() {
    
  	// set up pool context
  	setUpPoolContext();
    	
  	// setup the source database connection
    DatabaseData dd_src = new DatabaseData(DatabaseData.TYPE_MYSQL, "localhost", "3306", "test", "test#", "test");
    dd_src.setServletContext(null, context, "jdbc/conn_test");
    
    // setup the destination database connection (could use teh same context name as 'jdbc/conn_test')
    DatabaseData dd_dst = new DatabaseData(DatabaseData.TYPE_MYSQL, "localhost", "3306", "test", "test#", "test");
    dd_dst.setServletContext(null, context, "jdbc/conn_test2");
        
    // from and to tables
    String fromTable = "import_zipcodes_test_all";
    toTable = "import_zipcodes_test_transfer";
    
    // reset dest table
    deleteDestRecords(dd_dst);
    
    Transfer transfer = new Transfer(dd_src, dd_dst);
    transfer.setOffsetLimit(1000); // how many records to read at a time, its low here for testing
    transfer.setThreadsToSpawn(4);
    transfer.deleteSource(true);
    transfer.transferAllFields(fromTable, toTable);
    
    transfer = null;
    
    transfer = new Transfer(dd_src, dd_dst);
    transfer.setOffsetLimit(1000); // how many records to read at a time, its low here for testing
    transfer.setThreadsToSpawn(4);
   
    transfer.transferAllFields(fromTable, toTable);
    
  }
  
  /**
   * setup the inital context for the connection pooling
   */
  private void setUpPoolContext() {
		
		File executionlocation = null;
    try {
      executionlocation = new File(Run_Transfer_Zipcodes.class.getProtectionDomain().getCodeSource().getLocation().toURI());
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
		ManualSetupOfInitialPoolingContext ic = new ManualSetupOfInitialPoolingContext(tmpPath);
		context = ic.getInitialContext();
	}
  
  private void deleteDestRecords(DatabaseData dd) {
  	
  	String sql = "DELETE FROM " + toTable;
  	MySqlQueryUtil.update(dd, sql);
  }
  
}
