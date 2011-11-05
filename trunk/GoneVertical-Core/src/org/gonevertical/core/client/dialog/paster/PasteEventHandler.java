package org.gonevertical.core.client.dialog.paster;

import com.google.gwt.event.shared.EventHandler;

/**
 * For OnPaste Event
 * @author Brandon Donnelson
 * {@link http://gwt-examples.googelcode.com} 
 *
 */
public interface PasteEventHandler extends EventHandler {
  public void onEvent(PasteEvent event);
}
