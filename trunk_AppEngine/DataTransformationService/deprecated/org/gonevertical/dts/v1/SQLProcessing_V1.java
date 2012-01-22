package org.gonevertical.dts.v1;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gonevertical.dts.data.ColumnData;
import org.gonevertical.dts.data.DatabaseData;
import org.gonevertical.dts.data.DestinationData;
import org.gonevertical.dts.data.FieldData;
import org.gonevertical.dts.data.FieldDataComparator;
import org.gonevertical.dts.data.IdentityData;
import org.gonevertical.dts.data.SortDestinationField;
import org.gonevertical.dts.lib.StringUtil;


/**
 * sql processing
 * 
 * @author branflake2267
 * 
 */
public class SQLProcessing_V1 {
 
  // index types
  // TODO - moving these to columnData
  public static final int INDEXKIND_DEFAULT = 1;
  public static final int INDEXKIND_FULLTEXT = 2;
  
  int connLoadBalance = 0;
  protected Connection conn1 = null;
  protected Connection conn2 = null;

  // track where at in the importing
  private int index = 0;
  private int indexFile = 0;

  protected DestinationData dd = null;
  
  // TODO - make this the primary 
  protected DatabaseData databaseData = null;

  private boolean dropTableOff = false;

  // replace these fields
  private FieldData[] matchFields = null;

  // what database brand?
  protected int databaseType = 0;

  // keep track of the columns name, type and length
  // to make sure the data will fit into the columns that already exist
  private ColumnData[] columns = null;

  // TODO integrate this into all the queries
  protected String uniqueIdField = "ImportID";
  
  /**
   * constructor
   */
  public SQLProcessing_V1() {
  }

  protected void dropTableOff() {
    this.dropTableOff = true;
  }
  
  public void setUniqueIdField(String uniqueIdField) {
    this.uniqueIdField = uniqueIdField;
  }

  /**
   * TODO switch into this format later
   * 
   * @param databaseData
   */
  public void setDatabaseData(DatabaseData databaseData) {
    
    // work around
    if (dd == null) {
      dd = new DestinationData();
    }
    
    // work around for now
    dd.setDatabaseData(databaseData);
    
    if (databaseData.getDatabaseType() == DatabaseData.TYPE_MYSQL) {
      this.databaseType = DatabaseData.TYPE_MYSQL;
    } else if (databaseData.getDatabaseType() == DatabaseData.TYPE_MSSQL) {
      this.databaseType = DatabaseData.TYPE_MSSQL;
    }
    
  }
  
  /**
   * set the destination data
   * 
   * @param destinationData
   * @throws Exception
   */
  public void setDestinationData(DestinationData destinationData) {

    if (destinationData == null) {
      System.out.println("ERROR: DestinationData was not Set");
      System.exit(1);
    }
    
    dd = destinationData;

    // test for db type
    this.databaseType = dd.getDbType();

    if (dd.database.length() == 0) {
      System.err.println("ERROR: No Database: Whats the database name?");
      System.exit(1);
    }

    if (dd.username.length() == 0) {
      System.err.println("ERROR: No username: What is the sql database username?");
      System.exit(1);
    }

    if (dd.password.length() == 0) {
      System.err.println("ERROR: No password: What is the sql database password?");
      System.exit(1);
    }

    if (destinationData.host.length() == 0) {
      System.err.println("ERROR: No host: Where is the server located? ie. [IpAddress|host.domain.tld]");
      System.exit(1);
    }

    if (destinationData.port.length() == 0) {
      System.err.println("ERROR: No port: What doorway is the sql server behind? ie. [3306|1433]");
      System.exit(1);
    }

    if (destinationData.table.length() == 0) {
      System.err.println("ERROR: No destination table: What table do you want to import this data to?");
      System.exit(1);
    }
  }
  
  protected void setMatchFields(FieldData[] matchFields) {
    this.matchFields = matchFields;
  }

  /**
   * open the sql connection to work with
   * 
   * TODO - I am not so sure this is the best way to open two connections. TODO
   * - resize column opens two connections which is overkill
   * 
   * @throws Exception
   */
  public void openConnection() {

    // connection 1
    if (databaseType == 1) {
      conn1 = getConn_MySql();
    } else if (databaseType == 2) {
      conn1 = getConn_MsSql();
    }

    // connection 2
    if (databaseType == 1) {
      conn2 = getConn_MySql();
    } else if (databaseType == 2) {
      conn2 = getConn_MsSql();
    }
  }

