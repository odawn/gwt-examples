package org.gonevertical.textboxexpand.client.V3;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.dom.client.TouchStartHandler;
import com.google.gwt.event.logical.shared.AttachEvent;

public class AutoTextAreaEdit extends TextArea {

  /**
   * clone these style properties for size calculation
   */
  private String[] styles = { 
      "color",
      "direction", 
      
      "fontFace", // @fontFace?
      "fontFamily", 
      "fontSize", 
      "fontSizeAdjust",
      "fontStretch",
      "fontStyle",
      "fontVariant",
      "fontWeight",
      
      "letterSpacing", 
      "lineHeight", 
      
      "padding",
      "paddingBottom",
      "paddingLeft",
      "paddingRight",
      "paddingTop",
      
      "textAlign",
      "textDecoration",
      "textIndent",
      "textJustify",
      "textOutline",
      "textShawdow",
      "textTransform",
      
      "wordSpacing"
      
      //"whiteSpacing" ?
      //"punctuationTrim" ?
  };

  /**
   * original TextArea size set
   */
  private boolean orginalSet;

  /**
   * original TextArea width
   */
  private int originalWidth;

  /**
   * orginal TextArea height
   */
  private int originalHeight;

  /**
   * allow some room for future typing
   */
  private int headroomWidthPadding = 7;

  /**
   * allow for some room below the cursor
   */
  private int headroomHeightPadding = 14;

  /**
   * someone for the htmlForSizeTesting to hide in
   */
  private AbsolutePanel hiddenPanel;

  /**
   * somewhere to calculate the size
   */
  private HTML htmlForSizeTesting;

  /**
   * track toucheds to element
   */
  private boolean isTouched;

  /**
   * calculated width with headroomWidth padding
   */
  private int width;

  /**
   * calculated height with headroomHeight padding
   */
  private int height;

  private boolean delayedClone;

  private boolean clonedOnce;

  /**
   * constructor
   */
  public AutoTextAreaEdit() {
    super();
    setup(false, true);
  }

  /**
   * constructor
   * @param hideBorderUntilHover - hover over box to see border
   * @param grow - auto grow to size of text
   */
  public AutoTextAreaEdit(boolean hideBorderUntilHover, boolean grow) {
    super();
    setup(hideBorderUntilHover, grow);
  }

  private void setup(boolean hideBorderUntilHover, boolean grow) {

    setStyleName("AutoTextAreaEdit");

    setupGrow(grow);

    setUpEditHover(hideBorderUntilHover);    
  }

  /**
   * grow the input
   * @param grow
   */
  private void setupGrow(boolean grow) {
    if (grow == false) {
      return;
    }
    addKeyUpHandler(new KeyUpHandler() {
      public void onKeyUp(KeyUpEvent event) {
        setNewSize();
      }
    });
  }

