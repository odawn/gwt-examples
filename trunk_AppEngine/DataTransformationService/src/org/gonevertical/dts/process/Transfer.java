package org.gonevertical.dts.process;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gonevertical.dts.data.ColumnData;
import org.gonevertical.dts.data.DatabaseData;
import org.gonevertical.dts.data.FieldData;
import org.gonevertical.dts.lib.SqlUtil;
import org.gonevertical.dts.lib.sql.columnlib.ColumnLib;
import org.gonevertical.dts.lib.sql.columnmulti.ColumnLibFactory;
import org.gonevertical.dts.lib.sql.querylib.QueryLib;
import org.gonevertical.dts.lib.sql.querymulti.QueryLibFactory;
import org.gonevertical.dts.lib.sql.transformlib.TransformLib;
import org.gonevertical.dts.lib.sql.transformmulti.TransformLibFactory;


/**
 * transfer data from one table to another
 * 
 * TODO - do i want to add multple idents as the solution of transfering from source with no autoincrement?
 * TODO - how to transfer from source with no auto increment - also set idents on those that it uses
 *        partially done
 * TODO - auto build the identity index when using identities as keys
 * TODO - when testing if data exists and the count comes back more than 1, purge duplicates
 * TODO - write test for identity use
 * TODO - map using columndata and get rid of field data in here.
 * TODO - when transfering to a new auto created tables, and using by identities, need to create table first, b/c where query errors
 * 
 * oracle and microsoft sql need to be able to page with rownumbering
 * 
 * @author design
 *
 */
public class Transfer implements Runnable, Cloneable {
	
  private static final Logger log = LoggerFactory.getLogger(Transfer.class);

  // what kind of transfer process is going on
  public static final int MODE_TRANSFER_ALL = 1;
  public static final int MODE_TRANSFER_ONLY = 2;
  public static final int MODE_MASH = 3; // mash to it self
  private int mode = 0;
  
  //supporting libraries
  private QueryLib ql_src = null;
  private TransformLib tl_src = null;
  private ColumnLib cl_src = null;
  private QueryLib ql_des = null;
  private TransformLib tl_des = null;
  private ColumnLib cl_des = null;
  
  // source database settings
  private DatabaseData database_src = null;
  
  // destination database settings
  private DatabaseData database_des = null;
  
  // transfering from table to table
  private String tableLeft = null;
  private String tableRight = null;
  
  // table identity fields established so not to create duplicates
  private FieldData[] identFields = null;

  // table mapped fields from src to data
  private FieldData[] mappedFields = null;

  // table mapped fields for one to many
  private FieldData[] oneToMany = null;
  
  // column data structure which will be the destinations 
  private ColumnData[] columnData_src = null;
  private ColumnData[] columnData_des = null;
  
  private ColumnData[] columnData_src_oneToMany = null;
  private ColumnData[] columnData_des_oneToMany = null;
  
  // pass around during processing of one to many items
  private ColumnData oneToManyTablePrimaryKey = null;
  
  // one to many relationship like userId='1'
  private String oneToMany_RelationshipSql = null;
  
  // hard code values in on a one to many records
  private ArrayList<HashMap<String,String>> hardOneToMany = new ArrayList<HashMap<String,String>>();
  
  // filter your query
  private String srcWhere = null;
  
  // this will skip comparing dest columns - on an update it will update with out overwrite policy
  private boolean compareDestValues = true;
  
  // total count query
  private long total = 0;
  
  // query this many records at a time
  private long limitOffset = 100;
	
  // at index
  private long index = 0;
  

	private boolean purgeAtEnd = false;
	
	/**
	 * what offset is this thread on
	 */
	private long offset;
	
	/**
	 * what limit is this thread on
	 */
	private long limit;
	
	/**
	 * what thread are we on
	 */
	private int threadCount;

	/**
	 * how many threads to spawn to transfer
	 */
	private int totalThreadCount = 1;
	private int offsetIndex;
  private boolean skipPaging;
  private String orderBy;
  
  /**
   * delete source after copy/transfer
   */
	private boolean deleteSource = false;

	// use identities instead of the primary key
	private ColumnData[] columnIdentities;

  private boolean keepPrimaryKey;

  private Calendar purgeAtEnd_Dt;
  
  /**
   * Transfer data object setup 
   * 
   * @param database_src
   * @param database_dest
   */
  public Transfer(DatabaseData database_src, DatabaseData database_dest) {
    this.database_src = database_src;
    this.database_des = database_dest;
    setSupportingLibraries();
  }
  
  /**
   * guice injects the libraries needed for the database
   */
  private void setSupportingLibraries() {
    ql_src = QueryLibFactory.getLib(database_src.getDatabaseType());
    cl_src = ColumnLibFactory.getLib(database_src.getDatabaseType());
    tl_src = TransformLibFactory.getLib(database_src.getDatabaseType());
    ql_des = QueryLibFactory.getLib(database_des.getDatabaseType());
    cl_des = ColumnLibFactory.getLib(database_des.getDatabaseType());
    tl_des = TransformLibFactory.getLib(database_des.getDatabaseType());
  }

  /**
   * set up tables from to
   * 
   * @param fromTable
   * @param toTable
   */
  public void transferAllFields(String fromTable, String toTable) {
    this.mode = MODE_TRANSFER_ALL;
    this.tableLeft = fromTable;
    this.tableRight = toTable;
    start();
  }
  
  public void transferOnlyMappedFields(String fromTable, String toTable, FieldData[] mappedFields) {
    this.mode = MODE_TRANSFER_ONLY;
    this.tableLeft = fromTable;
    this.tableRight = toTable;
    this.mappedFields = mappedFields;
    start();
  }
  
  public void mashSrc(String fromTable, String toTable, FieldData[] mappedFields) {
    this.mode = MODE_MASH;
    this.tableLeft = fromTable;
    this.tableRight = toTable;
    this.mappedFields = mappedFields;
    start();
  }
  
