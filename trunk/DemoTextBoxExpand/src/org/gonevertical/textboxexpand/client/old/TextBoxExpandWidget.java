package org.gonevertical.textboxexpand.client.old;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TextBoxExpandWidget extends Composite {

  private HTML h;
  private TextBox tb;

  public TextBoxExpandWidget() {
    
    VerticalPanel vp = new VerticalPanel();
    RootPanel.get().add(vp);
    
    tb = new TextBox();
    tb.setText("Type in this");
    vp.add(tb);
    
    tb.addKeyUpHandler(new KeyUpHandler() {
      public void onKeyUp(KeyUpEvent event) {
        setSize();
      }
    });
    
    initWidget(tb);
    
    h = new HTML();
    AbsolutePanel ap = new AbsolutePanel();
    RootPanel.get().add(ap);
    ap.add(h, -500, -500);
  }

  private void setSize() {
    String s = tb.getText();
    s = s.replaceAll("\040", "&nbsp;");
    h.setHTML(s);
    
    System.out.println("s: " + s);
    
    int width = h.getOffsetWidth();
    if (width > 50) {
      tb.setWidth(width + "px");
    }
    System.out.println("width=" + width);
  }

}
