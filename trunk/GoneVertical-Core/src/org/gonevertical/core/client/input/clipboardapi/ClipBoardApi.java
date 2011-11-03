package org.gonevertical.core.client.input.clipboardapi;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Event;

/**
 * I'm copying the class layout for RichTextAreaImpl...
 * 
 * @author Brandon Donnelson
 * {@link http://gwt-examples.googelcode.com} 
 *
 */
public class ClipBoardApi {

  private ClipBoardApiImpl impl = GWT.create(ClipBoardApiImpl.class);
  
  public String getText(Event event) {
    return impl.getText(event);
  }
  
}
