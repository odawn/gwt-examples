package org.gonevertical.dts.lib.sql.transformlib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.gonevertical.dts.data.ColumnData;
import org.gonevertical.dts.data.DatabaseData;
import org.gonevertical.dts.data.ColumnData.Type;
import org.gonevertical.dts.lib.sql.querylib.QueryLib;
import org.gonevertical.dts.lib.sql.querymulti.QueryLibFactory;
import org.gonevertical.dts.lib.sql.transformmulti.TransformLibFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MySqlTransformLibTest {

  private DatabaseData dd;
  private QueryLib ql;
  private TransformLib tl;
  
  @Before
  public void setUp() throws Exception {
    dd = new DatabaseData(DatabaseData.TYPE_MYSQL, "ark", "3306", "test", "test#", "test");
    
    ql = QueryLibFactory.getLib(DatabaseData.TYPE_MYSQL);
    tl = TransformLibFactory.getLib(DatabaseData.TYPE_MYSQL);
  }

  @After
  public void tearDown() throws Exception {
    dd = null;
    ql = null;
    tl = null;
  }

  @Test
  public void testFixTableName() {
    String table = "na%";
    String expected = "na_per";
    String actual = tl.fixTableName(table);
    assertEquals(expected, actual);
  }
  
  @Test
  public void testFixTableName2() {
    String table = "na#";
    String expected = "na_num";
    String actual = tl.fixTableName(table);
    assertEquals(expected, actual);
  }
  
  @Test
  public void testFixTableName3() {
    String table = "na$!.[]";
    String expected = "na_";
    String actual = tl.fixTableName(table);
    assertEquals(expected, actual);
  }
  
  @Test
  public void testFixTableName4() {
    String table = "na_";
    String expected = "na_";
    String actual = tl.fixTableName(table);
    assertEquals(expected, actual);
  }

  @Test
  public void testCreateTable() {
    String table = "test_table";
    tl.dropTable(dd, table);
    tl.createTable(dd, "test_table", "TestId");
    boolean actual = tl.doesTableExist(dd, table);
    boolean exptected = true;
    assertEquals(exptected, actual);
  }

  @Test
  public void testDoesTableExist() {
    String table = "test_table";
    tl.dropTable(dd, table);
    tl.createTable(dd, "test_table", "TestId");
    boolean actual = tl.doesTableExist(dd, table);
    boolean exptected = true;
    assertEquals(exptected, actual);
  }

  @Test
  public void testDoesColumnExist() {
    String table = "test_table";
    tl.dropTable(dd, table);
    tl.createTable(dd, "test_table", "TestId");
    ColumnData columnData = new ColumnData(table, "TestId", "BIGINT DEFAULT 0");
    boolean actual = tl.doesColumnExist(dd, columnData);
    boolean exptected = true;
    assertEquals(exptected, actual);
  }

  @Test
  public void testQueryColumnDatabaseDataStringString() {
    String table = "test_table";
    tl.dropTable(dd, table);
    tl.createTable(dd, "test_table", "TestId");
    ColumnData columnData = new ColumnData(table, "TestId", "BIGINT DEFAULT 0");
    ColumnData col = tl.queryColumn(dd, table, "TestId");
    assertEquals(columnData.getColumnName(), col.getColumnName());
  }

  @Test
  public void testQueryColumnDatabaseDataColumnData() {
    String table = "test_table";
    tl.dropTable(dd, table);
    tl.createTable(dd, "test_table", "TestId");
    ColumnData columnData = new ColumnData(table, "TestId", "BIGINT DEFAULT 0");
    ColumnData col = tl.queryColumn(dd, columnData);
    assertEquals(columnData.getColumnName(), col.getColumnName());
  }

  @Test
  public void testQueryColumns() {
    String table = "test_table";
    tl.dropTable(dd, table);
    tl.createTable(dd, "test_table", "TestId");
    ColumnData columnData2 = new ColumnData(table, "Name", "VARCHAR(50) DEFAULT NULL");
    tl.createColumn(dd, columnData2);
    ColumnData[] cols = tl.queryColumns(dd, table, null);
    assertEquals(2, cols.length);
  }
  
  @Test
  public void testQueryColumns2() {
    String table = "test_table";
    tl.dropTable(dd, table);
    tl.createTable(dd, "test_table", "TestId");
    ColumnData columnData2 = new ColumnData(table, "Name", "VARCHAR(50) DEFAULT NULL");
    tl.createColumn(dd, columnData2);
    ColumnData[] cols = tl.queryColumns(dd, table, "`field`='Name'");
    assertEquals(1, cols.length);
  }

  @Test
  public void testQueryPrimaryKey() {
    String table = "test_table";
    tl.dropTable(dd, table);
    tl.createTable(dd, "test_table", "TestId");
    ColumnData primaryKeyCol = tl.queryPrimaryKey(dd, table);
    String expected = "TestId";
    assertEquals(expected, primaryKeyCol.getColumnName());
  }

  @Test
  public void testQueryIsColumnPrimarykey() {
    String table = "test_table";
    tl.dropTable(dd, table);
    tl.createTable(dd, "test_table", "TestId");
    ColumnData columnData = new ColumnData(table, "TestId", "BIGINT DEFAULT 0");
    boolean b = tl.queryIsColumnPrimarykey(dd, columnData);
    assertEquals(true, b);
  }
  
  @Test
  public void testQueryIsColumnPrimarykey2() {
    String table = "test_table";
    tl.dropTable(dd, table);
    tl.createTable(dd, "test_table", "TestId");
    ColumnData columnData2 = new ColumnData(table, "Name", "VARCHAR(50) DEFAULT NULL");
    tl.createColumn(dd, columnData2);
    boolean b = tl.queryIsColumnPrimarykey(dd, columnData2);
    assertEquals(false, b);
  }

  @Test
  public void testCreateColumnDatabaseDataColumnDataArray() {
    String table = "test_table";
    tl.dropTable(dd, table);
    tl.createTable(dd, "test_table", "TestId");
    ColumnData c1 = new ColumnData(table, "Name", "VARCHAR(50) DEFAULT NULL");
    ColumnData c2 = new ColumnData(table, "Desc", "VARCHAR(50) DEFAULT NULL");
    ColumnData c3 = new ColumnData(table, "Note", "VARCHAR(50) DEFAULT NULL");
    ColumnData[] cols = new ColumnData[3];
    cols[0] = c1;
    cols[1] = c2;
    cols[2] = c3;
    tl.createColumn(dd, cols);
    ColumnData[] cr = tl.queryColumns(dd, table, null);
    assertEquals(4, cr.length);
  }

  @Test
  public void testCreateColumnDatabaseDataColumnData() {
    String table = "test_table";
    tl.dropTable(dd, table);
    tl.createTable(dd, "test_table", "TestId");
    ColumnData columnData2 = new ColumnData(table, "Name", "VARCHAR(50) DEFAULT NULL");
    tl.createColumn(dd, columnData2);
    ColumnData[] cols = tl.queryColumns(dd, table, "`field`='Name'");
    assertEquals(1, cols.length);
  }

  @Test
  public void testCreateColumnDatabaseDataColumnDataIntString() {
    String table = "test_table";
    tl.dropTable(dd, table);
    tl.createTable(dd, "test_table", "TestId");
    ColumnData columnData2 = new ColumnData(table, "Name", "VARCHAR(50) DEFAULT NULL");
    tl.createColumn(dd, columnData2, Type.VARCHAR, "50");
    ColumnData[] cols = tl.queryColumns(dd, table, "`field`='Name'");
    assertEquals(1, cols.length);
  }

  @Test
  public void testDoesIndexExist() {
    String table = "test_table";
    tl.dropTable(dd, table);
    tl.createTable(dd, "test_table", "TestId");
    ColumnData columnData2 = new ColumnData(table, "Name", "VARCHAR(50) DEFAULT NULL");
    tl.createColumn(dd, columnData2);
    String sql = "ALTER TABLE `test`.`test_table` ADD INDEX testindexname(`Name`);";
    ql.update(dd, sql);
    boolean actual = tl.doesIndexExist(dd, table, "testindexname");
    assertEquals(true, actual);
  }

  @Test
  public void testCreateIndex() {
    String table = "test_table";
    tl.dropTable(dd, table);
    tl.createTable(dd, "test_table", "TestId");
    ColumnData columnData2 = new ColumnData(table, "Name", "VARCHAR(50) DEFAULT NULL");
    tl.createColumn(dd, columnData2);
    String indexName = "testindexname";
    String indexColumns = "TestId,Name";
    int indexKind = ColumnData.INDEXKIND_DEFAULT;
    tl.createIndex(dd, table, indexName, indexColumns, indexKind);
    boolean actual = tl.doesIndexExist(dd, table, "testindexname");
    assertEquals(true, actual);
  }

  @Test
  public void testCreateIndex_forIdentities() {
    String table = "test_table";
    tl.dropTable(dd, table);
    tl.createTable(dd, "test_table", "TestId");
    ColumnData columnData2 = new ColumnData(table, "Name", "VARCHAR(50) DEFAULT NULL");
    columnData2.setIdentity(true);
    tl.createColumn(dd, columnData2);
    ColumnData columnData3 = new ColumnData(table, "Phone", "VARCHAR(15) DEFAULT NULL");
    columnData3.setIdentity(true);
    tl.createColumn(dd, columnData3);
    ColumnData[] columnData = new ColumnData[2];
    columnData[0] = columnData2;
    columnData[1] = columnData3;
    tl.createIndex_forIdentities(dd, columnData, "identiesIndex");
    boolean actual = tl.doesIndexExist(dd, table, "identiesIndex");
    assertEquals(true, actual);
  }

  @Test
  public void testDeleteColumn() {
    String table = "test_table";
    tl.dropTable(dd, table);
    tl.createTable(dd, "test_table", "TestId");
    ColumnData columnData2 = new ColumnData(table, "Name", "VARCHAR(50) DEFAULT NULL");
    tl.createColumn(dd, columnData2);
    tl.deleteColumn(dd, columnData2);
    boolean b = tl.doesColumnExist(dd, columnData2);
    assertEquals(false, b);
  }

  @Test
  public void testDeleteColumns() {
    String table = "test_table";
    tl.dropTable(dd, table);
    tl.createTable(dd, "test_table", "TestId");
    ColumnData columnData2 = new ColumnData(table, "Name", "VARCHAR(50) DEFAULT NULL");
    tl.createColumn(dd, columnData2);
    ColumnData columnData3 = new ColumnData(table, "Desc", "VARCHAR(50) DEFAULT NULL");
    tl.createColumn(dd, columnData3);
    ColumnData[] columnData = new ColumnData[2];
    columnData[0] = columnData2;
    columnData[1] = columnData3;
    tl.deleteColumns(dd, columnData);
    ColumnData[] cr = tl.queryColumns(dd, table, null);
    assertEquals(1, cr.length);
  }

  @Test
  public void testDeleteEmptyColumns() {
    String table = "test_table";
    tl.dropTable(dd, table);
    tl.createTable(dd, "test_table", "TestId");
    ColumnData columnData2 = new ColumnData(table, "Name", "VARCHAR(50) DEFAULT NULL");
    tl.createColumn(dd, columnData2);
    ColumnData columnData3 = new ColumnData(table, "Desc", "VARCHAR(50) DEFAULT NULL");
    tl.createColumn(dd, columnData3);
    ColumnData[] columnData = new ColumnData[2];
    columnData[0] = columnData2;
    columnData[1] = columnData3;
    tl.deleteEmptyColumns(dd, table, null);
    ColumnData[] cr = tl.queryColumns(dd, table, null);
    assertEquals(1, cr.length);
  }

  @Test
  public void testQueryColumnCharactersLongestLength() {
    String table = "test_table";
    tl.dropTable(dd, table);
    tl.createTable(dd, "test_table", "TestId");
    ColumnData columnData2 = new ColumnData(table, "Name", "TEXT DEFAULT NULL");
    tl.createColumn(dd, columnData2);
    String sql = "INSERT INTO " + table + " SET Name='abcdef';";
    ql.update(dd, sql);
    sql = "INSERT INTO " + table + " SET Name='abcdefghijklmnopqr';";
    ql.update(dd, sql);
    sql = "INSERT INTO " + table + " SET Name='abcdefghijklmnopqrstuvwxyz';";
    ql.update(dd, sql);
    ColumnData column = new ColumnData(table, "Name", "TEXT DEFAULT NULL");
    long maxlength = tl.queryColumnCharactersLongestLength(dd, table, column);
    assertEquals(26, maxlength);
  }

  @Test
  public void testShowCreateTable() {
    String table = "test_table";
    tl.dropTable(dd, table);
    tl.createTable(dd, "test_table", "TestId");
    ColumnData columnData2 = new ColumnData(table, "Name", "TEXT DEFAULT NULL");
    tl.createColumn(dd, columnData2);
    String expected = "CREATE TABLE `test_table` (\n  " +
      "`TestId` bigint(20) NOT NULL auto_increment,\n  " +
      "`Name` text,\n  " +
      "PRIMARY KEY  (`TestId`)\n" +
      ") ENGINE=MyISAM DEFAULT CHARSET=latin1";
    String actual = tl.showCreateTable(dd, table);
    assertEquals(expected, actual);
  }

  @Test
  public void testDropTable() {
    String table = "test_table";
    tl.dropTable(dd, table);
    tl.createTable(dd, "test_table", "TestId");
    tl.dropTable(dd, table);
    boolean actual = tl.doesTableExist(dd, table);
    assertEquals(false, actual);
  }

  @Test
  public void testDoesColumnContainData() {
    String table = "test_table";
    tl.dropTable(dd, table);
    tl.createTable(dd, "test_table", "TestId");
    ColumnData columnData2 = new ColumnData(table, "Name", "TEXT DEFAULT NULL");
    tl.createColumn(dd, columnData2);
    String sql = "INSERT INTO " + table + " SET Name='abcdef';";
    ql.update(dd, sql);
    sql = "INSERT INTO " + table + " SET Name='abcdefghijklmnopqr';";
    ql.update(dd, sql);
    sql = "INSERT INTO " + table + " SET Name='abcdefghijklmnopqrstuvwxyz';";
    ql.update(dd, sql);
    ColumnData columnData = new ColumnData(table, "Name", "TEXT DEFAULT NULL");
    boolean b = tl.doesColumnContainData(dd, columnData);
    assertEquals(true, b);
  }

  @Test
  public void testDeleteIndexForColumnDatabaseDataColumnData() {
    String table = "test_table";
    tl.dropTable(dd, table);
    tl.createTable(dd, "test_table", "TestId");
    ColumnData columnData2 = new ColumnData(table, "Name", "VARCHAR(50) DEFAULT NULL");
    tl.createColumn(dd, columnData2);
    String indexName = "testindexname";
    String indexColumns = "TestId,Name";
    int indexKind = ColumnData.INDEXKIND_DEFAULT;
    tl.createIndex(dd, table, indexName, indexColumns, indexKind);
    tl.deleteIndex(dd, table, "testindexname");
    boolean actual = tl.doesIndexExist(dd, table, "testindexname");
    assertEquals(false, actual);
  }

  @Test
  public void testDeleteIndexForColumnDatabaseDataColumnDataArray() {
    String table = "test_table";
    tl.dropTable(dd, table);
    tl.createTable(dd, "test_table", "TestId");
    ColumnData columnData2 = new ColumnData(table, "Name", "VARCHAR(50) DEFAULT NULL");
    ColumnData columnData3 = new ColumnData(table, "Desc", "VARCHAR(50) DEFAULT NULL");
    tl.createColumn(dd, columnData2);
    tl.createColumn(dd, columnData3);
    String indexName = "testindexname";
    String indexColumns = "TestId,Name";
    int indexKind = ColumnData.INDEXKIND_DEFAULT;
    tl.createIndex(dd, table, indexName, indexColumns, indexKind);
    ColumnData[] columnData = new ColumnData[2];
    columnData[0] = columnData2;
    columnData[1] = columnData3;
    tl.deleteIndexForColumn(dd, columnData);
    boolean actual = tl.doesIndexExist(dd, table, "testindexname");
    assertEquals(false, actual);
  }

  @Test
  public void testDeleteIndex() {
    String table = "test_table";
    tl.dropTable(dd, table);
    tl.createTable(dd, "test_table", "TestId");
    ColumnData columnData2 = new ColumnData(table, "Name", "VARCHAR(50) DEFAULT NULL");
    ColumnData columnData3 = new ColumnData(table, "Desc", "VARCHAR(50) DEFAULT NULL");
    tl.createColumn(dd, columnData2);
    tl.createColumn(dd, columnData3);
    String indexName = "testindexname";
    String indexColumns = "TestId,Name";
    int indexKind = ColumnData.INDEXKIND_DEFAULT;
    tl.createIndex(dd, table, indexName, indexColumns, indexKind);
    tl.deleteIndex(dd, table, indexName);
    boolean actual = tl.doesIndexExist(dd, table, "testindexname");
    assertEquals(false, actual);
  }

  @Test
  public void testAlterColumnDatabaseDataColumnData2() {
    String table = "test_table";
    tl.dropTable(dd, table);
    tl.createTable(dd, "test_table", "TestId");
    ColumnData columnData2 = new ColumnData(table, "Name", "VARCHAR(50) DEFAULT NULL");
    ColumnData columnData3 = new ColumnData(table, "Desc", "VARCHAR(50) DEFAULT NULL");
    tl.createColumn(dd, columnData2);
    tl.createColumn(dd, columnData3);
    String indexName = "testindexname";
    String indexColumns = "TestId,Name";
    int indexKind = ColumnData.INDEXKIND_DEFAULT;
    tl.createIndex(dd, table, indexName, indexColumns, indexKind);
    columnData2.setType("VARCHAR(150) DEFAULT NULL");
    columnData3.setType("INTEGER DEFAULT 0");
    ColumnData[] columnData = new ColumnData[2];
    columnData[0] = columnData2;
    columnData[1] = columnData3;
    tl.alterColumn(dd, columnData);
    ColumnData[] cols = tl.queryColumns(dd, table, null);
    boolean f = false;
    if (cols[1].getSqlType().equals("varchar(150)") == true && 
        cols[2].getSqlType().equals("int(11)") == true) {
      f = true;
    } 
    assertEquals(true, f);
  }
  
  @Test
  public void testAlterColumnDatabaseDataColumnData3() {
    String table = "test_table";
    tl.dropTable(dd, table);
    tl.createTable(dd, "test_table", "TestId");
    ColumnData columnData2 = new ColumnData(table, "Name", "VARCHAR(50) DEFAULT NULL");
    ColumnData columnData3 = new ColumnData(table, "Desc", "VARCHAR(50) DEFAULT NULL");
    tl.createColumn(dd, columnData2);
    tl.createColumn(dd, columnData3);
    String indexName = "testindexname";
    String indexColumns = "TestId,Name";
    int indexKind = ColumnData.INDEXKIND_DEFAULT;
    tl.createIndex(dd, table, indexName, indexColumns, indexKind);
    columnData2.setType("VARCHAR(150) DEFAULT NULL");
    columnData3.setType("INTEGER DEFAULT 0");
    ColumnData[] columnData = new ColumnData[2];
    columnData[0] = columnData2;
    columnData[1] = columnData3;
    tl.alterColumn(dd, columnData);
    // does the index restoration work
    boolean actual = tl.doesIndexExist(dd, table, indexName);
    assertEquals(true, actual);
  }

  @Test
  public void testAlterColumnDatabaseDataColumnData1() {
    String table = "test_table";
    tl.dropTable(dd, table);
    tl.createTable(dd, "test_table", "TestId");
    ColumnData columnData2 = new ColumnData(table, "Name", "VARCHAR(50) DEFAULT NULL");
    ColumnData columnData3 = new ColumnData(table, "Desc", "VARCHAR(50) DEFAULT NULL");
    tl.createColumn(dd, columnData2);
    tl.createColumn(dd, columnData3);
    String indexName = "testindexname";
    String indexColumns = "TestId,Name";
    int indexKind = ColumnData.INDEXKIND_DEFAULT;
    tl.createIndex(dd, table, indexName, indexColumns, indexKind);
    columnData2.setType("VARCHAR(150) DEFAULT NULL");
    tl.alterColumn(dd, columnData2);
    ColumnData[] cols = tl.queryColumns(dd, table, null);
    boolean f = false;
    if (cols[1].getSqlType().equals("varchar(150)") == true) {
      f = true;
    } 
    assertEquals(true, f);
  }
  
  @Test
  public void testAlterColumnDatabaseDataColumnData4() {
    String table = "test_table";
    tl.dropTable(dd, table);
    tl.createTable(dd, "test_table", "TestId");
    ColumnData columnData2 = new ColumnData(table, "Name", "VARCHAR(50) DEFAULT NULL");
    ColumnData columnData3 = new ColumnData(table, "Desc", "VARCHAR(50) DEFAULT NULL");
    tl.createColumn(dd, columnData2);
    tl.createColumn(dd, columnData3);
    String indexName = "testindexname";
    String indexColumns = "TestId,Name";
    int indexKind = ColumnData.INDEXKIND_DEFAULT;
    tl.createIndex(dd, table, indexName, indexColumns, indexKind);
    columnData2.setType("VARCHAR(150) DEFAULT NULL");
    tl.alterColumn(dd, columnData2);
    // does the index restoration work
    boolean actual = tl.doesIndexExist(dd, table, indexName);
    assertEquals(true, actual);
  }

  @Test
  public void testShowCreateIndex() {
    String table = "test_table";
    tl.dropTable(dd, table);
    tl.createTable(dd, "test_table", "TestId");
    ColumnData columnData2 = new ColumnData(table, "Name", "VARCHAR(50) DEFAULT NULL");
    ColumnData columnData3 = new ColumnData(table, "Desc", "VARCHAR(50) DEFAULT NULL");
    tl.createColumn(dd, columnData2);
    tl.createColumn(dd, columnData3);
    String indexName = "testindexname";
    String indexColumns = "TestId,Name";
    int indexKind = ColumnData.INDEXKIND_DEFAULT;
    tl.createIndex(dd, table, indexName, indexColumns, indexKind);
    String[] indexes = tl.showCreateIndex(dd, columnData2);
    String actual = indexes[0];
    String expected = "ADD INDEX `testindexname` (`TestId`,`Name`)";
    assertEquals(expected, actual);
  }

  @Test
  public void testGetType() {
    // no need to do this
  }
  
  @Test
  public void testCreateUser() {
    fail("This needs to be finished");
  }
  
  @Test
  public void testDoesUserExist() {
    fail("This needs to be finished");
  }

}

