package org.gonevertical.dts.v2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * run this from a parent project, not in standalone
 * 
 * @author branflake2267
 *
 */
public class Run_Logger_Test {

  private static final Logger log = LoggerFactory.getLogger(Run_Logger_Test.class);
		
	// try including this in parent project
	public Run_Logger_Test() {
		
	}
	
	public void run() {
		
		log.error("Test worked");
	}
	
}
