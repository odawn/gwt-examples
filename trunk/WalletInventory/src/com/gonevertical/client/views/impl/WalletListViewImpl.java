package com.gonevertical.client.views.impl;

import java.util.Iterator;
import java.util.List;

import com.gonevertical.client.app.ApplicationFactory;
import com.gonevertical.client.app.requestfactory.dto.WalletDataProxy;
import com.gonevertical.client.views.WalletListView;
import com.gonevertical.client.views.WalletListView.Presenter;
import com.gonevertical.client.views.widgets.WalletListItemWidget;
import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class WalletListViewImpl extends Composite implements WalletListView {

  private Presenter presenter;
  
  private ApplicationFactory appFactory;
  
  private static WalletListViewUiBinder uiBinder = GWT.create(WalletListViewUiBinder.class);
  @UiField HorizontalPanel hpMenu;
  @UiField VerticalPanel pList;
  
  private List<WalletDataProxy> walletData;

  interface WalletListViewUiBinder extends UiBinder<Widget, WalletListViewImpl> {
  }

  public WalletListViewImpl() {
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
  
  public void setData(List<WalletDataProxy> walletData) {
    this.walletData = walletData;
  }
  
  public void start() {
    pList.clear();
    
    if (walletData == null) {
      return;
    }
    
    int i=0; 
    Iterator<WalletDataProxy> itr = walletData.iterator();
    while(itr.hasNext()) {
      WalletDataProxy d = itr.next();
      add(i,d);
      i++;
    }
  }

  private void add(int i, WalletDataProxy walletDataProxy) {
    WalletListItemWidget wItem = new WalletListItemWidget();
    pList.add(wItem);
    wItem.setPresenter(presenter);
    wItem.setData(walletDataProxy);
    wItem.draw();
  }
  
}
