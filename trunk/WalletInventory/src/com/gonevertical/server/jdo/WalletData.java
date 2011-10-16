package com.gonevertical.server.jdo;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import javax.jdo.annotations.Extension;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;
import javax.jdo.annotations.Version;
import javax.jdo.annotations.VersionStrategy;

import com.gonevertical.server.PMF;

@PersistenceCapable
@Version(strategy = VersionStrategy.VERSION_NUMBER, extensions = { @Extension(vendorName = "datanucleus", key = "key", value = "version") })
public class WalletData {
  
  public static PersistenceManager getPersistenceManager() {
    return PMF.get().getPersistenceManager();
  }
  
  public static WalletData findWalletData(Long id) {
    Long uid = UserData.getLoggedInUserId();
    if (id == null) {
      return null;
    }
    PersistenceManager pm = getPersistenceManager();
    try {
      WalletData e = pm.getObjectById(WalletData.class, id);
      if (e.getUserId() != uid) {
        e = null;
      }
      return e;
    } finally {
      pm.close();
    }
  }
  
  public static Long countAll() {
    PersistenceManager pm = getPersistenceManager();
    try {
      // TODO change to JDO
      //return ((Number) em.createQuery("select count(o) from WalletData o").getSingleResult()).longValue();
    } finally {
      pm.close();
    }
    return 0l;
  }
  
  public static Long countWalletDataByUser() {
    Long uid = UserData.getLoggedInUserId();
    PersistenceManager pm = getPersistenceManager();
    try {
      javax.jdo.Query query = pm.newQuery("select count(o) from "+ WalletData.class.getName() + " o");
      query.setFilter("o.userId=\"" + uid + "\"");
      Long r = (Long) query.execute();
      return r;
    } finally {
      pm.close();
    }
  }
  
  public static List<WalletData> findWalletDataByUser() {
    Long uid = UserData.getLoggedInUserId();
    PersistenceManager pm = getPersistenceManager();
    try {
      javax.jdo.Query query = pm.newQuery("select o from WalletData o");
      query.setFilter("o.userId==\"" + uid + "\"");
      List<WalletData> list = (List<WalletData>) query.execute();
      list.size();
      return list;
    } finally {
      pm.close();
    }
  }

  
  
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Long id;
  
  @Persistent
  private Integer version;
 
  /**
   * entity owner - the person who's logged in
   */
  @Persistent
  private Long userId; 
  
  @Persistent
  private String name;
  
  @Persistent(defaultFetchGroup = "true", dependentElement = "true")  
  private List<WalletItemData> items;
 
  
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
    this.items = items;
  }
  public List<WalletItemData> getItems() {
    return items;
  }
  
  public WalletData persist() {
    Long uid = UserData.getLoggedInUserId();
    setUserId(uid);
    
    PersistenceManager pm = getPersistenceManager();
    Transaction tx = pm.currentTransaction();
    try {
      tx.begin();
      pm.makePersistent(this);
      tx.commit();
    } finally {
      pm.close();
    }
    return this;
  }

  public void remove() {
    PersistenceManager pm = getPersistenceManager();
    Transaction tx = pm.currentTransaction();
    try {
      WalletData e = pm.getObjectById(WalletData.class, this.id);
      tx.begin();
      pm.deletePersistent(e);
      tx.commit();
    } finally {
      pm.close();
    }
  }
  
  
}
