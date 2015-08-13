
&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" scrolling=0/&gt;

# Notes On Version 3 Api #
> Maps V3 api needs outside help due to limited resources currently on other projects at the moment. I am suggesting automating the javascript implementation build process to help us not fall behind on future javascript api version. (Automating is the GWT APIs team idea, Thanks Eric)
  * [Automate Issue](http://code.google.com/p/gwt-google-apis/issues/detail?id=452) - Lets get the api build process automated so we don't fall behind again!
  * [Request V3 Issue](http://code.google.com/p/gwt-google-apis/issues/detail?id=381&q=v3&colspec=ID%20Type%20API%20Status%20Priority%20Milestone%20Owner%20Summary) - Issue for version 3 api.
  * [V3 Contribution Discussion](https://groups.google.com/d/topic/gwt-google-apis/4e9GU638KCc/discussion) - V3 contribution, vinray has made a v3 api, but JSIO objects have to be stripped


# Demo GWT Maps #
> The goal is to render or a map with GWT.

  * [Demo](http://demogwtmaps.appspot.com) - Simple Maps Demo
  * [Source](http://code.google.com/p/gwt-examples/source/browse/trunk/DemoGWTMaps/src/org/gonevertical/demogwtmaps/client/Demo1.java) - find the source here
  * [c.Gawkat.com](http://c.gawkat.com/#vt_home) - I use gwt maps api here for my panoramic virtual tours.

## Custom Map Controls ##
> Want to add your own control to the map.
```
    ControlPosition controlPosition = new ControlPosition(ControlAnchor.TOP_LEFT, 100, 0);
    Control handControl = new Control.CustomControl(controlPosition) {
      public boolean isSelectable() {
        return true;
      }
      protected Widget initialize(MapWidget map) {
        Image mhand = new Image(UI_IMAGES.map_hand());
        mhand.addClickHandler(new ClickHandler() {
          public void onClick(ClickEvent event) {
            // do something
          }
        });
        return mhand;
      }
    };
    map.addControl(handControl);
```

> Method example
```
private void drawMapAddItems() {
    final Image mhand = new Image(UI_IMAGES.map_hand());
    final Image mhandsel = new Image(UI_IMAGES.map_hand_sel());
    
    final Image mpush = new Image(UI_IMAGES.map_add_pushpin());
    final Image mpushsel = new Image(UI_IMAGES.map_add_pushpin_sel());
    
    
    ControlPosition controlPosition = new ControlPosition(ControlAnchor.TOP_LEFT, 100, 0);
    Control handControl = new Control.CustomControl(controlPosition) {
      public boolean isSelectable() {
        return true;
      }
      public Widget initialize(MapWidget map) {  
        return mhand;
      }
    };
    map.addControl(handControl);
    
    ControlPosition controlPosition2 = new ControlPosition(ControlAnchor.TOP_LEFT, 100, 0);
    Control handControl2 = new Control.CustomControl(controlPosition2) {
      public boolean isSelectable() {
        return true;
      }
      public Widget initialize(MapWidget map) {  
        return mhandsel;
      }
    };
    map.addControl(handControl2);
    
    mhand.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        mhand.setVisible(false);
        mhandsel.setVisible(true);
      }
    });
    
    mhandsel.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {
        mhand.setVisible(true);
        mhandsel.setVisible(false);
      }
    });
    
    mhandsel.setVisible(true);
    mhand.setVisible(false);
  }
```

&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;
