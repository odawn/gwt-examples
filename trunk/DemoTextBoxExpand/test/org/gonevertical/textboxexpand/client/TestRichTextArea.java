package org.gonevertical.textboxexpand.client;

import org.gonevertical.core.client.input.richtext.WiseRichTextArea;
import org.gonevertical.core.client.style.ComputedStyle;

import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.RootPanel;

public class TestRichTextArea extends GWTTestCase {


  private WiseRichTextArea rta;

  public String getModuleName() {
    return "org.gonevertical.textboxexpand.DemoTextBoxExpand";
  }
  
  public void gwtSetUp() {
    rta = new WiseRichTextArea();
    RootPanel.get().add(rta);
  }
  
  public void gwtTearDown() {
    rta = null;
  }
  
  public void testThisWorks() {
    String a = "work?";
    String b = "work?";
    assertEquals(a, b);
  }
  
  
  
  public void testWidthOnInit() {
    rta.setText("Test");
    int left = rta.getOffsetWidth();
    int right = rta.getHtmlSizingPanel().getOffsetWidth();
    System.out.println("width: left=" + left + " right=" + right);
    assertEquals(left, right);
  }
  
  public void testWidthSetting() {
    rta.setWidth("200px");
    rta.setText("Test");
    int left = rta.getOffsetWidth();
    int right = rta.getHtmlSizingPanel().getOffsetWidth();
    System.out.println("width: left=" + left + " right=" + right);
    assertEquals(left, right);
  }
  
  public void testHeightOnInit() {
    rta.setText("Test");
    int left = rta.getOffsetHeight();
    int right = rta.getHtmlSizingPanel().getOffsetHeight();
    System.out.println("height: left=" + left + " right=" + right);
    assertEquals(left, right);
  }
  
  
  
  public void testCloneColor() {
    rta.setText("Test");
    String property = "color";
    String left = ComputedStyle.getStyleProperty(rta.getElement(), property);
    String right = ComputedStyle.getStyleProperty(rta.getHtmlSizingPanel().getElement(), property);
    System.out.println("property=" + property + " left=" + left + " right=" + right);
    assertEquals(left, right);
  }
  
  public void testCloneDirection() {
    rta.setText("Test");
    String property = "direction";
    String left = ComputedStyle.getStyleProperty(rta.getElement(), property);
    String right = ComputedStyle.getStyleProperty(rta.getHtmlSizingPanel().getElement(), property);
    System.out.println("property=" + property + " left=" + left + " right=" + right);
    assertEquals(left, right);
  }
  
  public void testCloneFont() {
    rta.setText("Test");
    String property = "font";
    String left = ComputedStyle.getStyleProperty(rta.getElement(), property);
    String right = ComputedStyle.getStyleProperty(rta.getHtmlSizingPanel().getElement(), property);
    System.out.println("property=" + property + " left=" + left + " right=" + right);
    assertEquals(left, right);
  }
 
  public void testCloneFontFace() {
    rta.setText("Test");
    String property = "fontFace";
    String left = ComputedStyle.getStyleProperty(rta.getElement(), property);
    String right = ComputedStyle.getStyleProperty(rta.getHtmlSizingPanel().getElement(), property);
    System.out.println("property=" + property + " left=" + left + " right=" + right);
    assertEquals(left, right);
  }
  
  public void testCloneFontFamily() {
    rta.setText("Test");
    String property = "fontFamily";
    String left = ComputedStyle.getStyleProperty(rta.getElement(), property);
    String right = ComputedStyle.getStyleProperty(rta.getHtmlSizingPanel().getElement(), property);
    System.out.println("property=" + property + " left=" + left + " right=" + right);
    assertEquals(left, right);
  }
   
  public void testCloneFontFamily_WebFont() {
    rta.setStyleName("test_webfont");
    rta.setText("Test");
    String property = "fontFamily";
    String left = ComputedStyle.getStyleProperty(rta.getElement(), property);
    String right = ComputedStyle.getStyleProperty(rta.getHtmlSizingPanel().getElement(), property);
    System.out.println("property=" + property + " left=" + left + " right=" + right);
    assertEquals(left, "Rammetto One");
    assertEquals(right, "Rammetto One");
  }
  
