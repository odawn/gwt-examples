package org.gonevertical.dts.lib.file;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csvreader.CsvReader;

public class MoveFileData {
	
  private static final Logger log = LoggerFactory.getLogger(MoveFileData.class);

  public static final int MATCH_HEADERS = 1;
  public static final int MATCH_FILENAME = 2;
  private int matchHow = 0;
  
  // TODO change to private
  public String matchHeaderValues = null;
  
  // TODO change to private
  public char delimiter;
  
  // TODO change to private
  public String pathToMoveToDir = null;
  
  // regex a file name by this
  private String matchFileNameRegex = null;
  
  private String[] header = null;
  
  /**
   * set a sample file to get the matchheaderValues from
   * 
   * @param fileOrdir
   */
  public void setMatchHeaderValues(File fileOrdir) {
    matchHow = MATCH_HEADERS;

    File file = checkForFile(fileOrdir);

    CsvReader reader = null;
    try {     
      reader = new CsvReader(file.toString(), delimiter);
    } catch (FileNotFoundException e) {
      System.err.println("doesFileHeaderMatchStr: Could not open CSV Reader");
      e.printStackTrace();
      return;
    }
    
    if (reader == null) {
      matchHeaderValues = null;
    }
    
    header = null;
    try {
      reader.readHeaders();
      header = reader.getHeaders();
    } catch (IOException e) {
      System.out.println("doesFileHeaderMatchStr: could not read headers");
      e.printStackTrace();
      
    }
    
    if (header == null) {
      matchHeaderValues = null;
    }
    
    String sheader = "";
    for (int i=0; i < header.length; i++) {
      sheader += header[i];
    }
    
    matchHeaderValues = sheader;
  }
  
  private File checkForFile(File fileOrdir) {
  
    File file = null;
    if (fileOrdir.isDirectory() == true) {
      file = getFile(fileOrdir);
      if (file == null) {
        String newPathToCheck = fileOrdir.getAbsolutePath() + "/done";
        file = checkForFile(new File(newPathToCheck));
      }
      
    } else {
      file = fileOrdir;
    }
  
    return file;
  }
  
  private File getFile(File dir) {
    File[] files = dir.listFiles();
    File file = null;
    for (int i=0; i < files.length; i++) {
      if (files[i].isFile() == true) {
        file = files[i];
      }
    }
    return file;
  }
  
  public void setMatchByFileName(String regex) {
    this.matchHow = MATCH_FILENAME;
    this.matchFileNameRegex = regex;
  }
  
  public int getMatchHow() {
    return this.matchHow;
  }
  
  public String getHeaders() {
    return this.matchHeaderValues;
  }
  
  public char getDelimiter() {
    return this.delimiter;
  }
  
  public String getMovePath() {
    return this.pathToMoveToDir;
  }
  
  public String getFileNameRegex() {
    return this.matchFileNameRegex;
  }

  public boolean compareHeaders(String[] compare) {
  
    Arrays.sort(header);
    Arrays.sort(compare);
  
    int totalHeader = header.length;
    int totalCompare = compare.length;
    int matched = 0;
    int notMatched = 0;
    for (int i=0; i < compare.length; i++) {
      if (isheaderIn(compare[i]) == true) {
        matched++;
      } else {
        notMatched++;
      }
       
    }

    boolean b = false;
    if (matched == totalHeader) {
      b = true;
    } else if (totalCompare < totalHeader) {
      b = false;
    } else if (totalCompare > totalHeader) {
      // TODO
      b = false;
    }
    
    return b;
  }
  
  private boolean isheaderIn(String name) {
    int i = Arrays.binarySearch(header, name);
    boolean b = false;
    if (i > -1) {
      b = true;
    }
    return b;
  }
  
  
}
