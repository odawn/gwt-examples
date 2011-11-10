package org.gonevertical.core.client.style;

import com.google.gwt.user.client.Element;

public class ComputedStyle {
  /**
   * 
   * https://developer.mozilla.org/En/DOM:window.getComputedStyle
   * http://help.dottoro.com/ljswreks.php
   * 
   * @param el - dom element
   * @param prop - get the properties style, like width, fontFamily, fontSize, fontSizeAdjust. property must be camelCase
   * @return
   */
  public static native String getStyleProperty(Element el, String prop)  /*-{ 
    var computedStyle;
    if (document.defaultView && document.defaultView.getComputedStyle) { // standard (includes ie9)
      computedStyle = document.defaultView.getComputedStyle(el, null)[prop];
    
    } else if (el.currentStyle) { // IE older
      computedStyle = el.currentStyle[prop];
    
    } else { // inline style
      computedStyle = el.style[prop];
    }
    return computedStyle;
  }-*/;
  
  public static native String getStyle(Element el, String prop)  /*-{ 
    var style = el.style[prop];
    return style;
  }-*/;

}
