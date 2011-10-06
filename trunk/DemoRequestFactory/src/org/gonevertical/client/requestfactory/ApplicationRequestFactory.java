package org.gonevertical.client.requestfactory;

import com.google.web.bindery.requestfactory.shared.RequestFactory;

public interface ApplicationRequestFactory extends RequestFactory {

  NameDataRequest nameDataRequest();
  
  // TODO other application entity type requests go here ...
  
}
