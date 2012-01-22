package org.gonevertical.dts.v2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gonevertical.dts.data.SourceData;
import org.gonevertical.dts.data.StatData;

public class ProcessImport {
	
  private static final Logger log = LoggerFactory.getLogger(ProcessImport.class);

  // data start point
  private SourceData sourceData = null;
  
  // data endpoint
  private DestinationData dd = null;

  // file processing class - starts the process going through the files
  private FileProcessing fileProcessing = null;
  
  /**
   * constructor - init
   * 
   * @param sourceData
   * @param destinationData
   */
  public ProcessImport(SourceData sourceData, DestinationData destinationData) {
    this.sourceData = sourceData;
    this.dd = destinationData;
    fileProcessing = new FileProcessing(sourceData, destinationData);
  }
  
  /**
   * import data
   */
  public void runImport() {
    fileProcessing.run();
    
    if (dd.optimise == true) {
      Optimise o = new Optimise(dd);
      o.run();
    }
    
    // this will happen on a first import, so to optimise early, then start agian
    if (fileProcessing.getReturnOnOptimise() == true) {
      dd.optimise = false;
      dd.dropTable = false;
      runImport();
    }
    
    log.info("ProcessImport.runImport(): Finished with processing import: sourceData.file: " + sourceData.file.getName());
  }
  
  /**
   * find data in a csv file
   */
  public void runFindInFile() {
    fileProcessing.findMatchInFile();
  }
  
  public StatData getStats() {
  	return fileProcessing.getStats();
  }

  
}
