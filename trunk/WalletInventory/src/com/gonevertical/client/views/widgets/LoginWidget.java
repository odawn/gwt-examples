package com.gonevertical.client.views.widgets;

import com.gonevertical.client.app.ClientFactory;
import com.gonevertical.client.app.activity.places.WalletListPlace;
import com.gonevertical.client.app.requestfactory.dto.UserDataProxy;
import com.gonevertical.client.app.user.AuthEvent;
import com.gonevertical.client.app.user.AuthEventHandler;
import com.gonevertical.client.app.user.AuthEvent.Auth;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;

public class LoginWidget extends Composite {

  private static LoginWidgetUiBinder uiBinder = GWT.create(LoginWidgetUiBinder.class);
  @UiField HTML htmlNick;
  @UiField HTML htmlUrl;

  private ClientFactory clientFactory;

  private boolean alreadyInit;

  interface LoginWidgetUiBinder extends UiBinder<Widget, LoginWidget> {
  }

  public LoginWidget() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  public void setClientFactory(ClientFactory clientFactory) {
    this.clientFactory = clientFactory;

    // this is overkill in here, but here for example
    if (alreadyInit == false) {
      clientFactory.getEventBus().addHandler(AuthEvent.TYPE, new AuthEventHandler() {
        public void onAuthEvent(AuthEvent event) {
          Auth e = event.getAuthEvent();
          setState(e, event.getUserData());
        }
      });
    }
    alreadyInit = true;
    
    // maybe userData has already been loaded before this inits
    if (clientFactory.getUserData() != null) {
      setState(Auth.LOGGEDIN, clientFactory.getUserData());
    }
  }

  private void setState(Auth auth, UserDataProxy userData) {
    if (auth == Auth.LOGGEDIN) {
      setLoggedIn(userData);

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
    String qs = Window.Location.getQueryString();
    if (qs != null) {
      url += URL.encode(qs);
    }

    // This is a must, always clean before draw
    SafeHtmlBuilder builder = new SafeHtmlBuilder();
    builder.appendHtmlConstant("<a href='" + url + "'>")
    .appendEscaped("Sign In")
    .appendHtmlConstant("</a>");
    htmlUrl.setHTML(builder.toSafeHtml());
  }

  /**
   * logged in, lets go to the wallet list
   */
  private void setLoggedIn(UserDataProxy userData) {
    if (userData == null) {
      return;
    }

    setNick(userData);

    String url = userData.getLogoutUrl();
    String qs = Window.Location.getQueryString();
    if (qs != null) {
      url += URL.encode(qs);
    }

    // This is a must, always clean before draw
    SafeHtmlBuilder builder = new SafeHtmlBuilder();
    builder.appendHtmlConstant("<a href='" + url + "'>")
    .appendEscaped("Sign Out")
    .appendHtmlConstant("</a>");
    htmlUrl.setHTML(builder.toSafeHtml());
  }

  private void setNick(UserDataProxy userData) {
    if (userData == null) {
      return;
    }

    String nick = userData.getGoogleNickname();

    // This is a must, always clean before draw
    SafeHtmlBuilder builder = new SafeHtmlBuilder();
    builder.appendEscaped(nick);

    htmlNick.setHTML(builder.toSafeHtml());
  }

}
