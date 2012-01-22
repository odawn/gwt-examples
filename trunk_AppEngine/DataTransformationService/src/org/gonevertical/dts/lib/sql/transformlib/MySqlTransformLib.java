package org.gonevertical.dts.lib.sql.transformlib;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.log4j.Priority;
import org.gonevertical.dts.data.ColumnData;
import org.gonevertical.dts.data.DatabaseData;
import org.gonevertical.dts.data.StatData;
import org.gonevertical.dts.data.UserDbData;
import org.gonevertical.dts.data.ColumnData.Type;
import org.gonevertical.dts.lib.StringUtil;
import org.gonevertical.dts.lib.sql.columnlib.MySqlColumnLib;
import org.gonevertical.dts.lib.sql.querylib.MySqlQueryLib;


public class MySqlTransformLib implements TransformLib {

  private static final Logger log = LoggerFactory.getLogger(MySqlTransformLib.class);
	
	/**
	 * mysql query library
	 */
  private MySqlQueryLib ql = new MySqlQueryLib();
  
  /**
   * mysql column library
   */
  private MySqlColumnLib cl = new MySqlColumnLib();
  
  /**
   * stats tracing
   */
	private StatData stats;
  
	/**
	 * constructor
	 */
  public MySqlTransformLib() {
  }
  
  public void setStats(StatData stats) {
  	this.stats = stats;
  	ql.setStats(stats);
  }

  private void setTrackSql(String sql) {
  	if (stats == null) {
  		return;
  	}
  	stats.setTrackSql(sql);
  }

  private void setTrackError(String error) {
  	if (stats == null) {
  		return;
  	}
  	stats.setTrackError(error);
  }

  /**
   * fix table name
   * 
   * @param table
   * @return
   */
  public String fixTableName(String table) {

    // max table length name
    if (table.length() > 64) {
      table = table.substring(0, 63);
    }
    table = table.trim();

    table = table.replaceAll("[#]", "_num");
    table = table.replaceAll("[%]", "_per");
    table = table.replaceAll("[.]", "_");
    table = table.replaceAll("[\040]", "_");
    
    table = table.replaceAll("[^\\w]", "");
    table = table.replaceAll("[\r\n\t]", "");
    table = table.replaceAll("(\\W)", "");

    return table;
  }
  
  /**
   * simple create table to work with
   * 
   * TODO - what if the schema does not exist, create it?
   * 
   * @param dd
   * @param table
   * @param primaryKeyName
   */
  public void createTable(DatabaseData dd, String table, String primaryKeyName) {
    if (table == null | primaryKeyName == null) {
      return;
    }
    if (table.length() == 0 | primaryKeyName.length() == 0) {
      return;
    }
    boolean doesExist = doesTableExist(dd, table);
    if (doesExist == true) {
      return;
    }
    String sql = "CREATE TABLE `" + dd.getDatabase() + "`.`" + table + "` " +
    	"(" + 
        "`" + primaryKeyName + "` BIGINT NOT NULL AUTO_INCREMENT, PRIMARY KEY (`" + primaryKeyName + "`) " + 
      ") ENGINE = MyISAM;";
    
    log.info("createTable: " + sql);
    
    ql.update(dd, sql);
  }
  
  /**
   * does table exist?
   * 
   * @param dd
   * @param table
   * @return
   */
  public boolean doesTableExist(DatabaseData dd, String table) {
    String query = "SHOW TABLES FROM `" + dd.getDatabase() + "` LIKE '" + table + "';";
    return ql.queryStringAndConvertToBoolean(dd, query);
  }
  
  /**
   * does column name exist in table?
   * 
   * @param dd
   * @param table
   * @param columnData
   * @return
   */
  public boolean doesColumnExist(DatabaseData dd, ColumnData columnData) {
    String table = columnData.getTable();
    if (table == null || columnData == null) {
      return false;
    }
    if (table.length() == 0 || columnData.getColumnName().length() == 0) {
      return false;
    }
    String sql = "SHOW COLUMNS FROM `" + table + "` FROM `" + dd.getDatabase() + "` LIKE '" + columnData.getColumnName() + "';";
    boolean r = ql.queryStringAndConvertToBoolean(dd, sql);
    return r;
  }
  
