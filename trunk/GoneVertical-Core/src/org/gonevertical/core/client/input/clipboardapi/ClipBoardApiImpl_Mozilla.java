package org.gonevertical.core.client.input.clipboardapi;

import com.google.gwt.user.client.Event;

/**
 * 
 * this sucks
 *  
 *
 */
public class ClipBoardApiImpl_Mozilla extends ClipBoardApiImpl_Standard {
  
  @Override
  public String getText(Event event) {
    System.out.println("ClipBoardApiImpl_Mozilla() used");
    return getTextJsni(event);
  }
  
  public native String getTextJsni(Event event) /*-{
    
    alert('event=' + event.toString());
    
    var text = "";
    
     if ($wnd.clipboardData) {
      try {
        text = $wnd.clipboardData.getData("Text");
        alert('text0=' + text + ' event=' + event.toString());
        return text;
      } catch (e) {}
    }
  
    // lets try the standard - http://dev.w3.org/2006/webapi/clipops/
    if (event.clipboardData) {
      try {
        text = event.clipboardData.getData("Text");
        alert('text1=' + text + ' event=' + event.toString());
        return text;
      } catch (e) {}
    }
  
    // firefox probably won't work
    try {
      if (netscape.security.PrivilegeManager.enablePrivilege) {
        netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect");
      } else {
          //return "";
      }
    } catch (ex) {
        return "";
    }
  
    var clip = Components.classes["@mozilla.org/widget/clipboard;1"].getService(Components.interfaces.nsIClipboard);
    if (!clip) return "";
  
    var trans = Components.classes["@mozilla.org/widget/transferable;1"].createInstance(Components.interfaces.nsITransferable);
    if (!trans) return "";
  
    trans.addDataFlavor("text/unicode");
    clip.getData(trans, clip.kGlobalClipboard);
  
    var str = new Object();
    var strLength = new Object();
  
    trans.getTransferData("text/unicode", str, strLength);
    if (str) str = str.value.QueryInterface(Components.interfaces.nsISupportsString);
    if (str) text = str.data.substring(0, strLength.value / 2);
  
    //alert('text3=' + text);
  
    return text;
  
  }-*/;
  
}
