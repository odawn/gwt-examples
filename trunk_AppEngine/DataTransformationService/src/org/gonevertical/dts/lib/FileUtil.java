package org.gonevertical.dts.lib;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.gonevertical.dts.data.UserDbData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.csvreader.CsvReader;

public class FileUtil {
	
  private static final Logger log = LoggerFactory.getLogger(FileUtil.class);

  public FileUtil() {
  }
  
  /**
   * how many lines in the file, good for figuring out how many csv rows
   * 
   * @param file
   * @return
   */
  public long getFileLineCount(File file) {
    if (file == null) {
      log.warn("Error: Your file was null.");
      return 0;
    }
    FileInputStream fis = null;
    DataInputStream dis = null;
    long i = 0;
    try {
      fis = new FileInputStream(file);
      dis = new DataInputStream(fis);
      BufferedReader br = new BufferedReader(new InputStreamReader(dis));
      String s = null;
      while ((s = br.readLine()) != null) {
        i++;
      }
      br.close();
      fis.close();
      dis.close();
    } catch (FileNotFoundException e) {
      log.error("error", e);
    } catch (IOException e) {
      log.error("error", e);
    }
    return i;
  }
  
  /**
   * find first regex value in a file
   * 
   * @param file
   * @param regex
   * @return
   */
  public static boolean findInFile(File file, String regex) {
    if (file == null) {
      log.warn("Error: Your file was null.");
      return false;
    }
    FileInputStream fis = null;
    BufferedInputStream bis = null;
    DataInputStream dis = null;
    BufferedReader br = null;
    boolean found = false;
    try {
      fis = new FileInputStream(file);
      bis = new BufferedInputStream(fis);
      dis = new DataInputStream(bis);
      br = new BufferedReader(new InputStreamReader(dis));
      String s = null;
      while ((s = br.readLine()) != null) {
        found = StringUtil.findMatch(regex, s);
        if (found == true) {
          break;
        }
      }
      br.close();
      fis.close();
      bis.close();
      dis.close();
    } catch (FileNotFoundException e) {
      log.error("error", e);
    } catch (IOException e) {
      log.error("error", e);
    }
    return found;
  }
  
  public static String findInFileAndReturnLine(File file, String regex) {
    if (file == null) {
      log.warn("Error: Your file was null.");
      return null;
    }
    String line = null;
    FileInputStream fis = null;
    BufferedInputStream bis = null;
    DataInputStream dis = null;
    BufferedReader br = null;
    boolean found = false;
    try {
      fis = new FileInputStream(file);
      bis = new BufferedInputStream(fis);
      dis = new DataInputStream(bis);
      br = new BufferedReader(new InputStreamReader(dis));
      String s = null;
      while ((s = br.readLine()) != null) {
        found = StringUtil.findMatch(regex, s);
        if (found == true) {
          line = s;
          break;
        }
      }
      br.close();
      fis.close();
      bis.close();
      dis.close();
    } catch (FileNotFoundException e) {
      log.error("error", e);
    } catch (IOException e) {
      log.error("error", e);
    }
    return line;
  }
  
  
  /**
   * do a line by line regex replace, like taking out the dollar sign in the file
   * 
   * @param file
   * @param regexFind
   * @param regexReplace
   * @return
   */
  public void replaceInFileByLine(File file, String regexFind, String regexReplace) {
    if (file == null) {
      log.warn("Error: Your file was null.");
      return;
    }
    String tmpName = file.getAbsolutePath() + ".tmp";
    FileInputStream fis = null;
    BufferedInputStream bis = null;
    DataInputStream dis = null;
    BufferedReader br = null;
    try {
      fis = new FileInputStream(file);
      bis = new BufferedInputStream(fis);
      dis = new DataInputStream(bis);
      br = new BufferedReader(new InputStreamReader(dis));
      FileWriter fstream = new FileWriter(tmpName);
      BufferedWriter out = new BufferedWriter(fstream);
      String s = null;
      while ((s = br.readLine()) != null) {
        s = s.replaceAll(regexFind, regexReplace);
        out.write(s + "\n");
      }
      out.close();
      fstream.close();
      br.close();
      fis.close();
      bis.close();
      dis.close();
    } catch (FileNotFoundException e) {
      log.error("error", e);
    } catch (IOException e) {
      log.error("error", e);
    }
    File rf = new File(tmpName);
    rf.renameTo(file);
    //file.delete();
  }
   
