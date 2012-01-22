package org.gonevertical.dts.lib.openoffice;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * 
 * 
 * @author branflake2267
 *
 * http://www.artofsolving.com/opensource/jodconverter/guide
 * http://www.oooninja.com/2008/02/batch-command-line-file-conversion-with.html
 * 
 * or
 * 
 *  unoconv --listener &
    sleep 20
    unoconv -f pdf *.odt
    unoconv -f doc *.odt
    unoconv -f html *.odt
    kill -15 %-
 *
 */
public class ConvertToExcel {

  public static void main(String[] args) {
    new ConvertToExcel().convert();
  }

  public ConvertToExcel() {
  }

  public void convert() {
    
    ///officeListen();
    
    File executionlocation = null;
    //try {
      executionlocation = new File(""); // Run_Test_Import_v1.class.getProtectionDomain().getCodeSource().getLocation().toURI()
    //} catch (URISyntaxException e) {
      //e.printStackTrace();
    //}
    String execPath = executionlocation.getParent();
    String filename = execPath + "/data/export/export_oo_test.ods";
   
    runUnoconv(filename);

    System.out.println("Finished");
  }
  
  public void convert(String filename) {
    runUnoconv(filename);
  }
  
  private void runJod() {
    //String execPath = executionlocation.getParent();
    //String in = execPath + "/data/export/export_oo_test.ods";
    //String out = execPath + "/data/export/export_oo_test.xls";
    //String cmd = "python "+ execPath +"/src_office/org/gonevertical/dts/openoffice/DocumentConverter.py " + in + " " + out;
    //runCmd(cmd);
  }
  
  private void runUnoconv(String filename) {
    //String cmd = "unoconv --listener";
    //runCmd(cmd);
    //cmd = "sleep 5";
    //runCmd(cmd);
    String command = "unoconv -f xls " + filename + "";
    System.out.println("exporting to excel: " + command);
    try {
	    Runtime.getRuntime().exec(command);
    } catch (IOException e) {
	    e.printStackTrace();
    }
    //cmd = "kill -15 %-";
    //runCmd(cmd);
  }
  
  private void officeListen() {
  	
  	// TODO is the port open to start with??? 
  	
    String cmd = "soffice -headless -accept=\"socket,port=8100;urp;\"";
    runCmd(cmd);
  }
  
  private void runCmd(String cmd) {

    System.out.println(cmd);

    String s = null;
    try {
      Process p = Runtime.getRuntime().exec(cmd);
      BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
      BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
      System.out.println("Here is the standard output of the command:\n");
      
      while ((s = stdInput.readLine()) != null) {
        System.out.println(s);
      }
      System.out.println("Here is the standard error of the command (if any):\n");
      while ((s = stdError.readLine()) != null) {
        System.out.println(s);
      }
      System.exit(0);
    }
    catch (IOException e) {
      System.out.println("exception happened - here's what I know: ");
      e.printStackTrace();
      System.exit(-1);
    }

  }

}
