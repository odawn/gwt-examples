
&lt;wiki:gadget url="http://gwt-examples.googlecode.com/svn/trunk/DemoGadgetXml/war/gadget-aff.xml" height="100" width="740" border="0" /&gt;



# Demo Activities and Places #
> https://mywalletinventory.appspot.com/ - Demo

## Working Application ##
  * [Wallet Inventory App](https://mywalletinventory.appspot.com) - App Demo (keep your phone numbers incase it gets stolen)

## Application Source ##
  * [Main App Source](http://code.google.com/p/gwt-examples/source/browse/#svn%2Ftrunk%2FWalletInventory) - Wallet Inventory App source
  * [Included Module Source](http://code.google.com/p/gwt-examples/source/browse/#svn%2Ftrunk%2FGoneVertical-Core%2Fsrc%2Forg%2Fgonevertical%2Fcore%2Fclient) - Included module source, with the WiseTextBox and a couple other core widgets

## Reference ##
  * [Activites & Places Dev Guide](http://code.google.com/webtoolkit/doc/latest/DevGuideMvpActivitiesAndPlaces.html) - GWT docs

## Other Developer Demos ##
  * http://www.mikemitterer.at/infopoint/programmierung/gwt-requestfactory/ - Nice request factory demo here

# GWT Features Used #
> I think I've used some fun features in this project.

## Activities ##
  * [Activities Source](http://code.google.com/p/gwt-examples/source/browse/#svn%2Ftrunk%2FWalletInventory%2Fsrc%2Fcom%2Fgonevertical%2Fclient%2Fapp%2Factivity)

## Places ##
  * [Places Source](http://code.google.com/p/gwt-examples/source/browse/#svn%2Ftrunk%2FWalletInventory%2Fsrc%2Fcom%2Fgonevertical%2Fclient%2Fapp%2Factivity%2Fplaces)
  * [WalletEditPlace.java Source](http://code.google.com/p/gwt-examples/source/browse/trunk/WalletInventory/src/com/gonevertical/client/app/activity/places/WalletEditPlace.java#20) - Edit Place with url tokenizer
  * [ClientFactoryImpl Source](http://code.google.com/p/gwt-examples/source/browse/trunk/WalletInventory/src/com/gonevertical/client/app/ClientFactoryImpl.java#153) - use the request factory in your tokenizers!!!

## `RequestFactory` ##
  * [RequestFactory Source](http://code.google.com/p/gwt-examples/source/browse/#svn%2Ftrunk%2FWalletInventory%2Fsrc%2Fcom%2Fgonevertical%2Fclient%2Fapp%2Frequestfactory)

## Client Factory ##
  * [ClientFactory.java Source](http://code.google.com/p/gwt-examples/source/browse/trunk/WalletInventory/src/com/gonevertical/client/app/ClientFactory.java) - Interface
  * [ClientFactoryImpl.java Source](http://code.google.com/p/gwt-examples/source/browse/trunk/WalletInventory/src/com/gonevertical/client/app/ClientFactoryImpl.java) - Implementation using deferred binding.
  * [WalletInventory.gwt.xml Source](http://code.google.com/p/gwt-examples/source/browse/trunk/WalletInventory/src/com/gonevertical/WalletInventory.gwt.xml) - Deferred binding done in the project configuration xml file

## Google Account ##
  * [SignInViewImpl](http://code.google.com/p/gwt-examples/source/browse/trunk/WalletInventory/src/com/gonevertical/client/views/impl/SignInViewImpl.java#114) - Google Account UserData Use
  * [LoginWidget.java Source](http://code.google.com/p/gwt-examples/source/browse/trunk/WalletInventory/src/com/gonevertical/client/views/widgets/LoginWidget.java) - LoginWidget on the top right of the app
  * [UserData.java Source](http://code.google.com/p/gwt-examples/source/browse/trunk/WalletInventory/src/com/gonevertical/server/jdo/UserData.java) - Using Google User as the auth and tracking of the owner, whos who

## Custom Event ##
  * [Login Events](http://code.google.com/p/gwt-examples/source/browse/trunk/WalletInventory/#WalletInventory%2Fsrc%2Fcom%2Fgonevertical%2Fclient%2Fapp%2Fuser) - Global custom events used for login

## App Engine SSL Hosting ##
  * [web.xml Source](http://code.google.com/p/gwt-examples/source/browse/trunk/WalletInventory/war/WEB-INF/web.xml#8) - Servlet SSL configuration for App Engine

## 100% Width & Height UI ##
  * [style.css Source](http://code.google.com/p/gwt-examples/source/browse/trunk/WalletInventory/src/com/gonevertical/public/css/style.css) - bootstrap style overriding clean module style
  * [WalletInventory.css Source](http://code.google.com/p/gwt-examples/source/browse/trunk/WalletInventory/war/WalletInventory.css) - the main stylesheet

## Module Include ##
  * [GoneVertical-Core Project Source](http://code.google.com/p/gwt-examples/source/browse/#svn%2Ftrunk%2FGoneVertical-Core%2Fsrc%2Forg%2Fgonevertical%2Fcore%2Fclient%2Finput) - Core source code for modules that can cross project boundaries. Add this project to the build path and then add the module to the config file.
  * [WalletInventory.gwt.xml Source](http://code.google.com/p/gwt-examples/source/browse/trunk/WalletInventory/src/com/gonevertical/WalletInventory.gwt.xml) - use a independent decoupled module for core widgets that cross projects

## Google Web Fonts ##
  * [WalletInventory.css Source](http://code.google.com/p/gwt-examples/source/browse/trunk/WalletInventory/war/WalletInventory.css#19) - Import Google Fonts

## Adsense ##
  * [WalletInventory.html Source](http://code.google.com/p/gwt-examples/source/browse/trunk/WalletInventory/war/WalletInventory.html) - adsense use
  * [Layout.java Source](http://code.google.com/p/gwt-examples/source/browse/trunk/WalletInventory/src/com/gonevertical/client/layout/Layout.java) - move the ads to better location, look for moveAdsDivTimed();

## Javascript Injection Google +1 & Facebook Like ##
  * [More Info](demogwtshare.md) - See DemoGWTShare wiki page for more info
  * [Layout.java Source](http://code.google.com/p/gwt-examples/source/browse/trunk/WalletInventory/src/com/gonevertical/client/layout/Layout.java#85) - Google Plus One +1 script injection and use
  * [Layout.java Source](http://code.google.com/p/gwt-examples/source/browse/trunk/WalletInventory/src/com/gonevertical/client/layout/Layout.java#102) - Facebook like button

## App Engine JDO ##
  * [WalletData Source](http://code.google.com/p/gwt-examples/source/browse/trunk/WalletInventory/src/com/gonevertical/server/jdo/WalletData.java) - JDO owned relationships with key serialization and key web safe transport.

## Image Bundling ##
  * [UiImages.java Source](http://code.google.com/p/gwt-examples/source/browse/trunk/GoneVertical-Core/src/org/gonevertical/core/client/images/UiImages.java) - Bundled Images in the included module
  * [LoadingWidget.java Source](http://code.google.com/p/gwt-examples/source/browse/trunk/GoneVertical-Core/src/org/gonevertical/core/client/loading/LoadingWidget.java#21) - One of the places I use the image resource

## Google Analytics ##
  * TODO

## Growing TextBox ##
  * [WiseTextBox.java Source](http://code.google.com/p/gwt-examples/source/browse/trunk/GoneVertical-Core/src/org/gonevertical/core/client/input/WiseTextBox.java) - Included Module TextBox that grows as you type

&lt;wiki:gadget url="http://gwt-examples.googlecode.com/svn/trunk/DemoGadgetXml/war/gadget-aff.xml" height="100" width="740" border="0" /&gt;

