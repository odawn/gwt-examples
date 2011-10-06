package org.gonevertical.client;

import org.gonevertical.client.namelist.NameList;
import org.gonevertical.client.requestfactory.ApplicationRequestFactory;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class DemoRequestFactory implements EntryPoint {

  
  public void onModuleLoad() {
    
    final EventBus eventBus = new SimpleEventBus();
    
    ApplicationRequestFactory requestFactory = GWT.create(ApplicationRequestFactory.class);
    
    requestFactory.initialize(eventBus);
    
    /**
     * my global items for this apps session
     */
    ClientPersistence cp = new ClientPersistence(eventBus, requestFactory);

    NameList nameList = new NameList(cp);
    RootPanel.get().add(nameList);
    
    nameList.draw();
  }
  
  
}
