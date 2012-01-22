package org.gonevertical.dts.v2;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gonevertical.dts.data.ColumnData;
import org.gonevertical.dts.data.DatabaseData;
import org.gonevertical.dts.data.ColumnData.Type;
import org.gonevertical.dts.lib.StringUtil;
import org.gonevertical.dts.lib.datetime.DateTimeParser;
import org.gonevertical.dts.lib.sql.MySqlQueryUtil;
import org.gonevertical.dts.lib.sql.columnlib.ColumnLib;
import org.gonevertical.dts.lib.sql.columnmulti.ColumnLibFactory;
import org.gonevertical.dts.lib.sql.querylib.QueryLib;
import org.gonevertical.dts.lib.sql.querymulti.QueryLibFactory;
import org.gonevertical.dts.lib.sql.transformlib.TransformLib;
import org.gonevertical.dts.lib.sql.transformmulti.TransformLibFactory;

/**
 * 
 * TODO - delete duplicates - copy from v1
 * TODO - delete empty columns - copy from v1
 * 
 * @author BDonnelson
 * 
 */
public class Optimise {
	
  private static final Logger log = LoggerFactory.getLogger(Optimise.class);

  //supporting libraries
  private QueryLib ql = null;
  private TransformLib tl = null;
  private ColumnLib cl = null;
  
  private DestinationData destinationData = null;
  
  // used to examine the value if its a date, and it can transform it
  private DateTimeParser dtp = new DateTimeParser();
  
  private ColumnData[] columnData = null;

  // resize these columns
  private ArrayList<ColumnData> alterColumns = null;
  
  // when examining the column values, watch the values types
  private Type fieldType = Type.TEXT;
  
  // decimal lengths
  private int deca = 0;
  private int decb = 0;
  
  private boolean discoverToTable = false;

	private int totalThreadCount = 6;

	private String where;
  
  public Optimise(DestinationData destinationData) {
    this.destinationData = destinationData;
    setSupportingLibraries();
  }
  
  /**
   * guice injects the libraries needed for the database
   */
  private void setSupportingLibraries() {
    // get query library
    ql = QueryLibFactory.getLib(destinationData.databaseData.getDatabaseType());
    
    // get column library
    cl = ColumnLibFactory.getLib(destinationData.databaseData.getDatabaseType());
    
    // get tranformation library
    tl = TransformLibFactory.getLib(destinationData.databaseData.getDatabaseType());
  }
  
  /**
   * optimize all the columns
   */
  public void run() {
    
    alterColumns = new ArrayList<ColumnData>();
    
    String where = "`Field` NOT LIKE 'Auto_%'"; // all columns except auto columns
    columnData = tl.queryColumns(destinationData.databaseData, destinationData.table, where);
    if (columnData == null) {
      log.info("no columns to optimise, Exiting");
      return;
    }
    
    markColumnsThatAreIdents();
    
    processDiscovery();
    
    alterColumns();
  }
    
  /**
   * optimise only these columns
   * 
   * @param columnData
   */
  public void run(ColumnData[] columnData) {
    alterColumns = new ArrayList<ColumnData>();
    
    if (columnData == null) {
      return;
    }
    this.columnData = columnData; 
    
    markColumnsThatAreIdents();
    
    processDiscovery();
    
    alterColumns();
  }
  
  private void markColumnsThatAreIdents() {
    if (destinationData.identityColumns == null) {
      return;
    }
    for (int i=0; i < columnData.length; i++) {
      for (int b=0; b < destinationData.identityColumns.length; b++) {
        if (columnData[i].getColumnName().toLowerCase().equals(destinationData.identityColumns[b].destinationField) == true) {
          columnData[i].setIdentity(true);
        }
      }
    }
  }
  
  /**
   * discover the column types and save them in a tmp table to deal with later
   */
  public void discoverColumnTypes() {
    discoverToTable = true;
    alterColumns = new ArrayList<ColumnData>();
    
    String where = "`Field` NOT LIKE 'Auto_%'"; // all columns except auto columns
    columnData = tl.queryColumns(destinationData.databaseData, destinationData.table, where);
    if (columnData == null) {
      log.info("no columns to optimise, exiting");
      return;
    }
    
    markColumnsThatAreIdents();
    
    createTmpDiscoverTable();
    
    processDiscovery();
  }
  
