package com.gonevertical.client.views.widgets;

import com.gonevertical.client.app.requestfactory.dto.WalletDataProxy;
import com.gonevertical.client.views.WalletListView.Presenter;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class WalletListItemWidget extends Composite {

  private Presenter presenter;
  
  private static WalletListItemWidgetUiBinder uiBinder = GWT.create(WalletListItemWidgetUiBinder.class);
  @UiField HTML htmlName;
  @UiField TextBox tbName;

  private WalletDataProxy walletData;
  

  interface WalletListItemWidgetUiBinder extends
      UiBinder<Widget, WalletListItemWidget> {
  }

  public WalletListItemWidget() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  public void setData(WalletDataProxy walletDataProxy) {
    this.walletData = walletDataProxy; 
  }
  
  public void setPresenter(Presenter presenter) {
    this.presenter = presenter;
  }
  
  public void draw() {
    
  }

}
