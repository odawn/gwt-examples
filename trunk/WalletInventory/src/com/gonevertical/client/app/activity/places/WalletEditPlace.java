package com.gonevertical.client.app.activity.places;

import com.gonevertical.client.app.requestfactory.dto.WalletDataProxy;
import com.gonevertical.server.domain.WalletData;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;

public class WalletEditPlace extends Place{

  private WalletDataProxy walletData;
  
  private String token;

  public WalletEditPlace(WalletDataProxy walletData) {
    this.walletData = walletData;
  }
  
  public WalletEditPlace(String token) {
    this.token = token;
    // TODO load walletData;
  }

  public String getToken() {
    return token;
  }

  @Prefix("e")
  public static class Tokenizer implements PlaceTokenizer<WalletEditPlace> {

    @Override
    public String getToken(WalletEditPlace place) {
      return place.getToken();
    }

    @Override
    public WalletEditPlace getPlace(String token) {
      return new WalletEditPlace(token);
    }

  }
  
  public void setWalletData(WalletDataProxy walletData) {
    this.walletData = walletData;
  }
  public WalletDataProxy getWalletData() {
    return walletData;
  }
  
}
