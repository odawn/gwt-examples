package org.gonevertical.dts.v2;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gonevertical.dts.data.ColumnData;
import org.gonevertical.dts.data.FieldData;
import org.gonevertical.dts.data.FieldDataComparator;
import org.gonevertical.dts.data.SourceData;
import org.gonevertical.dts.data.StatData;
import org.gonevertical.dts.lib.FileUtil;
import org.gonevertical.dts.lib.csv.Csv;
import org.gonevertical.dts.lib.sql.columnlib.ColumnLib;
import org.gonevertical.dts.lib.sql.columnmulti.ColumnLibFactory;
import org.gonevertical.dts.lib.sql.querylib.QueryLib;
import org.gonevertical.dts.lib.sql.querymulti.QueryLibFactory;
import org.gonevertical.dts.lib.sql.transformlib.TransformLib;
import org.gonevertical.dts.lib.sql.transformmulti.TransformLibFactory;
import org.gonevertical.dts.process.copy.Copy;

import com.csvreader.CsvReader;

public class CsvProcessing extends FlatFileProcessing {

  private static final Logger log = LoggerFactory.getLogger(CsvProcessing.class);
	
  // supporting libraries
  private QueryLib ql;
  private TransformLib tl;
  private ColumnLib cl;
  
  private Csv csv;
  
  // csv reader 2.0
  private CsvReader csvRead;

  // work on this file
  private File file;
  
  // data source
  private SourceData sd;
  
  // data destination
  private DestinationData dd;

  // columns being imported 
  private ColumnData[] columnData;
  
  private ColumnData[] defaultColumns;
  
  // track the row position
  private int rowIndex;
  
  private boolean returnToOptimise;
  
  // keep track of stats of whats going on
  private StatData stats = new StatData();;
  
  /**
   * constructor
   */
  public CsvProcessing(SourceData sourceData, DestinationData destinationData) {
    this.sd = sourceData;
    this.dd = destinationData;
    
    // preprocess these settings
    if (destinationData.ffsd != null) {
      setData(destinationData.ffsd);  
    }
    
    // setup injector libraries
    setSupportingLibraries();
  }
  
  /**
   * guice injects the libraries needed for the database
   */
  private void setSupportingLibraries() {
    ql = QueryLibFactory.getLib(dd.databaseData.getDatabaseType());
    cl = ColumnLibFactory.getLib(dd.databaseData.getDatabaseType());
    tl = TransformLibFactory.getLib(dd.databaseData.getDatabaseType());
    csv = new Csv();
    
    stats.setSupportingLibraries(cl,ql,tl);
    
    ql.setStats(stats);
    tl.setStats(stats);
    
  }
  
  /**
   * process the csv records out of the file
   * 
   * @param fileIndex
   * @param file
   */
  protected void processCsvFile(int fileIndex, File file) {
    this.file = file;
    
    // set stats
    stats.start();
    stats.setSourceFile(file);
    stats.setDestTable(dd.table);
    
    // count file lines
    countLinesInFile(file);
    
    // open the csv file for reading
    openFileAndRead();

    // create table
    createTable();
    
    // create default columns datecreated, dateupdated
    createDefaultFields();
    
    // get and create columns
    setupColumns();

    //markColumnsThatAreIdents();
     
    createIdentitiesIndex();
    
    // loop through data rows
    iterateRowsData(fileIndex);
    
    // end stats for this file
    stats.end();
    
    // print report
    stats.print();
    
    // save log to table
    stats.saveToTable(dd.databaseData, dd.getLogToTable(), dd.getLoggingTable());
    
    // check to see if line count match insert/update count, notify if not
    compareLinesToSaveCount();
    
    // are there errors from processing this file?
    isThereErros();
    
    // if no updates or inserts where done tell me?
    isThereUpdatesAndInserts();
    
    // closed the csv 
    csvRead.close();
  }
  
