package com.gonevertical.client.views.impl;

import java.util.Iterator;
import java.util.List;

import com.gonevertical.client.app.ApplicationFactory;
import com.gonevertical.client.app.requestfactory.dto.WalletDataProxy;
import com.gonevertical.client.global.loadingwidget.LoadingWidget;
import com.gonevertical.client.views.WalletListView;
import com.gonevertical.client.views.widgets.WalletListItemWidget;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

public class WalletListViewImpl extends Composite implements WalletListView {

  private Presenter presenter;

  private ApplicationFactory appFactory;

  private static WalletListViewUiBinder uiBinder = GWT.create(WalletListViewUiBinder.class);
  @UiField HorizontalPanel hpMenu;
  @UiField VerticalPanel pList;
  @UiField PushButton bAdd;
  @UiField LoadingWidget wLoading;

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

  public void draw() {
    
    loadWallets();
    
  }

  private void loadWallets() {
    pList.clear();
    
    wLoading.showLoading(true); 
    
    // its important to note, that to get the children you need to use .with("children");
    Request<List<WalletDataProxy>> req = appFactory.getRequestFactory().getWalletDataRequest().findWalletDataByUser().with("items");
    req.fire(new Receiver<List<WalletDataProxy>>() {
      public void onSuccess(List<WalletDataProxy> walletData) {
        wLoading.showLoading(false);
        process(walletData);
      }
      public void onFailure(ServerFailure error) {
        wLoading.showLoading(false);
        super.onFailure(error);
      }
    });
  }

  private void process(List<WalletDataProxy> walletData) {
    this.walletData = walletData;
    if (walletData == null || walletData.size() == 0) {
      return;
    }
    int i=0; 
    Iterator<WalletDataProxy> itr = walletData.iterator();
    while(itr.hasNext()) {
      WalletDataProxy d = itr.next();
      add(i, d);
      i++;
    }
  }

  private void add() {
    int i = pList.getWidgetCount();
    WalletListItemWidget wItem = add(i, null);
    wItem.edit();
  }

  private WalletListItemWidget add(int i, WalletDataProxy walletDataProxy) {
    WalletListItemWidget wItem = new WalletListItemWidget();
    pList.add(wItem);
    wItem.setPresenter(presenter);
    wItem.setAppFactory(appFactory);
    wItem.setData(i, walletDataProxy);
    wItem.draw();
    return wItem;
  }

  @UiHandler("bAdd")
  public void onBAddClick(ClickEvent event) {
    add();
  }
}