  /**
   * analyze columns to go smaller
   */
  private void processDiscovery() {
    for (int i=0; i < columnData.length; i++) {
      checkColumn(columnData[i]);
    }
  }
  
  /**
   * check column
   * 
   * @param columnData
   */
  private void checkColumn(ColumnData columnData) {
    
    if (columnData == null) {
      return;
    }
    
    // skip auto_ and primary key
    if (columnData.getColumnName().matches("Auto_.*") == true) {
      return;
    }
    
    if (columnData.getIsPrimaryKey() == true) {
      return;
    }
    
    // only analyze text columns
    if (destinationData.optimise_TextOnlyColumnTypes == true && 
        columnData.getSqlType().toLowerCase().contains("text") == false) {
      log.trace("checkColumn(): skipping b/c destinationData.optimise_TextOnlyColumnTypes=true and columnData.getType().toLowerCase().contains(\"text\") = false");
      return;
    }
      
    // examine for the best field type (column type) int, decimal, text varchar.
    analyzeColumnType(columnData);
    
    // get character max length in column
    int maxCharLength = getMaxLength(columnData);
    
    String newColumnType = getColumnType(columnData, maxCharLength);
    
    // did type change, then alter
    boolean changed = didItChange(columnData, newColumnType);
    
    // TODO - if changing to a datetime, need to transform all values in the column to datetime
    
    if (changed == true) {
      alter(columnData, newColumnType);
    }
    
    if (discoverToTable == true) {
      saveToTmpTable(columnData, newColumnType, maxCharLength, changed, fieldType, deca, decb);
    }
  }
  
  private void saveToTmpTable(ColumnData columnData, String newColumnType,
      int maxCharLength, boolean changed, Type fieldType, int deca, int decb) {
    
    String tmptable = destinationData.table + "_auto_discover";
    String sql = "INSERT INTO " + tmptable + " SET " +
    		"DateCreated=NOW(), " +
    		"Column_Name='" + columnData.getColumnName() + "', " +
    		"Column_Len='" + columnData.getCharLength() + "', " +
    		"ColumnType_New='" + newColumnType + "', " +
    		"MaxCharLen='" + maxCharLength + "', " +
    		"FieldType='" + fieldType.value() + "', " +
    		"DecA='" + deca + "', " +
    		"DecB='" + decb + "';";
    ql.update(destinationData.databaseData, sql);
  }

  private void createTmpDiscoverTable() {
    String tmptable = destinationData.table + "_auto_discover";
    tl.createTable(destinationData.databaseData, tmptable, "Id");
    
    ColumnData c0 = new ColumnData(tmptable, "DateCreated", "DATETIME");
    ColumnData c1 = new ColumnData(tmptable, "Column_Name", "VARCHAR(50)");
    ColumnData c2 = new ColumnData(tmptable, "Column_Len", "INTEGER");
    ColumnData c3 = new ColumnData(tmptable, "ColumnType_New", "VARCHAR(100)");
    ColumnData c4 = new ColumnData(tmptable, "MaxCharLen", "INTEGER");
    ColumnData c5 = new ColumnData(tmptable, "FieldType", "INTEGER");
    ColumnData c6 = new ColumnData(tmptable, "DecA", "INTEGER");
    ColumnData c7 = new ColumnData(tmptable, "DecB", "INTEGER");
    tl.createColumn(destinationData.databaseData, c0);
    tl.createColumn(destinationData.databaseData, c1);
    tl.createColumn(destinationData.databaseData, c2);
    tl.createColumn(destinationData.databaseData, c3);
    tl.createColumn(destinationData.databaseData, c4);
    tl.createColumn(destinationData.databaseData, c5);
    tl.createColumn(destinationData.databaseData, c6);
    tl.createColumn(destinationData.databaseData, c7);
  }
  
  private int getMaxLength(ColumnData columnData) {
    String sql = cl.getSql_GetMaxCharLength(destinationData.databaseData, columnData);
    log.info("checking column length: " + sql);
    int maxCharLength = ql.queryInteger(destinationData.databaseData, sql);
    return maxCharLength;
  }
  
  public void alterExplicit(ColumnData columnData, String columnType) {
    alterColumns = new ArrayList<ColumnData>();
    
    alter(columnData, columnType);
    
    alterColumns();
  }
  
