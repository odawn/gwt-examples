package org.gonevertical.dts.v1;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.gonevertical.dts.data.ColumnData;
import org.gonevertical.dts.data.DestinationData;
import org.gonevertical.dts.data.FieldData;

import com.csvreader.CsvReader;

public class CSVProcessing_V1 extends FlatFileProcessing_V1 {

  // work on this file
  private File file = null;

  // this file has this delimter
  private char delimiter;

  // csv reader 2.0
  private CsvReader reader = null;

  // sql methods
  private Optimise_V1 sql = new Optimise_V1();

  // destination data settings, used in first column processing
  private DestinationData dd = null;

  private File processingfile = null;
  
  /**
   * constructor
   */
  public CSVProcessing_V1() {
  }
  
  /**
   * set the settings
   * 
   * @param delimiter
   * @param destinationData
   * @param matchFields
   */
  public void setData(char delimiter, DestinationData destinationData, FieldData[] matchFields) {
    this.delimiter = delimiter;
    this.dd  = destinationData;

    try {
      sql.setDestinationData(destinationData);
    } catch (Exception e) {
      e.printStackTrace();
      System.out.println("ERROR: CSVProcessing: forogot destinationData is null");
      System.exit(1);
    }
    sql.setMatchFields(matchFields);
  }
  
  public void setData(char delimiter) {
    this.delimiter = delimiter;
  }
  
  /**
   * when reading a directory of files, don't drop table when moving to the second file notification
   */
  public void dropTableOff() {
    sql.dropTableOff();
  }
  
  /**
   * parse the file, insert/update sql, then optimise it if needed.
   *  
   * @param fileIndex - when reading a director of files(.list) this is the index number of which file its on.
   * @param file - the file to parse
   */
  protected void parseFile_Sql(int fileIndex, File file) {
    this.file = file;

    sql.setFile(file);
    
    // open a sql connection
    sql.openConnection();

    // create table
    sql.createTable();

    // create columns importid, datecreated, dateupdated used for tracking
    sql.createDefaultFields();

    // open the file
    openFileAndRead();

    // create columns from file
    getColumnsInHeader();

    // loop through data rows
    iterateRowsData(fileIndex);

    // optimise table (if set on)
    sql.runOptimise();

    // delete empty columns (if set on)
    sql.deleteEmptyColumns();

    // done with connection
    sql.closeConnection();
  }
  
  protected boolean parseFile_Match(int fileIndex, File file) {
    this.file = file;

    // open the file
    openFileAndRead();

    // process columns
    getColumnsInHeader();
    
    if (foundMatch == true) {
      return true;
    }
    
    // loop through data rows
    iterateRowsData(fileIndex);

    if (foundMatch == true) {
      return true;
    }
    
    return false;
  }

  /**
   * open file and start reading it
   */
  private void openFileAndRead() {
    
    try {
      if (Character.toString(delimiter) == null) {
        System.out.println("openFileAndRead: You forgot to set a delimiter. Exiting.");
        System.exit(1);
      }
    } catch (Exception e1) {
      System.out.println("openFileAndRead: You forgot to set a delimiter. Exiting.");
      e1.printStackTrace();
      System.exit(1);
    }
    
    try {     
      reader = new CsvReader(file.toString(), delimiter);
    } catch (FileNotFoundException e) {
      System.err.println("CSV Reader, Could not open CSV Reader");
      e.printStackTrace();
    }
  }

  /**
   * get columns from file, and create them in database if need be
   *  
   *  ~~~ 1st row processing ~~~~
   * 
   * @throws Exception
   */
  private void getColumnsInHeader() {
    
    ColumnData[] columns = null;	
    try {
      reader.readHeaders();
      columns = makeColumnlist(reader.getHeaders());
      columns = stopAtColumnsCount(columns);
    } catch (IOException e) {
      System.out.println("getColumnsInHeader: couln't read columns");
      e.printStackTrace();
    }

    // insert columns into database if need be
    if (columns != null) {
      
      // add data to table
      if (mode == MODE_SQLIMPORT) {
        sql.createColumns(columns);
        
      } else if (mode == MODE_FINDFILEMATCH) {
        if (foundMatch == true) {
          return;
        }
      } else if (mode == MODE_FILEEXPORT) {
        // TODO add in file export ability
      } 
      
    } else {
      System.err.println("CSV Reader could not get columns");
      System.exit(1);
    }

  }

