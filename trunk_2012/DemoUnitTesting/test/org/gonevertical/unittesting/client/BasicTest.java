package org.gonevertical.unittesting.client;

import com.google.gwt.junit.client.GWTTestCase;

public class BasicTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "org.gonevertical.unittesting.DemoUnitTesting";
  }
  
  public void testWorks() {
    assertEquals(true, true);
  }

}
