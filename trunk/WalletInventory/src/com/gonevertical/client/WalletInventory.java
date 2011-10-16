package com.gonevertical.client;

import com.gonevertical.client.app.ApplicationFactory;
import com.gonevertical.client.app.activity.ApplicationActivityMapper;
import com.gonevertical.client.app.activity.ApplicationPlaceHistoryMapper;
import com.gonevertical.client.app.activity.places.SignInPlace;
import com.gonevertical.client.views.SignInView;
import com.gonevertical.client.views.WalletEditView;
import com.gonevertical.client.views.WalletListView;
import com.gonevertical.client.views.impl.SignInViewImpl;
import com.gonevertical.client.views.impl.WalletEditViewImpl;
import com.gonevertical.client.views.impl.WalletListViewImpl;
import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

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
