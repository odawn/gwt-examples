package com.gonevertical.server.jdo;

import java.util.ArrayList;
import java.util.Collection;
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

  public static WalletData findWalletData(String id) {
    Long uid = UserData.getLoggedInUserId();
    if (id == null) {
      return null;
    }
    Key key = KeyFactory.stringToKey(id);
    PersistenceManager pm = getPersistenceManager();
    try {
      WalletData e = pm.getObjectById(WalletData.class, key);
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

    String qfilter = "userId==" + uid + "";

    PersistenceManager pm = getPersistenceManager();
    try {
      javax.jdo.Query query = pm.newQuery("select from " + WalletData.class.getName());
      query.setFilter(qfilter);
      List<WalletData> list = (List<WalletData>) query.execute();
      //int c = list.size();
      
      // TODO - This will get all the data including children
      //   But request factory won't transport the children back. Now what......
      List<WalletData> r = (List<WalletData>) pm.detachCopyAll(list);
      
      return r;
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
    
    // TODO there seems like there is a better way to do this. moving on for now
    ArrayList<WalletItemData> a = new ArrayList<WalletItemData>();
    Iterator<WalletItemData> itr = items.iterator();
    while(itr.hasNext()) {
      WalletItemData d = itr.next();
      if (d.getId() != null) {
        d.setId(key, d.getId());
      }
      a.add(d);
    }
    this.items = items;
  }
  public List<WalletItemData> getItems() {
    return items;
  }

  public WalletData persist() {

    // set the owner of this entity
    userId = UserData.getLoggedInUserId();

    // JPA @Version does this automatically, but JDO @Version is not working like that. Not sure why.
    if (version == null) {
      version = 0l;
    }
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