  /**
   * query column
   * 
   * @param dd
   * @param table
   * @param columnName
   * @return
   */
  public ColumnData queryColumn(DatabaseData dd, String table, String columnName) {
    String where = "`FIELD`='" + columnName + "'";
    ColumnData[] c = queryColumns(dd, table, where);
    ColumnData r = null;
    if (c != null && c.length > 0) {
      r = c[0];
    }
    return r;
  }
  
  /**
   * query column
   * 
   * @param dd
   * @param columnData
   * @return
   */
  public ColumnData queryColumn(DatabaseData dd, ColumnData columnData) {
    String where = "`FIELD`='" + columnData.getColumnName() + "'";
    ColumnData[] c = queryColumns(dd, columnData.getTable(), where);
    ColumnData r = null;
    if (c != null && c.length > 0) {
      r = c[0];
    }
    return r;
  }
  
  /**
   * get columns 
   * 
   * @param dd
   * @param table
   * @param where - ex: where="`FIELD` LIKE '%myCol%'"; 
   * @return
   */
  public ColumnData[] queryColumns(DatabaseData dd, String table, String where) {
    if (where != null && where.toLowerCase().contains("where") == false) {
      where = "WHERE " + where;
    } else if (where == null) {
      where = "";
    }
    String sql = "SHOW COLUMNS FROM `" + table + "` FROM `" + dd.getDatabase() + "` " + where + ";";
    setTrackSql(sql);
    ColumnData[] columns = null;
    try {
      Connection conn = dd.getConnection();
      Statement select = conn.createStatement();
      ResultSet result = select.executeQuery(sql);
      columns = new ColumnData[ql.getResultSetSize(result)];
      int i = 0;
      while (result.next()) {
        columns[i] = new ColumnData();
        columns[i].setTable(table);
        columns[i].setColumnName(result.getString(1));
        columns[i].setType(result.getString(2));
        //(3) null or not
        //(4) Key
        if (result.getString("Key") != null && 
            result.getString("Key").matches("PRI") == true) {
          columns[i].setIsPrimaryKey(true);
        }
        i++;
      }
      result.close();
      result = null;
      select.close();
      select = null;
      conn.close();
      conn = null;
    } catch (SQLException e) {
      System.err.println("Error: queryColumns(): " + sql);
      setTrackError(e.toString());
      log.error("Optimise.queryColumns(): ", e);
    }
    return columns;
  }
  
  public ColumnData queryPrimaryKey(DatabaseData dd, String table) {
    String sql = "SHOW COLUMNS FROM `" + table + "` FROM `" + dd.getDatabase() + "` WHERE `Key`='PRI';";
    setTrackSql(sql);
    ColumnData column = new ColumnData();
    try {
      Connection conn = dd.getConnection();
      Statement select = conn.createStatement();
      ResultSet result = select.executeQuery(sql);
      while (result.next()) {
        column.setIsPrimaryKey(true);
        column.setColumnName(result.getString(1));
        column.setType(result.getString(2));
        column.setTable(table);
      }
      result.close();
      result = null;
      select.close();
      select = null;
      conn.close();
      conn = null;
    } catch (SQLException e) {
      System.err.println("Error: queryPrimaryKey(): " + sql);
      setTrackError(e.toString());
      log.error("Optimise.queryPrimaryKey(): ", e);
    } 
    return column;
  }
  
