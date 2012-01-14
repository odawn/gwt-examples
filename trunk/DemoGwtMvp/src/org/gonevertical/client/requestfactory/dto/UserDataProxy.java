package org.gonevertical.client.requestfactory.dto;

import org.gonevertical.server.jdo.UserData;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyFor;

@ProxyFor(UserData.class)
public interface UserDataProxy extends EntityProxy {

  String getLoginUrl();
  
  String getLogoutUrl();
  
  String getId();

  String getGoogleNickname();
  
}
