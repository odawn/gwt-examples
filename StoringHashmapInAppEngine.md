
&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;



# Storing a HashMap into an Entity as a HashSet #
> What I do is setup an ancestor collection of HashSet{HashMapJdo} in my entity. The Hashset is a unique set of objects based on hashCode(), which I use in the class HashMapJdo below.

  * this is what I store the mapped key and values into and becomes my HashSet{HashMapJdo} in the parent entity.
```
import java.util.HashMap;
import java.util.HashSet;
import java.util.logging.Logger;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.gonevertical.server.ServerPersistence;
import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class HashMapJdo {

  @NotPersistent
  private static final Logger log = Logger.getLogger(HashMapJdo.class.getName());

  @NotPersistent
  private ServerPersistence sp;

  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Key key;

  @Persistent
  private String hashMapKey;

  @Persistent
  private String hashMapValue;

  /**
   * init
   */
  public HashMapJdo() {
  }

  public void setData(String hashMapKey, String hashMapValue) {
    this.hashMapKey = hashMapKey;
    this.hashMapValue = hashMapValue;
  }

  public void setData(Integer hashMapKey, String hashMapValue) {
    if (hashMapKey != null) {
      this.hashMapKey = Integer.toString(hashMapKey);
    }
    this.hashMapValue = hashMapValue;
  }

  public String getHashMapKey() {
    return hashMapKey;
  }
  public String getHashMapValue() {
    return hashMapValue;
  }

  @Override
  public int hashCode() {
    int hash = 1;
    if (hashMapKey != null) {
      hash = hash * 31 + hashMapKey.hashCode();
    }
    return hash;
  }

  public static HashSet<HashMapJdo> getHashSetIntKey(HashMap<Integer, String> hm) {
    if (hm == null || hm.size() == 0) {
      return null;
    }
    HashSet<HashMapJdo> hs = new HashSet<HashMapJdo>();
    for (Integer key : hm.keySet()) {
      HashMapJdo hmj = new HashMapJdo();
      hmj.setData(key, hm.get(key));
      hs.add(hmj);
    }
    return hs;
  }
  
  public String toString() {
    String s = "";
    s += "key=" + key + " ";
    s += "hashMapKey=" + hashMapKey + " ";
    s += "hashMapValue=" + hashMapValue + " ";
    return s;
  }

}
```

## How I Use It ##
> In my TaskJdo persistance entity storage I use it like this.

  * This is my TaskJdo Class which I use for monitoring tasks generated in App Engine.
