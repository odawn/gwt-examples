package org.gonevertical.client.requestfactory;

import org.gonevertical.server.domain.NameData;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyFor;

@ProxyFor(NameData.class)
public interface NameDataProxy extends EntityProxy {
  
  // instead of getId, use stableId()
  Long getId();
  
  Integer getVersion();
  
  void setName(String name);
  
  String getName();
  
  
}
