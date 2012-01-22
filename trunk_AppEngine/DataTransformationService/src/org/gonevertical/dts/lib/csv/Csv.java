package org.gonevertical.dts.lib.csv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.gonevertical.dts.lib.address.AddressParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csvreader.CsvReader;

public class Csv {

  private static final Logger log = LoggerFactory.getLogger(Csv.class);
	
  public Csv() {
  }
  
  public CsvReader open(File file, char delimiter) {
    CsvReader csvRead = null;
    try {
      if (Character.toString(delimiter) == null) {
        log.error("Csv.open(): openFileAndRead: You forgot to set a delimiter. Exiting.");
        System.exit(1);
      }
    } catch (Exception e1) {
    	log.error("Csv.open(): You forgot to set a delimiter. Exiting.", e1);
      e1.printStackTrace();
      System.exit(1);
    }
    try {     
      csvRead = new CsvReader(file.toString(), delimiter);
    } catch (FileNotFoundException e) {
      log.error("Csv.CsvReader(): Could not open CSV Reader", e);
      e.printStackTrace();
    }
    return csvRead;
  }
  
  public String[] getColumns(CsvReader csvRead) {
    String[] columns = null;
    try {
      csvRead.readHeaders();
      columns = csvRead.getHeaders();
    } catch (IOException e) {
      log.error("Csv.getColumns(): couln't read headers: ", e);
      e.printStackTrace();
    }
    return columns;
  }
  
}
