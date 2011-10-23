package com.gonevertical.client.app.activity.places;

import com.gonevertical.client.app.requestfactory.dto.WalletDataProxy;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.google.web.bindery.requestfactory.shared.RequestFactory;

public class WalletEditPlace extends Place {
  
  @Prefix("EditWallet") // historyToken anchor
  public static class Tokenizer implements PlaceTokenizer<WalletEditPlace> {

    private RequestFactory requestFactory;

    public Tokenizer(RequestFactory requestFactory) {
      this.requestFactory = requestFactory;
    }
    
    /**
     * from - app activated
     */
    @Override
    public String getToken(WalletEditPlace place) {
      
      System.out.println("WalletEditPlace.getPlace(place)");
      
     
      String s = "";
      if (place.getWalletData() == null) {
        s = "new";
      } else {
        s = requestFactory.getHistoryToken(place.getWalletData().stableId());
      }
      
      return place.getToken();
    }

    /**
     * from - url activated
     */
    @Override
    public WalletEditPlace getPlace(String token) {
      
      
      System.out.println("WalletEditPlace.getPlace(token) token=" + token);
      
      
      return new WalletEditPlace(token);
    }

  }
  
  

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

  
  public void setWalletData(WalletDataProxy walletData) {
    this.walletData = walletData;
  }
  public WalletDataProxy getWalletData() {
    return walletData;
  }
  
}
