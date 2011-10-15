package com.gonevertical.server.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.Version;

import com.gonevertical.server.EMF;

@Entity
public class WalletData {
  
  public static final EntityManager entityManager() {
    return EMF.get().createEntityManager();
  }
  
  public static WalletData findWalletData(Long id) {
    Long uid = UserData.getLoggedInUserId();
    if (id == null) {
      return null;
    }
    EntityManager em = entityManager();
    try {
      WalletData e = em.find(WalletData.class, id);
      if (e.getUserId() != uid) {
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
      Query query = em.createQuery("select count(o) from WalletData o where o.userId=:userId");
      query.setParameter("userId", uid);
      return ((Number) query.getSingleResult()).longValue();
    } finally {
      em.close();
    }
  }
  
  public static List<WalletData> findWalletDataByUser() {
    Long uid = UserData.getLoggedInUserId();
    EntityManager em = entityManager();
    try {
      Query query =  em.createQuery("select o from WalletData o");
      query.setParameter("userId", uid);
      List<WalletData> list = query.getResultList();
      list.size();
      return list;
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
 
  /**
   * entity owner - the person who's logged in
   */
  private Long userId; 
  
  private String name;
  
  @ElementCollection  
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