  /**
   * find a file with the first value
   * 
   * @param dir
   * @param regex
   * @return
   */
  public File findInDir(File dir, String regex) {
    
    if (dir.isDirectory() == false) {
      log.warn("Error: dir is supposed to be a directory, not a file.");
      return null;
    }
    
    File[] files = dir.listFiles();
    
    if (files == null) {
    	log.warn("No files to search");
    }
    
    File foundFile = null;
    for (int i=0; i < files.length; i++) {
      if (files[i].isFile() == true) {
        boolean found = findInFile(files[i], regex);
        if (found == true) {
          foundFile = files[i];
          break;
        }
      }
    }
    
    return foundFile;
  }
  
  /**
   * find a line count of a file with a particular regex value in a directory
   * 
   * @param dir
   * @param regex
   * @return
   */
  public long findLineCount(File dir, String regex) {
    if (dir.isDirectory() == false) {
      log.warn("Error: dir is supposed to be a directory, not a file.");
      return 0;
    }
    
    // first find the file we want
    File foundFile = findInDir(dir, regex);
    
    if (foundFile == null) {
      log.warn("findLineCount: couldn't find file");
      return 0;
    }
    
    // now get the line count
    long linecount = getFileLineCount(foundFile);
    
    return linecount;
  }
 
  
  /**
   * move file to new directory, and it will change the file name if one exists already
   * 
   * @param moveFile
   * @param toDir
   */
  public void moveFile(String moveFile, String toDir) {
    moveFile(new File(moveFile), new File(toDir));
  }
 
  public void moveFile(File moveFile, String toDir) {
    moveFile(moveFile, new File(toDir));
  }
  
  public void moveFile(File moveFile, File toDir) {
    
    if (toDir.exists() == false) {
      toDir.mkdirs();
    }
    
    File checkFile = new File(toDir.getPath() + "/" + moveFile.getName());
    String f = "";
    if (checkFile.exists() == true) {
      int i = getFileCount(toDir);
      f = i + "_";
    } 
    
    File file = moveFile;
    File dir = toDir;
    File dest = new File(dir, f + file.getName());
    boolean moved = file.renameTo(dest);
    if (moved != true) {
    	try {
	      FileUtils.moveFile(file, dest);
      } catch (IOException e) {
	      copyFile(file, dest);
	      delete(file);
      }
    }
  }
  
  private int getFileCount(File dir) {
    int i = 0;
    File[] files = dir.listFiles();
    if (files != null) {
      i = files.length;
    }
    return i;
  }
  
  public static boolean createDirectory(String path) {
    
    if (path == null | path.length() == 0) {
      log.warn("createDirectory path was null");
      return false;
    }
    
    File file = new File(path);
    boolean b = file.mkdirs();
    return b;
  }
  
  /**
   * move this file to a done folder
   * 
   * @param file
   */
  public void moveFileToFolder_Done(File file) {
    
    // create done folder if it doesn't exist
    String donePath = file.getParent() + "/done"; 
    createDirectory(donePath);
    
    moveFile(file, donePath);
  }
  
