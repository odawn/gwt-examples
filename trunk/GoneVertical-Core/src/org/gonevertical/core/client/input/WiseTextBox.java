package org.gonevertical.core.client.input;

import org.gonevertical.core.client.html.HtmlSanitizerUtils;
import org.gonevertical.core.client.style.ComputedStyle;

import com.google.gwt.event.dom.client.BlurEvent;
import com.google.gwt.event.dom.client.BlurHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.FocusEvent;
import com.google.gwt.event.dom.client.FocusHandler;
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
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;

public class WiseTextBox extends TextBox {

  /**
   * clone these style properties
   */
  private String[] styles = { 
      "direction", 
      "font",
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

  /**
   * original textbox size set
   */
  private boolean orginalSet;

  /**
   * original textbox width
   */
  private int originalWidth;

  /**
   * orginal textbox height
   */
  private int originalHeight;

  /**
   * allow some room for future typing
   */
  private int headroomWidthPadding = 7;

  /**
   * allow for some room below the cursor
   */
  private int headroomHeightPadding = 7;

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

  private boolean hideBorderUntilHover;

  private String defaultText;

  private boolean grow;

  public WiseTextBox() {
    super();
    setup(false, true);
  }

  public WiseTextBox(boolean hideBorderUntilHover, boolean grow) {
    super();
    setup(hideBorderUntilHover, grow);
  }

  private void setup(boolean hideBorderUntilHover, boolean grow) {
    this.hideBorderUntilHover = hideBorderUntilHover;
    this.grow = grow;

    addStyleName("gv-core-WiseTextBox");

    setUpEditHover();

    setupHandlers();
  }

  private void setupHandlers() {

    addMouseOverHandler(new MouseOverHandler() {
      public void onMouseOver(MouseOverEvent event) {
        if (hideBorderUntilHover == true) {
          setEdit(true);
        }
      }
    });

    addMouseOutHandler(new MouseOutHandler() {
      public void onMouseOut(MouseOutEvent event) {
        if (hideBorderUntilHover == true) {
          setEdit(false);  
        }
      }
    });

    addTouchStartHandler(new TouchStartHandler() {
      public void onTouchStart(TouchStartEvent event) {
        if (hideBorderUntilHover == true) {
          setTouched();  
        }
      }
    });

    addKeyUpHandler(new KeyUpHandler() {
      public void onKeyUp(KeyUpEvent event) {
        if (defaultText != null) {
          drawDefaultText();
        }
      }
    });

    addKeyPressHandler(new KeyPressHandler() {
      public void onKeyPress(KeyPressEvent event) {
        if (grow == true) {
          setNewSize();
        }
      }
    });

    addChangeHandler(new ChangeHandler() {
      public void onChange(ChangeEvent event) {
        setDefaultText();
      }
    });

    addFocusHandler(new FocusHandler() {
      public void onFocus(FocusEvent event) {
        setZeroText();
      }
    });

    addBlurHandler(new BlurHandler() {
      public void onBlur(BlurEvent event) {
        setDefaultText();
      }
    });
  }

  public void setEditHover(boolean hideBorderUntilHover) {
    this.hideBorderUntilHover = hideBorderUntilHover;
    setUpEditHover();
  }

  public void setDefaultText(String defaultText) {
    this.defaultText = defaultText;
    addStyleName("gv-core-WiseTextBox-default");
    setText(defaultText);
  }

  private void setDefaultText() {
    if (getText().trim().length() != 0) {
      return;
    }
    addStyleName("gv-core-WiseTextBox-default");
    setText(defaultText); 
  }

  private void drawDefaultText() {
    if (defaultText == null) {
      return;
    }
    if (getText().trim().equals(defaultText) == true) {
      addStyleName("gv-core-WiseTextBox-default");
    } else {
      removeStyleName("gv-core-WiseTextBox-default");
    }
  }

  /**
   * when focusing, and the default text is set, set it to zero text
   */
  private void setZeroText() {
    if (defaultText == null) {
      return;
    }
    if (defaultText.equals(getText().trim()) == true) {
      setText("");
    }
  }

  /**
   * hide border until hovering over the input
   */
  private void setUpEditHover() {
    if (hideBorderUntilHover == true) {
      setEdit(false);
    }   
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
    removeStyleName("gv-core-WiseTextBox-noborder");
  }

  private void setView() {
    addStyleName("gv-core-WiseTextBox-noborder");
  }

  private void setNewSize() {

    // only done once
    setOriginalSize();

    // only done once
    setupHiddenPanel();

    // doevery time
    getCurrentTextWidth();

    if (width > originalWidth) {
      super.setWidth(width + "px");
    }

  }

  private void getCurrentTextWidth() {

    String s = getText();
    s = s.replaceAll("\040", "&nbsp;");
    
    
    SafeHtml sh = HtmlSanitizerUtils.sanitizeHtml(s); // allowing more through the gate
    htmlForSizeTesting.setHTML(sh);

    width = htmlForSizeTesting.getOffsetWidth();
    width += headroomWidthPadding;

    //height = htmlForSizeTesting.getOffsetHeight();
    //height += headroomHeightPadding; 
  }

  private void setOriginalSize() {
    if (orginalSet == false) {
      originalWidth = getOffsetWidth();
      originalHeight = getOffsetHeight();
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
      //System.out.println("setStyle style=" + styles[i] + " prop=" + prop);
    }
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

  private void setupHiddenPanel() {
    if (htmlForSizeTesting != null) {
      return;
    }

    // create a panel I can hide
    hiddenPanel = new AbsolutePanel();
    RootPanel.get().add(hiddenPanel);

    // create a panel that won't go 100%
    HorizontalPanel hp = new HorizontalPanel();
    hiddenPanel.add(hp, 1000, 1000); // hide it
    //hiddenPanel.add(hp); // show it for debugging

    // create a spot to measure html
    htmlForSizeTesting = new HTML("", false); // note text area you would want to wrap
    hp.add(htmlForSizeTesting);

    // for debugging
    //hp.addStyleName("test1");

    // for some reason, the computed style takes a sec to kick in.... hmmmm
    cloneStyle();
  }

  @Override
  public void setText(String text) {
    if (defaultText != null && text.equals(defaultText) == false) {
      removeStyleName("gv-core-WiseTextBox-default"); // TODO setup a method for this
    }
    super.setText(text);
    setNewSize();
    // work around for intial setup not sure why
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
  }

  @Override
  public void setHeight(String height) {
    super.setHeight(height);
    height = height.replaceAll("^[0-9]", "");
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
