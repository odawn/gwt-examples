package org.gonevertical.core.client.input.clipboardapi;

import com.google.gwt.user.client.Event;


public class ClipBoardApiImpl_IE6 extends ClipBoardApiImpl_Standard {

  @Override
  public String getText(Event event) {
    System.out.println("ClipBoardApiImpl_IE6 used");
    return getTextJsni(event);
  }
  
  public native String getTextJsni(Event event) /*-{
    var text = "";
    if ($wnd.clipboardData) { // IE
      try {
        text = $wnd.clipboardData.getData("Text");
        //alert('text2=' + text);
        return text;
      } catch (e) {}
    }
    return text;
  }-*/;
  
}
