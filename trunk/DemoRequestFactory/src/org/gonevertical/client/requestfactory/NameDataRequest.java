package org.gonevertical.client.requestfactory;

import java.util.List;

import org.gonevertical.server.domain.NameData;

import com.google.web.bindery.requestfactory.shared.InstanceRequest;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;

@Service(NameData.class)
public interface NameDataRequest extends RequestContext {

  Request<Long> count();
  
  Request<NameDataProxy> findNameData(Long id);
  
  Request<List<NameDataProxy>> query();
  
  Request<List<NameDataProxy>> query(int firstResult, int maxResult);
  
  InstanceRequest<NameDataProxy, Void> persist();

  InstanceRequest<NameDataProxy, Void> remove();

}
