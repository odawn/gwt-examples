package org.gonevertical.core.client.style;

import com.google.gwt.user.client.Element;

public class ComputedStyle {

  public static native String getStyleProperty(Element el, String prop)  /*-{ 
    
      if (document.defaultView && document.defaultView.getComputedStyle) {
        return document.defaultView.getComputedStyle(el, null)[prop];
        
      } else if (el.currentStyle) {
        return el.currentStyle[prop];
        
      } else {
        return el.style[prop];
      }
    
  }-*/;
    
  
  /*
   * 
   var computedStyle; 
    if (typeof element.currentStyle != 'undefined') { 
      computedStyle = element.currentStyle; 
    } else { 
      computedStyle = document.defaultView.getComputedStyle(element, null); 
    } 
    return computedStyle[prop];
     
   */
}
