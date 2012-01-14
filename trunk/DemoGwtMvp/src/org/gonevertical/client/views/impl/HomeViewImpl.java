 package org.gonevertical.client.views.impl;

import org.gonevertical.client.app.ClientFactory;
import org.gonevertical.client.app.user.AuthEvent;
import org.gonevertical.client.app.user.AuthEvent.Auth;
import org.gonevertical.client.app.user.AuthEventHandler;
import org.gonevertical.client.requestfactory.dto.UserDataProxy;
import org.gonevertical.client.views.HomeView;
import org.gonevertical.core.client.loading.LoadingWidget;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class HomeViewImpl extends Composite implements HomeView {

  private Presenter presenter;

  private ClientFactory clientFactory;
  
  private boolean alreadyInit;

  private static HomeViewImplUiBinder uiBinder = GWT.create(HomeViewImplUiBinder.class);
  @UiField LoadingWidget wLoading;
  @UiField HTML htmlSignIn;

  interface HomeViewImplUiBinder extends UiBinder<Widget, HomeViewImpl> {
  }

  public HomeViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  @Override
  public void setPresenter(Presenter presenter) {
    this.presenter = presenter;
  }

  @Override
  public void setClientFactory(ClientFactory clientFactory) {
    this.clientFactory = clientFactory;
    
    // this is overkill in here, but here for example
    if (alreadyInit == false) {
      //System.out.println("SignViewImpl.setClientFactory(): init");
      clientFactory.getEventBus().addHandler(AuthEvent.TYPE, new AuthEventHandler() {
        public void onAuthEvent(AuthEvent event) {
          Auth e = event.getAuthEvent();
          setState(e, event.getUserData());
        }
      });
    }
    alreadyInit = true;
  }

  public void start() {
    wLoading.showLoading(true);
    
    if (clientFactory.getIsLoggedIn() == null) {
      // wait for login event b/c hasn't happened yet
      
    } else if (clientFactory.getIsLoggedIn() == true) {
      setLoggedIn();
      
    } else if (clientFactory.getIsLoggedIn() == false) { 
      setLoggedOut(clientFactory.getUserData());
    }
  }

  private void setState(Auth auth, UserDataProxy userData) {
    if (auth == Auth.LOGGEDIN) {
      setLoggedIn();
    } else if (auth == Auth.LOGGEDOUT) {
      setLoggedOut(userData);
    }
  }
  
  private void setLoggedIn() {
    wLoading.showLoading(false);
    //presenter.goTo(new WalletListPlace());
  }


  /**
   * lets use the url to show where to login at
   * @param userData
   */
  private void setLoggedOut(UserDataProxy userData) {
    wLoading.showLoading(false);
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
    .appendEscaped("Please Sign In")
    .appendHtmlConstant("</a>");
    htmlSignIn.setHTML(builder.toSafeHtml());
  }

}
