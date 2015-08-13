
&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;

# Table of Contents #


# Indexing and your Application #
> My application entirely exists in javascript therefore a search engine spider hasn't been able to figure out where the content is. So here what I have been doing to get my content indexed so it's useful to other people.

**Here is my entirely all GWT application that I have been applying my techniques to: http://GoneVertical.com**

# Keywords and Description #
> I use Google web master tools to verify all the things are working. https://www.google.com/webmasters/tools/
  * I make sure I have a complete sentence in meta tags description.

# Content for the Spiders #
> As soon as my page loads, my GWT application loads, that is with a javascript enabled client. Therefore I have to use hidden content for Google's spider to see. Check out my home page source on http://GoneVertical.com to see what I did.

# Content Redirection #
> I direct spiders to my content from my homepage. If a user comes, the javascript will load how I want the user so see the content. Since a spider follows links, it will follow my links hidden in white font. The spider will index the content, so when a user clicks on that, the page redirects to the gwt location that has that content. I think this system could get abused and therefore, may not last forever. But used in the proper context, it should work good for getting the spiders to the proper places in your application for good indexing, helping people find what they want when they want.

  * http://gonevertical.com/info/howitworks.html - this is an example I use to get a search engine spider, but redirects to the GWT Application location of the same content.

> ## GWT Application Content Inclusion ##
> This is how I include my external pages, html pages, or other content into my GWT application system. This is like a php include or an Apache include. And one of my favorite tricks.

> This allows me to show my content to spiders and use it in my application environment. This has been the most efficient way for me to edit and present my contextual content quickly.
```
// This is my class to including other page content into my application
public class HowItWorks extends Composite {

	private VerticalPanel pWidget = new VerticalPanel();
	
	/**
	 * contructor - init page
	 */
	public HowItWorks() {
	
		Widget w = new HTMLInclude("info/howitworks.html"); 
		pWidget.add(w);
		
		initWidget(pWidget);
	}
}

public class HTMLInclude extends HTML {

  public HTMLInclude(final String url) {

    super();

    final HTMLInclude widget = this;

    try {
      new RequestBuilder(RequestBuilder.GET, url).sendRequest("",
          new RequestCallback() {

            public void onError(Request request, Throwable exception) {
              GWT.log("HTMLInclude: error fetching " + url, exception);
            }

            public void onResponseReceived(Request request, Response response) {
              if (response.getStatusCode() == 200) {
                widget.setHTML(response.getText());
              } else {
                GWT.log("HTMLInclude: bad code when fetching " + url + "["
                    + response.getStatusCode() + "]", null);
              }
            }

          });
    } catch (RequestException e) {
      GWT.log("HTMLInclude: exception thrown fetching " + url, e);
    }

  }
}
```

> This is what I put on the the page the spider sees. If a javascript client loads the page, the user will get redirected back to the application, where this content is stored.
  * I keep my content in > gwt\_project/public/info/content.html
```
<!-- This would be the html page that has content -->

page html content goes here.

<script>
	// send non web spiders back to gwt version of this same content
        // Link according to your application folder mine was in domain.tld/info/page.html
	window.location = "../#HowItWorks"
</script>

```

# Hard to Explain #
> I hoped that help gives an example to get spiders to your site content, while keeping your GWT application model intact. This has been very effective to me and I hope this can give others little tidbits to grow there application indexing with the power of keeping there application fully web 2.0.

&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;