  /**
   * set a custom where query for the source table (no need for WHERE)
   * 
   * @param srcWhere
   */
  public void setWhere(String srcWhere) {
  	if (srcWhere == null) {
  		return;
  	}
  	if (srcWhere.toLowerCase().contains("where")) {
  		srcWhere = srcWhere.replaceAll("(i?)where", "");
  	}
  			
    this.srcWhere = srcWhere;
  }
  
  /**
   * set up tables from to and mapped fields to transfer
   * 
   * @param fromTable
   * @param toTable
   * @param mappedFields
   */
  public void transferOnlyMappedFields(String fromTable, String toTable, FieldData[] mappedFields, FieldData[] oneToMany) {
    this.mode = MODE_TRANSFER_ONLY;
    this.tableLeft = fromTable;
    this.tableRight = toTable;
    this.mappedFields = mappedFields;
    this.oneToMany  = oneToMany;
    start();
  }
  
  /**
   * prep the objects needed
   */
  private void start() {
    
    index = 0;
    
    if (mode == MODE_TRANSFER_ALL) {
      setColumnData_All();
    } else if (mode == MODE_TRANSFER_ONLY) {
      setColumnData();
    } else if (mode == MODE_MASH) {
      setColumnData();
    }
    
    createDestTable();
    
    createColumns();
    
    // if using identities instead of primary key, lets set it up for use
    setIdentityIntoColumnArrays();
    
    if (mode == MODE_TRANSFER_ALL) {
      processSrc();
    } else if (mode == MODE_TRANSFER_ONLY) {
      processSrc();
    } else if (mode == MODE_MASH) {
      processSrc_Mash();
    }
    
    purgeStaleRecordsOnDest();
  }
  
  private void purgeStaleRecordsOnDest() {
    
    if (purgeAtEnd == false) {
      return;
    }
  
    String t = "";
    if (database_des.getDatabaseType() == DatabaseData.TYPE_MSSQL) {
      t += database_des.getDatabase() + ".";
      t += database_des.getTableSchema() + ".";
    }
    t +=  tableRight + " ";
    
    String dt = SqlUtil.getCalendarAsSqlDt(purgeAtEnd_Dt);
    
    String sql = "DELETE FROM " + t + " " +
    		"WHERE Auto_PurgeAtEnd IS NULL OR Auto_PurgeAtEnd < '" + dt + "';";
    ql_des.update(database_des, sql);
  }

  public void showDifferenceInTables(String fromTable, String toTable) {
  	this.tableLeft = fromTable;
    this.tableRight = toTable;
  	
    ColumnData[] left = tl_src.queryColumns(database_src, tableLeft, null);
    ColumnData[] right = tl_des.queryColumns(database_des, tableRight, null);
  	
    ColumnData[] diff = cl_src.difference(left, right);
    
    if (diff == null) {
    	log.info("Transerver.showDifferenceInTables(): Nothing Different");
    	return;
    }
    
    for (int i=0; i < diff.length; i++) {
    	String name = diff[i].getName();
    	log.info("Transerver.showDifferenceInTables(): diff table: " + name);
    }
  }

  /**
   * if dest table doesn't exist create it
   */
  private void createDestTable() {
    String primaryKeyName = cl_src.getPrimaryKey_Name(columnData_src);
    tl_des.createTable(database_des, tableRight, primaryKeyName);
  }
  
  private void createColumns() {
    
    for (int i=0; i < columnData_src.length; i++) {
      tl_des.createColumn(database_des, columnData_des[i]);
    }
    
    if (purgeAtEnd == true) {
      ColumnData c = new ColumnData(tableRight, "Auto_PurgeAtEnd", "DATETIME DEFAULT NULL");
      tl_des.createColumn(database_des, c);
      purgeAtEnd_Dt = Calendar.getInstance();
    }
    
  }
  
  private void setColumnData_All() {
    columnData_src = tl_src.queryColumns(database_src, tableLeft, null);
    
    columnData_des = new ColumnData[columnData_src.length];
    for(int i=0; i < columnData_src.length; i++) {
      columnData_des[i] = new ColumnData();
      columnData_des[i] = (ColumnData) columnData_src[i].clone();
      columnData_des[i].setTable(tableRight);
    }
   
  }
  
  private String getSrcWhere() {
    String sql = "";
    if (srcWhere != null && srcWhere.length() > 0) {
      sql = " AND " + srcWhere;
    }
    return sql; 
  }
  
  /**
   * TODO start with columndata instead of field data, b/c there is more overhead here than I need
   * TODO slim down column data public vars 
   */
  private void setColumnData() {
    
    columnData_src = new ColumnData[mappedFields.length];
    columnData_des = new ColumnData[mappedFields.length];
    for (int i = 0; i < mappedFields.length; i++) {
      columnData_src[i] = new ColumnData();
      columnData_des[i] = new ColumnData();
      
      columnData_src[i].setTable(tableLeft);
      columnData_src[i].setColumnName(mappedFields[i].sourceField);
      columnData_src[i].setIsPrimaryKey(mappedFields[i].isPrimaryKey);
      columnData_src[i].setOverwriteWhenBlank(mappedFields[i].onlyOverwriteBlank);
      columnData_src[i].setOverwriteWhenZero(mappedFields[i].onlyOverwriteZero);
      columnData_src[i].setRegex(mappedFields[i].regexSourceField);
      columnData_src[i].setDeleteExisting(mappedFields[i].deleteExisting);
      
      columnData_des[i].setTable(tableRight);
      columnData_des[i].setColumnName(mappedFields[i].destinationField);
      columnData_des[i].setIsPrimaryKey(mappedFields[i].isPrimaryKey);
      columnData_des[i].setOverwriteWhenBlank(mappedFields[i].onlyOverwriteBlank);
      columnData_des[i].setCase(mappedFields[i].changeCase);
      columnData_des[i].setDeleteExisting(mappedFields[i].deleteExisting);
         
    }

    if (oneToMany != null) {
      columnData_src_oneToMany = new ColumnData[oneToMany.length];
      columnData_des_oneToMany = new ColumnData[oneToMany.length];
      
      for (int i=0; i < oneToMany.length; i++) {
        columnData_src_oneToMany[i] = new ColumnData();
        columnData_des_oneToMany[i] = new ColumnData();
        
        columnData_src_oneToMany[i].setColumnName(oneToMany[i].sourceField);
        columnData_src_oneToMany[i].setOverwriteWhenBlank(oneToMany[i].onlyOverwriteBlank);
        columnData_src_oneToMany[i].setRegex(oneToMany[i].regexSourceField);
        columnData_src_oneToMany[i].setDeleteExisting(oneToMany[i].deleteExisting);
        
        columnData_des_oneToMany[i].setColumnName(oneToMany[i].destinationField);
        columnData_des_oneToMany[i].setOverwriteWhenBlank(oneToMany[i].onlyOverwriteBlank);
        columnData_des_oneToMany[i].setRegex(oneToMany[i].regexSourceField);
        columnData_des_oneToMany[i].setTable(oneToMany[i].differentDestinationTable);
        columnData_des_oneToMany[i].setDeleteExisting(oneToMany[i].deleteExisting);
        
        hardOneToMany.add(oneToMany[i].hardOneToMany);
      }
    }
    
  }
  
