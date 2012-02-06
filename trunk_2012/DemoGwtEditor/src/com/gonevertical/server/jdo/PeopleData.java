package com.gonevertical.server.jdo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import javax.jdo.annotations.Element;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.gonevertical.server.Filter;
import com.gonevertical.server.RequestFactoryUtils;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;

@PersistenceCapable
public class PeopleData {

  private static final Logger log = Logger.getLogger(PeopleData.class.getName());

  /**
   * id is base64 string of Key
   * @param id
   * @return
   */
  public static PeopleData findPeopleData(String id) {
    PeopleData d = RequestFactoryUtils.find(PeopleData.class, id);
    return d;
  }

  public static List<PeopleData> findPeopleData(long start, long end) {
    ArrayList<Filter> tfilter = null;
    List<PeopleData> list = RequestFactoryUtils.findList(PeopleData.class, tfilter, start, end);
    return list;
  }
  
  public static Long findCount() {
    ArrayList<Filter> tfilter = null;
    Long count = RequestFactoryUtils.findCount(PeopleData.class, tfilter);
    return count;
  }

  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Key key;

  @Persistent
  private Long version;
  
  @Persistent
  private Date dateCreated;

  @Persistent
  private Boolean active;
  
  @Persistent 
  private String nameFirst;
  
  @Persistent 
  private String nameLast;
  
  @Persistent
  private Integer gender;
  
  @Persistent
  private Text note;
  
  @Persistent
  private HashSet<String> search;
  
  /**
   * owned collection 
   *  - Note to use request factory ListEditor for todo, both attributes have to be set below.
   */
  @Persistent(defaultFetchGroup = "true", dependentElement = "true")
  private List<TodoData> todos;

  
  @NotPersistent
  private List<TodoData> deleteOrphans;
  
  
  
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
  
  public void setActive(boolean active) {
    this.active = active;
  }
  public Boolean getActive() {
    return active;
  }
  
  public void setNameFirst(String nameFirst) {
    this.nameFirst = nameFirst;
    setSearch();
  }
  public String getNameFirst() {
    return nameFirst;
  }
  
  public void setNameLast(String nameLast) {
    this.nameLast = nameLast;
    setSearch();
  }
  public String getNameLast() {
    return nameLast;
  }
  
  public void setGender(Integer gender) {
    this.gender = gender;
  }
  public Integer getGender() {
    return gender;
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
  
  public void setTodos(List<TodoData> todos) {
    if ((todos == null || todos.size() == 0)  && this.todos != null) { // TODO cause transient error, can't do this in the same transaction
     setOrphans(todos);
    } else {
      HashSet<TodoData> difference = new HashSet<TodoData>(this.todos);
      difference.removeAll(todos);
      setOrphans(difference);
    }
    this.todos = todos;
  }
  
  private void setOrphans(HashSet<TodoData> orphans) {
    if (orphans == null || orphans.size() == 0) {
      return;
    }
    ArrayList<TodoData> a = new ArrayList<TodoData>();
    Iterator<TodoData> itr = orphans.iterator();
    while(itr.hasNext()) {
      a.add(itr.next());
    }
    deleteOrphans = a;
  }

  private void setOrphans(List<TodoData> orphans) {
    deleteOrphans = orphans;
  }

  private void removeOrphans() {
    if (deleteOrphans == null) {
      return;
    }
    Iterator<TodoData> itr = deleteOrphans.iterator();
    while(itr.hasNext()) {
      TodoData e = itr.next();
      removeTodo(e);
    }
  }
  
  /**
   * only delete if this child owned by parent
   * @param e
   */
  private void removeTodo(TodoData e) {
    if (e == null) {
      return;
    }
    Key parent = this.key;
    Key child = e.getKey().getParent();
    if (parent != null && child != null && parent.equals(child) == true) {
      RequestFactoryUtils.remove(e);
    }
  }



  public List<TodoData> getTodos() {
    return todos;
  }

  public PeopleData persist() {
    incrementVersion();
    setDateCreated();   
    PeopleData r = RequestFactoryUtils.persist(this);
    return r;
  }
 

  public boolean remove() {
    return RequestFactoryUtils.removeByAdminOnly(this);
  }
 
  /**
   * this is a bit redundant when both names are set. Good for now, and good for example. No matter, its fast
   */
  private void setSearch() {
    HashSet<String> hs = new HashSet<String>();

    if (nameLast != null) {
      String ln = nameLast;
      if (ln != null && ln.trim().length() > 0) {
        hs.add(ln.toLowerCase());

        if (ln.length() > 1) {

          for (int i=1; i < ln.length(); i++) {
            String s = ln.substring(0, i);
            hs.add(s.toLowerCase());
          }
        }
      }
    }

    if (nameFirst != null) {
      String fn = nameFirst;
      if (fn != null && fn.trim().length() > 0) {
        hs.add(fn.toLowerCase());

        if (fn.length() > 1) {
          for (int i=1; i < fn.length(); i++) {
            String s = fn.substring(0, i);
            hs.add(s.toLowerCase());
          }
        }
      }
    }
    search = hs;
  }
}
