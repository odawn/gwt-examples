package com.gonevertical.client.app.events;

import com.google.gwt.event.shared.EventHandler;
import com.google.web.bindery.requestfactory.shared.EntityProxy;

public interface DeleteEventHandler extends EventHandler {
  
  public void onDeleteEvent(DeleteEvent event);
  
}
