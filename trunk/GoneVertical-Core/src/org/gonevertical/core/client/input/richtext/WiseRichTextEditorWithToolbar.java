package org.gonevertical.core.client.input.richtext;

import org.gonevertical.core.client.html.HtmlSanitizerUtils;
import org.gonevertical.core.client.input.richtext.toolbar.RichTextToolbar;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.RichTextArea;

public class WiseRichTextEditorWithToolbar extends Composite {

  private WiseRichTextArea rta;

  public WiseRichTextEditorWithToolbar() {
    rta = new WiseRichTextArea();
    
    rta.setSize("100%", "14em");
    RichTextToolbar rtt = new RichTextToolbar(rta);
    rtt.setWidth("100%");
    
    // Add the components to a panel
    Grid grid = new Grid(3, 1);
    grid.setWidget(0, 0, rtt);
    grid.setWidget(1, 0, rta);
    
    initWidget(grid);
  }
  
  public void setHTML(String html) {
    SafeHtml s = HtmlSanitizerUtils.sanitizeHtml(html);
    rta.setHTML(s);
  }
  
  public void setHTML(SafeHtml html) {
    rta.setHTML(html);
  }
  
  public String getHtml() {
    return rta.getHTML();
  }
  
  
}
