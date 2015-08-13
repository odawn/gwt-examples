
&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" scrolling=0 /&gt;

# Demo GWT GData Using OAuth #
> The goal is to get access to my documents with my GWT app. The hardest part is the authentication part, but its not as bad as it looks. Its a bit easier than I thought.

## OAuth Authentication (Best!) ##
> This time I setup a demo using GData OAuth authentication which was tough at first, because I didn't understand the 3 legged process. Although after finally figuring it out, its quite simple to use. This example demo, shows how the 3 legged process works, saving a token to the JDO data store for use later. I left off checking for a previous token, so it can be tried over and over. I'm impressed how easily the OAuth process actually is now and I am excited to work with it on my larger projects. I didn't sign this demo, so it could look suspicious. I don't do any data collecting other than your google id and authorized token. I added the use of multiple scopes.

  * [Demo](http://demogwtgdataoauth.appspot.com) - My OAuth Demo, and works to get your blogger list, if you have some blogs.
  * [Source](http://code.google.com/p/gwt-examples/source/browse/#svn%2Ftrunk%2FDemoGWTGDataOAuth%2Fsrc%2Forg%2Fgonevertical%2Fdemo%253Fstate%253Dclosed) - See the project source here.
  * [OAuth Servlet](http://code.google.com/p/gwt-examples/source/browse/trunk/DemoGWTGDataOAuth/src/org/gonevertical/demo/server/servlet/Servlet_AskForAccess.java) - servlet that takes care of the oauth

> ### Important Note - Register Application ###
> This is important, to get a application consumer key and secret, go here: https://www.google.com/accounts/ManageDomains

> ### OAuth Popup Window ###
> To setup the token to Google, I use a popup window and servlet to do the 3 steps.
  * [Source](http://code.google.com/p/gwt-examples/source/browse/trunk/DemoGWTGDataOAuth/src/org/gonevertical/demo/server/servlet/Servlet_AskForAccess.java) - The OAuth Servlet

> ### OAuth Playground ###
  * http://googlecodesamples.com/oauth_playground/ - OAuth playground, sniff to see whats going on.

> ### Step by Step ###
> More instructions made by others
  * [Setting up an OAuth provider on Google App Engine](http://ikaisays.com/2011/05/26/setting-up-an-oauth-provider-on-google-app-engine/) - Very simple instructions made by 'Guru' Ikai.

## GData SpreadSheet Example Use ##
> This is how I use the oauth token once I set it up.

```
public void test() {

    String scope = "https://spreadsheets.google.com/feeds/";
    AppTokenJdo appToken = AppTokenStore.getToken(scope);

    String consumerKey = sp.getOAuthConsumerKey();
    String consumerSecret = sp.getOAuthConsumerSecret();

    String token = appToken.getAccessTokenKey();
    String tokenSecret = appToken.getAccessTokenSecret();

    GoogleOAuthParameters oauthParameters = new GoogleOAuthParameters();
    oauthParameters.setOAuthConsumerKey(consumerKey);
    oauthParameters.setOAuthConsumerSecret(consumerSecret);
    oauthParameters.setOAuthToken(token);
    oauthParameters.setOAuthTokenSecret(tokenSecret);

    //log.info("Token " + token + " TokenSecret: " + tokenSecret + " ConsumerKey: " + consumerKey + " ConsumerSecret: " + consumerSecret);

    SpreadsheetService service = new SpreadsheetService("Gone_Vertical_LLC-CoreSystem_v1");
    try {
      service.setOAuthCredentials(oauthParameters, new OAuthHmacSha1Signer());
    } catch (OAuthException e) {
      e.printStackTrace();
    }

    URL url = null;
    try {                    
      url = new URL("https://spreadsheets.google.com/feeds/spreadsheets/private/full");
    } catch (MalformedURLException e1) {
      e1.printStackTrace();
    }
    SpreadsheetFeed feed = null;
    try {
      feed = service.getFeed(url, SpreadsheetFeed.class);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (ServiceException e) {
      e.printStackTrace();
    }
    List<SpreadsheetEntry> spreadsheets = feed.getEntries();
    for (int i = 0; i < spreadsheets.size(); i++) {
      SpreadsheetEntry entry = spreadsheets.get(i);
      //System.out.println("\t" + entry.getTitle().getPlainText());
    }

  }
```



## AuthSub Authentication ##
> Although I thought this would work, oauth is much better.
  * [Demo](http://demogwtgdata.appspot.com/) - Demo authentication process and use. currently using authsub.
  * [Source](http://code.google.com/p/gwt-examples/source/browse/trunk/DemoGwtGdata/src/org/gonevertical/gdata/server/servlet/FetcherServlet.java) - Find the source here.


&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;