  public void testCloneFontSize() {
    rta.setText("Test");
    String property = "fontSize";
    String left = ComputedStyle.getStyleProperty(rta.getElement(), property);
    String right = ComputedStyle.getStyleProperty(rta.getHtmlSizingPanel().getElement(), property);
    System.out.println("property=" + property + " left=" + left + " right=" + right);
    assertEquals(left, right);
  }
  
  public void testCloneFontSizeAdjust() {
    rta.setText("Test");
    String property = "fontSizeAdjust";
    String left = ComputedStyle.getStyleProperty(rta.getElement(), property);
    String right = ComputedStyle.getStyleProperty(rta.getHtmlSizingPanel().getElement(), property);
    System.out.println("property=" + property + " left=" + left + " right=" + right);
    assertEquals(left, right);
  }
  
  public void testCloneFontStretch() {
    rta.setText("Test");
    String property = "fontStretch";
    String left = ComputedStyle.getStyleProperty(rta.getElement(), property);
    String right = ComputedStyle.getStyleProperty(rta.getHtmlSizingPanel().getElement(), property);
    System.out.println("property=" + property + " left=" + left + " right=" + right);
    assertEquals(left, right);
  }
  
  public void testCloneFontStyle() {
    rta.setText("Test");
    String property = "fontStyle";
    String left = ComputedStyle.getStyleProperty(rta.getElement(), property);
    String right = ComputedStyle.getStyleProperty(rta.getHtmlSizingPanel().getElement(), property);
    System.out.println("property=" + property + " left=" + left + " right=" + right);
    assertEquals(left, right);
  }
  
  public void testCloneFontVariant() {
    rta.setText("Test");
    String property = "fontVariant";
    String left = ComputedStyle.getStyleProperty(rta.getElement(), property);
    String right = ComputedStyle.getStyleProperty(rta.getHtmlSizingPanel().getElement(), property);
    System.out.println("property=" + property + " left=" + left + " right=" + right);
    assertEquals(left, right);
  }
  
  public void testCloneFontWeight() {
    rta.setText("Test");
    String property = "fontWeight";
    String left = ComputedStyle.getStyleProperty(rta.getElement(), property);
    String right = ComputedStyle.getStyleProperty(rta.getHtmlSizingPanel().getElement(), property);
    System.out.println("property=" + property + " left=" + left + " right=" + right);
    assertEquals(left, right);
  }
  
  public void testCloneLetterSpacing() {
    rta.setText("Test");
    String property = "letterSpacing";
    String left = ComputedStyle.getStyleProperty(rta.getElement(), property);
    String right = ComputedStyle.getStyleProperty(rta.getHtmlSizingPanel().getElement(), property);
    System.out.println("property=" + property + " left=" + left + " right=" + right);
    assertEquals(left, right);
  }
  
  public void testClonelineHeight() {
    rta.setText("Test");
    String property = "lineHeight";
    String left = ComputedStyle.getStyleProperty(rta.getElement(), property);
    String right = ComputedStyle.getStyleProperty(rta.getHtmlSizingPanel().getElement(), property);
    System.out.println("property=" + property + " left=" + left + " right=" + right);
    assertEquals(left, right);
  }
  
  public void testClonePadding() {
    rta.setText("Test");
    String property = "padding";
    String left = ComputedStyle.getStyleProperty(rta.getElement(), property);
    String right = ComputedStyle.getStyleProperty(rta.getHtmlSizingPanel().getElement(), property);
    System.out.println("property=" + property + " left=" + left + " right=" + right);
    assertEquals(left, right);
  }
  
  public void testClonePaddingBottom() {
    rta.setText("Test");
    String property = "paddingBottom";
    String left = ComputedStyle.getStyleProperty(rta.getElement(), property);
    String right = ComputedStyle.getStyleProperty(rta.getHtmlSizingPanel().getElement(), property);
    System.out.println("property=" + property + " left=" + left + " right=" + right);
    assertEquals(left, right);
  }
  
  public void testClonePaddingLeft() {
    rta.setText("Test");
    String property = "paddingLeft";
    String left = ComputedStyle.getStyleProperty(rta.getElement(), property);
    String right = ComputedStyle.getStyleProperty(rta.getHtmlSizingPanel().getElement(), property);
    System.out.println("property=" + property + " left=" + left + " right=" + right);
    assertEquals(left, right);
  }
  
