package org.gonevertical.dts.data;

import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SortDestinationField implements Comparator<FieldData> {

  private static final Logger log = LoggerFactory.getLogger(SortDestinationField.class);
	
	@Override
	public int compare(FieldData a, FieldData b) {
		
		int i = a.destinationField.compareToIgnoreCase(b.destinationField);
		
		return i;
	}
	
}
