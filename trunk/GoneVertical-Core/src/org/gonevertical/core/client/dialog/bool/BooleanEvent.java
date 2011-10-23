package org.gonevertical.core.client.dialog.bool;

import com.google.gwt.event.shared.GwtEvent;

/**
 * A boolean event, true | false OR yes | no
 * 
 * @author Brandon Donnelson
 * {@link http://gwt-examples.googelcode.com} 
 *
 */
public class BooleanEvent extends GwtEvent<BooleanEventHandler> {

  public static Type<BooleanEventHandler> TYPE = new Type<BooleanEventHandler>();

  public static enum Selected {
    TRUE, FALSE;
  }

  private Selected selected;

  public BooleanEvent(Selected selected) {
    this.selected = selected;
  }

  @Override
  public Type<BooleanEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(BooleanEventHandler handler) {
    handler.onBooleanEvent(this);
  }

  public Selected getBooleanEvent() {
    return selected;
  }

}