  private ColumnData[] stopAtColumnsCount(ColumnData[] columns) {
    int c = columns.length;
    
    if (dd != null && dd.stopAtColumnCount > 1 && columns.length > dd.stopAtColumnCount) {
      c = dd.stopAtColumnCount;
    }
    
    ColumnData[] b = new ColumnData[c];
    for (int i=0; i < c; i++) {
      b[i] = columns[i];
    }
    return b;
  }

  /**
   * iterate through data in file
   * 
   * @param indexFile
   */
  private void iterateRowsData(int indexFile) {

    String[] values = null;
    int index = 0;
    
    // when the first row is data, need to move up one row to start
    if (dd != null && dd.firstRowHasNoFieldNames == true) {
      index--;
    }
    
    try {
      while (reader.readRecord()) {

        values = reader.getValues();

        values = stopAtColumnCount(values);
        
        values = processRowValues(index+1, values);
        
        // add data to table
        if (mode == MODE_SQLIMPORT) {
          sql.addData(indexFile, index, values);
        } else if (mode == MODE_FINDFILEMATCH) {
          if (foundMatch == true) {
            return;
          }
        } else if (mode == MODE_FILEEXPORT) {
          // TODO add in file export ability
        } 

        // debug
        //listValues(index, values);

        index++;
      }
    } catch (IOException e) {
      System.out.println("Error: Can't loop through data!");
      e.printStackTrace();
    }

  }

  private String[] stopAtColumnCount(String[] values) {
   
    int c = values.length;
    
    if (dd != null && dd.stopAtColumnCount > 1 && values.length > dd.stopAtColumnCount) {
      c = dd.stopAtColumnCount;
    }
    
    String[] b = new String[c];
    for (int i=0; i < c; i++) {
      b[i] = values[i];
    }
    return b;
  }
  
  /**
   * make a column 1(header) list of the fields, 
   *   which will always be needed when inserting/updating the data into sql
   * 
   *   ~~~ first row processsing ~~~
   * 
   * @param columns
   * @return
   */
  private ColumnData[] makeColumnlist(String[] columns) {

    // add some custome columns
    columns = extendColumns(columns);
    
    ColumnData[] cols = new ColumnData[columns.length];
    for(int i=0; i < columns.length; i++) {

      // FlatFileSettings processing - pre-process the values before sql
      columns[i] = evaluate(0, i, columns[i]);
      
      cols[i] = new ColumnData();
      
      if (dd != null && dd.firstRowHasNoFieldNames == true && i < columns.length-1) { //skip extended columns
        cols[i].setName(" "); // this will change into 'c0','c1',... column names
      } else {
        cols[i].setName(columns[i]);
      }
      
    }

    return cols;
  }
  
  /**
   * add some custom columns
   *  
   * @param columns
   * @return
   */
  private String[] extendColumns(String[] columns) {
    
    int extendCount = 0;
    
    if (dd != null && dd.setSrcFileIntoColumn == true) {
     extendCount++; 
    }
    
    int newCount = extendCount + columns.length;
    String[] c = new String[newCount];
    
    for (int i=0; i < columns.length; i++) {
      c[i] = columns[i];
    }
    
    int b = columns.length;
    if (dd != null && dd.setSrcFileIntoColumn) {
      c[b] = "SrcFile";
      b++;
    }
    
    return c;
  }

  private void listValues(int index, String[] values) {

    String s = "";
    for (int i=0; i < values.length; i++) {
      s += ", " + values[i];
    }

    System.out.println(index + ". " + s);
  }

  /**
   * process each row > 0 against the flat file settings
   * 
   * @param row
   * @param values
   * @return
   */
  private String[] processRowValues(int row, String[] values) {
    for (int i=0; i < values.length; i++) {
      if (foundMatch == true) {
        break;
      }
      values[i] = evaluate(row, i, values[i]);
    }
    
    // add in custom values - to match custom column
    values = extendValues(row, values);
    
    return values;
  }

  private String[] extendValues(int row, String[] values) {
    
    int extendCount = 0;
    
    if (dd != null && dd.setSrcFileIntoColumn == true) {
     extendCount++; 
    }
    
    int newCount = extendCount + values.length;
    String[] v = new String[newCount];
    
    for (int i=0; i < values.length; i++) {
      v[i] = values[i];
    }
    
    int b = values.length;
    if (dd != null && dd.setSrcFileIntoColumn) {
      v[b] = file.getAbsolutePath();
      b++;
    }
    
    return v;
  }
  
}
