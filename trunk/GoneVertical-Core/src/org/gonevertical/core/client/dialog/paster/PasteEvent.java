package org.gonevertical.core.client.dialog.paster;

import com.google.gwt.event.shared.GwtEvent;

/**
 * OnPaste Event
 * 
 * @author Brandon Donnelson
 * {@link http://gwt-examples.googelcode.com} 
 *
 */
public class PasteEvent extends GwtEvent<PasteEventHandler> {

  public static Type<PasteEventHandler> TYPE = new Type<PasteEventHandler>();

  private String paste;
  
  public PasteEvent(String paste) {
    this.paste = paste;
  }

  @Override
  public Type<PasteEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(PasteEventHandler handler) {
    handler.onEvent(this);
  }

  public String getData() {
    return paste;
  }

}
