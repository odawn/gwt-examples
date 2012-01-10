package org.gonevertical.core.client.input.richtext;

import org.gonevertical.core.client.html.HtmlSanitizerUtils;
import org.gonevertical.core.client.input.event.EditEvent;
import org.gonevertical.core.client.input.event.EditEvent.Edit;
import org.gonevertical.core.client.input.event.EditEventHandler;
import org.gonevertical.core.client.input.richtext.WiseRichTextArea;
import org.gonevertical.core.client.style.ComputedStyle;

import com.gargoylesoftware.htmlunit.javascript.host.Event;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class EditWiseRichTextArea extends Composite {

  private WiseRichTextArea rta = new WiseRichTextArea();
  
  private HTML htmlEdit = new HTML();
  
  public EditWiseRichTextArea(EventBus eventBus) {
    
    htmlEdit.setVisible(false);
    
    VerticalPanel p = new VerticalPanel();
    p.add(rta);
    p.add(htmlEdit);
    
    initWidget(p);
    
    // TODO?
    //p.setCellVerticalAlignment(w, align)
   
    setup(eventBus);
  }

  private void setup(EventBus eventBus) {
    if (eventBus == null) {
      return;
    }
    
    eventBus.addHandler(EditEvent.TYPE, new EditEventHandler() {
      public void onBooleanEvent(EditEvent event) {
        setEdit(event.getEditEvent());
      }
    });
  }

  private void setEdit(Edit editEvent) {
    if (editEvent == null) {
      return;
    }
    if (editEvent == Edit.TRUE) {
      htmlEdit.setVisible(false);
      rta.setVisible(true);
      
    } else if (editEvent == Edit.FALSE) {
      htmlEdit.setHTML(rta.getHTML());
      rta.setVisible(false);
      htmlEdit.setVisible(true);
    }
  }

  public void setText(String text) {
    rta.setText(text);
  }
  
  public String getText() {
    return rta.getText();
  }
  
  public void setWidth(String width) {
    super.setWidth(width);
    rta.setWidth(width);
    //htmlEdit.setWidth(width);
  }
  
  public void setHeight(String height) {
    super.setHeight(height);
    rta.setHeight(height);
    //htmlEdit.setHeight(height);
  }
  
  public void setSize(String width, String height) {
    super.setSize(width, height);
    rta.setSize(width, height);
    //htmlEdit.setSize(width, height);
  }
  
  public void addStyle(String style) {
    rta.addStyleName(style);
  }
  
  public void setStyle(String style) {
    rta.setStyleName(style);
  }

  public void setFeatureGrow(boolean enabled) {
    rta.setFeatureGrow(enabled);
  }
  
  public void setFeatureDebu(boolean enabled) {
    rta.setFeatureDebug(enabled);
  }
  
  public void setFeatureHideBorderUntilHover(boolean enabled) {
    rta.setFeatureHideBorderUntilHover(enabled);
  }

  public void addBlurHandler(BlurHandler handler) {
    rta.addBlurHandler(handler);
  }
  
  public void setHTML(String html) {
    rta.setHTML(html);
  }

  public String getHTML() {
    return rta.getHTML();
  }

  public String getValue() {
    return rta.getValue();
  }

  public void setValue(String value) {
    rta.setValue(value);
  }
  
  public void setEnabled(boolean enabled) {
    rta.setEnabled(enabled);
  }
  
}
