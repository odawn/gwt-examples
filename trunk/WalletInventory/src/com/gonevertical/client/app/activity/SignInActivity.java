package com.gonevertical.client.app.activity;

import com.gonevertical.client.app.ApplicationFactory;
import com.gonevertical.client.app.activity.places.SignInPlace;
import com.gonevertical.client.views.SignInView;
import com.gonevertical.client.views.impl.SignInViewImpl;
import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

public class SignInActivity extends AbstractActivity implements SignInView.Presenter {

  private SignInView view;
  
  private ApplicationFactory appFactory;
  
  public SignInActivity(SignInPlace place, ApplicationFactory appFactory) {
    this.appFactory = appFactory;
  }

  /**
   * Invoked by the ActivityManager to start a new Activity
   */
  @Override
  public void start(AcceptsOneWidget containerWidget, EventBus eventBus) {
    if (view == null) {
      view = new SignInViewImpl();
    }
    view.setAppFactory(appFactory);
    view.setPresenter(this);
    containerWidget.setWidget(view.asWidget());
    view.start();
  }

  /* not needed
  @Override
  public String mayStop() {
    return "Please hold on. This activity is stopping.";
  }
  */

  /**
   * Navigate to a new Place in the browser
   */
  public void goTo(Place place) {
    appFactory.getPlaceController().goTo(place);
  }
  
}
