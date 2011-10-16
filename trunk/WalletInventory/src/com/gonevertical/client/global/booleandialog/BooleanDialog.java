package com.gonevertical.client.global.booleandialog;

import com.gonevertical.client.global.booleandialog.BooleanEvent.Selected;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.HandlerRegistration;

public class BooleanDialog extends DialogBox {

  private static BooleanDialogUiBinder uiBinder = GWT.create(BooleanDialogUiBinder.class);
  @UiField HorizontalPanel pMessage;
  @UiField HTML htmlMessage;
  @UiField PushButton bYes;
  @UiField PushButton bNo;
  
  
  interface BooleanDialogUiBinder extends UiBinder<Widget, BooleanDialog> {
  }

  public BooleanDialog(String message) {
    setWidget(uiBinder.createAndBindUi(this));
    
    setMessage(message);
  }
  
  public void setMessage(String message) {
    if (message == null) {
      htmlMessage.setHTML("");
    }
    htmlMessage.setHTML(SimpleHtmlSanitizer.sanitizeHtml(message));
  }

  @UiHandler("bYes")
  void onBYesClick(ClickEvent event) {
    hide();
    fireEvent(new BooleanEvent(Selected.TRUE));
  }
  
  @UiHandler("bNo")
  void onBNoClick(ClickEvent event) {
    hide();
    fireEvent(new BooleanEvent(Selected.FALSE));
  }
  
  public HandlerRegistration addSelectionHandler(BooleanEventHandler handler) {
    return addHandler(handler, BooleanEvent.TYPE);
  }
  
}
