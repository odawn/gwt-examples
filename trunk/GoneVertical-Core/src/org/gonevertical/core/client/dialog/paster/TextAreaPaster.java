package org.gonevertical.core.client.dialog.paster;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.event.dom.client.ClickEvent;

/**
 * I use this to target plain text pasting
 * 
 * @author Brandon Donnelson
 * {@link http://gwt-examples.googelcode.com} 
 *
 */
public class TextAreaPaster extends DialogBox {

  private static TextAreaPasterUiBinder uiBinder = GWT.create(TextAreaPasterUiBinder.class);
  @UiField TextArea textArea;
  @UiField PushButton pushButton;

  interface TextAreaPasterUiBinder extends UiBinder<Widget, TextAreaPaster> {
  }

  /**
   * constructor - init widget
   */
  public TextAreaPaster() {
    super(true);
    setAnimationEnabled(true);
    setWidget(uiBinder.createAndBindUi(this));
    sinkEvents(Event.ONPASTE);
    textArea.setFocus(true);
  }
  
  /**
   * observe OnPaste|Paste Event, pass the rest up
   */
  @Override
  public void onBrowserEvent(Event event) {
    super.onBrowserEvent(event);
    switch (event.getTypeInt()) {
    case Event.ONPASTE:
      event.stopPropagation();
      fireDelayed();
      break;
    }
  }
  
  /**
   * need to do this after the paste has made it to the textbox, not before
   */
  private void fireDelayed() {
    Timer t = new Timer() {
      public void run() {
        String s = textArea.getValue();
        fireEvent(new PasteEvent(s));
        close();
      }
    };
    t.schedule(5);
  }

  public HandlerRegistration addPasteHandler(PasteEventHandler handler) {
    return addHandler(handler, PasteEvent.TYPE);
  }
  
  @UiHandler("textArea")
  void onTextAreaKeyPress(KeyPressEvent event) {
    if (event.getCharCode() == KeyCodes.KEY_ESCAPE) {
      close();
    }
  }
  
  private void close() {
    hide();
    textArea.setValue("");
  }
  
  @UiHandler("pushButton")
  void onPushButtonClick(ClickEvent event) {
    close();
  }
}
