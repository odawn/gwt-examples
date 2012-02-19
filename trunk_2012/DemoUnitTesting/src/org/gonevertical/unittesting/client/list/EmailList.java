package org.gonevertical.unittesting.client.list;

import java.util.Iterator;
import java.util.List;

import org.gonevertical.unittesting.client.item.EmailItem;
import org.gonevertical.unittesting.client.presenter.EmailData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;

public class EmailList extends Composite {

  interface EmailListUiBinder extends UiBinder<Widget, EmailList> {}
  
  private static EmailListUiBinder uiBinder = GWT.create(EmailListUiBinder.class);
  
  @UiField 
  FlowPanel emailList;

  private List<EmailData> emails;

  public EmailList() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  public void setEmails(List<EmailData> emails) {
    this.emails = emails;
  }
  
  public void draw() {
    if (emails == null) {
      return;
    }
    emailList.clear();
    
    Iterator<EmailData> itr = emails.iterator();
    while (itr.hasNext()) {
      add(itr.next());
    }
  }

  private void add(EmailData emailData) {
    EmailItem item = new EmailItem();
    emailList.add(item);
    item.setEmailData(emailData);
    item.draw();
  }
  
  public int getListCount() {
    return emailList.getWidgetCount();
  }

  public EmailItem getEmaiItemInList(int index) {
    if (getListCount() == 0) {
      return null;
    }
    return (EmailItem) emailList.getWidget(index);
  }
}
