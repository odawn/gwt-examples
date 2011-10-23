package org.gonevertical.core.client.url;

import java.util.HashMap;

import com.google.gwt.user.client.History;

/**
 * @author Brandon Donnelson
 * 
 * when sharing to some, it will encode the querystring (after the #), so I want to use characters that won't need special encoding.
 */
public class HistoryTokenUtils {
  
  /**
   * notes represent this as "?" And will make it "~"
   */
  public static final String PARAMSSTART = "~"; 
  
  /**
   * instead of "&", will make it "::"
   */
  public static final String PARAMSPLITTER = "::"; 
  
  /**
   * instead of "=" will make it ":"
   */
  public static final String PARAMKEYVALUESPLITTER = ":"; 

  /**
   * constructor
   */
  public HistoryTokenUtils() {
  }

  /**
   * get query string & parameters anywhere in an app
   * 
   * @return
   */
  public static HistoryTokenData getHistoryTokenData() {
    String historytoken = getHistoryTokenAnchor(History.getToken());
    HashMap<String, String> params = getHistoryTokenParameters(History.getToken());
    HistoryTokenData qsd = new HistoryTokenData();
    qsd.setHistoryData(historytoken, params);
    return qsd;
  }
  
  /**
   * get the full history token domain.tld/index.htm[historytoken?param1=a&param2=b]
   * @return
   */
  public static String getHistoryToken() {
    return History.getToken();
  }
  
  /**
   * issue a new historyToken event
   * 
   * @param historyToken
   * @param issueEvent
   */
  public static void newItem(String historyToken, boolean issueEvent) {
    History.newItem(historyToken, issueEvent);
  }
  
  /**
   * issue a new historyToken event with params
   * 
   * @param historyToken
   * @param params
   * @param issueEvent
   */
  public static void newItem(String historyToken, HashMap<String,String> params, boolean issueEvent) {
    HistoryTokenData qsd = new HistoryTokenData(historyToken, params);
    String ht = qsd.getHistoryTokenWithParameters();
    History.newItem(ht, issueEvent);
  }
  
  /**
   * issue a new historyToken events, using the current token in view but add these parameters
   * @param params
   */
  public static void newItem(HashMap<String,String> params, boolean issueEvent) {
    HistoryTokenData oldQsd = getHistoryTokenData();
    HistoryTokenData newQsd = new HistoryTokenData(oldQsd.getHistoryToken(), params);
    String ht = newQsd.getHistoryTokenWithParameters();
    History.newItem(ht, issueEvent);
  }
  

  /**
   * get historyToken by itself
   * 
   * like domain.tld#[historyToken]?params1=a&params2=b
   * 
   * @param historyToken
   * @return
   */
  private static String getHistoryTokenAnchor(String historyToken) {
    if (historyToken == null) {
      return null
          ;
    }
    // skip if there is no question mark
    if (historyToken.contains(PARAMSSTART) == false) {
      return historyToken;
    }

    // get just the historyToken/anchor tag
    String[] s = null; 
    if (historyToken.contains(PARAMSSTART) == true) {
      s = historyToken.split(PARAMSSTART);
    }
    
    if (s != null){
      historyToken = s[0];
    }

    return historyToken;
  }

  /**
   * get historyToken parameters
   * 
   * like domain.tld#historyToken?[?params1=a&params2=b]
   * 
   * @param historyToken (anchor tag)
   * @return HashMap of the parameters ([varName, var] OR [key, value])
   */
  private static HashMap<String, String> getHistoryTokenParameters(String historyToken) {

    // skip if there is no question mark
    if (historyToken.contains(PARAMSSTART) == false) {
      return null;
    }
    
    int index = historyToken.indexOf(PARAMSSTART) + 1;
    if (index == 0) {
      return null;
    }

    // get the sub string of parameters var=1&var2=2&var3=3... params1=a&params2=b...
    HashMap<String, String> params = null;
    try {
      String[] s = historyToken.substring(index, historyToken.length()).split(PARAMSPLITTER);
      params = new HashMap<String, String>();
      for (int i = 0; i < s.length; i++) {
        String[] ss = s[i].split(PARAMKEYVALUESPLITTER);
        params.put(ss[0], ss[1]);
      }
    } catch (Exception e) {
      params = null;
    }

    return params;
  }

}
