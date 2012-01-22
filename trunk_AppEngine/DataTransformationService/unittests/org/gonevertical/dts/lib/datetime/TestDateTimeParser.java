package org.gonevertical.dts.lib.datetime;


import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestDateTimeParser {

  private DateTimeParser dtp;

  @Before
  public void setUp() throws Exception {
    dtp = new DateTimeParser();
  }

  @After
  public void tearDown() throws Exception {
    dtp = null;
  }
  
  @Test
  public void test1() { 
    assertEquals(dtp.getDateMysql("04/01/2008"), "2008-04-01 00:00:00");
  } 

  @Test
  public void test3() {
    assertEquals(dtp.getDateMysql("04-01-08"), "2008-04-01 00:00:00");
  }
  
  @Test
  public void test4() {
    assertEquals(dtp.getDateMysql("4-5-09"), "2009-04-05 00:00:00");
  }
  
  @Test
  public void test5() {
    assertEquals(dtp.getDateMysql("5/6/2009"), "2009-05-06 00:00:00");
  }
  
  @Test
  public void test6() {
    assertEquals(dtp.getDateMysql("3-apr-09"), "2009-04-03 00:00:00");
  }
  
  @Test
  public void test6b() {
    assertEquals(dtp.getDateMysql("10-feb"), "2010-02-01 00:00:00");
    assertEquals(dtp.getDateMysql("9-feb"), "2009-02-01 00:00:00");
  }
  
  @Test
  public void test7() {
    assertEquals(dtp.getDateMysql("may 2009"), "2009-05-01 00:00:00");
  }
  
  @Test
  public void test8() {
    assertEquals(dtp.getDateMysql("apr 08"), "2008-04-01 00:00:00");
  }
  
  @Test
  public void test9() {
    assertEquals(dtp.getDateMysql("January 02, 2009"), "2009-01-02 00:00:00");
  }
  
  
  @Test
  public void test10() {
    assertEquals(dtp.getDateMysql("20091203000000"), "2009-12-03 00:00:00");
  }
  
  @Test
  public void test11() {
    assertEquals(dtp.getDateMysql("20111231"), "2011-12-31 00:00:00");
    assertEquals(dtp.getDateMysql("201002"), "2010-02-01 00:00:00");
  }
  
  @Test
  public void test12() {
    assertEquals(dtp.getDateMysql("jan 2009"), "2009-01-01 00:00:00");
  }
  
  @Test
  public void test13() {
    assertEquals(dtp.getDateMysql("2009-12-01 01:02:01"), "2009-12-01 01:02:01");
  }
  
  @Test
  public void test14() {
    assertEquals(dtp.getDateMysql("2009-12-01 00:00:00AM"), "2009-12-01 00:00:00");
  }
  
  @Test
  public void test15() {
    assertEquals(dtp.getDateMysql("02/23/2009 11:14:31"), "2009-02-23 11:14:31");
  }
  
  @Test
  public void test16() {
    assertEquals(dtp.getDateMysql("04/03/2009 01:09:34 PM"), "2009-04-03 13:09:34");
  }
  
  @Test
  public void test17() {
    assertEquals(dtp.getDateMysql("8/6/2009 9:42"), "2009-08-06 09:42:00");
  }
  
  @Test
  public void test18() {
    assertEquals(dtp.getDateMysql("8/6/2009 9:42"), "2009-08-06 09:42:00");
  }
  
  @Test
  public void test19() {
    assertEquals(dtp.getDateMysql("02/23/09 11:14 am"), "2009-02-23 11:14:00");
  }
  
  @Test
  public void test20() {
    assertEquals(dtp.getDateMysql("20090514000000000000"), "2009-05-14 00:00:00");
  }
  
  @Test
  public void test21() {
    assertEquals(dtp.getDateMysql("abc20090514000000000000"), "2009-05-14 00:00:00");
  }
  
  @Test
  public void test22() {
    assertEquals(dtp.getDateMysql("01/01/2009 0:00"), "2009-01-01 00:00:00");
  }
  
  @Test
  public void test23() {
    assertEquals(dtp.getDateMysql("2009-12-01 01:02:01.0"), "2009-12-01 01:02:01");
    assertEquals(dtp.getDateMysql("2009-12-01 01:02:01.92"), "2009-12-01 01:02:01");
  }
  
  @Test
  public void test24() {
    assertEquals(dtp.getDateMysql("11/5/2009 12:00:00 AM"), "2009-11-05 12:00:00");
  }
  
  @Test
  public void test25() {
  	String r = dtp.getDateMysql("3/30/2010 01:03:00am");
    assertEquals(r, "2010-03-30 01:03:00");
  }
  
  @Test
  public void test26() {
    assertEquals(dtp.getDateMysql("3/30/2010 2:12pm"), "2010-03-30 14:12:00");
  }
  
}
