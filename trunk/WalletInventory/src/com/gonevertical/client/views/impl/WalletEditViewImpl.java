package com.gonevertical.client.views.impl;

import com.gonevertical.client.app.ApplicationFactory;
import com.gonevertical.client.app.requestfactory.dto.WalletDataProxy;
import com.gonevertical.client.views.WalletEditView;
import com.gonevertical.client.views.WalletEditView.Presenter;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class WalletEditViewImpl extends Composite implements WalletEditView {

  private Presenter presenter;
  
  private ApplicationFactory appFactory;
  
  private static WalletEditViewUiBinder uiBinder = GWT.create(WalletEditViewUiBinder.class);
  @UiField VerticalPanel pList;

  private WalletDataProxy walletData;

  interface WalletEditViewUiBinder extends UiBinder<Widget, WalletEditViewImpl> {
  }

  public WalletEditViewImpl() {
    initWidget(uiBinder.createAndBindUi(this));
  }
  
  @Override
  public void setPresenter(Presenter presenter) {
    this.presenter = presenter;
  }
  
  @Override
  public void setAppFactory(ApplicationFactory appFactory) {
    this.appFactory = appFactory;
  }
  
  public void setData(WalletDataProxy walletData) {
    this.walletData = walletData;
  }

  public void start() {
    // TODO
  }
}
