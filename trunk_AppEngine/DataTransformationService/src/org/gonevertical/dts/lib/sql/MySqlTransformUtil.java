package org.gonevertical.dts.lib.sql;

import org.gonevertical.dts.data.ColumnData;
import org.gonevertical.dts.data.ColumnData.Type;
import org.gonevertical.dts.data.DatabaseData;
import org.gonevertical.dts.lib.sql.transformlib.MySqlTransformLib;


public class MySqlTransformUtil extends MySqlQueryUtil {

  public MySqlTransformUtil() {
  }

  /**
   * fix table name
   * 
   * @param table
   * @return
   */
  public static String fixTableName(String table) {
    return new MySqlTransformLib().fixTableName(table);
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
  public static void createTable(DatabaseData dd, String table, String primaryKeyName) {
    new MySqlTransformLib().createTable(dd, table, primaryKeyName);
  }
  
  /**
   * does table exist?
   * 
   * @param dd
   * @param table
   * @return
   */
  public static boolean doesTableExist(DatabaseData dd, String table) {
    return new MySqlTransformLib().doesTableExist(dd, table);
  }
  
  /**
   * does column name exist in table?
   * 
   * @param dd
   * @param table
   * @param columnData
   * @return
   */
  public static boolean doesColumnExist(DatabaseData dd, ColumnData columnData) {
    return new MySqlTransformLib().doesColumnExist(dd, columnData);
  }
  
  /**
   * query column
   * 
   * @param dd
   * @param table
   * @param columnName
   * @return
   */
  public static ColumnData queryColumn(DatabaseData dd, String table, String columnName) {
    return new MySqlTransformLib().queryColumn(dd, table, columnName);
  }
  
  /**
   * query column
   * 
   * @param dd
   * @param columnData
   * @return
   */
  public static ColumnData queryColumn(DatabaseData dd, ColumnData columnData) {
    return new MySqlTransformLib().queryColumn(dd, columnData);
  }
  
  /**
   * get columns 
   * 
   * @param dd
   * @param table
   * @param where - ex: where="`FIELD` LIKE '%myCol%'"; 
   * @return
   */
  public static ColumnData[] queryColumns(DatabaseData dd, String table, String where) {
    return new MySqlTransformLib().queryColumns(dd, table, where);
  }
  
  public static ColumnData queryPrimaryKey(DatabaseData dd, String table) {
    return new MySqlTransformLib().queryPrimaryKey(dd, table);
  }
  
  public static boolean queryIsColumnPrimarykey(DatabaseData dd, ColumnData columnData) {
    return new MySqlTransformLib().queryIsColumnPrimarykey(dd, columnData);
  }

  /**
   * create columns
   * 
   * @param dd
   * @param columnData
   */
  public static ColumnData[] createColumn(DatabaseData dd, ColumnData[] columnData) {
    return new MySqlTransformLib().createColumn(dd, columnData);
  }
  
  /**
   * create column
   * 
   * @param dd
   * @param table
   * @param columnData - column name and type - varchar(255) or TEXT or TEXT DEFAULT NULL or INTEGER DEFAULT 0
   */
  public static ColumnData createColumn(DatabaseData dd, ColumnData columnData) {
    return new MySqlTransformLib().createColumn(dd, columnData);
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
  public static void createColumn(DatabaseData dd, ColumnData column, Type columnType, String length) {
    new MySqlTransformLib().createColumn(dd, column, columnType, length);
  }
  
  /**
   * does indexName exist
   * 
   * @param dd
   * @param table
   * @param indexName
   * @return
   */
  public static boolean doesIndexExist(DatabaseData dd, String table, String indexName) {
    return new MySqlTransformLib().doesIndexExist(dd, table, indexName);
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
  public static void createIndex(DatabaseData dd, String table, String indexName, String indexColumns, int indexKind) {
    new MySqlTransformLib().createIndex(dd, table, indexName, indexColumns, indexKind);
  }
  
  /**
   * create index for identities
   * 
   * @param dd
   * @param columnData - all the columnData - will get the identities columns from in them
   */
  public static void createIndex_forIdentities(DatabaseData dd, ColumnData[] columnData, String indexName) {
    new MySqlTransformLib().createIndex_forIdentities(dd, columnData, indexName);
  }
  
  /**
   * delete column
   * 
   * @param dd
   * @param table
   * @param columnData
   */
  public static void deleteColumn(DatabaseData dd, ColumnData columnData) {
    new MySqlTransformLib().deleteColumn(dd, columnData);
  }
  
  /**
   * delete columns
   * 
   * @param dd
   * @param table
   * @param columnData
   */
  public static void deleteColumns(DatabaseData dd, ColumnData[] columnData) {
    new MySqlTransformLib().deleteColumns(dd, columnData);
  }
  
  /**
   * delete columns with no data
   *   first check each column, then after that, then go through and delete the columns
   * @param dd
   * @param table
   * @param pruneColumnData - skip these columns
   */
  public static void deleteEmptyColumns(DatabaseData dd, String table, ColumnData[] pruneColumnData) {
    new MySqlTransformLib().deleteEmptyColumns(dd, table, pruneColumnData);
  }

  /**
   * query column characters longest length,
   *    it looks through the entire columns recordset and finds the longest string's character count
   * @param dd
   * @param table
   * @param column
   * @return
   */
  public static long queryColumnCharactersLongestLength(DatabaseData dd, String table, ColumnData column) {
    return new MySqlTransformLib().queryColumnCharactersLongestLength(dd, table, column);
  }

  /**
   * show create table script
   * 
   * @param dd
   * @param table
   * @return
   */
  public static String showCreateTable(DatabaseData dd, String table) {
    return new MySqlTransformLib().showCreateTable(dd, table);
  }
  
  /**
   * drop table, delete it
   * 
   * @param dd
   * @param table
   */
  public static void dropTable(DatabaseData dd, String table) {
    new MySqlTransformLib().dropTable(dd, table);
  }
  
  /**
   * does a column contain any data in it?
   * 
   * @param dd
   * @param columnData
   * @return
   */
  public static boolean doesColumnContainData(DatabaseData dd, ColumnData columnData) {
    return new MySqlTransformLib().doesColumnContainData(dd, columnData);
  }
  
  /**
   * delete all indexing for a column. find all the indexing that uses this column 
   *
   * @param dd
   * @param columnData
   * @return indexes, for recreation
   */
  public static String[] deleteIndexForColumn(DatabaseData dd, ColumnData columnData) {
    return new MySqlTransformLib().deleteIndexForColumn(dd, columnData);
  }
  
  /**
   * delete indexes and returns indexes deleted
   * 
   * @param dd
   * @param columnData
   * @return - indexes deleted
   */
  public static String[] deleteIndexForColumn(DatabaseData dd, ColumnData[] columnData) {
    return new MySqlTransformLib().deleteIndexForColumn(dd, columnData);
  }
    
  /**
   * delete index
   * 
   * @param dd
   * @param table
   * @param indexName
   */
  public static void deleteIndex(DatabaseData dd, String table, String indexName) {
    new MySqlTransformLib().deleteIndex(dd, table, indexName);
  }
  
  /**
   * alter column
   *   delete the indexes on the column, then restore them on alter
   * @param dd
   * @param columnData
   */
  public static void alterColumn(DatabaseData dd, ColumnData columnData) {
    new MySqlTransformLib().alterColumn(dd, columnData);
  }
  
  public static void alterColumn(DatabaseData dd, ColumnData[] columnData) {
    new MySqlTransformLib().alterColumn(dd, columnData);
  }
  
  public static String[] showCreateIndex(DatabaseData dd, ColumnData columnData) {
    return new MySqlTransformLib().showCreateIndex(dd, columnData);
  }

  
  
}
