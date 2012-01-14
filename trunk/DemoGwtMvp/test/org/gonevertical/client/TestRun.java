package org.gonevertical.client;

import org.gonevertical.client.app.ClientFactory;
import org.gonevertical.client.app.ClientFactoryImpl;
import org.gonevertical.client.views.HomeView;
import org.gonevertical.client.views.HomeView.Presenter;
import org.gonevertical.client.views.impl.HomeViewImpl;

import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;

public class TestRun extends GWTTestCase {

  public static final int ASYNC_DELAY_MS = 5000;
  
  @Override
  public String getModuleName() {
    return "org.gonevertical.DemoGwtMvp";
  }

  public void testWorks() {
    assertTrue(true);
  }
  
  public void testSomething() {
    
    ClientFactory clientFactory = new ClientFactoryImpl();
    
    // TODO more later
    
  }
  
}
