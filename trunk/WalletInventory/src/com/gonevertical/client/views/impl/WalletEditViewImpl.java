package com.gonevertical.client.views.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.gonevertical.client.app.ApplicationFactory;
import com.gonevertical.client.app.requestfactory.dto.WalletDataProxy;
import com.gonevertical.client.app.requestfactory.dto.WalletItemDataProxy;
import com.gonevertical.client.views.WalletEditView;
import com.gonevertical.client.views.widgets.WalletEditItemWidget;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

public class WalletEditViewImpl extends Composite implements WalletEditView {

  public static enum State {
    VIEW, EDIT;
  }
  private State stateIs;
  
  private Presenter presenter;

  private ApplicationFactory appFactory;

  private static WalletEditViewUiBinder uiBinder = GWT.create(WalletEditViewUiBinder.class);
  @UiField VerticalPanel pList;
  @UiField HTML htmlName;
  @UiField TextBox tbName;
  @UiField PushButton bFinished;
  @UiField PushButton bAdd;
  @UiField FocusPanel pFocusName;

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

  public void draw() {

    drawName();
    
    drawItems();
  }

  private void drawItems() {
    pList.clear();
    if (walletData == null || 
        walletData.getItems() == null || 
        walletData.getItems().size() == 0) {
      return;
    }
    
    int i=0;
    Iterator<WalletItemDataProxy> itr = walletData.getItems().iterator();
    while(itr.hasNext()) {
      WalletItemDataProxy d = itr.next();
      add(i, d);
      i++;
    }
  }
  
  private void add() {
    int i = pList.getWidgetCount();
    add(i, null);
  }

  private WalletEditItemWidget add(int i, WalletItemDataProxy itemData) {
    WalletEditItemWidget wItem = new WalletEditItemWidget();
    pList.add(wItem);
    wItem.addChangeHandler(new ChangeHandler() {
      public void onChange(ChangeEvent event) {
        setItemsData();
        save();
      }
    });
    wItem.setPresenter(presenter);
    wItem.setAppFactory(appFactory);
    wItem.setData(i, itemData);
    wItem.draw();
    return wItem;
  }

  private void setItemsData() {
    if (walletData == null) {
      walletData = appFactory.getRequestFactory().getWalletDataRequest().create(WalletDataProxy.class);
    }
    walletData.setItems(getItems());
  }

  private List<WalletItemDataProxy> getItems() {
    if (pList.getWidgetCount() == 0) {
      return null;
    }
    List<WalletItemDataProxy> items = new ArrayList<WalletItemDataProxy>();
    for (int i=0; i < pList.getWidgetCount(); i++) {
      WalletEditItemWidget wItem = (WalletEditItemWidget) pList.getWidget(i);
      items.add(wItem.getData());
    }
    return items;
  }

  private void drawName() {
    if (walletData == null || 
        walletData.getName() == null || 
        walletData.getName().trim().length() == 0) {
      tbName.setText("");
      htmlName.setHTML("");
      return;
    }
    
    String s = walletData.getName();
    SafeHtml sh = SimpleHtmlSanitizer.sanitizeHtml(s);
    tbName.setText(sh.toString());
    htmlName.setHTML(sh);
  }
  
  private void setNameData() {
    if (walletData == null) {
      walletData = appFactory.getRequestFactory().getWalletDataRequest().create(WalletDataProxy.class);
    }
    walletData.setName(getName());
  }

  private String getName() {
    String s = tbName.getText().trim();
    if (s.length() == 0) {
      s = null;
    }
    return s;
  }

  private void save() {
    Request<WalletDataProxy> req = appFactory.getRequestFactory().getWalletDataRequest().persist().using(walletData);
    req.fire(new Receiver<WalletDataProxy>() {
      public void onSuccess(WalletDataProxy walletData) {
        process(walletData);
      }
      public void onFailure(ServerFailure error) {
        super.onFailure(error);
      }
    });
  }
  
  private void process(WalletDataProxy walletData) {
    this.walletData = walletData;
  }
  
  private void setState(State state) {
    stateIs = state;
    if (state == State.VIEW) {
      setStateView();
    } else if (state == State.EDIT) {
      setStateEdit();
    }
  }

  private void setStateView() {
    htmlName.setVisible(true);
    tbName.setVisible(true);
  }

  private void setStateEdit() {
    htmlName.setVisible(false);
    tbName.setVisible(false);
  }
  
  @UiHandler("tbName")
  void onTbNameValueChange(ValueChangeEvent event) {
    setNameData();
    save();
    drawName();
  }
  
  @UiHandler("pFocusName")
  void onPFocusNameTouchStart(TouchStartEvent event) {
    if (stateIs == State.VIEW) {
      setState(State.EDIT);
    } else if (stateIs == State.EDIT) {
      setState(State.VIEW);
    }
  }
  
  @UiHandler("pFocusName")
  void onPFocusNameMouseOver(MouseOverEvent event) {
    setState(State.EDIT);
  }
  
  @UiHandler("pFocusName")
  void onPFocusNameMouseOut(MouseOutEvent event) {
    setState(State.VIEW); 
  }
  
  @UiHandler("bAdd")
  void onBAddClick(ClickEvent event) {
    add();
  }
}
