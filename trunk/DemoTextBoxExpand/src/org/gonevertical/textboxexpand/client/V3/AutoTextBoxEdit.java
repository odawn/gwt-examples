package org.gonevertical.textboxexpand.client.V3;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;

public class AutoTextBoxEdit extends TextBox {

  private boolean orginalSet;
  private int originalWidth;
  private int originalHeight;
  
  private int headroomWidthPadding;

  private String[] styles = { 
      "direction", 
      "fontFamily", 
      "fontSize", 
      "fontSizeAdjust",
      "fontStyle", 
      "fontWeight",
      "letterSpacing", 
      "lineHeight", 
      "padding",
      "textAlign",
      "textDecoration", 
      "textTransform",
      //"width",
      "wordSpacing" 
  };

  private AbsolutePanel hiddenPanel;
  
  private HTML htmlForSizeTesting;
  
  /**
   * constructor
   */
  public AutoTextBoxEdit(boolean hideBorderUntilHover, boolean growWidth) {
    super();
    
    setStyleName("AutoTextBoxEdit");
    
    /**
     * grow width as one types
     */
    if (growWidth == true) {
      addKeyUpHandler(new KeyUpHandler() {
        public void onKeyUp(KeyUpEvent event) {
          setSize();
        }
      });
    }

    /**
     * hide border until hovering over the textbox
     */
    if (hideBorderUntilHover == false) {
      return;
    }
    
    setEdit(false);
    
    addMouseOverHandler(new MouseOverHandler() {
      public void onMouseOver(MouseOverEvent event) {
        setEdit(true);
      }
    });

    addMouseOutHandler(new MouseOutHandler() {
      public void onMouseOut(MouseOutEvent event) {
        setEdit(false);
      }
    });

  }

  public void setEdit(boolean edit) {
    if (edit == true) {
      setEdit();

    } else {
      setView();
    }
  }

  private void setEdit() {
    removeStyleName("AutoTextBoxEdit-readonly");
  }

  private void setView() {
    addStyleName("AutoTextBoxEdit-readonly");
  }

  private void setSize() {
    setOriginalSize();

    int width = getCurrentTextWidth();

    if (width > originalWidth) {
      
      // for debugging
      GWT.log("setting width=" + width);
      
      setWidth(width + "px");
    }
  }

  private int getCurrentTextWidth() {

    // inputted text
    String s = getText();

    // make spaces mean something always
    s = s.replaceAll("\040", "&nbsp;");

    // clean it always
    SafeHtml sh = SimpleHtmlSanitizer.sanitizeHtml(s);
    htmlForSizeTesting.setHTML(sh);

    int width = htmlForSizeTesting.getOffsetWidth();

    width += headroomWidthPadding;
    
    return width;
  }

  private void setOriginalSize() {
    if (orginalSet == true) {
      return;
    }
    originalWidth = getOffsetHeight();
    originalHeight = getOffsetWidth();

    setupHiddenPanel();

    cloneStyle();
  }

  private void cloneStyle() {
    Element eleft = getElement();
    Style style = eleft.getStyle();

    Element eright = htmlForSizeTesting.getElement();

    for (int i=0; i < styles.length; i++) {
      String prop = style.getProperty(styles[i]);
      if (prop != null && prop.trim().length() != 0) {
        eright.getStyle().setProperty(styles[i], prop);
        
        // for debugging
        GWT.log("cloning style=" + styles[i] + " prop=" + prop);
      }
    }
  }

  private void setupHiddenPanel() {
    if (htmlForSizeTesting != null) {
      return;
    }
    
    // create a panel I can hide
    hiddenPanel = new AbsolutePanel();
    RootPanel.get().add(hiddenPanel);
    
    // create a panel that won't go 100%
    HorizontalPanel hp = new HorizontalPanel();
    //hiddenPanel.add(hp, -500, -500); // hide it
    hiddenPanel.add(hp); // show it for debugging
    
    
    // create a spot to measure html
    htmlForSizeTesting = new HTML("", false); // note text area you would want to wrap
    hp.add(htmlForSizeTesting);
    
    
    // for debugging
    hp.addStyleName("test1");
    
  }

}