  public void testClonePaddingRight() {
    rta.setText("Test");
    String property = "paddingRight";
    String left = ComputedStyle.getStyleProperty(rta.getElement(), property);
    String right = ComputedStyle.getStyleProperty(rta.getHtmlSizingPanel().getElement(), property);
    System.out.println("property=" + property + " left=" + left + " right=" + right);
    assertEquals(left, right);
  }
  
  public void testClonePaddingTop() {
    rta.setText("Test");
    String property = "paddingTop";
    String left = ComputedStyle.getStyleProperty(rta.getElement(), property);
    String right = ComputedStyle.getStyleProperty(rta.getHtmlSizingPanel().getElement(), property);
    System.out.println("property=" + property + " left=" + left + " right=" + right);
    assertEquals(left, right);
  }
  
  public void testCloneTextAlign() {
    rta.setText("Test");
    String property = "textAlign";
    String left = ComputedStyle.getStyleProperty(rta.getElement(), property);
    String right = ComputedStyle.getStyleProperty(rta.getHtmlSizingPanel().getElement(), property);
    System.out.println("property=" + property + " left=" + left + " right=" + right);
    assertEquals(left, right);
  }
  
  public void testCloneTextDecoration() {
    rta.setText("Test");
    String property = "textDecoration";
    String left = ComputedStyle.getStyleProperty(rta.getElement(), property);
    String right = ComputedStyle.getStyleProperty(rta.getHtmlSizingPanel().getElement(), property);
    System.out.println("property=" + property + " left=" + left + " right=" + right);
    assertEquals(left, right);
  }
  
  public void testCloneTextIndent() {
    rta.setText("Test");
    String property = "textIndent";
    String left = ComputedStyle.getStyleProperty(rta.getElement(), property);
    String right = ComputedStyle.getStyleProperty(rta.getHtmlSizingPanel().getElement(), property);
    System.out.println("property=" + property + " left=" + left + " right=" + right);
    assertEquals(left, right);
  }
  
  public void testCloneTextJustify() {
    rta.setText("Test");
    String property = "textJustify";
    String left = ComputedStyle.getStyleProperty(rta.getElement(), property);
    String right = ComputedStyle.getStyleProperty(rta.getHtmlSizingPanel().getElement(), property);
    System.out.println("property=" + property + " left=" + left + " right=" + right);
    assertEquals(left, right);
  }
  
  public void testCloneTextOutline() {
    rta.setText("Test");
    String property = "textOutline";
    String left = ComputedStyle.getStyleProperty(rta.getElement(), property);
    String right = ComputedStyle.getStyleProperty(rta.getHtmlSizingPanel().getElement(), property);
    System.out.println("property=" + property + " left=" + left + " right=" + right);
    assertEquals(left, right);
  }
  
  public void testCloneTextShadow() {
    rta.setText("Test");
    String property = "textShadow";
    String left = ComputedStyle.getStyleProperty(rta.getElement(), property);
    String right = ComputedStyle.getStyleProperty(rta.getHtmlSizingPanel().getElement(), property);
    System.out.println("property=" + property + " left=" + left + " right=" + right);
    assertEquals(left, right);
  }
  
  public void testCloneTextTransform() {
    rta.setText("Test");
    String property = "textTransform";
    String left = ComputedStyle.getStyleProperty(rta.getElement(), property);
    String right = ComputedStyle.getStyleProperty(rta.getHtmlSizingPanel().getElement(), property);
    System.out.println("property=" + property + " left=" + left + " right=" + right);
    assertEquals(left, right);
  }
  
  public void testCloneWordSpacing() {
    rta.setText("Test");
    String property = "textSpacing";
    String left = ComputedStyle.getStyleProperty(rta.getElement(), property);
    String right = ComputedStyle.getStyleProperty(rta.getHtmlSizingPanel().getElement(), property);
    System.out.println("property=" + property + " left=" + left + " right=" + right);
    assertEquals(left, right);
  }
  
  
  public void testClone() {
    rta.setText("Test");
    String property = "padding";
    String left = ComputedStyle.getStyleProperty(rta.getElement(), property);
    String right = ComputedStyle.getStyleProperty(rta.getHtmlSizingPanel().getElement(), property);
    System.out.println("property=" + property + " left=" + left + " right=" + right);
    assertEquals(left, right);
  }
  
}
