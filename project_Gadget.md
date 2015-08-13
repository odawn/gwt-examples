
&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" scrolling=0/&gt;



# Project GWT Gadget #
> There are two ways you can get a GWT gadget to IGoogle, one through the GWT gadgets API, which is better. The second is to include a standalone module.

  * [Source of the example on this page](http://code.google.com/p/gwt-examples/source/browse/trunk/DemoGadgetXml/war/gadget-gwt-examples-ads.xml) - Advertising source, stored in the subversion source repository

## GWT Gadgets ##
> There is some serious develop going on in the source code. Not sure when the next release is.
  * [Google GWT Gadget Reference](http://code.google.com/p/gwt-google-apis/wiki/GadgetsGettingStarted) - GWT Gadgets Api
  * [Docs For Native Javascript](http://code.google.com/apis/wave/extensions/gadgets/guide.html) - Native Google Gadget JS Docs
  * [Module Preference](http://code.google.com/apis/gadgets/docs/reference.html) - Documentation
  * http://www.opensocial.org/ - The specs start here

## Build Notes ##
> Few things I learned along the way.
  1. RootPanel.get().add(widget); - don't use a elementId to place content/app on page
  1. Try a basic gadget first before you do a big one.
  1. I combined my bigger project with my gadget project, instead of writing an entire gadget.
  1. Test it out in the hosted debugger before merging into gadget init()
  1. Javascript native calls, don't forget to include those modules as well.
  1. Try the compile first before uploading it to Google App Engine
  1. Include Style in the project.gadget.xml module (build reference)
  1. Testing gadgets in this wiki get cached, so expect some time before the new gadget that you upload may not change at first.

## Module Prefs ##
  * title
  * description
  * author
  * author\_email
  * screenshot  (suggested)
  * author\_location  (suggested)
  * author\_affiliation  (suggested)
  * title\_url  (suggested)
  * directory\_title   (required if title contains user prefs)

## Google Docs Spreadsheet Gadget ##
> My goal was to figure out the range selection and how to integrate with Google docs spreadsheet. This gadget works in spreadsheet and you can select range.
  * [Spreadsheet Gadget Source](http://code.google.com/p/gwt-examples/source/browse/trunk/DemoGwtGadgetSpreadsheet/src/org/gonevertical/demo/client/SpreadsheetGadget.java) - Very simple gadget spreadsheet source.
  * [Preferences for Gadget Source](http://code.google.com/p/gwt-examples/source/browse/trunk/DemoGwtGadgetSpreadsheet/src/org/gonevertical/demo/client/SpreadsheetGadgetPreferences.java) - define the range preference here

# Examples / Demos #

## Google Code Project Wiki Advertising ##
> Advertising in a Google project wiki advertising is quite simple. Check out the gadget xml file. All you'll need to make is a simple xml file with the advertising javascript code.

> This is how you add it to the wiki.
```
<wiki:gadget url="http://gwt-examples.googlecode.com/svn/trunk/DemoGadgetXml/war/gadget-gwt-examples-ads.xml" height="85" width="480" border="0" />
```
> &lt;wiki:gadget url="http://gwt-examples.googlecode.com/svn/trunk/DemoGadgetXml/war/gadget-gwt-examples-ads.xml" height="85" width="480" border="0" /&gt;

## Example GWT Gadget ##
> This is my first gwt gadget, compiled using GWT. This is a fantastic way to get the widget built.

  * [Source](http://code.google.com/p/gwt-examples/source/browse/#svn/trunk/DemoGwtGadget/src/org/gonevertical/demo/client) - this is a very basic simple example to make sure it works, and it does.
```
wiki:gadget url="http://demogwtgadget.appspot.com/demogwtgadget/org.gonevertical.demo.client.DemoGwtGadget.gadget.xml" height="100" width="400" border="1" />
```
> &lt;wiki:gadget url="http://demogwtgadget.appspot.com/demogwtgadget/org.gonevertical.demo.client.DemoGwtGadget.gadget.xml" height="100" width="400" border="1" /&gt;


&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="96" width="740" border="0" scrolling=0/&gt;
## Math Flash Card Application ##
> I'm trying to use my mathflashcard. I built a separate project because I couldn't get the RPC to work. I haven't figured out what was failing, but Google App Engine logs where saying that access was restricted and was vague. So I decided to divide and conquer the issue and try only client side code first. Here is my first attempt to bring a bigger gadget onboard.
  * [Source](http://code.google.com/p/gwt-examples/source/browse/#svn/trunk/DemoGadgetMathFlashCard/src/com/gawkat/flashcard/client) - Rpc and JDO have been taken out in this source to try client side stuff only.
> <a href='http://www.google.com/ig/adde?moduleurl=http://demogadgetmathflashcard.appspot.com/flashcard/com.gawkat.flashcard.client.FlashCard.gadget.xml'><img src='http://buttons.googlesyndication.com/fusion/add.gif' alt='Add to iGoogle' /></a>
> [IGoogle Directory](http://www.google.com/ig/directory?url=demogadgetmathflashcard.appspot.com/flashcard/com.gawkat.flashcard.client.FlashCard.gadget.xml) - Listed in the IGoogle Directory
```
<wiki:gadget url="http://demogadgetmathflashcard.appspot.com/flashcard/com.gawkat.flashcard.client.FlashCard.gadget.xml" height="200" width="200" border="1" />
```
> &lt;wiki:gadget url="http://demogadgetmathflashcard.appspot.com/flashcard/com.gawkat.flashcard.client.FlashCard.gadget.xml" height="200" width="200" border="1" /&gt;



# Time Stamp Test And Convert #
> Test or convert your Javascript or Unix timestamp.
```
<wiki:gadget url="http://demogwtdatetimegadget.appspot.com/demogwtdatetime_gadget/org.gonevertical.demo.client.DemoGwtDateTime_Gadget.gadget.xml" height="400" width="400" border="1" />
```
&lt;wiki:gadget url="http://demogwtdatetimegadget.appspot.com/demogwtdatetime\_gadget/org.gonevertical.demo.client.DemoGwtDateTime\_Gadget.gadget.xml" height="400" width="400" border="1" /&gt;

# Trying Other Ways #
> These ways below haven't worked.

## Try 1 Example of Math Flash Cards Appication (not working yet) ##
```
 <wiki:gadget url="http://mathflashcard.appspot.com/gadget-gwt-math-flash-card.xml" height="400" width="400" border="1" />
```

## Try 2 ##
> when trying to add the js in, it throughs a frame access error in the console.
```
<wiki:gadget url="http://demogwtjsni.appspot.com/gadget-gwt-tooltip.xml" height="100" width="400" border="1" />
```

&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;
