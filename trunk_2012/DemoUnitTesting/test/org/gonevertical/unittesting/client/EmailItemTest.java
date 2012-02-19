package org.gonevertical.unittesting.client;

import org.gonevertical.unittesting.client.item.EmailItem;
import org.gonevertical.unittesting.client.list.EmailWidgetsState;
import org.gonevertical.unittesting.client.presenter.EmailData;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * compile your project first
 */
public class EmailItemTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "org.gonevertical.unittesting.DemoUnitTesting";
  }
  
  public void testWorks() {
    assertEquals(true, true);
  }

  public void testSetEmailData() {
    EmailData left = new EmailData();
    left.setEmail("test@g.com");
    
    EmailItem widget = new EmailItem();
    RootPanel.get().add(widget);
    
    widget.setEmailData(left);
    widget.getEmailData();
    EmailData right = widget.getEmailData();
    assertEquals(left.getEmail(), right.getEmail());
  }
  
  public void testDrawState() {
    EmailItem widget = new EmailItem();
    RootPanel.get().add(widget);
    
    EmailWidgetsState right = widget.getState();
    assertEquals(EmailWidgetsState.VIEW, right);
    
    widget.setState();
    assertEquals(EmailWidgetsState.EDIT, widget.getState());
    widget.setState();
    assertEquals(EmailWidgetsState.VIEW, widget.getState());
    widget.setState();
    assertEquals(EmailWidgetsState.EDIT, widget.getState());
  }
  
  public void testDrawView() {
    EmailData left = new EmailData();
    left.setEmail("test@g.com");
    
    EmailItem widget = new EmailItem();
    RootPanel.get().add(widget);

    widget.draw();
    
    assertEquals(true, widget.isEmailHtmlVisible());
    assertEquals(false, widget.isEmailTextBoxVisible());
  }
  
  public void testDrawEdit() {
    EmailData left = new EmailData();
    left.setEmail("test@g.com");
    
    EmailItem widget = new EmailItem();
    RootPanel.get().add(widget);

    widget.setState(EmailWidgetsState.EDIT);
    widget.draw();
    
    assertEquals(false, widget.isEmailHtmlVisible());
    assertEquals(true, widget.isEmailTextBoxVisible());
  }
  
}
