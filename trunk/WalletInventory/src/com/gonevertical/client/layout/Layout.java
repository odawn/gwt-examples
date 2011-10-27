package com.gonevertical.client.layout;

import com.gonevertical.client.app.ClientFactory;
import com.gonevertical.client.app.ClientFactoryImpl;
import com.gonevertical.client.app.user.AuthEvent;
import com.gonevertical.client.app.user.AuthEventHandler;
import com.gonevertical.client.app.user.AuthEvent.Auth;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.ScriptElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.gonevertical.client.views.widgets.LoginWidget;
import com.google.gwt.user.client.ui.FlowPanel;

public class Layout extends Composite {

  private static LayoutUiBinder uiBinder = GWT.create(LayoutUiBinder.class);
  @UiField SimplePanel pContent;
  @UiField LoginWidget wLogin;
  @UiField FlowPanel adPanel;
  @UiField VerticalPanel vpWidget;
  @UiField VerticalPanel vpFooter;
  @UiField VerticalPanel vpMain;
  @UiField FlowPanel fpPlusOne;
  
  private ClientFactory clientFactory;

  interface LayoutUiBinder extends UiBinder<Widget, Layout> {
  }

  public Layout(ClientFactory clientFactory) {
    this.clientFactory = clientFactory;
    initWidget(uiBinder.createAndBindUi(this));
    
    wLogin.setClientFactory(clientFactory);
    
    clientFactory.getEventBus().addHandler(AuthEvent.TYPE, new AuthEventHandler() {
      public void onAuthEvent(AuthEvent event) {
        //Auth e = event.getAuthEvent();
        moveAdsDivTimed();
      }
    });
    
    drawPlusOne();
    
    //addStyleName("test1");
    //vpWidget.addStyleName("test2");
    //vpMain.addStyleName("test3");
    //vpFooter.addStyleName("test4");
  }
  
  public SimplePanel getContentPanel() {
    return pContent;
  }
  
  /**
   * moving a few seconds later, for the effect only
   */
  private void moveAdsDivTimed() {
    Timer t = new Timer() {
      public void run() {
        moveAdsDiv();
      }
    };
    t.schedule(500);
  }
  
  /**
   * move the ads div to the better location in the app layout
   */
  public void moveAdsDiv() {
    RootPanel w = RootPanel.get("ads");
    adPanel.add(w);
  }
  
  private void drawPlusOne() {
    String s = "<g:plusone href=\"https://mywalletinventory.appspot.com\"></g:plusone>";
    HTML h = new HTML(s);
    fpPlusOne.add(h);
    
    Document doc = Document.get();
    ScriptElement script = doc.createScriptElement();
    script.setSrc("https://apis.google.com/js/plusone.js");
    script.setType("text/javascript");
    script.setLang("javascript");
    doc.getBody().appendChild(script);
  }
}
