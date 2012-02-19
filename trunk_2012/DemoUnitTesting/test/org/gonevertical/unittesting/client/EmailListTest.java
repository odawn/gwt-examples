package org.gonevertical.unittesting.client;

import java.util.ArrayList;

import org.gonevertical.unittesting.client.item.EmailItem;
import org.gonevertical.unittesting.client.list.EmailList;
import org.gonevertical.unittesting.client.list.EmailWidgetsState;
import org.gonevertical.unittesting.client.presenter.EmailData;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * compile your project first
 */
public class EmailListTest extends GWTTestCase {

  @Override
  public String getModuleName() {
    return "org.gonevertical.unittesting.DemoUnitTesting";
  }
  
  public void testWorks() {
    assertEquals(true, true);
  }

  public void testSetEmailData() {

    EmailData a = new EmailData();
    a.setEmail("a@a.com");
    
    EmailData b = new EmailData();
    a.setEmail("b@b.com");
    
    EmailData c = new EmailData();
    c.setEmail("c@c.com");
    
    ArrayList<EmailData> emails = new ArrayList<EmailData>();
    emails.add(a);
    emails.add(b);
    emails.add(c);
    
    EmailList widget = new EmailList();
    RootPanel.get().add(widget);
    widget.setEmails(emails);
    widget.draw();
    
    assertEquals(3, widget.getListCount());
    
  }
  
  public void testSetEmailData2() {

    EmailData a = new EmailData();
    a.setEmail("a@a.com");
    
    ArrayList<EmailData> emails = new ArrayList<EmailData>();
    emails.add(a);
    
    EmailList widget = new EmailList();
    RootPanel.get().add(widget);
    widget.setEmails(emails);
    widget.draw();
    
    assertEquals(1, widget.getListCount());
  }
  
  /**
   * Did the list Data make it to item data
   */
  public void testSetEmailData_MakeitToList() {

    EmailData a = new EmailData();
    a.setEmail("a@a.com");
    
    ArrayList<EmailData> emails = new ArrayList<EmailData>();
    emails.add(a);
    
    EmailList widget = new EmailList();
    RootPanel.get().add(widget);
    widget.setEmails(emails);
    widget.draw();
    
    EmailItem item = widget.getEmaiItemInList(0);
    EmailData emaildata = item.getEmailData();
    
    assertEquals(a.getEmail(), emaildata.getEmail());
  }
}
