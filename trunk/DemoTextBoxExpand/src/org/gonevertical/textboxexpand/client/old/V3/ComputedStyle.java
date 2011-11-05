package org.gonevertical.textboxexpand.client.old.V3;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Element;

public class ComputedStyle {

  public static native String getStyleProperty(Element element, String style)  /*-{ 
    var computedStyle; 
    if (typeof element.currentStyle != 'undefined') { 
      computedStyle = element.currentStyle; 
    } else { 
      computedStyle = document.defaultView.getComputedStyle(element, null); 
    } 
    return computedStyle[style]; 
  }-*/;
    
}