  private void alter(ColumnData columnData, String columnType) {
    
    boolean isPrimKey = tl.queryIsColumnPrimarykey(destinationData.databaseData, columnData);
    if (isPrimKey == true) {
      destinationData.debug("alter(): skipping altering primary key: " + columnData.getColumnName());
      return;
    }
    
    // when altering dates, make sure every value is transformed to
    if (columnType.toLowerCase().contains("datetime") == true) {
      formatColumn_ToDateTime(columnData);
    } else if (columnType.toLowerCase().contains("int") == true | columnType.toLowerCase().contains("dec") == true) {
      formatColumn_ToInt(columnData);
    }
    
    columnData.setType(columnType);
    
    // be sure that we won't truncate when altering to certian types like int and date
    setBlanksToNull(columnData);
    
    // store it
    alterColumns.add(columnData);
  }

  private boolean didItChange(ColumnData columnData, String newColumnType) {
    boolean b = true;
    
    String orgColumnType = columnData.getSqlType();
    String orgType = StringUtil.getValue("(.*?\\))", orgColumnType);
    String newType = StringUtil.getValue("(.*?\\))", newColumnType);
    
    if (orgType == null) {
      orgType = orgColumnType;
    }
    
    if (newType == null) {
      newType = newColumnType;
    }
    
    if (orgType.toLowerCase().equals(newType.toLowerCase()) == true) {
      b = false;
    }
    
    return b;
  }
  
