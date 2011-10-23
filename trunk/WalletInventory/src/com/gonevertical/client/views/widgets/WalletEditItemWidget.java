package com.gonevertical.client.views.widgets;

import org.gonevertical.core.client.input.WiseTextBox;

import com.gonevertical.client.app.ClientFactory;
import com.gonevertical.client.app.requestfactory.ApplicationRequestFactory;
import com.gonevertical.client.app.requestfactory.WalletDataRequest;
import com.gonevertical.client.app.requestfactory.dto.WalletItemDataProxy;
import com.gonevertical.client.global.booleandialog.BooleanDialog;
import com.gonevertical.client.global.booleandialog.BooleanEvent;
import com.gonevertical.client.global.booleandialog.BooleanEvent.Selected;
import com.gonevertical.client.global.booleandialog.BooleanEventHandler;
import com.gonevertical.client.global.loadingwidget.LoadingWidget;
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
import com.google.gwt.user.client.ui.FocusPanel;

public class WalletEditItemWidget extends Composite {
  
  public static enum State {
    VIEW, EDIT;
  }
  private State stateIs;
  
  private Presenter presenter;

  private ClientFactory clientFactory;

  private static WalletEditItemWidgetUiBinder uiBinder = GWT.create(WalletEditItemWidgetUiBinder.class);
  @UiField WiseTextBox tbName;
  @UiField PushButton bDelete;
  @UiField FocusPanel focusPanel;

  interface WalletEditItemWidgetUiBinder extends UiBinder<Widget, WalletEditItemWidget> {
  }

  private WalletItemDataProxy itemData;

  private BooleanDialog wconfirm;

  private int index;

  private LoadingWidget wLoading;

  public WalletEditItemWidget() {
    initWidget(uiBinder.createAndBindUi(this));
  }

  public void setPresenter(Presenter presenter) {
    this.presenter = presenter;
  }

  public void setClientFactory(ClientFactory clientFactory) {
    this.clientFactory = clientFactory;
  }
  
  public void setLoading(LoadingWidget wLoading) {
    this.wLoading = wLoading;
  }

  public void setData(int i, WalletItemDataProxy itemData) {
    this.index = i;
    // TODO set style depending on i
    this.itemData = itemData;
  }

  public WalletItemDataProxy getData(WalletDataRequest request) {
    if (itemData == null) {
      itemData = request.create(WalletItemDataProxy.class);
    } else {
      itemData = request.edit(itemData);
    }
    
    itemData.setName(getName());
    
    return itemData;
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
      String s = index + " Frequent Card 1-800-123-xxxx ";
      tbName.setText(s);
      return;
    }
    
    String s = itemData.getName();
    SafeHtml sh = SimpleHtmlSanitizer.sanitizeHtml(s);
    tbName.setText(sh.asString());
  }
  
  private void setName() {
    String s = tbName.getText().trim();
    if (s != null && s.length() == 0) {
      s = "My Wallet";
    }
    if (itemData != null && s.equals(itemData.getName()) == false) {
      fireChange();
    }
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
    wLoading.showLoading(true);
    Request<Boolean> req = clientFactory.getRequestFactory().getWalletItemDataRequest().deleteWalletItemData(itemData.getId());
    req.fire(new Receiver<Boolean>() {
      public void onSuccess(Boolean data) {
        wLoading.showLoading(false);
        if (data != null && data.booleanValue() == true) {
          removeFromParent();
          fireChange();
        } else {
          wLoading.showError("Oops, I couldn't delete that.");
        }
      }
      public void onFailure(ServerFailure error) {
        wLoading.showError();
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
    tbName.setEdit(false);
  }

  private void setStateEdit() {
    tbName.setEdit(true);
  }

  private void fireChange() {
    NativeEvent nativeEvent = Document.get().createChangeEvent();
    ChangeEvent.fireNativeEvent(nativeEvent, this);
  }

  public HandlerRegistration addChangeHandler(ChangeHandler handler) {
    return addDomHandler(handler, ChangeEvent.getType());
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
  void onTbNameChange(ChangeEvent event) {
    drawName();
    fireChange();
  }

  @UiHandler("bDelete")
  public void onBDeleteClick(ClickEvent event) {
    delete();
  }
  
  @UiHandler("focusPanel")
  void onFocusPanelMouseOver(MouseOverEvent event) {
    setState(State.EDIT);
  }
  
  @UiHandler("focusPanel")
  void onFocusPanelMouseOut(MouseOutEvent event) {
    setName();
    setState(State.VIEW);
  }


}
