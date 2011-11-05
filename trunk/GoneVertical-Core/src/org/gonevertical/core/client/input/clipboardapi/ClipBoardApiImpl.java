package org.gonevertical.core.client.input.clipboardapi;

import com.google.gwt.user.client.Event;

public class ClipBoardApiImpl {

  public String getText(Event event) {
    System.out.println("ClipBoardApiImpl() used");
    return getTextJsni(event);
  }
  
  /**
   * WebKit/Chrome/Safari
   * 
   * @param event
   * @return
   */
  public native String getTextJsni(Event event) /*-{
    var text = "";
    if (event.clipboardData) {
      try {
        text = event.clipboardData.getData("Text");
        return text;
      } catch (e) {}
    }
    return text;
  }-*/;
  


}
