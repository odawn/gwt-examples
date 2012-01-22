package org.gonevertical.dts.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDbData {
	
  private static final Logger log = LoggerFactory.getLogger(UserDbData.class);

  private String userName = null;
  
  private String password = null;
  
  /**
   * ips the user has access to
   */
  private String[] host = null;
  
  public UserDbData() {
  }
  
  public void setData(String userName, String password) {
    this.userName = userName;
    this.password = password;
  }
  
  public void setHost(String[] host) {
    this.host = host;
  }
  
  public String getUserName() {
    return userName;
  }
  
  public String getPassword() {
    return password;
  }

  public String[] getHost() {
    return host;
  }
  
  
}
