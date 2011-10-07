package org.gonevertical.client.namelist;

import com.google.gwt.event.shared.GwtEvent;

/**
 * authorization event, in response to logging into Google system
 * 
 * @author branflake2267
 *
 */
public class AddNameEvent extends GwtEvent<AddNameHandler> {

  public static Type<AddNameHandler> TYPE = new Type<AddNameHandler>();
  
  public AddNameEvent() {
  }

  @Override
  public Type<AddNameHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(AddNameHandler handler) {
    handler.onEvent(this);
  }

}
