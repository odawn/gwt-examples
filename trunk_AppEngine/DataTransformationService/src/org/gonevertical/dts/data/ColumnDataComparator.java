package org.gonevertical.dts.data;

import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColumnDataComparator implements Comparator<ColumnData> {

  private static final Logger log = LoggerFactory.getLogger(ColumnDataComparator.class);
	
  public static final int NAME = 1;
  
  // default type is by column name
  public int by = NAME;

  public ColumnDataComparator(int by) {
    setBy(by);
  }
  
  public void setBy(int by) {
    this.by = by;
    if (by == 0) {
      by = NAME;
    }
  }
  
  public int compare(ColumnData a, ColumnData b) {
    
    int c = -1;
    
    if (by == NAME) {
      String ac = a.getColumnName();
      String bc = b.getColumnName();
      c = ac.compareToIgnoreCase(bc);
    }

    return c;
  }

}
