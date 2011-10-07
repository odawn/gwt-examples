package org.gonevertical.client.nameinput;

import org.gonevertical.client.ClientPersistence;
import org.gonevertical.client.requestfactory.ApplicationRequestFactory;
import org.gonevertical.client.requestfactory.NameDataProxy;
import org.gonevertical.client.requestfactory.NameDataRequest;
import org.gonevertical.server.namedata.NameData;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

public class NameInput extends Composite {

  private ClientPersistence cp;

  private static NameInputUiBinder uiBinder = GWT.create(NameInputUiBinder.class);
  @UiField TextBox tbName;
  @UiField PushButton bAdd;

  interface NameInputUiBinder extends UiBinder<Widget, NameInput> {
  }

  public NameInput(ClientPersistence cp) {
    this.cp = cp;
    initWidget(uiBinder.createAndBindUi(this));
  }

  @UiHandler("bAdd")
  void onBAddClick(ClickEvent event) {
    add();
  }

  @UiHandler("tbName")
  void onTbNameKeyUp(KeyUpEvent event) {
    if (KeyCodes.KEY_ENTER == event.getNativeEvent().getKeyCode()) {
      add();
    }
  }

  private void add() {
    String name = tbName.getText().trim();
    
    if (name.trim().length() == 0) {
      Window.alert("Did you actually put a name in there? Add some characters please.");
      return;
    }

    save(name);
  }

  private void save(String name) {

    // get requestfactory piping
    NameDataRequest nameDataRequest = cp.getRequestFactory().nameDataRequest();

    // create new entity
    NameDataProxy newName = nameDataRequest.create(NameDataProxy.class);
    newName.setName(name);

    // persist the entity
    nameDataRequest.persist().using(newName).fire(new Receiver<Void>() {
      public void onSuccess(Void response) { // on success
        process();
      }
      public void onFailure(ServerFailure error) {
        super.onFailure(error);
        Window.alert("Oops, I couldn't save that name for some reason.");
      }
      
    });
  }

  private void process() {
    tbName.setText("");
    
    // tell parent it was saved
    
  }


}
