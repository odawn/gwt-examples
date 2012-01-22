package org.gonevertical.dts.data;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlatFileSettingsData {
	
  private static final Logger log = LoggerFactory.getLogger(FlatFileSettingsData.class);

  //set all the settings into this object array
  private ArrayList<FlatFileSettingsData> settings = new ArrayList<FlatFileSettingsData>();
  
  public final static int CHANGEVALUE = 1;
  
  public final static int CHANGEVALUEWHENEMPTY = 2;
  
  public final static int CHANGEVALUEWITHREGEXMATCH = 3;
  
  public final static int CHANGEVALUEWITHREGEXREPLACE = 4;
  
  // TODO - this hasn't been completed yet
  public final static int CHANGEVALUEAUTO = 5;
  
  // english date format 01/31/2009
  public final static int CHANGEVALUEINTODATESTRING = 6;
  
  // sql datetime format
  public final static int CHANGEVALUEINTODATETIME = 7;
  
  public final static int FINDINFILE_REGEX_COLROW = 8;
  
  /**
   * constructor
   */
  public FlatFileSettingsData() {
  }

  /**
   * get the settings data for processing
   * 
   * @return
   */
  public ArrayList<FlatFileSettingsData> getSettings() {
    return settings;
  }

  // work on options logic
  // on row
  // on column
  // on both row + column
  
  // what action to take when this matches
  public int action = 0;
  
  // work on row x
  public int row = -1;
  
  // work on col y 
  public int column = -1;
  
  // match or replace regex
  public String regex = null;
  
  // value to replace with match regex
  public String value = null;
  
  // match a header field and transform the row;
  public String headerField = null;
  
  /* TODO add more options, that is just row, or just column for all types
   * TODO -1 is that of don't match column or row
   * */
  
  public void setchangeValue(int row, int column, String value) {
    FlatFileSettingsData ffsd = new FlatFileSettingsData();
    ffsd.action = CHANGEVALUE;
    ffsd.row = row;
    ffsd.column = column;
    ffsd.value = value;
    settings.add(ffsd);
  }
  
  public void setchangeValueWhenEmptyMatch(int row, int column, String value) {
    FlatFileSettingsData ffsd = new FlatFileSettingsData();
    ffsd.action = CHANGEVALUEWHENEMPTY;
    ffsd.row = row;
    ffsd.column = column;
    ffsd.value = value;
    settings.add(ffsd);
  }
  
  public void setchangeValueWithRegexMatch(int row, int column, String regex, String value) {
    FlatFileSettingsData ffsd = new FlatFileSettingsData();
    ffsd.action = CHANGEVALUEWITHREGEXMATCH;
    ffsd.row = row;
    ffsd.column = column;
    ffsd.value = value;
    ffsd.regex = regex;
    settings.add(ffsd);
  }
  
  public void setchangeValueWithRegexReplace(int row, int column, String regex) {
    FlatFileSettingsData ffsd = new FlatFileSettingsData();
    ffsd.action = CHANGEVALUEWITHREGEXREPLACE;
    ffsd.row = row;
    ffsd.column = column;
    ffsd.regex = regex;
    settings.add(ffsd);
  }
  
  public void setchangeValueIntoDateStringFormat(int row, int column) {
    FlatFileSettingsData ffsd = new FlatFileSettingsData();
    ffsd.action = CHANGEVALUEINTODATESTRING;
    ffsd.row = row;
    ffsd.column = column;
    settings.add(ffsd);
  }
  
  /**
   * match row 0 / header field and transform the column into data string format mm/dd/yyyy
   * @param headerField
   */
  public void setchangeValueIntoDateStringFormat(String headerField) {
    FlatFileSettingsData ffsd = new FlatFileSettingsData();
    ffsd.action = CHANGEVALUEINTODATESTRING;
    ffsd.headerField = headerField;
    settings.add(ffsd);
  }
  
  /**
   * Change column into 2009-12-05 00:00:00 datetime string
   * @param headerField
   */
  public void setchangeValueIntoSqlDateTimeFormat(String headerField) {
    FlatFileSettingsData ffsd = new FlatFileSettingsData();
    ffsd.action = CHANGEVALUEINTODATETIME;
    ffsd.headerField = headerField;
    settings.add(ffsd);
  }
  
  
  public void setchangeValueIntoSqlDateTimeFormat(int row, int column) {
    FlatFileSettingsData ffsd = new FlatFileSettingsData();
    ffsd.action = CHANGEVALUEINTODATETIME;
    ffsd.row = row;
    ffsd.column = column;
    settings.add(ffsd);
  }
  
  /**
   * find a value in a file
   * 
   * @param row
   * @param column
   * @param regex
   */
  public void findInFile_byRegex(int row, int column, String regex) {
    FlatFileSettingsData ffsd = new FlatFileSettingsData();
    ffsd.action = FINDINFILE_REGEX_COLROW;
    ffsd.row = row;
    ffsd.column = column;
    ffsd.regex = regex;
    settings.add(ffsd);
  }
  
  public void findInFile_byRegex(String headerField, String regex) {
    FlatFileSettingsData ffsd = new FlatFileSettingsData();
    ffsd.action = FINDINFILE_REGEX_COLROW;
    ffsd.headerField = headerField;
    ffsd.regex = regex;
    settings.add(ffsd);
  }
  
  /**
   * auto format a to best values, like date and phone numbers to consistent format 
   * 
   * TODO date format like ? datetime or common eng
   * @param row
   * @param column
   */
  private void setautoFormatValue(int row, int column) {
    
  }
  
}
