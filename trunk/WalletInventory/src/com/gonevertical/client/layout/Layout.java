package com.gonevertical.client.layout;

import com.gonevertical.client.app.ClientFactory;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.gonevertical.client.views.widgets.LoginWidget;

public class Layout extends Composite {

  private static LayoutUiBinder uiBinder = GWT.create(LayoutUiBinder.class);
  @UiField SimplePanel pContent;
  @UiField LoginWidget wLogin;
  
  private ClientFactory clientFactory;

  interface LayoutUiBinder extends UiBinder<Widget, Layout> {
  }

  public Layout() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  public void setClientFactory(ClientFactory clientFactory) {
    this.clientFactory = clientFactory;
    wLogin.setClientFactory(clientFactory);
  }
  
  public SimplePanel getContentPanel() {
    return pContent;
  }
  
}
