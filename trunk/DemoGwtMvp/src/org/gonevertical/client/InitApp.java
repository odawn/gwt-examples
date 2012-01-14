package org.gonevertical.client;

import org.gonevertical.client.app.ClientFactory;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class InitApp implements EntryPoint {

  public static final int VERSION = 1;

  /**
  * Global App Objects/Classes - using deferred binding to create object
  */
 private ClientFactory clientFactory = GWT.create(ClientFactory.class);
  
  public void onModuleLoad() {
    // done in ClientFactory
  }
  
}