  private void isThereUpdatesAndInserts() {
  	if (stats != null && stats.hasUpdatesAndInserts() == true) {
  		return;
  	}
  	// TODO need to fix the offset for this, when substiting rows, or not
  	//log.error("CsvProcessing.isThereUpdatesAndInserts(): " +
  			//"For some reason there where no Inserts or Updates done. " +
  			//"File:" + file.getName() + " sourceData.file: " + sd.file.getName() + " " +
  					//"stats.fileLineCount: " + stats.getFileLineCount() + "\n\n" + stats.toString());
  }

	private void isThereErros() {
  	if (stats != null && stats.getHasErrors() == false) {
  		return;
  	}
  	log.error("CsvProcessing.parseFile() has errors. File: " + file.getName() + " " +
  			"sourceData.file: " + sd.file.getName() + "\n\n" + stats.toString());
  }

	/**
	 * compare how many lines where counted, to the lines inserted and updated into the database, 
	 *   was the same amount saved?
	 */
	private void compareLinesToSaveCount() {

		if (returnToOptimise == true) {
			return;
		}
		
		boolean b = stats.doesLineCountMatchSaveCount(sd);
  	
  	if (b == false) {
  		log.error("CsvProcessing.compareLinesToSaveCount(): " +
  				"Import FileLineCount doesn't match what sqlSaveCount:\n " +
  				"File:" + file.getName() + " sourceData.file: " + sd.file.getName() + "\n" +
  				"FileLineCount: " + stats.getFileLineCount() + " saveCount: " + stats.getSaveCount() + "\n\n " + stats.toString());
  	}
  	
  }

	/**
   * count how many lines are in the file, this is how many records should go into the database.
   * 
   * @param file
   */
  private void countLinesInFile(File file) {
	  FileUtil fu = new FileUtil();
	  long fileLineCount = fu.getFileLineCount(file);
	  stats.setFileLineCount(fileLineCount);
  }

	private void createIdentitiesIndex() {
    if (dd.identityColumns == null) {
      return;
    }
   
    String sql = cl.getSql_IdentitiesIndex(dd.databaseData, columnData);
    if (sql == null) {
      log.info("skipping identities indexing, probably already created. createIdentitiesIndex()");
      return;
    }
    ql.update(dd.databaseData, sql);
  }

  /**
   * look for match only
   *   
   *   TODO - finish method - this needs to find a match
   *   
   * @param fileIndex
   * @param file
   * @return
   */
  protected boolean parseFile_Match(int fileIndex, File file) {
    this.file = file;

    // open the csv file for reading
    openFileAndRead();

    // TODO compare version 1 
    
    return false;
  }

  /**
   * open file and start reading it
   */
  private void openFileAndRead() {
    csvRead = csv.open(file, sd.delimiter);
  }
  
  /**
   * setup columns from csv header
   *  (this will move the csv reader one row foward)
   */
  private void setupColumns() {
  	
  	CsvReader cr = null;
  	if (sd.getHeadersFile() != null) {
  		cr = getSubstitueHeadersFromFile();
  	} else {
  		cr = csvRead;
  	}
  	
    String[] columns = csv.getColumns(cr);
    
    columns = extendColumns(columns);
    
    columnData = processColumnsToColumnData(columns);
    tl.createColumn(dd.databaseData, columnData);
  }
  
  /**
   * get a different set of headers from another file
   * @return
   */
  private CsvReader getSubstitueHeadersFromFile() {
  	CsvReader cr = csv.open(sd.getHeadersFile(), sd.getHeaderDelimiter());
	  return cr;
  }

	/**
   * make a column 1(header) list of the fields, 
   *   which will always be needed when inserting/updating the data into sql
   * 
   *   ~~~ first row processsing ~~~
   * 
   * @param header
   * @return
   */
  private ColumnData[] processColumnsToColumnData(String[] header) {
    
  	ColumnData[] columnData = new ColumnData[header.length];
    
    for(int i=0; i < header.length; i++) {
      columnData[i] = new ColumnData();
      columnData[i].setTable(dd.table);
     
      // is this column used for identity
      if (isThisColumnUsedForIdentity(columnData[i], header[i])) {
        columnData[i].setIdentity(true);
      }
      
      // pre-process flat file values
      header[i] = evaluate(0, i, header[i]);
      
      // change Column Name by using Field matching
      header[i] = changeColumnName(header[i]);
      
      // does the first row have header/column names?
      if (sd != null && 
      		sd.getHeadersFile() == null && // has no header file
      		sd.getIgnoreFirstRow() == true && // first row is set to ignore
      		i < header.length - 1) { // its at the first row
        columnData[i].setColumnName(" "); // this will change into 'c0','c1',... column names
        
      } else { // first row has the column names
        columnData[i].setColumnName(header[i]);
      }
      
    }
    return columnData;
  }
  
