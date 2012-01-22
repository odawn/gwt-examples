package org.gonevertical.dts.v1;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.gonevertical.dts.data.ColumnData;
import org.gonevertical.dts.lib.datetime.DateTimeParser;


/**
 * 
 * TODO - add an option to skip non TEXT type columns, this way, not to optimize anything already possibily optimized, 
 *        optimise can always make it bigger automatically
 * 
 * @author BDonnelson
 * 
 */
public class Optimise_V1 extends SQLProcessing_V1 {

  // column field type, determined during examine
  private int fieldType = 0;
  
  // column field length, determined using getMaxFieldLength
  private int fieldLength = 0;


  @Deprecated
  private int alterTries = 0;
  
  @Deprecated
  private int alterTries_UpRecordSampleCount = 0;
  
  // track sampling of record analyziation steps
  private int analyzeTrackingNextNotfication = 500;
  
  // track sampling of record index
  private int analyzeTracking = 0;
    
  // when formating a date column, transform it to a new column instead of updating it self.
  // TODO may move this to a different format later?
  private String transformToColumn = null;

  // this is the file being processed
  private File file = null;
  
  /**
   * constructor
   */
  public Optimise_V1() {
  }

  protected void runOptimise(ColumnData[] columns) {

    if (dd.optimise == false) {
      System.out.println("skipping optimising: destinationData.optimise = false");
      return;
    }

    // loop through each column
    optimizeColumns(columns);

    System.out.println("Done Optimising");
  }

  /**
   * I want to optimise this table
   */
  public void runOptimise() {

	System.out.println("Running Optimise");
	  
    if (dd.optimise == false) {
      System.out.println("skipping optimising: destinationData.optimise = false");
      return;
    }

    // open connections to work with the data
    openConnection();

    // have to delete all indexes except primary key so to fix the indexed columns
    // TODO - change this to remember indexing then restore indexing!!!!!!!
    deleteIndexesBeforeOptimisation();
    
    // do this first, b/c it will cause problems after getting columns
    deleteEmptyColumns();

    // get columns in the table
    ColumnData[] columns = getColumns();

    // go fix the columns up - make em better
    columns = fixColumns(columns);

    // loop through each column
    optimizeColumns(columns);

    // mysql optimize
    optimizeTable();
    
    // fix indexes
    repairTable();
    
    // close the connections at the end
    closeConnection();
  }
  
  public void optimizeTable() {
    String sql = ""; 
    if (databaseType == 1) {
      sql = "OPTIMIZE TABLE " + dd.table;
    }
    
    try {
      Connection conn = getConnection();
      Statement update = conn.createStatement();
      update.executeUpdate(sql);
      update.close();
      
    } catch (SQLException e) {
      System.err.println("optimize failure: " + sql);
      e.printStackTrace();
      System.out.println("");
    }
  }
  
  public void repairTable() {
    String sql = "";
    if (databaseType == 1) {
      sql = "REPAIR TABLE " + dd.table + " QUICK";
    }
    try {
      Connection conn = getConnection();
      Statement update = conn.createStatement();
      update.executeUpdate(sql);
      update.close();
      
    } catch (SQLException e) {
      System.err.println("repair failure: " + sql);
      e.printStackTrace();
      System.out.println("");
    }
  }
  
  /**
   * I want to optimise and index these columns
   * 
   * @param c - name of the column in array
   */
  public void runIndexing(String[] c) {
    // work around
    dd.optimise_TextOnlyColumnTypes = false;
    
    int indexKind = INDEXKIND_DEFAULT;
    runIndexing(c, indexKind);
  }
  
  public void runIndexing_FullText(String[] c) {
    int indexKind = INDEXKIND_FULLTEXT;
    runIndexing(c, indexKind);
  }
  
  private void runIndexing(String[] c, int indexKind) {
    
    if (c == null) {
      System.out.println("no columns to index");
      return;
    }
    
    openConnection();
    
    // TODO - optional does indexing need to be deleted first?
    
    ColumnData[] columns = getColumns(c);
    
    // first optimise the columns that need indexing
    // TODO - change column to text if not text
    if (indexKind != INDEXKIND_FULLTEXT) {
      optimizeColumns(columns);
    } 
    
    // index the same columns
    indexColumns(columns, indexKind);
    
    // close the connections at the end
    closeConnection();
    
    System.out.println("Done Indexing!");
  }
  
