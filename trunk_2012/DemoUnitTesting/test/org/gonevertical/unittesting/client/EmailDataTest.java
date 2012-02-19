package org.gonevertical.unittesting.client;

import org.gonevertical.unittesting.client.presenter.EmailData;

import com.google.gwt.junit.client.GWTTestCase;

public class EmailDataTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "org.gonevertical.unittesting.DemoUnitTesting";
  }
  
  public void testWorks() {
    assertEquals(true, true);
  }

  public void testSetEmail() {
    String left = "test@g.com";
    EmailData emailData = new EmailData();
    emailData.setEmail(left);
    String right = emailData.getEmail();
    assertEquals(left, right);
  }
}
