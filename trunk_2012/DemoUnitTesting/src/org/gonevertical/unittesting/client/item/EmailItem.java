package org.gonevertical.unittesting.client.item;

import org.gonevertical.unittesting.client.list.EmailWidgetsState;
import org.gonevertical.unittesting.client.presenter.EmailData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.event.dom.client.ClickEvent;

public class EmailItem extends Composite {
  
  interface EmailItemUiBinder extends UiBinder<Widget, EmailItem> {}
  
  private static EmailItemUiBinder uiBinder = GWT.create(EmailItemUiBinder.class);
  
  @UiField 
  TextBox tbEmail;
  
  @UiField 
  HTML htmlEmail;
  @UiField PushButton bEdit;
  
  private EmailData emailData;
  
  private EmailWidgetsState state;

  public EmailItem() {
    initWidget(uiBinder.createAndBindUi(this));
    
    setState(EmailWidgetsState.VIEW);
  }

  public void setEmailData(EmailData emailData) {
    this.emailData = emailData;
  }
  
  public void draw() {
    drawEmail();
  }
  
  /**
   * set widget state to edit or view
   * @param state
   */
  public void setState(EmailWidgetsState state) {
    this.state = state;
    if (state == EmailWidgetsState.VIEW) {
      displayView();
    } else if (state == EmailWidgetsState.EDIT) {
      displayEdit();
    }
  }
  
  /**
   * change back in forth between states
   */
  public void setState() {
    if (state == EmailWidgetsState.VIEW) {
      setState(EmailWidgetsState.EDIT);
    } else if (state == EmailWidgetsState.EDIT) {
      setState(EmailWidgetsState.VIEW);
    }
  }
  
  public EmailWidgetsState getState() {
    return state;
  }

  private void displayView() {
    tbEmail.setVisible(false);
    htmlEmail.setVisible(true);
  }

  private void displayEdit() {
    tbEmail.setVisible(true);
    htmlEmail.setVisible(false);
  }

  private void drawEmail() {
    String s = "";
    if (emailData != null && emailData.getEmail() != null) {
      s = emailData.getEmail();
    }
    drawEmailTextBox(s);
    drawEmailHtml(s);
  }
  
  private void drawEmailTextBox(String value) {
    tbEmail.setValue(value);
  }

  private void drawEmailHtml(String value) {
    SafeHtml html = SimpleHtmlSanitizer.sanitizeHtml(value);
    htmlEmail.setHTML(html);
  }

  @UiHandler("tbEmail")
  void onTbEmailChange(ChangeEvent event) {
    String value = tbEmail.getValue();
    drawEmailHtml(value);
  }

  public EmailData getEmailData() {
    return emailData;
  }
  
  public boolean isEmailHtmlVisible() {
    return htmlEmail.isVisible();
  }
  
  public boolean isEmailTextBoxVisible() {
    return tbEmail.isVisible();
  }
  
  @UiHandler("bEdit")
  void onBEditClick(ClickEvent event) {
    setState();
  }
}
