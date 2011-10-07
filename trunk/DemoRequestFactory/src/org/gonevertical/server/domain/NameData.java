package org.gonevertical.server.domain;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

import org.gonevertical.server.EMF;

@Entity
public class NameData {

  public static final EntityManager entityManager() {
    return EMF.get().createEntityManager();
  }

  public static long count() {
    EntityManager em = entityManager();
    try {
      return ((Number) em.createQuery("select count(o) from " + NameData.class.getSimpleName() + " o")
          .getSingleResult()).longValue();
    } finally {
      em.close();
    }
  }

  /**
   * It is mandatory to have a find[EntityClass] here!!!
   * 
   * @param id
   * @return
   */
  public static NameData findNameData(Long id) {
    if (id == null) {
      return null;
    }
    EntityManager em = entityManager();
    try {
      NameData employee = em.find(NameData.class, id);
      return employee;
    } finally {
      em.close();
    }
  }

  public static List<NameData> query() {
    EntityManager em = entityManager();
    try {
      List<NameData> list = em.createQuery("select o from " + NameData.class.getSimpleName() + " o")
          .getResultList();
      // force to get all the Names
      list.size();
      return list;
    } finally {
      em.close();
    }
  }

  public static List<NameData> query(int firstResult, int maxResults) {
    EntityManager em = entityManager();
    try {
      List<NameData> resultList = em.createQuery("select o from " + NameData.class.getSimpleName() + " o")
          .setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
      // force it to materialize
      resultList.size();
      return resultList;
    } finally {
      em.close();
    }
  }


  @Id
  @Column(name = "id")
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /**
   * Used by RequestFactory to infer if an entity has changed
   */
  @Version
  @Column(name = "version")
  private Integer version;

  private String name;

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

  public void setName(String name) {
    this.name = name;
  }
  public String getName() {
    return name;
  }



  public void persist() {
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
      NameData attached = em.find(NameData.class, this.id);
      em.remove(attached);
    } finally {
      em.close();
    }
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("Id: ").append(getId()).append(", ");
    sb.append("name: ").append(getName()).append(", ");
    return sb.toString();
  }

}
