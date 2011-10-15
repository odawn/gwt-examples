package com.gonevertical.client.views.widgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

public class WalletEditItemWidget extends Composite {

  private static WalletEditItemWidgetUiBinder uiBinder = GWT
      .create(WalletEditItemWidgetUiBinder.class);


  interface WalletEditItemWidgetUiBinder extends
      UiBinder<Widget, WalletEditItemWidget> {
  }

  public WalletEditItemWidget() {
    initWidget(uiBinder.createAndBindUi(this));
  }

}
