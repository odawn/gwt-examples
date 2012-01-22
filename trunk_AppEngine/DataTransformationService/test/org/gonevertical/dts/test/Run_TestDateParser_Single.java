package org.gonevertical.dts.test;

import org.gonevertical.dts.lib.datetime.DateTimeParser;

public class Run_TestDateParser_Single {

  public static void main(String[] args) {
    
    DateTimeParser parse = new DateTimeParser();
    
    //parse.test_MySqlDate("8/6/2009 9:42", "2009-08-06 09:42:00"); 
    
    // 2009-05-14 00:00:00:000000
    
    parse.test_MySqlDate("907-200", "2009-05-14 00:00:00");
  }
  
}