  /**
   * match a file with the same header values given
   * 
   * @param file
   * @param matchHeaderValues
   * @param delimiter
   * @return
   */
  public boolean doesFileHeaderMatchStr(File file, String matchHeaderValues, char delimiter) {
    
    // match with out a delimiter
    String sdelimiter = Character.toString(delimiter);
    matchHeaderValues = matchHeaderValues.replaceAll(sdelimiter, "");

    CsvReader reader = null;
    try {     
      reader = new CsvReader(file.toString(), delimiter);
    } catch (FileNotFoundException e) {
      log.error("doesFileHeaderMatchStr: Could not open CSV Reader", e);
    }
    
    if (reader == null) {
      return false;
    }
    
    String[] header = null;
    try {
      reader.readHeaders();
      header = reader.getHeaders();
    } catch (IOException e) {
      log.error("doesFileHeaderMatchStr: could not read headers", e);
    }
    
    if (header == null) {
      return false;
    }
    
    String sheader = "";
    for (int i=0; i < header.length; i++) {
      sheader += header[i];
    }
    
    if (sheader.equals(matchHeaderValues)) {
      return true;
    }
    
    // TODO - do a lessor, use less file values to make string and check ??
    
    return false;
  }
  
  /**
   * get csv header fields
   * 
   * @param file
   * @param delimiter
   * @return
   */
  public String[] getHeader(File file, char delimiter) {
    
    CsvReader reader = null;
    try {     
      reader = new CsvReader(file.toString(), delimiter);
    } catch (FileNotFoundException e) {
      log.error("doesFileHeaderMatchStr: Could not open CSV Reader", e);
    }
    
    if (reader == null) {
      return null;
    }
    
    String[] header = null;
    try {
      reader.readHeaders();
      header = reader.getHeaders();
    } catch (IOException e) {
      log.error("doesFileHeaderMatchStr: could not read headers", e);
    }

    if (header == null) {
    	return null;
    }

    return header;
  }

  public boolean doesFileNameMatch(File file, String regex) {

  	if (regex == null | file.getName() == null) {
  		return false;
  	}

  	boolean b = false;
  	try {
  		Pattern p = Pattern.compile(regex);
  		Matcher m = p.matcher(file.getName());
  		b = m.find();
  	} catch (Exception e) {
  		log.error("doesFileNameMatch: regex error", e);
  	}

  	return b;
  }

  /**
   * get file size in KB
   * 
   * @param file
   * @return
   */
  public static long getFileSize(File file) {
  	if (file == null) {
  		return 0;
  	}
  	long size= file.length() / 1000;
  	return size;
  }

  /**
   * change the file name from file.csv to file_1.csv
   * 
   * @param file
   * @param index
   * @return
   */
  public static String getNewFileName(File file, int index) {
  	String fileName = file.getName();
  	String regex = ".*\\.(.*)";
  	String newFileName = null;
  	try {
  		Pattern p = Pattern.compile(regex);
  		Matcher m = p.matcher(fileName);
  		String ext = m.group(1);
  		String newExt = "_" + index + "." + ext;
  		newFileName = m.replaceAll(newExt);
  	} catch (Exception e) {
  		log.error("findMatch: regex error", e);
  	}
  	return newFileName;
  }

  /**
   * copy file 
   * 
   * @param inputFile
   * @param outputFile
   */
  public void copyFile(File inputFile, File outputFile) {
  	FileReader in = null;
  	try {
  		in = new FileReader(inputFile);
  	} catch (FileNotFoundException e) {
  		log.error("error", e);
  	}
  	FileWriter out = null;
  	try {
  		out = new FileWriter(outputFile);
  		int c;
  		while ((c = in.read()) != -1) {
  			out.write(c);
  		}
  	} catch (IOException e) {
  		log.error("FileUtil.copyFile Error: ", e);
  	}

  	try {
  		in.close();
  		out.close();
  	} catch (IOException e) {
  		log.error("error", e);
  	}
  }

  public static boolean delete(File file) { 

  	if (file.isDirectory()){
  		File[] childFiles = file.listFiles();
  		for (File child : childFiles){
  			delete(child);
  		}
  	}
  	
  	boolean b = file.delete();

  	if (b == false) {
  		file.deleteOnExit();
  		
  	}

  	return b;
  }
   
  public static boolean writeToFile(File file, String s) {
    boolean success = false;
    BufferedWriter out = null;
    try {
      out = new BufferedWriter(new FileWriter(file, false));
    } catch (IOException e) {
      e.printStackTrace();
    }    
    try {
      out.write(s);
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return success;
  }
  
}
