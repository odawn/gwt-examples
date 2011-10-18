package com.gonevertical.client.views.widgets;

import com.gonevertical.client.app.ApplicationFactory;
import com.gonevertical.client.app.requestfactory.dto.WalletItemDataProxy;
import com.gonevertical.client.global.booleandialog.BooleanDialog;
import com.gonevertical.client.global.booleandialog.BooleanEvent;
import com.gonevertical.client.global.booleandialog.BooleanEvent.Selected;
import com.gonevertical.client.global.booleandialog.BooleanEventHandler;
import com.gonevertical.client.views.WalletEditView.Presenter;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.TouchEndEvent;
import com.google.gwt.event.dom.client.TouchStartEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.requestfactory.shared.Receiver;
import com.google.web.bindery.requestfactory.shared.Request;
import com.google.web.bindery.requestfactory.shared.ServerFailure;

public class WalletEditItemWidget extends Composite {
  
  public static enum State {
    VIEW, EDIT;
  }
  private State stateIs;
  
  private Presenter presenter;

  private ApplicationFactory appFactory;

  private static WalletEditItemWidgetUiBinder uiBinder = GWT.create(WalletEditItemWidgetUiBinder.class);
  @UiField HTML htmlName;
  @UiField TextBox tbName;
  @UiField PushButton bDelete;

  interface WalletEditItemWidgetUiBinder extends UiBinder<Widget, WalletEditItemWidget> {
  }

  private WalletItemDataProxy itemData;

  private BooleanDialog wconfirm;

  private int index;

  public WalletEditItemWidget() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  public void setPresenter(Presenter presenter) {
    this.presenter = presenter;
  }

  public void setAppFactory(ApplicationFactory appFactory) {
    this.appFactory = appFactory;
  }

  public void setData(int i, WalletItemDataProxy itemData) {
    this.index = i;
    // TODO set style depending on i
    this.itemData = itemData;
  }

  public WalletItemDataProxy getData() {
    if (itemData == null) {
      itemData = appFactory.getRequestFactory().getWalletItemDataRequest().create(WalletItemDataProxy.class);
    }
    return itemData;
  }
  
  public void setNameData() {
    if (itemData == null) {
      itemData = appFactory.getRequestFactory().getWalletItemDataRequest().create(WalletItemDataProxy.class);
    }    
    itemData.setName(getName());
  }

  public void draw() {

    // default
    setState(State.VIEW);

    drawName();
  }

  private void drawName() {
    if (itemData == null || 
        itemData.getName() == null || 
        itemData.getName().trim().length() == 0) {
      String s = index + " Frequent Card 1-800-781-xxxx ";
      tbName.setText(s);
      htmlName.setHTML(s);
      return;
    }
    
    String s = itemData.getName();
    SafeHtml sh = SimpleHtmlSanitizer.sanitizeHtml(s);
    tbName.setText(sh.toString());
    htmlName.setHTML(sh);
  }

  private String getName() {
    String s = tbName.getText().trim();
    if (s.length() == 0) {
      s = null;
    }
    return s;
  }

  private void delete() {
    if (wconfirm == null) {
      wconfirm = new BooleanDialog("Are you sure you want to delete this?");
      wconfirm.addSelectionHandler(new BooleanEventHandler() {
        public void onBooleanEvent(BooleanEvent event) {
          if (event.getBooleanEvent() == Selected.TRUE) {
            deleteIt();
          } else if (event.getBooleanEvent() == Selected.FALSE) {
            // do nothing
          }
        }
      });
    }
    wconfirm.center();
  }

  private void deleteIt() {
    if (itemData == null) {
      removeFromParent();
      fireChange();
      return;
    }
    Request<Void> req = appFactory.getRequestFactory().getWalletItemDataRequest().remove().using(itemData);
    req.fire(new Receiver<Void>() {
      public void onSuccess(Void response) {
        removeFromParent();
        fireChange();
      }
      public void onFailure(ServerFailure error) {
        super.onFailure(error);
      }
    });
  }

  private void setState(State state) {
    stateIs = state;
    if (state == State.VIEW) {
      setStateView();
    } else if (state == State.EDIT) {
      setStateEdit();
    }
  }

  private void setStateView() {
    drawName();
    htmlName.setVisible(true);
    tbName.setVisible(false);
  }

  private void setStateEdit() {
    htmlName.setVisible(false);
    tbName.setVisible(true);
  }

  @UiHandler("tbName")
  public void onTbNameTouchStart(TouchStartEvent event) {
    if (stateIs == State.VIEW) {
      setState(State.EDIT);
    } else if (stateIs == State.EDIT) {
      setState(State.VIEW);
    }
  }

  @UiHandler("tbName")
  public void onTbNameTouchEnd(TouchEndEvent event) {
  }

  @UiHandler("tbName")
  public void onTbNameMouseOver(MouseOverEvent event) {
    setState(State.EDIT);
  }

  @UiHandler("tbName")
  public void onTbNameMouseOut(MouseOutEvent event) {
    setState(State.VIEW);
  }

  @UiHandler("tbName")
  void onTbNameChange(ChangeEvent event) {
    setNameData();
    drawName();
    fireChange();
  }

  @UiHandler("bDelete")
  public void onBDeleteClick(ClickEvent event) {
    delete();
  }
  
  private void fireChange() {
    NativeEvent nativeEvent = Document.get().createChangeEvent();
    ChangeEvent.fireNativeEvent(nativeEvent, this);
  }

  public HandlerRegistration addChangeHandler(ChangeHandler handler) {
    return addDomHandler(handler, ChangeEvent.getType());
  }
  
 
}
