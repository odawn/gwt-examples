package com.gonevertical.client.app.requestfactory;

import java.util.List;

import com.gonevertical.client.app.requestfactory.dto.PeopleDataProxy;
import com.gonevertical.server.data.PeopleData;
import com.google.web.bindery.requestfactory.shared.InstanceRequest;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;

@Service(PeopleData.class)
public interface PeopleDataRequest extends RequestContext {

  Request<PeopleDataProxy> findPeopleData(String id);
  
  Request<List<PeopleDataProxy>> findPeopleData(long start, long end);
  
  Request<Long> findCount();
  
  InstanceRequest<PeopleDataProxy, PeopleDataProxy> persist();
  
  InstanceRequest<PeopleDataProxy, Boolean> remove();
  
}
