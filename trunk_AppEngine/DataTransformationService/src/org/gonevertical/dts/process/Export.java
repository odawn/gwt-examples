package org.gonevertical.dts.process;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gonevertical.dts.data.ColumnData;
import org.gonevertical.dts.data.DatabaseData;
import org.gonevertical.dts.lib.FileUtil;
import org.gonevertical.dts.lib.sql.columnlib.ColumnLib;
import org.gonevertical.dts.lib.sql.columnmulti.ColumnLibFactory;
import org.gonevertical.dts.lib.sql.querylib.QueryLib;
import org.gonevertical.dts.lib.sql.querymulti.QueryLibFactory;
import org.gonevertical.dts.lib.sql.transformlib.TransformLib;
import org.gonevertical.dts.lib.sql.transformmulti.TransformLibFactory;
import org.gonevertical.dts.lib.text.WordParser;


public class Export {
	
  private static final Logger log = LoggerFactory.getLogger(WordParser.class);
  
  public static final int EXPORTAS_CSV = 1;
  public static final int EXPORTAS_SQL = 2;
  
  private int exportAs = EXPORTAS_CSV;
  
  //supporting libraries
  private QueryLib ql = null;
  private TransformLib tl = null;
  private ColumnLib cl = null;
  
  private DatabaseData src = null;
  
  // destination file
  private File des = null;
  
  // exporting to this file
  private File file = null;
  
  // keep track of what output file export is on
  // des.csv|sql
  // des_1.csv|sql
  private int fileIndex = 0;
  
  // columns that will be exported
  private ColumnData[] columnData = null;
  
  // table to export data from
  private String table = null;
  
  // filter query using where
  private String whereSql = null;
  
  // like LIMIT 0,100
  private String limitSql = null;

  // output to file
  private BufferedWriter out = null;
  
  // if exporting as SQL - do you want to create table script in the export
  private boolean createTable = false;

  // keep files around <= 1GB
  private long maxOutFileSizeKB = 1048576;
  
  // skip these columns
  private ColumnData[] pruneColumnData = null;
  
  // do not export the primary key (default is false)
  // set to true if you really want the primary key exported
  private boolean exportPrimaryKey = true;
  
  // add to the file name
  private String name;
  
  // set the export source style
  private int exportServerType = DatabaseData.TYPE_MYSQL;
  
  // this is like from database.table
  private String database;
  
  public Export(DatabaseData source, File destinationDirectory) {
    this.src = source;
    this.des = destinationDirectory;
    setSupportingLibraries();
  }
  
  /**
   * guice injects the libraries needed for the database
   */
  private void setSupportingLibraries() {
    // get query library
    ql = QueryLibFactory.getLib(src.getDatabaseType());
    
    // get column library
    cl = ColumnLibFactory.getLib(src.getDatabaseType());
    
    // get tranformation library
    tl = TransformLibFactory.getLib(src.getDatabaseType());
  }
  
  public void setAddToFileName(String name) {
    this.name = name;
  }
  
  /**
   * export (insert style) DatabaseData.TYPE_MYSQL;
   * 
   * @param serverType
   */
  public void setExportServerType(int serverType) {
    this.exportServerType = serverType;
  }
  
  /**
   * which table would you like to export from?
   * 
   * @param table
   */
  public void setTable(String table) {
    this.table = table;
  }
  
  public void setTable(String table, String whereSql, String limitSql) {
    this.table = table;
    this.whereSql = whereSql;
    this.limitSql = limitSql;
  }
  
  /**
   * which columns would you like to export?
   *   if null will export all
   * 
   * @param columns
   */
  public void setColumns(ColumnData[] columns) {
    this.columnData = columns;
  }
  
  /**
   * if exporting as sql, do you want to create table script in it?
   * 
   * @param b
   */
  public void setShowCreateTable(boolean b) {
    this.createTable = b;
  }
  
  /**
   * export the primary key in csv? default is false
   * 
   * @param b
   */
  public void setSkipPrimaryKey(boolean b) {
    this.exportPrimaryKey = b;
  }
  
  /**
   * prune these columns out
   * 
   * @param pruneColumnData
   */
  public void setPruneColumns(ColumnData[] pruneColumnData) {
    this.pruneColumnData = pruneColumnData;
  }
  
