package com.gonevertical.client.views.impl;

import com.gonevertical.client.app.ApplicationFactory;
import com.gonevertical.client.app.activity.places.WalletEditPlace;
import com.gonevertical.client.app.activity.places.WalletListPlace;
import com.gonevertical.client.app.requestfactory.dto.UserDataProxy;
import com.gonevertical.client.app.user.AuthEvent;
import com.gonevertical.client.app.user.AuthEvent.Auth;
import com.gonevertical.client.app.user.AuthEventHandler;
import com.gonevertical.client.views.SignInView;
import com.gonevertical.client.views.SignInView.Presenter;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.google.gwt.uibinder.client.UiField;
import com.gonevertical.client.global.LoadingWidget;
import com.google.gwt.user.client.ui.HTML;

public class SignInViewImpl extends Composite implements SignInView {

  private Presenter presenter;

  private ApplicationFactory appFactory;
  
  private boolean alreadyInit;

  private static SignInViewImplUiBinder uiBinder = GWT.create(SignInViewImplUiBinder.class);
  @UiField LoadingWidget wLoading;
  @UiField HTML htmlSignIn;

  interface SignInViewImplUiBinder extends UiBinder<Widget, SignInViewImpl> {
  }

  public SignInViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));

  }

  @Override
  public void setPresenter(Presenter presenter) {
    this.presenter = presenter;
  }

  @Override
  public void setAppFactory(ApplicationFactory appFactory) {
    this.appFactory = appFactory;
    
    // this is overkill in here, but here for example
    if (alreadyInit == false) {
      System.out.println("SignViewImpl.setAppFactory(): init");
      appFactory.getEventBus().addHandler(AuthEvent.TYPE, new AuthEventHandler() {
        public void onAuthEvent(AuthEvent event) {
          Auth e = event.getAuthEvent();
          setState(e, event.getUserData());
        }
      });
    }
    alreadyInit = true;
  }

  public void start() {

    createUser();

  }

  private void setState(Auth auth, UserDataProxy userData) {
    if (auth == Auth.LOGGEDIN) {
      setLoggedIn();
    } else if (auth == Auth.LOGGEDOUT) {
      setLoggedOut(userData);
    }
  }

  /**
   * lets use the url to show where to login at
   * @param userData
   */
  private void setLoggedOut(UserDataProxy userData) {
    if (userData == null) {
      // this shouldn't happen, b/c we need the urls
      return;
    }
    String url = userData.getLoginUrl();
    
    // This is a must, always clean before draw
    SafeHtmlBuilder builder = new SafeHtmlBuilder();
    builder.appendHtmlConstant("<a href='" + url + "'>")
    .appendEscaped("Please Sign In")
    .appendHtmlConstant("</a>");
    htmlSignIn.setHTML(builder.toSafeHtml());
  }

  /**
   * logged in, lets go to the wallet list
   */
  private void setLoggedIn() {
    presenter.goTo(new WalletListPlace(null));
  }

  /**
   * this will create/lookup a user in the datastore according to the Google Login
   */
  private void createUser() {

    wLoading.showLoading(true, "Loading...");

    Request<UserDataProxy> req = appFactory.getRequestFactory().getUserDataRequest().createUserData();
    req.fire(new Receiver<UserDataProxy>() {
      public void onSuccess(UserDataProxy data) {
        wLoading.showLoading(false);
        process(data);
      }
      public void onFailure(ServerFailure error) {
        wLoading.showLoading(false);
        super.onFailure(error);
      }
    });
  }

  private void process(UserDataProxy data) {
    appFactory.setUserData(data);
    
    
  }

}
