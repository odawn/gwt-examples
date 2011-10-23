package com.gonevertical.client;

import com.gonevertical.client.app.ClientFactory;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WalletInventory implements EntryPoint {

  /**
   * Global App Objects/Classes - using deferred binding to create object
   */
  private ClientFactory clientFactory = GWT.create(ClientFactory.class);
  
  
  /**
   * first thing to load
   */
  public void onModuleLoad() {

    // The stuff inits in ApplicationFactoryImpl.java
    
    
  }

}
