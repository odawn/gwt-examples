
&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;
# Table of Contents #


# Quick Tips #
> Quick tips that might help you. These are things that I have learned along the way that make my job easier.

# Console/Syslog Debugging #
> Using System.out.println to print to debugger console or when running it on the server it will print to the /var/log/Syslog which is very helpful for debugging.
```
System.out.println("MyMethod has this var: " + this.MyVar);
```

# Print RPCs Serialized Response #
> I found this in GWT sample Dynamic Table. I love this function b/c it prints out the object that will be sent in the RPC. Put this snippet in your server side of the rpc call, MyServerImpl.java class.
```
//stick this in your server side, MyServiceImpl.java class
/**
 * Write the serialized response out to stdout. This is a very unusual thing
 * to do, but it allows us to create a static file version of the response
 * without deploying a servlet.
 */
protected void onAfterResponseSerialized(String serializedResponse) {
  System.out.println(serializedResponse);
}
```

# Serialization - Passing Data Around #
> I love this way of moving data around from client to server, server to client and between classes. This has got to be one of the coolest tricks.

```
/**
 * Hold relevant data for Bible. And pass it around.
 * This class is meant to be serialized in RPC calls.
 */
public class BibleData implements IsSerializable {

	//bible passage choices
	public String[] StartBook; 
	public int StartChapter;
	public int StartVerse;
	public String[] EndBook;
	public int EndChapter;
	public int EndVerse;
	
	//bible passage selections
	public int rsID;
	public int selStartBook;
	public int selStartChapter;
	public int selStartVerse;
	public int selEndBook;
	public int selEndChapter;
	public int selEndVerse;
}
```

# Image #
> Show an external image on your page.
```
//stick the image in the ~/www folder
HorizontalPanel pLoading = new HorizontalPanel();
String sImage = GWT.getModuleBaseURL() + "loading2.gif";
Image image = new Image(sImage);
pLoading.add(image);
```

# String Comparison #
> You can compare strings like this. When using string, its best to always init with null, and then always check the string var before doing something with it.
```
//myString.equals(OtherString) is boolean

//example string
String myString = null;

// check string before doing something with it
if (myString == null) {
  // return ""; or return null; or break;
  // or myString = ""; then if (myString.length())
}

// skip errors in a method for null string before comparing!!!!
if (myString == null) {
   return;
}

//comapre like (this == that)
if (myString.equals(myOtherString)) {
    //do this
}

//compare like (this != nothing)
if (myString.equals(null) == false) {
    //do this
}

//like (this != nothing)
if (myString.equals("") == false) {
    //do this
}

//my favorite way to compare so far
if (myString == null || myString == "") {
   //usually don't need this, but helps me skip erros when I don't have an object to comapre too.
} else if (myString.equals("some string")) {
   //do stuff
} else {
   //do stuff
}

```

# Clicklistener #
> Avoid calling a listener more than once! Add the clicklistener in the constructor of the widget not a method called over and over. Every time you call a method with a clicklister, you add another observer to that object. Then if you want to do something with that object, you may cause multiples of something to happen, which is annoying!
```
private HorizontalPanel pWidgetContent = new HorizontalPanel();
private PushButton bPushMe = new PushButton("Push Me");
/**
 * constructor - init a widget maybe
 */
public MyClass() {
  initWidget(pWidgetContent);
  bPushMe.addClickListener(this);
}

public void drawMorePanelsEveryOnceInWhile() {
  //don't stick listener here
  //bPushMe.addClickListener(this);
}

public void myMethod() {
  //do something
}

public void onClick(Widget sender) {
  if (sender == bPushMe) {
     this.myMethod();
  }
}
```

