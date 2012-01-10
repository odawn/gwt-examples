package org.gonevertical.textboxexpand.client.old;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TextAreaExpandWidget_V2 extends Composite {

  private HTML h;
  private TextArea taBox;
  
  private int prevHeight;

  public TextAreaExpandWidget_V2() {
    
    VerticalPanel vp = new VerticalPanel();
    RootPanel.get().add(vp);
    
    taBox = new TextArea();
    vp.add(taBox);
    
    taBox.setWidth("150px");
    taBox.setHeight("60px");
    taBox.setText("Type in this");
    
    taBox.addKeyUpHandler(new KeyUpHandler() {
      public void onKeyUp(KeyUpEvent event) {
        setSize();
      }
    });
    
    initWidget(taBox);
    
    h = new HTML("test", true);
    
    
    HorizontalPanel hp = new HorizontalPanel();
    hp.setWidth("150px");
    hp.add(h);
    
    AbsolutePanel ap = new AbsolutePanel();
    ap.add(hp, -500, -500);
    
    RootPanel.get().add(ap);
    
    //hp.addStyleName("test1");
  }

  private void setSize() {
    
    //debug();
    
    Element e = taBox.getElement();
    int scrollheight = e.getPropertyInt("scrollHeight");
    
    
    taBox.setHeight(scrollheight + "px");
    
    System.out.println("scrollheight=" + scrollheight);
    
    Element e2 = taBox.getElement();
    int scrollheight2 = e2.getPropertyInt("scrollHeight");
    
    System.out.println("scrollheight2=" + scrollheight2);
  }

  private void debug() {
    
     Element e = taBox.getElement();
     int sh = e.getPropertyInt("scrollHeight");
     
     String y = e.getStyle().getPosition();
    
    String s = "";
    s += "curpos=" + taBox.getCursorPos() + " sh=" + sh;
   
    System.out.println(s);
    
  }

}
