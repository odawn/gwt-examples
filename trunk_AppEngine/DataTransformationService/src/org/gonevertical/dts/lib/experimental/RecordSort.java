package org.gonevertical.dts.lib.experimental;

import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecordSort implements Comparator<Record> {

  private static final Logger log = LoggerFactory.getLogger(RecordSort.class);
  
  public int compare(Record a, Record b) {
    
    int c = 0;
    
    String[] aGb = a.getGroubByValues();
    String[] bGb = b.getGroubByValues();
    for (int i=0; i < aGb.length; i++) {
      c += aGb[i].compareTo(bGb[i]);
    }
    
    return c;
  }

}