  public boolean queryIsColumnPrimarykey(DatabaseData dd, ColumnData columnData) {
    String sql = "SHOW COLUMNS FROM `" + columnData.getTable() + "` FROM `" + dd.getDatabase() + "` " +
    		"WHERE `Key`='PRI' AND `Field`='" + columnData.getColumnName() + "';";
    setTrackSql(sql);
    boolean b = false;
    try {
      Connection conn = dd.getConnection();
      Statement select = conn.createStatement();
      ResultSet result = select.executeQuery(sql);
      while (result.next()) {
        b = true;
      }
      result.close();
      result = null;
      select.close();
      select = null;
      conn.close();
      conn = null;
    } catch (SQLException e) {
      System.err.println("Error: queryPrimaryKey(): " + sql);
      setTrackError(e.toString());
      log.error("Optimise.queryIsColumnPrimarykey()", e);
    } 
    return b;
  }

  /**
   * create columns
   * 
   * @param dd
   * @param columnData
   */
  public ColumnData[] createColumn(DatabaseData dd, ColumnData[] columnData) {
    if (columnData == null) {
      return null;
    }
    for (int i=0; i < columnData.length; i++) {
      if (columnData[i].getColumnName() == null || columnData[i].getColumnName().trim().length() == 0) {
        columnData[i].setColumnName("c" + i);
      }
      columnData[i] = createColumn(dd, columnData[i]);
    }
    return columnData;
  }
  
  /**
   * create column
   * 
   * @param dd
   * @param columnData
   */
  public ColumnData createColumn(DatabaseData dd, ColumnData columnData) {
    if (columnData == null || columnData.getColumnName().length() == 0) {
      return null;
    }
    
    String table = columnData.getTable();
    if (table == null) {
      log.error("ColumnData does not have a table set. table=" + table);
      return null;
    }
    
    // figures out the syntax from columnData
    String sqlSyntaxType = cl.createSqlColumnSyntax(columnData);
    
    boolean exist = doesColumnExist(dd, columnData);    
    if (exist == true) {
      ColumnData exCol = queryColumn(dd, table, columnData.getColumnName());
      columnData.setType(exCol.getSqlType());
      return columnData;
    }

    if (sqlSyntaxType == null) {
      sqlSyntaxType = "TEXT DEFAULT NULL";
    }
    String sql = "ALTER TABLE `" + dd.getDatabase() + "`.`" + table + "` " +
    		"ADD COLUMN `" + columnData.getColumnName() + "`  " + sqlSyntaxType + ";";
    
    log.info("MySqlTransformUtil.createColumn(): " + sql);
    
    ql.update(dd, sql);
    
    return columnData;
  }
  
  /**
   * create a column by specifying a type and maybe length
   * 
   * @param dd
   * @param table
   * @param column
   * @param columnType
   * @param length - varchar(length), decimal(length)
   */
  public void createColumn(DatabaseData dd, ColumnData column, Type columnType, String length) {
    if (length == null | length.length() == 0) {
      if (columnType == Type.VARCHAR) {
        length = "255";
      } else if (columnType == Type.DECIMAL) {
        length = "10,4";
      }
    }
    String type = "";
    if (columnType == Type.TEXT) {
      type = "TEXT DEFAULT NULL";
    } else if (columnType == Type.VARCHAR) {
      type = "VARCHAR(" + length + ") DEFAULT NULL";
    } else if (columnType == Type.INTEGER) {
      type = "INT DEFAULT 0";
    } else if (columnType == Type.DECIMAL) {
      type = "DECIMAL(" + length + ") DEFAULT 0.0";
    } else if (columnType == Type.DATETIME) {
      type = "DATETIME DEFAULT NULL";
    } else {
      type = "TEXT DEFAULT NULL";
    }
    column.setType(type);
    
    createColumn(dd, column);
  }
  
  /**
   * does indexName exist
   * 
   * @param dd
   * @param table
   * @param indexName
   * @return
   */
  public boolean doesIndexExist(DatabaseData dd, String table, String indexName) {
    String sql = "SHOW INDEX FROM `" + table + "` FROM `" + dd.getDatabase() + "` WHERE (`Key_name`= '" + indexName + "')";
    return ql.queryStringAndConvertToBoolean(dd, sql);
  }
  
