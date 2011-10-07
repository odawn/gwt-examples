package org.gonevertical.client.namelist;

import java.util.Iterator;
import java.util.List;

import org.gonevertical.client.ClientPersistence;
import org.gonevertical.client.nameinput.NameInput;
import org.gonevertical.client.requestfactory.NameDataProxy;
import org.gonevertical.client.requestfactory.NameDataRequest;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.AttachEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class NameList extends Composite {
  
  private ClientPersistence cp;
  
  private static NameListUiBinder uiBinder = GWT.create(NameListUiBinder.class);
  @UiField VerticalPanel pAdd;
  @UiField VerticalPanel pList;
  @UiField HorizontalPanel pLoading;
  @UiField HTML pExist;
  
  interface NameListUiBinder extends UiBinder<Widget, NameList> {
  }

  public NameList(ClientPersistence cp) {
    this.cp = cp;
    initWidget(uiBinder.createAndBindUi(this));
    
    pExist.setVisible(false);
  }

  public void draw() {
    drawAddInput();
    
    loadNames();
  }
  
  private void drawAddInput() {
    pAdd.clear();
    NameInput ni = new NameInput(cp);
    pAdd.add(ni);
    ni.addAddNameHandler(new AddNameHandler() {
      public void onEvent(AddNameEvent event) {
        NameDataProxy newNameData = event.getNameData();
        drawName(newNameData);
      }
    });
  }
  
  private void loadNames() {
    
    // get requestfactory piping
    NameDataRequest nameDataRequest = cp.getRequestFactory().nameDataRequest();
    
    showLoading(true);
    
    nameDataRequest.query().fire(new Receiver<List<NameDataProxy>>() {
      public void onSuccess(List<NameDataProxy> data) {
        showLoading(false);
        process(data);
      }
      public void onFailure(ServerFailure error) {
        showLoading(false);
        super.onFailure(error);
      }
    });
  }

  private void process(List<NameDataProxy> data) {
    pList.clear();
    if (data == null || data.size() == 0) {
      return;
    } 
    
    Iterator<NameDataProxy> itr = data.iterator();
    while(itr.hasNext()) {
      NameDataProxy d = itr.next();
      drawName(d);
    }
  }

  private void drawName(NameDataProxy data) {
    if (data == null) {
      return;
    }
  
    NameItem ni = new NameItem(cp);
    pList.add(ni);
    ni.setData(data);
    ni.draw();
    ni.addDeleteNameHandler(new DeleteNameHandler() {
      public void onEvent(DeleteNameEvent event) {
        showExist();
      }
    });
    
    showExist();
  }
  
  

  private void showLoading(boolean b) {
    pLoading.setVisible(b);
  }
  
  private void showExist() {
    if (pList.getWidgetCount() > 0) {
      pExist.setVisible(false);
    } else {
      pExist.setVisible(true);
    }
  }

}
