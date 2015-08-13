
&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" scrolling=0 /&gt;

# Reference #
> My goal is to add buttons dynamically integrated into GWT so I can add the states, instead of pages into the url link. I doing something like this on my http://c.gawkat.com

  * [Demo](http://demogwtshare.appspot.com) - demo google buzz and facebook share buttons.
  * [Source](http://code.google.com/p/gwt-examples/source/browse/trunk/DemoGwtShare/src/org/gonevertical/share/client/Share.java?r=2204) - see the source

## Javascript Include ##

Google buzz javascript
```
private void setupBuzzScript() {
    Document doc = Document.get();
    ScriptElement script = doc.createScriptElement();
    script.setSrc("http://www.google.com/buzz/api/button.js");
    script.setType("text/javascript");
    script.setLang("javascript");
    doc.getBody().appendChild(script);
  }
  
```

Google Buzz Html
```
  String s = "<a " +
    "href=\"http://www.google.com/buzz/post\" " +
    "class=\"google-buzz-button\" " +
    "title=\"Check out this GWT Share Demo\" " +
    "data-message=\"I made this to test out some sharing buttons.\" " +
    "data-url=\"http://demogwtshare.appspot.com\" " +
    "data-imageurl=\"http://demogwtshare.appspot.com/images/preview.jpg\" " +
    "data-locale=\"en\" " +
    "data-button-style=\"normal-count\"></a>";
    
    getHBuzz().setHTML(s);
```

## Twitter Include ##
> Here is how I dynamically add the twitter javascript into the gwt app.

```
private void drawTwitter() {
    //<a href="http://twitter.com/Gawkat" class="twitter-follow-button" data-show-count="false">Follow @Gawkat</a>
    //<script src="http://platform.twitter.com/widgets.js" type="text/javascript"></script>
    
    String s = "<a href=\"http://twitter.com/Gawkat\" class=\"twitter-follow-button\" data-show-count=\"false\">Follow @Gawkat</a>";
    HTML h = new HTML(s);
    fpTwitter.add(h);
    
    Document doc = Document.get();
    ScriptElement script = doc.createScriptElement();
    script.setSrc("http://platform.twitter.com/widgets.js");
    script.setType("text/javascript");
    script.setLang("javascript");
    doc.getBody().appendChild(script);
  }
```

## Google Plus One +1 ##
> Simple way to add +1 to your GWT App. Include the script tag into the java code like this:
```
private void drawPlusOne() {
    //<!-- Place this tag in your head or just before your close body tag -->
    //<script type="text/javascript" src="https://apis.google.com/js/plusone.js"></script>
    //<!-- Place this tag where you want the +1 button to render -->
    //<g:plusone></g:plusone>
   
    String s = "<g:plusone href=\"http://gawkat.com\"></g:plusone>";
    HTML h = new HTML(s);
    fpPlusOne.add(h);
    
    Document doc = Document.get();
    ScriptElement script = doc.createScriptElement();
    script.setSrc("https://apis.google.com/js/plusone.js");
    script.setType("text/javascript");
    script.setLang("javascript");
    doc.getBody().appendChild(script);
  }
```

## Gadget XML ##
> I can't get this to work as of yet. But here is gadget include. Try 1.

  * [Source](http://code.google.com/p/gwt-examples/source/browse/trunk/DemoGadgetXml/war/gadget-gwt-examples-sharethis.xml) - gadget xml source. (doesn't work yet)

&lt;wiki:gadget url="http://gwt-examples.googlecode.com/svn-history/trunk/DemoGadgetXml/war/gadget-gwt-examples-sharethis.xml" border="0"  width="600" height="300" /&gt;