  /**
   * create Index
   * 
   * @param dd
   * @param table
   * @param indexName 
   * @param indexColumns
   * @param indexKind 0 for default | ColumnData.INDEXKIND_DEFAULT | ColumnData.INDEXKIND_FULLTEXT
   */
  public void createIndex(DatabaseData dd, String table, String indexName, String indexColumns, int indexKind) {

    // Index name length limitation
    if (indexName.length() > 30) {
      indexName = indexName.substring(0,30);
    }
    
    if (doesIndexExist(dd, table, indexName) == true) {
      return;
    }
    
    String kind = "";
    if (indexKind == ColumnData.INDEXKIND_DEFAULT | indexKind == 0) {
      kind = ""; // default is nothing stated
    } else if (indexKind == ColumnData.INDEXKIND_FULLTEXT) {
      kind = "FULLTEXT";
    }
    
    // if the column is a text column, set the index length and its not full text
    // ALTER TABLE `test`.`test` ADD INDEX `new_index1`(`myTxt`(900));
    // ALTER TABLE `test`.`test` ADD INDEX `new_index2`(`smallInt`, `myTxt`(900));
    // This should come in on indexColumns

    String sql = "ALTER TABLE `" + dd.getDatabase() + "`.`" + table + "` " + 
      "ADD " + kind + " INDEX `" + indexName + "`(" + indexColumns + ");";
      
    ql.update(dd, sql);
  }
  
  /**
   * create index for identities
   * 
   * @param dd
   * @param columnData - all the columnData - will get the identities columns from in them
   */
  public void createIndex_forIdentities(DatabaseData dd, ColumnData[] columnData, String indexName) {
                                         
    ColumnData[] identities = new MySqlColumnLib().getColumns_Identities(columnData);

    String indexes = "";
    for (int i = 0; i < identities.length; i++) {
 
      indexes += "`" + columnData[i].getColumnName() + "`";
        
      if (i < identities.length - 1) {
        indexes += ", ";
      }
    }
    
    // create the index
    String table = columnData[0].getTable();
    createIndex(dd, table, indexName, indexes, ColumnData.INDEXKIND_DEFAULT);
  }
  
  /**
   * delete column
   * 
   * @param dd
   * @param table
   * @param columnData
   */
  public void deleteColumn(DatabaseData dd, ColumnData columnData) {
    String sql = "ALTER TABLE `" + dd.getDatabase() + "`.`" + columnData.getTable() + "` " +
    		"DROP COLUMN `" + columnData.getColumnName() + "`;";
    ql.update(dd, sql);
  }
  
  /**
   * delete columns
   * 
   * @param dd
   * @param table
   * @param columnData
   */
  public void deleteColumns(DatabaseData dd, ColumnData[] columnData) {
    String sql = "ALTER TABLE `" + dd.getDatabase() + "`.`" + columnData[0].getTable() + "` ";
    for (int i=0; i < columnData.length; i++) {
      String column = columnData[i].getColumnName();
      sql += "DROP COLUMN `" + column + "`";
      if (i < columnData.length-1) {
        sql += ", ";
      }
    }
    ql.update(dd, sql);
  }
  
  /**
   * delete columns with no data
   *   first check each column, then after that, then go through and delete the columns
   * @param dd
   * @param table
   * @param pruneColumnData - skip these columns
   */
  public void deleteEmptyColumns(DatabaseData dd, String table, ColumnData[] pruneColumnData) {
    if (table == null) {
      return;
    }
    // get all columns of table
    ColumnData[] columnData = queryColumns(dd, table, null);
    columnData = new MySqlColumnLib().prune(columnData, pruneColumnData);
    ArrayList<ColumnData> deleteCols = new ArrayList<ColumnData>();
    int i2 = columnData.length - 1; // count down total
    for (int i = 0; i < columnData.length; i++) {

      log.info(i2 + ". checking column is Empty?: " + columnData[i].getColumnName() + " for data.");
      
      if (doesColumnContainData(dd, columnData[i]) == false && 
          queryIsColumnPrimarykey(dd, columnData[i]) == false) {
        deleteCols.add(columnData[i]);
      }

      // count down
      i2--;
    }
    ColumnData[] odelCols = new ColumnData[deleteCols.size()];
    deleteCols.toArray(odelCols);
    deleteColumns(dd, odelCols);
  }