  // close connections
  public void closeConnection() {
    try {
      conn1.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
    try {
      conn2.close();
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  /**
   * does table exist?
   * 
   * @return
   */
  private boolean isTableExist() {

    String query = "";
    if (databaseType == 1) {
      query = "SHOW TABLES FROM `" + dd.database + "` LIKE '" + dd.table + "';";
    } else if (databaseType == 2) {
      query = "SELECT TABLE_NAME FROM INFORMATION_SCHEMA.TABLES "
          + "WHERE TABLE_CATALOG = '" + dd.database + "' AND "
          + "TABLE_SCHEMA = '" + dd.tableSchema + "' AND " + "TABLE_NAME = '"
          + dd.table + "'";
    }

    return getBooleanQueryFromString(query);
  }

  /**
   * create table
   * 
   */
  public void createTable() {

    boolean doesExist = isTableExist();
    if (doesExist == true) {
      if (dd.dropTable == true && dropTableOff == false) {
        dropTable();
      } else if (dd.dropTable == false && doesExist == false) {
        return;
      } else if (dropTableOff == true && doesExist == true) {
        return;
      } else if (doesExist == true) {
        return;
      }
    }

    String query = "";
    if (databaseType == 1) {
      query = "CREATE TABLE `" + dd.database + "`.`" + dd.table + "` ("
          + "`ImportID` INT NOT NULL AUTO_INCREMENT, PRIMARY KEY (`ImportID`) "
          + ") ENGINE = MyISAM;";
    } else if (databaseType == 2) {
      query = "CREATE TABLE " + dd.database + "." + dd.tableSchema + "."
          + dd.table + " " + "( [ImportID] [INT] IDENTITY(1,1) NOT NULL);";
    }
    updateSql(query);
  }

  /**
   * create the things needed to make the auto import go smoothly
   * 
   * 1. create import id 2. create date created field 3. create date updated
   * field 4. create index of teh identities, to make the updates go faster
   */
  protected void createDefaultFields() {
    // in some cases the fields below might not exist so lets verify that.

    // DateCreated - shows when this record was inserted
    String column = "DateCreated";
    String type = "";
    if (databaseType == 1) {
      type = "DATETIME DEFAULT NULL";
    } else if (databaseType == 2) {
      type = "DATETIME NULL";
    }
    createColumn(column, type);

    // DateUpdated - used to show when update happened
    if (dd.checkForExistingRecordsAndUpdate == true) {
      column = "DateUpdated";
      type = "";
      if (databaseType == 1) {
        type = "DATETIME DEFAULT NULL";
      } else if (databaseType == 2) {
        type = "DATETIME NULL";
      }
      createColumn(column, type);
    }

    // create indexes of identity columns listed
    if (dd.createIndexs == true && dd.identityColumns != null) {
      createIndex_forIdentities();
    }
  }

  /**
   * sql drop table
   */
  public void dropTable() {

    String query = "";
    if (databaseType == 1) {
      query = "DROP TABLE IF EXISTS `" + dd.database + "`.`" + dd.table + "`;";
    } else if (databaseType == 2) {
      if (isTableExist()) {
        query = "DROP TABLE " + dd.database + "." + dd.tableSchema + "." + dd.table + ";";
      }
    }

    updateSql(query);
  }

  /**
   * does column exist?
   * 
   * @param column
   * @return
   */
  private boolean getColumnExist(String column) {

    if (column == null) {
      return false;
    }

    if (column.length() == 0) {
      return false;
    }

    String query = "";
    if (databaseType == 1) {
      query = "SHOW COLUMNS FROM `" + dd.table + "` FROM `" + dd.database
          + "` LIKE '" + column + "';";
    } else if (databaseType == 2) {
      query = "SELECT COLUMN_NAME FROM " + dd.database
          + ".INFORMATION_SCHEMA.Columns " + "WHERE TABLE_SCHEMA='"
          + dd.tableSchema + "' AND " + "TABLE_NAME='" + dd.table + "' AND "
          + "COLUMN_NAME = '" + column + "';";
    }

    boolean rtn = getBooleanQueryFromString(query);

    return rtn;
  }

  /**
   * get columndata for columns using list of string array of column names
   * 
   * @param columns
   * @return
   */
  protected ColumnData[] getColumns(String[] columns) {
    ColumnData[] rtncolumns = new ColumnData[columns.length];
    for (int i=0; i < columns.length; i++) {
      rtncolumns[i] = new ColumnData();
      rtncolumns[i] = getColumn(columns[i]);
    }
    return rtncolumns;
  }
  
  /**
   * get columns name, type, length info
   * 
   * @return
   */
  protected ColumnData[] getColumns() {
    ColumnData[] columns = null;
    if (databaseType == 1) {
      columns = getColumns_Mysql();
    } else if (databaseType == 2) {
      columns = getColumns_MsSql();
    }
    return columns;
  }

  protected ColumnData getColumn(String column) {
    if (column == null) {
      return null;
    }

    if (column.length() == 0) {
      return null;
    }

    ColumnData[] columns = null;
    if (databaseType == 1) {
      columns = getColumn_Mysql(column);
    } else if (databaseType == 2) {
      columns = getColumn_MsSql(column);
    }
    
    if (columns == null) {
    	return null;
    }
    
    if (columns.length == 0) {
    	return null;
    }
    
    ColumnData col = columns[0];
    return col;
  }

  private ColumnData[] getColumns_Mysql() {
    return getColumn_Mysql(null);
  }

  /**
   * get column a column
   * 
   * @param column - if this is null, get all the columns
   * @return
   */
  private ColumnData[] getColumn_Mysql(String column) {

    String cquery = "";
    if (column != null) {
      if (column.length() > 0) {
        cquery = " FIELD LIKE '" + column + "' ";
      }
    }
    
    String doOnlyTextColumns  = "";
    if (dd.optimise_TextOnlyColumnTypes == true) {
      if (cquery.length() > 0) {
        doOnlyTextColumns += " AND ";
      }
      doOnlyTextColumns += " TYPE like 'Text%' ";
    }
    
    String where = "";
    if (cquery.length() > 0 | doOnlyTextColumns.length() > 0) {
      where = " WHERE " + cquery + doOnlyTextColumns;
    } 

    String query = "SHOW COLUMNS FROM `" + dd.table + "` " + 
      "FROM `" + dd.database + "` " + where + " ;";
    

    System.out.println("query: " + query);

    ColumnData[] columns = null;
    try {
      Connection conn = getConnection();
      Statement select = conn.createStatement();
      ResultSet result = select.executeQuery(query);

      columns = new ColumnData[getResultSetSize(result)];

      int i = 0;
      while (result.next()) {

        columns[i] = new ColumnData();
        columns[i].setName(result.getString(1));
        columns[i].setType(result.getString(2));

        i++;
      }
      result.close();
      select.close();
    } catch (SQLException e) {
      System.err.println("Mysql Statement Error:" + query);
      e.printStackTrace();
    }

    return columns;
  }
  
  private ColumnData[] getColumns_MsSql() {
    return getColumn_MsSql(null);
  }

  /**
   * get column from mssql server - works
   * 
   * @param column
   * @return
   */
  private ColumnData[] getColumn_MsSql(String column) {

    String cquery = "";
    if (column != null) {
      if (column.length() > 1) {
        cquery = " AND COLUMN_NAME='" + column + "' ";
      }
    }
    
    String doOnlyTextColumns  = "";
    if (dd.optimise_TextOnlyColumnTypes == true) {
      if (cquery.length() > 0) {
        // TODO
        //doOnlyTextColumns += " AND ";
      }
      // TODO 
      //doOnlyTextColumns += " Type like 'Text%' ";
    }

    String query = "SELECT COLUMN_NAME, Data_Type " + 
        "FROM INFORMATION_SCHEMA.Columns " + 
        "WHERE TABLE_NAME ='" + dd.table + "' AND " + 
        "TABLE_SCHEMA='" + dd.tableSchema + "' AND " +
        "TABLE_CATALOG='" + dd.database + "' " + cquery + doOnlyTextColumns + ";";

    // System.out.println("query: " + query);

    ArrayList<ColumnData> c = new ArrayList<ColumnData>();
    try {
      Connection conn = getConnection();
      Statement select = conn.createStatement();
      ResultSet result = select.executeQuery(query);
      while (result.next()) {
        ColumnData col = new ColumnData();
        col.setName(result.getString(1));
        col.setType(result.getString(2));

        c.add(col);
      }
      result.close();
      select.close();
    } catch (SQLException e) {
      System.err.println("Mysql Statement Error:" + query);
      e.printStackTrace();
    }

    ColumnData[] columns = new ColumnData[c.size()];
    for (int i = 0; i < c.size(); i++) {
      columns[i] = c.get(i);
    }

    return columns;
  }

  public ColumnData getPrimaryKey() {
    ColumnData columnData = null;
    if (databaseType == 1) {
      columnData = getPrimaryKey_MySql();
    } else if (databaseType == 2) {
      columnData = getPrimaryKey_MsSql();
    }
    return columnData;
  }
  
  public ColumnData getPrimaryKey(String table) {
    dd.table = table;
    ColumnData columnData = null;
    if (databaseType == 1) {
      columnData = getPrimaryKey_MySql();
    } else if (databaseType == 2) {
      columnData = getPrimaryKey_MsSql();
    }
    dd.table = null;
    return columnData;
  }

  private ColumnData getPrimaryKey_MySql() {
    
    String where = "`Key`='PRI'";
    
    String sql = "SHOW COLUMNS FROM `" + dd.table + "` " + 
      "FROM `" + dd.database + "` " + where + " ;";
  
    System.out.println("query: " + sql);
  
    ColumnData column = new ColumnData();
    try {
      Connection conn = getConnection();
      Statement select = conn.createStatement();
      ResultSet result = select.executeQuery(sql);
      while (result.next()) {
        column.setIsPrimaryKey(true);
        column.setColumnName(result.getString(1));
        column.setType(result.getString(2));
      }
      result.close();
      select.close();
    } catch (SQLException e) {
      System.err.println("Mysql Statement Error:" + sql);
      e.printStackTrace();
    }

    return column;
  }
  
  /**
   * get the primary key column
   * 
   * @param databaseData
   * @param table
   * @return
   */
  protected ColumnData getPrimaryKey_MySql_v2(DatabaseData databaseData, String table) {
    
    String where = "WHERE `Key`='PRI'";
    
    String sql = "SHOW COLUMNS FROM " + table + " " + 
      "FROM `" + databaseData.getDatabase() + "` " + where + " ;";
  
    //System.out.println("query: " + sql);
  
    ColumnData column = new ColumnData();
    try {
      Connection conn = databaseData.getConnection();
      Statement select = conn.createStatement();
      ResultSet result = select.executeQuery(sql);
      while (result.next()) {
        column.setIsPrimaryKey(true);
        column.setColumnName(result.getString(1));
        column.setType(result.getString(2));
      }
      result.close();
      select.close();
    } catch (SQLException e) {
      System.err.println("Mysql Statement Error:" + sql);
      e.printStackTrace();
    }
    
    return column;
  }
  
  private ColumnData getPrimaryKey_MsSql() {
    // TODO
    return null;
  }
  
  
  /**
   * create columns
   * 
   * @param columns
   * @return
   */
  protected void createColumns(ColumnData[] columns) {
    columns = fixColumns(columns);

    ColumnData[] cols = new ColumnData[columns.length];
    for (int i = 0; i < columns.length; i++) {
      createColumn(columns[i].getName());

      cols[i] = new ColumnData();
      cols[i] = getColumn(columns[i].getName());
      
      if (dd.stopAtColumnCount > 1 && dd.stopAtColumnCount == i) {
        break;
      }
    }

    this.columns = cols;
  }

  /**
   * Create a column always starts with text as the default
   * 
   * Two reasons to start with text. 1. fields can have length > 255 2. 65535
   * length limit of sql insert NOTE: have to alter to varchar/text first then
   * drill down further after that
   * 
   * @param column
   */
  public void createColumn(String column) {
    String type = "";
    if (databaseType == 1) {
      type = "TEXT DEFAULT NULL";
    } else if (databaseType == 2) {
      type = "TEXT NULL"; // VARCHAR(255) // TODO -aggregate functions don't
                          // work with this. Need to alter to text with lenths
                          // greather than text
    }
    createColumn(column, type);
  }

  /**
   * replace column names, fix column names with sql friendly version
   * 
   * @param columns
   * @return
   */
  protected ColumnData[] fixColumns(ColumnData[] columns) {

    ArrayList<ColumnData> aColumns = new ArrayList<ColumnData>();
    for (int i = 0; i < columns.length; i++) {

      if (columns[i] == null) {
        columns[i] = new ColumnData();
        columns[i].setName("c" + i);
      } else if (columns[i].getName().length() == 0
          || columns[i].getName().matches("[\040]*")) {
        columns[i].setName("c" + i);
      }

      String column = columns[i].getName();
      column = replaceToMatchingColumn(column); // replace with matching column
                                                // name
      column = fixName(column); // fix column name if need be, make it sql
                                // friendly
      columns[i].setName(column);
      aColumns.add(columns[i]);
    }

    // change to object
    ColumnData[] cols = new ColumnData[aColumns.size()];
    for (int i = 0; i < aColumns.size(); i++) {
      cols[i] = aColumns.get(i);
    }

    return columns;
  }

  /**
   * create a column
   * 
   * @param column
   * @param type [TEXT, VARCHAR(255)]
   */
  public void createColumn(String column, String type) {

    if (column == null) {
      return;
    }

    if (column.length() == 0) {
      return;
    }

    // fix column name to ok for mysql
    column = fixName(column);

    boolean exist = getColumnExist(column);
    if (exist == true) {
      return;
    }

    // default column type for creation
    if (type == null) {
      if (databaseType == 1) { // mysql
        type = "TEXT";
      } else if (databaseType == 2) {
        type = "VARHCAR(255)"; // mssql TODO figure out how to alter in mssql, cast?
      }
    }

    String query = "";
    if (databaseType == 1) {
      query = "ALTER TABLE `" + dd.database + "`.`" + dd.table
          + "` ADD COLUMN `" + column + "`  " + type + ";";
    } else if (databaseType == 2) {
      query = "ALTER TABLE " + dd.database + "." + dd.tableSchema + "."
          + dd.table + " ADD [" + column + "] " + type + ";";
    }

    updateSql(query);
  }
  
  /**
   * create a columns
   * TODO need more flexibility with lengths
   * 
   * @param column
   * @param type
   */
  private void createColumn(String column, int type) {

    String t = "";
//    if (type == ColumnData.FIELDTYPE_TEXT) {
//      t = "TEXT DEFAULT NULL";
//    } else if (type == ColumnData.FIELDTYPE_VARCHAR) {
//      t = "VARCHAR(255) DEFAULT NULL";
//    } else if (type == ColumnData.FIELDTYPE_INT) {
//      
//    } else if (type == ColumnData.FIELDTYPE_DECIMAL) {
//      
//    } else if (type == ColumnData.FIELDTYPE_DATETIME) {
//      
//    } else {
//      t = "TEXT DEFAULT NULL";
//    }
    
    createColumn(column, t);
  }

  /**
   * replace the field name with another field name that was listed
   * 
   * @param column
   * @return
   */
  private String replaceToMatchingColumn(String column) {

    if (matchFields == null) {
      return column;
    }

    Comparator<FieldData> searchByComparator = new FieldDataComparator();

    FieldData searchFor = new FieldData();
    searchFor.sourceField = column;

    int index = Arrays.binarySearch(matchFields, searchFor, searchByComparator);

    if (index >= 0) {
      column = matchFields[index].destinationField;
    }

    return column;
  }

  /**
   * get a mysql database connection
   * 
   * @return
   */
  private Connection getConn_MySql() {

    String url = "jdbc:mysql://" + dd.host + ":" + dd.port + "/";
    String driver = "com.mysql.jdbc.Driver";
    System.out.println("getConn_MySql: url:" + url + " user: " + dd.username + " driver: " + driver);

    Connection conn = null;
    try {
      Class.forName(driver).newInstance();
      conn = DriverManager.getConnection(url + dd.database, dd.username,
          dd.password);
    } catch (Exception e) {
      System.err.println("MySql Connection Error:");
      e.printStackTrace();
      System.out.println("Fix Connection.");
      System.exit(1);
    }

    return conn;
  }

  /**
   * go back in forth over a connection to load balance over more than one cpu
   * 
   * @return
   */
  protected Connection getConnection() {

    if (conn1 == null && dd != null) {
      openConnection();
    }
    
    if (conn2 == null && dd != null) {
      openConnection();
    }

    Connection c = null;
    if (connLoadBalance == 0) {
      connLoadBalance = 1;
      c = conn1;
    } else {
      connLoadBalance = 0;
      c = conn2;
    }
    return c;
  }

  /**
   * get ms sql connection
   * 
   * // Note: this class name changes for ms sql server 2000 thats it // It has
   * to match the JDBC library that goes with ms sql 2000
   * 
   * @return
   */
  private Connection getConn_MsSql() {

    String url = "jdbc:sqlserver://" + dd.host + ";user=" + dd.username
        + ";password=" + dd.password + ";databaseName=" + dd.database + ";";
    String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    System.out.println("getConn_MsSql: url:" + url + " user: " + dd.username
        + " driver: " + driver);

    Connection conn = null;
    try {
      Class.forName(driver);
      conn = DriverManager.getConnection(url);
    } catch (Exception e) {
      System.err.println("MsSql Connection Error: " + e.getMessage());
      e.printStackTrace();
      System.exit(1);
    }
    return conn;
  }

  /**
   * close sql connection
   * 
   * @param conn
   */
  protected void closeConnection(Connection conn) {
    if (conn != null) {
      try {
        conn.close();
      } catch (SQLException e) {
        System.err.println("MsSql Connection Closing Error: " + e.getMessage());
        e.printStackTrace();
      }
    }
  }

  /**
   * update query
   * 
   * TODO - how to handle a truncation error, and for which column it happened on
   * 
   * @param query
   */
   public void updateSql(String query) {

    if (query == null) {
      System.out.println("no query given");
      return;
    }

    // for debugging
    if (index >= 0 && index <= 10) {
    	System.out.println("f:" + indexFile + ": row:" + index + ". " + query); 
    } else {
    	System.out.println("f:" + indexFile + ": row:" + index + ". " + query.substring(0,50)); 
    }
    
    //System.out.println("f:" + indexFile + ": row:" + index + ". " + query); 
    
    try {
      Connection conn = getConnection();
      Statement update = conn.createStatement();
      update.executeUpdate(query);
      update.close();
    } catch (SQLException e) {
      dealWithSqlError(e, query);
    }
  }
  
  protected void updateSql_v2(DatabaseData databaseData, String sql) {

     if (sql == null) {
       System.out.println("no query given");
       return;
     }

     System.out.println("f:" + indexFile + ": row:" + index + ". " + sql); 
     
     try {
       Connection conn = databaseData.getConnection();
       Statement update = conn.createStatement();
       update.executeUpdate(sql);
       update.close();
     } catch (SQLException e) {
       setDatabaseData(databaseData);
       openConnection();
       dealWithSqlError(e, sql);
       dd.table = null;
       closeConnection();
     }
   }
   
  /**
   * deal with errors, auto fix them, like truncation
   * 
   * @param e
   * @param query
   */
  private void dealWithSqlError(SQLException e, String query) {
    
    if (dealwithTruncationError(e, query) == true) {
      
    } else {
      System.err.println("Mysql Statement Error: " + query);
      e.printStackTrace();
      System.out.println("");
    }

  }
  
  private boolean dealwithTruncationError(SQLException e, String query) {
    
    // mysql truncation error -> Data truncation: Data too long for column 'Period' at row 1
    
    String msg = e.getMessage();
    
    // TODO - datetime date 01/01/2009 being put into a datetime column
    if (msg.contains("too long") == false) { // || msg.contains("truncation") == false happens whith datetime stuffing
      return false;
    } 
    
    // get column
    String column = getTruncationColumn(msg);
    if (column == null) {
      return false;
    }
    
    // get value length needed
    int length = getTruncationValueLength(column, query);
    if (length == 0) {
      return false;
    }
    
    // get column type
    ColumnData cd = getColumn(column);
    String columnType = null;// cd.columnType;
    
    resizeColumnLength(column, columnType, length);
    
    // TODO - deal with recursion error here
    updateSql(query);
    
    return true;
  }
  
  private String getTruncationColumn(String s) {
    
    String re = "\'(.*?)\'";
    Pattern p = Pattern.compile(re);
    Matcher m = p.matcher(s);
    boolean found = m.find();
    
    String f = null;
    if (found == true) {
      f = m.group(1);
    } 
    
    return f;
  }
  
  /**
   * find values length, this works only in mysql UPDATE SET or INSERT SET 
   * 
   *  TODO mysql (VALUES)
   *  TODO mssql trucation msg
   * 
   * @param column
   * @param query
   * @return
   */
  private int getTruncationValueLength(String column, String query) {
    
    // UPDATE db.table SET `column`='01/01/2008' WHERE ImportId='1';
    
    String re = "SET.*?`" + column + "`?.*?=.*?\'(.*?)\'";
    Pattern p = Pattern.compile(re);
    Matcher m = p.matcher(query);
    boolean found = m.find();
    
    String f = "";
    if (found == true) {
      f = m.group(1);
    } 
    
    int i = f.length();
    
    return i;
  }

  /**
   * does a value exist? return boolean
   * 
   * @param query
   * @return
   */
  protected boolean getBooleanQueryFromString(String query) {

    String value = null;
    try {
      Connection conn = getConnection();
      Statement select = conn.createStatement();
      ResultSet result = select.executeQuery(query);
      while (result.next()) {
        value = result.getString(1);
      }
      select.close();
      result.close();
    } catch (SQLException e) {
      System.err.println("Mysql Statement Error:" + query);
      e.printStackTrace();
    }

    boolean rtn = false;
    if (value != null) {
      rtn = true;
    }

    return rtn;
  }

  protected int getQueryInt(String sql) {

    int i = 0;
    try {
      Connection conn = getConnection();
      Statement select = conn.createStatement();
      ResultSet result = select.executeQuery(sql);
      while (result.next()) {
        i = result.getInt(1);
      }
      select.close();
      result.close();
    } catch (SQLException e) {
      System.err.println("Mysql Statement Error:" + sql);
      e.printStackTrace();
    }

    return i;
  }
  
  /**
   * get an Integer value from sql query
   * 
   * @param databaseData
   * @param sql
   * @return
   */
  protected int getQueryInt_v2(DatabaseData databaseData, String sql) {

    int i = 0;
    try {
      Connection conn = databaseData.getConnection();
      Statement select = conn.createStatement();
      ResultSet result = select.executeQuery(sql);
      while (result.next()) {
        i = result.getInt(1);
      }
      select.close();
      result.close();
    } catch (SQLException e) {
      System.err.println("Mysql Statement Error:" + sql);
      e.printStackTrace();
    }

    return i;
  }

  // TODO this is like query int
  protected int getQueryIdent(String query) {
    int i = 0;
    try {
      Connection conn = getConnection();
      Statement select = conn.createStatement();
      ResultSet result = select.executeQuery(query);
      while (result.next()) {
        i = result.getInt(1);
      }
      select.close();
      result.close();
    } catch (SQLException e) {
      System.err.println("Mysql Statement Error:" + query);
      e.printStackTrace();
    }
    return i;
  }

  /**
   * fix the name to be sql friendly
   * 
   * 
   * mysql table, and columns should be < 64 char
   * 
   * @param s
   * @return
   */
  private String fixName(String s) {

    if (s.length() > 64) {
      s = s.substring(0, 63);
    }
    s = s.trim();

    s = s.replace("'", "");
    s = s.replace("[\"\r\n\t]", "");
    s = s.replace("!", "");
    s = s.replace("@", "");
    s = s.replace("#", "_Num");
    s = s.replace("$", "");
    s = s.replace("^", "");
    s = s.replace("\\*", "");
    s = s.replace("\\", "");
    s = s.replace("\\+", "");
    s = s.replace("=", "");
    s = s.replace("~", "");
    s = s.replace("`", "");
    s = s.replace("\\{", "");
    s = s.replace("\\}", "");
    s = s.replace("\\[", "");
    s = s.replace("\\]", "");
    s = s.replace("\\|", "");
    s = s.replace(".", "_");
    s = s.replace(",", "");
    s = s.replace("\\.", "");
    s = s.replace("<", "");
    s = s.replace(">", "");
    s = s.replace("?", "");
    s = s.replace("&", "");
    s = s.replace("/", "");
    s = s.replace("%", "_per");
    s = s.replace(" ", "_");
    s = s.replace("(", "");
    s = s.replace(")", "");
    s = s.replaceAll("(\\W)", "");
    //s = s.replaceAll("(\\-)", "");

    return s;
  }

  /**
   * get row count
   * 
   * @param result
   * @return
   */
  protected int getResultSetSize(ResultSet result) {
    int size = -1;
    try {
      result.last();
      size = result.getRow();
      result.beforeFirst();
    } catch (SQLException e) {
      return size;
    }
    return size;
  }

  /**
   * add data to table
   * 
   * @param columns
   * @param values
   */
  public void addData(int indexFile, int index, String[] values) {
    this.index = index;
    this.indexFile = indexFile;

    // does record already exist?
    int id = 0;
    if (dd.checkForExistingRecordsAndUpdate == true
        && dd.identityColumns != null) {
      id = getRecordExist(columns, values);
    }

    // check to see if the lengths of the columns/fields will fit
    try {
      doDataLengthsfit(values);
    } catch (Exception e) {
      e.printStackTrace();
    }

    String query = null;
    if (databaseType == 1) {

      if (id > 0) {
        query = getQuery_Update_MySql(columns, values, id);
      } else {
        query = getQuery_Insert_MySql(columns, values);
      }

    } else if (databaseType == 2) {

      if (id > 0) {
        query = getQuery_Update_MsSql(columns, values, id);
      } else {
        query = getQuery_Insert_MsSql(columns, values);
      }

    }

    if (query != null) {
      updateSql(query);
    }

  }

  private String getQuery_Insert_MsSql(ColumnData[] columns, String[] values) {

    String cs = "";
    String vs = "";
    for (int i = 0; i < columns.length; i++) {

      String c = columns[i].getName();
      if (c.length() > 0) {
        cs += "[" + c + "]";
        try {
          vs += "'" + escapeForSql(values[i]) + "'";
        } catch (Exception e) {
          vs += "''";
        }

        if (i < columns.length - 1) {
          cs += ", ";
          vs += ", ";
        }
      }
    }

    cs = fixcomma(cs);
    vs = fixcomma(vs);

    String s = "INSERT INTO " + dd.database + "." + dd.tableSchema + "."
        + dd.table + " (DateCreated, " + cs + ") " + "VALUES (GETDATE(), " + vs
        + ");";

    return s;
  }

  private String getQuery_Insert_MySql(ColumnData[] columns, String[] values) {

    String q = "";
    for (int i = 0; i < columns.length; i++) {

      String c = "";
      String v = "";

      c = columns[i].getName();

      try {
        v = values[i];
      } catch (Exception e) {
        v = "";
      }

      v = escapeForSql(v);

      if (c.length() > 0) {
        q += "`" + c + "`='" + v + "'";

        if (i < columns.length - 1) {
          q += ", ";
        }
      }
    }

    q = fixcomma(q);

    String s = "INSERT INTO `" + dd.database + "`.`" + dd.table + "` "
        + "SET DateCreated=NOW(), " + q + ";";

    return s;
  }

  private String fixcomma(String s) {
    s = s.trim();
    if (s.matches(".*[,]")) {
      s = s.substring(0, s.length() - 1);
    }
    return s;
  }

  /**
   * create indexes of the identities given
   * 
   * mysql index can't be over a 1000 bytes
   * Text index has to have a number set
   * 
   * NOTE: setting default  to varchar(50)
   */
  private void createIndex_forIdentities() {

    if (dd.identityColumns == null) {
      System.out.println("skipping creating indexes, b/c there are no identiy columns listed");
      return;
    }

    String indexes = "";
    for (int i = 0; i < dd.identityColumns.length; i++) {
      
      String column = dd.identityColumns[i].destinationField;
      
      // force a columnType if need be
      String columnType = null;
      if (dd.identityColumns[i].destinationField_ColumnType == null) {
        columnType = "VARCHAR(50) DEFAULT NULL";
      } else {
        columnType = dd.identityColumns[i].destinationField_ColumnType;
      }
      createColumn(column, columnType);

      if (databaseType == 1) {
        indexes += "`" + column + "`";
        
      } else if (databaseType == 2) {
        indexes += "[" + column + "]";
      }

      if (i < dd.identityColumns.length - 1) {
        indexes += ", ";
      }
    }
    
    // created the index
    String indexName = "index_auto";
    createIndex(indexName, indexes, Optimise_V1.INDEXKIND_DEFAULT);
  }

  /**
   * create index for identities given
   * 
   * NOTE: skip the primary ImportID 
   * 
   * @param columns - can be multiple columns
   */
  protected void createIndex(String indexName, String columns, int indexKind) {

    // TODO not sure of actual length limitation for indexName
    if (indexName.length() > 30) {
      indexName = indexName.substring(0,30);
    }
    
    if (doesIndexExist(indexName) == true) {
      return;
    }
    
    // TODO - do for MS too
    String kind = "";
    if (indexKind == Optimise_V1.INDEXKIND_FULLTEXT) {
      kind = "FULLTEXT";
    }
    
    // TODO if the column is a text column, set the index length and its not full text

    String query = "";
    if (databaseType == 1) {
      query = "ALTER TABLE `" + dd.database + "`.`" + dd.table + "` "
          + "ADD " + kind + " INDEX `" + indexName + "`(" + columns + ");";
      
    } else if (databaseType == 2) {
      query = "CREATE INDEX [" + indexName + "] ON " + dd.database + "."
          + dd.tableSchema + "." + dd.table + " (" + columns + ") ;";
    }

    updateSql(query);
  }

  /**
   * does an index exist
   * 
   * @param c
   * @return
   */
  private boolean doesIndexExist(String c) {

    String sql = "";
    if (databaseType == 1) {
      sql = "SHOW INDEX FROM `" + dd.table + "` FROM `" + dd.database + "` "
          + "WHERE (Key_name != 'Primary') AND (Key_name = '" + c + "')";
    } else if (databaseType == 2) {
      sql = "";
    }
    System.out.println("sql: " + sql);
    return getBooleanQueryFromString(sql);
  }
  
  /**
   * delete indexes for this column, (so that one can resize column)
   * 
   * @param c
   */
  protected void deleteIndexsForColumn(String c) {
    
    String sql = "";
    if (databaseType == 1) {
      sql = "SHOW INDEX FROM `" + dd.table + "` FROM `" + dd.database + "` "
      + "WHERE (Key_name != 'Primary') AND (Key_name = '" + c + "')";
      
    } else if (databaseType == 2) {
      sql = "";
    }

    try {
      Connection conn = getConnection();
      Statement select = conn.createStatement();
      ResultSet result = select.executeQuery(sql);
      while (result.next()) {
        deleteIndex(result.getString(3));
      }
      select.close();
      result.close();
    } catch (SQLException e) {
      System.err.println("Mysql Statement Error:" + sql);
      e.printStackTrace();
    }
  }

  /**
   * delete all indexes except primary
   */
  protected void deleteAllIndexs() {

    String sql = "";
    if (databaseType == 1) {
      sql = "SHOW INDEX FROM `" + dd.table + "` FROM `" + dd.database + "` " + 
        "WHERE Key_name != 'Primary'";
    } else if (databaseType == 2) {
      sql = "";
    }
    
    System.out.println("sql: " + sql);
    
    try {
      Connection conn = getConnection();
      Statement select = conn.createStatement();
      ResultSet result = select.executeQuery(sql);
      
      while (result.next()) {
        // TODO - Key_name is 3 for mysql , don't know for Mssql yet
        String index = result.getString(3);
        deleteIndex(index);
      }
      select.close();
      result.close();
    } catch (SQLException e) {
      System.err.println("Mysql Statement Error:" + sql);
      e.printStackTrace();
    }
    

  }

  /**
   * delete one index
   */
  private void deleteIndex(String index) {

    String sql = "DROP INDEX " + index + " ON `" + dd.table + "`;";
    updateSql(sql);
  }

  /**
   * get where query
   * 
   * TODO - deal with what happens when these columns do not exist
   * 
   * @param columns
   * @param values
   * @return
   */
  private String getIdentiesWhereQuery(ColumnData[] columns, String[] values) {

    IdentityData[] identityData = findIdentityData(columns, values);

    if (identityData.length == 0) {
      return "";
    }

    String q = "";
    for (int i = 0; i < identityData.length; i++) {

      String c = identityData[i].column;
      String v = escapeForSql(identityData[i].value);

      if (databaseType == 1) {
        q += "(`" + c + "`='" + v + "')";
      } else if (databaseType == 2) {
        q += "([" + c + "]='" + v + "')";
      }

      if (i < identityData.length - 1) {
        q += " AND ";
      }

    }

    return q;
  }

  /**
   * find identity columns
   * 
   * @param columns
   * @return
   */
  private IdentityData[] findIdentityData(ColumnData[] columns, String[] values) {

    ArrayList<IdentityData> ident = new ArrayList<IdentityData>();
    for (int i = 0; i < dd.identityColumns.length; i++) {

      String column = dd.identityColumns[i].destinationField;

      int index = searchForColumn(columns, column);
      if (index >= 0) {
        IdentityData id = new IdentityData();
        id.column = column;

        String value = "";
        try {
          value = values[index];
        } catch (Exception e) {
        }

        id.value = value;
        ident.add(id);
      }

    }

    IdentityData[] idents = new IdentityData[ident.size()];
    for (int i = 0; i < ident.size(); i++) {
      idents[i] = new IdentityData();
      idents[i] = ident.get(i);
    }

    return idents;
  }
  
  protected String getIdentitiesColumns_inCsv() {
    
    if (dd.identityColumns == null) {
      return "";
    }
    
    String columns = "";
    
    for (int i = 0; i < dd.identityColumns.length; i++) {

      columns += dd.identityColumns[i].destinationField;

      if (i < dd.identityColumns.length - 1) {
        columns += ",";
      }
    }
    
    return columns;
  }

  private int searchForColumn(ColumnData[] columns, String key) {

    int index = -1;
    for (int i = 0; i < columns.length; i++) {

      if (columns[i].getName().equals(key)) {
        index = i;
        break;
      }
    }
    return index;
  }

  private int getRecordExist(ColumnData[] columns, String[] values) {

    int id = 0;
    if (databaseType == 1) {
      id = getRecordExist_MySql(columns, values);
    } else if (databaseType == 2) {
      id = getRecordExist_MsSql(columns, values);
    }

    return id;
  }

  /**
   * does the row already exist in mysql?
   * 
   * TODO - confirm that this works in all cases - need limit 0,1
   * 
   * @param columns
   * @param values
   * @return
   */
  private int getRecordExist_MySql(ColumnData[] columns, String[] values) {

    // get idents
    String whereQuery = getIdentiesWhereQuery(columns, values);

    if (whereQuery.length() == 0) {
      return -1;
    }

    String query = "SELECT ImportId FROM `" + dd.database + "`.`" + dd.table
        + "` " + "WHERE " + whereQuery + " LIMIT 0,1";

    System.out.println("Exist?: " + query);
    
    int id = getQueryIdent(query);

    return id;
  }

  private int getRecordExist_MsSql(ColumnData[] columns, String[] values) {

    // get idents
    String whereQuery = getIdentiesWhereQuery(columns, values);

    if (whereQuery.length() == 0) {
      return -1;
    }

    String query = "SELECT TOP 1 ImportId FROM " + dd.database + "."
        + dd.tableSchema + "." + dd.table + " " + "WHERE " + whereQuery;

    int id = getQueryIdent(query);

    return id;
  }

  private String getQuery_Update_MySql(ColumnData[] columns, String[] values,
      int id) {

    String q = "";
    for (int i = 0; i < columns.length; i++) {

      String c = "";
      String v = "";

      c = columns[i].getName();

      try {
        v = values[i];
      } catch (Exception e) {
        v = "";
      }

      v = escapeForSql(v);

      if (c.length() > 0) {
        q += "`" + c + "`='" + v + "'";

        if (i < columns.length - 1) {
          q += ", ";
        }
      }
    }

    q = fixcomma(q);

    String s = "UPDATE `" + dd.database + "`.`" + dd.table + "` "
        + "SET DateUpdated=NOW(), " + q + " " + "WHERE (ImportID='" + id
        + "');";

    return s;
  }

  private String getQuery_Update_MsSql(ColumnData[] columns, String[] values,
      int id) {

    String q = "";
    for (int i = 0; i < columns.length; i++) {

      String c = "";
      String v = "";

      c = columns[i].getName();

      try {
        v = values[i];
      } catch (Exception e) {
        v = "";
      }

      v = escapeForSql(v);

      if (c.length() > 0) {
        q += "[" + c + "]='" + v + "'";
        if (i < columns.length - 1) {
          q += ", ";
        }
      }
    }

    q = fixcomma(q);

    String s = "UPDATE " + dd.database + "." + dd.tableSchema + "." + dd.table
        + " " + "SET DateUpdated=GETDATE(), " + q + " " + "WHERE (ImportID='"
        + id + "');";

    return s;
  }

  /**
   * escape string to db
   * 
   * remove harmfull db content remove harmfull tags
   * 
   * @param s
   * @return
   */
  protected static String escapeForSql(String s) {
    s = StringUtil.escapeSql(s);
    // escape utils returns null if null
    if (s == null) {
      s = "";
    }
    s = s.replace("\\", "\\\\");
    s = s.trim();
    return s;
  }

  /**
   * delete empty columns - any column with no values (all null | '')
   */
  public void deleteEmptyColumns() {

    if (dd.deleteEmptyColumns == false) {
      System.out
          .println("Skipping deleting empty Columns: destinationData.deleteEmptyColumns=false");
      return;
    }

    System.out.println("Going to delete empty columns: ");

    // loop through columns and see if they are empty
    ColumnData[] columns = getColumns();
    int i2 = columns.length - 1;
    for (int i = 0; i < columns.length; i++) {

      System.out.println(i2 + ". checking column is Empty?: "
          + columns[i].getName() + " for data.");
      if (getColumnHaveStuff(columns[i].getName()) == false) {
        deleteColumn(columns[i].getName());
      }

      // count down
      i2--;
    }
  }

  private boolean getColumnHaveStuff(String column) {

    String query = "";
    if (databaseType == 1) {
      query = "SELECT COUNT(`" + column + "`) AS Total FROM `" + dd.database
          + "`.`" + dd.table + "` " + "WHERE (`" + column + "` != '');";
    } else if (databaseType == 2) {
      query = "SELECT COUNT(*) AS Total FROM " + dd.database + "."
          + dd.tableSchema + "." + dd.table + " " + "WHERE  ([" + column
          + "] IS NOT NULL);"; // TODO - confirm not null... stinking can't use
                               // aggregate on text types...
    }

    int i = getQueryInt(query);

    boolean b = true;
    if (i == 0) {
      b = false;
    }

    return b;
  }

  private void deleteColumn(String column) {

    if (dd.deleteEmptyColumns == false) {
      return;
    }

    // do not delete indexed columns
    if (getColumnIndexed(column) == true) {
      System.out.println("Can't delete this column, its indexed");
      return;
    }

    String query = "";
    if (databaseType == 1) {
      query = "ALTER TABLE `" + dd.database + "`.`" + dd.table + "` "
          + "DROP COLUMN `" + column + "`;";
    } else if (databaseType == 2) {
      query = "ALTER TABLE " + dd.database + "." + dd.tableSchema + "."
          + dd.table + " " + "DROP COLUMN " + column + ";";
    }

    updateSql(query);
  }

  private boolean getColumnIndexed(String column) {

    if (column == null) {
      return false;
    }

    // don't delete this column
    if (column.equals("DateCreated") | column.equals("DateUpdated")) {
      return true;
    }

    Comparator<FieldData> searchByComparator = new SortDestinationField();
    FieldData searchFor = new FieldData();
    searchFor.destinationField = column;

    int index = -1;
    if (matchFields != null) {
      index = Arrays.binarySearch(matchFields, searchFor, searchByComparator);
    }

    boolean rtn = false;
    if (index > 0) {
      rtn = true;
    }

    return rtn;
  }

  /**
   * will the data fit into the columns if not resize them
   * 
   * @param columns
   * @param values
   */
  private void doDataLengthsfit(String[] values) {

    if (values == null) {
      return;
    }

    int resize = 0;
    for (int i = 0; i < columns.length; i++) {

      String value = "";
      try {
        value = values[i];
      } catch (Exception e) {
      }
      resize = columns[i].testSizeOfValue(value);
      if (resize > 0) {
        String type = null; //resizeColumnLength(columns[i].getName(), columns[i].columnType, resize);
        columns[i].setType(type);
      }
    }
  }

  private String resizeColumnLength(String column, String columnType, int length) {

    if (length == 0) {
      return "";
    }

    Optimise_V1 optimise = new Optimise_V1();
    optimise.setDestinationData(dd);
    optimise.setMatchFields(matchFields);
    optimise.openConnection();
    String r = optimise.resizeColumn(column, columnType, length);
    optimise.closeConnection();
    return r;
  }

  /**
   * get row data into array
   * 
   * @param columns
   * @param result
   * @return
   */
  private String[] getRowData(String[] columns, ResultSet result) {
    String[] rtn = new String[columns.length];
    for (int i = 0; i < columns.length; i++) {
      try {
        rtn[i] = result.getString(i + 1);
      } catch (SQLException e) {
        System.err.println("Error in getRowData");
        e.printStackTrace();
      }
    }
    return rtn;
  }

  /**
   * Create several reverse columns
   * 
   * @param columns
   */
  protected ColumnData[] createReverseColumns(ColumnData[] columns) {
    
    ColumnData[] rtn = new ColumnData[columns.length];
    for (int i=0; i < columns.length; i++) {
      rtn[i] = new ColumnData();
      rtn[i] = createReversedColumn(columns[i]);
    }
    
    return rtn;
  }
  
  /**
   * create a column in reverse of its source
   * 
   * @param srcColumn - column to reverse
   */
  private ColumnData createReversedColumn(ColumnData c) {
    
    String srcColumn = c.getName();
    String dstColumn = c.getName() + "__Reverse";
    String type = null; // c.columnType;
    createColumn(dstColumn, type);
    
    // copy the data
    String sql = "";
    if (databaseType == 1) {
      sql = "UPDATE " + dd.table + " " +
        "SET " + dstColumn + "=REVERSE(" + srcColumn + ") " +
        "WHERE (" + dstColumn + "='');"; // TODO - (dstColumn IS NOT NULL);
    } else if (databaseType == 2) {
      // TODO finish later
      sql = "";
    }
    
    System.out.println("copying reverse: " + sql);
    
    updateSql(sql);
    
    System.out.println("finished with reverse copy");
    
    // send back reverse column name for indexing
    ColumnData rtn = c;
    rtn.setName(dstColumn);
    return rtn;
  }
  
  /**
   * does table has duplicates compared to the identity data
   *  
   * @return how many duplicates
   */
  protected int getTableHasDuplicates() {
    
    // get total record count for table
    int tc = getTableRecordCount();
    
    // check distinct count for identities
    int tdc = getTableDistinctIdentCount();
    
    int r = tc - tdc;
    
    return r;
  }

  private int getTableRecordCount() {
    String sql = "";
    if (databaseType == 1) {
      sql = "SELECT COUNT(*) AS t FROM " + dd.database + "." + dd.table + ";"; 
    } else if (databaseType == 2) {
      // TODO
      sql = "";
    }
    return getQueryInt(sql);
  }
  
  private int getTableDistinctIdentCount() {
    
    if (dd.identityColumns == null) {
      return 0;
    }
    
    // get ident columns
    String idents_Columns = getIdentitiesColumns_inCsv();
    
    String sql = "";
    if (databaseType == 1) {
      sql = "SELECT DISTINCT " + idents_Columns + " FROM " + dd.database + "." + dd.table + ";"; 
    } else if (databaseType == 2) {
      // TODO
      sql = "";
    }

    int c = 0;
    try {
      Connection conn = getConnection();
      Statement select = conn.createStatement();
      ResultSet result = select.executeQuery(sql);
      
      if (databaseType == 1) {
        c = getResultSetSize(result);
      } else if (databaseType == 2) {
        while (result.next()) {
          c++;
        }
      }
      select.close();
      result.close();
    } catch (SQLException e) {
      System.err.println("Mysql Statement Error:" + sql);
      e.printStackTrace();
    }
    
    return c;
  }
  
}// end class