  /**
   * create default columns
   */
  private void createDefaultFields() {
    defaultColumns = new ColumnData[2];
    defaultColumns[0] = new ColumnData();
    defaultColumns[1] = new ColumnData();
    
    defaultColumns[0].setTable(dd.table);
    defaultColumns[0].setColumnName(dd.dateCreated);
    defaultColumns[0].setType("DATETIME DEFAULT NULL");
    defaultColumns[0].setValueAsFunction("NOW()");
   
    defaultColumns[1].setTable(dd.table);
    defaultColumns[1].setColumnName(dd.dateUpdated);
    defaultColumns[1].setType("DATETIME DEFAULT NULL");
    defaultColumns[1].setValueAsFunction("NOW()");
    
    tl.createColumn(dd.databaseData, defaultColumns);
  }
  
  /**
   * is this Column used for identity
   * 
   * TODO - deal with no identities being used
   * 
   * @param columnData
   * @param headerValue
   * @return
   */
  private boolean isThisColumnUsedForIdentity(ColumnData columnData, String headerValue) {
    // find the destination field by comparing source field
    int index = FieldData.getSourceFieldIndex(dd.identityColumns, headerValue);
    
    // no matches found in field data
    if (index < 0) {
      return false;
    } else {
      return true;
    }
  }
  
  /**
   * change the column header name by field matching
   * 
   * @param columnName
   * @return columnName
   */
  private String changeColumnName(String columnName) {
    if (dd.changeColumn == null) {
      return columnName;
    }
    Comparator<FieldData> searchByComparator = new FieldDataComparator();
    Arrays.sort(dd.changeColumn);
    FieldData searchFor = new FieldData();
    searchFor.sourceField = columnName;
    int index = Arrays.binarySearch(dd.changeColumn, searchFor, searchByComparator);
    if (index > -1) {
      columnName = dd.changeColumn[index].destinationField;
    }
    return columnName;
  }
  
  /**
   * iterate through data in file
   * 
   * @param indexFile
   */
  private void iterateRowsData(int indexFile) {
    rowIndex = 0;
    stats.setRowCount(rowIndex);
    
    // move count up one when skipping first one, so index count matches
    if ((sd != null && 
    		sd.getHeadersFile() == null && // don't move count one if there is a substitue header 
    		sd.getIgnoreFirstRow() == false) // when ignore first row, move back count
    		) {  
      rowIndex--;
    }

    
    // start looping through the records
    try {
      while (csvRead.readRecord()) {
      	
      	if (sd.getIgnoreFirstRow() == true && rowIndex == 0) {// skip
      		log.info("skipping frist row :" + rowIndex);
      	
      	} else if (sd.getIgnoreLastRow() == true && rowIndex == stats.getFileLineCount()-1) { // TODO - does the rowIndex-- mess this up
      		log.info("skipping last row: " + rowIndex);
      		
      	} else {
      		process(rowIndex, csvRead);
      	}
        
        
        // optimise early - this will cause optimising after so many rows, then will start agian
        if (dd != null && dd.optimise == true && dd.optimise_RecordsToExamine == rowIndex) {
          returnToOptimise = true;
          return;
        }
        
        // stop early
        if (dd != null && dd.stopAtRow == rowIndex && rowIndex != -1) {
          return;
        }
        
        rowIndex++;
      
        // keep track of row count
        stats.setAddRowCount();
      }
      
    } catch (IOException e) {
      log.error("CsvProcessing.iterateRowsData(): Can't loop through data", e);
      e.printStackTrace();
    }
  }
  