  /**
   * query column characters longest length,
   *    it looks through the entire columns recordset and finds the longest string's character count
   * @param dd
   * @param table
   * @param column
   * @return
   */
  public long queryColumnCharactersLongestLength(DatabaseData dd, String table, ColumnData column) {
    String sql = "SELECT MAX(LENGTH(`" + column.getColumnName() + "`)) FROM " + dd.getDatabase() + "." + table + ";";
    return ql.queryLong(dd, sql);
  }

  /**
   * show create table script
   * 
   * @param dd
   * @param table
   * @return
   */
  public String showCreateTable(DatabaseData dd, String table) {
    String sql = "SHOW CREATE TABLE `" + dd.getDatabase() + "`.`" + table + "`";
    setTrackSql(sql);
    String s = null;
    try {
      Connection conn = dd.getConnection();
      Statement select = conn.createStatement();
      ResultSet result = select.executeQuery(sql);
      while (result.next()) {
        s = result.getString(2);
      }
      select.close();
      select = null;
      result.close();
      result = null;
      conn.close();
    } catch (SQLException e) {
      System.err.println("Error: queryString(): " + sql);
      setTrackError(e.toString());
      log.error("showCreateTable():", e);
    } 
    return s;
  }
  
  /**
   * drop table, delete it
   * 
   * @param dd
   * @param table
   */
  public void dropTable(DatabaseData dd, String table) {
    String sql = "DROP TABLE IF EXISTS `" + dd.getDatabase() + "`.`" + table + "`;";
    ql.update(dd, sql);
  }
  
  /**
   * does a column contain any data in it?
   * 
   * @param dd
   * @param columnData
   * @return
   */
  public boolean doesColumnContainData(DatabaseData dd, ColumnData columnData) {
    String sql = "SELECT COUNT(`" + columnData.getColumnName() + "`) AS Total " +
    		"FROM `" + dd.getDatabase() + "`.`" + columnData.getTable() + "` " + 
    		"WHERE (`" + columnData.getColumnName() + "` != '' OR " +
    				"`" + columnData.getColumnName() + "` IS NOT NULL);";
    long c = ql.queryLong(dd, sql);
    boolean b = true;
    if (c == 0) {
      b = false;
    }
    return b;
  }
  
  /**
   * delete all indexing for a column. find all the indexing that uses this column 
   *
   * @param dd
   * @param columnData
   * @return indexes, for recreation
   */
  public String[] deleteIndexForColumn(DatabaseData dd, ColumnData columnData) {
    if (columnData == null) {
      return null;
    }
    
    String[] indexesToRestore = showCreateIndex(dd, columnData);
    
    String table = columnData.getTable();
    String sql = "SHOW INDEX FROM `" + dd.getDatabase() + "`.`" + columnData.getTable() + "` " + 
      "WHERE (Key_name != 'Primary') AND (Column_name = '" + columnData.getColumnName() + "')";
    setTrackSql(sql);
    try {
      Connection conn = dd.getConnection();
      Statement select = conn.createStatement();
      ResultSet result = select.executeQuery(sql);
      while (result.next()) {
        String indexName = result.getString(3);
        deleteIndex(dd, table, indexName);
      }
      select.close();
      select = null;
      result.close();
      result = null;
    } catch (SQLException e) {
      System.err.println("Error: deleteIndexForColumn(): " + sql);
      setTrackError(e.toString());
      log.error("Optimise.deleteIndexForColumn(): Error: ", e);
    }

    return indexesToRestore;
  }
  
