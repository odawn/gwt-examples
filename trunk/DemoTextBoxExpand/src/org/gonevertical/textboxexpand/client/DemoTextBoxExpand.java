package org.gonevertical.textboxexpand.client;


import org.gonevertical.core.client.input.WiseTextArea;
import org.gonevertical.core.client.input.WiseTextBox;
import org.gonevertical.core.client.input.richtext.WiseRichTextArea;
import org.gonevertical.textboxexpand.client.old.V3.AutoTextAreaEdit;
import org.gonevertical.textboxexpand.client.old.V3.AutoTextBoxEdit;

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

  /**
   * These widgets are now in -> GoneVertical-Core project as an included module
   * see the project.gwt.xml module inheritance  
   * http://code.google.com/p/gwt-examples/source/browse/#svn%2Ftrunk%2FGoneVertical-Core%2Fsrc%2Forg%2Fgonevertical%2Fcore%2Fclient%2Finput
   */
  public void onModuleLoad() {
  
    
//    // auto growth, but always show the border
//    WiseTextBox tbEdit1 = new WiseTextBox();
//    tbEdit1.setFeatureGrow(true);
//    tbEdit1.setFeatureHideBorderUntilHover(false);
//    tbEdit1.setWidth("150px");
//    tbEdit1.setText("test 1 2 3");
//    //tbEdit1.setFeatureDebug(true);
//    
//    
//    
//    WiseTextBox tbEdit2 = new WiseTextBox();
//    tbEdit2.setFeatureGrow(true);
//    tbEdit2.setFeatureHideBorderUntilHover(true);
//    tbEdit2.setWidth("170px");
//    tbEdit2.setText("Hover over me (grows too)");
//    //tbEdit2.setFeatureDebug(true);
//    
//    
//    
//    
//    WiseTextArea taEdit3 = new WiseTextArea();
//    taEdit3.setFeatureGrow(true);
//    taEdit3.setFeatureHideBorderUntilHover(false);
//    //taEdit3.setSize("150px", "100px");
//    //taEdit3.setText("aaa bbb ccc ddd eee fff ggg hhh iii jjj kkk lll mmm nnn ooo ppp qqq rrr sss ttt uuu vvv www xxx yyy zzz");
//    //taEdit3.setFeatureDebug(true);
    
    
    
    // Rich Text Area 1
    WiseRichTextArea taEdit4 = new WiseRichTextArea();
    taEdit4.setFeatureGrow(true);
    taEdit4.setFeatureDebug(true);
    //taEdit4.setValue("0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 0 1 2 3 4 5 6 7 8 9 0");
    
    
    
//    // Rich Text Area 1    
//    WiseRichTextArea taEdit5 = new WiseRichTextArea();
//    taEdit5.setFeatureGrow(true);
//    taEdit5.setStyleName("test_webfont");
//    //taEdit5.setFeatureDebug(true);
    
    
    
    
    
    
    
    Grid grid = new Grid(5, 2);
    
//    //1. show a grid of the textbox examples
//    grid.setWidget(0, 0, new HTML("TextBox (Grows)"));
//    grid.setWidget(0, 1, tbEdit1);
//    
//    //2. textbox
//    grid.setWidget(1, 0, new HTML("TextBox (Hovers & Grows)"));
//    grid.setWidget(1, 1, tbEdit2);
//    
//    //3. textarea
//    grid.setWidget(2, 0, new HTML("TextArea (Grows)"));
//    grid.setWidget(2, 1, taEdit3);
    
    //4. richtextarea
    grid.setWidget(3, 0, new HTML("RichTextArea (Grows & Paste Intercepting & Double Click)</br>Only inserts plain text (firefox/mozilla may not work due to its security)"));
    grid.setWidget(3, 1, taEdit4);
    
    
//    //5. richtextarea
//    grid.setWidget(4, 0, new HTML("RichTextArea (Grows & Paste Intercepting & Double Click)</br>Only inserts plain text (firefox/mozilla may not work due to its security)"));
//    grid.setWidget(4, 1, taEdit5);
    
    
    // center layout
    VerticalPanel vp = new VerticalPanel();
    vp.setWidth("100%");
    vp.add(grid);
    vp.setCellHorizontalAlignment(grid, HorizontalPanel.ALIGN_CENTER);
    
    // add to root panel
    RootPanel.get().add(vp);
  }

}