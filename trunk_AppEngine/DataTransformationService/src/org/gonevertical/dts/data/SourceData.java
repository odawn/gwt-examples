package org.gonevertical.dts.data;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SourceData {
	
  private static final Logger log = LoggerFactory.getLogger(SourceData.class);
 
	// file or directory
	public File file;
	
	// column/field separator
	public char delimiter;
	
	// flate file pre-processing
	public FlatFileSettingsData ffsd = null;
	
	// subsitute header file
	private File header = null;
	
	// substitue header file, header delimiter
	private char headerDelimiter;

	/**
	 * substitue header file, does the first row have fields or headers
	 */
	private boolean ignoreFirstRow = false;

	/**
	 * ignore the very last row
	 */
	private boolean ignoreLastRow = false;
		
	/**
	 * constructor
	 */
	public SourceData() {
	}
	
	/**
	 * is the source file a directory?
	 * @return
	 */
	public boolean isFileDirectory() {
	  boolean b = file.isDirectory();
	  return b;
	}
	
	/**
	 * change out the headers (columns / field names) with a file of your choice
	 * 
	 * @param header
	 * @param firstRowHasFields
	 */
	public void setSubstitueHeaders(File file, boolean ignoreFirstRow, char delimiter) {
		this.header = file;
		this.ignoreFirstRow = ignoreFirstRow;
		this.headerDelimiter = delimiter;
	}
	
	public void setSubstitueHeaders(File file, char delimiter) {
		this.header = file;
		this.ignoreFirstRow = false;
		this.headerDelimiter = delimiter;
	}

	public File getHeadersFile() {
		return header;
	}
	
	public char getHeaderDelimiter() {
	  return headerDelimiter;
  }
	
	/**
	 * should we ignore first row, skip over first row?
	 * 
	 * @return
	 */
	public boolean getIgnoreFirstRow() {
		return ignoreFirstRow;
	}
	
	/**
	 * should we ignore last row, skip over last row?
	 * 
	 * @return
	 */
	public boolean getIgnoreLastRow() {
		return ignoreLastRow;
	}

	/**
	 * the first row is data - so ignore it.
	 *   If no substitute headers set, then it will make c0, c1, c2, for column names
	 *   
	 * @param b
	 */
	public void setFirstRowHasNoFieldNames(boolean b) {
		if (b == false) {
			ignoreFirstRow = true;
		} else if (b == true) {
			ignoreFirstRow = false;
		}
	}
	
	/**
	 * ignore the first row, skip over first row.
	 * 
	 * @param b
	 */
	public void setIgnoreFirstRow(boolean b) {
		this.ignoreFirstRow = b;
	}
	
	/**
	 * ignore the last row, skip over last row.
	 * 
	 * @param b
	 */
	public void setIgnoreLastRow(boolean b) {
		this.ignoreLastRow = b;
	}
	

	
}
