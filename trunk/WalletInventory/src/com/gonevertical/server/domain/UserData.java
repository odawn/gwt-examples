package com.gonevertical.server.domain;

import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.Transient;
import javax.persistence.Version;

import com.gonevertical.server.EMF;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@Entity
public class UserData {
  
  private static final Logger log = Logger.getLogger(WalletData.class.getName());

  public static final EntityManager entityManager() {
    return EMF.get().createEntityManager();
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

  public static UserData findUserData(String id) {
    if (id == null) {
      return null;
    }
    Key k = KeyFactory.stringToKey(id);
    EntityManager em = entityManager();
    try {
      UserData e = em.find(UserData.class, k);
      return e;
    } finally {
      em.close();
    }
  }

  public static UserData findUserDataByGoogleEmail(String googleEmail) {
    EntityManager em = entityManager();
    try {
      Query query = em.createQuery("select o from UserData o where o.googleEmail =:googleEmail");
      query.setParameter("googleEmail", googleEmail);
      List<UserData> list = (List<UserData>) query.getResultList();
      int size = list.size();
      if (size == 0) {
        return null;
      }
      Iterator<UserData> itr = list.iterator();
      UserData ud = itr.next();
      return ud;
    } finally {
      em.close();
    }
  }

  public static UserData findUserDataByGoogleUserId(String googleUserId) {
    EntityManager em = entityManager();
    try {
      Query query = em.createQuery("select o from UserData o where o.googleUserId =:googleUserId");
      query.setParameter("googleUserId", googleUserId);
      List<UserData> list = (List<UserData>) query.getResultList();
      int size = list.size();
      if (size == 0) {
        return null;
      }
      Iterator<UserData> itr = list.iterator();
      UserData ud = itr.next();
      return ud;
    } finally {
      em.close();
    }
  }


  @Id
  @Column(name = "key")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Key key;

  @Version
  @Column(name = "version")
  private Integer version;

  private String googleUserId;
  
  private String googleEmail;
  
  private String googleNickname;

  @Transient
  private String loginUrl;

  @Transient
  private String logoutUrl;


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
  
  private Key getKey() {
    return key;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }
  public Integer getVersion() {
    return version;
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

  public UserData persist() {
    EntityManager em = entityManager();
    try {
      em.persist(this);
    } finally {
      em.close();
    }
    return this;
  }

  public void remove() {
    
    Long uid = getLoggedInUserId();
    
    EntityManager em = entityManager();
    try {
      UserData e = em.find(UserData.class, key);
      if (e != null && e.getKey() != null && e.getKey().getId() != uid) {
        return;
      }
      em.remove(this);
    } finally {
      em.close();
    }
  }
}
