package org.gonevertical.dts.lib.sql.columnlib;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gonevertical.dts.data.ColumnData;
import org.gonevertical.dts.data.ColumnDataComparator;
import org.gonevertical.dts.data.DatabaseData;
import org.gonevertical.dts.lib.sql.querylib.MySqlQueryLib;
import org.gonevertical.dts.lib.sql.transformlib.MySqlTransformLib;

public class OracleColumnLib implements ColumnLib {
	
  private static final Logger log = LoggerFactory.getLogger(OracleColumnLib.class);

  public OracleColumnLib() {
  }
  
  /**
   * get column values from sql resultSet
   * 
   * @param result
   * @param columnData
   * @return
   */
  public ColumnData[] getResult(ResultSet result, ColumnData[] columnData) {
    return getResult(result, columnData, null);
  }

  /**
   * get column values from sql resultSet 
   * 
   * @param result
   * @param columnData
   * @param pruneColumnData
   * @return
   */
  public ColumnData[] getResult(ResultSet result, ColumnData[] columnData, ColumnData[] pruneColumnData) {
    if (columnData == null | result == null) {
      return null;
    }
    columnData = prune(columnData, pruneColumnData);
    for (int i=0; i < columnData.length; i++) {
      String value = null;
      try {
        value = result.getString(columnData[i].getColumnName());
      } catch (SQLException e) {
        e.printStackTrace();
      }
      columnData[i].setValue(value);
    }
    return columnData;
  }

  /**
   * get sql select statement string from columnData like cola,colb,colc,...
   * 
   * @param columnData
   * @return
   */
  public String getSql_Names(ColumnData[] columnData) {
    return getSql_Names(columnData, null);
  }

  /**
   * get sql select statement string from columnData like cola,colb,colc
   *  
   * @param columnData
   * @param pruneColumnData
   * @return
   */
  public String getSql_Names(ColumnData[] columnData, ColumnData[] pruneColumnData) {
    if (columnData == null) {
      return null;
    }
    columnData = prune(columnData, pruneColumnData);
    String sql = "";
    for (int i=0; i < columnData.length; i++) {
      sql += "" + columnData[i].getColumnName() + "";
      if (i < columnData.length -1) {
        sql += ",";
      }
    }
    return sql;
  }

  public String getSql_Names_WSql(ColumnData[] columnData, ColumnData[] pruneColumnData) {
    if (columnData == null) {
      return null;
    }
    columnData = prune(columnData, pruneColumnData);

    String sql = "";
    for (int i=0; i < columnData.length; i++) {
      String c = "";
      String cn_sql =  columnData[i].getColumnAsSql();
      if (cn_sql != null) {
        c = cn_sql;
      } else {
        c = "" + columnData[i].getColumnName() + "";
      }
      sql += c;
      if (i < columnData.length -1) {
        sql += ",";
      }
    }
    return sql;
  }

  /**
   * get column names in csv format "a","b","c"
   * 
   * @param columnData
   * @return
   */
  public String getCsv_Names(ColumnData[] columnData) {
    return getCsv_Names(columnData, null);
  }

  /**
   * get column names in csv format "a","b","c"
   * 
   * @param columnData
   * @param pruneColumnData
   * @return
   */
  public String getCsv_Names(ColumnData[] columnData, ColumnData[] pruneColumnData) {
    if (columnData == null) {
      return null;
    }
    columnData = prune(columnData, pruneColumnData);
    String sql = "";
    for (int i=0; i < columnData.length; i++) {
      sql += "\"" + columnData[i].getColumnName() + "\"";
      if (i < columnData.length -1) {
        sql += ",";
      }
    }
    return sql;
  }

  /**
   * get columnData values as csv, "value1", "value2", "value3",...
   * 
   * @param columnData
   * @return
   */
  public String getCsv_Values(ColumnData[] columnData) {
    return getCsv_Values(columnData, null);
  }