  /**
   * export as csv or sql
   * 
   * @param exportAs static constants in this file
   */
  public void run(int exportAs) {
    this.exportAs = exportAs;
    
    if (des == null) {
      System.out.println("no destination file set");
      return;
    }
    
    // get all columns?
    if (columnData == null) {
      columnData = getColumns();
    }
    
    // prune columns?
    if (pruneColumnData != null) {
      columnData = cl.prune(columnData, pruneColumnData);
    }
    
    // the primary key column
    if (exportPrimaryKey == false) {
      columnData = cl.prunePrimaryKey(columnData);
    }
    
    setFile();
    
    openFile();
    
    loopData();
    
    closeFile();
    
    System.out.println("Finished!");
  }
  
  /**
   * start exporting the data
   */
  private void loopData() {
    
    String d = "";
    if (database != null) {
      d = database;
    }
    
    String sql = "SELECT ";
    sql += cl.getSql_Names(columnData) + " ";
    sql += "FROM " + d + "."+ table + " ";
    
    if (whereSql != null && whereSql.trim().length() != 0) {
      sql += " WHERE " + whereSql;
    }
    
    if (limitSql != null) {
      sql += " " + limitSql;
    }
    
    try {
      Connection conn = src.getConnection();
      Statement select = conn.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY);
      select.setFetchSize(5000);
      ResultSet result = select.executeQuery(sql);
      int i = 0;
      while (result.next()) {
        columnData = cl.getResult(result, columnData);
        appendToFile(i);
        i++;
      }
      result.close();
      result = null;
      select.close();
      select = null;
    } catch (SQLException e) {
      System.err.println("Error: loopData(): " + sql);
      e.printStackTrace();
      log.error("Export.loopData() Error: " + sql, e);
    }
    
  }
  
  /**
   * write the data to the file
   * 
   * @param i - row index
   */
  private void appendToFile(int i) {
    
    String s = "";
    if (i == 0) {
      s = getHeader();
      s += getRows();
    } else {
      s = getRows();
    }
    
    // keep files <= 1GB
    checkFileSize();
    
    // DEBUG
    System.out.println(i + ". DEBUG OUT: " + s);
    
    writeFile(s);
  }
  
  /**
   * get header values
   * 
   * @return
   */
  private String getHeader() {
    String s = "";
    if (exportAs == EXPORTAS_CSV) {
      s = cl.getCsv_Names(columnData);
    } else if (exportAs == EXPORTAS_SQL) {
      s = showTableCreate() + "\n\n";
    }
    s += "\n";
    return s;
  }
  
  /**
   * show create table syntax
   * @return
   */
  private String showTableCreate() {
    String s = "";
    if (createTable == true) {
      s = tl.showCreateTable(src, table);
    }
    return s;
  }
  
  /**
   * get row values
   * 
   * @return
   */
  private String getRows() {
    String s = "";
    if (exportAs == EXPORTAS_CSV) {
      s = cl.getCsv_Values(columnData);
    } else if (exportAs == EXPORTAS_SQL) {
      s = cl.getSql_Insert(columnData); 
    }
    s += "\n";
    return s;
  }
  
  /**
   * get all columns of table
   * 
   * @return
   */
  private ColumnData[] getColumns() {
    ColumnData[] c = tl.queryColumns(src, table, null);
    return c;
  }
  
  /**
   * open the file for writing to
   */
  private void openFile() {
    try {
      out = new BufferedWriter(new FileWriter(file, false));
    } catch (IOException e) {
      e.printStackTrace();
      log.error("Export.openFile(): Error Opening File: ", e);
    }
  }
  
  /**
   * write to file
   * 
   * @param s
   */
  private void writeFile(String s) {
    try {
      out.write(s);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * close file
   */
  private void closeFile() {
    try {
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  /**
   * set the file name to export to
   * 
   * @param index
   */
  private void setFile() {
    String ext = "";
    if (exportAs == EXPORTAS_CSV) {
      ext = "csv";
    } else if (exportAs == EXPORTAS_SQL) {
      ext = "sql";
    }
    
    String add = "";
    if (name != null && name.length() > 0) {
      add = "_" + name;
    }
    
    String fileName = des.getAbsolutePath() + "/" + table + "_" + fileIndex + add + "." + ext;
    file = new File(fileName);
  }
  
  /**
   * check the file size is less than 1GB
   */
  private void checkFileSize() {
    long sizeinkb = FileUtil.getFileSize(file); 
    if (sizeinkb >= maxOutFileSizeKB) {
      fileIndex++;
      closeFile();
      setFile();
      openFile();
    }
  }

  /**
   * set database like  from database.table
   * @param database
   */
  public void setDatabase(String database) {
    this.database = database;
  }
  
  
}
