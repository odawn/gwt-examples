package org.gonevertical.client.app.activity.place;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.Prefix;
import com.google.web.bindery.requestfactory.shared.RequestFactory;

public class HomePlace extends Place {
  
  /** 
   * I'm not really using the tokenizer here, but good for example
   */
  @Prefix("Entry")
  public static class Tokenizer implements PlaceTokenizer<HomePlace> {

    private RequestFactory requestFactory;

    
    public Tokenizer(RequestFactory requestFactory) {
      this.requestFactory = requestFactory;
    }
    
    @Override
    public String getToken(HomePlace place) {
      String token = place.getToken();
      return "";
    }

    @Override
    public HomePlace getPlace(String token) {
      return new HomePlace();
    }

  }
  
  

  private String token;

  public HomePlace() {
    //this.token = token;
  }

  public String getToken() {
    return token;
  }

 
}
