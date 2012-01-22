package org.gonevertical.dts.lib.datetime;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gonevertical.dts.lib.csv.Csv;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO not sure how I will do the international months yet
 * 
 * Experimental stage
 */
public class DateTimeParser {
	
  private static final Logger log = LoggerFactory.getLogger(DateTimeParser.class);

  /*
   * some formats I have seen. php strtotime is the ultimate
   * 12/01/2009
   * 2009/12/01
   * 01/12/2009
   * jan 01 2009
   * January 01, 2009  
   * ??? "jan 012009" don't know if this works
   * 2009-12-01 00:00:00
   * 2009-12-01 00:00:00AM
   * 20091201000000
   * 
   * 02/23/2009 11:14:31
   * 03/03/2009 09:09:31 AM 
   */

  // date string given
  private String datetime = null;

  // date object created from date string given
  private Date date = null;

  // return type mm/dd/yyyy
  public final static int TYPE_ENG_DATE = 1;
  
  // return type yyyy-MM-dd HH:MM:SS
  public final static int TYPE_MYSQL_DATETIME = 2;

  // did it match to one of the formats?
  public boolean isDate = false;
  
  /**
   * constructor
   */
  public DateTimeParser() {
  }

  /**
   * parse the date time into Date
   *   this will return original value if nothing is matched
   *   
   * @param parseDateTime
   * @return
   */
  public Date getDate(String parseDateTime) {
    this.datetime = parseDateTime;
    getDate(TYPE_ENG_DATE);
    return date;
  }
  
  /**
   * parse datetime and get it in calendar format. 
   * 
   * @param parseDateTime
   * @param cal - init the calendar to your locale if you like
   * @return
   */
  public Calendar getCalendar(String parseDateTime, Calendar cal) {
    this.datetime = parseDateTime;
    getDate(TYPE_ENG_DATE);
    
    if (date != null) {
    	cal = Calendar.getInstance();
    	cal.setTime(date);
    } else {
    	cal = null;
    }
    return cal;
  }
  
  /**
   * does one of the date formats match the value, can this value be parsed as a datetime?
   * 
   * @param parseDateTime
   * @return
   */
  public boolean getIsDate(String parseDateTime) {
    isDate = false;
    this.datetime = parseDateTime;
    getDate(TYPE_ENG_DATE);
    return isDate;
  }
  
  /**
   * is the value just parsed recognized as a datetime value?
   * @return
   */
  public boolean getIsDate() {
    return isDate;
  }
  
  public boolean getIsDateExplicit(String parseDateTime) {
    isDate = false;
    this.datetime = parseDateTime;
    getDateExplicit(TYPE_MYSQL_DATETIME);
    return isDate;
  }
  
  /**
   * after parsing get the date object
   * @return
   */
  public Date getDate() {
    return this.date;
  }

  /**
   * get datetime in string mm/dd/yyyy
   * @return
   */
  public String getDate_EngString(String parseDateTime) {
    this.datetime = parseDateTime;
    String s = getDate(TYPE_ENG_DATE);
    return s;
  }

  public String getDateMysql(String parseDateTime) {
    this.datetime = parseDateTime;
    String s = getDate(TYPE_MYSQL_DATETIME);
    if (isDate == false) {
      System.out.println("DateTimeParser.getDateMysql(): Can't figure out date format. valueWas: " + parseDateTime);
    }
    return s;
  }
  
  public String getDateMysqlExplicit(String parseDateTime) {
    this.datetime = parseDateTime;
    String s = getDateExplicit(TYPE_MYSQL_DATETIME);
    return s;
  }
  
