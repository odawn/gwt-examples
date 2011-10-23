package com.gonevertical.client.app.activity;

import com.gonevertical.client.app.ClientFactory;
import com.gonevertical.client.app.activity.places.WalletEditPlace;
import com.gonevertical.client.app.requestfactory.dto.WalletDataProxy;
import com.gonevertical.client.views.WalletEditView;
import com.gonevertical.client.views.impl.WalletEditViewImpl;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class WalletEditActivity extends AbstractActivity implements WalletEditView.Presenter {

  private WalletEditView view;

  private ClientFactory clientFactory;
  
  private WalletDataProxy walletData;

  public WalletEditActivity(WalletEditPlace place, ClientFactory clientFactory) {
    this.walletData = place.getWalletData();
    this.clientFactory = clientFactory;
  }

  /**
   * Invoked by the ActivityManager to start a new Activity
   */
  @Override
  public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
    if (view == null) {
      view = new WalletEditViewImpl();
    }
    view.setPresenter(this);
    view.setClientFactory(clientFactory);
    containerWidget.setWidget(view.asWidget());
    view.setData(walletData);
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
    clientFactory.getPlaceController().goTo(place);
  }
  
}
