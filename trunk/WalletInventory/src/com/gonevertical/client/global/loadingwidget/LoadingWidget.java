package com.gonevertical.client.global.loadingwidget;

import com.gonevertical.client.global.UiImages;
import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.shared.SimpleHtmlSanitizer;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class LoadingWidget extends Composite {
  
  UiImages uiImages = GWT.create(UiImages.class);

  private static LoadingWidgetUiBinder uiBinder = GWT.create(LoadingWidgetUiBinder.class);
  @UiField Image imgLoading;
  @UiField HorizontalPanel pWidget;
  @UiField HTML htmlMessage;
  @UiField HTML htmlSpacer;

  interface LoadingWidgetUiBinder extends UiBinder<Widget, LoadingWidget> {
  }

  public LoadingWidget() {
    initWidget(uiBinder.createAndBindUi(this));
    
    imgLoading.setResource(uiImages.loading());
    imgLoading.setSize("20px", "20px");

    // set default state
    showLoading(false);
  }
  
  public void showLoading(boolean show) {
    setVisible(show);
    setMessage(null);
  }
  
  public void showLoading(boolean show, String message) {
    showLoading(show);
    setMessage(message);
  }
  
  public void hideTimed(int delayMillis, String message) {
    showLoading(true, message);
    hideTimed(delayMillis);
  }
  
  public void hideTimed(int delayMillis) {
    if (delayMillis == 0) {
      delayMillis = 4000; // 4 sec
    }
    Timer t = new Timer() {
      public void run() {
        showLoading(false);
      }
    };
    t.schedule(delayMillis);
  }
  
  private void setMessage(String s) {
    if (s == null || s.trim().length() == 0) {
      htmlMessage.setHTML("");
      htmlSpacer.setVisible(false);
      htmlMessage.setVisible(false);
      return;
    }
    htmlMessage.setHTML(SimpleHtmlSanitizer.sanitizeHtml(s));
    htmlSpacer.setVisible(true);
    htmlMessage.setVisible(true);
  }
  
  public void hide() {
    showLoading(false);
  }

}
