package org.gonevertical.dts.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShellUtil {
	
  private static final Logger log = LoggerFactory.getLogger(ShellUtil.class);
  
  public void exec(String command) {
    try {
      Runtime.getRuntime().exec(command);
    } catch (IOException e) {
      e.printStackTrace();
    }
    
  }
  
  public void exec_output(String command) {
    try {
      Process p = Runtime.getRuntime().exec(command);
      BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
      BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
      String s = "";
      while ((s = stdInput.readLine()) != null) {
        System.out.println(s);
      }
      while ((s = stdError.readLine()) != null) {
        System.out.println(s);
      }
    }
    catch (IOException e) {
      e.printStackTrace();
      log.error("error", e);
    }
  }
  
}
