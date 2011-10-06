package org.gonevertical.client;

import org.gonevertical.client.requestfactory.ApplicationRequestFactory;

import com.google.gwt.event.shared.EventBus;

public class ClientPersistence {

  private EventBus eventBus;
  
  private ApplicationRequestFactory requestFactory;

  public ClientPersistence(EventBus eventBus, ApplicationRequestFactory requestFactory) {
    this.eventBus = eventBus;
    this.requestFactory = requestFactory;
  }

  public EventBus getEventBus() {
    return eventBus;
  }
  
  public ApplicationRequestFactory getRequestFactory() {
    return requestFactory;
  }
}
