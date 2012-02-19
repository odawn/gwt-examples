package org.gonevertical.unittesting.client;

import junit.framework.Test;
import junit.framework.TestSuite;

import com.google.gwt.junit.tools.GWTTestSuite;

public class A_RunAllTests extends GWTTestSuite {

  public static Test suite() {
    TestSuite suite = new TestSuite("Widget unit testing demo");
    
    suite.addTestSuite(BasicTest.class);
    suite.addTestSuite(EmailDataTest.class);
    suite.addTestSuite(EmailItemTest.class);
    suite.addTestSuite(EmailListTest.class);
    
    return suite;
  
  }
}
