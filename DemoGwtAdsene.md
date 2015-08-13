
&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" scrolling=0 /&gt;

# Plan B Due to No Ads Iframing #
> Since I can't advertise Google in iframe, I setup Affiliate Banner Rotator. [More about that here](DemoAdvertisingAffiliate.md)

# How I Integrated Adsense #
My goal is to integrate Adsense into Java code so I coud draw or render the ad into the area I would like it and when the application state changes like a page loads or reloads I would like to redraw or refresh the ad like a page would.

# Reference #
  * [Demo](http://demogwtads.appspot.com) - GAE Demo
  * [Source](http://code.google.com/p/gwt-examples/source/browse/trunk/DemoGwtAdsense/src/org/gonevertica/adsense/client/Layout.java) - See the Java source

## IFrame Method ##
> Its easy to do a iframe method for adsense. I load up the page with the script in it and it works fine.
  * [Source](http://code.google.com/p/gwt-examples/source/browse/trunk/DemoGwtAdsense/src/org/gonevertica/adsense/client/AdsWidget.java) - My Iframe ad widget
  * [Gawkat.com](http://c.gawkat.com) - I use it here too
## Script Include ##
> I can't seem to get this to work yet b/c ad will erase the page and put it self on it. Google teams need to make for better Adsense integration for Applications!

> Below works although has bad side effect.
```
  public static native void setupAdsenseVars() /*-{
      $wnd.google_ad_client = "ca-pub-0032065764310410";
      $wnd.google_ad_slot = "4089409905";
      $wnd.google_ad_width = 728;
      $wnd.google_ad_height = 90;
  }-*/;

  private void setupAdsenseScript() {
    Document doc = Document.get();
    ScriptElement script = doc.createScriptElement();
    script.setSrc("http://pagead2.googlesyndication.com/pagead/show_ads.js");
    script.setType("text/javascript");
    script.setLang("javascript");
    vpAdsenseScript.getElement().appendChild(script);
  }
```