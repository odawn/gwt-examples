
&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" scrolling=0 /&gt;

# GWT Visualization Or Charts #

## Tools ##
  * http://imagecharteditor.appspot.com/ - Very nice chart editor!
  * http://code.google.com/apis/chart/image/docs/gallery/qr_codes.html - reference

## Create a QR Bar Code ##
> Create a QR bar code using the java code in GWT.

```
package org.gonevertical.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.AnnotatedTimeLine;
import com.google.gwt.visualization.client.visualizations.Gauge;
import com.google.gwt.visualization.client.visualizations.GeoMap;
import com.google.gwt.visualization.client.visualizations.ImageAreaChart;
import com.google.gwt.visualization.client.visualizations.ImageBarChart;
import com.google.gwt.visualization.client.visualizations.ImageChart;
import com.google.gwt.visualization.client.visualizations.ImageLineChart;
import com.google.gwt.visualization.client.visualizations.ImagePieChart;
import com.google.gwt.visualization.client.visualizations.ImageSparklineChart;
import com.google.gwt.visualization.client.visualizations.IntensityMap;
import com.google.gwt.visualization.client.visualizations.MapVisualization;
import com.google.gwt.visualization.client.visualizations.MotionChart;
import com.google.gwt.visualization.client.visualizations.OrgChart;
import com.google.gwt.visualization.client.visualizations.Table;
import com.google.gwt.visualization.client.visualizations.corechart.CoreChart;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class DemoGwtChart implements EntryPoint {


  public void onModuleLoad() {

    VisualizationUtils.loadVisualizationApi(
        new Runnable() {
          public void run() {
            draw();
          }
        }, AnnotatedTimeLine.PACKAGE, CoreChart.PACKAGE,
        Gauge.PACKAGE, GeoMap.PACKAGE, ImageChart.PACKAGE,
        ImageLineChart.PACKAGE, ImageAreaChart.PACKAGE, ImageBarChart.PACKAGE,
        ImagePieChart.PACKAGE, IntensityMap.PACKAGE,
        MapVisualization.PACKAGE, MotionChart.PACKAGE, OrgChart.PACKAGE,
        Table.PACKAGE,
        ImageSparklineChart.PACKAGE);

  }

  private void draw() {

    ImageChart.Options options = ImageChart.Options.create();
    options.set("chs", "400x400");
    options.set("cht", "qr");
    options.set("chl", "Hello+world");

    DataTable dataTable = DataTable.create();
    ImageChart widget = new ImageChart(dataTable, options);

    RootPanel.get().add(widget);

    System.out.println("finished");
  }

}
```

&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;