  /**
   * get columnData values as csv, "value1", "value2", "value3",...
   * 
   * @param columnData
   * @return
   */
  public String getCsv_Values(ColumnData[] columnData, ColumnData[] pruneColumnData) {
    if (columnData == null) {
      return null;
    }
    columnData = prune(columnData, pruneColumnData);
    String sql = "";
    for (int i=0; i < columnData.length; i++) {
      String v = columnData[i].getValue();
      if (v == null) {
        v = "";
      }
      sql += "\"" + v + "\"";
      if (i < columnData.length -1) {
        sql += ",";
      }
    }
    return sql;
  }

  /**
   * get columns as sql, column1='value1', column2='value2', column3='value3',...
   * 
   * @param columnData
   * @return
   */
  public String getSql(ColumnData[] columnData) {
    return getSql(columnData, null);
  }

  /**
   * get columns as sql, column1='value1', column2='value2', column3='value3',...
   * 
   * @param columnData
   * @param pruneColumnData
   * @return
   */
  public String getSql(ColumnData[] columnData, ColumnData[] pruneColumnData) {
    if (columnData == null) {
      return "";
    }

    // get distinct columns, otherwise two of the same will fail
    columnData = getColumns_Distinct(columnData);

    columnData = prune(columnData, pruneColumnData);
    String sql = "";
    for (int i=0; i < columnData.length; i++) {
      String c = "" + columnData[i].getColumnName() + "";    
      String v = null;
      if (columnData[i].isFunctionSetForValue() == true) {
        v = columnData[i].getValueAsFunction();
      } else {
        if (columnData[i].getValue() == null) {
          v = "NULL";
        } else {
          String s = new MySqlQueryLib().escape(columnData[i].getValue());
          v = "'" + s + "'";
        }
      }

      sql += c + "=" + v;
      if (i < columnData.length -1) {
        sql += ",";
      }
    }
    return sql;
  }

  /**
   * get Columns as Sql Insert Statement
   * 
   * @param columnData
   * @return
   */
  public String getSql_Insert(ColumnData[] columnData) {
    return getSql_Insert(columnData, null);
  }

  /**
   * get Columns as Sql Insert Statement
   * 
   *   REMEMBER - in most cases primary key will need to be pruned, I decided to keep it out in case i wanted it
   *   columnData = prunePrimaryKey(columnData); This should be done by earlier method
   * @param columnData
   * @param pruneColumnData
   * @return
   */
  public String getSql_Insert(ColumnData[] columnData, ColumnData[] pruneColumnData) {
    if (columnData == null) {
      return "";
    }
    columnData = prune(columnData, pruneColumnData);
    String table = columnData[0].getTable();
    String fields = getSql(columnData);
    String sql = "INSERT INTO " + table + " SET " + fields + ";";
    return sql;
  }

  /**
   * get update sql statement
   * 
   * @param columnData
   * @param primaryKeyId
   * @return
   */
  public String getSql_Update(ColumnData[] columnData) {
    return getSql_Update(columnData, null);
  }

  /**
   * get update sql statement
   * 
   * @param columnData
   * @param primaryKeyId
   * @param pruneColumnData
   * @return
   */
  public String getSql_Update(ColumnData[] columnData, ColumnData[] pruneColumnData) {
    if (columnData == null) {
      return null;
    }
    ColumnData priKeyCol = getPrimaryKey_ColumnData(columnData);
    String where = " WHERE " + priKeyCol.getColumnName() + "='" + priKeyCol.getValue() + "'";

    String sql = "UPDATE " + columnData[0].getTable() + " SET ";
    pruneColumnData = merge(pruneColumnData, priKeyCol);
    sql += getSql(columnData, pruneColumnData);
    sql += where;

    return sql;
  }

