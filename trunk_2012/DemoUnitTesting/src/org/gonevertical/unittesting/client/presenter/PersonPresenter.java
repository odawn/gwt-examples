package org.gonevertical.unittesting.client.presenter;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.FlowPanel;
import org.gonevertical.unittesting.client.list.EmailList;

public class PersonPresenter extends Composite {

  private static PersonPresenterUiBinder uiBinder = GWT.create(PersonPresenterUiBinder.class);
  @UiField EmailList emailList;

  interface PersonPresenterUiBinder extends UiBinder<Widget, PersonPresenter> {
  }

  public PersonPresenter() {
    initWidget(uiBinder.createAndBindUi(this));
  }
  
  public void start() {
    List<EmailData> emails = getEmailList();
    emailList.setEmails(emails);
    emailList.draw();
  }

  private List<EmailData> getEmailList() {
    EmailData a = new EmailData();
    a.setEmail("a@a.com");
    
    EmailData b = new EmailData();
    b.setEmail("b@b.com");
    
    EmailData c = new EmailData();
    c.setEmail("c@c.com");
    
    ArrayList<EmailData> list = new ArrayList<EmailData>();
    list.add(a);
    list.add(b);
    list.add(c);
    
    return list;
  }

}
