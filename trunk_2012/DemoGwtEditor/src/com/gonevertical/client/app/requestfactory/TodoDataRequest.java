package com.gonevertical.client.app.requestfactory;

import java.util.List;

import com.gonevertical.client.app.requestfactory.dto.PeopleDataProxy;
import com.gonevertical.client.app.requestfactory.dto.TodoDataProxy;
import com.gonevertical.server.jdo.PeopleData;
import com.gonevertical.server.jdo.TodoData;
import com.google.web.bindery.requestfactory.shared.InstanceRequest;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;

@Service(TodoData.class)
public interface TodoDataRequest extends RequestContext {

  InstanceRequest<TodoDataProxy, Boolean> remove();
  
}