# Keyboardlistener #
  * Example of a keyboard listener. Listen to ten key or number pad with numlock on.
  * [GWT Interface KeyboardListener Reference](http://google-web-toolkit.googlecode.com/svn/javadoc/1.4/com/google/gwt/user/client/ui/KeyboardListener.html)
  * Google Example on the [Textbox](http://google-web-toolkit.googlecode.com/svn/javadoc/1.4/com/google/gwt/user/client/ui/TextBox.html)
```
public class CalculatorWidget extends Composite implements ClickListener, KeyboardListener {
        TextBox tbDisplay = new TextBox();

   	public CalculatorWidget() {

		//listen to display
		tbDisplay.addKeyboardListener(this);

                RootPanel.get().add(tbDisplay);
	}


        public void onClick(Widget sender) {
	}
	
	public void onKeyDown(Widget sender, char keyCode, int modifiers) {
	}
	
	public void onKeyPress(Widget sender, char keyCode, int modifiers) {
	
		RootPanel.get().add(new Label("key pressed: " + keyCode + " modifier: " + modifiers));
		
		switch (keyCode) {
		case KeyboardListener.KEY_ALT: 
			break;
		case KeyboardListener.KEY_BACKSPACE:
			break;
		case KeyboardListener.KEY_CTRL:
			break;
		case KeyboardListener.KEY_DELETE:
			break;
		case KeyboardListener.KEY_DOWN:
			break;
		case KeyboardListener.KEY_END:
			break;
		case KeyboardListener.KEY_ENTER:
			break;
		case KeyboardListener.KEY_ESCAPE:
			break;
		case KeyboardListener.KEY_HOME:
			break;
		case KeyboardListener.KEY_LEFT:
			break;
		case KeyboardListener.KEY_PAGEDOWN:
			break;
		case KeyboardListener.KEY_PAGEUP:
			break;
		case KeyboardListener.KEY_RIGHT:
			break;
		case KeyboardListener.KEY_SHIFT:
			break;
		case KeyboardListener.KEY_TAB:
			break;
		case KeyboardListener.KEY_UP:
			break;
		default:
			
			String keyPressed = Character.toString(keyCode);
		
			if (keyPressed.equals("+") & (modifiers == 1) | keyPressed.equals("+")) { // +	
			} else if ((keyPressed.equals("8") & (modifiers == 1)) | keyPressed.equals("*")) { // *
			} else if (keyPressed.equals("-")) { // -
			} else if (keyPressed.equals("/")) { // /
			} else if (Character.isDigit(keyCode)) { //0-9
			} else if (keyPressed.equals("=")) { // =
			} else { //all other characters
				Window.alert("others");
			}

			break;
		}//end case
	}//end onKeyPress
	
	public void onKeyUp(Widget sender, char keyCode, int modifiers) {	
	}
		
	
}
```

&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;

# Get ListBox Value's Index #
> Here is a method used to find the value's index, so then you can setSelected for listbox by value.
```
/**
 * get list box value's index
 *
 * @param lb - what listbox are you looking for value's index
 * @param value - what is the value's index your looking for
 * @return
 */
public static int getListBoxValuesIndex(ListBox lb, String value) {
  if (value == null) {
    return 0;
  }
  for (int i = 0; i < lb.getItemCount(); i++) {
    String CompareValue = lb.getValue(i);
    if (value.equals(CompareValue)) {
      return i;
    }
  }
  return 0;
}
```

# Get Listbox Selected Value (int) #
> Get a listbox's selected value
```
  public static int getListBoxSelectedValue(ListBox lb) {
    int sel = lb.getSelectedIndex();
    int r = 0;
    if (sel > 0) {
      r = Integer.parseInt(lb.getValue(sel));
    }
    return r;
  }
```
# Get Widget(s) Data In a Panel #
> Getting Widgets in a panel is a nice way to interact with widgets you make. You can add several widgets into a panel, and get all of them by looping through the widgets in the panel. I make a specific panel to put all my reminder widgets in. Then I gather data and use it elsewhere.
```
/**
 * get my reminders
 * @return ReminderData
 */
private ReminderData[] getReminderData() {
	
        //count my reminders i have in the verticalpanel pReminder
	int count = pReminder.getWidgetCount();
	
        //I use ReminderData object to transport my data around
	ReminderData[] reminderData = new ReminderData[count];
	
        //loop through my ReminderWidget(s) in the pReminderPanel
	for (int i=0; i < count; i++) {

                //cast the widget to my ReminderWidget
		ReminderWidget rw = (ReminderWidget) pReminder.getWidget(i);

                //I use a method to gather up the values in the reminder Widget
		reminderData[i] = rw.getReminderData();
	}

        //then I return the values to the next object, and then to rpc
	return reminderData;
}
```

# Observe Events On Vertical Panel #
> Add this class and use it just like you would a vertical panel. This extends vertical panel and allows you to watch for events on the panel. This class below is only watching for clicks, but you can watch for all the events by adding them to sink events.

```
public class WatchEventPanel extends VerticalPanel {

	/**
	 * change listener to watch for fired events
	 */
	private ChangeListenerCollection changeListeners;
	
	/**
	 * constructor - extending VerticalPanel with event watching
	 */
	public WatchEventPanel() {
		
		//watch this widgets events
		sinkEvents(Event.ONCLICK);
		
	}

	/**
	 * watch for particular browser events - sinkEvents(Events.[ONCLICK|etc])
	 */
    public void onBrowserEvent(Event event) {
        switch (DOM.eventGetType(event)) {
        	case Event.ONCLICK:
        		if (changeListeners != null) {
        			changeListeners.fireChange(this);
        		}
        		break;
        }
    }
    
	/**
	 * add change listener - watch for changes in this widget
	 * @param listener
	 */
	public void addChangeListener(ChangeListener listener) {
		if (changeListeners == null)
			changeListeners = new ChangeListenerCollection();
		changeListeners.add(listener);
	}

	/**
	 * remove change listener - remove watching for changes in this widget
	 * @param listener
	 */
	public void removeChangeListener(ChangeListener listener) {
		if (changeListeners != null)
			changeListeners.remove(listener);
	}

}
```

Another way to watch for events on a vertical panel using sinkEvents. In this class, I watch for Click using a changelistener. I observe mouse events using mouselistener. You can observe this verticalPanel widget via using the mouselistener and/or change listener. There are lots of different ways you can configure listeners.
```
/**
 * I use this panel for the drag handler for my composite widgets
 * dragHandler = where the mouse holds the widget while dragging
 * 
 * @author Brandon Donnelson
 * 
 * I extend the vertical panel - not a composite widget
 */
public class WatchEventPanel extends VerticalPanel implements SourcesMouseEvents {

	private MouseListenerCollection mouseListeners;
	private ChangeListenerCollection changeListeners;

	/**
	 * constructor - extending VerticalPanel with event watching
	 */
	public WatchEventPanel() {
		
		//watch this widget's mouse events
		sinkEvents(Event.MOUSEEVENTS);
	}

	/**
	 * watch for browser events - from sinkEvents([MouseEvents])
	 */
    public void onBrowserEvent(Event event) {

        switch (DOM.eventGetType(event)) {
        	case Event.ONMOUSEDOWN:
	        	if (mouseListeners != null) {
					mouseListeners.fireMouseEvent(this, event);
				}
	        	if (changeListeners != null) {
	        		changeListeners.fireChange(this);
	        	}
        		break;
        	case Event.ONMOUSEMOVE:
	        	if (mouseListeners != null) {
					mouseListeners.fireMouseEvent(this, event);
				}
        		break;
        	case Event.ONMOUSEOUT:
        		break;
        	case Event.ONMOUSEOVER:
        		break;
        	case Event.ONMOUSEUP:
	        	if (mouseListeners != null) {
					mouseListeners.fireMouseEvent(this, event);
				} 
        		break;
        	case Event.ONMOUSEWHEEL:
        		break;
        } 
    }
    
    /**
     * observer stuff
     */
	public void addMouseListener(MouseListener listener) {
		if (mouseListeners == null)
			mouseListeners = new MouseListenerCollection();
		mouseListeners.add(listener);
	}

	public void removeMouseListener(MouseListener listener) {
		if (mouseListeners != null)
			mouseListeners.remove(listener);
	} 

	/**
	 * add change listener - watch for changes in this widget
	 * @param listener
	 */
	public void addChangeListener(ChangeListener listener) {
		if (changeListeners == null)
			changeListeners = new ChangeListenerCollection();
		changeListeners.add(listener);
	}

	/**
	 * remove change listener - remove watching for changes in this widget
	 * @param listener
	 */
	public void removeChangeListener(ChangeListener listener) {
		if (changeListeners != null)
			changeListeners.remove(listener);
	}

}
```

# Parsing Query String #
> get/pass parameters from the http query string. I use the history token to pass paramters, which makes more sense, since you don't have to refresh the page. Here is my method I do it with.

```

/**
 * init history support, start watching for changes in history
 * 
 * observe history changes (tokens)
 */
public void initHistorySupport() {

	History.addHistoryListener(this);

	// check to see if there are any tokens passed at startup via the
	// browser’s URI
	String token = History.getToken();
	if (token.length() == 0) {
		onHistoryChanged("trainer");
	} else {
		onHistoryChanged(token);
	}
}


/**
 * parse history token ?[var=1&var2=2&var3=3]
 * 
 * Parse the history token like querystring - domain.tld#historyToken?params
 * 
 * @param historyToken
 * @return
 */
public static HashMap parseHistoryToken(String historyToken) {

	//skip if there is no question mark
	if (!historyToken.contains("?")) {
		return null;
	}
	
	// ? position
	int questionMarkIndex = historyToken.indexOf("?") + 1;
	
	//get the sub string of parameters var=1&var2=2&var3=3...
	String[] arStr = historyToken.substring(questionMarkIndex, historyToken.length()).split("&");

	HashMap params = new HashMap();
	for (int i = 0; i < arStr.length; i++) {
		
		String[] substr = arStr[i].split("=");
		
		params.put(substr[0], substr[1]);
		
		//debug
		System.out.println("param[" + i + "]=" + arStr[i]);
	}

	//debug
	System.out.println("map: " + params);

	return params;

}
```

# Google Analytics Track Anchor Tag #
> Moved to [project\_UrchinTracker](project_UrchinTracker.md) - Extending this information there.

# Round Percentage #
Round a double. Round a percentage. Round a decimal to places.
```
    private double roundDouble(double d, int places) {
        return Math.round(d * Math.pow(10, (double) places)) / Math.pow(10,
            (double) places);
    }

    private double exampleUse() {

       int count = 6;
       int days = 21;

        // calculate ratio
        double ratio = 0;
        if (count > 0) {
        	ratio = (double)count / (double)days ;
        } else {
        	ratio = 0;
        }
        
        // round
        ratio = roundDouble(ratio, 4);
        
        // make it a percentage
        ratio = ratio * 100;

        return ratio;
    }
```

# Slit String (CSV) #
> Split a comma separated string with double quotes encasing them.
```
String testSplit = "\"1:a,b,c,d\",\"2,e,f,g,h\",\"3,i,j,k,l,m\",\"4,n,o,p,q\"";
System.out.println("testSplit: " + testSplit);

String[] arSplit = testSplit.split("\",\"");

for (int i=0; i < arSplit.length; i++) {

	String s = arSplit[i]; 

	s = s.replaceFirst("^\"", ""); // get rid of the start quote if theres one
	s = s.replaceFirst("\"$", ""); // get rid of the end quote if theres one

	System.out.println(i + ". split: " + arSplit[i] + " Fixed:" + s);

}
```

# Remove HTML Tags #
> Strip out html tags that could be dangerous. Remove html tags before drawing to screen. May need to do something like this if the request comes from rpc for safety.
```
private String removeHtmlTags(String html) {
	String regex = "(<([^>]+)>)";
	html = html.replaceAll(regex, "");
	return html;
}
```

# Timer #
> Timer example. Show something delayed or how then hide it.
```
public void hideTimed() {
	pWidget.setVisible(true);
	
	Timer t = new Timer() {
		public void run() {
			pWidget.setVisible(false);
		}
	};
	t.schedule(3000);
}
```

# Url Encode #
> Encode url. Use this both on the client and server side.
```
private static String urlencode(String s) {
	
	String r = "";
	for (int i=0; i < s.length(); i++) {
		r += encodeChar(s.charAt(i));
	}
	
	return r;
}

private static String encodeChar(char c) {
	String cS = Character.toString(c);
	
	String s = "";
	if (cS.matches("[a-zA-Z0-9_.\\-~&=]") == false) {
		s = "%" + Byte.toString((byte) c);
	} else {
		s = cS;
	}
	
	return s;
}
```

# Servlet Information #
> Get servlet request GET/POST information. Get Requesting url, ip address, and other information that came in on the header request to the servlet.
```
//
# only use in [YourAppService]Impl 
HttpServletRequest request = getThreadLocalRequest();
String url = request.getRequestURL().toString();

# only use in [YourAppService]Impl 
private String getRequestUrlOAuth() {

	HttpServletRequest request = getThreadLocalRequest();
	String host = request.getRemoteHost();
	String path = request.getPathInfo();
	
	String url = "http://" + host + path;
	
	return url;		
}
```

# Send a Widget To Popup Window #
> This is what I do so i can print a widget. I send the widget.toString(); to popup window.
```
// Send html (widget.toString();) to popup window
public static native void test(String html) /*-{
  var win = $wnd.window.open("","PopUpWindow","menubar=yes,location=yes,resizable=yes,scrollbars=yes,status=yes");
  win.document.write(html);
}-*/;
```

# Get Browser Type #
> Get the type of browser using the application.
```
public static native String getUserAgent() /*-{ 
  var ua = navigator.userAgent.toLowerCase();
  
  if (ua.indexOf("opera") != -1) { 
          return "opera"; 
  } 
  if (ua.indexOf("webkit") != -1) { 
          return "safari"; 
  } 
  if ((ua.indexOf("msie 6.0") != -1) 
  ||  (ua.indexOf("msie 7.0") != -1)) { 
          return "ie6"; 
  } 
  if (ua.indexOf("gecko") != -1) { 
          var result = /rv:([0-9]+)\.([0-9]+)/.exec(ua); 
          if (result && result.length == 3) { 
                  var version = (parseInt(result[1]) * 10) + parseInt(result[2]); 
                  if (version >= 18) 
                          return "gecko1_8"; 
          } 
          return "gecko"; 
  } 
  return "unknown"; 
}-*/; 
```

# Including Another Project #
> When including another project to your project. Things to remember.
  * 1. Add project to build path. click on project > build path > projects > add (project)
  * 2. Add source code folder. click on project > build path > libraries > add class folder > add the projects class folder. !!!! this is important to do and the most frustrating to remember !!!
  * 3. Add project inherits in project.gwt.xml file. Edit file add >  

&lt;inherits name='com.gawkat.gwt.system.System' /&gt;


  * 4. Remove parents project entry point, you will not need two entry points. Think of it like this, both projects xml files will be parsed, and added together. So remove duplicate items.
  * 5. Check my example out gwt-Core Top level proejct and gwt-System, included project in gwt-Core.

# Paging #
> Paging widget. I use this when I want to page through a record set.
```
public class Page extends Composite implements ClickListener, ChangeListener {

  // observers
  private ChangeListenerCollection changeListeners;
  private int action = 0;

  // actions
  public final static int CHANGE_START = 1;
  public final static int CHANGE_LIMIT = 0;
  
  private VerticalPanel pWidget = new VerticalPanel();

  private HorizontalPanel pPrevPages = new HorizontalPanel();
  private HorizontalPanel pNextPages = new HorizontalPanel();
  private FlowPanel pOnPage = new FlowPanel();
  private FlowPanel pTotalPages = new FlowPanel();
  
  // buttons
  private PushButton bPrev = new PushButton("<");
  private PushButton bNext = new PushButton(">");
  private PushButton bStart = new PushButton();
  private PushButton bEnd = new PushButton();
  
  private ListBox lbLimit = new ListBox();
  
  private int total = 0;
  private int start = 0;
  private int limit = 15; // also set this in DrillDownData as default
  
  private int firstPage = 0;
  private int onPage = 0;
  private int lastPage = 0;
  private int totalPages = 0;
  
  /**
   * constructor - init widget
   */
  public Page() {
    bStart.setTitle("Goto to starting page");
    bPrev.setTitle("Previous page");
    bNext.setTitle("Next page");
    bEnd.setTitle("Goto last page");
    
    pOnPage.setTitle("Current page");
    pTotalPages.setTitle("Total pages");
    lbLimit.setTitle("Display this many records at a time in a page");
    
    HorizontalPanel hp = new HorizontalPanel();
    hp.add(bStart);
    hp.add(new HTML("&nbsp;"));
    hp.add(bPrev);
    hp.add(new HTML("&nbsp;"));
    hp.add(pPrevPages);
    hp.add(new HTML("&nbsp;"));
    hp.add(pOnPage);
    hp.add(new HTML("&nbsp;"));
    hp.add(pNextPages);
    hp.add(new HTML("&nbsp;"));
    hp.add(bNext);
    hp.add(new HTML("&nbsp;"));
    hp.add(bEnd);
    hp.add(new HTML("&nbsp;"));
    hp.add(pTotalPages);
    hp.add(new HTML("&nbsp;"));
    hp.add(lbLimit);
    
    pWidget.add(hp);

    initWidget(pWidget);

    pWidget.setStyleName("Page");

    // observe
    bPrev.addClickListener(this);
    bNext.addClickListener(this);
    bStart.addClickListener(this);
    bEnd.addClickListener(this);
    lbLimit.addChangeListener(this);
    
    // Style
    pWidget.setStyleName("100%");
    
    bStart.addStyleName("Page-Button");
    bPrev.addStyleName("Page-Button");
    bNext.addStyleName("Page-Button");
    bEnd.addStyleName("Page-Button");
    lbLimit.addStyleName("Page-LbLimit");

    drawLimitChoices();
  }
  
  public int getStart() {
    return start;
  }
  
  public int getLimit() {
    return limit;
  }

  public void setCounts(int total, int start, int limit) {
    this.total = total;
    this.start = start;
    this.limit = limit;
    
    // run page calculations
    setOnPage();
    setTotalPages();
    setFirstPage();
    setLastPage();
        
    displayPrev();
    displayNext();
    
    drawPrevPages();
    drawNextPages();
    getOnPage();
    drawTotalPages();
    
    setLimitAt();
  }
  
  private void setOnPage() {
    onPage = (int) (start / limit);
  }
  
  private void setTotalPages() {
    totalPages = (total / limit);
  }
  
  private void setFirstPage() {
    firstPage = 0;
    bStart.setText("1");
  }
  
  private void setLastPage() {
    lastPage = total - limit;
  }
  
  private void displayPrev() {
    if (onPage == 0) {
      bPrev.setEnabled(false);
      bStart.setEnabled(false);
    }else if (onPage > 0) {
      bPrev.setEnabled(true);
      bStart.setEnabled(true);
    }
  }
  
  private void displayNext() {
    if (onPage == lastPage) {
      bNext.setEnabled(false);
      bEnd.setEnabled(false);
    } else if (totalPages > 0) {
      bNext.setEnabled(true);
      bEnd.setEnabled(true);
    } else {
      bNext.setEnabled(false);
      bEnd.setEnabled(false);
    }
  }
  
  private void drawPrevPages() {
    pPrevPages.clear();

    for(int i = onPage - 2; i < onPage; i++) {
      if (i >= 0) {
        drawPageButton(pPrevPages, i);
      }
    }
  }

  private void drawNextPages() {
    pNextPages.clear();
    for(int i = onPage + 1; i < onPage + 3; i++) {
      if (i == lastPage + 1) {
        break;
      }
      if (totalPages > i) {
        drawPageButton(pNextPages, i);
      }
    }
  }
  
  private void drawPageButton(HorizontalPanel hp, int i) {
    int p = i+1;
    PageButton b = new PageButton(p);
    hp.add(b);
    b.addChangeListener(new ChangeListener() {
      public void onChange(Widget sender) {
        PageButton b = (PageButton) sender;
        setPage(b.getPage());
      }
    });
    b.addStyleName("Page-Button");
    b.setTitle("Goto Page " + p);
  }
  
  private void drawTotalPages() {
    String p = Integer.toString(totalPages);
    bEnd.setText(p);
  }

  private void getOnPage() {
    pOnPage.clear();
    String p = Integer.toString(onPage+1);
    pOnPage.add(new HTML("<b>" + p + "</b>"));
  }
  
  public int getAction() {
    return this.action;
  }
  
  private void setNext() {
    if ((start + limit > total)) {
      start = total - limit;
    } else {
      start += limit;
    }
  }
  
  private void setPrev() {
    if ((start - limit) < 0) {
      start = 0;
    } else {
      start -= limit;
    }
  }
  
  private void setStart() {
    start = 0;
  }
  
  private void setEnd() {
    start = total - limit;
  }

  private void setPage(int page) {
    start = (page-1) * limit;
    fire(CHANGE_START);
  }
  
  private void drawLimitChoices() {
    lbLimit.addItem("15");
    lbLimit.addItem("25");
    lbLimit.addItem("50");
    lbLimit.addItem("75");
    lbLimit.addItem("100");
  }
  
  private void setLimitAt() {
    int sel = 0;
    for (int i=0; i < lbLimit.getItemCount(); i++) {
      String slimit = Integer.toString(limit);
      String value = lbLimit.getItemText(i); 
      if (value.equals(slimit)) {
        sel = i;
        break;
      }
    }
    lbLimit.setSelectedIndex(sel);
  }
  
  private void setLimit() {
    int sel = lbLimit.getSelectedIndex();
    limit = Integer.parseInt(lbLimit.getItemText(sel));
    fire(CHANGE_LIMIT);
  }
  
  private void fire(int action) {
    this.action = action;
    if (changeListeners != null) {
      changeListeners.fireChange(this);
    }
  }

  public void addChangeListener(ChangeListener listener) {
    if (changeListeners == null)
      changeListeners = new ChangeListenerCollection();
    changeListeners.add(listener);
  }

  public void removeChangeListener(ChangeListener listener) {
    if (changeListeners != null)
      changeListeners.remove(listener);
  }

  // observe
  public void onClick(Widget sender) {
    if (sender == bPrev) {
      setPrev();
    } else if (sender == bNext) {
      setNext();
    } else if (sender == bStart) {
      setStart();
    } else if (sender == bEnd) {
      setEnd();
    }
    fire(CHANGE_START);
  }

  public void onChange(Widget sender) {
    if (sender == lbLimit) {
      setLimit();
    }
  }
  
}
```

> Paging Button
```
public class PageButton extends Composite implements ClickListener {

  private ChangeListenerCollection changeListeners = null;
  
  private int page = 0;
  
  public PageButton(int page) {
    this.page = page;
    String p = Integer.toString(page);
    PushButton b = new PushButton(p);
    initWidget(b);
    b.addClickListener(this);
    b.addStyleName("Page-Button");
  }
  
  public int getPage() {
    return this.page;
  }
  
  public void addChangeListener(ChangeListener listener) {
    if (changeListeners == null)
      changeListeners = new ChangeListenerCollection();
    changeListeners.add(listener);
  }

  public void removeChangeListener(ChangeListener listener) {
    if (changeListeners != null)
      changeListeners.remove(listener);
  }

  public void onClick(Widget sender) {
   if (changeListeners != null) {
     changeListeners.fireChange(this);
   }
  }
}
```


&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;
