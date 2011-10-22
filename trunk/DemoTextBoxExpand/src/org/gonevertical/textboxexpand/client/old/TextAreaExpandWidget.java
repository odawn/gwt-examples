package org.gonevertical.textboxexpand.client.old;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TextAreaExpandWidget extends Composite {

  private HTML h;
  private TextArea ta;

  public TextAreaExpandWidget() {
    
    VerticalPanel vp = new VerticalPanel();
    RootPanel.get().add(vp);
    
    ta = new TextArea();
    ta.setWidth("150px");
    ta.setHeight("60px");
    ta.setText("Type in this");
    
    vp.add(ta);
    
    ta.addKeyUpHandler(new KeyUpHandler() {
      public void onKeyUp(KeyUpEvent event) {
        setSize();
      }
    });
    
    initWidget(ta);
    
    h = new HTML("test", true);
    
    
    HorizontalPanel hp = new HorizontalPanel();
    hp.setWidth("150px");
    hp.add(h);
    
    AbsolutePanel ap = new AbsolutePanel();
    ap.add(hp, -500, -500);
    
    RootPanel.get().add(ap);
    
    hp.addStyleName("test1");
  }

  private void setSize() {
    String s = ta.getText();
    //s = s.replaceAll("\040", "&nbsp;");
    h.setHTML(s);
    
    
    System.out.println("s: " + s);
    
    int width = h.getOffsetWidth();
    int offset = 0;
    if (width > 150) { // compensate for long continuous words
      int compensate = width - 150;
      offset = compensate / 5;
    }
    
    int height = h.getOffsetHeight();
    height = height + offset;
    if (height > 60) {
      ta.setHeight(height + "px");
    }
    System.out.println("height=" + height + " offset" + offset);
  }

}