  /**
   * get sql for calculating the max characters length of a column
   * 
   * @param dd
   * @param columnData
   * @return
   */
  public String getSql_GetMaxCharLength(DatabaseData dd, ColumnData columnData) {
    if (columnData == null) {
      return null;
    }
    String sql = "SELECT MAX(LENGTH(" + columnData.getColumnName() + ")) " +
    "FROM " + dd.getDatabase() + "." + columnData.getTable() + "" ;
    return sql;
  }

  /**
   * prune columns
   * 
   * @param columnData
   * @param pruneColumnData
   * @return
   */
  public ColumnData[] prune(ColumnData[] columnData, ColumnData[] pruneColumnData) {
    if (pruneColumnData == null) {
      return columnData;
    }
    ArrayList<ColumnData> cols = new ArrayList<ColumnData>();

    // loop through columns
    for (int i=0; i < columnData.length; i++) {

      if (doesColumnExist(pruneColumnData, columnData[i]) == true) {
        // don't add it if we find it
      } else {
        cols.add(columnData[i]);
      }

    }

    ColumnData[] r = new ColumnData[cols.size()];
    cols.toArray(r);
    return r;
  }

  /**
   * prune PrimaryKey from columnData
   * 
   * @param columnData
   * @return
   */
  public ColumnData[] prunePrimaryKey(ColumnData[] columnData) {
    if (columnData == null) {
      return null;
    }
    ArrayList<ColumnData> newCols = new ArrayList<ColumnData>();
    for (int i=0; i < columnData.length; i++) {
      if (columnData[i].getIsPrimaryKey() == false) {
        newCols.add(columnData[i]);
      }
    }
    ColumnData[] r = new ColumnData[newCols.size()];
    r = (ColumnData[]) newCols.toArray(r);
    return r;
  }

  /**
   * does column exist?
   * 
   * @param searchColumnData - look in these columns
   * @param forColumnData - comparing this name to pruneColumnData
   * 
   * @return
   */
  public boolean doesColumnExist(ColumnData[] searchColumnData, ColumnData forColumnData) {
    Comparator<ColumnData> sort = new ColumnDataComparator(ColumnDataComparator.NAME);
    Arrays.sort(searchColumnData, sort);
    int index = Arrays.binarySearch(searchColumnData, forColumnData, sort);
    boolean b = false;
    if (index >= 0) {
      b = true;
    }
    return b;
  }

  /**
   * find column by name not using comparator
   * 
   * @param searchColumnData
   * @param forColumnData
   * @return
   */
  public int searchColumnByName_NonComp(ColumnData[] searchColumnData, ColumnData forColumnData) {
    int index = -1;
    for (int i=0; i < searchColumnData.length; i++) {
      if (searchColumnData[i].getColumnName().toLowerCase().equals(forColumnData.getColumnName().toLowerCase()) == true) {
        index = i;
        break;
      }
    }
    return index;
  }

  /**
   * find column by name using comparator
   * 
   * @param searchColumnData
   * @param forColumnData
   * @return
   */
  public int searchColumnByName_UsingComparator(ColumnData[] searchColumnData, ColumnData forColumnData) {
    Comparator<ColumnData> sort = new ColumnDataComparator(ColumnDataComparator.NAME);
    Arrays.sort(searchColumnData, sort);
    int index = Arrays.binarySearch(searchColumnData, forColumnData, sort);
    return index;
  }

  public int searchColumnByName_UsingComparator(ColumnData[] searchColumnData, String columnName) {
    ColumnData forColumnData = new ColumnData();
    forColumnData.setColumnName(columnName);
    int index = searchColumnByName_UsingComparator(searchColumnData, forColumnData);
    return index;
  }

  /**
   * does column name exist?
   * 
   * @param searchColumnData
   * @param forColumnName
   * @return
   */
  public boolean doesColumnNameExist(ColumnData[] searchColumnData, String forColumnName) {
    if (searchColumnData == null | forColumnName == null) {
      return false;
    }
    boolean b = false;
    for (int i=0; i < searchColumnData.length; i++) {
      if (searchColumnData[i].getColumnName().equals(forColumnName)) {
        b = true;
        break;
      }
    }
    return b;
  }

