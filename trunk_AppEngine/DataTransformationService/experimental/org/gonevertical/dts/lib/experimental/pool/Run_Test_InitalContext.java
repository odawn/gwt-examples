package org.gonevertical.dts.lib.experimental.pool;

import org.gonevertical.dts.lib.pooling.ManualSetupOfInitialPoolingContext;

public class Run_Test_InitalContext {

	public static void main(String[] args) {
		
		String contextXmlPath = "/Users/branflake2267/Documents/workspace/Metrics_Nca/war/META-INF/context.xml";
		
		String tmpPath = "/Users/branflake2267/tmp";
		
		ManualSetupOfInitialPoolingContext ic = new ManualSetupOfInitialPoolingContext(tmpPath);
		
		
		
	}
	
}
