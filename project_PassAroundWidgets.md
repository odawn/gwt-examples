
&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="95" width="735" border="0" /&gt;

# Pass Around Widgets In an Array #
Download Eclipse Project From SVN: [~/gwt-test-ObjectReference/](http://gwt-examples.googlecode.com/svn/trunk/gwt-test-ObjectReference/)

> ## Code Snippet ##
```
public class Inputs {

	private final VerticalPanel vp = new VerticalPanel();
	private Widget[] objects;
	
	/**
	 * constructor
	 */
	public Inputs() {
		
		
		Widget[] objects = new Widget[4];
		
		TextArea ta = new TextArea();
		objects[0] = ta;
		
		TextBox tb = new TextBox();
		objects[1] = tb;
		
		TextBox tb2 = new TextBox();
		objects[2] = tb2;
		
		PushButton save = new PushButton("Save");
		objects[3] = save;
		
		
		this.setObjects(objects);
	}
	
	/**
	 * Make form of objects 
	 */
	public void drawForm() {
	
		vp.add(new Label("test"));
		
		RootPanel.get().add(vp);
		
		this.drawObjects();
	}
	
	public void setObjects(Widget[] objects) {
		this.objects = objects;
	}
	
	
	public void drawObjects() {
		
		for (int i=0; i < this.objects.length; i++) {
			vp.add(this.objects[i]);
		}
		
	}
	
}
```

&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;
