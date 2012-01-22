package org.gonevertical.dts.lib.text;

import java.util.ArrayList;

import org.gonevertical.dts.lib.sql.transformlib.OracleTransformLib;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TextCleaner {
	
  private static final Logger log = LoggerFactory.getLogger(TextCleaner.class);
 
  private ArrayList<String> removeRegex = null;
  
  private String text = null;

  public TextCleaner() {
  }

  // TODO find duplicates (?\b\w+\b)(?=.+\b\k\b)
  
  public void setText(String text) {
    this.text  = text;
  }
  
  /**
   * set patterns to remove
   * 
   * @param regex
   */
  public void setRemoveRegex(String regex) {
    if (regex == null | regex.length() == 0) {
      return;
    }
    if (removeRegex == null) {
      removeRegex = new ArrayList<String>();
    }
    removeRegex.add(regex);
  }
  
  public void setRemoveAllWord_CaseIns(String word) {
    if (word == null | word.length() == 0) {
      return;
    }
    if (removeRegex == null) {
      removeRegex = new ArrayList<String>();
    }
    
    String regex = "(?i)\\b("+word+")\\b";
    
    removeRegex.add(regex);
  }
  
  public void setRemoveAllPunctuation() {
    text = text.replaceAll("\\.", " ");
    text = text.replaceAll("\\!", " ");
    text = text.replaceAll("\\?", " ");
    text = text.replaceAll("\\.", " ");
    text = text.replaceAll("\\,", " ");
  }
  
  public void setRemoveAllNonAlpha() {

    text = text.replaceAll("\\.", " ");
    text = text.replaceAll("\\!", " ");
    text = text.replaceAll("\\?", " ");
    text = text.replaceAll("[0-9%]", " ");
    text = text.replaceAll("\t", " ");
    text = text.replaceAll("\n", " ");
    text = text.replaceAll("\r", " ");
    text = text.replaceAll("'", "");
    text = text.replaceAll("[\\W]", " "); // this shoul do it all
        
  }
  
  public void clean() {
    removeLineBreaks();
    removeRegex();
    fixWordSpacing();
  }
  
  public String getText() {
    return this.text.trim();
  }
  
  private void removeLineBreaks() {
    text = text.replaceAll("\n", " ");
    text = text.replaceAll("\r", " ");
    text = text.replaceAll("\t", " ");
  }
  
  private void removeRegex() {
    
    if (removeRegex == null) {
      return;
    }
    
    for (int i=0; i < removeRegex.size(); i++) {
      removeRegex(removeRegex.get(i));
    }
    
  }
  
  private void removeRegex(String regex) {
    text = text.replaceAll(regex, " ");
  }
  
  private void fixWordSpacing() {
    text = text.replaceAll("[\040]+", " ");
  }
  

}
