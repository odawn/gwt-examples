package org.gonevertical.dts.v1;

import java.io.File;
import java.util.Arrays;

import org.gonevertical.dts.data.DestinationData;
import org.gonevertical.dts.data.FieldData;
import org.gonevertical.dts.data.FlatFileSettingsData;
import org.gonevertical.dts.data.SourceData;
import org.gonevertical.dts.lib.FileUtil;


public class FileProcessing_V1 {

  private CSVProcessing_V1 csvProcess = new CSVProcessing_V1();

  // sql items
  private DestinationData desinationData = null;

  private FieldData[] matchFields = null;

  @Deprecated // move to source data
  private char delimiter;

  private boolean isDirectory = false;

  // source data
  private SourceData sourceData = null;

  // flat file settings
  private FlatFileSettingsData ffsd = null;

  /**
   * constructor
   */
  public FileProcessing_V1() {
  }

  public void setData(SourceData sourceData) {
    this.sourceData = sourceData;
    csvProcess.setData(sourceData.delimiter);
  }
  
  public void setData(FlatFileSettingsData ffsd) {
    this.ffsd = ffsd;
    csvProcess.setData(ffsd);
  }
  
  /**
   * set the data and run
   * 
   * @param sourceData
   * @param destinationData
   * @param matchFields
   */
  public void setData(SourceData sourceData, DestinationData destinationData, FieldData[] matchFields) {
    this.sourceData = sourceData;
    this.delimiter = sourceData.delimiter;
    this.desinationData = destinationData;

    if (matchFields != null) {
      Arrays.sort(matchFields);
    }
    this.matchFields = matchFields;

    csvProcess.setData(delimiter, destinationData, matchFields);

    try {
      run_Sql_Import();
    } catch (Exception e) {
      System.out.println("File Error:");
      e.printStackTrace();
    }
  }

  /**
   * start running the process
   * 
   * @param sourceData
   * @throws Exception 
   */
  private void run_Sql_Import() {

    File[] files;

    // is the file  a file or directory
    isDirectory = sourceData.file.isDirectory();
    if (isDirectory == true) {
      files = sourceData.file.listFiles();
      Arrays.sort(files);
    } else {
      files = new File[1];
      files[0] = sourceData.file;
      if (sourceData.file.isFile() == false) {
        System.err.println("File is not a file; It has to be a valid directory or file.");
        System.exit(1);
      }
    }

    loop(files);

    System.out.println("All Done: with files.");
  }
  
  /**
   * start the loop through the files
   * 
   * @param files
   */
  private void loop(File[] files) {

    Arrays.sort(files);

    for (int i=0; i < files.length; i++) {

      System.out.println("File: " + files[i].getName());

      if (files[i].isFile() == true) {

        // when extracting a bunch of the same files, skip optimisation after the first
        if (isDirectory == true && i > 0 && howManyAreFiles(files) > 1) {
          csvProcess.dropTableOff();
        }
        
        csvProcess.parseFile_Sql(i, files[i]);
        
        moveFileWhenDone(files[i]);
      }
    }

  }
  
  private void moveFileWhenDone(File file) {
    if (desinationData != null && desinationData.moveFileToDone == true) {
      FileUtil f = new FileUtil();
      f.moveFileToFolder_Done(file);
    }
  }
  
  /**
   * how many real files are we going to process, this delegates the drop table
   * @param files
   * @return
   */
  private int howManyAreFiles(File[] files) {
    int is = 0;
    for (int i=0; i < files.length; i++) {
      if (files[i].isFile()) {
        is++;
      }
    }
    return is;
  }

  
  
  

  public File findMatchInFile() {
        
    if (sourceData == null) {
      System.out.println("SourceData not set. Exiting.");
      System.exit(1);
    }
    
    if (ffsd == null) {
      System.out.println("FlatFileSettings not set. Exiting.");
      System.exit(1);
    }
    
    // set to match a file, not sql import
    csvProcess.setMode(FlatFileProcessing_V1.MODE_FINDFILEMATCH);
    
    File[] files;
    isDirectory = sourceData.file.isDirectory();
    if (isDirectory == true) {
      files = sourceData.file.listFiles();
      Arrays.sort(files);
    } else {
      files = new File[1];
      files[0] = sourceData.file;
      if (sourceData.file.isFile() == false) {
        System.err.println("File is not a file; It has to be a valid directory or file.");
        System.exit(1);
      }
    }

    File foundFile = loop_ToFindMatch(files);
    
    return foundFile;
  }
  
  private File loop_ToFindMatch(File[] files) {

    Arrays.sort(files);

    File foundfile = null;
    for (int i=0; i < files.length; i++) {

      System.out.println("Processing File: " + files[i].getName());

      if (files[i].isFile() == true) {
        boolean found = csvProcess.parseFile_Match(i, files[i]);
        if (found == true) {
          foundfile = files[i];
          break;
        }
      }
    }

    return foundfile;
  }

  
}
