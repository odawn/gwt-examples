package com.gonevertical.server.jdo;

import java.util.ArrayList;
import java.util.Iterator;
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
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@PersistenceCapable
@Version(strategy=VersionStrategy.VERSION_NUMBER, column="version", extensions={@Extension(vendorName="datanucleus", key="key", value="version")})
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
      // TODO
      //javax.jdo.Query query = pm.newQuery("select count(o) from WalletData o  where o.userId==\"" + uid + "\"");
      //Long r = (Long) query.execute();
      return 0l;
    } finally {
      pm.close();
    }
  }
  
  public static List<WalletData> findWalletDataByUser() {
    Long uid = UserData.getLoggedInUserId();
    PersistenceManager pm = getPersistenceManager();
    try {
      javax.jdo.Query query = pm.newQuery("select from " + WalletData.class.getName());
      query.setFilter("userId==\"" + uid + "\"");
      List<WalletData> list = (List<WalletData>) query.execute();
      list.size();
      return list;
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      pm.close();
    }
    return null;
  }

  
  
  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Key key;
  
  @Persistent
  private Long version;
 
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
    if (id == null) {
      return;
    }
    this.key = KeyFactory.createKey(WalletData.class.getName(), id);
  }
  public Long getId() {
    Long id = null;
    if (key != null) {
      id = key.getId();
    }
    return id;
  }
  
  public void setVersion(Long version) {
    this.version = version;
  }
  public Long getVersion() {
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
    
    // TODO I'm not sure request factory will do this or not yet.
    ArrayList<WalletItemData> a = new ArrayList<WalletItemData>();
    Iterator<WalletItemData> itr = items.iterator();
    while(itr.hasNext()) {
      WalletItemData d = itr.next();
      if (d.getId() != null && d.getId() > 0) { // null on id to increment
        // need parentKey, to reference the owned entities
        d.setId(key, d.getId());
      }
      a.add(d);
    }
    
    this.items = a;
  }
  public List<WalletItemData> getItems() {
    return items;
  }
  
  public WalletData persist() {
    Long uid = UserData.getLoggedInUserId();
    setUserId(uid);
    
    // JPA @Version does this automatically, but JDO @Version is not working like that. Not sure why.
    version++;
    
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
      WalletData e = pm.getObjectById(WalletData.class, key);
      tx.begin();
      pm.deletePersistent(e);
      tx.commit();
    } finally {
      pm.close();
    }
  }
  
  
}
