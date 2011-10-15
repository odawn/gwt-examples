package com.gonevertical.server.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Query;
import javax.persistence.Version;

import com.gonevertical.server.EMF;


@Entity
public class WalletItemData {
  
  
  public static final EntityManager entityManager() {
    return EMF.get().createEntityManager();
  }
  
  public static WalletItemData findWalletItemData(Long id) {
    Long uid = UserData.getLoggedInUserId();
    if (id == null) {
      return null;
    }
    EntityManager em = entityManager();
    try {
      WalletItemData e = em.find(WalletItemData.class, id);
      if (e.getUserId() != uid) {
        e = null;
      }
      return e;
    } finally {
      em.close();
    }
  }
  
  public static long countAll() {
    EntityManager em = entityManager();
    try {
      return ((Number) em.createQuery("select count(o) from WalletItemData o").getSingleResult()).longValue();
    } finally {
      em.close();
    }
  }
  
  public static long countWalletItemDataByUser() {
    Long uid = UserData.getLoggedInUserId();
    EntityManager em = entityManager();
    try {
      Query query = em.createQuery("select count(o) from WalletItemData o where o.userId=:userId");
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
      Query query =  em.createQuery("select o from WalletItemData o");
      query.setParameter("userId", uid);
      List<WalletItemData> list = query.getResultList();
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
   * the entity owner - the person who's logged in. Will set this on the client side. 
   * I'm not to concerned b/c its a child of a parent. Keep for reference in debugging.
   */
  private Long userId;
  
  private String name;
  
  private String contact;
  
  
  public void setId(Long id) {
    this.id = id;
  }
  public Long getId() {
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
    EntityManager em = entityManager();
    try {
      WalletData attached = em.find(WalletData.class, this.id);
      em.remove(attached);
    } finally {
      em.close();
    }
  }
}
