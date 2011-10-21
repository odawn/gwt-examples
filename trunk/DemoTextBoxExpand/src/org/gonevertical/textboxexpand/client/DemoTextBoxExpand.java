package org.gonevertical.textboxexpand.client;

import org.gonevertical.textboxexpand.client.V3.AutoTextBoxEdit;

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
    AutoTextBoxEdit tbEdit = new AutoTextBoxEdit(hideBorderUntilHover, growWidth);
    
    
    // show a grid of the textbox examples
    Grid grid = new Grid(3, 2);
    grid.setWidget(0, 0, new HTML("Try the TextBox"));
    grid.setWidget(0, 1, tbEdit);
    
    
    // center layout
    VerticalPanel vp = new VerticalPanel();
    vp.setWidth("100%");
    vp.add(grid);
    vp.setCellHorizontalAlignment(grid, HorizontalPanel.ALIGN_CENTER);
    
    // add to root panel
    RootPanel.get().add(vp);
  }

}