package org.gonevertical.client.namelist;

import org.gonevertical.client.requestfactory.NameDataProxy;

import com.google.gwt.event.shared.GwtEvent;

public class DeleteNameEvent extends GwtEvent<DeleteNameHandler> {

  public static Type<DeleteNameHandler> TYPE = new Type<DeleteNameHandler>();
  
  private NameDataProxy data;
  
  public DeleteNameEvent() {
  }

  @Override
  public Type<DeleteNameHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(DeleteNameHandler handler) {
    handler.onEvent(this);
  }

 
}
