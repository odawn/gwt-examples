package com.gonevertical.server.domain;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.Query;
import javax.persistence.Version;

import com.gonevertical.server.EMF;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@Entity
public class WalletData {

  private static final Logger log = Logger.getLogger(WalletData.class.getName());

  public static final EntityManager entityManager() {
    return EMF.get().createEntityManager();
  }
  public static WalletData findWalletData(String id) {
    Long uid = UserData.getLoggedInUserId();
    if (id == null) {
      return null;
    }
    Key key = KeyFactory.stringToKey(id);
    EntityManager em = entityManager();
    try {
      WalletData e = em.find(WalletData.class, key);
      if (e.getUserId() != uid) { // does the uid really own this wallet
        e = null;
      }
      return e;
    } finally {
      em.close();
    }
  }

  public static Long countAll() {
    EntityManager em = entityManager();
    try {
      return ((Number) em.createQuery("select count(o) from WalletData o").getSingleResult()).longValue();
    } finally {
      em.close();
    }
  }

  public static Long countWalletDataByUser() {
    Long uid = UserData.getLoggedInUserId();
    EntityManager em = entityManager();
    try {
      Query query = em.createQuery("select count(o) from WalletData o where o.userId =:userId");
      query.setParameter("userId", uid);
      Long r = (Long) query.getSingleResult();
      return r;
    } finally {
      em.close();
    }
  }

  /**
   * get list of wallets for user
   * Note: use .with("childEntity") to get children
   * 
   * @return
   */
  public static List<WalletData> findWalletDataByUser() {
    Long uid = UserData.getLoggedInUserId();

    EntityManager em = entityManager();
    try {
      Query query = em.createQuery("select o from WalletData o where o.userId =:userId");
      query.setParameter("userId", uid);
      List<WalletData> list = (List<WalletData>) query.getResultList();
      int c = list.size(); // org.datanucleus.exceptions.NucleusUserException: Object Manager has been closed
      return list;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      em.close();
    }
    return null;
  }


  
  /**
   * primary id
   * transposed to web safe id (base64) for transport
   */
  @Id
  @Column(name = "key")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Key key;

  /**
   * mandatory parameter used with request factory
   */
  @Version
  @Column(name = "version")
  private Integer version;

  /**
   * entity owner - the person who's logged in
   */
  private Long userId; 

  /**
   * name of the wallet
   */
  private String name;

  /**
   * items in the wallet
   * owned collection entities
   */
  @ElementCollection(targetClass=WalletItemData.class)
  @CollectionTable(name="items")
  @MapKey(name="key")
  private List<WalletItemData> items;


  
  
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

  public void setVersion(Integer version) {
    this.version = version;
  }
  public Integer getVersion() {
    return version;
  }

  public void setUserId(Long userId) {
    this.userId = userId;
  }
  public Long getUserId() {
    return userId;
  }

  public void setName(String name) {
    this.name = name;
  }
  public String getName() {
    return name;
  }

  public void setItems(List<WalletItemData> items) {
    if (items == null) {
      this.items = null;
      return;
    }

    // set owner, so the owner can remove later
    Long uid = UserData.getLoggedInUserId();

    Iterator<WalletItemData> itr = items.iterator();
    if (itr == null) {
      this.items = null;
      return;
    }
    ArrayList<WalletItemData> a = new ArrayList<WalletItemData>();
    while(itr.hasNext()) {
      WalletItemData d = itr.next();
      if (d != null && d.getId() != null) {
        d.setId(key, d.getId());
      }
      if (d != null) {
        d.setUserId(uid);
        a.add(d);
      }
    }
    this.items = items;
  }
  public List<WalletItemData> getItems() {
    return items;
  }

  public WalletData persist() {

    // set the owner of this entity
    Long uid = UserData.getLoggedInUserId();

    EntityManager em = entityManager();
    try {

      // make sure the owner is making the modification
      if (key != null) { 
        WalletData e = em.find(WalletData.class, key);
        if (e != null && e.getUserId() != uid) {
          log.severe("WalletData.persist() Error: Something weird going on in setting UID. e.getUserId=" + e.getUserId() + " uid=" + uid);
          return null;
        }
      }

      setUserId(uid);
      em.persist(this);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      em.close();
    }
    return this;
  }

  public void remove() {

    // for checking owner
    Long uid = UserData.getLoggedInUserId();

    EntityManager em = entityManager();
    try {
      WalletData e = em.find(WalletData.class, key);
      if (e != null && e.getUserId() != uid) { // make sure only the owner can delete
        log.severe("WalletData.remove() Error: Something weird going on in setting UID. e.getUserId=" + e.getUserId() + " uid=" + uid);
        return; // TODO maybe return back something
      }
      em.remove(this);
    } finally {
      em.close();
    }
  }


}
