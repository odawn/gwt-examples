package org.gonevertical.core.client.onpaste;

import com.google.gwt.user.client.Event;


public class ClipBoardApiImpl_Mozilla extends ClipBoardApiImpl_Standard {
  
  @Override
  public String getText(Event event) {
    System.out.println("ClipBoardApiImpl_Mozilla() used");
    return getTextJsni(event);
  }
  
  public native String getTextJsni(Event event) /*-{
    var text = "";
  
    // ????
    try {
        if (netscape.security.PrivilegeManager.enablePrivilege) {
          netscape.security.PrivilegeManager.enablePrivilege("UniversalXPConnect");
        } else {
            //return "";
        }
    } catch (ex) {
        //return "";
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
