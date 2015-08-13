
&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;



# GWT JSNI #
> Testing GWT JSNI

  * [Demo](http://demogwtjsni.appspot.com)
  * [Source](http://code.google.com/p/gwt-examples/source/browse/#svn/trunk/DemoJSNI/src/org/gonevertical/demo/client)


## Code Example JSNI ##
> I send in a reference to the widget.
```
public static native void registerJsni(ToolTipWidget ttw) /*-{

	$wnd.showTooltip_id = function(elementId, html) {
		ttw.@org.gonevertical.demo.client.ToolTipWidget::showTooltip(Ljava/lang/String;Ljava/lang/String;)(elementId, html);
	}
	
	$wnd.showTooltip_xy = function(x, y, html) {
		ttw.@org.gonevertical.demo.client.ToolTipWidget::showTooltip(IILjava/lang/String;)(x, y, html);
	}
	
        $wnd.showTooltip = function(html) {
		ttw.@org.gonevertical.demo.client.ToolTipWidget::showTooltip(Ljava/lang/String;)(html);
	}
	
	$wnd.hideTooltip = function() {
		ttw.@org.gonevertical.demo.client.ToolTipWidget::hideTooltip()();
	}
	
}-*/;
```

> Returning  a value:
```
// native javascript
var myImage = new Image();
myImage.src = "/images/test.jpg";
function test() {
  var width = myImage.width;
  return width;
}

// GWT
 private static native int test() /*-{
    var v = $wnd.test();
    return v;
  }-*/;
```

## Tracking Mouse Position X &Y For Tooltip ##
> Keeping track of the mouse x and y events, and scroll top and left events, is something used in this project. I keep track of the DOM events preview, and use that to store a mouseX and a mouseY. This is like the GWT <1.5 Browser events. You can listen to the DOM events of the entire window, which I use for the tooltip placement.

```
/**
 * register mouse and scroll events
 */
private void registerMouse() {
	
	/* deprecated
	Event.addEventPreview(new EventPreview() {
		public boolean onEventPreview(Event event) {
			mouseX = event.getClientX();
			mouseY = event.getClientY();
			return false;
		}
	});
	*/
	
	// observe mouse x and y 
	Event.addNativePreviewHandler(new NativePreviewHandler() {
		public void onPreviewNativeEvent(NativePreviewEvent event) {
			NativeEvent e = event.getNativeEvent();
			
			type = e.getType();
			mouseX = e.getClientX();
			mouseY = e.getClientY();
			
			// test
			int wheel = e.getMouseWheelVelocityY();
			
			// debug
			System.out.println("x: " + mouseX + " y: " + mouseY + " wheel: " + wheel + " type: " + type);
		}
	});
	
	// observe scroll event, so we can offset x and y for tooltip
	Window.addWindowScrollHandler(new ScrollHandler() {
		public void onWindowScroll(ScrollEvent event) {
			
			scrollLeft = event.getScrollLeft();
			scrollTop = event.getScrollTop();
			
			// debug
			System.out.println("scrollLeft: " + scrollLeft + " scrollTop: " + scrollTop);
		}
	});
}
```

## Absolute Event Panel ##
> I also create an absolute event panel that I can observe and listen to mouse over and out on.

```
public class ToolTipPanel extends AbsolutePanel implements HasMouseOverHandlers, HasMouseOutHandlers {

	public ToolTipPanel() {
                // only observe/listen to mouse events in this panel
		sinkEvents(Event.MOUSEEVENTS);
	}

	public HandlerRegistration addMouseOutHandler(MouseOutHandler handler) {
		return addHandler(handler, MouseOutEvent.getType());
	}

	public HandlerRegistration addMouseOverHandler(MouseOverHandler handler) {
		return addHandler(handler, MouseOverEvent.getType());
	}

}
```

## Including GWT Module Into Any WebSite ##
> You can incude this code in any website, by copying the ./war/demojsni/ directory into another websites root directory.

> Then all you need to do is include the javascript.
```
<script type="text/javascript" language="javascript" src="demojsni/demojsni.nocache.js"></script>
```

![http://demogwtjsni.appspot.com/images/files.png](http://demogwtjsni.appspot.com/images/files.png)


&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;
