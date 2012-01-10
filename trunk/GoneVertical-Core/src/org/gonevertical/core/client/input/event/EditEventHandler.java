package org.gonevertical.core.client.input.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * 
 * @author Brandon Donnelson
 * {@link http://gwt-examples.googelcode.com} 
 *
 */
public interface EditEventHandler extends EventHandler {
  
  public void onBooleanEvent(EditEvent event);
  
}
