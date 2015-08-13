
&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;


## My Workaround to Pasting ##
> In development, but here you go if your early.
  * [WorkAround Source](http://code.google.com/p/gwt-examples/source/browse/#svn%2Ftrunk%2FGoneVertical-Core%2Fsrc%2Forg%2Fgonevertical%2Fcore%2Fclient%2Finput%2Frichtext%2Fworkaround) - I only added to hook events in the impl classes.

# RichTextArea Paste Hack #
> I want to intercept paste or onpaste in richtextarea

## Paste Hack ##
> I found where to intercept a paste event - See the wnd.addEventListener('paste', elem.gwt\_test, true);...
```
  // my inherited RichTextArea
  public WiseRichTextArea(boolean hideBorderUntilHover, boolean grow) {
    super();
    setup(hideBorderUntilHover, grow);
  }

  private void setup(boolean hideBorderUntilHover, boolean grow) {
    this.hideBorderUntilHover = hideBorderUntilHover;
    this.grow = grow;

    addStyleName("gv-core-WiseRichTextArea");

    setUpEditHover();  

    setupHandlers();

    sinkEvents(Event.ONPASTE);
  }

  @Override 
  public void onBrowserEvent(Event event) { 
    super.onBrowserEvent(event); 
    switch (event.getTypeInt()) { 
    case Event.ONPASTE: 
      System.out.println("Paste Detected"); 
      Window.alert("Paste Works!!! Yippie!!!");
      break; 
    } 
  } 
```
> My hack:
```
// RichTextAreaImplSafari.java source hack
 @Override
  protected native void hookEvents() /*-{
    var elem = this.@com.google.gwt.user.client.ui.impl.RichTextAreaImpl::elem;
    var wnd = elem.contentWindow;

    elem.__gwt_handler = function(evt) {
      if (elem.__listener) {
        if (@com.google.gwt.user.client.impl.DOMImpl::isMyListener(Ljava/lang/Object;)(elem.__listener)) {
          @com.google.gwt.user.client.DOM::dispatchEvent(Lcom/google/gwt/user/client/Event;Lcom/google/gwt/user/client/Element;Lcom/google/gwt/user/client/EventListener;)(evt, elem, elem.__listener);
        }
      }
    };    

    wnd.addEventListener('keydown', elem.__gwt_handler, true);
    wnd.addEventListener('keyup', elem.__gwt_handler, true);
    wnd.addEventListener('keypress', elem.__gwt_handler, true);
    wnd.addEventListener('mousedown', elem.__gwt_handler, true);
    wnd.addEventListener('mouseup', elem.__gwt_handler, true);
    wnd.addEventListener('mousemove', elem.__gwt_handler, true);
    wnd.addEventListener('mouseover', elem.__gwt_handler, true);
    wnd.addEventListener('mouseout', elem.__gwt_handler, true);
    wnd.addEventListener('click', elem.__gwt_handler, true);

    // Whats needed . this works.
    wnd.addEventListener('paste', elem.__gwt_handler, true);

    // Focus/blur event handlers. For some reason, [add|remove]eventListener()
    // doesn't work on the iframe element (at least not for focus/blur). Don't
    // dispatch through the normal handler method, as some of the querying we do
    // there interferes with focus.
    wnd.onfocus = function(evt) {
      if (elem.__listener) {
        @com.google.gwt.user.client.DOM::dispatchEvent(Lcom/google/gwt/user/client/Event;Lcom/google/gwt/user/client/Element;Lcom/google/gwt/user/client/EventListener;)(evt, elem, elem.__listener);
      }
    };

    wnd.onblur = function(evt) {
      if (elem.__listener) {
        @com.google.gwt.user.client.DOM::dispatchEvent(Lcom/google/gwt/user/client/Event;Lcom/google/gwt/user/client/Element;Lcom/google/gwt/user/client/EventListener;)(evt, elem, elem.__listener);
      }
    };
  }-*/;
```

## RichTextArea Inheritance ##
![https://google-web-toolkit.googlegroups.com/attach/36acd0598bc9022a/RichTextAreaInheritance.png?view=1&part=4&.jpg](https://google-web-toolkit.googlegroups.com/attach/36acd0598bc9022a/RichTextAreaInheritance.png?view=1&part=4&.jpg)


# (older notes) #
> I am getting an error when adding a RichTextEditor to a DialogBox. I have tried a few workarounds and haven't found a good solution. I tried Timing the adding of the rta, this works for the first time the DialogBox is centered(). After hiding and trying for a second time to center() it, the Dialogbox will fail because of the rich text editor.

  * Built with GWT Version 0.0.2415\_M2
  * project located in source code. Source > trunk > gwt-test-RichTextEditor [source](http://code.google.com/p/gwt-examples/source/browse)
  * Demo of error (compiled war project) - http://gawkat.com/RichTextEditorPopUp/
  * [Download WAR](http://code.google.com/p/gwt-examples/downloads/list?can=1&q=RichTextEditor&colspec=Filename+Summary+Uploaded+Size+DownloadCount)

## I found a Bug ##
> [Issue 2396](http://code.google.com/p/google-web-toolkit/issues/detail?id=2396)

### Details ###
> Error in eclipse Developments shell debugger
```
[ERROR] Uncaught exception escaped
java.lang.RuntimeException: Failed to invoke native method: @com.google.gwt.user.client.ui.impl.RichTextAreaImplStandard::unhookEvents() with 0 arguments.
	at com.google.gwt.dev.shell.moz.LowLevelMoz.invoke(LowLevelMoz.java:132)
	at com.google.gwt.dev.shell.moz.ModuleSpaceMoz.doInvoke(ModuleSpaceMoz.java:98)
	at com.google.gwt.dev.shell.ModuleSpace.invokeNative(ModuleSpace.java:474)
	at com.google.gwt.dev.shell.ModuleSpace.invokeNativeVoid(ModuleSpace.java:275)
	at com.google.gwt.dev.shell.JavaScriptHost.invokeNativeVoid(JavaScriptHost.java:121)
	at com.google.gwt.user.client.ui.impl.RichTextAreaImplStandard.unhookEvents(RichTextAreaImplStandard.java:308)
	at com.google.gwt.user.client.ui.impl.RichTextAreaImplStandard.uninitElement(RichTextAreaImplStandard.java:228)
	at com.google.gwt.user.client.ui.RichTextArea.onDetach(RichTextArea.java:435)
	at com.google.gwt.user.client.ui.Panel.doDetachChildren(Panel.java:174)
	at com.google.gwt.user.client.ui.Widget.onDetach(Widget.java:146)
```

> Error in firefox (from demo)
```
Error: uncaught exception: [Exception... "Component returned failure code: 0x80004005 (NS_ERROR_FAILURE) [nsIDOMEventTarget.removeEventListener]"  nsresult: "0x80004005 (NS_ERROR_FAILURE)"  location: "JS frame :: http://gawkat.com/RichTextEditorPopUp/8BB667AFA4257A2DB4290D56312C8A25.cache.html :: kB :: line 381"  data: no]
```





&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;