  /**
   * delete indexes and returns indexes deleted
   * 
   * @param dd
   * @param columnData
   * @return - indexes deleted
   */
  public String[] deleteIndexForColumn(DatabaseData dd, ColumnData[] columnData) {
    
    ArrayList<String[]> index = new ArrayList<String[]>();
    for(int i=0; i < columnData.length; i++) {
      String[] inToRestore = deleteIndexForColumn(dd, columnData[i]);
      if (inToRestore.length > 0) {
        index.add(inToRestore);
      }
    }
    
    ArrayList<String> in = new ArrayList<String>();
    for (int i=0; i < index.size(); i++) {
      String[] rr = index.get(i);
      for (int b=0; b < rr.length; b++) {
        in.add(rr[b]);
      }
    }
    
    String[] r = new String[in.size()];
    in.toArray(r);
    
    return r;
  }
    
  /**
   * delete index
   * 
   * @param dd
   * @param table
   * @param indexName
   */
  public void deleteIndex(DatabaseData dd, String table, String indexName) {
    String sql = "DROP INDEX `" + indexName + "` ON `" + table + "`;";
   
    log.info("deleteIndex: " + sql);
    
    ql.update(dd, sql);
  }
  
  /**
   * alter column
   *   delete the indexes on the column, then restore them on alter
   * @param dd
   * @param columnData
   */
  public void alterColumn(DatabaseData dd, ColumnData columnData) {
    String[] sqlIndexRestore = deleteIndexForColumn(dd, columnData);
    String indexSql = StringUtil.toCsv_NoQuotes(sqlIndexRestore);
    
    String modifyColumn = "`" + columnData.getColumnName() + "` " + columnData.getSqlType();
    String sql = "ALTER TABLE `" + dd.getDatabase() + "`.`" + columnData.getTable() + "` MODIFY COLUMN " + modifyColumn + " ";
    
    if (indexSql != null) {
      sql += ", " + indexSql;
    }
    
    log.info("Transform: alterColum(): " + sql);
    
    ql.update(dd, sql);
  }
  
  public void alterColumn(DatabaseData dd, ColumnData[] columnData) {
    if (columnData == null && columnData.length == 0) {
      return;
    }
    String[] sqlIndexRestore = deleteIndexForColumn(dd, columnData);
   
    
    String sql = "ALTER TABLE `" + dd.getDatabase() + "`.`" + columnData[0].getTable() + "` ";
    for (int i=0; i < columnData.length; i++) {
      String modifyColumn = "MODIFY COLUMN `" + columnData[i].getColumnName() + "` " + columnData[i].getSqlType();
      sql += " " + modifyColumn + " ";
      if (i < columnData.length - 1) {
        sql += ",";
      }
    }
    
    sqlIndexRestore = checkIndexTextLengths(dd, columnData, sqlIndexRestore);
    String indexSql = StringUtil.toCsv_NoQuotes(sqlIndexRestore);
    if (indexSql != null) {
      sql += ", " + indexSql;
    }
    
    log.info("Transform: alterColum(): " + sql);
    
    ql.update(dd, sql);
  }
  
  private String[] checkIndexTextLengths(DatabaseData dd, ColumnData[] columnData, String[] indexes) {
    for (int i=0; i < indexes.length; i++) {
      indexes[i] = checkIndexTextLength(dd, columnData, indexes[i]);
    }
    return indexes;
  }

  private String checkIndexTextLength(DatabaseData dd, ColumnData[] columnData, String index) {
    
    log.info("index: " + index);
    if (index.contains(",") == true) {
      String colstr = StringUtil.getValue("\\((.*)\\)$", index);
      String[] cols = colstr.split(",");
      ColumnData[] newIndex = new ColumnData[cols.length];
      for (int i=0; i < cols.length; i++) {
        String colname = StringUtil.getValue("`(.*)`", cols[i]);
        log.info(colname);
        int cIndex = new MySqlColumnLib().searchColumnByName_UsingComparator(columnData, colname);
        if (cIndex > -1) {
          ColumnData c = columnData[cIndex];
          newIndex[i] = c;
        } else {
          newIndex[i] = new MySqlTransformLib().queryColumn(dd, columnData[0].getTable(), colname);
        }
      }
      String csql = new MySqlColumnLib().getSql_Index_Multi(newIndex);
      index = index.replaceAll("\\(.*\\)$", "(" + csql + ")");
      log.info("replace: " + index);
    }
   
    return index;
  }

