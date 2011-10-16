package com.gonevertical.client.views;

import com.gonevertical.client.app.ApplicationFactory;
import com.gonevertical.client.app.requestfactory.dto.WalletDataProxy;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.ui.IsWidget;

public interface WalletEditView extends IsWidget {

  interface Presenter {
    void goTo(Place place);
  }

  void setPresenter(Presenter presenter);
  
  void setAppFactory(ApplicationFactory appFactory);
  
  void setData(WalletDataProxy walletData);
  
  void draw();

  
}