  /**
   * sample the columns values and see what type is the best
   * 
   * @param columnData
   */
  private void analyzeColumnType(ColumnData columnData) {
    
    if (columnData.getSqlType().contains("text") == false) {
      log.info("Optimise.analyzeColumnType(): Type already defined in columnData, skipping and going with column definition.");
      return;
    }
    
    fieldType = null;  
    deca = 0;
    decb = 0;
    
    String random = "";
    if (destinationData.optimise_RecordsToExamine > 0 && destinationData.optimise_skipRandomExamine == false) {
        random = "ORDER BY RAND()";
    }
    
    // TODO - move this into library
    String c = "";
    if (destinationData.getDatabaseData().getDatabaseType() == DatabaseData.TYPE_MYSQL) {
      c = "`" + columnData.getColumnName() + "`";
    } else if (destinationData.getDatabaseData().getDatabaseType() == DatabaseData.TYPE_MSSQL)  {
      c = "[" + columnData.getColumnName() + "]";
    } else {
      c = "" + columnData.getColumnName() + "";
    }
    
    // sample values that aren't null
    String ignoreNullValues = "";
    if (destinationData.optimise_ignoreNullFieldsWhenExamining == true) {
      ignoreNullValues = "WHERE (" + c + " IS NOT NULL)";
    }

    // column query
    String sql = "SELECT " + c + " " + 
        "FROM " + destinationData.databaseData.getDatabase() + "." + columnData.getTable() + " " +
        "" + ignoreNullValues + " " + random + " " + getLimitQuery() + ";"; 
      
    log.info("Analyzing Column For Type: " + c + " query: " + sql);

    Connection conn = null;
    Statement select = null;
    try { 
      conn = destinationData.databaseData.getConnection();
      select = conn.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY);
      select.setFetchSize(Integer.MIN_VALUE);
      ResultSet result = select.executeQuery(sql);
      int i = 0;
      while (result.next()) {
        examineField(result.getString(1));
        i++;
      }
      select.close();
      select = null;
      result.close();
      result = null;
      conn.close();
      conn = null;
    } catch (SQLException e) {
      System.err.println("SQL Statement Error:" + sql);
      e.printStackTrace();
    } finally {
    	select = null;
    	conn = null;
    }
    
  }
  
  /**
   * what is the value type?
   *   I use chain logic here where, the lowest is text/varchar
   * @param s
   */
  private void examineField(String s) {

    // skip when empty
    if (s == null) {
      // don't even analyze field type when nothing
      return;
    }

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

    if (isText == false && isDate == false) {
      isInt = isInt(s);
    }

    if (isInt == true && isDate == false) {
      isZero = isIntZero(s);
    }

    if (isText == false && isDate == false) {
      isDecimal = isDecimal(s);
    }
    
    //isEmpty(s); // this overides the types, need better logic, later...

    if (isDate == true && isText == false && isInt == false && isDecimal == false && 
        fieldType != Type.VARCHAR &&
        fieldType != Type.INTEGER &&
        fieldType != Type.ZEROFILL &&
        fieldType != Type.TEXT &&
        fieldType != Type.DECIMAL) { // date is first b/c it has text in it
      fieldType = Type.DATETIME; 

    } else if (isText == true && fieldType != Type.DATETIME) {
        fieldType = Type.VARCHAR; 

    } else if (isInt == true && isZero == true && 
        fieldType != Type.DATETIME && 
        fieldType != Type.VARCHAR && 
        fieldType != Type.DECIMAL) { // not date, text, decimal
        fieldType = Type.ZEROFILL; // int with zeros infront 0000123

    } else if (isInt == true && 
        fieldType != Type.VARCHAR && 
        fieldType != Type.TEXT && 
        fieldType != Type.DECIMAL &&
        fieldType != Type.ZEROFILL) { // not date,text,decimal
        fieldType = Type.INTEGER;

    } else if (isDecimal == true && 
        fieldType != Type.DATETIME && 
        fieldType != Type.VARCHAR) {
        fieldType = Type.DECIMAL; 

    } else if (isEmpty == true && 
        fieldType != Type.DATETIME && 
        fieldType != Type.VARCHAR && 
        fieldType != Type.ZEROFILL && 
        fieldType != Type.INTEGER && 
        fieldType != Type.DECIMAL) { // has nothing
        fieldType = Type.EMPTY;
      
    } else {
      //fieldType = Type.TEXT;
    }

    // debug chain logic
    destinationData.debug("ThefieldType: " + fieldType + " ForTheValue::: " + s + " isText:"+ isText + " isInt:" + isInt + " isZeroInt:"+isZero + " isDecimal:"+isDecimal);
  }

  private String getLimitQuery() {

    if (destinationData.optimise_RecordsToExamine <= 0) {
      return "";
    }

    int limit = 0;
    if (destinationData.optimise_RecordsToExamine > 0) {
      limit = destinationData.optimise_RecordsToExamine;
    } 

    String sql = "";
    if (limit > 0) {
      sql = " LIMIT 0," + destinationData.optimise_RecordsToExamine + " ";
    }

    return sql;
  }
  
  /**
   * TODO move this to columnlib *********************** 
   * 
   * 
   * @param columnData
   * @param charLength
   * @return
   */
  private String getColumnType(ColumnData columnData, int charLength) {

    if (columnData.getSqlType().contains("text") == false) {
      System.out.println("getColumnType(): column's type is already set. Skipping setting a new one.");
      return columnData.getSqlType();
    }
    
    // can skip discovery of other types
    if (destinationData.skipOptimisingIntDateTimeDecTypeColumns == true) {
      fieldType = Type.VARCHAR;
    }
    
    String columnType = null;
    switch (fieldType) {
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
    	int maxLenth = charLength + decb;
      columnType = "DECIMAL(" + maxLenth + "," + decb + ")"; // not doing this b/c it errors DEFAULT 0.0 when nothing exists ''
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

  /**
   * is the value an integer positive or negative
   * 
   * @param s
   * @return
   */
  private boolean isInt(String s) {
    // take the commas out of the and see if its an int value 
    s = s.replaceAll(",", ""); 
    
    boolean b = false;
    if (s.matches("[\\(]?[0-9]+[\\)]")) {
      b = true;
    } else if (s.matches("[-]?[0-9]+")) {
      b = true;
    }
    return b;
  }

  /**
   * is this int with, starts with zeros
   * 
   * @param s
   * @return
   */
  private boolean isIntZero(String s) {
    // take the commas out of the and see if its an int value 
    s = s.replaceAll(",", ""); 
    
    boolean b = false;
    if (s.matches("[\\(][0-9]+[\\)]") && s.matches("^[\\(]0[0-9]+[\\)]")) { // does it have negative (12345) value
      b = true;
    } else if (s.matches("[0-9]+") && s.matches("^0[0-9]+")) {
      b = true;
    }
    return b;
  }

  /**
   * is this a decimal?
   * 
   * @param s
   * @return
   */
  private boolean isDecimal(String s) {
    // take the commas out of the and see if its an int value 
    s = s.replaceAll(",", ""); 
    
    boolean b = false;
    if (s.matches("^[\\(]\\.\\d+|^[\\(]?\\d+\\.\\d+[\\)]")) { // does it have negative (12345) value
      b = true;
      getDecimalLengths(s);
    } else if (s.matches("^[-]?\\.\\d+|^[-]?\\d+\\.\\d+")) {
      b = true;
      getDecimalLengths(s);
    }
    return b;
  }

  private boolean isDate(String s) {
    boolean b = false;
    b = dtp.getIsDateExplicit(s);
    return b;
  }
  
  private void getDecimalLengths(String s) {
    int l = 0;
    int r = 0;
    if (s.contains(".")) {
      String[] a = s.split("\\.");
      l = a[0].length();
      try {
        r = a[1].length();
      } catch (Exception e) {
        r = 0;
      }
      l = l + r;
    } else {
      l = s.length();
    }

    if (l > deca) {
      deca = l;
    }
    
    if (r > decb) {
      decb = r;
    }
    
    destinationData.debug("decimal: left: " + deca + " right: " + decb + " value: " + s);
  }
  
  private void formatColumn_ToDateTime(ColumnData columnData) {
  	
    setBlanksToNull(columnData);
    
    String sql = "SELECT COUNT(*) AS t FROM `" + destinationData.databaseData.getDatabase() + "`.`" + columnData.getTable() + "`;"; 
    long total = ql.queryLong(destinationData.databaseData, sql);
    
    long index = total;
    
  	long lim = 20000;
  	BigDecimal tp = new BigDecimal(0);
  	if (total > 0) {
  		tp = new BigDecimal(total).divide(new BigDecimal(lim, MathContext.DECIMAL32), MathContext.DECIMAL32).setScale(0, RoundingMode.UP);	
  	} else {
  		tp = new BigDecimal(1);
  	}

  	long offset = 0;
  	long limit = 0;
  	for (int i=0; i < tp.intValue(); i++) {

  		// spawn more than one thread to copy, in order to do this, connection pooling will need to be setup
  		Thread[] threads = new Thread[totalThreadCount ];
  		for (int threadCount=0; threadCount < totalThreadCount; threadCount++) {

  			if (i==0) {
  				offset = 0;
  				limit = lim;
  			} else {
  				offset = ((i + 1 ) * lim) - lim;
  				limit = lim;
  			}

  			// setup object
  			Optimise_FormatColumn formatColumn = new Optimise_FormatColumn();
  			formatColumn.setData(destinationData, Optimise_FormatColumn.FORMAT_DATETIME, columnData, offset, limit, index);

  			threads[threadCount] = new Thread(formatColumn);

  			if (totalThreadCount > 1) {
  				i++;
  			}
  			
  			index = index - lim;
  		}

  		for (int threadCount=0; threadCount < totalThreadCount; threadCount++) {
  			threads[threadCount].start();
  		}

  		// join threads - finish the threads before moving to the next pages
  		for (int threadCount=0; threadCount < totalThreadCount; threadCount++) { 
  			try {
  				threads[0].join();
  			} catch (InterruptedException e) {
  				e.printStackTrace();
  			}
  		}
  	}
    
  }
  
  private void formatColumn_ToInt(ColumnData columnData) {

    setBlanksToNull(columnData);
    
  	String sql = "SELECT COUNT(*) as t FROM `" + destinationData.databaseData.getDatabase() + "`.`" + columnData.getTable() + "`;";
  	long total = ql.queryLong(destinationData.databaseData, sql);

  	long index = total;
  	
  	long lim = 20000;
  	BigDecimal tp = new BigDecimal(0);
  	if (total > 0) {
  		tp = new BigDecimal(total).divide(new BigDecimal(lim, MathContext.DECIMAL32), MathContext.DECIMAL32).setScale(0, RoundingMode.UP);	
  	} else {
  		tp = new BigDecimal(1);
  	}

  	long offset = 0;
  	long limit = 0;
  	for (int i=0; i < tp.intValue(); i++) {

  		if (i==0) {
  			offset = 0;
  			limit = lim;
  		} else {
  			offset = ((i + 1 ) * lim) - lim;
  			limit = lim;
  		}

  		// TODO move this up to class var
  		int totalThreadCount = 6;

  		// spawn more than one thread to copy, in order to do this, connection pooling will need to be setup
  		Thread[] threads = new Thread[totalThreadCount];
  		for (int t=0; t < totalThreadCount; t++) {

  			if (i==0) {
  				offset = 0;
  				limit = lim;
  			} else {
  				offset = ((i + 1 ) * lim) - lim;
  				limit = lim;
  			}

  			// setup object
  			Optimise_FormatColumn formatColumn = new Optimise_FormatColumn();
  			formatColumn.setData(destinationData, Optimise_FormatColumn.FORMAT_INT, columnData, offset, limit, index);

  			threads[t] = new Thread(formatColumn);

  			if (totalThreadCount > 1) {
  				i++;
  			}
  			
  			index = index - lim;
  		}

  		for (int t=0; t < totalThreadCount; t++) {
  			threads[t].start();
  		}

  		// join threads - finish the threads before moving to the next pages
  		for (int t=0; t < totalThreadCount; t++) { 
  			try {
  				threads[0].join();
  			} catch (InterruptedException e) {
  				e.printStackTrace();
  				log.error("Optimise.formatColumn_ToInt(): Thread Join Error: ", e);
  			}
  		}
  	}
  	
  }
  
  /**
   * TODO move this to the library so each db is different
   * 
   * @param columnData
   */
  private void setBlanksToNull(ColumnData columnData) {

    // TODO - move this into library
    String c = "";
    if (destinationData.getDatabaseData().getDatabaseType() == DatabaseData.TYPE_MYSQL) {
      c = "`" + columnData.getColumnName() + "`";
    } else if (destinationData.getDatabaseData().getDatabaseType() == DatabaseData.TYPE_MSSQL)  {
      c = "[" + columnData.getColumnName() + "]";
    } else {
      c = "" + columnData.getColumnName() + "";
    }
    
    String sql = "UPDATE `" + destinationData.databaseData.getDatabase() + "`.`" + columnData.getTable() + "` " +
    		"SET " + c + "=NULL WHERE " + c + "='';";
    System.out.println(sql);
    ql.update(destinationData.databaseData, sql);
  }

  private void alterColumns() {
    if (alterColumns.size() == 0) {
      return;
    }
    
    ColumnData[] columns = new ColumnData[alterColumns.size()];
    alterColumns.toArray(columns);
    
    tl.alterColumn(destinationData.databaseData, columns);
  }
  
  /**
   * how many duplicates are in table?
   * 
   * @return
   */
  public long getTableHasDuplicates() {
    
    // get total record count for table
    long tc = getTableRecordCount();
    
    // check distinct count for identities
    long tdc = getTableDistinctIdentCount();
    
    long r = tc - tdc;
    
    log.info("tableRowCount=" + tc + " tableDistinctCount=" + tdc + " Difference: " + r);
    
    return r;
  }
  
  private long getTableRecordCount() {
  	
    String sqlWhere = "";
    if (where != null) {
    	sqlWhere = where;
    }
    
    String sql = "SELECT COUNT(*) AS t " +
    		"FROM " + destinationData.databaseData.getDatabase() + "." + destinationData.table + " " + sqlWhere + ";";
    log.info(sql);
    return ql.queryLong(destinationData.databaseData, sql);
  }
  
  private long getTableDistinctIdentCount() {
    
    if (destinationData.identityColumns == null) {
      return 0;
    }
    
    String sqlWhere = "";
    if (where != null) {
    	sqlWhere = where;
    }
    
    // get ident columns
    String idents_Columns = getIdentitiesColumns_inCsv();
    
    String sql = "SELECT DISTINCT " + idents_Columns + " FROM " + 
      destinationData.databaseData.getDatabase() + "." + destinationData.table + " " + sqlWhere + ";"; 

    log.info(sql);
    
    long c = 0;
    try {
      Connection conn = destinationData.databaseData.getConnection();
      Statement select = conn.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY);
      select.setFetchSize(Integer.MIN_VALUE);
      ResultSet result = select.executeQuery(sql);
      while (result.next()) {
      	c++;
      }
      select.close();
      select = null;
      result.close();
      result = null;
      conn.close();
      conn = null;
    } catch (SQLException e) {
      e.printStackTrace();
      log.error("Optimise.getTableDistinctIdentCount(): Error:", e);
    }
    return c;
  }
  
  private String getIdentitiesColumns_inCsv() {
    if (destinationData.identityColumns == null) {
      return "";
    }
    String columns = "";
    for (int i = 0; i < destinationData.identityColumns.length; i++) {
      columns += destinationData.identityColumns[i].destinationField;
      if (i < destinationData.identityColumns.length - 1) {
        columns += ",";
      }
    }
    
    return columns;
  }
  
  public void deleteDuplicates(String where) {
  	this.where = where;
  	deleteDuplicates();
  }
  
  /**
   * delete duplicates based on the identities set
   */
  public void deleteDuplicates() {
    
    long c = getTableHasDuplicates();
    
    if (c == 0) {
     log.info("No duplicates exist for the identities.");
     return;
    }
    
    String sqlWhere = "";
    if (where != null) {
    	sqlWhere = where;
    }
    
    String idents_Columns = getIdentitiesColumns_inCsv();
    
    // load the records that indicate they there duplicates
    String sql = "SELECT " + destinationData.primaryKeyName + " FROM " + 
    	destinationData.databaseData.getDatabase() + "." + destinationData.table + " " +
    	sqlWhere +
    	"GROUP BY "+ idents_Columns + " HAVING count(*) > 1;"; 

    log.info(sql);
    
    try {
      Connection conn = destinationData.databaseData.getConnection();
      Statement select = conn.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY);
      select.setFetchSize(Integer.MIN_VALUE);
      ResultSet result = select.executeQuery(sql);
      int index = 0; //ql.getResultSetSize(result); TODO change this, b/c its not supported in foward read
      while (result.next()) {
        processDuplicate(index, result.getInt(1));
        index--;
      }
      select.close();
      select = null;
      result.close();
      result = null;
      conn.close();
      conn = null;
    } catch (SQLException e) {
      System.err.println("Mysql Statement Error:" + sql);
      e.printStackTrace();
      log.error("deleteDuplicates():", e);
    }
  }
  
  /**
   * delete duplicate records
   * 
   * @param index
   * @param uniqueId
   */
  private void processDuplicate(int index, int uniqueId) {
    
    String idents_Columns = getIdentitiesColumns_inCsv();
    String where = "WHERE " + destinationData.primaryKeyName + "='" + uniqueId + "'";
    
    String sql = "SELECT "+ idents_Columns + " FROM " + 
      destinationData.databaseData.getDatabase() + "." + destinationData.table + " " + where; 

    log.info(sql);
    
    try {
      Connection conn = destinationData.databaseData.getConnection();
      Statement select = conn.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY);
      select.setFetchSize(Integer.MIN_VALUE);
      ResultSet result = select.executeQuery(sql);
      while (result.next()) {
        String[] values = new String[destinationData.identityColumns.length];
        for (int i=0; i < destinationData.identityColumns.length; i++) {
          values[i] = result.getString(i+1);
        }
        deleteDuplicate(values);
      }
      select.close();
      select = null;
      result.close();
      result = null;
      conn.close();
      conn = null;
    } catch (SQLException e) {
      System.err.println("Mysql Statement Error:" + sql);
      e.printStackTrace();
      log.error("Optimise.processDuplicate() Error:", e);
    }
  }

  private void deleteDuplicate(String[] identValues) {
    
    String where = "";
    for (int i=0; i < destinationData.identityColumns.length; i++) {
      where += "" + destinationData.identityColumns[i].destinationField + "='" + identValues[i] + "'";
      if (i < destinationData.identityColumns.length-1) {
        where += " AND ";
      }
    }
    
    String sql = "SELECT " + destinationData.primaryKeyName + " FROM " + 
    destinationData.databaseData.getDatabase() + "." + destinationData.table + " WHERE " + where; 
 
    log.info(sql);
    
    try {
      Connection conn = destinationData.databaseData.getConnection();
      Statement select = conn.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, 
          java.sql.ResultSet.CONCUR_READ_ONLY);
      select.setFetchSize(Integer.MIN_VALUE);
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
      select = null;
      result.close();
      result = null;
      conn.close();
      conn = null;
    } catch (SQLException e) {
      System.err.println("Mysql Statement Error:" + sql);
      e.printStackTrace();
      log.error("Optimise.deleteDuplicates(): Error:", e);
    }
    
  }
  
  private void deleteRecord(int uniqueId) {
    
    String where = "" + destinationData.primaryKeyName + "='" + uniqueId + "'";
    
    String sql = "DELETE FROM " + destinationData.databaseData.getDatabase() + "." + destinationData.table + " WHERE " + where; 
    
    log.info(sql);
    
    ql.update(destinationData.databaseData, sql);
  }
  
}
