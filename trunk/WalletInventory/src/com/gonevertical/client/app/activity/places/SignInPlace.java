package com.gonevertical.client.app.activity.places;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.google.web.bindery.requestfactory.shared.RequestFactory;

public class SignInPlace extends Place {
  
  @Prefix("Entry")
  public static class Tokenizer implements PlaceTokenizer<SignInPlace> {

    private RequestFactory requestFactory;

    public Tokenizer(RequestFactory requestFactory) {
      this.requestFactory = requestFactory;
    }
    
    @Override
    public String getToken(SignInPlace place) {
      return place.getToken();
    }

    @Override
    public SignInPlace getPlace(String token) {
      return new SignInPlace(token);
    }

  }
  
  

  private String token;

  public SignInPlace(String token) {
    this.token = token;
  }

  public String getToken() {
    return token;
  }

 
}
