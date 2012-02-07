package com.gonevertical.server.data;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.gonevertical.server.EMF;
import com.gonevertical.server.RequestFactoryUtils;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@Entity
public class UserData {

  private static final Logger log = Logger.getLogger(UserData.class.getName());

  public static EntityManager getEntityManager() {
    EntityManager em = EMF.get().createEntityManager();
    return em;
  }

  private static User getGoogleUser() {
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn() == false) {
      return null;
    }
    User user = userService.getCurrentUser();
    return user;
  }

  public static Long getLoggedInUserId() {
    User user = getGoogleUser();
    if (user == null) {
      return null;
    }
    UserData userData = findUserDataByGoogleUserId(user.getUserId());
    if (userData == null) {
      return null;
    }
    if (userData.getId() == null) {
      return null;
    }

    Key key = userData.getKey();
    if (key == null) {
      return null;
    }

    return key.getId();
  }

  /**
   * id is base64 string of Key
   * @param id
   * @return
   */
  public static UserData findUserData(String id) {
    return RequestFactoryUtils.find(UserData.class, id);
  }

  public static UserData findUserDataByGoogleEmail(String googleEmail) {
    EntityManager em = getEntityManager();
    try {
      UserData ud = (UserData) em.createQuery("select o from " + UserData.class.getSimpleName() + " o").getSingleResult();
      return ud;
    } catch (Exception e) {
      log.log(Level.SEVERE, "Error: UserData.findUserDataByGoogleEmail(googleEmail): googleEmail=" + googleEmail, e);
      e.printStackTrace();
    } finally {
      em.close();
    }
    return null;
  }

  public static UserData findUserDataByGoogleUserId(String googleUserId) {
    EntityManager em = getEntityManager();
    try {
      UserData ud = (UserData) em.createQuery("select o from " + UserData.class.getSimpleName() + " o").getSingleResult();
      return ud;
    } catch (Exception e) {
      log.log(Level.SEVERE, "Error: UserData.findUserDataByGoogleUserId(googleUserId): googleUserId=" + googleUserId, e);
      e.printStackTrace();
    } finally {
      em.close();
    }
    return null;
  }

  public static UserData findLoggedInUserPrivileges() {
    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn() == false) {
      log.info("UserJdo.getUserData(): info: user is not logged in");
      return null;
    }
    
    User user = userService.getCurrentUser();
    String googleUserId = user.getUserId();
    
    // did we save this user in db yet?
    UserData ud = null;
    try {
      ud = findUserDataByGoogleUserId(googleUserId);
    } catch (Exception e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // set useful client vars
    ud.setIsSystemAdmin();
    
    return ud;
  }
  
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Key key;

  @Version
  @Column(name = "version")
  private Long version;
 
  private Date dateCreated;

  private String googleUserId;
  
  private String googleEmail;

  private String googleNickname;
  
  private String loginUrl;

  private String logoutUrl;
  
  @Transient
  private Boolean isSystemAdmin;
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("key=" + key + ",");
    sb.append("version=" + version + ",");
    sb.append("googleUserId=" + googleUserId + ",");
    sb.append("googleEmail=" + googleEmail + ",");
    sb.append("googleNickname=" + googleNickname + ",");
    sb.append("loginUrl=" + loginUrl + ",");
    sb.append("logoutUrl=" + logoutUrl + " ");
    return sb.toString();
  }

  public void setId(String id) {
    if (id == null) {
      return;
    }
    key = KeyFactory.stringToKey(id);
  }
  public String getId() {
    String id = null;
    if (key != null) {
      id = KeyFactory.keyToString(key);
    }
    return id;
  }

  public Key getKey() {
    return key;
  }

  public void setVersion(Long version) {
    this.version = version;
  }
  public Long getVersion() {
    if (version == null) {
      version = 0l;
    }
    return version;
  }
  private void incrementVersion() {
    if (version == null) {
      version = 0l;
    } else {
      version++;
    }
  }
  
  public void setDateCreated() {
    if (dateCreated == null) {
      dateCreated = new Date();
    }
  }
  public Date getDateCreated() {
    return dateCreated;
  }

  public void setGoogleUserId(String googleUserId) {
    this.googleUserId = googleUserId;
  }
  public String getGoogleUserId() {
    return googleUserId;
  }

  public void setGoogleEmail(String googleEmail) {
    if (googleEmail != null) {
      googleEmail = googleEmail.toLowerCase();
    }
    this.googleEmail = googleEmail;
  }
  public String getGoogleEmail() {
    return googleEmail;
  }

  public void setGoogleNickname(String googleNickname) {
    this.googleNickname = googleNickname;
  }
  public String getGoogleNickname() {
    return googleNickname;
  }

  public void setLoginUrl() {
    UserService userService = UserServiceFactory.getUserService();
    loginUrl = userService.createLoginURL("/");
  }
  public String getLoginUrl() {
    return loginUrl;
  }

  public void setLogoutUrl() {
    UserService userService = UserServiceFactory.getUserService();
    logoutUrl = userService.createLogoutURL("/");
  }
  public String getLogoutUrl() {
    return logoutUrl;
  }
  
  private void setIsSystemAdmin() {
    try {
      UserService userService = UserServiceFactory.getUserService();
      this.isSystemAdmin = userService.isUserAdmin();
    } catch (Exception e) {
      log.warning("UserJdo.setIsSystemAdmin() error: e=" + e);
    }
  }
  public boolean getIsSystemAdmin() {
    return isSystemAdmin;
  }

  public static UserData createUserData() {
    UserData userData = null; 

    UserService userService = UserServiceFactory.getUserService();
    if (userService.isUserLoggedIn() == false) {
      UserData r = new UserData();
      r.setLoginUrl();
      //r.setLogoutUrl(); // not needed
      return r;
    }

    User u = userService.getCurrentUser();
    if (u == null) {
      UserData r = new UserData();
      r.setLoginUrl();
      //r.setLogoutUrl(); // not needed
      return r;
    }

    // has the user been here before? lookup and or create
    userData = findUserDataByGoogleUserId(u.getUserId());
    if (userData == null) {
      userData = new UserData();
    }

    userData.setGoogleUserId(u.getUserId());
    userData.setGoogleEmail(u.getEmail());
    userData.setGoogleNickname(u.getNickname());
    //userData.setLoginUrl(); // not needed
    userData.setLogoutUrl();

    return userData.persist();
  }

  /**
   * can admin
   * @return
   */
  public boolean canAdmin() {
    //UserService userService = UserServiceFactory.getUserService();
    return true; //userService.isUserAdmin();
  }
  
  public UserData persist() {
    incrementVersion();
    setDateCreated();
    UserData r = RequestFactoryUtils.persist(this);
    return r;
  }
  public boolean remove() {
    return RequestFactoryUtils.removeByAdminOnly(this);
  }
 
}