```
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.gonevertical.client.global.generic.TextData;
import com.gonevertical.client.global.task.TaskData;
import com.gonevertical.client.global.task.TaskData.KeyType;
import com.gonevertical.server.ServerPersistence;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Text;

@PersistenceCapable
public class TaskJdo {

  @NotPersistent
  private static final Logger log = Logger.getLogger(TaskJdo.class.getName());

  @NotPersistent
  private ServerPersistence sp;

  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Key key;

  @Persistent
  private Long userCreated;

  @Persistent
  private Date dateCreated;

  @Persistent
  private String name;

  @Persistent
  private Boolean finished;

  @Persistent
  private Text note;

  @Persistent
  private String completed;

  @Persistent
  private Date dateFinished;

  /**
  * this is my hashmap stored as an ancestor collection HashSet
  */
  @Persistent
  private HashSet<HashMapJdo> hashmap;

  /**
   * init
   * @param sp
   */
  public TaskJdo(ServerPersistence sp) {
    this.sp = sp;
  }
  private TaskJdo() {
  }

  private void set(ServerPersistence sp) {
    this.sp = sp;
  }

  private void setId(Long id) {
    if (id == null) {
      dateCreated = new Date();
      return;
    }
    key = KeyFactory.createKey(TaskJdo.class.getSimpleName(), id);
  }
  private Long getId() {
    Long id = key.getId();
    return id;
  }

  private void setUserId() {
    if (sp.getUserData() == null) {
      return;
    }
    this.userCreated = sp.getUserData().getId();
  }

  public Long getUserCreated() {
    return userCreated;
  }

  /*
  public Boolean getIsPublic() {
    return isPublic;
  }

  public Boolean getIsPrivate(){ 
    return isPrivate;
  }
   */

  public TaskJdo queryJdo(Long id) {
    PersistenceManager pm = sp.getPersistenceManager();
    TaskJdo o = null;;
    try {
      o = pm.getObjectById(TaskJdo.class, id);
      //if (sp.getUserCan(o.getIsPrivate(), o.getIsPublic(), id) == false) {
      //o = null;
      //}
    } catch (Exception e) {
    }
    return o;
  }

  public TaskJdo getThis() {
    return this;
  }

  public TaskData queryData(Long id) {
    if (id == null) {
      return null;
    }
    PersistenceManager pm = sp.getPersistenceManager();
    TaskJdo tj = null;
    try {
      tj = pm.getObjectById(TaskJdo.class, id);
    } catch (Exception e) {
    }
    if (tj == null) {
      return null;
    }
    TaskData taskData = tj.getData();

    // can view?
    /*
    if (taskData.getUserCreated() != sp.getUserId()) {
      return null;
    }
    */

    return taskData;
  }

  public boolean deleteData(Long id) {
    if (id == null) {
      return true;
    }
    boolean success = false;
    PersistenceManager pm = sp.getPersistenceManager();
    Transaction tx = pm.currentTransaction();
    try {
      TaskJdo tj = pm.getObjectById(TaskJdo.class, id);
      TaskData td = tj.getData();
      if (td.getUserCreated() == sp.getUserId()) { // user can delete
        tx.begin();
        pm.deletePersistent(tj);
        tx.commit();
      }
      success = true;
    } catch (Exception e) {
      success = false;
    } finally {
      if (tx.isActive()) {
        tx.rollback();
      }
      pm.close();
    }
    return success;
  }

  public TaskData saveData(TaskData data) throws Exception {
    PersistenceManager pm = sp.getPersistenceManager();
    Transaction tx = pm.currentTransaction();
    try {
      if (data.getId() != null) {
        TaskJdo update = pm.getObjectById(TaskJdo.class, data.getId());
        update.set(sp);
        update.setData(data);
        tx.begin();
        pm.makePersistent(update);
        tx.commit();
        data.setId(update.getId());
        log.info("saveData(): info: update: taskJdo=" + update.toString());
      } else {
        setData(data);
        tx.begin();
        pm.makePersistent(this);
        tx.commit();
        data.setId(getId());
        log.info("saveData(): info: insert: taskJdo=" + this.toString());
      }
    } catch (Exception e) {
      log.log(Level.SEVERE, "save(): ", e);
      e.printStackTrace();
      throw new Exception("oops");
    } finally {
      if (tx.isActive()) {
        tx.rollback();
      }
      pm.close();
    }
    return data;
  }

  public void setData(TaskData data) {
    setId(data.getId());
    setUserId();
    name = data.getName();
    finished = data.getFinished();
    setNote(data.getNote());
    setCompleted(data.getCompleted());
    dateFinished = data.getDateFinished();
    this.hashmap = getHashSet(data.getHashMap());
  }

  private HashSet<HashMapJdo> getHashSet(HashMap<KeyType, String> hm) {
    if (hm == null || hm.size() == 0) {
      return null;
    }
    HashSet<HashMapJdo> hs = new HashSet<HashMapJdo>();
    for (TaskData.KeyType key : hm.keySet()) {
      String k = key.value();
      HashMapJdo hmj = new HashMapJdo();
      hmj.setData(k, hm.get(key));
      hs.add(hmj);
    }
    return hs;
  }
  
  private HashMap<KeyType, String> getHashMap() {
    if (hashmap == null || hashmap.size() == 0) {
      return null;
    }
    HashMap<KeyType, String> hm = new HashMap<TaskData.KeyType, String>();
    Iterator<HashMapJdo> itr = hashmap.iterator();
    while(itr.hasNext()) {
      HashMapJdo j = itr.next();
      hm.put(TaskData.KeyType.fromValue(j.getHashMapKey()), j.getHashMapValue());
    }
    return hm;
  }

  public TaskData getData() {
    TaskData data = new TaskData();
    data.setId(getId());
    data.setUserCreated(userCreated);
    data.setDateCreated(dateCreated);
    data.setName(name);
    data.setFinished(finished);
    data.setNote(getNote());
    data.setCompleted(getCompleted());
    data.setDateFinished(dateFinished);
    data.setHashMap(getHashMap());
    return data;
  }

  private void setCompleted(BigDecimal completed) {
    if (completed == null) {
      this.completed = null;
      return;
    }
    this.completed = completed.toString();
  }
  private BigDecimal getCompleted() {
    if (completed == null) {
      return null;
    }
    return new BigDecimal(completed);
  }

  private void setNote(TextData note) {
    if (note == null) {
      this.note = null;
      return;
    }
    this.note = new Text(note.getValue());
  }
  private TextData getNote() {
    if (note == null) {
      return null;
    }
    return new TextData(note.getValue());
  }
  
  public String toString() {
    String s = "";
    s += "key=" + key + " ";
    s += "userCreated=" + userCreated + " ";
    s += "dateCreated=" + dateCreated + " ";
    s += "name=" + name + " ";
    s += "finished=" + finished + " ";
    s += "note=" + note + " ";
    s += "completed=" + completed + " ";
    s += "dateFinished=" + finished + " "; 
    s += "hashmap={ " + hashmap + " } ";
    return s;
  }
  
}
```

  * This is my GWT data RPC transport class/object. I send this to the client side. I use enum to static type my generic hashmap system. This should help clarify the above class if your wondering what KEYTYPE is.
