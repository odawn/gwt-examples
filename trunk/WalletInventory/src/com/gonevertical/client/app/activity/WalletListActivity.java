package com.gonevertical.client.app.activity;

import com.gonevertical.client.app.ApplicationFactory;
import com.gonevertical.client.app.activity.places.WalletListPlace;
import com.gonevertical.client.views.WalletListView;
import com.gonevertical.client.views.impl.WalletListViewImpl;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class WalletListActivity extends AbstractActivity implements WalletListView.Presenter{

  private WalletListView view;

  private ApplicationFactory appFactory;

  public WalletListActivity(WalletListPlace place, ApplicationFactory clientFactory) {
    this.appFactory = clientFactory;
  }

  /**
   * Invoked by the ActivityManager to start a new Activity
   */
  @Override
  public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
    if (view == null) {
      view = new WalletListViewImpl();
    }
    view.setPresenter(this);
    view.setAppFactory(appFactory);
    containerWidget.setWidget(view.asWidget());
    view.draw();
  }

  /**
   * Ask user before stopping this activity
   */
  @Override
  public String mayStop() {
    return "Please hold on. This activity is stopping.";
  }

  /**
   * Navigate to a new Place in the browser
   */
  public void goTo(Place place) {
    appFactory.getPlaceController().goTo(place);
  }
  
}
