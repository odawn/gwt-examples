package org.gonevertical.core.client.onpaste;

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
        //alert('text1=' + text);
        return text;
      } catch (e) {}
    }
    return text;
  }-*/;
  


}