  /**
   * transform date into new format
   * 
   * @param type
   * @return
   */
  private String getDate(int type) {
    
    if (datetime == null) {
      return "";
    }
    
    datetime = datetime.trim().toLowerCase();
    
    // reset it just in case
    date = null;
    
    DateFormat df = null;
    if (type == TYPE_ENG_DATE) {
      df = new SimpleDateFormat("MM/dd/yyyy");
      
    } else if (type == TYPE_MYSQL_DATETIME) {
      df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
    
    String s = checkAll(df);
    
    return s;
  }

  private String getDateExplicit(int type) {
    
    if (datetime == null) {
      return "";
    }
    
    datetime = datetime.trim().toLowerCase();
    
    // reset it just in case
    date = null;
    
    DateFormat df = null;
    if (type == TYPE_ENG_DATE) {
      df = new SimpleDateFormat("MM/dd/yyyy");
      
    } else if (type == TYPE_MYSQL_DATETIME) {
      df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }
    
    String s = checkExplicit(df);
    
    return s;
  }
  
  private String checkAll(DateFormat df) {
    String s = "";
    if (checkIfPhoneNumber() == true) { // phone number 123-123-1234 is not a date
    	isDate = false;
    	s = datetime;
    } else if (checkIfNumber1() == true) { // this is not a date - 1235-1234
    	isDate = false;
    	s = datetime;
    } else if (checkforFormat_monthYearTogether() == true) {  // keep letter type first - like matching jan or Janurary
      s = df.format(date);
      isDate = true;
    } else if (checkforFormat_monthYear() == true) {  // keep letter type first - like matching jan or Janurary
      s = df.format(date);
      isDate = true;
    } else if (checkforFormat_monthDayYear() == true) { // keep letter type first - like matching jan or Janurary
      s = df.format(date);
      isDate = true;
    } else if (checkforFormat_weird1() == true) { // 3-apr-09
      s = df.format(date);
      isDate = true;
    } else if (checkforFormat_weird2() == true) { // 9-dec or 10-dec
      s = df.format(date);
      isDate = true;
    } else if (checkforFormat_datetime12hr() == true ) { // month first
      s = df.format(date);
      isDate = true;
    } else if (checkforFormat_datetime12hra() == true ) { // month first
      s = df.format(date);
      isDate = true;
    } else if (checkforFormat_datetime12hr2() == true ) { // year first
      s = df.format(date);
      isDate = true;
    } else if (checkforFormat_datetime24hr() == true ) { // month first
      s = df.format(date);
      isDate = true;
    } else if (checkforFormat_datetime24hra() == true ) { // month first
      s = df.format(date);
      isDate = true;
    } else if (checkforFormat_datetime24hr2() == true ) { // year first
      s = df.format(date);
      isDate = true;
    } else if (checkforFormat_datetime24hr3() == true ) { // `yyyy-mm-dd` only
      s = df.format(date);
      isDate = true;
    } else if (checkforFormat_datetime24hr4() == true ) {
      s = df.format(date);
      isDate = true;
    } else if (checkforFormat_engDateString() == true ) {
      s = df.format(date);
      isDate = true;
    } else if (checkforFormat_engDateStringNoDay() == true ) {
      s = df.format(date);
      isDate = true;
    } else if (checkforFormat_Intformat() == true) { 
      s = df.format(date);
      isDate = true;
    } else if (checkforFormat_Intformat_Short() == true) {
      s = df.format(date); // 20091231
      isDate = true;
    } else if (checkforFormat_Intformat_Shorter() == true) {
      s = df.format(date); // 20091231
      isDate = true;
    } else {
      // return orginal if not matched
      s = datetime;
      isDate = false;
    }
    return s;
  }
  
  /**
   * match date explicitly, that is take out the int formats, that could be other things
   * 
   * @param df
   * @return
   */
  private String checkExplicit(DateFormat df) {
    String s = "";
    if (checkIfPhoneNumber() == true) { // phone number 123-123-1234 is not a date
    	isDate = false;
    } else if (checkIfNumber1() == true) { // this is not a date - 1235-1234
      isDate = false;
      s = datetime;
      
    }  else if (checkforFormat_monthYearTogether() == true) {  // keep letter type first - like matching jan or Janurary
      s = df.format(date);
      isDate = true;
    } else if (checkforFormat_monthYear() == true) {  // keep letter type first - like matching jan or Janurary
      s = df.format(date);
      isDate = true;
    } else if (checkforFormat_monthDayYear() == true) { // keep letter type first - like matching jan or Janurary
      s = df.format(date);
      isDate = true;
    } else if (checkforFormat_weird1() == true) { // 3-apr-09
      s = df.format(date);
      isDate = true;
    } else if (checkforFormat_datetime12hr() == true ) { // month first
      s = df.format(date);
      isDate = true;
    } else if (checkforFormat_datetime12hra() == true ) { // month first
      s = df.format(date);
      isDate = true;
    } else if (checkforFormat_datetime12hr2() == true ) { // year first
      s = df.format(date);
      isDate = true;
    } else if (checkforFormat_datetime24hr() == true ) { // month first
      s = df.format(date);
      isDate = true;
    } else if (checkforFormat_datetime24hra() == true ) { // month first
      s = df.format(date);
      isDate = true;
    } else if (checkforFormat_datetime24hr2() == true ) { // year first
      s = df.format(date);
      isDate = true;
    } else if (checkforFormat_datetime24hr3() == true ) { // `yyyy-mm-dd` only
      s = df.format(date);
      isDate = true;
    } else if (checkforFormat_datetime24hr4() == true ) {
      s = df.format(date);
      isDate = true;
    } else if (checkforFormat_engDateString() == true ) {
      s = df.format(date);
      isDate = true;
    } else if (checkforFormat_engDateStringNoDay() == true ) {
      s = df.format(date);
      isDate = true;
    } else {
      // return orginal if not matched
      s = datetime;
      isDate = false;
    }
    return s;
  }
  
  private boolean checkIfNumber1() {
	  String regex = "^[0-9]+[.,\\-\040/]+?[0-9]{3,}$"; // 1235-456 is not a date
	  boolean found = false;
  	try {
      Pattern p = Pattern.compile(regex);
      Matcher m = p.matcher(datetime);
      found = m.find();
    } catch (Exception e) {
      System.out.println("findMatch: regex error");
      found = false;
    }
  	
	  return found;
  }
  
  private boolean checkIfPhoneNumber() {
	  String regex = "^[0-9]{3}[.,\\-\040/]+?[0-9]{3}[.,\\-\040/]+?[0-9]{4}$"; //123-123-1234 | 123-456 is not a date
	  boolean found = false;
  	try {
      Pattern p = Pattern.compile(regex);
      Matcher m = p.matcher(datetime);
      found = m.find();
    } catch (Exception e) {
      System.out.println("findMatch: regex error");
      found = false;
    }
  	
	  return found;
  }

	/**
   * match jan09 orJan2009 
   * 
   * @return
   */
  private boolean checkforFormat_monthYearTogether() {

    if (isMonthSpelledOut(datetime) == false) {
      return false;
    }
    
    // jan09 or jan2009
    String re = "^([a-zA-Z]+)([0-9]+)$";
    Pattern p = Pattern.compile(re);
    Matcher m = p.matcher(datetime);
    boolean found = m.find();

    int month = 0;
    int year = 0;
    if (found == true) {
      String mm = m.group(1);
      String yy = m.group(2);
      
      if (mm == null | yy == null) {
        return false;
      }
      
      month = getMonth(mm) - 1;
      year = getYear(yy);
    } else {
      return false;
    }

    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.DAY_OF_MONTH, 1);
    cal.set(Calendar.MONTH, month);
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);