  private void processSrc() {
  	
  	setTotal();
    
  	if (skipPaging == false) {
  	  loopThroughPages();
  	} else {
  	  loop();
  	}
  }
  
  /**
   * dont thread or page through source record set, 
   *   for now oracle and ms copies should use this, until row num process is worked out
   */
  private void loop() {
    
    processSrc_WithOutPaging();
    
  }

  private void setTotal() {
  	String where = "";
  	if (srcWhere != null && srcWhere.length() > 0) {
  		where = " WHERE " + srcWhere;
  	}
  	
  	String db = database_src.getDbCatalogAndTable();
  	
    String sql = "SELECT COUNT(*) AS t FROM " + db + "." + tableLeft + " " + where;
    log.info("Transfer.setTotal(): " + sql);
    
    total = ql_src.queryLong(database_src, sql);
    index = total;
  }
  
  private void loopThroughPages() {
    
    long lim = limitOffset;
    BigDecimal tp = new BigDecimal(0);
    if (total > 0) {
    	tp = new BigDecimal(total).divide(new BigDecimal(lim, MathContext.DECIMAL32), MathContext.DECIMAL32).setScale(0, RoundingMode.UP);	
    } else {
    	tp = new BigDecimal(1);
    }
    
    long offset = 0;
    long limit = 0;
    for (int i=0; i <= tp.intValue()+1; i++) {
    
    	// spawn more than one thread to copy, in order to do this, connection pooling will need to be setup
    	Thread[] threads = new Thread[totalThreadCount];
    	for (int threadCount=0; threadCount < totalThreadCount; threadCount++) {
    	
        if (i==0) {
          offset = 0;
          limit = lim;
        } else {
          offset = ((i + 1 ) * lim) - lim;
          limit = lim;
        }
               
        // setup thread
        Transfer transfer = (Transfer) this.clone();
        transfer.setThread(threadCount);
        transfer.setProcessSrc(offset, limit);
        threads[threadCount] = new Thread(transfer);
       
        // update count down
        index = index - limitOffset;
        
        // move offset in with more than one thread
        if (totalThreadCount > 1 &&  threadCount < totalThreadCount-1) {
        	i++;
        }
    	}
    	
    	for (int threadCount=0; threadCount < totalThreadCount; threadCount++) {
    		 threads[threadCount].start();
    	}
      
    	// join threads - finish the threads before moving to the next pages
    	for (int threadCount=0; threadCount < totalThreadCount; threadCount++) { 
        try {
	        threads[threadCount].join();
        } catch (InterruptedException e) {
	        log.error("error", e);
        }
    	}
    	
    } // end of loop through pages
    
  }
  
  private void setProcessSrc(long offset, long limit) {
  	this.offset = offset;
  	this.limit = limit;
  }
  
  public void run() {
  	log.info("Start of thread " + threadCount + " offset: " + offset + " limit: " + limit);
  	
  	processSrcForPaging(offset, limit);
  }

	private void setThread(int threadCount) {
	  this.threadCount = threadCount;
  }
	