  /**
   * process values from csv row
   * 
   * @param index
   * @param reader
   */
  private void process(int index, CsvReader reader) {
    String[] values = null;
    
    // get values from row
    try {
      values = reader.getValues();
    } catch (IOException e) {
      log.error("CsvProcessing.process():", e);
      e.printStackTrace();
    }
    
    // extend some custom values into the data
    values = extendValues(index, values);
    
    // pre-process values in row
    values = preProcessFlatFileValues(index + 1, values);
    
    // add values to columnData
    columnData = cl.addValues(columnData, values);
    
    testColumnValueTypes();
    testColumnValueSizes();
    
    // save Columns/Values to db
    save();
  }
  
  /**
   * test the value type will fit into the sql type
   */
  private void testColumnValueTypes() {
    
    for (int i=0; i < columnData.length; i++) {
      String vraw = columnData[i].getValue();
      boolean testTypeThrown = columnData[i].getTestTypeThrow();
      columnData[i].setValue(vraw);
      
      // alter back to text
      if (testTypeThrown == true) {
        columnData[i].setType("TEXT DEFAULT NULL");
        tl.alterColumn(dd.databaseData, columnData[i]);
      }
   
    }
    
  }

  /**
   * does the values lengths fit into sql column, so not to truncate
   */
  private void testColumnValueSizes() {
    for (int i=0; i < columnData.length; i++) {
      columnData[i].alterColumnSizeBiggerIfNeedBe(dd.databaseData);
    }
  }

  /**
   * save the column's values into db
   */
  private void save() {
    
    long primaryKeyId = doesDataExist();
     
    String sql = "";
    if (primaryKeyId > 0) {
    	
      ColumnData primaryKeyColumn = new ColumnData();
      primaryKeyColumn.setTable(dd.table);
      primaryKeyColumn.setIsPrimaryKey(true);
      primaryKeyColumn.setColumnName(dd.primaryKeyName);
      primaryKeyColumn.setValue(primaryKeyId);
      
      // using a compare of the previously stored data
      if (compareBefore(columnData, primaryKeyColumn) == true) {
        log.info("skipping b/c of compare. Are you sure you wanted this feature?");
        return;
      }
      
      ColumnData[] u = cl.merge(columnData, defaultColumns[1]); // add update column
      u = cl.merge(u, primaryKeyColumn);
      sql = cl.getSql_Update(u);
      
    } else { 
      ColumnData[] i = cl.merge(columnData, defaultColumns[0]); // add insert column
      sql = cl.getSql_Insert(i);
    }
    
    log.info(rowIndex + ". SAVE(): " + sql);
    
    ql.update(dd.databaseData, sql, false);
    
    // TODO - deal with truncation error ~~~~~~~~~~~~~~~~~~~~~~~~~~
    // TODO - truncation should rarely happen here, b/c this is tested for earlier.
    
    stats.setSaveCount();
  }
 
  /**
   * run a comparison on the columndata before saving it
   * 
   * @param columnData
   * @param primKeyColumn
   * @return
   */
  private boolean compareBefore(ColumnData[] columnData, ColumnData primKeyColumn) {
    
    if (dd.compareBeforeUpdate == null) {
      return false;
    }
    boolean b = false;
    
    String where = " WHERE " + primKeyColumn.getColumnName() + "='" + primKeyColumn.getValue() + "'";
    
    FieldData[] c = dd.compareBeforeUpdate;
    for (int i=0; i < c.length; i++) {
      
    	ColumnData forColumnData = new ColumnData(primKeyColumn.getTable(), c[i].destinationField, "TEXT");
      
      int index = cl.searchColumnByName_NonComp(columnData, forColumnData);
      
      if (index > -1) {
        String sql = "SELECT " + c[i].destinationField + " FROM " + primKeyColumn.getTable() + " " + where;
        String beforeValue = columnData[index].getValue();
        String inTableValue = ql.queryString(dd.databaseData, sql);
        
        if (Integer.parseInt(beforeValue) < Integer.parseInt(inTableValue)) {
          b = true;
        }
        log.trace("CsvProcessing_v2.compareBefore(): forTable:" + dd.table + ": before: " + beforeValue + " < inTable: " + inTableValue + " result: " + b);
      }
      
    }
    
    return b;
  }
  
