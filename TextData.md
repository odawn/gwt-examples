
&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;


# Text Data #
> I build specific class/object static types to match the Google App Engine value types for RPC data transport.

```
public class TextData implements IsSerializable {

  /**
   * store larger datastore text
   */
  private String text;

  // must have an empty constructor for serialization policy
  public TextData() {
  }
  
  public TextData(String text) {
    this.text = text;
  }
  
  public void setText(String text) {
    this.text = text;
  }
  
  public String getText() {
    return text;
  }
  
  public String toString() {
    String s = "";
    s += "text=" + text + " ";
    return s;
  }
}
```

&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;
