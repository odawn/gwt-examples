package org.gonevertical.dts.lib.text;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WordParser {
	
  private static final Logger log = LoggerFactory.getLogger(WordParser.class);

  private TextCleaner textCleaner = new TextCleaner();
  
  private String text = null;

  private int wordCount = 0;

  private String[] split = null;

  private ArrayList<String> combos = null;

  public WordParser() {
  }

  public void setText(String text) {

    if (text == null) {
      System.out.println("can't work with null");
      return;
    }
    
    // setup a new combo object, clear past object so not to add more
    combos = new ArrayList<String>();

    this.text = text;
    this.text = this.text.trim();
    
    clean();
    countWords();
  }

  public void run(int howManyWords) {
    if (text == null) {
      System.out.println("can't work with null");
      return;
    }

    if (wordCount < 2) {
      // TODO - Has to be more than to words, why did i put this here, can't remember
      return;
    }
    
    int start = 0;
    int end = howManyWords;
    for (int i = 0; i < wordCount; i++) {
      String s = getTextWords(start, end);
      System.out.println("words:" + s);

      start++;
      end++;

      if (end > wordCount) {
        break;
      }

    }
  }
  
  private String getTextWords(int start, int end) {

    // 0,0 = first word
    /*
     * String re = "^(?:\\S+\\s+){"+start+"}(\\S+)\\040+(\\S+\\s+){"+end+"}";
     * 
     * Pattern p = Pattern.compile(re); Matcher m = p.matcher(text); boolean
     * found = m.find();
     * 
     * String s = null; if (found == true) { s = m.group(1) + " "; s +=
     * m.group(2); }
     */

    String s = "";
    for (int i = start; i < end; i++) {
      try {
        s += split[i] + " ";
      } catch (Exception e) {
      }
    }

    s = s.trim();

    combos.add(s);

    return s;
  }

  private void clean() {
    textCleaner.setText(text);
    textCleaner.setRemoveAllNonAlpha();
    textCleaner.clean();
    text = textCleaner.getText();
  }
  
  public String getText() {
    return this.text;
  }

  private void countWords() {
    // TODO - do more error checking and report back no combo
    try {
      split = text.trim().split("[\040]+");
      wordCount = split.length;
    } catch (Exception e) {
    }
    
  }

  public ArrayList<String> getCombos() {
    return combos;
  }

}
