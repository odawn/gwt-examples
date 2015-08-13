I put more notes here: http://c.gwt-examples.com/javascriptinjection


&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;

# GWT Script Inject #
> I'm using this to load a Google javascript api.  This has a onload and onload fail callback after the script loads.
```
public class LoadApi {
  /**
   * load api
   * 
   * @param onLoad - callback on load of api success
   * @param onLoadFail - callback on load api failure
   * @param sensor - derive location [true|false]
   */
  public static void go(Runnable onLoad, Runnable onLoadFail, boolean sensor) {
    load(onLoad, onLoadFail, sensor);
  }
  
  private static void load(final Runnable onLoad, final Runnable onLoadFail, boolean sensor) {
    ScriptInjector.fromUrl("http://maps.googleapis.com/maps/api/js?sensor=" + sensor).setCallback(
        new Callback<Void, Exception>() {
          public void onFailure(Exception reason) {
            onLoadFail.run();
          }
          public void onSuccess(Void result) {
            onLoad.run();
          }
        }).inject();
  }
  
  /**
   * private constructor
   */
  private LoadApi() {
  }
}
```