package org.gonevertical.textboxexpand.client;

import org.gonevertical.core.client.input.richtext.WiseRichTextArea;
import org.gonevertical.core.client.style.ComputedStyle;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.RootPanel;

public class TestComputedStyle extends GWTTestCase {

  private HTML html;

  public String getModuleName() {
    return "org.gonevertical.textboxexpand.DemoTextBoxExpand";
  }
  
  public void gwtSetUp() {
    html = new HTML("I am flying over the moon");
    RootPanel.get().add(html);
  }
  
  public void gwtTearDown() {
    html.removeFromParent();
    html = null;
  }
  
  public void testThisWorks() {
    String a = "work?";
    String b = "work?";
    assertEquals(a, b);
  }
  
  public void testWidth() {
    html.setWidth("126px");
    String expected = ComputedStyle.getStyleProperty(html.getElement(), "width");
    System.out.println("width1=" + expected + " = " + "126px");
    assertEquals(expected, "126px");
  }
  
  /**
   * works now, due to loading style sheet in module config
   */
  public void testWidthViaCss() {
    html.setStyleName("test_width");
    String expected = ComputedStyle.getStyleProperty(html.getElement(), "width");
    System.out.println("width=" + expected + " = " + "205px");
    assertEquals(expected, "205px");
  }
  
  /**
   * works now, due to loading style sheet in module config
   */
  public void testWebFont() {
    html.setStyleName("test_webfont");
    String expected = ComputedStyle.getStyleProperty(html.getElement(), "fontFamily");
    System.out.println("fontFamily=" + expected + " = " + " Rammetto One");
    assertEquals(expected, "Rammetto One");
  }

  
}
