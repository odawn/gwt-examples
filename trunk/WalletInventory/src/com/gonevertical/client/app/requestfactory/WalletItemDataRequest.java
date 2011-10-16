package com.gonevertical.client.app.requestfactory;

import com.gonevertical.client.app.requestfactory.dto.WalletItemDataProxy;
import com.gonevertical.server.domain.WalletItemData;
import com.google.web.bindery.requestfactory.shared.InstanceRequest;
import com.google.web.bindery.requestfactory.shared.RequestContext;
import com.google.web.bindery.requestfactory.shared.Service;

@Service(WalletItemData.class)
public interface WalletItemDataRequest extends RequestContext {

  InstanceRequest<WalletItemDataProxy, Void> remove();
  
}
