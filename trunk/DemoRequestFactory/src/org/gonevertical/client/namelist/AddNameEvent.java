package org.gonevertical.client.namelist;

import org.gonevertical.client.requestfactory.NameDataProxy;

import com.google.gwt.event.shared.GwtEvent;

public class AddNameEvent extends GwtEvent<AddNameHandler> {

  public static Type<AddNameHandler> TYPE = new Type<AddNameHandler>();
  
  private NameDataProxy data;
  
  public AddNameEvent(NameDataProxy data) {
    this.data = data;
  }

  @Override
  public Type<AddNameHandler> getAssociatedType() {
    return TYPE;
  }

  @Override
  protected void dispatch(AddNameHandler handler) {
    handler.onEvent(this);
  }

  public NameDataProxy getNameData() {
    return data;
  }
}
