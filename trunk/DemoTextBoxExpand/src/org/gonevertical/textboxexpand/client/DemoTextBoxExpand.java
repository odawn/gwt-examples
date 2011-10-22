package org.gonevertical.textboxexpand.client;


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DemoTextBoxExpand implements EntryPoint {

  
  public void onModuleLoad() {
  
    // auto growth, but always show the border
    boolean hideBorderUntilHover = false;
    boolean growWidth = true;
    AutoTextBoxEdit tbEdit1 = new AutoTextBoxEdit(hideBorderUntilHover, growWidth);
    tbEdit1.setWidth("150px");
    tbEdit1.setText("test 1 2 3");
    
    
    hideBorderUntilHover = true;
    growWidth = true;
    AutoTextBoxEdit tbEdit2 = new AutoTextBoxEdit(hideBorderUntilHover, growWidth);
    tbEdit1.setWidth("170px");
    tbEdit2.setText("Hover over me (grows too)");
    
    
    hideBorderUntilHover = false;
    growWidth = true;
    AutoTextAreaEdit taEdit3 = new AutoTextAreaEdit(hideBorderUntilHover, growWidth);
    //taEdit3.setSize("150px", "100px");
    //taEdit3.setText("Type in me");
    
    
    
    // show a grid of the textbox examples
    Grid grid = new Grid(3, 2);
    grid.setWidget(0, 0, new HTML("TextBox (Grows)"));
    grid.setWidget(0, 1, tbEdit1);
    
    
    grid.setWidget(1, 0, new HTML("TextBox (Hovers & Grows)"));
    grid.setWidget(1, 1, tbEdit2);
    
    grid.setWidget(2, 0, new HTML("TextArea (Grows)"));
    grid.setWidget(2, 1, taEdit3);
    
    
    // center layout
    VerticalPanel vp = new VerticalPanel();
    vp.setWidth("100%");
    vp.add(grid);
    vp.setCellHorizontalAlignment(grid, HorizontalPanel.ALIGN_CENTER);
    
    // add to root panel
    RootPanel.get().add(vp);
  }

}