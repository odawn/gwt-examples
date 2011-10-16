package com.gonevertical.client.app.requestfactory.dto;

import com.gonevertical.server.domain.WalletItemData;
import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyFor;

@ProxyFor(WalletItemData.class)
public interface WalletItemDataProxy extends EntityProxy {

  Long getId();
  
  Long getUserId();
  
  void setName(String name);
  
  String getName();
  
  void setContact(String contact);
  
  String getContact();
  
}
