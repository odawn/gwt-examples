package org.gonevertical.client.namelist;

import org.gonevertical.client.ClientPersistence;
import org.gonevertical.client.requestfactory.NameDataProxy;
import org.gonevertical.client.requestfactory.NameDataRequest;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.web.bindery.requestfactory.shared.InstanceRequest;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;

public class NameItem extends Composite {

  private ClientPersistence cp;

  private HandlerManager handlerManager;

  private static NameItemUiBinder uiBinder = GWT.create(NameItemUiBinder.class);
  @UiField HTML htmlName;
  @UiField PushButton bDelete;

  private NameDataProxy data;


  interface NameItemUiBinder extends UiBinder<Widget, NameItem> {
  }

  public NameItem(ClientPersistence cp) {
    this.cp = cp;
    initWidget(uiBinder.createAndBindUi(this));

    handlerManager = new HandlerManager(this);
  }

  public void setData(NameDataProxy data) {
    this.data = data;
  }

  public void draw() {

    drawName();

  }

  private void drawName() {
    String name = data.getName();

    String s = "None";
    if (name != null) {
      s = name;
    }

    /**
     * always sanitize!!!
     */
    htmlName.setHTML(SimpleHtmlSanitizer.sanitizeHtml(s));
  }


  @UiHandler("bDelete")
  void onBDeleteClick(ClickEvent event) {

    delete();

  }

  private void delete() {

    if (data == null || data.stableId() == null) {
      removeFromParent();
      fireDelete();
      return;
    }

    // get requestfactory piping
    NameDataRequest nameDataRequest = cp.getRequestFactory().nameDataRequest();

    nameDataRequest.remove().using(data).fire(new Receiver<Void>() {
      public void onSuccess(Void response) {
        processDelete();
      }
    });

  }

  private void processDelete() {
    removeFromParent();
    fireDelete();
  }

  private void fireDelete() {
    fireEvent(new DeleteNameEvent());
  }

  public void fireEvent(GwtEvent<?> event) {
    handlerManager.fireEvent(event);
  }

  public HandlerRegistration addDeleteNameHandler(DeleteNameHandler handler) {
    return handlerManager.addHandler(DeleteNameEvent.TYPE, handler);
  }
}
