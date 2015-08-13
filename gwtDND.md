
&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;

# Table of Contents #


# GWT-DND Library #
> http://code.google.com/p/gwt-dnd/ - very nice drag n drop library


# Dragging a Composite Widget #
> In order to drag a composite widget you must make your own dragHandler. In this case, I make a custom vertical panel with sinkEvents in order to watch for clicks(mouseEvents) on the verticalpanel.


> Make your composite widget consisting of this panel somewhere in the composite widget.
```
/**
 * I use this panel for the drag handler for my composite widgets
 * dragHandler = where the mouse holds the widget while dragging
 * 
 * @author Brandon Donnelson
 * 
 * I extend the vertical panel - not a composite widget
 */
public class WatchEventPanel extends VerticalPanel implements SourcesMouseEvents {

	private MouseListenerCollection mouseListeners;

	/**
	 * constructor - extending VerticalPanel with event watching
	 */
	public WatchEventPanel() {
		
		//watch this widget's mouse events
		sinkEvents(Event.MOUSEEVENTS);
	}

	/**
	 * watch for browser events - from sinkEvents([MouseEvents])
	 */
    public void onBrowserEvent(Event event) {

        switch (DOM.eventGetType(event)) {
        	case Event.ONMOUSEDOWN:
	        	if (mouseListeners != null) {
					mouseListeners.fireMouseEvent(this, event);
				}
        		break;
        	case Event.ONMOUSEMOVE:
	        	if (mouseListeners != null) {
					mouseListeners.fireMouseEvent(this, event);
				}
        		break;
        	case Event.ONMOUSEOUT:
        		break;
        	case Event.ONMOUSEOVER:
        		break;
        	case Event.ONMOUSEUP:
	        	if (mouseListeners != null) {
					mouseListeners.fireMouseEvent(this, event);
				} 
        		break;
        	case Event.ONMOUSEWHEEL:
        		break;
        } 
    }
    
    /**
     * observer stuff
     */
	public void addMouseListener(MouseListener listener) {
		if (mouseListeners == null)
			mouseListeners = new MouseListenerCollection();
		mouseListeners.add(listener);
	}

	public void removeMouseListener(MouseListener listener) {
		if (mouseListeners != null)
			mouseListeners.remove(listener);
	} 

}
```

> ## Getting the DragHandler From The Composite Widget In Order To Drag It ##
> I make a method to get my dragHandler (WatchEventPanel) from my composite widget.
```
//make my widget first
MyWidget wCompositeWidget = new MyWidget();
MyWidget.setLabel("test");

//get my dragHandler in that widget (WatchEventPanel)
Widget wDrag = wCompositeWidget.getDragHandler();

//add my widget to a panel that renders to screen
pVerticalPanel.add(wCompositeWidget);
		
//make Vertical Panel draggable
widgetDragController.makeDraggable(pVerticalPanel, wDrag);
```


# Test Drag In Drop #
> Add the gwt-dnd lib to your class path.

```
/**
 * test drag and drop
 * @author branflake2267
 *
 */
public class Drag extends Composite implements DragHandler {

	private PickupDragController dragController;


	//constructor 
	public Drag() {

		// boundary - has to be absolute panel
		AbsolutePanel boundaryPanel = new AbsolutePanel();
		boundaryPanel.setSize("300px", "500px");
		boundaryPanel.addStyleName("test1");

	    // instantiate the common drag controller used the less specific examples
	    dragController = new PickupDragController(boundaryPanel, true);
	    dragController.setBehaviorMultipleSelection(false);
		

		//init widget - its boundary
		initWidget(boundaryPanel);

		// instantiate drag handler to listen for events
		dragController.addDragHandler(this);
		
		
		// initialize our widget drag controller
		PickupDragController widgetDragController = new PickupDragController(boundaryPanel, false);
		widgetDragController.setBehaviorMultipleSelection(false);
		widgetDragController.addDragHandler(this);

		
		// initialize horizontal panel to hold our columns
		HorizontalPanel horizontalPanel = new HorizontalPanel();
		horizontalPanel.addStyleName("test4");
		horizontalPanel.setSpacing(6);
		horizontalPanel.setHeight("100%");
		boundaryPanel.add(horizontalPanel);
		

		// initialize inner vertical panel to hold individual widgets
		VerticalPanel verticalPanel = new VerticalPanel();
		verticalPanel.addStyleName("test1");
		verticalPanel.setSpacing(5);
		horizontalPanel.add(verticalPanel);

		// initialize a widget drop controller for the current column
		NoInsertAtEndIndexedDropController widgetDropController = new NoInsertAtEndIndexedDropController(verticalPanel);
		widgetDragController.registerDropController(widgetDropController);

		for (int row = 1; row <= 5; row++) {
			// initialize a widget
			HTML widget = new HTML("Draggable&nbsp;#" + row);
			widget.addStyleName("test3");
			widget.setHeight(Random.nextInt(4) + 2 + "em");
			verticalPanel.add(widget);

			// make the widget draggable
			widgetDragController.makeDraggable(widget);
		}
	}


	/**
	 * drag done do
	 */
	public void onDragEnd(DragEndEvent event) {
	}

	/**
	 * drag start do
	 */
	public void onDragStart(DragStartEvent event) {
	}
	
	/**
	 * preview drag end do 
	 */
	public void onPreviewDragEnd(DragEndEvent event) throws VetoDragException {
	}

	/**
	 * preview drag start do
	 */
	public void onPreviewDragStart(DragStartEvent event) throws VetoDragException {	
	}

}

```

> You will also need this file
```
/**
 * IndexedDropController that disallows dropping after the last child, which is
 * assumed to be dummy spacer widget preventing parent collapse.
 */
public class NoInsertAtEndIndexedDropController extends IndexedDropController {

  private IndexedPanel dropTarget;

  public NoInsertAtEndIndexedDropController(IndexedPanel dropTarget) {
    super(dropTarget);
    this.dropTarget = dropTarget;
  }

  @Override
  protected void insert(Widget widget, int beforeIndex) {
    if (beforeIndex == dropTarget.getWidgetCount()) {
      beforeIndex--;
    }
    super.insert(widget, beforeIndex);
  }
}
```

&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;
