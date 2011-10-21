package com.gonevertical.server.domain;

import java.util.List;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.Version;

import com.gonevertical.server.EMF;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@Entity
@Embeddable
public class WalletItemData {

  private static final Logger log = Logger.getLogger(WalletItemData.class.getName());

  public static final EntityManager entityManager() {
    return EMF.get().createEntityManager();
  }

  public static WalletItemData findWalletItemData(String id) {
    //Long uid = UserData.getLoggedInUserId();
    if (id == null) {
      return null;
    }
    Key key = KeyFactory.stringToKey(id);
    EntityManager em = entityManager();
    try {
      WalletItemData e = em.find(WalletItemData.class, key);
      // if (e.getUserId() != uid) { // i'm not going to enforce this on a child here.
      //e = null;
      //}
      return e;
    } finally {
      em.close();
    }
  }

  public static long countAll() {
    EntityManager em = entityManager();
    try {
      // TODO change to JDO
      //return ((Number) em.createQuery("select count(o) from WalletItemData o").getSingleResult()).longValue();
    } finally {
      em.close();
    }
    return 0l;
  }

  public static long countWalletItemDataByUser() {
    Long uid = UserData.getLoggedInUserId();
    EntityManager em = entityManager();
    try {
      Query query = em.createQuery("select count(o) from WalletItemData o where o.userId =:userId");
      query.setParameter("userId", uid);
      return ((Number) query.getSingleResult()).longValue();
    } finally {
      em.close();
    }
  }

  public static List<WalletItemData> findWalletItemDataByUser() {
    Long uid = UserData.getLoggedInUserId();
    EntityManager em = entityManager();
    try {
      Query query = em.createQuery("select o from WalletItemData o where o.userId =:userId");
      query.setParameter("userId", uid);
      List<WalletItemData> list = (List<WalletItemData>) query.getResultList();
      list.size();
      return list;
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

  /**
   * the entity owner - the person who's logged in. Will set this on the client side. 
   * I'm not to concerned b/c its a child of a parent. Keep for reference in debugging.
   */
  private Long userId;

  private String name;
  
  private String contact;


  public void setId(Key parentKey, String id) {
    if (parentKey == null) {
      return;
    }
    if (id != null) {
      setId(id);
    } else {
      key = KeyFactory.createKey(parentKey, WalletItemData.class.getName(), null);
    }
  }
  public void setId(String id) {
    key = KeyFactory.stringToKey(id);
  }
  public String getId() {
    String id = null;
    if (key != null) {
      id = KeyFactory.keyToString(key);
    }
    return id;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }
  public Long getUserId() {
    return userId;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }
  public Integer getVersion() {
    return version;
  }

  public void setName(String name) {
    this.name = name;
  }
  public String getName() {
    return name;
  }

  public void setContact(String contact) {
    this.contact = contact;
  }
  public String getContact() {
    return contact;
  }


  public void persist() {

    // set the owner of this entity
    Long uid = UserData.getLoggedInUserId();
    setUserId(uid);
    
    EntityManager em = entityManager();
    try {
      em.persist(this);
    } finally {
      em.close();
    }
  }

  public void remove() {
    // for checking owner
    Long uid = UserData.getLoggedInUserId();

    EntityManager em = entityManager();
    try {
      WalletItemData e = em.find(WalletItemData.class, key);
      if (e != null && e.getUserId() != null && e.getUserId() != uid) { // make sure only the owner can delete
        log.severe("WalletItemData.remove() Error: Something weird going on in setting UID. e.getUserId=" + e.getUserId() + " uid=" + uid);
        return; // TODO maybe return back something
      }
      em.remove(this);
    } catch (Exception e) {
     e.printStackTrace();
    } finally {
      em.close();
    }
  }
}
