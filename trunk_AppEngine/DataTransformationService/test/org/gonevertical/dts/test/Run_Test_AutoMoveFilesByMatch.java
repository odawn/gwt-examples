package org.gonevertical.dts.test;

import java.io.File;

import org.gonevertical.dts.lib.file.MoveFile;
import org.gonevertical.dts.lib.file.MoveFileData;


public class Run_Test_AutoMoveFilesByMatch {

  /**
   * @param args
   */
  public static void main(String[] args) {
    
    MoveFileData fileTypeA = new MoveFileData();
    fileTypeA.delimiter = ",".charAt(0);
    fileTypeA.setMatchHeaderValues(new File("/home/branflake2267/files/process/typea"));
    fileTypeA.pathToMoveToDir = "/home/branflake2267/files/process/typea";
    
    MoveFile move = new MoveFile();
    move.setProcessDirectory(new File("/home/branflake2267/files/process"));
    move.addMoveFileData(fileTypeA);
    move.run();
    
  }

}