  /**
   * I want to optimise and reverse index these columns
   * 
   * @param column
   */
  public void runIndexing_Reverse(String[] c) {
    
    if (c.length == 0) {
      System.out.println("nothing to reverse index");
      return;
    }
    
    // open connections to work with the data
    openConnection();
    
    // TODO - optional does indexing need to be deleted first?
       
    // first optimise the columns that need indexing
    ColumnData[] columns = getColumns(c);
    //loopThroughColumns(columns);
    
    // copy data to reverse columns
    columns = createReverseColumns(columns);
    
    if (columns.length == 0) {
      return;
    }
    
    // index the same columns
    indexColumns(columns, INDEXKIND_DEFAULT);
    
    // close the connections at the end
    closeConnection();
    
    System.out.println("Done Indexing!");
  }

  /**
   * delete all the indexes, then optimise all columns
   * Note: not able to alter a indexed column
   * 
   * TODO - change this on a need as basis, may have done already??
   * TODO - should remember indexing, then restore it
   */
  private void deleteIndexesBeforeOptimisation() {
    
    if (dd.skipDeletingIndexingBeforeOptimise == true) {
      return;
    }
    
    // delete all indexes, so columns can be optimised, then re-index
    deleteAllIndexs();
    
  }
  
  /**
   * resize a column
   * 
   * @param column
   * @param columnType
   * @param length
   */
  protected String resizeColumn(String column, String columnType, int length) {
    
    this.fieldLength = length;
    this.fieldType = getFieldType(columnType);

    System.out.println("resizeColumn: " + fieldType);
    
    String type = "";
    if (databaseType == 1) {
      type = getColumnType_MySql();
    } else if (databaseType == 2) {
      type = getColumnType_MsSql();
    }

    alterColumn(column, type);
    
    return type;
  }

  private void optimizeColumns(ColumnData[] columns) {

    int i2 = columns.length - 1;
    for (int i = 0; i < columns.length; i++) {

      if (columns[i] != null) {
        // reset
        fieldType = 0;
        fieldLength = 0;
  
        // console
        System.out.println(i2 + ". Analyzing column: " + columns[i].getName());
  
        // analyze column
        analyzeColumn(columns[i].getName());
  
        // get type that was determined by analyzeColumn
        String columnType = getColumnType();
  
        // alter column
        alterColumn(columns[i].getName(), columnType);
      }
      
      i2--;
    }

  }

  /**
   * alter column
   * 
   * NOTE - now using getMaxFieldLength to find columns max length
   * 
   * @param column
   * @param columnType
   */
  private void alterColumn(String column, String columnType) {

    // skip system columns
    if (column.equals(uniqueIdField) | column.equals("DateCreated") | column.equals("DateUpdated")) {
      return;
    }
    
    // possible skip: if there is an index on this column get rid of the index, so it can be resized
    deleteIndexsForColumn(column);
    
    // possible skip: does column already have this type? skip if it does
    ColumnData compareColumn = getColumn(column);
    
    if (columnType.toLowerCase().contains(compareColumn.getSqlType().toLowerCase())) {
      //System.out.println("Column already has this columnType: " + compareColumn.type);
      return;
    }

    // TODO - the enable keys and disable keys still take the same amount of time, 
    // and ending the task in the middle of, will cause the keys to be disabled until enabled manually later.
    String modifyColumn = "";
    String alterQuery = "";
    String disableKeys = null;
    String enableKeys = null;
    if (databaseType == 1) {
      modifyColumn = "`" + column + "` " + columnType;
      //disableKeys = "ALTER TABLE `" + dd.database + "`.`" + dd.table + "` DISABLE KEYS; ";
      alterQuery += "ALTER TABLE `" + dd.database + "`.`" + dd.table + "` MODIFY COLUMN " + modifyColumn + "";
      //enableKeys = "ALTER TABLE `" + dd.database + "`.`" + dd.table + "` ENABLE KEYS; ";
    } else if (databaseType == 2) {
      modifyColumn = "[" + column + "] " + columnType;
      alterQuery = "ALTER TABLE " + dd.database + "." + dd.tableSchema + "." + dd.table + " ALTER COLUMN " + modifyColumn;
    }

    System.out.println("altering: " + alterQuery);
    
    try {
      Connection conn = getConnection();
      Statement update = conn.createStatement();
      if (disableKeys != null) {
        update.executeUpdate(disableKeys);
      }
      update.executeUpdate(alterQuery);
      if (enableKeys != null) {
        update.executeUpdate(enableKeys);
      }
      update.close();
      
    } catch (SQLException e) {
      System.err.println("Alter failure: " + alterQuery);
      e.printStackTrace();
      System.out.println("");
    }

  }
  
