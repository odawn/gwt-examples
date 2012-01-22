package org.gonevertical.dts.lib;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SqlUtil {
  
  private static final Logger log = LoggerFactory.getLogger(SqlUtil.class);

  /**
   * get calendar as string like '2010-08-14 14:21:05'
   * 
   * @param cal
   * @return
   */
  public static String getCalendarAsSqlDt(Calendar cal) {
    if (cal == null) {
      return null;
    }
    
    int year = cal.get(Calendar.YEAR);
    int month = cal.get(Calendar.MONTH) + 1;
    int day = cal.get(Calendar.DATE);
    int hh = cal.get(Calendar.HOUR);
    int min = cal.get(Calendar.MINUTE);
    int sec = cal.get(Calendar.SECOND);
    
    String s = year + "-" + month + "-" + day + " " + hh + ":" + min + ":" + sec;
    
    return s;
  }
  
  /**
   * get date as string like '2010-08-14 14:21:05'
   * 
   * @param date
   * @return
   */
  public static String getDateAsSqlDt(java.util.Date date) {
    if (date == null) {
      return null;
    }
    
    int year = date.getYear() + 1900;
    int month = date.getMonth() + 1;
    int day = date.getDate();
    int hh = date.getHours();
    int min = date.getMinutes();
    int sec = date.getSeconds();
    
    String s = year + "-" + month + "-" + day + " " + hh + ":" + min + ":" + sec;
    
    return s;
  }
  
  /**
   * get sql from file
   * 
   * @param sfile
   * @return
   */
  public static String getSql(File file) {
    String buffer = "";
    FileInputStream fis = null;
    BufferedInputStream bis = null;
    DataInputStream dis = null;
    BufferedReader br = null;
    try {
      fis = new FileInputStream(file);
      bis = new BufferedInputStream(fis);
      dis = new DataInputStream(bis);
      br = new BufferedReader(new InputStreamReader(dis));
      String s = null;
      while ((s = br.readLine()) != null) {
        buffer += s;
      }
      br.close();
      fis.close();
      bis.close();
      dis.close();
    } catch (FileNotFoundException e) {
      //logger.error(e);
      e.printStackTrace();
      log.error("", e);
    } catch (IOException e) {
      //logger.error(e);
      e.printStackTrace();
      log.error("", e);
    }
    return buffer;
  }
  
}
