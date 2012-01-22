package org.gonevertical.dts.lib.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gonevertical.dts.lib.FileUtil;
import org.gonevertical.dts.lib.experimental.RecordSort;


public class MoveFile extends FileUtil {
	
  private static final Logger log = LoggerFactory.getLogger(MoveFile.class);

  // what to match and move to where
  private ArrayList<MoveFileData> moveFileData = null;
  
  // diretory to process
  private File dir = null;
  
  
  public MoveFile() {
    moveFileData = new ArrayList<MoveFileData>();
  }
  
  public void addMoveFileData(MoveFileData moveFileData) {
    this.moveFileData.add(moveFileData);
  }
  
  public void setProcessDirectory(File dir) {
    this.dir = dir;
  }
  
  public void run() {
    processFiles();
    
    System.out.println("Finished");
  }
  
  private void processFiles() {
    
    File[] files = dir.listFiles();
    Arrays.sort(files);
    
    for (int i=0; i < files.length; i++) {
      
      System.out.println("processing file: " + files[i].getName());
      if (files[i].isDirectory()) {
        // skip directory
      } else {
        check(files[i]);
      }
      
    }
    
  }

  private void check(File file) {
   
    // don't work with the partly downloaded files
    if (file.getName().contains(".part")) {
      System.out.println("skipping file: " + file.getName());
      return;
    }
    
    for (int i=0; i < moveFileData.size(); i++) {
      //System.out.println("\t "+i+". checking agianst moveFileData" + moveFileData.get(i).getMovePath());
      
      boolean match = false;
      
      if (moveFileData.get(i).getFileNameRegex() != null && moveFileData.get(i).getFileNameRegex().length() > 0) {
        
        match = checkByFileName(i, moveFileData, file);
        
      } else {
        match = checkByHeaders(i, moveFileData, file);
      }
      
      if (match == true) {
        break;
      }
      
    }
    
  }
  
  private boolean checkByFileName(int i, ArrayList<MoveFileData> moveFileData, File file) {
    
    boolean match = false;
    if (doesFileNameMatch(file, moveFileData.get(i).getFileNameRegex()) == true) {
      String toDir = moveFileData.get(i).pathToMoveToDir;
      System.out.println("\tmoving file:" + file.getName() + " to: " + toDir);
      moveFile(file, toDir);
      match = true;
    }
    
    return match;
  }
  
  private boolean checkByHeaders(int i, ArrayList<MoveFileData> moveFileData, File file) {

    MoveFileData m = moveFileData.get(i);
    String[] compareHeader = getHeader(file, m.getDelimiter());
    boolean match = m.compareHeaders(compareHeader);
    
    if (match == true) {
      String toDir = moveFileData.get(i).pathToMoveToDir;
      System.out.println("\tmoving file:" + file.getName() + " to: " + toDir);
      moveFile(file, toDir);
      match = true;
    }
    
    return match;
  }


  
}
