
&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;

# Google Analytics Urchin Tracker Examples #
> Integrating google analytics ga.js into the GWT application is very easy and shown in my new example. I also have an example of it working on Google app engine.

# New - GWT 2.03 + GAE #
> Integrated GWT and Google Analytics example
  * [Source](http://code.google.com/p/gwt-examples/source/browse/trunk/DemoGoogleAnalytics/src/org/gonevertical/demo/client/Track.java) - Project source
  * [Demo](http://demogwtanalytics.appspot.com/) - Demo of the application on the Google App Engine

```
public static native void trackGoogleAnalytics(String historyToken) /*-{
      
  try {
  	
  	// setup tracking object with account
  	var pageTracker = $wnd._gat._getTracker("UA-2862268-12"); // change account please!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
  	
  	pageTracker._setRemoteServerMode();
  	
  	// turn on anchor observing
  	pageTracker._setAllowAnchor(true)
  	
  	// send event to google server
  	pageTracker._trackPageview(historyToken);
  	    
  } catch(err) {
    
    // debug
    alert('FAILURE: to send in event to google analytics: ' + err);
  }

}-*/;
```

# Older Example #
> I found that using JSNI, accessing the native javascript from the java methods was the best way to set off get request to Google Analytics. I also found that you can specifically track anchor tags, which is the main staple for my ajax applications. In that, I also use domain.tld#myAnchor?param1=a&parm2=b&param3=c for sending in parameters with out page refresh. You can find in the source the entire example.
  * See Entire Project is in source
  * Reference Configuration Data - http://www.google.com/analytics/InstallingGATrackingCode.pdf
  * Track anchor tag - notice var below

> In UrchinTracker.gwt.xml file you want to include urchin.js file
```
#UrchinTracker.gwt.xml file
<module>
  	<!-- google analytics js include -->
  	<!--  I can get this script to work - it was easier to read the code -->
  	<!-- download the script from here, or google analytics. -->
  	<script src="urchin.js"/>
  	
	<!-- Inherit the core Web Toolkit stuff. -->
	<inherits name='com.google.gwt.user.User'/>
	<!-- Specify the app entry point class. -->
	<entry-point class='com.tribling.gwt.test.urchintracker.client.UrchinTracker'/>
</module>

#JSNI method used to send a tracking request to google analytics
/**
 * Send tracking to google analytics
 * 
 * http://www.google.com/analytics/InstallingGATrackingCode.pdf
 * 
 * This works great using the urchin.js for the JSNI
 * You can see how things can work here, and run with it somewhere else
 * 
 * Verify your get requests in firefox to Google Analytics with tamper data
 */
private static native void track(String s) /*-{

	// urchin.js included in UrchinTracker.gwt.xml
	// so far this works - cool - include the urchin.js in the xml file
	$wnd._uacct = "UA-2862268-12";
	$wnd._uanchor = 1;
	$wnd.urchinTracker("/UrchinTracker/" + s); // I had to have pages after the first request
}-*/;
```

> ### Problem - Only Loads OnLoad ###
> All though I wanted to track this way, it will only track the page loading. Below I show a better way of doing it.
```
<!-- web statistics -->
<script type="text/javascript">
	var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
	document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
	var pageTracker = _gat._getTracker("UA-xxxxxxxx-13");

	// *** track anchor tags ***
	pageTracker._setAllowAnchor(true);

	pageTracker._initData();
	pageTracker._trackPageview();
</script>
```

> # Anchor Tag Parameters #
> I love this little method b/c it makes life much simpler in that you can have parameters with out a page reload in the querystring using the anchor tag.
  * More Advanced History Version - [project\_HistoryAnchor](project_HistoryAnchor.md) - I improved the anchor methods

> You can Parse the querystring anchor tag like - domain.tld#historyToken?params=1&param2=b&param3=abc and get the parameters
```
/**
 * parse history token ?[var=1&var2=2&var3=3]
 * 
 * Parse the history token like querystring - domain.tld#historyToken?params=1&param2=b&param3=abc
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
		//System.out.println("param[" + i + "]=" + arStr[i]);
	}

	//debug
	//System.out.println("map: " + params);

	return params;
}
```


&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;
