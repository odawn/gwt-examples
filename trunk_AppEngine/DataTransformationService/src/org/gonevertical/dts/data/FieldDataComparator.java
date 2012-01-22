package org.gonevertical.dts.data;

import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
 
public class FieldDataComparator implements Comparator<FieldData> {
	
  private static final Logger log = LoggerFactory.getLogger(FieldDataComparator.class);

  // sort by sourceField
	public int compare(FieldData a, FieldData b) {
		int i = a.sourceField.compareToIgnoreCase(b.sourceField);
		return i;
	}

}
