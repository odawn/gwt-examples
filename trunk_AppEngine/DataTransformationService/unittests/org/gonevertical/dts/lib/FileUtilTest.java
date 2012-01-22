package org.gonevertical.dts.lib;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.net.URISyntaxException;

import org.gonevertical.dts.test.Run_Test_Import_v1;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class FileUtilTest {

  private FileUtil fileUtil = null;
  
  private String execPath = "";
  
  @Before
  public void setUp() throws Exception {
    fileUtil = new FileUtil();
    
    File executionlocation = null;
    try {
      executionlocation = new File(Run_Test_Import_v1.class.getProtectionDomain().getCodeSource().getLocation().toURI());
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    execPath = executionlocation.getParent();
  }

  @After
  public void tearDown() throws Exception {
    fileUtil = null;
    execPath = null;
  }

  @Test
  public void testGetFileLineCount() {
    long linecount = fileUtil.getFileLineCount(new File(execPath + "/data/test/linecount.txt"));
    assertEquals(10, linecount);
  }

  @Test
  public void testFindInFile() {
    File file = new File(execPath + "/data/test/template1.txt");
    boolean found = fileUtil.findInFile(file, "\\$");
    assertEquals(true, found);
  }

  @Test
  public void testReplaceInFile() {
    File from = new File(execPath + "/data/test/template1.txt");
    File to = new File(execPath + "/data/test/replaceinfile.txt");
    fileUtil.copyFile(from, to);
    fileUtil.replaceInFileByLine(to, "\\$", "_");
    boolean found = fileUtil.findInFile(to, "\\$");
    assertEquals(false, found);
  }

  @Test
  public void testFindInDir() {
    File file = new File(execPath + "/data/test");
    File found = fileUtil.findInDir(file, "\\$");
    File actualFile = new File(execPath + "/data/test/template1.txt");
    assertEquals(actualFile, found);
  }

  @Test
  public void testFindLineCount() {
    File file = new File(execPath + "/data/test/template1.txt");
    File dir = new File(execPath + "/data/test");
    long lineCount = fileUtil.getFileLineCount(file);
    long foundCount = fileUtil.findLineCount(dir, "\\$");
    assertEquals(lineCount, foundCount);
  }
  
  @Test
  public void testMoveFileStringString() {
    fail("Not yet implemented");
  }

  @Test
  public void testMoveFileFileString() {
    //fileUtil.moveFile(moveFile, toDir)
  }

  @Test
  public void testMoveFileFileFile() {
    fail("Not yet implemented");
  }

  @Test
  public void testCreateDirectory() {
    fail("Not yet implemented");
  }

  @Test
  public void testMoveFileToFolder_Done() {
    fail("Not yet implemented");
  }

  @Test
  public void testDoesFileHeaderMatchStr() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetHeader() {
    fail("Not yet implemented");
  }

  @Test
  public void testDoesFileNameMatch() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetFileSize() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetNewFileName() {
    fail("Not yet implemented");
  }
  
  @Test
  public void testcopyFile() {
    fail("Not yet implemented");
  }

}