  /**
   * does the data already exist
   * 
   *  TODO deal with empty to null
   *  
   * @return
   */
  private long doesDataExist() {
   
    if (dd.identityColumns == null) {
     return -1;
   }
   
   String idents = cl.getSql_IdentitiesWhere(columnData);

   String where = " WHERE " + idents;
   
   String sql = "SELECT " + dd.primaryKeyName + " FROM " + dd.table + " " + where;
   
   log.info(sql);
   
   long primaryKeyId = ql.queryLong(dd.databaseData, sql); 
   
   return primaryKeyId; 
  } 
  
  /**
   * process each row > 0 against the flat file settings
   * 
   * @param row
   * @param values
   * @return
   */
  private String[] preProcessFlatFileValues(long row, String[] values) {
    for (int i=0; i < values.length; i++) {
      values[i] = evaluate(row, i, values[i]);
    }
    return values;
  }

  /**
   * create destination table
   */
  private void createTable() {
    tl.createTable(dd.databaseData, dd.table, dd.primaryKeyName);
  }
  
  
  
  
  /**
   * TODO change the way I add values
   *  
   * @param values
   * @return
   */
  private String[] extendColumns(String[] values) {
    
    int extendCount = 0;
    
    if (dd != null && dd.getSrcFileIntoColumn() == true) {
     extendCount++; 
    }
    
    int newCount = extendCount + values.length;
    String[] c = new String[newCount];
    
    for (int i=0; i < values.length; i++) {
      c[i] = values[i];
    }
    
    int b = values.length;
    if (dd != null && dd.getSrcFileIntoColumn()) {
      c[b] = "SrcFile";
      b++;
    }
    
    return c;
  }
  
  /**
   * TODO change the way i do this
   * 
   * @param row
   * @param values
   * @return
   */
  private String[] extendValues(int row, String[] values) {
    
    int extendCount = 0;
    
    if (dd != null && dd.getSrcFileIntoColumn() == true) {
     extendCount++; 
    }
    
    int newCount = extendCount + values.length;
    String[] v = new String[newCount];
    
    for (int i=0; i < values.length; i++) {
      v[i] = values[i];
    }
    
    int b = values.length;
    if (dd != null && dd.getSrcFileIntoColumn()) {
      v[b] = file.getAbsolutePath();
      b++;
    }
    
    return v;
  }
  
  
  private void listValues(int index, String[] values) {

    String s = "";
    for (int i=0; i < values.length; i++) {
      s += ", " + values[i];
    }
    
    log.info(index + ". " + s);
  }
  
  private ColumnData[] stopAtColumnsCount(ColumnData[] columns) {
    int c = columns.length;
    if (dd != null && dd.stopProcessingAtRowCount > 1 && columns.length > dd.stopProcessingAtRowCount) {
      c = dd.stopProcessingAtRowCount;
    }
    ColumnData[] b = new ColumnData[c];
    for (int i=0; i < c; i++) {
      b[i] = columns[i];
    }
    return b;
  }
  
  private String[] stopAtColumnCount(String[] values) {
    
    int c = values.length;
    
    if (dd != null && dd.stopProcessingAtRowCount > 1 && values.length > dd.stopProcessingAtRowCount) {
      c = dd.stopProcessingAtRowCount;
    }
    
    String[] b = new String[c];
    for (int i=0; i < c; i++) {
      b[i] = values[i];
    }
    return b;
  }
  
  
  private void markColumnsThatAreIdents() {
    if (dd.identityColumns == null) {
      return;
    }
    for (int i=0; i < columnData.length; i++) {
      for (int b=0; b < dd.identityColumns.length; b++) {
        if (columnData[i].getColumnName().toLowerCase().equals(dd.identityColumns[b].destinationField) == true) {
          columnData[i].setIdentity(true);
        }
      }
    }
  }
  
  /**
   * do we need to start over after early optimisation?
   * @return
   */
  public boolean getReturnToOptimise() {
    boolean r = returnToOptimise;
    returnToOptimise = false;
    return r;
  }

	public StatData getStats() {
	  return stats;
  }
  
}
