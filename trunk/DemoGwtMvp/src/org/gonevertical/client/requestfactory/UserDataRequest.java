package org.gonevertical.client.requestfactory;

import org.gonevertical.client.requestfactory.dto.UserDataProxy;
import org.gonevertical.server.jdo.UserData;

import com.google.web.bindery.requestfactory.shared.InstanceRequest;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;

@Service(UserData.class)
public interface UserDataRequest extends RequestContext {

  Request<UserDataProxy> findUserData(String id);
  
  Request<UserDataProxy> createUserData();
  
  InstanceRequest<UserDataProxy, Void> remove();
}
