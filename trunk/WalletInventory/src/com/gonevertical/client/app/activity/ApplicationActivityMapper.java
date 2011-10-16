package com.gonevertical.client.app.activity;

import com.gonevertical.client.app.ApplicationFactory;
import com.gonevertical.client.app.activity.places.SignInPlace;
import com.gonevertical.client.app.activity.places.WalletEditPlace;
import com.gonevertical.client.app.activity.places.WalletListPlace;
import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

public class ApplicationActivityMapper implements ActivityMapper {
  
  private ApplicationFactory appFactory;

  /**
   * AppActivityMapper associates each Place with its corresponding
   * {@link Activity}
   * 
   * @param appFactory Factory to be passed to activities
   * @param walleteditview 
   * @param walletlistview 
   * @param signinview 
   */
  public ApplicationActivityMapper(ApplicationFactory appFactory) {
    super();
    this.appFactory = appFactory;
  }
  
  /**
   * Map each Place to its corresponding Activity. 
   */
  @Override
  public Activity getActivity(Place place) {

    if (place instanceof SignInPlace) {
      return new SignInActivity((SignInPlace) place, appFactory);
      
    } else if (place instanceof WalletListPlace) {
      return new WalletListActivity((WalletListPlace) place, appFactory);
      
    } else if (place instanceof WalletEditPlace) {
      return new WalletEditActivity((WalletEditPlace) place, appFactory);
      
    } else {
      return null;
    }
  }

}
