package com.gonevertical.client.app.activity.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class SignInPlace extends Place {

  private String token;

  public SignInPlace(String token) {
    this.token = token;
  }

  public String getToken() {
    return token;
  }

  public static class Tokenizer implements PlaceTokenizer<SignInPlace> {

    @Override
    public String getToken(SignInPlace place) {
      return place.getToken();
    }

    @Override
    public SignInPlace getPlace(String token) {
      return new SignInPlace(token);
    }

  }
}
