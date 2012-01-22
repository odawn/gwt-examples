package org.gonevertical.dts.test.lib;

import org.gonevertical.dts.lib.datetime.DateTimeParser;

public class DateParserTests {

  private DateTimeParser parse = new DateTimeParser();
  
  public DateParserTests() {
  }

  public void run() {
    
    // test dates 
    
    parse.test_EngDate("04/01/2008", "04/01/2008");
    parse.test_EngDate("04-01-08", "04/01/2008");
    parse.test_EngDate("4-5-09", "04/05/2009");
    parse.test_EngDate("5/6/2009", "05/06/2009");
    parse.test_EngDate("3-apr-09", "04/03/2009");
    parse.test_EngDate("may 2009", "05/01/2009");
    parse.test_EngDate("apr 08", "04/01/2008");
    parse.test_EngDate("January 01, 2009", "01/01/2009");
    parse.test_EngDate("20091203000000", "12/03/2009");
    parse.test_EngDate("20091231", "12/31/2009");
    
    
    parse.test_MySqlDate("jan 2009", "2009-01-01 00:00:00");
    parse.test_MySqlDate("2009-12-01 01:02:01", "2009-12-01 01:02:01");
    parse.test_MySqlDate("2009-12-01 00:00:00AM", "2009-12-01 00:00:00"); 
    parse.test_MySqlDate("02/23/2009 11:14:31", "2009-02-23 11:14:31"); 
    parse.test_MySqlDate("04/03/2009 01:09:34 PM", "2009-04-03 13:09:34"); 
    parse.test_MySqlDate("8/6/2009 9:42", "2009-08-06 09:42:00"); 
    parse.test_MySqlDate("8/6/2009 9:42", "2009-08-06 09:42:00"); 
    
    
    parse.test_MySqlDate("02/23/09 11:14 am", "2009-02-23 11:14:00"); 
    
    parse.test_MySqlDate("20090514000000000000", "2009-05-14 00:00:00");
    parse.test_MySqlDate("abc20090514000000000000", "2009-05-14 00:00:00");
    
    parse.test_MySqlDate("01/01/2009 0:00", "2009-01-01 00:00:00"); 
  }
  
  
  
}
