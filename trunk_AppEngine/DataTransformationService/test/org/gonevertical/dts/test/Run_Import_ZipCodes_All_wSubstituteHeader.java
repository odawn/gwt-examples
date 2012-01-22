package org.gonevertical.dts.test;

import java.io.File;
import java.net.URISyntaxException;

import org.gonevertical.dts.data.DatabaseData;
import org.gonevertical.dts.data.FieldData;
import org.gonevertical.dts.data.SourceData;
import org.gonevertical.dts.v2.DestinationData;
import org.gonevertical.dts.v2.ProcessImport;


public class Run_Import_ZipCodes_All_wSubstituteHeader {

  // source data point
  private static SourceData sourceData = null;
  
  // destination data point
  private static DestinationData destinationData = null;
  
  
  public static void main(String[] args) {
    run();
  }
  
  public static void run() {
    
    // set path dynamically
    File executionlocation = null;
    try {
      executionlocation = new File(Run_Test_Import_v1.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    
    // files system root location
    String execPath = executionlocation.getParent();
    
    // source settings
    sourceData = new SourceData();

    // header
    File file = new File(execPath + "/data/import/zip_codes_onlyheader.csv");
    boolean ignoreFirstRow = false;
    char delimiter = ",".charAt(0);
    sourceData.setSubstitueHeaders(file, ignoreFirstRow, delimiter);
    
    sourceData.setIgnoreLastRow(true);
    
    // data
    String pathToFile = execPath + "/data/import/zip_codes_noheader.csv"; 
    sourceData.delimiter = ",".charAt(0);
    sourceData.file = new File(pathToFile);
    
    // des identity fields
    FieldData[] identities = new FieldData[1];
    identities[0] = new FieldData();
    identities[0].sourceField = "zip_code";
    identities[0].destinationField = "zip_code";
    
    // des change columns
    FieldData[] changeColumns = null;

    // database settings
    DatabaseData databaseData = new DatabaseData(DatabaseData.TYPE_MYSQL, "localhost", "3306", "test", "test#", "test");
    String table = "import_zipcodes_test_all_wsubheader";
    
    
    // destination settings
    destinationData = new DestinationData();
    destinationData.displayElapsedTime();
    destinationData.setData(databaseData, changeColumns, identities, table);
    
    
    // Settings
    destinationData.dropTable = true;
    destinationData.optimise = true;
    destinationData.optimise_RecordsToExamine = 300;
    
    // debug output
    destinationData.debug = 1;
    
    
    
    ProcessImport p = new ProcessImport(sourceData, destinationData);
    p.runImport();
    destinationData.displayElapsedTime();
    
    

  }
  
}