  /**
   * get the value of the primary key
   * 
   * @param columnData
   * @return
   */
  public String getPrimaryKey_Value(ColumnData[] columnData) {
    int indexPrimKey = getPrimaryKey_Index(columnData);
    String value = null;
    if (indexPrimKey > -1) {
      value = columnData[indexPrimKey].getValue();
    }
    return value;
  }

  /**
   * find primary key column name
   * 
   * @param columnData
   * @return
   */
  public String getPrimaryKey_Name(ColumnData[] columnData) {
    if (columnData == null) {
      return null;
    }
    String s = "";
    for (int i=0; i < columnData.length; i++) {
      if (columnData[i].getIsPrimaryKey() == true) {
        s = columnData[i].getColumnName();
        break;
      }
    }
    return s;
  }

  /**
   * get the index of primary key 
   * 
   * @param columnData
   * @return
   */
  public int getPrimaryKey_Index(ColumnData[] columnData) {
    int f = -1;
    for (int i=0; i < columnData.length; i++) {
      if (columnData[i].getIsPrimaryKey() == true) {
        f = i;
        break;
      }
    }
    return f;
  }

  /**
   * get the primary column object
   * 
   * @param columnData
   * @return
   */
  public ColumnData getPrimaryKey_ColumnData(ColumnData[] columnData) {
    // TODO - do I need to sort first? or should I sort first, b/c this wouldn't work if primary key is not listed in order
    int index = getPrimaryKey_Index(columnData);
    ColumnData r = null;
    if (index > -1) {
      r = columnData[index];
    }
    return r;
  }

  /**
   * add values into the column Data
   * 
   * @param columnData
   * @param values
   * @return
   */
  public ColumnData[] addValues(ColumnData[] columnData, String[] values) {
    if (columnData == null) {
      return null;
    }
    for (int i=0; i < columnData.length; i++) {
      if (i < values.length) {
        columnData[i].setValue(values[i]);
      } else {
        String s = null;
        columnData[i].setValue(s);
      }
    }
    return columnData;
  }

  /**
   * get Identitys Where statement 
   * 
   * @param columnData
   * @return
   */
  public String getSql_IdentitiesWhere(ColumnData[] columnData) {

    // get columns used first
    ArrayList<ColumnData> cols = new ArrayList<ColumnData>();
    for(int i=0; i < columnData.length; i++) {
      if (columnData[i].getIdentityUse() == true) {
        cols.add(columnData[i]);
      }
    }

    // create sql where vars
    String sql = "";
    for(int i=0; i < cols.size(); i++) {
      ColumnData col = cols.get(i);
      String c = col.getColumnName();
      String v = col.getValue();
      if (v == null || v.trim().length() == 0) {
        sql += "" + c + " IS NULL ";
      } else {
        v = new MySqlQueryLib().escape(col.getValue());
        sql += "" + c + "='" + v + "'";
      }
      
      if (i < cols.size() - 1) {
        sql += " AND ";
      }
    }

    return sql;
  }

  /**
   * get Sql for identities indexing
   * 
   * TODO - when looking for columns to index make sure they aren't varchar(0)
   * 
   * @param dd
   * @param columnData
   * @return
   */
  public String getSql_IdentitiesIndex(DatabaseData dd, ColumnData[] columnData) {
    if (columnData == null) {
      return null;
    }

    String autoIndexName = "auto_identities";

    boolean exists = new MySqlTransformLib().doesIndexExist(dd, columnData[0].getTable(), autoIndexName);
    if (exists == true) {
      return null;
    }

    // get columns used first
    ArrayList<ColumnData> cols = new ArrayList<ColumnData>();
    for(int i=0; i < columnData.length; i++) {
      if (columnData != null && columnData[i].getIdentityUse() == true) {
        cols.add(columnData[i]);
      }
      
      // limit is 16, TODO -maybe a setting later
      if (i > 16) {
        break;
      }
    }

    if (cols.size() == 0) {
      System.out.println("the identity column name given doesn't exist");
      return null;
    }

    ColumnData[] columns = new ColumnData[cols.size()];
    cols.toArray(columns);

    String sqlColumns = getSql_Index_Multi(columns);

    String sql = "ALTER TABLE " + dd.getDatabase() + "." + columnData[0].getTable() + " " +
    "ADD INDEX " + autoIndexName + "(" + sqlColumns + ")"; 

    return sql;
  }

