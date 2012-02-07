package com.gonevertical.server.data;

import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

import com.gonevertical.client.app.requestfactory.dto.TodoDataProxy;
import com.gonevertical.server.EMF;
import com.gonevertical.server.RequestFactoryUtils;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.api.datastore.Transaction;
import com.google.web.bindery.requestfactory.shared.EntityProxyId;

@Entity
public class TodoData {

  private static final Logger log = Logger.getLogger(TodoData.class.getName());

  public static EntityManager getEntityManager() {
    return EMF.get().createEntityManager();
  }

  /**
   * id is base64 string of Key
   * @param id
   * @return
   */
  public static TodoData findTodoData(String id) {
    return RequestFactoryUtils.find(TodoData.class, id);
  }

  public static Boolean remove(String id) {

    Key key = KeyFactory.stringToKey(id);
    TodoData o = RequestFactoryUtils.find(TodoData.class, key); 

    Key parent = o.getKey().getParent();
    if (parent.equals(key.getParent()) == false) {
      return false;
    }

    Boolean success = false;
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    Transaction txn = datastore.beginTransaction();
    try {
      datastore.delete(key);
      txn.commit();
      success = true;
    } finally {
      if (txn.isActive()) {
        txn.rollback();
      }
    }
    return success;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Key key;

  @Version
  @Column(name = "version")
  private Long version;

  private Date dateCreated;

  private String todo;

  private Text note;

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("key=" + key + ",");
    sb.append("version=" + version + ",");
    // TODO
    return sb.toString();
  }

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

  public Key getKey() {
    return key;
  }

  public void setVersion(Long version) {
    this.version = version;
  }
  public Long getVersion() {
    if (version == null) {
      version = 0l;
    }
    return version;
  }
  private void incrementVersion() {
    if (version == null) {
      version = 0l;
    } else {
      version++;
    }
  }

  public void setDateCreated() {
    if (dateCreated == null) {
      dateCreated = new Date();
    }
  }
  public Date getDateCreated() {
    return dateCreated;
  }

  public void setTodo(String todo) {
    this.todo = todo;
  }
  public String getTodo() {
    return todo;
  }

  public void setNote(String note) {
    if (note == null) {
      this.note = null;
    } else {
      this.note = new Text(note);
    }
  }
  public String getNote() {
    if (note == null) {
      return null;
    } else {
      return note.getValue();
    }
  }

  public TodoData persist() {
    incrementVersion();
    setDateCreated();
    TodoData r = RequestFactoryUtils.persist(this);
    return r;
  }
  public boolean remove() {
    return RequestFactoryUtils.removeByAdminOnly(this);
  }

  @Override
  public int hashCode() {
    int hash = 7;
    hash = 31 * hash + key.hashCode();
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if ((obj == null) || (obj.getClass() != this.getClass())) {
      return false;
    }
    TodoData r = (TodoData) obj;
    boolean b = false;
    if (key != null && r.key != null) {
      if (key.equals(key) == true) {
        b = true;
      }
    }
    return b;
  }

}
