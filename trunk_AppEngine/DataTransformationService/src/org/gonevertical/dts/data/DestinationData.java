package org.gonevertical.dts.data;

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
	
  private DatabaseData databaseData = null;

  // TODO moving to databaseData object
  @Deprecated
  public String host;
  @Deprecated
  public String port;
  @Deprecated
  public String username;
  @Deprecated
  public String password;
  @Deprecated
  public String database;
  // moving to int type in databaseData - Constants
  @Deprecated
  public String databaseType = "MySql";

  // table for records to exist
  public String table;

  // this only appears in MsSql that I know of. select * from [catalog].[tableSchema].[table]
  public String tableSchema;
  
  // identiy columns to match update instead of insert?
  public FieldData[] identityColumns;

  // drop table if exists before insert 
  // (if looping files in a directory, it will drop table in the beginning)
  public boolean dropTable = false;

  // auto optimise at the end of inserting/updating records
  // this will skip optimising table and can do this separately if needed.
  public boolean optimise = false;

  // how many records to examine for optimisation of the column data
  // records are examine randomly over this many records
  public int optimise_RecordsToExamine = 1000; 

  // when Examining the data, examine the recores in the column it randomly
  public boolean optimise_skipRandomExamine = false;

  // when Examing the data, ignore null values, this will slow things down in huge record sets
  public boolean optimise_ignoreNullFieldsWhenExamining = true;

  // optimise lengths into varchar and text only
  // this will have to be done first every time, so that
  // one can go from varchar->date, varchar->int (instead of text>date=will not work)
  // TO much confusion over this var, needs to get rid of it
  @Deprecated
  public boolean optimise_TextOnly = true;

  // use this if you need to repeat optimisation and want to skip all but TEXT columns
  public boolean optimise_TextOnlyColumnTypes = false;
  
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

  // when no field names exist in the first row, make this true
  public boolean firstRowHasNoFieldNames = false;

  // save the source file into the records srcFile= /home/branflake2267/home.txt
  public boolean setSrcFileIntoColumn = false;
  
  // after processing the file, move it to the done folder
  public boolean moveFileToDone = false;
  
  // stop at column, don't process any columns over this count. 0 process all
  public int stopAtColumnCount = 0;
  
  /**
   * make database type an integer
   * 
   * TODO - move to setDatabaseData object - static constant
   * 
   * @return
   */
  @Deprecated
  public int getDbType() {
    databaseType = databaseType.toLowerCase();
    int type = 0;
    if (this.databaseType.equals("mysql")) {
      type = DatabaseData.TYPE_MYSQL;
    } else if (databaseType.equals("mssql")) {
      type = DatabaseData.TYPE_MSSQL;
    } else {
      System.err.println("ERROR: No DatabaseTye: [MySql|MsSql]");
      System.exit(1);
    }
    return type;
  }
  
  /**
   * set database data
   * 
   * splitting up this class so I can reuse the database settings for other activites needed
   * 
   * @param databaseData
   */
  public void setDatabaseData(DatabaseData databaseData) {
    this.databaseData = databaseData;
    this.host = databaseData.getHost();
    this.port = databaseData.getPort();
    this.username = databaseData.getUsername();
    this.password = databaseData.getPassword();
    this.database = databaseData.getDatabase();
    
    if (databaseData.getDatabaseType() == DatabaseData.TYPE_MYSQL) {
      this.databaseType = "MySql";
    } else if (databaseData.getDatabaseType() == DatabaseData.TYPE_MSSQL) {
      this.databaseType = "MsSql";
    }
  }
  
}
