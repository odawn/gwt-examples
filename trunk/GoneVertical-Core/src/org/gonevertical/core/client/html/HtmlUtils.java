package org.gonevertical.core.client.html;

public class HtmlUtils {

  public static native String encodeURIComponent(String s) /*-{
    return encodeURIComponent(s);
  }-*/;

  public static native String decodeURIComponent(String s) /*-{
    return decodeURIComponent(s);
  }-*/;
  
}
