package org.gonevertical.client.requestfactory;

import com.google.web.bindery.requestfactory.shared.RequestFactory;

public interface ApplicationRequestFactory extends RequestFactory {

  UserDataRequest getUserDataRequest();
  
  // TODO add other requests here...
  
}
