
&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" scrolling=0 /&gt;

# Image Water Marker Using HTML5 & GWT #
> This app was produced using HTML5 elements. Canvas, CSS3 corners, FileApi, Input Range.

  * [Water Marker App](http://waterbughack.appspot.com/) - The finished app at the end of the hackathon
  * [Source](http://code.google.com/p/gwt-examples/source/browse/trunk/ImageMod/src/local/hackathon/client/watermark/) - The finished source at the end of the hackathon

## Team ##
> Brandon Donnelson, Mikhail Fassakhov, Matthew Synder, Daniel Sofer, Clive Boulton

# Our Hackathon HTML5 Goal #
> We tried to make an app that could put a copyright and/or water mark on an image quickly using HTML5 canvas. This was a challenge given we had a short amount of time. We came up with a working prototype in this version.

# Where #
> Google Kirkland.
  * [Pictures](https://plus.google.com/111739836936169749229/posts/Kwre7fB5CCk) - Event Pictures


## Water Marker Widget Source ##
> The main part of the app happens here: [LinkToClass](http://code.google.com/p/gwt-examples/source/browse/trunk/ImageMod/src/local/hackathon/client/watermark/WaterMarkWidget.java)
```
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.canvas.dom.client.FillStrokeStyle;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.dom.client.LoadEvent;
import com.google.gwt.event.dom.client.LoadHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;

public class WaterMarkWidget extends Composite {

  private FileUpload fileUpload;

  private FlowPanel pProcess;

  private FlowPanel fpImage;

  private FlowPanel fpRight;
  private TextArea txtrCopyrightThis;
  private Context2d context;
  private Image image;
  private TextBox tbColor;
  private HTML tbSize;
  
  private Image imgLogo;

  private int fontSize = 40;
  private ListBox comboBox;

  /**
   * init
   */
  public WaterMarkWidget() {

    FlowPanel pWidget = new FlowPanel();
    initWidget(pWidget);
    pWidget.setWidth("100%");

    FlowPanel fpWidget = new FlowPanel();
    pWidget.add(fpWidget);
    fpWidget.setSize("100%", "100%");
    fpWidget.setStyleName("WaterMarkWidget");

    fpRight = new FlowPanel();
    fpWidget.add(fpRight);
    fpRight.setStyleName("typewindow");
    
    imgLogo = new Image("/images/logo.png");
    fpRight.add(imgLogo);

    fileUpload = new FileUpload();
    fpRight.add(fileUpload);

    txtrCopyrightThis = new TextArea();
    txtrCopyrightThis.setText("Copyright This");
    fpRight.add(txtrCopyrightThis);
    txtrCopyrightThis.setWidth("100%");
    
    tbColor = new TextBox();
    tbColor.setText("#000000");
    fpRight.add(tbColor);
    tbColor.setWidth("100%");
    
    tbSize = new HTML("<input id=\"stepper\" type=\"range\" min=\"1\" step=\"2\" value=\"40\">");
    fpRight.add(tbSize);
    tbSize.setSize("100%", "");
    
    comboBox = new ListBox();
    comboBox.addItem("Abel");
    comboBox.addItem("Rationale");
    comboBox.addItem("Rochester");
    comboBox.addItem("Actor");
    comboBox.addItem("Carme");
    comboBox.addItem("Federo");
    fpRight.add(comboBox);
    
    
    //bDownload = new PushButton("Download");
    //bDownload.setEnabled(false);
    
    //bDownload.setHTML("Download");


    fpImage = new FlowPanel();
    fpWidget.add(fpImage);
    fpImage.setStyleName("uploadedimage");

    pProcess = new FlowPanel();
    pWidget.add(pProcess);

    setup();
  }

  private void setup() {

    Element e = fileUpload.getElement();
    observeBlobData(this, e);

    redraw();
  }

  private void redraw() {
    Timer t = new Timer() {
      public void run() {
        drawCanvas();
        redraw();
      }
    };
    t.schedule(400);
  }

  /**
   * draw widget stuff
   */
  public void draw() {

  }

  private static native void observeBlobData(WaterMarkWidget w, Element e) /*-{
  function handleFileSelect(evt) {  
      var files = evt.target.files;
      var output = [];
      for (var i=0, f; f = files[i]; i++) {
         var name = f.name;
         var type = f.type; 

         var reader = new FileReader();  
         reader.onload = function(evt) { 
           w.@local.hackathon.client.watermark.WaterMarkWidget::setBlobData(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)(evt.target.result, name, type); 
         }  
         reader.readAsDataURL(files[i]);
      }
    }
    e.addEventListener('change', handleFileSelect, false);
  }-*/;

  public void setBlobData(String base64, String name, String type) {
    setFileAsImage(base64);
  }

  private void setFileAsImage(String base64) {
    image = new Image(base64);
    pProcess.add(image); 
    image.addLoadHandler(new LoadHandler() {
      public void onLoad(LoadEvent event) {
        pProcess.clear();
        drawCanvas();
      }
    });
  }

  private void drawCanvas() {
    if (image == null) {
      return;
    }
    fpImage.clear();

    ImageElement imageElement = ImageElement.as(image.getElement());

    Canvas canvasTmp = Canvas.createIfSupported();
    fpImage.add(canvasTmp);
    canvasTmp.setWidth("100%");
    canvasTmp.setStyleName("uploadedimage");

    canvasTmp.setCoordinateSpaceHeight((int) image.getHeight());
    canvasTmp.setCoordinateSpaceWidth((int) image.getWidth());

    context = canvasTmp.getContext2d();

    int dx = 0;
    int dy = 0;
    context.drawImage(imageElement, dx, dy);

    context.setFillStyle(getFontStyle());
    context.setFont(getFontSize() + " " + getFontType());
    context.setTextBaseline("top");
    context.setTextAlign("right");
    
    context.fillText(getText(), (image.getWidth()-20), image.getHeight() - fontSize-30); 
  }

  private String getFontType() {
    int sel = comboBox.getSelectedIndex();
    String s = comboBox.getValue(sel);
    if (s == null) {
      s = "Helvetica";
    }
    return s;
  }

  private String getText() {
    String s = txtrCopyrightThis.getText();    
    return s;
  }

  private String getFontStyle() {
    String s = tbColor.getText();
    return s;
  }
  
  private String getFontSize() {
    com.google.gwt.user.client.Element e = DOM.getElementById("stepper");
    fontSize  = e.getPropertyInt("value");
    
    return fontSize + "px";
  }

  private void changeText() {
 
  }
  
}
```