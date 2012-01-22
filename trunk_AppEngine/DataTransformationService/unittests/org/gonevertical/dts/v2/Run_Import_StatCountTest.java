package org.gonevertical.dts.v2;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.net.URISyntaxException;

import org.gonevertical.dts.data.DatabaseData;
import org.gonevertical.dts.data.FieldData;
import org.gonevertical.dts.data.SourceData;
import org.gonevertical.dts.data.StatData;
import org.gonevertical.dts.test.Run_Test_Import_v1;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class Run_Import_StatCountTest {
	
	@Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }
	
  @Test
  public void testImport_StatCountWithHeader() {
  	
  	// database settings
    DatabaseData databaseData = new DatabaseData(DatabaseData.TYPE_MYSQL, "localhost", "3306", "test", "test#", "test");
  	
  	 // set path dynamically
    File executionlocation = null;
    try {
      executionlocation = new File(Run_Test_Import_v1.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    String execPath = executionlocation.getParent();
    String pathToFile = execPath + "/data/import/statcount_with_header.txt"; 
    
    // source
    SourceData sourceData = new SourceData();
    sourceData.delimiter = ",".charAt(0);
    sourceData.file = new File(pathToFile);

    
    // des identity fields
    FieldData[] identities = null;
    
    // des change columns
    FieldData[] changeColumns = null;
    
    String table = "tmp_statcount_with_header";
    

    // destination settings
    DestinationData dd = new DestinationData();
    dd.setData(databaseData, changeColumns, identities, table);
    
    
    // Settings
    dd.dropTable = true;
    dd.optimise = false;
    dd.debug = 1;

    
		ProcessImport p = new ProcessImport(sourceData, dd);
    p.runImport();
    
    StatData stats = p.getStats();
   
    long fileLineCount = stats.getFileLineCount();
    long saveCount = stats.getSaveCount();
    boolean hasErrors = stats.getHasErrors();
    boolean match = stats.doesLineCountMatchSaveCount(sourceData);
    
    assertEquals(11, fileLineCount);
    assertEquals(10, saveCount);
    assertEquals(false, hasErrors);
    assertEquals(true, match);
  }
  
  @Test
  public void testImport_StatCountWithOutHeader() {
  	
  	// database settings
    DatabaseData databaseData = new DatabaseData(DatabaseData.TYPE_MYSQL, "localhost", "3306", "test", "test#", "test");
  	
  	 // set path dynamically
    File executionlocation = null;
    try {
      executionlocation = new File(Run_Test_Import_v1.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    String execPath = executionlocation.getParent();
    String pathToFile = execPath + "/data/import/statcount_with_noheader.txt"; 
    
    // substitue header
    File file = new File(execPath + "/data/import/statcount_the_header.txt");
    char delimiter = ",".charAt(0);
    
    // source
    SourceData sourceData = new SourceData();
    sourceData.setSubstitueHeaders(file, delimiter);
    sourceData.delimiter = ",".charAt(0);
    sourceData.file = new File(pathToFile);

    
    // des identity fields
    FieldData[] identities = null;
    
    // des change columns
    FieldData[] changeColumns = null;
    
    String table = "tmp_statcount_with_noheader";
    

    // destination settings
    DestinationData dd = new DestinationData();
    dd.setData(databaseData, changeColumns, identities, table);
    
    
    // Settings
    dd.dropTable = true;
    dd.optimise = false;
    dd.debug = 1;

    
		ProcessImport p = new ProcessImport(sourceData, dd);
    p.runImport();
    
    StatData stats = p.getStats();
   
    long fileLineCount = stats.getFileLineCount();
    long saveCount = stats.getSaveCount();
    boolean hasErrors = stats.getHasErrors();
    boolean match = stats.doesLineCountMatchSaveCount(sourceData);
    
    assertEquals(10, fileLineCount);
    assertEquals(10, saveCount);
    assertEquals(false, hasErrors);
    assertEquals(true, match);
  }
  
  @Test
  public void testImport_StatCountWithOutHeaderAndLastLine() {
  	
  	// database settings
    DatabaseData databaseData = new DatabaseData(DatabaseData.TYPE_MYSQL, "localhost", "3306", "test", "test#", "test");
  	
  	 // set path dynamically
    File executionlocation = null;
    try {
      executionlocation = new File(Run_Test_Import_v1.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    String execPath = executionlocation.getParent();
    String pathToFile = execPath + "/data/import/statcount_with_noheader_nolastline.txt"; 
    
    // substitue header
    File file = new File(execPath + "/data/import/statcount_the_header.txt");
    char delimiter = ",".charAt(0);
    
    // source
    SourceData sourceData = new SourceData();
    sourceData.setSubstitueHeaders(file, delimiter);
    sourceData.delimiter = ",".charAt(0);
    sourceData.file = new File(pathToFile);
    sourceData.setIgnoreLastRow(true);

    
    // des identity fields
    FieldData[] identities = null;
    
    // des change columns
    FieldData[] changeColumns = null;
    
    String table = "tmp_statcount_with_noheader";
    

    // destination settings
    DestinationData dd = new DestinationData();
    dd.setData(databaseData, changeColumns, identities, table);
    
    
    // Settings
    dd.dropTable = true;
    dd.optimise = false;
    dd.debug = 1;

    
		ProcessImport p = new ProcessImport(sourceData, dd);
    p.runImport();
    
    StatData stats = p.getStats();
   
    long fileLineCount = stats.getFileLineCount();
    long saveCount = stats.getSaveCount();
    boolean hasErrors = stats.getHasErrors();
    boolean match = stats.doesLineCountMatchSaveCount(sourceData);
    
    assertEquals(11, fileLineCount);
    assertEquals(10, saveCount);
    assertEquals(false, hasErrors);
    assertEquals(true, match);
  }
  
  
}
