package org.gonevertical.client.app;

import org.gonevertical.client.app.activity.place.HomePlace;
import org.gonevertical.client.requestfactory.ApplicationRequestFactory;
import org.gonevertical.client.requestfactory.dto.UserDataProxy;

import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.PlaceController;


public interface ClientFactory {
  
	EventBus getEventBus();
	
	ApplicationRequestFactory getRequestFactory();
	
	PlaceController getPlaceController();

  void setUserData(UserDataProxy data);

  UserDataProxy getUserData();

  ActivityManager getActivityManager();
  
  Boolean getIsLoggedIn();
  
  
  /**
   * used by the historyMapper
   */
  HomePlace.Tokenizer getSignInTokenizer();

}
