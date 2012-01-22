package org.gonevertical.dts.lib.sql.transformlib;

import org.gonevertical.dts.data.ColumnData;
import org.gonevertical.dts.data.ColumnData.Type;
import org.gonevertical.dts.data.DatabaseData;
import org.gonevertical.dts.data.StatData;
import org.gonevertical.dts.data.UserDbData;

public interface TransformLib {

	public void setStats(StatData stats);
	
  public String getType();
  
  public String fixTableName(String table);
  
  public void createTable(DatabaseData dd, String table, String primaryKeyName);

  public boolean doesTableExist(DatabaseData dd, String table);
  
  public boolean doesColumnExist(DatabaseData dd, ColumnData columnData);
  
  public ColumnData queryColumn(DatabaseData dd, String table, String columnName);
  
  public ColumnData queryColumn(DatabaseData dd, ColumnData columnData);
  
  public ColumnData[] queryColumns(DatabaseData dd, String table, String where);
  
  public ColumnData queryPrimaryKey(DatabaseData dd, String table);
  
  public boolean queryIsColumnPrimarykey(DatabaseData dd, ColumnData columnData);
  
  public ColumnData[] createColumn(DatabaseData dd, ColumnData[] columnData);
  
  public ColumnData createColumn(DatabaseData dd, ColumnData columnData);
  
  public void createColumn(DatabaseData dd, ColumnData column, Type columnType, String length);
  
  public boolean doesIndexExist(DatabaseData dd, String table, String indexName);
  
  public void createIndex(DatabaseData dd, String table, String indexName, String indexColumns, int indexKind);
  
  public void createIndex_forIdentities(DatabaseData dd, ColumnData[] columnData, String indexName);
  
  public void deleteColumn(DatabaseData dd, ColumnData columnData);
  
  public void deleteColumns(DatabaseData dd, ColumnData[] columnData);
  
  public void deleteEmptyColumns(DatabaseData dd, String table, ColumnData[] pruneColumnData);

  public long queryColumnCharactersLongestLength(DatabaseData dd, String table, ColumnData column);

  public String showCreateTable(DatabaseData dd, String table);
  
  public void dropTable(DatabaseData dd, String table);
  
  public boolean doesColumnContainData(DatabaseData dd, ColumnData columnData);
  
  public String[] deleteIndexForColumn(DatabaseData dd, ColumnData columnData);
  
  public String[] deleteIndexForColumn(DatabaseData dd, ColumnData[] columnData);
  
  public void deleteIndex(DatabaseData dd, String table, String indexName);
  
  public void alterColumn(DatabaseData dd, ColumnData columnData);
  
  public void alterColumn(DatabaseData dd, ColumnData[] columnData);
 
  /**
   * show create index
   * 
   * @param dd
   * @param columnData
   * @return
   */
  public String[] showCreateIndex(DatabaseData dd, ColumnData columnData);
  
  public void createUser(DatabaseData dd, UserDbData userData);
  
  public void createUser(DatabaseData dd, String userName, String password, String host);
  
  public boolean doesUserExist(DatabaseData dd, String userName, String password, String host);
 
  public String[] getTablesAll(DatabaseData dd);
}
