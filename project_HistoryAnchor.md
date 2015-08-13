
&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;

# History Token / Anchor Tag Navigation #
> Navigating with anchor tags is very cool and a must in any application. You can also add parameters to your anchor tag which makes it super flexible to navigate with out a page refresh/reload.
  * HistoryToken = Anchor Tag - so we can stay on the same page. like domain.tld#anchorTag?var1=a&var2=b...
  * Find the entire project in source code.
  * This also deploys integrated Google Analytics

> ## My Suggestion to GWT Project ##
> I suggested an enhancement. Vote if you want something like this natively. Comment on it. [GWT Issue 2422](http://code.google.com/p/google-web-toolkit/issues/detail?id=2422)

> ## History Token Method Snippets ##
> Navigation using history tokens / anchor tags. Be able to use the back button, forward button with out problems. Send in parameters with the anchor tag.
  * use History.newItem("home"); //to fire a history change

> Init history support
```
/**
 * init history support, start watching for changes in history
 * 
 * use historyTokens (anchor tags) to navigate
 * 
 * observe history changes (tokens)
 */
private void initHistorySupport() {

	History.addHistoryListener(this);

	// check to see if there are any tokens passed at startup via the anchor tag
	String token = History.getToken();
	if (token.length() == 0) {
		
		// navigate with anchors to home
		History.newItem("home");
		
	} else {
		
		onHistoryChanged(token);
	}
}
```

> Parse history token / anchor tag for parameters
```
/**
 * parse the historyToken
 *  
 * like domaint.tld#anchor?[var=1&var3=2&var3=3]
 * 
 * @param historyToken anchor tag
 */
private String parseHistoryToken(String historyToken) {
	
	if (historyToken == null) {
		return "";
	}
	
	//get parameters from history token
	if (historyToken.contains("?")) {
		HashMap params = getHistoryTokenParameters(historyToken);

		//use the parameters
		setParams(params);

		//get just the history token / anchor tag , not with paramenters
		historyToken = getHistoryToken(historyToken);
	} 

	return historyToken;
}
```

> Get history parameters in an anchor tag
```
/**
 * get historyToken parameters
 * 
 * like domaint.tld#anchor?[var=1&var3=2&var3=3]
 * 
 * @param historyToken anchor tag
 * @return hashmap of the parameters
 */
private static HashMap getHistoryTokenParameters(String historyToken) {

	//skip if there is no question mark
	if (!historyToken.contains("?")) {
		return null;
	}
	
	//debug
	//System.out.println("parse historyToken: " + historyToken);
	
	// ? position
	int questionMarkIndex = historyToken.indexOf("?") + 1;
	
	//get the sub string of parameters var=1&var2=2&var3=3...
	String[] arStr = historyToken.substring(questionMarkIndex, historyToken.length()).split("&");
	HashMap params = new HashMap();
	for (int i = 0; i < arStr.length; i++) {
		String[] substr = arStr[i].split("=");
		params.put(substr[0], substr[1]);
	}

	//debug
	//System.out.println("map: " + params);

	return params;
}
```

> Get historyToken when looking at it with parameters.
```
/**
 * get historyToken by itself
 * 
 * like domain.tld#[historyToken]?params=1
 *  
 * @param historyToken
 * @return
 */
private String getHistoryToken(String historyToken) {
	
	//skip if there is no question mark
	if (!historyToken.contains("?")) {
		return "";
	}

	//get just the historyToken/anchor tag
	String[] arStr = historyToken.split("\\?");
	historyToken = arStr[0];

	return historyToken;
}
```

> Are there params in the historyToken?
```
/**
 * are there params in historyToken
 * 
 * @return
 */
private boolean isParamsInHistoryToken() {
	String s = History.getToken();
	
	if (s.contains("?")) {
		return true;
	} else {
		return false;
	}
}
```

> Use the paramters collected from the historyToken / anchor tag
```
/**
 * use the parameters
 * @param params
 */
private void setParams(HashMap params) {
	
	if (params == null) {
		return;
	}
		
	if (params.get("id") != null) {
		this.id = (String) params.get("id");
	}
	
	if (params.get("add") != null) {
		this.add  = (String) params.get("add");
	}
}
```

&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;
