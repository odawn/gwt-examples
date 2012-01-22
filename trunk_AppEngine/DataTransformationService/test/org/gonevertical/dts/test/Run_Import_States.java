package org.gonevertical.dts.test;

import java.io.File;
import java.net.URISyntaxException;

import org.gonevertical.dts.data.ColumnData;
import org.gonevertical.dts.data.DatabaseData;
import org.gonevertical.dts.data.FieldData;
import org.gonevertical.dts.data.SourceData;
import org.gonevertical.dts.lib.sql.transformlib.MySqlTransformLib;
import org.gonevertical.dts.v2.DestinationData;
import org.gonevertical.dts.v2.Indexing;
import org.gonevertical.dts.v2.ProcessImport;


public class Run_Import_States {

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
    String execPath = executionlocation.getParent();
    String pathToFile = execPath + "/data/export/states_0.csv"; 
    
    // source
    sourceData = new SourceData();
    sourceData.delimiter = ",".charAt(0);
    sourceData.file = new File(pathToFile);
    
    // des identity fields
    FieldData[] identities = new FieldData[1];
    identities[0] = new FieldData();
    identities[0].sourceField = "Name";
    identities[0].destinationField = "Name";
    
    // des change columns
    FieldData[] changeColumns = new FieldData[1];
    changeColumns[0] = new FieldData();
    changeColumns[0].sourceField = "Ab";
    changeColumns[0].destinationField = "TwoLetter";
    
    // database settings
    DatabaseData databaseData = new DatabaseData(DatabaseData.TYPE_MYSQL, "ark_home", "3306", "test", "test#", "test");
    String table = "import_states_test";
    
    // destination settings
    destinationData = new DestinationData();
    destinationData.setData(databaseData, changeColumns, identities, table);
    
    // Settings
    destinationData.dropTable = true;
    destinationData.optimise = true;
    destinationData.debug = 1;
    destinationData.setSrcFileIntoColumn(true);
    
    ProcessImport p = new ProcessImport(sourceData, destinationData);
    p.runImport();
    
    
    
    // repeat the process to update
    destinationData.dropTable = false;
    
    // try it agian
    pathToFile = execPath + "/data/export/states_0_testalter.csv"; 
    sourceData.file = new File(pathToFile);
    ProcessImport p2 = new ProcessImport(sourceData, destinationData);
    p2.runImport();
    
    
    ColumnData[] indexColumns = new ColumnData[2];
    indexColumns[0] = new ColumnData();
    indexColumns[1] = new ColumnData();
    
    indexColumns[0] = new MySqlTransformLib().queryColumn(destinationData.databaseData, table, "Name");
    indexColumns[1] = new MySqlTransformLib().queryColumn(destinationData.databaseData, table, "TwoLetter");
    
    Indexing index = new Indexing(destinationData);
    index.runIndexColumns(indexColumns);
  }
  



  

}
