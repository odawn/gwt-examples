package org.gonevertical.dts.process;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gonevertical.dts.data.DatabaseData;
import org.gonevertical.dts.lib.text.WordParser;


public class ImportSqlScript {
	
  private static final Logger log = LoggerFactory.getLogger(ImportSqlScript.class);

  public DatabaseData dd = null;
  
  public File file = null;
  
  public ImportSqlScript(DatabaseData destination) {
    dd = destination;
  }
  
  public void run(File file) {
    this.file = file;
    
    readFile();
  }
  
  private void readFile() {
    try{
      FileInputStream fstream = new FileInputStream(file);
      DataInputStream in = new DataInputStream(fstream);
      BufferedReader br = new BufferedReader(new InputStreamReader(in));
      String strLine;
      while ((strLine = br.readLine()) != null)   {
        process(strLine);
      }
      in.close();
    }catch (Exception e){
      System.err.println("Error: " + e.getMessage());
    }
  }

  private void process(String strLine) {
  
    // TODO should I use mysql -u.... or find a jdbc class/method?
    
  }
  
}
