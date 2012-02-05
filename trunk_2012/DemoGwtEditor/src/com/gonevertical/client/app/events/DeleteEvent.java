package com.gonevertical.client.app.events;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.requestfactory.shared.EntityProxy;

public class DeleteEvent extends GwtEvent<DeleteEventHandler> {

  public static Type<DeleteEventHandler> TYPE = new Type<DeleteEventHandler>();
  
  private Widget source;

  public DeleteEvent(Widget source) {
    this.source = source;
  }
  
  @Override
  public Type<DeleteEventHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(DeleteEventHandler handler) {
    handler.onDeleteEvent(this);
  }

  public Widget getSource() {
    return source;
  }
}
