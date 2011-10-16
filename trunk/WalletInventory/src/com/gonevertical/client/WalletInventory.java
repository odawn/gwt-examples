package com.gonevertical.client;

import com.gonevertical.client.app.ApplicationFactory;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class WalletInventory implements EntryPoint {

  /**
   * Global App Objects/Classes - using deferred binding to create object
   */
  private ApplicationFactory appFactory = GWT.create(ApplicationFactory.class);
  
  
  /**
   * first thing to load
   */
  public void onModuleLoad() {

    // The stuff inits in ApplicationFactoryImpl.java
    
    
  }

}