```
public class TaskData implements IsSerializable {

  public enum KeyType {
    BLOBID;
    public String value() {
      return name();
    }
    public static KeyType fromValue(String v) {
      return valueOf(v);
    }
  }
  
  /**
   * unique id that was created in jdo
   */
  private Long id;
  
  /**
   * who created it
   */
  private Long userCreated;
  
  /**
   * creation
   */
  private Date dateCreated;
  
  /**
   * task url
   */
  private String name;
  
  /**
   * is the task finished, and no longer needed
   */
  private Boolean finished;
  
  /**
   * send a note with the task
   */
  private TextData note;
  
  /**
   * percent completed
   */
  private BigDecimal completed;
  
  /**
   * finished at this time
   */
  private Date dateFinished;
  
  /**
   * when done remember these attributes
   */
  private HashMap<KeyType, String> hashmap;
  
  /**
   * constructor
   */
  public TaskData() {
  }
  
  public void setId(Long id) {
    this.id = id;
  }
  public Long getId() {
    return id;
  }
  
  public void setUserCreated(Long userCreated) {
    this.userCreated = userCreated;
  }
  public Long getUserCreated() {
    return userCreated;
  }
  
  public void setDateCreated(Date dateCreated) {
    this.dateCreated = dateCreated;
  }
  public Date getDateCreated() {
    return dateCreated;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  public String getName() {
    return name;
  }
  
  public void setFinished(Boolean finished) {
    this.finished = finished;
  }
  public Boolean getFinished() {
    return finished;
  }
  
  public void setNote(TextData note) {
    this.note = note;
  }
  public TextData getNote() {
    return note;
  }

  public void setCompleted(BigDecimal completed) {
    this.completed = completed;
  }
  public BigDecimal getCompleted() {
    return completed;
  }
  
  public void setDateFinished(Date dateFinished) {
    this.dateFinished = dateFinished;
  }
  public Date getDateFinished() {
    return dateFinished;
  }
 
  public void setHashMap(HashMap<KeyType, String> hashmap) {
    this.hashmap = hashmap;
  }
  public HashMap<KeyType, String> getHashMap() {
    return hashmap;
  }
}
```


&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;