  /**
   * get column index setup like "smallInt, myTxt(900)"
   * 
   * @param columns
   * @return
   */
  public String getSql_Index_Multi(ColumnData[] columns) {
    int size = 900;
    if (columns.length > 1) {
      size = (int) size / columns.length;
    }
    String sql = "";
    for(int i=0; i < columns.length; i++) {
      String c = columns[i].getColumnName();
      String len = "";
      if (columns[i].getSqlType().toLowerCase().contains("text") == true) {
        len = "(" + size + ")";
      }
      sql += "" + c + "" + len;
      if (i < columns.length - 1) {
        sql += ",";
      }
    }
    return sql;
  }

  /**
   * get just the columns that are identities
   * @param columnData
   * @return
   */
  public ColumnData[] getColumns_Identities(ColumnData[] columnData) {
    ArrayList<ColumnData> cols = new ArrayList<ColumnData>();
    for(int i=0; i < columnData.length; i++) {
      if (columnData[i].getIdentityUse() == true) {
        cols.add(columnData[i]);
      }
    }
    ColumnData[] r = new ColumnData[cols.size()];
    cols.toArray(r);
    return r;
  }

  /**
   * add object arrays
   * 
   * @param columnData
   * @param addColumnData
   * @return
   */
  public ColumnData[] merge(ColumnData[] columnData, ColumnData[] addColumnData) {
    ArrayList<ColumnData> cols = new ArrayList<ColumnData>();
    for(int i=0; i < columnData.length; i++) {
      cols.add(columnData[i]);
    }
    for(int i=0; i < addColumnData.length; i++) {
      cols.add(addColumnData[i]);
    }
    ColumnData[] r = new ColumnData[cols.size()];
    cols.toArray(r);
    return r;
  }
  
  public ColumnData[] difference(ColumnData[] columnData_Left, ColumnData[] columnData_Right) {
  	
  	ArrayList<ColumnData> cols = new ArrayList<ColumnData>();
  	
  	for(int i=0; i < columnData_Left.length; i++) {
  		ColumnData forColumnData = columnData_Left[i];
  		int index = searchColumnByName_NonComp(columnData_Right, forColumnData);
  		if (index < 0) {
  			cols.add(columnData_Left[i]);
  		}
  	}
  	
  	ColumnData[] r = new ColumnData[cols.size()];
    cols.toArray(r);
    return r;
  }

  /**
   * add column into object array
   * @param columnData
   * @param addColumnData
   * @return
   */
  public ColumnData[] merge(ColumnData[] columnData, ColumnData addColumnData) {
    ArrayList<ColumnData> cols = new ArrayList<ColumnData>();
    if (columnData != null) {
      for(int i=0; i < columnData.length; i++) {
        cols.add(columnData[i]);
      }
    }
    cols.add(addColumnData);
    ColumnData[] r = new ColumnData[cols.size()];
    cols.toArray(r);
    return r;
  }

  /**
   * get alter columns sql
   * 
   * @param dd
   * @param columnData
   * @return
   */
  public String getSql_AlterColumns(DatabaseData dd, ColumnData[] columnData) {
    String sql = "ALTER TABLE " + dd.getDatabase() + "." + columnData[0].getTable() + " ";
    sql += getSql_ModifyColumns(columnData) + ";";
    return sql;
  }