  private String getLimitQuery() {

    // when this is set to 0 sample all
    if (dd.optimise_RecordsToExamine <= 0) {
      return "";
    }

    int limit = 0;
    if (dd.optimise_RecordsToExamine > 0) {
      limit = dd.optimise_RecordsToExamine;
    } 

    String sql = "";
    if (limit > 0) {
      if (databaseType == 1) {
        sql = " LIMIT 0," + dd.optimise_RecordsToExamine + " ";
      } else if (databaseType == 2) {
        sql = " TOP " + dd.optimise_RecordsToExamine + " ";
      }
    }

    return sql;
  }

  /**
   * analyze a column for its column type and length 
   *    like varchar(50)
   *    
   * NOTE: on large tables random makes it create a tmp table sort which could take a while
   * 
   * @param column
   */
  private void analyzeColumn(String column) {

    if (column.equals(uniqueIdField) | column.equals("DateCreated") | column.equals("DateUpdated")) {
      System.out.println("Skipping Internal: " + column);
      return;
    }
    
    // this will get the field length for sure
    fieldLength = getMaxFieldLength(column);
    
    // we don't need to analyze anything if its going to be varchar/text
    if (dd.optimise_TextOnly == true) {
      fieldType = 2;
      return;
    }
    
    // If a sampling number is set, sample randomly
    // this helps in large record sets
    String random = "";
    if (dd.optimise_RecordsToExamine > 0 && dd.optimise_skipRandomExamine == false) {
      if (databaseType == 1) {
        random = "ORDER BY RAND()";
      } else if (databaseType == 2) {
        random = "ORDER BY NEWID()";
      }
    }
    
    String ignoreNullValues = "";
    if (dd.optimise_ignoreNullFieldsWhenExamining == true) {
      if (databaseType == 1) {
        ignoreNullValues = "WHERE (" + column + " IS NOT NULL)";
      } else if (databaseType == 2) {
        ignoreNullValues = "WHERE (" + column + " IS NOT NULL)";
      }
    }

    String query = "";
    if (databaseType == 1) {
      query = "SELECT " + column + " " + 
        "FROM " + dd.database + "." + dd.table + " " + ignoreNullValues + " " + random + " " + getLimitQuery() + ";"; 
      
    } else if (databaseType == 2) {
      query = "SELECT " + getLimitQuery() + " " + column + " " + 
        "FROM " + dd.database + "." + dd.tableSchema + 
        "." + dd.table + " " + ignoreNullValues + " " + random + ";"; 
    }

    //System.out.println("Analyzing Column For Type: " + column + " query: " + query);

    try { 
      // only read 500 records at a time, so not use a ton of memory for large samples
      Connection conn = getConnection();
      Statement select = conn.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY);
      select.setFetchSize(500); 
      ResultSet result = select.executeQuery(query);
      int i = 0;
      while (result.next()) {
       
        // draw to screen where we are at every so often
        trackAnalyzation(i);
        examineField(result.getString(1));
        
        i++;
      }
      select.close();
      result.close();
    } catch (SQLException e) {
      System.err.println("SQL Statement Error:" + query);
      e.printStackTrace();
    }
    

  }
  
  private int getMaxFieldLength(String column) {
    
    String sql = "";
    if (databaseType == 1) {
      sql = "SELECT MAX(LENGTH(`" + column + "`)) FROM " + dd.database + "." + dd.table;
    } else if (databaseType == 2) {
      // TODO
      sql = "";
    }
    
    //System.out.println("getMaxFieldLength sql: " + sql);
    
    int columnLen = getQueryInt(sql);
    
    return columnLen;
  }
  
  /**
   * figure out what type of column 
   * 
   * TODO - when down to varchar, stop examining rows
   * TODO - examine from beginning to end, use ratio instead
   *    if found 30%(guess) and there all numbers? maybe set that as a num instead of looking through all
   * @param s
   */
  private void examineField(String s) {

    if (s == null) {
      s = "";
    }

    // whats the size of the field length
    setFieldLength(s);

    boolean isInt = false;
    boolean isZero = false;
    boolean isDecimal = false;
    boolean isDate = false;
    boolean isText = false;
    boolean isEmpty = false;

    isDate = isDate(s);

    if (isDate == false) {
      isText = isText(s);
    }

    if (isText == false) {
      isInt = isInt(s);
    }

    if (isInt == true) {
      isZero = isIntZero(s);
    }

    if (isText == false) {
      isDecimal = isDecimal(s);
    }

    isEmpty = isEmpty(s);

    if (isDate == true) { // date is first b/c it has text in it
      fieldType = 1; // date

    } else if (isText == true && fieldType != 1) {
      fieldType = 2; // varchar

    } else if (isInt == true && isZero == true && fieldType != 1 && fieldType != 2 && fieldType != 5) { // not date, text, decimal
      fieldType = 3; // int with zeros infront 0000123

    } else if (isInt == true && fieldType != 2 && fieldType != 3 && fieldType != 5) { // not date,text,decimal
      fieldType = 4; // is Int

    } else if (isDecimal == true && fieldType != 1 && fieldType != 2) {
      fieldType = 5; // is decimal

    } else if (isEmpty == true && fieldType != 1 && fieldType != 2 && fieldType != 3 && fieldType != 4 && fieldType != 5) { // has nothing
      fieldType = 6;
    } else {
      fieldType = 7;
    }

    System.out.println("fieldType: " + fieldType + " Length: " + fieldLength + " Value::: " + s);
  }

  /**
   * change the field type int a Int reference
   * 
   * TODO - change field types int static final vars
   * 
   * @param columnType
   * @return
   */
  private int getFieldType(String columnType) {

    String type = columnType.toLowerCase();

    int fieldType = 0;
    if (type.contains("text")) {
      fieldType = 2;
    } else if (type.contains("date")) {
      fieldType = 1;
    } else if (type.contains("varchar")) {
      fieldType = 2;
    } else if (type.contains("int")) {
      fieldType = 3;
    } else if (type.contains("decimal")) {
      fieldType = 5;
    } else if (type.length() == 0) {
      fieldType = 6;
    }

    return fieldType;
  }

  /**
   * get ready to figure out how to alter the column
   */
  private String getColumnType() {

    if (dd.optimise_TextOnly == true) {
      fieldType = 2;
    }
    
    String columnType = null;

    if (databaseType == 1) {
      columnType = getColumnType_MySql();
    } else if (databaseType == 2) {
      columnType = getColumnType_MsSql();
    }

    return columnType;
  }

  private String getColumnType_MySql() {

    int len = getLenthForType();

    if (len == 0) {
      len = 1;
    }

    String columnType = "";
    switch (fieldType) {
    case 1: // datetime
      // TODO must transform column text into date
      // columnType = "DATETIME DEFAULT NULL";
      columnType = getText_Mysql();
      break;
    case 2: // varchar
      columnType = getText_Mysql();
      break;
    case 3: // int unsigned - with zero fill
      if (len < 10) {
        columnType = "INT UNSIGNED ZEROFILL DEFAULT " + len;
      } else {
        columnType = "BIGINT UNSIGNED ZEROFILL DEFAULT " + len;
      }
      break;
    case 4: // int
      if (len < 10) {
        columnType = "INT DEFAULT 0";
      } else {
        columnType = "BIGINT DEFAULT 0";
      }
      break;
    case 5: // decimal
      // TODO figure - columnType = "DECIMAL(18, 2) DEFAULT NULL";
      columnType = getText_Mysql();
      break;
    case 6: // empty
      columnType = getText_Mysql();
      break;
    case 7: // other
      columnType = getText_Mysql();
      break;
    default: // all others
      columnType = getText_Mysql();
      break;
    }

    return columnType;
  }
  
  private String getColumnType_MsSql() {

    int len = getLenthForType();

    if (len == 0) {
      len = 1;
    }

    String columnType = "";
    switch (fieldType) {
    case 1: // datetime
      columnType = "[DATETIME] NULL";
      break;
    case 2: // varchar
      if (len > 255) {
        columnType = "TEXT NULL";
      } else {
        columnType = "VARCHAR(" + len + ") NULL";
      }
      break;
    case 3: // int unsigned - with zero fill
      // TODO ?? for zero fill?
      columnType = "[INT] NULL";
      break;
    case 4: // int
      if (len < 8) {
        columnType = "[INT] NULL";
      } else {
        columnType = "[BIGINT] NULL";
      }
      break;
    case 5: // decimal
      // TODO - [decimal](18, 0) NULL
      columnType = "[VARCHAR](50) NULL";
      break;
    case 6: // empty
      columnType = "[VARCHAR](" + len + ") NULL"; 
      break;
    case 7: // other
      columnType = "[VARCHAR](" + len + ") NULL";                                    
      break;
    default:
      if (len > 255) {
        columnType = "TEXT DEFAULT NULL";
      } else {
        columnType = "VARCHAR(" + len + ") NULL";
      }
      break;
    }

    return columnType;
  }
  
  private String getText_Mysql() {
    int len = getLenthForType();
    String columnType = "";
    if (len > 255) {
      columnType = "TEXT DEFAULT NULL";
    } else {
      columnType = "VARCHAR(" + len + ") DEFAULT NULL";
    }
    return columnType;
  }

  /**
   * optimise length of a particular column Type
   * 
   * TODO - figure out decimal, watch for before and after . during parse TODO -
   * do I want to make a percenatage bigger than needed? like make bigger * 10%
   * although, for now I think I will skip this, make it exact, seems to work
   * 
   * @return
   */
  private int getLenthForType() {

    int l = 0;

    switch (fieldType) {
    case 1: // datetime
      l = fieldLength;
      break;
    case 2: // varchar
      l = fieldLength;
      break;
    case 3: // int unsigned - with zero fill
      l = fieldLength;
      break;
    case 4: // int
      l = fieldLength;
      break;
    case 5: // decimal
      l = fieldLength;
      break;
    case 6: // empty
      l = fieldLength;
      break;
    case 7: // other
      l = fieldLength;
      break;
    default:
      l = fieldLength;
      break;
    }

    return l;
  }

  private void setFieldLength(String s) {
    int size = s.length();
    if (size > fieldLength) {
      fieldLength = size;
    }
  }

  private boolean isEmpty(String s) {
    boolean b = false;
    if (s.isEmpty()) {
      b = true;
    }
    return b;
  }

  private boolean isText(String s) {
    boolean b = false;
    if (s.matches(".*[a-zA-Z].*")) {
      b = true;
    }
    return b;
  }

  private boolean isInt(String s) {
    boolean b = false;
    if (s.matches("[0-9]+")) {
      b = true;
    }
    return b;
  }

  /**
   * starts with zeros
   * @param s
   * @return
   */
  private boolean isIntZero(String s) {
    boolean b = false;
    if (s.matches("[0-9]+") && s.matches("^0[0-9]+")) {
      b = true;
    }
    return b;
  }

  private boolean isDecimal(String s) {
    boolean b = false;
    if (s.matches("^\\d+\\.\\d+|\\.\\d+")) {
      b = true;
    }
    return b;
  }

  /**
   * TODO add more date identifications
   * 
   * TODO -> jan 09 -> transform it too.
   * TODO - transform the value date into sql date to insert when transforming column
   * 
   * formats
   * 12/01/2009
   * 2009/12/01
   * 01/12/2009
   * jan 01 2009
   * January 01, 2009
   * 2009-12-01 00:00:00
   * 2009-12-01 00:00:00AM
   * 20091201000000
   * 
   * @param s
   * @return
   */
  private boolean isDate(String s) {

    s = s.toLowerCase();

    if (s.length() == 0) {
      return false;
    }

    boolean b = false;

    if (s.contains("jan")) {
      b = true;
    } else if (s.contains("feb")) {
      b = true;
    } else if (s.contains("mar")) {
      b = true;
    } else if (s.contains("apr")) {
      b = true;
    } else if (s.contains("may")) {
      b = true;
    } else if (s.contains("jun")) {
      b = true;
    } else if (s.contains("jul")) {
      b = true;
    } else if (s.contains("aug")) {
      b = true;
    } else if (s.contains("sep")) {
      b = true;
    } else if (s.contains("oct")) {
      b = true;
    } else if (s.contains("nov")) {
      b = true;
    } else if (s.contains("dec")) {
      b = true;
    }

    // TODO - proof this later
    if (s.matches("[0-9]{1,2}[-/][0-9]{1,2}[-/][0-9]{2,4}.*")) {
      b = true;
    }

    return b;
  }
  
  /**
   * draw to screen every so often where we are at in the record set
   * @param i
   */
  private void trackAnalyzation(int i) {
    if (i == analyzeTracking) {
      System.out.println(i);
      analyzeTracking = analyzeTracking + analyzeTrackingNextNotfication;
    }
  }

  /**
   * index several columns
   * 
   * @param columns
   */
  private void indexColumns(ColumnData[] columns, int indexKind) {
    
    String type = "";
    if (indexKind == INDEXKIND_FULLTEXT) {
      type = "FT_";
    }
    
    for (int i=0; i < columns.length; i++) {
      if (columns[i] != null) {
        String indexName = "auto_" + type + columns[i].getName() ;
        String column = "`" + columns[i].getName() + "`";
        createIndex(indexName, column, indexKind);
      }
    }
    
  }
  
  /**
   * this may not be the way that will work on other identity values that are ints
   */
  public void deleteDuplicates() {
    
    // set dd
    openConnection();
    
    int c = getTableHasDuplicates();
    
    if (c == 0) {
     System.out.println("No duplicates exist for the identities.");
     return;
    }
    
    String idents_Columns = getIdentitiesColumns_inCsv();
    
    // load the records that indicate they there duplicates
    String sql = "";
    if (databaseType == 1) {
      sql = "SELECT " + uniqueIdField + " FROM " + dd.database + "." + dd.table + 
          " GROUP BY "+ idents_Columns + " HAVING count(*) > 1;"; 
    } else if (databaseType == 2) {
      // TODO
      sql = "";
    }
    
    System.out.println(sql);
    
    try {
      Connection conn = getConnection();
      Statement select = conn.createStatement();
      ResultSet result = select.executeQuery(sql);
      while (result.next()) {
        getDuplicateValues(result.getInt(1));
      }
      select.close();
      result.close();
    } catch (SQLException e) {
      System.err.println("Mysql Statement Error:" + sql);
      e.printStackTrace();
    }
    
    closeConnection();
  }
  
  private void getDuplicateValues(int uniqueId) {
    
    String idents_Columns = getIdentitiesColumns_inCsv();
    String where = "WHERE " + uniqueIdField + "='" + uniqueId + "'";
    
    String sql = "";
    if (databaseType == 1) {
      sql = "SELECT "+ idents_Columns + " FROM " + dd.database + "." + dd.table + " " + where; 
    } else if (databaseType == 2) {
      // TODO
      sql = "";
    }
    
    System.out.println(sql);
    
    try {
      Connection conn = getConnection();
      Statement select = conn.createStatement();
      ResultSet result = select.executeQuery(sql);
      while (result.next()) {
        String[] values = new String[dd.identityColumns.length];
        for (int i=0; i < dd.identityColumns.length; i++) {
          // TODO what to do if ident is int?
          values[i] = result.getString(i+1);
        }
        deleteDuplicate(values);
      }
      select.close();
      result.close();
    } catch (SQLException e) {
      System.err.println("Mysql Statement Error:" + sql);
      e.printStackTrace();
    }
  }

  private void deleteDuplicate(String[] identValues) {
    
    String where = "";
    for (int i=0; i < dd.identityColumns.length; i++) {
      where += "" + dd.identityColumns[i].destinationField + "='" + identValues[i] + "'";
      if (i < dd.identityColumns.length-1) {
        where += " AND ";
      }
    }
    
    String sql = "";
    if (databaseType == 1) {
      sql = "SELECT " + uniqueIdField + " FROM " + dd.database + "." + dd.table + " WHERE " + where; 
    } else if (databaseType == 2) {
      // TODO
      sql = "";
    }
    
    System.out.println("sql" + sql);
    
    try {
      Connection conn = getConnection();
      Statement select = conn.createStatement();
      ResultSet result = select.executeQuery(sql);
      int i = 0;
      while (result.next()) {
        int uniqueId = result.getInt(1);
        if (i > 0) {
          deleteRecord(uniqueId);
        }
        i++;
      }
      select.close();
      result.close();
    } catch (SQLException e) {
      System.err.println("Mysql Statement Error:" + sql);
      e.printStackTrace();
    }
    
  }
  
  private void deleteRecord(int uniqueId) {
    
    String where = "" + uniqueIdField + "='" + uniqueId + "'";
    
    String sql = "";
    if (databaseType == 1) {
      sql = "delete FROM " + dd.database + "." + dd.table + " WHERE " + where; 
    } else if (databaseType == 2) {
      // TODO
      sql = "";
    }
    
    System.out.println("sql: " + sql);
    
    // TODO 
    updateSql(sql);
  }
  
  /**
   * format a column values to the common english date format
   * 
   * @param column
   */
  public void formatColumn_Date_EngString(String column) {
    int formatType = 1;
    formatColumn_Date(column, formatType);
  }
  
  /**
   * format the string to sql datetime format
   * 
   * @param column
   */
  public void formatColumn_Date_DateTime(String column) {
    int formatType = 2;
    formatColumn_Date(column, formatType);
  }
  
  /**
   * format date time column
   * 
   * @param column
   */
  private void formatColumn_Date(String column, int formatType) {
   
    openConnection();
    
    String sql = "";
    if (databaseType == 1) {
      sql = "SELECT " + uniqueIdField + ", " + column + " FROM " + dd.database + "." + dd.table; 
    } else if (databaseType == 2) {
      // TODO
      sql = "";
    }
    
    try {
      Connection conn = getConnection();
      Statement select = conn.createStatement();
      ResultSet result = select.executeQuery(sql);
      while (result.next()) {
        int uniqueId = result.getInt(1);
        String dt = result.getString(2);
        updateColumn_Date(uniqueId, column, formatType, dt);
      }
      select.close();
      result.close();
    } catch (SQLException e) {
      System.err.println("Mysql Statement Error:" + sql);
      e.printStackTrace();
    }
    
    closeConnection();
  }
  
  /**
   * transform string date into datetimestamp in this column
   * 
   * @param uniqueId
   * @param column
   */
  private void updateColumn_Date(int uniqueId, String column, int formatType, String datetime) {
    
    DateTimeParser parse = new DateTimeParser();
    
    String tranformed = "";
    if (formatType == 1) {
      tranformed = parse.getDate_EngString(datetime);
    } else {
      tranformed = parse.getDateMysql(datetime);
    }
    
    System.out.println("before: " + datetime + " after: " + tranformed);
    
    if (transformToColumn != null) {
      column = transformToColumn;
      checkDateTransformationColumn(column);
    }
    
    String sql = "";
    if (databaseType == 1) {
      sql = "UPDATE " + dd.database + "." + dd.table + " SET `" + column + "`='" + tranformed + "' WHERE " + uniqueIdField + "='" + uniqueId + "';"; 
    } else if (databaseType == 2) {
      // TODO
      sql = "";
    }
    
    System.out.println("sql: " + sql);
    
    updateSql(sql);
  }
  
  /**
   * update date transformation into new column
   * 
   * @param column
   */
  public void setColumnToTransformTo(String column) {
    transformToColumn = column;
  }
  
  /**
   * check date transformation column exists if not, create it.
   * @param column
   */
  private void checkDateTransformationColumn(String column) {
    String type = "DATETIME DEFAULT NULL";
    createColumn(column, type);
  }

  public void setFile(File file) {
    this.file = file;
  }
  
}
