package com.gonevertical.server.domain;

import java.util.Iterator;
import java.util.List;

import javax.jdo.annotations.NotPersistent;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.Version;

import com.gonevertical.server.EMF;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@Entity
public class UserData {

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
    return userData.getId();
  }

  public static UserData findUserData(Long id) {
    if (id == null) {
      return null;
    }
    EntityManager em = entityManager();
    try {
      UserData e = em.find(UserData.class, id);
      return e;
    } finally {
      em.close();
    }
  }

  public static UserData findUserDataByGoogleEmail(String googleEmail) {
    EntityManager em = entityManager();
    try {
      Query query = em.createQuery("select o from UserData o WHERE o.googleEmail =:googleEmail");
      query.setFirstResult(0);
      query.setMaxResults(1);
      query.setParameter("googleEmail", googleEmail);
      List<UserData> list = query.getResultList();
      long size = list.size();
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
      Query query = em.createQuery("select o from UserData o WHERE o.googleUserId =:googleUserId");
      query.setFirstResult(0);
      query.setMaxResults(1);
      query.setParameter("googleUserId", googleUserId);
      List<UserData> list = query.getResultList();
      long size = list.size();
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
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Version
  @Column(name = "version")
  private Integer version;

  private String googleUserId;

  private String googleEmail;
  
  private String googleNickname;
  
  @NotPersistent
  private String loginUrl;
  
  @NotPersistent
  private String logoutUrl;


  public void setId(Long id) {
    this.id = id;
  }
  public Long getId() {
    return id;
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
    EntityManager em = entityManager();
    try {
      WalletData attached = em.find(WalletData.class, this.id);
      em.remove(attached);
    } finally {
      em.close();
    }
  }
}