	/**
	 * process source with out paging - one thread only
	 * 
	 * @param offset
	 * @param limit
	 */
	private void processSrc_WithOutPaging() {

    ColumnData primKey = cl_src.getPrimaryKey_ColumnData(columnData_src);
    String where = "";
    // TODO - oracle seems to be type safe in its where, or its not the way to write sql this way for oracle
    // TODO - can have multiple primary keys (with zero autoincrement)
    if (primKey != null) {
      //where = "WHERE " + primKey.getColumnName() + " != '' AND " + primKey.getColumnName() + " IS NOT NULL";
    }
    
    String columnCsv = cl_src.getSql_Names_WSql(columnData_src, null);
    
    String columnCsv2 = "";
    if (columnData_src_oneToMany != null) {
      columnCsv2 = cl_src.getSql_Names_WSql(columnData_src_oneToMany, null);
      
      if (columnCsv2.length() > 0) {
        columnCsv2 = "," + columnCsv2;
      }
    }
    
    String sql = "";
    
    // Microsoft/Oracle paging sucks real bad. Why don't they have a built in function, duh!
    if (database_src.getDatabaseType() == DatabaseData.TYPE_MSSQL) {
      //sql += "SELECT * FROM ( ";
    }
    
    sql += "SELECT ";
    
    if (database_src.getDatabaseType() == DatabaseData.TYPE_MSSQL) {
      sql += "(ROW_NUMBER() OVER(ORDER BY " + primKey.getColumnName() + ")) AS Auto_RowNum, ";
    }
    
    sql += " " + columnCsv + " " + columnCsv2 + " ";
    sql += " FROM ";
    
    if (database_src.getDatabaseType() == DatabaseData.TYPE_MSSQL) {
      sql += database_src.getDatabase() + ".";
      sql += database_src.getTableSchema() + ".";
    }
    
    sql +=  tableLeft + " ";
    
    sql += where;
    sql += getSrcWhere();
    sql += getOrderBy();
    
    log.info("Transfer.processSrc_WithOutPaging(): " + sql);
    
    
    Connection conn = null;
    Statement select = null;
    try {
      conn = database_src.getConnection();
      select = conn.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY);
      select.setFetchSize(5000);
      ResultSet result = select.executeQuery(sql);
      while (result.next()) {

        // get values 
        for (int i=0; i < columnData_src.length; i++) {
          String value = result.getString(columnData_src[i].getColumnName());
          columnData_src[i].setValue(value);
        }
        
        // one to many relationships processing
        if (columnData_src_oneToMany != null && columnData_src_oneToMany.length > 0) {
          for (int i=0; i < columnData_src_oneToMany.length; i++) {
            String value = result.getString(columnData_src_oneToMany[i].getColumnName());
            columnData_src_oneToMany[i].setValue(value);
          }
        }

        process();
      }
      result.close();
      select.close();
      conn.close();
      result = null;
      select = null;
      conn = null;
    } catch (SQLException e) {
      log.error("error", e);
    } finally {
      conn = null;
      select = null;
    }
  }
  

	private void processSrcForPaging(long offset, long limit) {

    ColumnData primKey = cl_src.getPrimaryKey_ColumnData(columnData_src);
    String where = "";
    if (primKey != null) {
      where = "WHERE " + primKey.getColumnName() + " != '' AND " + primKey.getColumnName() + " IS NOT NULL";
    }
    
    String columnCsv = cl_src.getSql_Names_WSql(columnData_src, null);
    
    String columnCsv2 = "";
    if (columnData_src_oneToMany != null) {
      columnCsv2 = cl_src.getSql_Names_WSql(columnData_src_oneToMany, null);
      
      if (columnCsv2.length() > 0) {
        columnCsv2 = "," + columnCsv2;
      }
    }
    
    String sql = "";
    
    // Microsoft/Oracle paging sucks real bad. Why don't they have a built in function, duh!
    if (database_src.getDatabaseType() == DatabaseData.TYPE_MSSQL) {
    	sql += "SELECT * FROM ( ";
    }
    
    sql += "SELECT ";
    
    if (database_src.getDatabaseType() == DatabaseData.TYPE_MSSQL) {
    	sql += "(ROW_NUMBER() OVER(ORDER BY " + primKey.getColumnName() + ")) AS Auto_RowNum, ";
    }
    
    sql += " " + columnCsv + " " + columnCsv2 + " ";
    sql += " FROM ";
    
    if (database_src.getDatabaseType() == DatabaseData.TYPE_MSSQL) {
    	sql += database_src.getDatabase() + ".";
    	sql += database_src.getTableSchema() + ".";
    }
    sql += database_src.getDatabase() + "." + tableLeft + " ";
    
    sql += where;
    sql += getSrcWhere();
    sql += getOrderBy();
    
    if (database_src.getDatabaseType() == DatabaseData.TYPE_MYSQL) {
    	sql += " LIMIT " + offset + ", " + limit + ";";
    }
    
    if (database_src.getDatabaseType() == DatabaseData.TYPE_MSSQL) {
    	sql += " ) AS TableWithRows WHERE TableWithRows.Auto_RowNum >= " + offset + " AND TableWithRows.Auto_RowNum <= " + (offset + limit) + " ";
    }
    
    if (database_src.getDatabaseType() == DatabaseData.TYPE_ORACLE) {
      long end = (long) (offset + limit);
      sql += " AND ROWNUM >= " + offset + " AND ROWNUM <= " + end + "";
    }
    
    //if (database_src.getDatabaseType() == DatabaseData.TYPE_MSSQL) {
      //sql = sql.replaceAll("", "");
    //}
    
    //log.debug("Thread: " + threadCount + " sql: " + sql);
    System.out.println(sql);
    
    Connection conn = null;
    Statement select = null;
    offsetIndex = 0; // watch the offset loop
    try {
      conn = database_src.getConnection();
      select = conn.createStatement();
      ResultSet result = select.executeQuery(sql);
      while (result.next()) {

        // get values 
        for (int i=0; i < columnData_src.length; i++) {
          String value = result.getString(columnData_src[i].getColumnName());
          columnData_src[i].setValue(value);
          log.trace("\t\t Thread. " + threadCount + " "  + columnData_src[i].getColumnName() + "=" + value);
        }
        
        // one to many relationships processing
        if (columnData_src_oneToMany != null && columnData_src_oneToMany.length > 0) {
          for (int i=0; i < columnData_src_oneToMany.length; i++) {
            String value = result.getString(columnData_src_oneToMany[i].getColumnName());
            columnData_src_oneToMany[i].setValue(value);
          }
        }

        process();
        
        offsetIndex++; // use this to debug loop
      }
      result.close();
      select.close();
      conn.close();
      result = null;
      select = null;
      conn = null;
    } catch (SQLException e) {
    	e.printStackTrace();
      log.error("Error " + sql, e);
    } finally {
      conn = null;
      select = null;
    }
  }
  
  private void processSrc_Mash() {

  	String where = "";
  	if (srcWhere != null && srcWhere.length() > 0) {
  		where = " WHERE " + srcWhere;
  	}
  	
    String sql = "SELECT COUNT(*) AS t FROM " + tableLeft + "" + where;
    log.info("Transfer.processSrc_Mash(): " + sql);
    
    total = ql_src.queryLong(database_src, sql);
    index = total;
    
    long lim = limitOffset;
    BigDecimal tp = new BigDecimal(0);
    if (total > 0) {
    	tp = new BigDecimal(total).divide(new BigDecimal(lim)).setScale(0, RoundingMode.UP);	
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
      
      processSrc_Mash(offset, limit);
      
      log.trace("Transfer.processSrc_Mash(): offset: " + offset + " limit: " + limit);
    }
    
  }
  
  private void processSrc_Mash(long offset, long limit) {

    ColumnData primKey = cl_des.getPrimaryKey_ColumnData(columnData_des);
    String where = "WHERE " + primKey.getColumnName() + " != '' AND " + primKey.getColumnName() + " IS NOT NULL";
    
    ColumnData keyDes = cl_des.getPrimaryKey_ColumnData(columnData_des);
    
    String sql = "";
    sql = "SELECT " + keyDes.getColumnName() + " FROM " + tableRight + " ";
    sql += where;
    sql += getSrcWhere();
    sql += getOrderBy();
    sql += " LIMIT " + offset + ", " + limit + ";";
    
    log.info("Transfer.processSrc_Mash(): " + sql);
    
    Connection conn = null;
    Statement select = null;
    try {
      conn = database_des.getConnection();
      select = conn.createStatement();
      ResultSet result = select.executeQuery(sql);
      while (result.next()) {
        String getKeyValue = result.getString(1);
        processSrc_Mash(getKeyValue);
      }
      result.close();
      select.close();
      conn.close();
      result = null;
      select = null;
      conn = null;
    } catch (SQLException e) {
      log.error("Tansfer.Processrc_Mash(): Error: ", e);
    }
  }
  
  private String getOrderBy() {
    String sql = "";
    if (orderBy != null) {
    	sql = orderBy;
    }
    return sql;
  }

  private void processSrc_Mash(String keyValueDes) {

    String columnCsv = cl_src.getSql_Names_WSql(columnData_src, null);
    
    ColumnData keySrc = cl_src.getPrimaryKey_ColumnData(columnData_src);
    //ColumnData keyDes = ColumnData.getPrimaryKey_ColumnData(columnData_des);
    
    String sql = "";
    sql = "SELECT " + columnCsv + " FROM " + tableLeft + " ";
    sql += "WHERE " + keySrc.getColumnName() + " = '" + keyValueDes + "' ";
    sql += getSrcWhere();
    sql += getOrderBy();
    
    log.info("Transfer.processSrc_Mash(): " + sql);
    
    Connection conn = null;
    Statement select = null;
    try {
      conn = database_src.getConnection();
      select = conn.createStatement();
      ResultSet result = select.executeQuery(sql);
      while (result.next()) {

        // get values 
        for (int i=0; i < columnData_src.length; i++) {
          String value = result.getString(columnData_src[i].getColumnName());
          columnData_src[i].setValue(value);
        }
        
        // one to many relationships processing
        if (columnData_src_oneToMany != null && columnData_src_oneToMany.length > 0) {
          for (int i=0; i < columnData_src_oneToMany.length; i++) {
            String value = result.getString(columnData_src_oneToMany[i].getColumnName());
            columnData_src_oneToMany[i].setValue(value);
          }
        }

        process();
      }
      result.close();
      select.close();
      conn.close();
      result = null;
      select = null;
      conn = null;
    } catch (SQLException e) {
      log.error("error", e);
    }
  }
  
  /**
   * compare for overwrite checking
   */
  private void process() {
    
    // compare src values to the dst values, for overwrite policy
    getDestinationValuesForComparison();
    
    if (columnData_src_oneToMany != null && columnData_src_oneToMany.length > 0) {
      getDestinationValuesToCompareWith_OneToMany(columnData_src_oneToMany, columnData_des_oneToMany);
    }
    
    // merge src values to destination 
    merge();
    
    if (columnData_src_oneToMany != null && columnData_src_oneToMany.length > 0) {
      merge_OneToMany();
    }
           
    save();
    
    // save one to many
    if (columnData_src_oneToMany != null && columnData_src_oneToMany.length > 0) {
      saveOneToMany();
    }
  }
  
  private void saveOneToMany() {
    
   // does the value already exist?
   for (int i=0; i < columnData_des_oneToMany.length; i++) {
     
     long onetoId = getOneToManyId(columnData_des_oneToMany[i], hardOneToMany.get(i));
     
     saveOneToMany(onetoId, columnData_des_oneToMany[i], hardOneToMany.get(i));
     
   }
    
  }
  
  private void saveOneToMany(long onetoId, ColumnData columnData, HashMap<String,String> hardOneToMany) {
    
    String hardFields = getFields_OneToMany_Hard(hardOneToMany, 1);
    
    String datafields = getFields_OneToMany(columnData);
    if (datafields == null) {
      log.trace("Transfer.saveOneToMany(): no one to many value to insert.");
      return;
    }
    
    if (hardFields.length() > 0) {
      datafields += ", " + hardFields;
    }
    
    String sql = "";
    if (onetoId > 0) { // update
      
      datafields += ",DateUpdated=NOW()";
      String where = "(" + oneToManyTablePrimaryKey.getColumnName() + "='" + onetoId + "')";
      sql = "UPDATE " + columnData.getTable() + " SET " + datafields + " WHERE " + where;
      
    } else { // insert
      
      datafields += ",DateCreated=NOW()";
      sql = "INSERT INTO " + columnData.getTable() + " SET " + datafields;
      
    }
    log.info("Transfer.saveOneToMany(): " + sql);
    ql_des.update(database_des, sql);
  }

  private long getOneToManyId(ColumnData columnData, HashMap<String,String> hardOneToMany) {
    
    // get primary key
    oneToManyTablePrimaryKey = tl_des.queryPrimaryKey(database_des, columnData.getTable());
    
    String where = getOneToManySqlWhere(columnData, hardOneToMany);

    String sql = "SELECT " + oneToManyTablePrimaryKey.getColumnName() + " " +
    		"FROM " + columnData.getTable() + " WHERE " + where;
    
    log.trace("Transfer.getOneToManyId(): " + sql);
    
    long id = 0;
    Connection conn = null;
    Statement select = null;
    int size = 0;
    try {
      conn = database_des.getConnection();
      select = conn.createStatement();
      ResultSet result = select.executeQuery(sql);
      size = ql_des.getResultSetSize(result);
      while (result.next()) {
        id = result.getLong(1);
      }
      select.close();
      select = null;
      result.close();
      result = null;
      conn.close();
    } catch (SQLException e) {
      log.error("error", e);
    } finally {
      conn = null;
      select = null;
    }
    
    if (size > 1) {
    	deleteOneToMany(columnData.getTable(), where);
    	id = 0;
    } else {
    	//nothing
    }
    
    return id;
  }
  
  private void deleteOneToMany(String table, String where) {
  	String sql = "DELETE FROM " + table + " WHERE " + where;
  	log.trace("Transfer.deleteOneToMany(): Deleting duplicate one to many");
	  ql_des.update(database_des, sql);
  }

	// TODO - this is MYSQL specific - make it not specific using guice
  private String getOneToManySqlWhere(ColumnData columnData, HashMap<String,String> hardOneToMany) {

    String whereHard = "";
    if (hardOneToMany.isEmpty() == false) {
      whereHard = " AND " + getFields_OneToMany_Hard(hardOneToMany, 2);
    }
    
    String valueField = "";
    if ((columnData.getValue() == null || columnData.getValue().length() == 0) || columnData.getDeleteExisting() == true) {
    	valueField = "";
    } else {
    	valueField = " AND (" + columnData.getColumnName()+"='" + ql_src.escape(columnData.getValue()) + "')";
    }
    
    String where = getSql_PrimaryKeysWhere() + " " +  valueField  + " " + whereHard;
 
    return where;
  }

  /**
   * save data to dest table
   * 
   * TODO ignore columns - cl_des.doesColumnNameExist(columnData_des, "DateCreated")
   */
  private void save() {
    
    // does this data exist in dest table, (can have multile keys)
    long count = doIdentsExistAlready(database_des);
    
    String fields = getFields();
    
    
    if (fields == null || fields.trim().length() == 0) {
      //log.info("Transfer.save(): skipping save(). " +
      //		"probably b/c we only have a primary key and/or one to many is the only thing being used. " +
      //		"Its possible your only moving one to many records. Ignore this if only moving one to many.");
      return;
    }
    
    String sql = "";
    if (count > 0) { // update
    	String where = getSql_WhereQueryIfDataExists();
      sql = "UPDATE " + tableRight + " SET " + fields + " WHERE " + where; 
      
    } else { // insert
      sql = "INSERT INTO " + tableRight + " SET " + fields;
    }

    testColumnValueSizes(columnData_des);
    
    log.info(index + " Transfer.save(): " + sql);
    
    ql_des.update(database_des, sql, false);
    
    deleteSourceRecord();
    
    index--;
  }
  
  private void deleteSourceRecord() {
  	
  	if (deleteSource == false) {
  		return;
  	}
	  
  	String where = getSql_ForPrimaryKeys_ForSrc();
  	
  	String sql = "DELETE FROM " + tableLeft + " WHERE " + where;
  	log.trace("Transfer.deleteSourceRecord(): " + sql);
  	ql_src.update(database_src, sql);
  	
  }

	private void testColumnValueSizes(ColumnData[] columnData) {
    for (int i=0; i < columnData.length; i++) {
      columnData[i].alterColumnSizeBiggerIfNeedBe(database_des);
    }
  }
  
  private String getFields() {
    
    ColumnData[] cols = null;
    if (keepPrimaryKey == false) {
      cols = cl_des.prunePrimaryKey(columnData_des);
    } else {
      cols = columnData_des;
    }
  	
  	String sql = "";
    for (int i=0; i < cols.length; i++) {
      String column = cols[i].getColumnName();
      String value = cols[i].getValue();
      
      if (value != null && value.trim().length() == 0) {
        value = null;
      }
      
      String svalue = "";
      if (value == null) {
        svalue = "NULL";
      } else {
        svalue = "'" +  ql_src.escape(value) + "'";
      }
      
      sql += "" + column + "=" + svalue;
      if (i < cols.length -1) {
        sql += ",";
      }
    }
    
    if (purgeAtEnd == true) {
      String dt = SqlUtil.getCalendarAsSqlDt(purgeAtEnd_Dt);
      sql += ",Auto_PurgeAtEnd='" + dt + "'";
    }
    
    return sql;
  }
  
  private String getFields_OneToMany(ColumnData columnData) {
    
    String sql = "";
    
    // one to many relationship defined into the hash table like userId=3134
    sql += oneToMany_RelationshipSql + ", ";
    
    // data fields
    String column = columnData.getColumnName();
    String value = columnData.getValue();
    if (value == null || value.length() == 0) {
    	sql += "" + column + "=NULL";	
    } else {
    	sql += "" + column + "='" +  ql_src.escape(value) + "'";	
    }
    

    // don't insert blank data - need to insert null, b/c this is considered a value, if it where to erase a previous value
    //if (value == null || value.length() == 0) {
      //sql = null;
    //}
    
    return sql;
  }
  
  private String getFields_OneToMany_Hard(HashMap<String, String> hardOneToMany, int internaltype) {
    String sep = "";
    if (internaltype == 1) {
      sep = ",";
    } else if (internaltype == 2) {
      sep = " AND ";
    }
    
    String sql = "";
    Iterator iterator = (hardOneToMany.keySet()).iterator();
    int i=0;
    while (iterator.hasNext()) {
      String key = iterator.next().toString();
      String value = hardOneToMany.get(key).toString();
      if (i > 0) {
        sql += sep;
      }
      sql += "" + key + "='" + ql_src.escape(value) + "'";
      i++;
    }// end of while
    return sql;
  }
  
  /**
   * get dest values for comparison agianst the source data
   */
  private void getDestinationValuesForComparison() {

    //if (oneToMany_RelationshipSql == null && oneToMany != null) {
      setOneToManySqlRelationship();
    //}
    
    // this will skip comparing dest values, saving time, and just moving the data to dest regardless of overwrite policy
    if (compareDestValues == false) {
        for (int i=0; i < columnData_des.length; i++) {
          String s = null;
          columnData_des[i].setValue(s);
        }
      return;
    }
    
    // TODO do I need to keep getting the keys, or save them in class var
    // TODO asumming that the primary key is the same
    String srcPrimKeyValue = cl_src.getPrimaryKey_Value(columnData_src);
    String desPrimKeyColName = cl_des.getPrimaryKey_Name(columnData_des);
    
    if (srcPrimKeyValue == null || srcPrimKeyValue.length() == 0) {
      log.warn("Transfer.getDestinationValuesForComparison(): How come there is no primary key???");
    }
    
    String sql = "SELECT * FROM " + tableRight + " WHERE " +
      "(" + desPrimKeyColName + "='" +  ql_src.escape(srcPrimKeyValue) + "')";
    
    log.trace("getDestinationValuesForComparison(): " + sql);
    
    boolean b = false;
    Connection conn = null;
    Statement select = null;
    try {
      conn = database_des.getConnection();
      select = conn.createStatement();
      ResultSet result = select.executeQuery(sql);
      while (result.next()) {

        for (int i=0; i < columnData_des.length; i++) {
          String value = result.getString(columnData_des[i].getColumnName());
          columnData_des[i].setValue(value);
          b = true;
        }
        
      }
      result.close();
      select.close();
      conn.close();
      result = null;
      select = null;
      conn = null;
    } catch (SQLException e) {
      e.printStackTrace();
      log.error("error", e);
    }
    
    if (b == false) {
      for (int i=0; i < columnData_des.length; i++) {
        String s = null;
        columnData_des[i].setValue(s);
      }
    }
  }
  
  private void setOneToManySqlRelationship() {
    String srcPrimKeyValue = cl_src.getPrimaryKey_Value(columnData_src);
    String desPrimKeyColName = cl_des.getPrimaryKey_Name(columnData_des);
    oneToMany_RelationshipSql = "" + desPrimKeyColName + "='" +  ql_src.escape(srcPrimKeyValue) + "'"; 
  }
  
  private void getDestinationValuesToCompareWith_OneToMany(ColumnData[] src, ColumnData[] des) {
    
    // TODO - is the primary different in one to many table?
    String srcPrimKeyValue = cl_src.getPrimaryKey_Value(columnData_src);
    String desPrimKeyColName = cl_des.getPrimaryKey_Name(columnData_des);
    String where = "(" + desPrimKeyColName + "='" +  ql_src.escape(srcPrimKeyValue) + "')";
    
    for (int i=0; i < src.length; i++) {
      getDestinationValuesToCompareWith_OneToMany(where, src[i], des[i]);
    }
  }

  private void getDestinationValuesToCompareWith_OneToMany(String where, ColumnData src, ColumnData des) {
  
    String sql = "SELECT * FROM " + des.getTable() + " WHERE " + where + ";";
    log.trace("Transfer.getDestinationValuesToCompareWith_OneToMany(): " + sql);
    
    boolean b = false;
    Connection conn = null;
    Statement select = null;
    try {
      conn = database_des.getConnection();
      select = conn.createStatement();
      ResultSet result = select.executeQuery(sql);
      while (result.next()) {
        String value = result.getString(des.getColumnName());
        des.setValue(value);
        b = true;
      }
      result.close();
      select.close();
      conn.close();
      result = null;
      select = null;
      conn = null;
    } catch (SQLException e) {
      log.error("error", e);
    }
    
    if (b == false) {
      String s = null;
      des.setValue(s);
    }
    
  }

  /**
   * Test if the data exists in the dst table? 
   * 
   *   TODO - purge duplicates when find count > 1, this will happen when using identities, delete from dst table
   * 
   * @param databaseData
   * @return
   */
  private long doIdentsExistAlready(DatabaseData databaseData) {
    
  	String sql = "Select COUNT(*) as t " +
    		"FROM " + databaseData.getDatabase() + "." + tableRight + " " +
    		"WHERE " + getSql_WhereQueryIfDataExists();
    
    log.trace("Transfer.doIdentsExistAlready(): " + sql);
    
    long count = ql_des.queryLong(database_des, sql);
    return count;
  }
  
  /**
   * get where query for keys
   * @return
   */
  private String getSql_WhereQueryIfDataExists() {
    
    String sqlWhere = "";
    if (columnIdentities != null) { // use sql identity to find data
    	sqlWhere = getSql_WhereForIdents();
    	
    } else { // use primary key to find data
    	
      int is = 0;
      for (int i=0; i < columnData_des.length; i++) {
        if (columnData_des[i].getIsPrimaryKey() == true) {
          
          if (is >= 1) {
            sqlWhere += " AND ";
          }
          
          sqlWhere += "(" + columnData_des[i].getColumnName() + "='" + ql_des.escape(columnData_des[i].getValue()) + "')";
          
          is++;
        }
      }
      
    }
    
    return sqlWhere;
  }
  
  /**
   * get sql query for src ident
   * 
   * @return
   */
  private String getSql_ForPrimaryKeys_ForSrc() {
    
    String sql = "";
    int is = 0;
    for (int i=0; i < columnData_src.length; i++) {
      if (columnData_src[i].getIsPrimaryKey() == true) {
        
        if (is >= 1) {
          sql += " AND ";
        }
        
        sql += "(" + columnData_src[i].getColumnName() + "='" + ql_src.escape(columnData_src[i].getValue()) + "')";
        
        is++;
      }
    }
    
    return sql;
  }
  
  @Deprecated
  private String getSql_PrimaryKeysWhere() {
    String srcPrimKeyValue = cl_src.getPrimaryKey_Value(columnData_src);
    
    String desPrimKeyColName = cl_des.getPrimaryKey_Name(columnData_des);
    
    String where = "(" + desPrimKeyColName + "='" +  ql_src.escape(srcPrimKeyValue) + "')";
    
    return where;
  }
  
  private void merge() {
    
    for (int i=0; i < columnData_src.length; i++) {
      
      boolean onlyOverwriteBlank = columnData_des[i].getOverwriteWhenBlank();
      boolean onlyOverwriteZero = columnData_des[i].getOverwriteWhenZero();
      
      String desValue = columnData_des[i].getValue();
      
      // TODO get rid of this - needs testing
      if (desValue == null) {
        desValue = "";
      }
      
      log.trace("TEST: overwriteBlanOnly: " + onlyOverwriteBlank + " srcValue: " + columnData_src[i].getValue() + " des: " + columnData_des[i].getValue());
      
      // overwrite dest policy defined here
      if ( 
          (onlyOverwriteBlank == true && (desValue == null | desValue.length() == 0)) | 
          (onlyOverwriteZero == true && (desValue == null | desValue.length() == 0 | desValue.equals("0"))) 
         ) { // only overwrite when dest values are blank
        columnData_des[i].setValue(columnData_src[i].getValue());
        
      } else if (onlyOverwriteBlank == false | onlyOverwriteZero == false) { // always overwrite the des
        columnData_des[i].setValue(columnData_src[i].getValue());
      } else {
      	columnData_des[i].setValue(columnData_des[i].getValue());
      }
      
    }
   
  }
  
  private void merge_OneToMany() {
    
    // TODO - not sure if I need a merge
    // TODO - what no values exist on the other end?
    
    for (int i=0; i < columnData_src_oneToMany.length; i++) {
      
    	columnData_des_oneToMany[i].setDeleteExisting(columnData_src_oneToMany[i].getDeleteExisting());
      boolean onlyOverwriteBlank = columnData_des_oneToMany[i].getOverwriteWhenBlank();
      boolean onlyOverwriteZero = columnData_des_oneToMany[i].getOverwriteWhenZero();

      
      String desValue = columnData_des_oneToMany[i].getValue();
      
      if (desValue == null) {
        desValue = "";
      }
      /* this won't apply here for now, b/c I am just looking to see if the value exists in the table if not write.
      if ( (onlyOverwriteBlank == true && (desValue.equals("null") | desValue.length() == 0)) | 
          (onlyOverwriteZero == true && (desValue.equals("null") | desValue.length() == 0 | desValue.equals("0"))) ) { // write when blank
        columnData_des_oneToMany[i].setValue(columnData_src_oneToMany[i].getValue());
      } 
      */
      columnData_des_oneToMany[i].setValue(columnData_src_oneToMany[i].getValue());
    }
   
   
  }

  public void setCompareDestValues(boolean b) {
    this.compareDestValues = b;
  }
  
  public void setOffsetLimit(long limitOffset) {
  	this.limitOffset = limitOffset;
  }

	/**
	 * set how many threads to spawn - Warning, this should only be used with connection pooling
	 *   NOTE: will probably crash connection due to max connections or will hit the file descriptor OS ceiling
	 *   NOTE: netstat -na or ulimit -a
	 * @param totalSpawnThreadCount
	 */
	public void setThreadsToSpawn(int totalSpawnThreadCount) {
	  this.totalThreadCount = totalSpawnThreadCount;
	  
	  //TODO - should I set back to 1 if there is no context setup?
  }

	@Override
	public Object clone() {
		
		Transfer cloned = null;
    try {
	    cloned = (Transfer) super.clone();
    } catch (CloneNotSupportedException e) {
	    log.error("error", e);
    }
    
    // copy object arrays by cloning each object
    ColumnData[] cdSrc = new ColumnData[columnData_src.length];
    for (int i=0; i < columnData_src.length; i++) {
    	cdSrc[i] = (ColumnData) columnData_src[i].clone();
    }
    
    ColumnData[] cdDes = new ColumnData[columnData_des.length];
    for (int i=0; i < columnData_des.length; i++) {
    	cdDes[i] = (ColumnData) columnData_des[i].clone();
    }
    
    // also clone the columndata - transfer of data happens through these, and has to be split into the threads
    cloned.columnData_src = cdSrc;
    cloned.columnData_des = cdDes;
    
    if (columnData_src_oneToMany != null) {
    	ColumnData[] cdSrcOtm = new ColumnData[columnData_src_oneToMany.length];
    	for (int i=0; i < columnData_src_oneToMany.length; i++) {
      	cdSrcOtm[i] = (ColumnData) columnData_src_oneToMany[i].clone();
      }
    	cloned.columnData_src_oneToMany = cdSrcOtm;
    }
    
    if (columnData_des_oneToMany != null) {
    	ColumnData[] cdDesOtm = new ColumnData[columnData_des_oneToMany.length];
    	for (int i=0; i < columnData_des_oneToMany.length; i++) {
      	cdDesOtm[i] = (ColumnData) columnData_des_oneToMany[i].clone();
      }
    	cloned.columnData_des_oneToMany = cdDesOtm;
    }
    
		return cloned;
	}

	/**
	 * don't page through the record set, plain old move foward through it
	 * @param b
	 */
  public void setSkipPaging(boolean b) {
    this.skipPaging = b;
  }

  public void setOrderBy(String orderBy) {
    this.orderBy = orderBy;
  }

	public void deleteSource(boolean b) {
		this.deleteSource = b;
  }

	public void setIdentities(ColumnData[] columnIdentities) {
		this.columnIdentities = columnIdentities;
  }
	
	/**
	 * get identity where query
	 * @return
	 */
	private String getSql_WhereForIdents() {
	  String sql = cl_src.getSql_IdentitiesWhere(columnData_src);
	  return sql;
  }
	
	/**
	 * mark which columns are being used as the identities
	 *   I'm doing both just in case of future reference
	 */
	private void setIdentityIntoColumnArrays() {
		
		if (columnIdentities == null) {
			return;
		}
		
		// use identities instead of primary key
		for(int i=0; i < columnData_src.length; i++) {
  		
      if (columnIdentities != null) {
        int index = cl_src.searchColumnByName_UsingComparator(columnIdentities, columnData_src[i]);
        if (index >= 0) {
        	columnData_src[i].setIdentity(true);
        }
      }
      
		}
		
		for(int i=0; i < columnData_des.length; i++) {
  		
      if (columnIdentities != null) {
        int index = cl_des.searchColumnByName_UsingComparator(columnIdentities, columnData_des[i]);
        if (index >= 0) {
        	columnData_des[i].setIdentity(true);
        }
      }
      
		}
		
	}

  public void setKeepPrimaryKey(boolean b) {
    this.keepPrimaryKey = b;
  }

  /**
   * Maybe I could do this down the road, not sure there is an all in one solution here b/c of
   * the difference of every transfer
   * @param b
   */
  public void setPurgeAtEnd(boolean b) {
    this.purgeAtEnd = b;
  }

  
}