  /**
   * get modify column sql
   *   like MODIFY COLUMN Name varchar(100) DEFAULT NULL, MODIFY COLUMN TwoLetter varchar(2)  DEFAULT NULL
   *   
   * @param columnData
   * @return
   */
  public String getSql_ModifyColumns(ColumnData[] columnData) {
    String sql = "";
    for (int i=0; i < columnData.length; i++) {
      sql += "MODIFY COLUMN " + columnData[i].getColumnName() + " " + columnData[i].getSqlType() + " ";
      if (i < columnData.length - 1 ) {
        sql += ",";
      }
    }
    return sql;
  }

  /**
   * get distinct column names
   * 
   * @param columnData
   * @return
   */
  public ColumnData[] getColumns_Distinct(ColumnData[] columnData) {

    Comparator<ColumnData> sort = new ColumnDataComparator(ColumnDataComparator.NAME);

    ArrayList<ColumnData> a = new ArrayList<ColumnData>();
    for (int i=0; i < columnData.length; i++) {
      a.add(columnData[i]);
    }

    Collections.sort(a, sort);

    ArrayList<ColumnData> b = new ArrayList<ColumnData>();

    for (int i=0; i < a.size(); i++) {

      ColumnData key = a.get(i);

      // does a exist in b, if not add
      int index = Collections.binarySearch(b, key, sort);
      if (index < 0) {
        b.add(a.get(i));
      }

      Collections.sort(b, sort);
    }

    ColumnData[] r = new ColumnData[b.size()];
    b.toArray(r);

    return r;
  }

  public String getType() {
    return "MySql";
  }

  /**
   * TODO
   * create sql column syntax for column creation from columnData
   * @param columnData
   * @return
   */
  public String createSqlColumnSyntax(ColumnData columnData) {
    
    int charLength = columnData.getCharLength();
    
    String columnType = null;
    switch (columnData.getType()) {
    case DATETIME: 
      columnType = "DATETIME DEFAULT NULL";
      break;
    case VARCHAR: 
      if (charLength > 255) {
        columnType = "TEXT DEFAULT NULL";
      } else {
        columnType = "VARCHAR(" + charLength + ") DEFAULT NULL";
      }
      break;
    case ZEROFILL:
      if (charLength <= 8) {
        columnType = "INTEGER(" + charLength + ") ZEROFILL  DEFAULT 0"; 
      } else {
        columnType = "BIGINT(" + charLength + ") ZEROFILL DEFAULT 0"; 
      }      
      break;
    case INTEGER:
      if (charLength <= 2) {
        columnType = "TINYINT"; // DEFAULT 0
      } else if (charLength <= 8) {
        columnType = "INTEGER"; // DEFAULT 0
      } else if (charLength >= 20) { // why am I getting truncation error for 20 bytes?
        columnType = "VARCHAR(" + charLength + ") DEFAULT NULL";
      } else {
        columnType = "BIGINT"; // DEFAULT 0
      }
      break;
    case DECIMAL:
      int left = columnData.getDecimalPositionLeft();
      int right = columnData.getDecimalPositionRight();
      columnType = "DECIMAL(" + left + "," + right + ")"; // not doing this b/c it errors DEFAULT 0.0 when nothing exists ''
      break;
    case EMPTY:
      columnType = "CHAR(0)";
      break;
    case TEXT: 
      if (charLength > 255) {
        columnType = "TEXT DEFAULT NULL";
      } else {
        columnType = "VARCHAR(" + charLength + ") DEFAULT NULL";
      }
      break;
    default:
      if (charLength > 255) {
        columnType = "TEXT DEFAULT NULL";
      } else {
        columnType = "VARCHAR(" + charLength + ") DEFAULT NULL";
      }
      break;
    }

    return columnType;
  }
}
