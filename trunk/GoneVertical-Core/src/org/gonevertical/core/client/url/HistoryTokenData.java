package org.gonevertical.core.client.url;

import java.util.HashMap;
import java.util.Iterator;

import com.google.gwt.user.client.rpc.IsSerializable;

public class HistoryTokenData implements IsSerializable {

  /**
   * like domain.tld#[historyToken]?params1=a&params2=b
   */
  private String historyToken;
  
  /**
   * like domain.tld#historyToken?[?params1=a&params2=b]
   */
  private HashMap<String, String> parameters;
  
  /** 
   * use this just incase of future serialization
   */
  public HistoryTokenData() {
  }
  
  public HistoryTokenData(String historyToken) {
    this.historyToken = historyToken;
  }
  
  public HistoryTokenData(String historyToken, HashMap<String, String> parameters) {
    this.historyToken = historyToken;
    this.parameters = parameters;
  }
  
  public void setHistoryData(String historyToken, HashMap<String, String> parameters) {
    this.historyToken = historyToken;
    this.parameters = parameters;
  }
  
  /**
   * get historyToken domain.tld#[historyToken]?param1=a&param2=b
   * @return
   */
  public String getHistoryToken() {
    return historyToken;
  }
  
  public String getHistoryTokenWithParameters() {
    
    String s = "";
    if (historyToken != null) {
      s += historyToken;
    }
    
    if (parameters != null && parameters.size() > 0) {
      s += HistoryTokenUtils.PARAMSSTART;
      s += getParametersConcatenated();
    }
    
    return s;
  }
  
  /**
   * get the first part of the historyToken until the underscore
   * like domain.tld#[historyToken]_home?param1=a
   * @return
   */
  public String getHistoryTokenToChar(String toChar) {
  	if (historyToken.matches("^.*" + HistoryTokenUtils.PARAMSSTART + toChar + ".*") == false) {
  		return null;
  	}
  	String[] split = historyToken.split(toChar);
  	String s = null;
  	if (split != null) {
  		s = split[0];
  	}
    return s;
  }
  
  /**
   * domain.tld/index.html#historyToken[_]tabby[_]end 
   *   looks like-> [_]=splitby, then array={historyToken,tabby,end}
   * @param splitBy
   * @return
   */
  public String[] getHistoryToken_andSplitBy(String splitBy) {
    if (splitBy == null) {
      return null;
    }
  	if (historyToken.contains(splitBy) == false) {
  		String[] s = new String[1];
  		s[0] = historyToken;
  		return s;
  	}
  	String[] s = historyToken.split(splitBy);
    return s;
  }
  
  public HashMap<String, String> getParameters() {
    return this.parameters;
  }
  
  /**
   * get query string parameter and return a integer
   * 
   * @param key
   * @return
   */
  public Integer getParameterAsInt(String key) {

    if (parameters == null) {
      return null;
    }

    if (key == null || key.length() == 0) {
      return null;
    }

    String s = parameters.get(key);
    if (s == null) {
      return null;
    }

    Integer i = null;
    if (s.toString().length() > 0) {
      try {
        i = Integer.parseInt(s.toString());
      } catch (NumberFormatException e) {
        i = null;
      }
    }

    return i;
  }
  
  public Long getParameterAsLong(String key) {

    if (parameters == null) {
      return null;
    }

    if (key == null || key.length() == 0) {
      return null;
    }

    String s = parameters.get(key);
    if (s == null) {
      return null;
    }

    Long l = null;
    if (s.toString().length() > 0) {
      try {
        l = Long.parseLong(s.toString());
      } catch (NumberFormatException e) {
        l = null;
      }
    }

    return l;
  }

  /**
   * get a query string parameter and return a String
   * 
   * @param parameters
   * @param key
   * @return
   */
  public String getParameterAsString(String key) {

    if (parameters == null) {
      return null;
    }

    if (key == null || key.length() == 0) {
      return null;
    }

    String s = null;
    if (parameters.get(key) != null && parameters.get(key).toString().length() > 0) {
      s = parameters.get(key).toString().trim();
    }

    return s;
  }
  
  /**
   * get HashMap parameters in url concatenated form, ready for url
   * 
   * like [param1=a&param2=b&param3=c]
   * 
   * @return 
   */
  public String getParametersConcatenated() {

    if (parameters == null) {
      return null;
    }

    String qs = "";

    Iterator<String> iterator = parameters.keySet().iterator();
    int i = 0;
    while (iterator.hasNext()) {

      String key = iterator.next().toString();

      qs += key + HistoryTokenUtils.PARAMKEYVALUESPLITTER + parameters.get(key).toString();

      if (i < parameters.size() - 1) {
        qs += HistoryTokenUtils.PARAMSPLITTER;
      }
      i++;
    }
    
    if (qs.trim().length() == 0) {
      qs = null;
    }

    return qs;
  }
  
}
