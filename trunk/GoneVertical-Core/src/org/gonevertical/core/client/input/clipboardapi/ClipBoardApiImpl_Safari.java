package org.gonevertical.core.client.input.clipboardapi;

import com.google.gwt.user.client.Event;

public class ClipBoardApiImpl_Safari extends ClipBoardApiImpl_Standard {

  @Override
  public String getText(Event event) {
    System.out.println("ClipBoardApiImpl_Safari() used");
    return super.getText(event);
  }
  
}
