package org.gonevertical.dts.lib.openoffice;

import java.io.File;

public class SpreadSheetData {
  
  private File file = null;
  
  private char delimiter;
  
  private String sheetName = null;

	private RowData[] rowData;
  
	public SpreadSheetData() {
	}
	
  public SpreadSheetData(String sheetName, File file, char delimiter) {
    this.file = file;
    this.sheetName = sheetName;
    this.delimiter = delimiter;
  }

  public File getFile() {
    return file;
  }
  
  public String getSheetName() {
    return sheetName;
  }
  
  public char getDelimiter() {
    return delimiter;
  }
  
  public void setRowData(String sheetName, RowData[] rowData) {
  	this.sheetName = sheetName;
  	this.rowData = rowData;
  }
  
  public RowData[] getRowData() {
  	return this.rowData;
  }
  
  
}
