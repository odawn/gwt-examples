package com.gonevertical.client.app.activity.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class WalletListPlace extends Place {

  private String token;

  public WalletListPlace(String token) {
    this.token = token;
  }

  public String getToken() {
    return token;
  }

  public static class Tokenizer implements PlaceTokenizer<WalletListPlace> {

    @Override
    public String getToken(WalletListPlace place) {
      return place.getToken();
    }

    @Override
    public WalletListPlace getPlace(String token) {
      return new WalletListPlace(token);
    }

  }

}