  public String[] showCreateIndex(DatabaseData dd, ColumnData columnData) {

    String showCreateTable = showCreateTable(dd, columnData.getTable());
  
    String regex = "KEY[\040]+`.*?`" + columnData.getColumnName() + "`.*?\n";
    
    ArrayList<String> indexes = new ArrayList<String>(); 
    try {
      Pattern p = Pattern.compile(regex);
      Matcher m = p.matcher(showCreateTable);
      while (m.find()) {
        String index = m.group();
        // if the index was a text and
        index = changeIndexFromTextToVarchar(columnData, index);
        indexes.add(index);
      }
    } catch (Exception e) {
      log.error("ERROR: showCreateIndex(): findMatch: regex error", e);
    }
  
    String[] r = new String[indexes.size()];
    for (int i=0; i < indexes.size(); i++) {
      String s = indexes.get(i);
      s = s.replaceAll("\n", "");
      s = s.replaceAll("^KEY", "ADD INDEX");
      if (s.matches(".*?,") == true) {
        s = s.substring(0,s.length() - 1);
        r[i] = s;
      } else {
        r[i] = s;
      }
    }
    return r;
  }
  
  /**
   * when going from text to varchar, take out the index byte length
   * @param columnData
   * @param index
   * @return
   */
  private String changeIndexFromTextToVarchar(ColumnData columnData, String index) {
    
    boolean change = false;
    if (columnData.getSqlType().toLowerCase().contains("text") == false) {
      change = true;
    }
    
    // does the index have a index length?
    if (change == true && index.matches(".*([0-9]+).*\n") == true) {
      try {
        index = index.replaceFirst("(\\([0-9]+\\))", "");
      } catch (Exception e) {
      	setTrackError(e.toString());
        log.error("changeIndexFromTexttoVarchar()", e);
      }
    } 
    
    return index;
  }

  public String getType() {
    return "MySql";
  }
  
  public void createUser(DatabaseData dd, UserDbData userData) {
    String userName = userData.getUserName();
    String password = userData.getPassword();
    String[] host = userData.getHost();
    if (userName == null || password == null) {
      return;
    }
    for (int i=0; i < host.length; i++) {
      createUser(dd, userData.getUserName(), userData.getPassword(), host[i]);
    }
  }
  
  public void createUser(DatabaseData dd, String userName, String password, String host) {
    boolean exists = doesUserExist(dd, userName, password, host);
    if (exists == true) {
      return;
    }
    String sql = "CREATE USER '" + userName + "'@'" + host + "' IDENTIFIED BY '" + password + "';";
    ql.update(dd, sql);
  }
  
  public boolean doesUserExist(DatabaseData dd, String userName, String password, String host) {
    String sql = "SELECT COUNT(*) AS t FROM `mysql`.`user` " +
    		"WHERE `User`='" + userName + "' AND `Host`='" + host + "';";
    return ql.queryBoolean(dd, sql);
  }
  
  public String[] getTablesAll(DatabaseData dd) {
  	String sql = "SHOW TABLES FROM " + dd.getDatabase();
  	String[] table = null;
  	Connection conn = null;
    Statement select = null;
    try {
      conn = dd.getConnection();
      select = conn.createStatement();
      ResultSet result = select.executeQuery(sql);
      table = new String[ql.getResultSetSize(result)];
      int i = 0;
      while (result.next()) {
       table[i] = result.getString(1);
       i++;
      }
      result.close();
      result = null;
      select.close();
      select = null;
      conn.close();
    } catch (SQLException e) {
      System.err.println("Error: getAllTables(): " + sql);
      System.err.println("servletConn:" + dd.getConnetionByConext() + " Server: " + dd.getServer());
      log.error("Error: " + sql + "\n", e);
      e.printStackTrace();
    } finally {
      conn = null;
      select = null;
    }
    return table;
  }
  
}