  /**
   * hide border until hovering over the input
   */
  private void setUpEditHover(boolean hideBorderUntilHover) {
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

    addTouchStartHandler(new TouchStartHandler() {
      public void onTouchStart(TouchStartEvent event) {
        setTouched();
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

  private void setTouched() {
    if (isTouched == false) {
      isTouched = true;
      setEdit(false);
    } else {
      isTouched = false;
      setEdit(true);
    }
  }

  private void setEdit() {
    removeStyleName("AutoTextAreaEdit-readonly");
  }

  private void setView() {
    addStyleName("AutoTextAreaEdit-readonly");
  }

  private void setNewSize() {
    
    // only done once
    setOriginalSize();
    
    // onlydone once - sets up a location to calculate size
    setHiddenPanel();
    
    // only done once
    cloneStyleOnce();
    
    // do everytime - check for textbox getting bigger
    setMovingWidth();
    
    // everytime - calculate the size width and height of text
    setCurrentTextSize();
    
    
    /* only use height, but could do both
    if (width > originalWidth) {
      setWidth(width + "px");
    }
    */
    
    if (height > originalHeight) {
      setHeight(height + "px");
    }
  }

  /**
   * for changing textbox width
   */
  private void setMovingWidth() {
    if (htmlForSizeTesting == null) {
      return;
    }
    //TODO what the heck is with the offset width differring. Maybe i'm tired. 
    int left = 0;
    try {
      left = Integer.parseInt(ComputedStyle.getStyleProperty(this.getElement(), "width").replaceAll("[^0-9]", ""));
    } catch (NumberFormatException e) {
      e.printStackTrace();
      return;
    }
    int right = htmlForSizeTesting.getOffsetWidth();
    if (left != right) {
      setTextContainerWidth(left + "");
    }
  }

  private void cloneStyleOnce() {
    if (clonedOnce == false) {
      clonedOnce = true;
      cloneStyle();
    }
  }

  private void setCurrentTextSize() {

    String s = getText();
    s = s.replaceAll("\040\040", "\040&nbsp;"); // deal with double spacing context in html
    s = s.replaceAll("\r\n", "</br>"); 
    s = s.replaceAll("\n\r", "</br>"); 
    s = s.replaceAll("\r", "</br>"); 
    s = s.replaceAll("\n", "</br>");
    //s = s.replaceAll("\t", "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
    
    //System.out.println("s=" + s);
    
    //SafeHtml sh = SimpleHtmlSanitizer.sanitizeHtml(s); //encodes breaks, need to allow </br>
    htmlForSizeTesting.setHTML(s);

    width = htmlForSizeTesting.getOffsetWidth();
    width += headroomWidthPadding;

    height = htmlForSizeTesting.getOffsetHeight();
    height += headroomHeightPadding; 
  }

  private void setOriginalSize() {    
    if (orginalSet == false) {
      originalWidth = getOffsetWidth();
      originalHeight = getOffsetHeight();
      //System.out.println("OriginalSize w=" + originalWidth + " h=" + originalHeight);
      orginalSet = true;
    }
  }

  private void cloneStyle() {
    if (htmlForSizeTesting == null) {
      return;
    }

    Element eleft = getElement();
    Element eright = htmlForSizeTesting.getElement();

    if (styles == null) { // skip the initial init
      return;
    }
    for (int i=0; i < styles.length; i++) {
      String prop = ComputedStyle.getStyleProperty(eleft, styles[i]);
      eright.getStyle().setProperty(styles[i], prop);
      
      // debug
      System.out.println("TextArea cloneStyle() style=" + styles[i] + " prop=" + prop);
    }
    
    //eright.getStyle().setProperty("wordWrap", "break-word");
    // css3 long word breaking like it will break aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa....
    eright.getStyle().setProperty("wordBreak", "break-all");
  }
  
  /**
   * delay the clone style, b/c computed style seems to take a moment longer than.. not sure why
   */
  private void cloneStyleTimed() {
    if (delayedClone == true) {
      return;
    }
    delayedClone = true;
    Timer t = new Timer() {
      public void run() {
        cloneStyle();
        setNewSize();
      }
    };
    t.schedule(600);
  }

  /**
   * TODO move this to a shared hidden div.
   */
  private void setHiddenPanel() {
    if (htmlForSizeTesting != null) {
      return;
    }

    // create a panel I can hide
    hiddenPanel = new AbsolutePanel();
    RootPanel.get().add(hiddenPanel);
    
    // create a spot to measure html
    // note text area you would want to wrap
    htmlForSizeTesting = new HTML("", true); 
    hiddenPanel.add(htmlForSizeTesting);
    htmlForSizeTesting.setStyleName("AutoTextAreaEdit-Break"); // css3 long word breaking
    //hiddenPanel.add(hp, -1000, -1000); // hide it from view
    
    // setup width constraint
    setTextContainerWidth(Integer.toString(getOffsetWidth()));

    // for debugging
    hiddenPanel.addStyleName("test1");
    htmlForSizeTesting.addStyleName("test2");
    
    // for some reason, the computed style takes a sec to kick in.... hmmmm, probably due to attaching time
    //cloneStyle();
  }
  
  private void setTextContainerWidth(String width) {
    if (htmlForSizeTesting == null) {
      return;
    }
    htmlForSizeTesting.setWidth(width + "px");
    System.out.println("setTextContainerWidth=" + width);
  }
  
  @Override
  public void setText(String text) {
    super.setText(text);
    setNewSize();
    
    // delay for attatching or whatever. workaround here
    cloneStyleTimed();
  };

  @Override
  public void setStyleDependentName(String styleSuffix, boolean add) {
    super.setStyleDependentName(styleSuffix, add);
    cloneStyle();
  };

  @Override
  public void setStyleName(String style) {
    super.setStyleName(style);
    cloneStyle();
  }

  @Override
  public void setStylePrimaryName(String style) {
    super.setStylePrimaryName(style);
    cloneStyle();
  }

  @Override
  public void setWidth(String width) {
    super.setWidth(width);
    width = width.replaceAll("[^0-9]", "");
    try {
      originalWidth = Integer.parseInt(width);
    } catch (NumberFormatException e) {
    }
    
    setTextContainerWidth(width);
  }
  
  @Override
  public void setHeight(String height) {
    super.setHeight(height);
    height = height.replaceAll("[^0-9]", "");
    try {
      originalHeight = Integer.parseInt(height);
    } catch (NumberFormatException e) {
    }
  }
  
  @Override
  public void setSize(String width, String height) {
    setWidth(width);
    setHeight(height);
  }

}
