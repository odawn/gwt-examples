package org.gonevertical.dts.lib;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpPageGet {
  
  private static final Logger log = LoggerFactory.getLogger(HttpPageGet.class);

  /**
   * 
   * URL url = new URL("http://hostname:80/cgi");
   * 
   * Construct data
   * String data = URLEncoder.encode("key1", "UTF-8") + "=" + URLEncoder.encode("value1", "UTF-8");
   * data += "&" + URLEncoder.encode("key2", "UTF-8") + "=" + URLEncoder.encode("value2", "UTF-8");
   * 
   * @return
   */
  public static String getPage(URL url, String data) {
    if (url == null) {
      return null;
    }
    String page = "";
    try {
      URLConnection conn = url.openConnection();
      conn.setDoOutput(true);
      OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
      if (data != null) {
        wr.write(data);
      }
      wr.flush();
      BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
      String line = "";
      while ((line = rd.readLine()) != null) {
       page += line;
      }
      wr.close();
      rd.close();
    } catch (Exception e) {
      e.printStackTrace();
      log.error("error", e);
    }
    return page;
  }

}
