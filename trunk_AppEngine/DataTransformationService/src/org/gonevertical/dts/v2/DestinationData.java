package org.gonevertical.dts.v2;

import org.gonevertical.dts.data.DatabaseData;
import org.gonevertical.dts.data.FieldData;
import org.gonevertical.dts.data.FlatFileSettingsData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Destination values - controls insert/update methods
 * 
 * @author branflake2267
 *
 */
public class DestinationData {
  
  private static final Logger log = LoggerFactory.getLogger(DestinationData.class);

  public int debug = 0;
  
  // database connection
  public DatabaseData databaseData = null;

  // match these fields and change them 
  public FieldData[] changeColumn = null;
  
  // use these columns for identity, when adding data
  //   that has similarity matching 
  public FieldData[] identityColumns;
  
  // db table
  public String table;
  
  // flat file settings - preprocessing before sql processing
  public FlatFileSettingsData ffsd = null;
  
  
  // default primary key name on import
  public String primaryKeyName = "Auto_ImportId";
  public String dateCreated = "Auto_DateCreated";
  public String dateUpdated = "Auto_DateUpdated";
  
  // drop the table being added to in the beginning
  public boolean dropTable = false;

  // auto optimise at the end of adding data
  public boolean optimise = false;

  // how many records to examine for optimisation of the column data
  // records are examine randomly over this many records
  // pause at row count and optimise then continue
  public int optimise_RecordsToExamine = 10000;

  // when Examining the data, examine the recores in the column it randomly
  public boolean optimise_skipRandomExamine = false;

  // when Examing the data, ignore null values, this will slow things down in huge record sets
  public boolean optimise_ignoreNullFieldsWhenExamining = true;

  // use this if you need to repeat optimisation and want to skip all but TEXT columns
  public boolean optimise_TextOnlyColumnTypes = true;
  
  // skip optimising to other types
  public boolean skipOptimisingIntDateTimeDecTypeColumns = false;
  
  // this will delete indexes, then optimise
  // a column can't have a index on it if it needs altering
  public boolean skipDeletingIndexingBeforeOptimise = false;

  // delete empty columns after parsing a table
  public boolean deleteEmptyColumns = false;

  // if a record exists with identity columns update
  // Identity Columns data needs to be exact (its explicit!)
  public boolean checkForExistingRecordsAndUpdate = false;

  // if identity columns are given, and this is true, 
  // create indexes of the identity columns
  // Also will need Identity Columns listed
  public boolean createIndexs = true;

  // save the source file into the records srcFile= /home/branflake2267/home.txt
  public boolean setSrcFileIntoColumn = false;
  
  // after processing the file, move it to the done folder
  public boolean moveFileToDone = false;
  
  // stop at column, don't process any columns over this count. 0 process all
  public int stopProcessingAtRowCount = 0;
  
  /**
   * stop importing at row 
   *   -1 = don't stop
   */
  public int stopAtRow = -1;
  
  /**
   * comparison before update - TODO more doc
   */
  public FieldData[] compareBeforeUpdate = null;
  
  /**
   * logging time start
   */
  private long millisecondsStart = 0;
  
  /**
   * turn on/off logging to a table
   */
  private boolean loggingToTable = true;
  
  /**
   * logging to table name
   */
  private String loggingTable = "_auto_log";
 
  /**
   * constructor
   */
  public DestinationData() {
    setStart();
  }
  
  /**
   * optimize table settings basics
   * 
   * @param databaseData
   * @param table
   */
  public void setData(DatabaseData databaseData, String table) {
    this.databaseData = databaseData;
    this.table = table;
  }
  
  /**
   * set the necessities
   * 
   * @param databaseData
   * @param changeColumns
   * @param identities
   * @param table
   */
  public void setData(DatabaseData databaseData, FieldData[] changeColumns, FieldData[] identities, String table) {
    this.databaseData = databaseData;
    this.changeColumn = changeColumns;
    this.identityColumns = identities;
    this.table = table;
    
    //for (int i=0; i < identityColumns.length; i++) {
      //identityColumns[i].isPrimaryKey = true;
    //}
  }
  
  /**
   * changing logging into log4J 
   * 
   * @param s
   */
  @Deprecated
  public void debug(String s) {
    if (debug == 0) {
      
    } else if (debug == 1) {
      System.out.println("DEBUG: " + s);
    } else if (debug == 2) {
      System.out.println("DEBUG: " + s.substring(0,20));
    } else if (debug == 3) {
      System.out.println(".");
    }
  }
  
  private void setStart() {
    millisecondsStart = System.currentTimeMillis();
  }
  
  public void displayElapsedTime() {
    long endTime = System.currentTimeMillis();
    long howLong = endTime - millisecondsStart;
    long seconds = (long) (howLong * .001);
    long min = 0;
    if (seconds > 60) {
      min = (long) (seconds / 60);
    }
    System.out.println("milliseconds: " + howLong + " seconds: " + seconds + " minutes: " + min);
  }
  
  public void getElapsedTime(long millisecondsStart) {
    long endTime = System.currentTimeMillis();
    long howLong = endTime - millisecondsStart;
    long seconds = (long) (howLong * .001);
    System.out.println("milliseconds: " + howLong + " seconds: " + seconds);
  }

	public void setLogToTable(boolean b) {
		this.loggingToTable = b;
  }
	
	public boolean getLogToTable() {
		return loggingToTable;
	}
	
	public String getLoggingTable() {
		return loggingTable;
	}

	public void setSrcFileIntoColumn(boolean b) {
	  setSrcFileIntoColumn = b;
	}
	
  public boolean getSrcFileIntoColumn() {
    return setSrcFileIntoColumn;
  }

  public DatabaseData getDatabaseData() {
    return databaseData;
  }
}