    date = cal.getTime();

    return found;
  }
  
  /**
   * check format jan-09 or january-09
   * 
   * @return found
   */
  private boolean checkforFormat_monthYear() {

    if (isMonthSpelledOut(datetime) == false) {
      return false;
    }
    
    // jan-09 or january-09 or jan 09 or jan 2009  or jan, 2009
    String re = "^([a-zA-Z]+)[.,\\-\040]+?([0-9]+)$";
    Pattern p = Pattern.compile(re);
    Matcher m = p.matcher(datetime);
    boolean found = m.find();

    int month = 0;
    int year = 0;
    if (found == true) {
      String mm = m.group(1);
      String yy = m.group(2);
      
      if (mm == null | yy == null) {
        return false;
      }
      
      month = getMonth(mm) - 1;
      year = getYear(yy);
    } else {
      return false;
    }

    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.DAY_OF_MONTH, 1);
    cal.set(Calendar.MONTH, month);
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);

    date = cal.getTime();

    return found;
  }
  
  /**
   * jan 01 2009 or jan 01 09 or jan, 01 09 or jan, 01 2009
   * 
   * @return
   */
  private boolean checkforFormat_monthDayYear() {

    if (isMonthSpelledOut(datetime) == false) {
      return false;
    }
    
    String re = "^([a-zA-Z]+)[.,\\-\040/]+([0-9]+)[,\\-\040/]+([0-9]+)$";
    Pattern p = Pattern.compile(re);
    Matcher m = p.matcher(datetime);
    boolean found = m.find();

    int month = 0;
    int day = 0;
    int year = 0;
    if (found == true) {
      String mm = m.group(1);
      String dd = m.group(2);
      String yy = m.group(3);
      
      if (mm == null | dd == null | yy == null) {
        return false;
      }
      
      month = getMonth(mm) - 1;
      day = getDay(dd);
      year = getYear(yy);
      
    } else {
      return false;
    }

    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.DAY_OF_MONTH, day);
    cal.set(Calendar.MONTH, month);
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);

    date = cal.getTime();

    return found;
  }
  
  /**
   * mm/dd/yyyy hh:min:ss AM
   * 
   * @return
   */
  private boolean checkforFormat_datetime12hr() {

    String re = "^([0-9]{1,2})[/\\-\040\\.]([0-9]+)[/\\-\040\\.]([0-9]+)[\040]+([0-9]{2}):([0-9]{2}):([0-9]{2})?[/\\-\040\\.]?(am|pm)$";
    Pattern p = Pattern.compile(re);
    Matcher m = p.matcher(datetime);
    boolean found = m.find();

    int month = 0;
    int day = 0;
    int year = 0;
    int hour = 0;
    int minutes = 0;
    int seconds = 0;
    if (found == true) {
      String mm = m.group(1);
      String dd = m.group(2);
      String yy = m.group(3);
      String hh = m.group(4);
      String min = m.group(5);
      String ss = m.group(6);
      String ampm = m.group(7);
      
      if (mm == null | dd == null | yy == null | hh == null | min == null | ss == null | ampm == null) {
        return false;
      }
      
      month = getMonth(mm) - 1;
      day = getDay(dd);
      year = getYear(yy);
      if (ampm.equals("am")) {
        hour = Integer.parseInt(hh);  
      } else {
        hour = Integer.parseInt(hh) + 12;
      }
      
      minutes = Integer.parseInt(min);
      seconds = Integer.parseInt(ss);
       
    } else {
      return false;
    }
    
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.DAY_OF_MONTH, day);
    cal.set(Calendar.MONTH, month);
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.HOUR_OF_DAY, hour);
    cal.set(Calendar.MINUTE, minutes);
    cal.set(Calendar.SECOND, seconds);

    date = cal.getTime();

    return found;
  }

  /**
   * mm/dd/yyyy hh:min:ss
   * 
   * @return
   */
  private boolean checkforFormat_datetime24hr() {

    String re = "^([0-9]{1,2})[/\\-\040\\.]([0-9]+)[/\\-\040\\.]([0-9]+)[\040]+([0-9]{1,2}):([0-9]{2}):([0-9]{2})$";
    Pattern p = Pattern.compile(re);
    Matcher m = p.matcher(datetime);
    boolean found = m.find();

    int month = 0;
    int day = 0;
    int year = 0;
    int hour = 0;
    int minutes = 0;
    int seconds = 0;
    if (found == true) {
      String mm = m.group(1);
      String dd = m.group(2);
      String yy = m.group(3);
      String hh = m.group(4);
      String min = m.group(5);
      String ss = m.group(6);
      
      if (mm == null | dd == null | yy == null | hh == null | min == null | ss == null) {
        return false;
      }
      
      month = getMonth(mm) - 1;
      day = getDay(dd);
      year = getYear(yy);
      hour = Integer.parseInt(hh);
      minutes = Integer.parseInt(min);
      seconds = Integer.parseInt(ss);
       
    } else {
      return false;
    }
    
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.DAY_OF_MONTH, day);
    cal.set(Calendar.MONTH, month);
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.HOUR_OF_DAY, hour);
    cal.set(Calendar.MINUTE, minutes);
    cal.set(Calendar.SECOND, seconds);


    date = cal.getTime();

    return found;
  }
  


  /**
   * mm/dd/yyyy hh:mm AM
   * 
   * @return
   */
  private boolean checkforFormat_datetime12hra() {

    String re = "^([0-9]{1,2})[/\\-\040\\.]([0-9]{1,2})[/\\-\040\\.]([0-9]{2,4})[\040]+([0-9]{1,2}):([0-9]{1,2})?[/\\-\040\\.]?(am|pm)$";
    Pattern p = Pattern.compile(re);
    Matcher m = p.matcher(datetime);
    boolean found = m.find();

    int month = 0;
    int day = 0;
    int year = 0;
    int hour = 0;
    int minutes = 0;
    int seconds = 0;
    if (found == true) {
      String mm = m.group(1);
      String dd = m.group(2);
      String yy = m.group(3);
      String hh = m.group(4);
      String min = m.group(5);
     //String ss = m.group(6);
      String ampm = m.group(6);
      
      if (mm == null | dd == null | yy == null | hh == null | min == null | ampm == null) {
        return false;
      }
      
      month = getMonth(mm) - 1;
      day = getDay(dd);
      year = getYear(yy);
      if (ampm.equals("am")) {
        hour = Integer.parseInt(hh);  
      } else {
        hour = Integer.parseInt(hh) + 12;
      }
      
      minutes = Integer.parseInt(min);
      //seconds = Integer.parseInt(ss);
       
    } else {
      return false;
    }
    
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.DAY_OF_MONTH, day);
    cal.set(Calendar.MONTH, month);
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.HOUR_OF_DAY, hour);
    cal.set(Calendar.MINUTE, minutes);
    cal.set(Calendar.SECOND, 0);

    date = cal.getTime();

    return found;
  }

  /**
   * mm/dd/yyyy hh:min  (or[h:mm])
   * 
   * @return
   */
  private boolean checkforFormat_datetime24hra() {

    String re = "^([0-9]{1,2})[/\\-\040\\.]([0-9]+)[/\\-\040\\.]([0-9]+)[\040]+([0-9]{1,2}):([0-9]{2})$";
    Pattern p = Pattern.compile(re);
    Matcher m = p.matcher(datetime);
    boolean found = m.find();

    int month = 0;
    int day = 0;
    int year = 0;
    int hour = 0;
    int minutes = 0;
    int seconds = 0;
    if (found == true) {
      String mm = m.group(1);
      String dd = m.group(2);
      String yy = m.group(3);
      String hh = m.group(4);
      String min = m.group(5);
      //String ss = m.group(6);
      
      if (mm == null | dd == null | yy == null | hh == null | min == null) {
        return false;
      }
      
      month = getMonth(mm) - 1;
      day = getDay(dd);
      year = getYear(yy);
      hour = Integer.parseInt(hh);
      minutes = Integer.parseInt(min);
      //seconds = Integer.parseInt(ss);
       
    } else {
      return false;
    }
    
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.DAY_OF_MONTH, day);
    cal.set(Calendar.MONTH, month);
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.HOUR_OF_DAY, hour);
    cal.set(Calendar.MINUTE, minutes);
    cal.set(Calendar.SECOND, 0);


    date = cal.getTime();

    return found;
  }
  
  
  
  
  
  /**
   * yyyy-mm-dd hh:min:ss AM
   * 
   * @return
   */
  private boolean checkforFormat_datetime12hr2() {

    String re = "^([0-9]{4})[/\\-\040\\.]([0-9]+)[/\\-\040\\.]([0-9]+)[\040]+([0-9]{2}):([0-9]{2}):([0-9]{2})?[/\\-\040\\.]?(am|pm)$";
    Pattern p = Pattern.compile(re);
    Matcher m = p.matcher(datetime);
    boolean found = m.find();

    int month = 0;
    int day = 0;
    int year = 0;
    int hour = 0;
    int minutes = 0;
    int seconds = 0;
    if (found == true) {
      String yy = m.group(1);
      String mm = m.group(2);
      String dd = m.group(3);
      String hh = m.group(4);
      String min = m.group(5);
      String ss = m.group(6);
      String ampm = m.group(7);
      
      if (mm == null | dd == null | yy == null | hh == null | min == null | ss == null | ampm == null) {
        return false;
      }
      
      month = getMonth(mm) - 1;
      day = getDay(dd);
      year = getYear(yy);
      if (ampm.equals("am")) {
        hour = Integer.parseInt(hh);  
      } else {
        hour = Integer.parseInt(hh) + 12;
      }
      
      minutes = Integer.parseInt(min);
      seconds = Integer.parseInt(ss);
       
    } else {
      return false;
    }
    
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.DAY_OF_MONTH, day);
    cal.set(Calendar.MONTH, month);
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.HOUR_OF_DAY, hour);
    cal.set(Calendar.MINUTE, minutes);
    cal.set(Calendar.SECOND, seconds);

    date = cal.getTime();

    return found;
  }

  /**
   * yyyy-mm-dd hh:min:ss -- what happens when there is year starts first?
   * 
   * @return
   */
  private boolean checkforFormat_datetime24hr2() {

    String re = "^([0-9]{4})[/\\-\040\\.]([0-9]+)[/\\-\040\\.]([0-9]+)[\040]+([0-9]{2}):([0-9]{2}):([0-9]{2})$";
    Pattern p = Pattern.compile(re);
    Matcher m = p.matcher(datetime);
    boolean found = m.find();

    int month = 0;
    int day = 0;
    int year = 0;
    int hour = 0;
    int minutes = 0;
    int seconds = 0;
    if (found == true) {
      String yy = m.group(1);
      String mm = m.group(2);
      String dd = m.group(3);
      String hh = m.group(4);
      String min = m.group(5);
      String ss = m.group(6);
      
      if (mm == null | dd == null | yy == null | hh == null | min == null | ss == null) {
        return false;
      }
      
      month = getMonth(mm) - 1;
      day = getDay(dd);
      year = getYear(yy);
      hour = Integer.parseInt(hh);
      minutes = Integer.parseInt(min);
      seconds = Integer.parseInt(ss);
       
    } else {
      return false;
    }
    
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.DAY_OF_MONTH, day);
    cal.set(Calendar.MONTH, month);
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.HOUR_OF_DAY, hour);
    cal.set(Calendar.MINUTE, minutes);
    cal.set(Calendar.SECOND, seconds);

    date = cal.getTime();

    return found;
  }
  
  /**
   * 2009-12-31 00:00:00.0 - this format comes up when getting 
   * @return
   */
  private boolean checkforFormat_datetime24hr4() {

    String re = "^([0-9]{4})[/\\-\040\\.]([0-9]+)[/\\-\040\\.]([0-9]+)[\040]+([0-9]{2}):([0-9]{2}):([0-9]{2})\\.[0-9]+$";
    Pattern p = Pattern.compile(re);
    Matcher m = p.matcher(datetime);
    boolean found = m.find();

    int month = 0;
    int day = 0;
    int year = 0;
    int hour = 0;
    int minutes = 0;
    int seconds = 0;
    if (found == true) {
      String yy = m.group(1);
      String mm = m.group(2);
      String dd = m.group(3);
      String hh = m.group(4);
      String min = m.group(5);
      String ss = m.group(6);
      
      if (mm == null | dd == null | yy == null | hh == null | min == null | ss == null) {
        return false;
      }
      
      month = getMonth(mm) - 1;
      day = getDay(dd);
      year = getYear(yy);
      hour = Integer.parseInt(hh);
      minutes = Integer.parseInt(min);
      seconds = Integer.parseInt(ss);
       
    } else {
      return false;
    }
    
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.DAY_OF_MONTH, day);
    cal.set(Calendar.MONTH, month);
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.HOUR_OF_DAY, hour);
    cal.set(Calendar.MINUTE, minutes);
    cal.set(Calendar.SECOND, seconds);

    date = cal.getTime();

    return found;
  }
  
  /**
   * yyyy-mm-dd
   * @return
   */
  private boolean checkforFormat_datetime24hr3() {

    String re = "^([0-9]{4})[/\\-\040\\.]([0-9]+)[/\\-\040\\.]([0-9]+)$";
    Pattern p = Pattern.compile(re);
    Matcher m = p.matcher(datetime);
    boolean found = m.find();
  
    int month = 0;
    int day = 0;
    int year = 0;
    int hour = 0;
    int minutes = 0;
    int seconds = 0;
    if (found == true) {
      String yy = m.group(1);
      String mm = m.group(2);
      String dd = m.group(3);
  
      if (mm == null | dd == null | yy == null) {
        return false;
      }
  
      month = getMonth(mm) - 1;
      day = getDay(dd);
      year = getYear(yy);
      hour = 0;
      minutes = 0;
      seconds = 0;
  
    } else {
      return false;
    }
  
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.DAY_OF_MONTH, day);
    cal.set(Calendar.MONTH, month);
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.HOUR_OF_DAY, hour);
    cal.set(Calendar.MINUTE, minutes);
    cal.set(Calendar.SECOND, seconds);
  
    date = cal.getTime();
  
    return found;
  }
  
  /**
   * check for common string format mm/dd/yy or mm/dd/yyyy
   * 
   * @return found
   */
  private boolean checkforFormat_engDateString() {

    // mm/dd/yyyy
    String re = "^([0-9]{1,2})[/\\-\040\\.]+([0-9]+)[/\\-\040\\.]+([0-9]+)$";
    Pattern p = Pattern.compile(re);
    Matcher m = p.matcher(datetime);
    boolean found = m.find();

    int month = 0;
    int day = 0;
    int year = 0;
    if (found == true) {
      String mm = m.group(1);
      String dd = m.group(2);
      String yy = m.group(3);
      
      if (mm == null | dd == null | yy == null) {
        return false;
      }
      
      month = getMonth(mm) - 1;
      day = getDay(dd);
      year = getYear(yy);
      
    } else {
      return false;
    }
    
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.DAY_OF_MONTH, day);
    cal.set(Calendar.MONTH, month);
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);

    date = cal.getTime();

    return found;
  }

  /**
   * mm/yyyy
   * 
   * @return
   */
  private boolean checkforFormat_engDateStringNoDay() {

    String re = "^([0-9]+)[/\\-\040]+([0-9]+)$"; // got rid of period separation
    Pattern p = Pattern.compile(re);
    Matcher m = p.matcher(datetime);
    boolean found = m.find();

    int month = 0;
    int year = 0;
    if (found == true) {
      String mm = m.group(1);
      String yy = m.group(2);
      
      if (mm == null | yy == null) {
        return false;
      }
      
      month = getMonth(mm) - 1;
      year = getYear(yy);
    } else {
      return false;
    }

    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.DAY_OF_MONTH, 1);
    cal.set(Calendar.MONTH, month);
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);

    date = cal.getTime();

    return found;
  }
  
  /**
   * custom format -bsgbB620090200054003 or 20090200054003 or 200902000540030000000000
   * 
   * @return
   */
  private boolean checkforFormat_Intformat() {

    String re = "^.*?([0-9]{4})([0-9]{2})([0-9]{2})([0-9]{2})([0-9]{2})([0-9]{2})([0]+)?$";
    Pattern p = Pattern.compile(re);
    Matcher m = p.matcher(datetime);
    boolean found = m.find();

    int year = 0;
    int month = 0;
    int day = 0;
    int hour = 0;
    int min = 0;
    int sec = 0;
    if (found == true) {
      String yy = m.group(1);
      String mm = m.group(2);
      String dd = m.group(3);
      String hh = m.group(4);
      String mi = m.group(5);
      String ss = m.group(6);
      
      if (yy == null | mm == null | dd == null | hh == null | mi == null | ss == null) {
        return false;
      }
      
      year = getYear(yy);
      month = getMonth(mm) - 1;
      day = getDay(dd);
      hour = getTimeValue(hh);
      min = getTimeValue(mi);
      sec = getTimeValue(ss);
    } else {
      return false;
    }

    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.DAY_OF_MONTH, day);
    cal.set(Calendar.MONTH, month);
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.HOUR_OF_DAY, hour);
    cal.set(Calendar.MINUTE, min);
    cal.set(Calendar.SECOND, sec);

    date = cal.getTime();

    return found;
  }
  
  /**
   * 20091231 yyyymmdd
   * 
   * @return
   */
  private boolean checkforFormat_Intformat_Short() {
  
    String re = "^.*?([0-9]{4})([0-9]{2})([0-9]{2})$";
    Pattern p = Pattern.compile(re);
    Matcher m = p.matcher(datetime);
    boolean found = m.find();
  
    int year = 0;
    int month = 0;
    int day = 0;
    if (found == true) {
      String yy = m.group(1);
      String mm = m.group(2);
      String dd = m.group(3);

      if (yy == null | mm == null | dd == null) {
        return false;
      }
      
      year = getYear(yy);
      month = getMonth(mm) - 1;
      day = getDay(dd);
    } else {
      return false;
    }
  
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.DAY_OF_MONTH, day);
    cal.set(Calendar.MONTH, month);
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    date = cal.getTime();
    return found;
  }
  
  private boolean checkforFormat_Intformat_Shorter() {
    
  	//yyyymm
    String re = "^.*?([0-9]{4})([0-9]{2})$";
    Pattern p = Pattern.compile(re);
    Matcher m = p.matcher(datetime);
    boolean found = m.find();
  
    int year = 0;
    int month = 0;
    if (found == true) {
      String yy = m.group(1);
      String mm = m.group(2);

      if (yy == null | mm == null) {
        return false;
      }
      
      year = getYear(yy);
      month = getMonth(mm) - 1;
    } else {
      return false;
    }
  
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.DAY_OF_MONTH, 1);
    cal.set(Calendar.MONTH, month);
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);
    date = cal.getTime();
    return found;
  }
  
  /**
   * 3-apr-09 or 3 apr 09  or 3.apr.09 
   * 
   * @return
   */
  private boolean checkforFormat_weird1() {

    if (isMonthSpelledOut(datetime) == false) {
      return false;
    }
    
    // dd mm yy
    String re = "^([0-9]+)[\040-\\.]+([a-zA-Z]+)[\040-\\.]+([0-9]+)$";
    Pattern p = Pattern.compile(re);
    Matcher m = p.matcher(datetime);
    boolean found = m.find();

    int month = 0;
    int day = 0;
    int year = 0;
    if (found == true) {
      String dd = m.group(1);
      String mm = m.group(2);
      String yy = m.group(3);
      
      if (mm == null | dd == null | yy == null) {
        return false;
      }
      
      month = getMonth(mm) - 1;
      day = getDay(dd);
      year = getYear(yy);
      
    } else {
      return false;
    }
    
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.DAY_OF_MONTH, day);
    cal.set(Calendar.MONTH, month);
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);

    date = cal.getTime();

    return found;
  }
  
  /**
   * 9-dec or 10-feb
   * @return
   */
  private boolean checkforFormat_weird2() { 

    if (isMonthSpelledOut(datetime) == false) {
      return false;
    }
    
    // yy mm
    String re = "^([0-9]+)[\040-\\.]+([a-zA-Z]+)$";
    Pattern p = Pattern.compile(re);
    Matcher m = p.matcher(datetime);
    boolean found = m.find();

    int year = 0;
    int month = 0;
    if (found == true) {
      String yy = m.group(1);
      String mm = m.group(2);
      
      if (yy == null | mm == null) {
        return false;
      }
      
      year = getYear(yy);
      month = getMonth(mm) - 1;
      
      
    } else {
      return false;
    }
    
    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.DAY_OF_MONTH, 1);
    cal.set(Calendar.MONTH, month);
    cal.set(Calendar.YEAR, year);
    cal.set(Calendar.HOUR_OF_DAY, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.SECOND, 0);

    date = cal.getTime();

    return found;
  }
  
  private int getMonth(String s) {
    
    if (s == null){
      s = "";
    }
    s = s.toLowerCase();
    
    int month = -1;
    
    if (s.matches("[0-9]+")) {
      month = Integer.parseInt(s);
    } else if (s.contains("jan")) {
      month = 1;
    } else if (s.contains("feb")) {
      month = 2;
    } else if (s.contains("mar")) {
      month = 3;
    } else if (s.contains("apr")) {
      month = 4;
    } else if (s.contains("may")) {
      month = 5;
    } else if (s.contains("jun")) {
      month = 6;
    } else if (s.contains("jul")) {
      month = 7;
    } else if (s.contains("aug")) {
      month = 8;
    } else if (s.contains("sep")) {
      month = 9;
    } else if (s.contains("oct")) {
      month = 10;
    } else if (s.contains("nov")) {
      month = 11;
    } else if (s.contains("dec")) {
      month = 12;
    }
    
    if (month < 0) {
      System.out.println("Error parsing month:" + month);
    }
    
    return month;
  }

  private int getYear(String s) {

    int year = -1;
    if (s.matches("[0-9]{1}")) { 
    	year = getYearTwoDigit(s);
    } else if (s.matches("[0-9]{2}")) {
      year = getYearTwoDigit(s);
    } else if (s.matches("[0-9]{4}")) {
      year = Integer.parseInt(s);
    }  else { 
      // TODO - is there something I should do when no matches are found, and should be?
    }

    if (year < 0) {
      System.out.println("Error parsing year:" + year);
    }
    
    return year;
  }
  
  
  private int getYearTwoDigit(String s) {
    int y = Integer.parseInt(s);
    int currentYear = getCurrentYear();
     
    int yy = 0;
    if (y >= 0 && y <= currentYear) {
      yy = 2000 + Integer.parseInt(s);
    } else {
      yy = 1900 + Integer.parseInt(s);
    }
    
    return yy;
  }
  
  private int getCurrentYear() {
    Calendar cal = Calendar.getInstance();
    return cal.get(Calendar.YEAR);
  }

  private int getDay(String s) {
    int day = Integer.parseInt(s);
    return day;
  }

  private int getTimeValue(String s) {
    int t = Integer.parseInt(s);
    return t;
  }
  
  /**
   * TODO - more to do
   * 
   * @param s
   * @return
   */
  private boolean isDate(String s) {

    s = s.toLowerCase();

    if (s.length() == 0) {
      return false;
    }

    boolean b = false;

    if (s.contains("jan")) {
      b = true;
    } else if (s.contains("feb")) {
      b = true;
    } else if (s.contains("feb")) {
      b = true;
    } else if (s.contains("mar")) {
      b = true;
    } else if (s.contains("apr")) {
      b = true;
    } else if (s.contains("may")) {
      b = true;
    } else if (s.contains("jun")) {
      b = true;
    } else if (s.contains("jul")) {
      b = true;
    } else if (s.contains("aug")) {
      b = true;
    } else if (s.contains("sep")) {
      b = true;
    } else if (s.contains("oct")) {
      b = true;
    } else if (s.contains("nov")) {
      b = true;
    } else if (s.contains("dec")) {
      b = true;
    }

    // TODO - proof this later
    if (s.matches("[0-9]{1,2}[-/][0-9]{1,2}[-/][0-9]{2,4}.*")) {
      b = true;
    }

    return b;
  }

  private String test() {
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Date date = new Date();
    return dateFormat.format(date);
  }

  public boolean test_EngDate(String parsedate, String expected) {
    
    String result = getDate_EngString(parsedate);
    
    boolean b = false;
    if (result.equals(expected) == true) {
      b = true;
    }
    
    System.out.println("parsedate: " + parsedate + " result: " + result + " expected: " + expected + " " + b);
    
    return b;
  }
  
  public boolean test_MySqlDate(String parsedate, String expected) {
    
    String result = getDateMysql(parsedate);
    
    boolean b = false;
    if (result.equals(expected) == true) {
      b = true;
    }
    
    System.out.println("parsedate: " + parsedate + " result: " + result + " expected: " + expected + " " + b);
    
    return b;
  }

  /**
   * does the value have jan.* or feb.*...
   * 
   * @param s
   * @return
   */
  public boolean isMonthSpelledOut(String s) {
    boolean b = false;
    if (s.contains("jan")) {
      b = true;
    } else if (s.contains("feb")) {
      b = true;
    } else if (s.contains("feb")) {
      b = true;
    } else if (s.contains("mar")) {
      b = true;
    } else if (s.contains("apr")) {
      b = true;
    } else if (s.contains("may")) {
      b = true;
    } else if (s.contains("jun")) {
      b = true;
    } else if (s.contains("jul")) {
      b = true;
    } else if (s.contains("aug")) {
      b = true;
    } else if (s.contains("sep")) {
      b = true;
    } else if (s.contains("oct")) {
      b = true;
    } else if (s.contains("nov")) {
      b = true;
    } else if (s.contains("dec")) {
      b = true;
    }
    return b;
  }
  
}
