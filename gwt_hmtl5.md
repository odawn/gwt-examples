
&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;



# HTML5 and GWT #
> I'm going to write down my HTML5 stuff here. I'm in love with the HTML5 stuff!!!!!!
  * [HTML5 Canvas Specs](http://www.w3.org/TR/2dcontext/#canvasrenderingcontext2d)

## Demo ##
> Here is a demo.

  * [Demo](http://demogwtcanvas.appspot.com/) - I made this to test GAE base64 uploading (which is broken at the moment)
  * [Source](http://code.google.com/p/gwt-examples/source/browse/#svn%2Ftrunk%2FDemoGwtCanvas) - Source for the stuff. I made this demo to show GAE has a bug uploading base64
  * [Another Demo](http://code.google.com/p/gwt-examples/wiki/HTML5_Hackathon_ImageWaterMarker) - Image Watermarker Demo

### Some Issues with Base64 on GAE ###
> Some issues exist with base64 upload encoding. So if you working with images and want to upload them, there could be some challenges. I haven't found work arounds to them yet. I thought I had it working, but for some reason I can't.
  * http://code.google.com/p/googleappengine/issues/detail?id=4265 - Base64 encoded won't upload
  * http://code.google.com/p/googleappengine/issues/detail?id=4806 - Base64 size designation problem
  * [Group Discussion About Issues](http://code.google.com/appengine/forum/java-forum.html?place=topic%2Fgoogle-appengine-java%2FYWKZekpSpc0%2Fdiscussion)

## Image Scale / Resize ##
> I had trouble scaling an image in app engine and HTML5 canvas has so many more options and fun things to work with. The canvas uses the imageElement, which you can get from GWT Image. Note you have to use a load handler before you can work with the Image.

> Scale an image like this:
```
private Canvas canvasScreen;
private Context2d contextScreen;

public void onModuleLoad() {
    canvasScreen = Canvas.createIfSupported();
     
    if (canvasScreen == null) {
      RootPanel.get().add(new Label("Sorry, your browser doesn't support the HTML5 Canvas element"));
      return;
    }
    // for example
    canvasScreen.setCoordinateSpaceHeight(1000);
    canvasScreen.setCoordinateSpaceWidth(1000);
    
    contextScreen = canvasScreen.getContext2d();
    RootPanel.get().add(canvasScreen);
   
   loadImage();
}

// This is important to use a handler!
private void loadImage() {
    final Image img = new Image("/images/test.jpg");
    RootPanel.get().add(img);
    img.setVisible(false);
    
    img.addLoadHandler(new LoadHandler() {
      public void onLoad(LoadEvent event) {
        scale(img);
      }
    });
}

private void scale(Image img) {
    ImageData imageData = scaleImage(img, .1);
    
    drawToScreen(imageData);
}

private ImageData scaleImage(Image image, double scaleToRatio) {
    
    Canvas canvasTmp = Canvas.createIfSupported();
    Context2d context = canvasTmp.getContext2d();

    double ch = (image.getHeight() * scaleToRatio) + 100;
    double cw = (image.getWidth() * scaleToRatio) + 100;

    canvasTmp.setCoordinateSpaceHeight((int) ch);
    canvasTmp.setCoordinateSpaceWidth((int) cw);
    
    ImageElement imageElement = ImageElement.as(image.getElement());
   
    // s = source
    // d = destination 
    double sx = 0;
    double sy = 0;
    double sw = imageElement.getWidth();
    double sh = imageElement.getHeight();
    
    double dx = 0;
    double dy = 0;
    double dw = imageElement.getWidth();
    double dh = imageElement.getHeight();
    
    // tell it to scale image
    context.scale(scaleToRatio, scaleToRatio);
    
    // draw image to canvas
    context.drawImage(imageElement, sx, sy, sw, sh, dx, dy, dw, dh);
    
    // get image data
    double w = dw * scaleToRatio;
    double h = dh * scaleToRatio;
    ImageData imageData = context.getImageData(0, 0, w, h);

    return imageData;
}

private void drawToScreen(ImageData imageData) {
    Canvas canvasTmp = Canvas.createIfSupported();
    RootPanel.get().add(canvasTmp);
    canvasTmp.setStyleName("test1");
    canvasTmp.setCoordinateSpaceHeight((int) imageData.getHeight());
    canvasTmp.setCoordinateSpaceWidth((int) imageData.getWidth());
    Context2d context = canvasTmp.getContext2d();
    context.putImageData(imageData, 0, 0);
}
```

## Crop Image ##
> Crop an Image using the canvas element. Look at the scaling example for the image handler.
```
private ImageData cropImage(Image image, double sx, double sy, double sw, double sh) {
    
    Canvas canvasTmp = Canvas.createIfSupported();
    //RootPanel.get().add(canvasTmp); // debug it
    Context2d context = canvasTmp.getContext2d();

    canvasTmp.setCoordinateSpaceHeight((int) sh+100);
    canvasTmp.setCoordinateSpaceWidth((int) sw+100);
    
    ImageElement imageElement = ImageElement.as(image.getElement());
   
    double dx = 0;
    double dy = 0;
    double dw = sw;
    double dh = sh;
    
    // draw image to canvas
    context.drawImage(imageElement, sx, sy, sw, sh, dx, dy, dw, dh);
    
    // get image data
    double w = sw;
    double h = sh;
    ImageData imageData = context.getImageData(0, 0, w, h);

    return imageData;
}
```

## Scale and Crop ##
> Scale and crop in one method. Use a load handler like the first example on this page.
```
private ImageData scaleAndCropImage(Image image, double scaleToRatio, double sx, double sy, double sw, double sh) {
    
    Canvas canvasTmp = Canvas.createIfSupported();
    //RootPanel.get().add(canvasTmp);
    Context2d context = canvasTmp.getContext2d();

    double ch = (image.getHeight() * scaleToRatio) + 100;
    double cw = (image.getWidth() * scaleToRatio) + 100;

    canvasTmp.setCoordinateSpaceHeight((int) ch);
    canvasTmp.setCoordinateSpaceWidth((int) cw);
    
    ImageElement imageElement = ImageElement.as(image.getElement());

    // tell it to scale image
    context.scale(scaleToRatio, scaleToRatio);
    
    // draw image to canvas
    // s = source
    // d = destination     
    double dx = 0;
    double dy = 0;
    context.drawImage(imageElement, dx, dy);
    
    // get image data - if you go greater than the scaled image nothing will show up
    ImageData imageData = context.getImageData(sx, sy, sw, sh);

    return imageData;
}
```

## Test Canvas Base64 Mime Decoding ##
> Getting the imagedata canvas.toDataUrl("image/png") is encoded in base 64 Mime format. Here is one test I used to see if I was doing things correctly. This snippet is for the server side. Although the Base64Util class can be used on the client side, I haven't tried it.

```
    // I manually retrieved the base64 string and removed the first part up to the comma.
    // base64 = canvasTmp.toDataUrl("image/png"); // base64 Mime format
    String base64 = "iVBORw0KGgoAAAANSUhEUgA...="; //canvas.toDataUrl("image/png");
    
    byte[] b = Base64Util.decode(base64);

    String s = new String(b);
   
    String path = "/Users/branflake2267/Downloads/test_coder.png";
    File file = new File(path);
    try {
      BufferedWriter out = new BufferedWriter(new FileWriter(file));
      out.write(s);
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
```
> Base64 encode and decoder - I can't remember were I found this.
```
public class Base64Util {
  private final static char[] ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/".toCharArray();

  private static int[]  toInt   = new int[128];

  static {
    for(int i=0; i< ALPHABET.length; i++){
      toInt[ALPHABET[i]]= i;
    }
  }

  /**
   * Translates the specified byte array into Base64 string.
   *
   * @param buf the byte array (not null)
   * @return the translated Base64 string (not null)
   */
  public static String encode(byte[] buf){
    int size = buf.length;
    char[] ar = new char[((size + 2) / 3) * 4];
    int a = 0;
    int i=0;
    while(i < size){
      byte b0 = buf[i++];
      byte b1 = (i < size) ? buf[i++] : 0;
      byte b2 = (i < size) ? buf[i++] : 0;
      int mask = 0x3F;
      ar[a++] = ALPHABET[(b0 >> 2) & mask];
      ar[a++] = ALPHABET[((b0 << 4) | ((b1 & 0xFF) >> 4)) & mask];
      ar[a++] = ALPHABET[((b1 << 2) | ((b2 & 0xFF) >> 6)) & mask];
      ar[a++] = ALPHABET[b2 & mask];
    }
    switch(size % 3){
    case 1: ar[--a]  = '=';
    case 2: ar[--a]  = '=';
    }
    return new String(ar);
  }

  /**
   * Translates the specified Base64 string into a byte array.
   *
   * @param s the Base64 string (not null)
   * @return the byte array (not null)
   */
  public static byte[] decode(String s){
    int delta = s.endsWith( "==" ) ? 2 : s.endsWith( "=" ) ? 1 : 0;
    byte[] buffer = new byte[s.length()*3/4 - delta];
    int mask = 0xFF;
    int index = 0;
    for(int i=0; i< s.length(); i+=4){
      int c0 = toInt[s.charAt( i )];
      int c1 = toInt[s.charAt( i + 1)];
      buffer[index++]= (byte)(((c0 << 2) | (c1 >> 4)) & mask);
      if(index >= buffer.length){
        return buffer;
      }
      int c2 = toInt[s.charAt( i + 2)];
      buffer[index++]= (byte)(((c1 << 4) | (c2 >> 2)) & mask);
      if(index >= buffer.length){
        return buffer;
      }
      int c3 = toInt[s.charAt( i + 3 )];
      buffer[index++]= (byte)(((c2 << 6) | c3) & mask);
    }
    return buffer;
  } 

}
```

&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="110" width="760" border="0" /&gt;

## Upload Canvas Image ##
> This is a snippet of how to upload an image to the blobstore from the base64 encoded string given by toDataUrl();
```
public class UploadTest extends Composite {
  
  private RpcCoreServiceAsync rpcCore;
  private String url;
  private ClientPersistence cp;

  private static Random random = new Random(); 
  
  public UploadTest(ClientPersistence cp) {
    this.cp = cp;
    
    PushButton b = new PushButton("Test");
    
    VerticalPanel vp = new VerticalPanel();
    vp.add(b);
    
    initWidget(vp);
    
    b.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        send();
      }
    });
    
    rpcCore = RpcCore.init();
    
    getRpc_NewBlobKeyUrl();
  }
  
  private void getRpc_NewBlobKeyUrl() {
    
    // TODO use this in the future
    BlobDataFilter filter = new BlobDataFilter();
    
    rpcCore.getBlobStoreUrl(filter, new AsyncCallback<String>() {
      public void onSuccess(String url) {
        setUrl(url);
      }

      public void onFailure(Throwable caught) {
        cp.setRpcFailure(caught);
      }
    });
    
  }
  
  protected void setUrl(String url) {
    this.url = url;
  }

  private String getFile() {
    String s = ""; // data:image/png;base64,
    s = "iVBORw0KGgoAAAA.........zAZa+mCYAAAAASUVORK5CYII=";
    return s;
  }
  
  private void send() {
    
    String boundary = createBoundary();
    
    String requestData = getRequestData(boundary);
    
    //System.out.println(requestData);
    //url = "/TestOut/";
    
    RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, url);
    builder.setHeader("Content-Type", "multipart/form-data; boundary=" + boundary);
    builder.setHeader("Content-Length", Long.toString(requestData.length()));
    try {
      builder.sendRequest(requestData, new RequestCallback() {
        public void onResponseReceived(Request request, Response response) {
          if (response.getStatusCode() == 200) {
            System.out.println("response=" + response.getText());
          }
        }
        public void onError(Request request, Throwable exception) {
          exception.printStackTrace();
        }
      });
    } catch (RequestException e) {
      e.printStackTrace();
    }
  }
  
  private String createBoundary() {
    return "----GoneVerticalBoundary" + getRandomStr() + getRandomStr();
  }
  
  private String getRandomStr() {
    return Long.toString(random.nextLong(), 36);
  }

  private String getRequestData(String boundary) {
    String s = "";
    
    s += "--" + boundary + "\r\n";
    s += getRequestParameter("oid", cp.getUserId() + "");

    s += "--" + boundary + "\r\n";
    s += getRequestParameter("ltid", cp.getUserId() + "");

    s += "--" + boundary + "\r\n";
    s += getRequest_Image("test_new_coder_14.png", "image/png", getFile());
    
    s += "--" + boundary + "--\r\n"; //end
    
    return s;
  }
  
  private String getRequestParameter(String key, String value) {
    return "Content-Disposition: form-data; name=\"" + key + "\"\r\n\r\n" + value + "\r\n";
  }

  private String getRequest_Image(String fileName, String contentType, String file) {
    String s = "";
    s += "Content-Disposition: form-data; name=\"File\"; filename=\"" + fileName + "\"\r\n";
    s += "Content-Type: " + contentType + "\r\n";
    s += "Content-Transfer-Encoding: base64 \r\n\r\n";
    s += file;
    s += "\r\n";
    return s;
  }

}
```


## File Upload (Input) To Canvas - readAsDataURL ##
> When I want to use GWT FileUpload widget and then observe changes in native javascript so to use the FileReader to get the readAsDataURL.

> Use a file input to get an image to canvas.
```
public void draw() {
    
    FileUpload fu = new FileUpload();
    pUpload.add(fu);
    
    Element e = fu.getElement();
    
    observeImageData(this, e);
  }

  /**
   * observe changes on the file upload input
   * 
   * @param fuw this class (widget)
   * @param e
   */
  public static native void observeImageData(FileUploaderWidget_v2 fuw, Element e) /*-{
    function handleFileSelect(evt) {
      var files = evt.target.files;
      var output = [];
      for (var i = 0, f; f = files[i]; i++) {
         //var name = f.name;
         //var type = f.type; 
         //var size = f.size;
         var reader = new FileReader();  
         reader.onload = function (evt) {  
           fuw.@org.gonevertical.core.client.ui.admin.blobs.FileUploaderWidget_v2::setImage(Ljava/lang/String;)(evt.target.result);
         }  
         reader.readAsDataURL(files[i]);
      }
    }
    e.addEventListener('change', handleFileSelect, false);
  }-*/;

  /**
   * process base64 mime imagedata content string
   * 
   * @param imageData
   */
  public void setImage(String imageData) {
    final Image image = new Image(imageData);
    pProcess.add(image);
    image.addLoadHandler(new LoadHandler() {
      public void onLoad(LoadEvent event) {
        drawToCanvas(image);
      }
    });
  }
  
  private void drawToCanvas(Image image) {

    // Tdraw full size, then upload
    drawFullSize(image);
    
    // draw scaled image, then upload
    createThumbnail(image, 150);
    
  }
```

## Decoding Base64 ##
> I'm experimenting with this in chrome.
  * http://developers.whatwg.org/webappapis.html#dom-windowbase64-atob
```
  private static native void decodeBase64(UploadImage ui, String base64) /*-{
    var bin = window.atob(base64); 
    ui.@org.gonevertical.democanvas.client.UploadImage::setBinary(Ljava/lang/String;)(bin);
  }-*/;
  
  private void setBinary(String bin) {
    binaryData = bin;
  }
```

&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;
