package org.gonevertical.dts.process.copy;

import org.gonevertical.dts.process.Transfer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Copy {
  
  private static final Logger log = LoggerFactory.getLogger(Copy.class);

	private String tableLeft;
	private String tableRight;

	public Copy() {
	}
	
	public void setTables(String tableLeft, String tableRight) {
		this.tableLeft = tableLeft;
		this.tableRight = tableRight;
	}
	
	
	
}
