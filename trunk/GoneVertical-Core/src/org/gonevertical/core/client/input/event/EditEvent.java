package org.gonevertical.core.client.input.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * A boolean event, true | false OR yes | no
 * 
 * @author Brandon Donnelson
 * {@link http://gwt-examples.googelcode.com} 
 *
 */
public class EditEvent extends GwtEvent<EditEventHandler> {

  public static Type<EditEventHandler> TYPE = new Type<EditEventHandler>();

  public static enum Edit {
    TRUE, FALSE;
  }

  private Edit edit;

  public EditEvent(Edit edit) {
    this.edit = edit;
  }

  @Override
  public Type<EditEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(EditEventHandler handler) {
    handler.onBooleanEvent(this);
  }

  public Edit getEditEvent() {
    return edit;
  }

}
