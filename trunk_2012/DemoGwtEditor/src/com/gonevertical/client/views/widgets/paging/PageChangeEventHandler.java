package com.gonevertical.client.views.widgets.paging;

import com.google.gwt.event.shared.EventHandler;
import com.google.web.bindery.requestfactory.shared.EntityProxy;

public interface PageChangeEventHandler extends EventHandler {
  
  public void onEditEvent(PageChangeEvent event);
  
}
