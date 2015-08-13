

&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;

# Searching Textual Data In App Engine Quickly #
> This is a real basic method for searching textual data in Google App Engine. When I set the data to the Entity, I create a Hashset collection of the possible searched words, characters and soundex, which is a unique set of words that represent the text. When you query, you can use multiple filters to drill for matching entities.

## I use the `HashSet<String>` Collection for my searches ##
> I use the hashset collection because it uniquely stores the combinations.

## Use Lower case to match! ##
> Don't forget to use lowercase to match!

## Set Index ##
> Don't forget to set indexes for multiple type filter query drill down. Something like this:
```
<!-- this is war/WEB-INF/datastore-indexes.xml -->
<?xml version="1.0" encoding="utf-8"?>
<datastore-indexes autoGenerate="true">

    <!-- SchoolJdo -->

    <!-- PeopleJdo -->
    <datastore-index kind="PeopleJdo" ancestor="false" source="manual">
        <property name="search" direction="asc"/>
        <property name="nameLast" direction="asc"/>
    </datastore-index>
    
    <datastore-index kind="PeopleJdo" ancestor="false" source="manual">
        <property name="accountId" direction="asc"/>
        <property name="search" direction="asc"/>
        <property name="nameLast" direction="asc"/>
    </datastore-index>

	<!-- LearningPlanJdo -->

	<!-- CourseJdo -->

</datastore-indexes>
```

## Here is a quick example 1 - Searching Text ##
> I'm including most of the class for your examination. Theres more than one way to do this!
```
/**
* cedars are codes
*/
@PersistenceCapable
public class CedarsJdo {

  @NotPersistent
  private static final Logger log = Logger.getLogger(CedarsJdo.class.getName());

  @PrimaryKey
  @Persistent
  private String code;

  @Persistent
  private String name;

  @Persistent
  private Text description;

  @Persistent
  private Integer grade;

  @Persistent
  private HashSet<String> nameWords;

  @Persistent
  private HashSet<String> descriptionWords;

  //...

 public CedarsJdo(ServerPersistence sp) {
    this.sp = sp;
  }
  private CedarsJdo() {
  }

  public void setData(String code, String name, String description, int grade) {
    this.code = code;
    this.name = name;
    if (description != null) {
      this.description = new Text(description);
    } else {
      this.description = null;
    }
    this.grade = grade;

    setNameWords(name);
    setDescriptionWords(description);
  }

  private void setNameWords(String v) {
    if (v == null) {
      this.nameWords = null;
      return;
    }
    v = v.replaceAll("[^a-zA-Z0-9\040]","");
    v = v.toLowerCase();

    String[] s = v.split("\040");
    List<String> list = Arrays.asList(s);

    this.nameWords = new HashSet<String>(list);
  }

  private void setDescriptionWords(String v) {
    if (v == null) {
      descriptionWords = null;
      return;
    }
    v = v.replaceAll("[^a-zA-Z0-9\040]","");
    v = v.toLowerCase();

    String[] s = v.split("\040");
    List<String> list = Arrays.asList(s);

    this.descriptionWords = new HashSet<String>(list);
  }

  //...

  private CedarsItemData[] queryData(CedarsDataFilter filter) {

    String qfilter = getFilter(filter);

    ArrayList<CedarsItemData> a = new ArrayList<CedarsItemData>(); 
    PersistenceManager pm = sp.getPersistenceManager();
    try {
      Query q = pm.newQuery(CedarsJdo.class);
      if (qfilter != null) {
        q.setFilter(qfilter);
      }
      q.setRange(filter.getRangeStart(), filter.getRangeFinish());
      List<CedarsJdo> c = (List<CedarsJdo>) q.execute();
      Iterator<CedarsJdo> itr = c.iterator();
      while (itr.hasNext()) {
        CedarsJdo j = itr.next();
        if (j != null) {
          CedarsJdo detatched = pm.detachCopy(j);
          a.add(detatched.getData());
        }
      }
      q.closeAll();
    } catch (Exception e) { 
      e.printStackTrace();
      log.log(Level.SEVERE, "", e);
    } finally {
      pm.close();
    }
    if (a.size() == 0) {
      return null;
    }
    CedarsItemData[] r = new CedarsItemData[a.size()];
    a.toArray(r);
    return r;
  }

  private String getFilter(CedarsDataFilter filter) {

    String qfilter = "";

    if (filter.getSearch() != null) {
      for (int i=0; i < filter.getSearch().length; i++) {
        qfilter += " nameWords==\"" + filter.getSearch()[i].toLowerCase().trim() + "\"";
        if (i < filter.getSearch().length - 1) {
          qfilter += " && ";
        }
      }
    }
    
    if (filter.getCode() != null) {
      qfilter += "code==\"" + filter.getCode() + "\"";
    }

    if (qfilter.length() == 0) {
      qfilter = null;
    }

    return qfilter;
  }


}
```

## Here is another example 2 - Search People's Names ##
> Here I  do more with individual characters left to right, which suites name searching quite well. What I don't have here yet is soundex integers that represent a possible sound match, and think this would be another good option similar to "Like" in sql, although a bit more subjective.

> I included the whole class for your looksee. This is how I save my people. Notice private void setSearch(PeopleData data);
```
@PersistenceCapable
public class PeopleJdo {

  @NotPersistent
  private static final Logger log = Logger.getLogger(PeopleJdo.class.getName());

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
  private Long accountId;

  @Persistent
  private Long userId;

  @Persistent
  private String nameFirst;

  @Persistent
  private String nameLast;
  
  @Persistent
  private HashSet<String> search;

  @Deprecated
  @Persistent
  private String googleEmail;
  @Persistent
  private LinkedHashSet<String> googleEmails;


  @Persistent
  private Boolean typeAdmin;

  @Persistent
  private Boolean typeConsultant;

  @Persistent
  private Boolean typeTeacher;

  @Persistent
  private Boolean typeParent;

  @Persistent
  private Boolean typeStudent;


  /**
   * student learning plan
   */
  @Persistent
  private LinkedHashSet<Long> plans;

  /**
   * courses
   */
  @Persistent
  private LinkedHashSet<Long> courses;

  /**
   * schools
   */
  @Persistent
  private LinkedHashSet<Long> schoolIds;



  @Persistent
  private LinkedHashSet<Long> parentPeopleIds;

  @Persistent
  private LinkedHashSet<Long> teachersPeopleIds;

  @Persistent
  private LinkedHashSet<Long> consultantsPeopleIds;



  public String toString() {
    String s = "";
    s += "key=" + key + " ";
    s += "userId=" + userId + " ";
    s += "nameFirst=" + nameFirst + " ";
    s += "nameLast=" + nameLast + " ";
    s += "googleEmail=" + nameLast + " ";
    s += "typeAdmin=" + typeAdmin + " ";
    //s += "typeConsultant=" + typeConsultant + " "; 
    return s;
  }

  /**
   * init
   * @param sp
   */
  public PeopleJdo(ServerPersistence sp) {
    this.sp = sp;
  }
  private PeopleJdo() {
  }

  public void set(ServerPersistence sp) {
    this.sp = sp;
  }

  private void setId(Long id) {
    if (id == null || id == 0) {
      dateCreated = new Date();
      return;
    }
    key = KeyFactory.createKey(PeopleJdo.class.getSimpleName(), id);
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

  public PeopleJdo getThis() {
    return this;
  }

  public PeopleData saveSchooPeopleData(PeopleData data) throws Exception {

    PersistenceManager pm = sp.getPersistenceManager();
    Transaction tx = pm.currentTransaction();
    try {
      if (data.getId() != null && data.getId() > 0) {
        PeopleJdo update = pm.getObjectById(PeopleJdo.class, data.getId());
        update.set(sp);
        update.setData(data);
        tx.begin();
        pm.makePersistent(update);
        tx.commit();
        data.setId(update.getId());
      } else {
        setData(data);
        tx.begin();
        pm.makePersistent(this);
        tx.commit();
        data.setId(getId());
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

  public void setData(PeopleData data) {
    setId(data.getId());
    setUserId();

    // TODO make sure admin is the only one changing this
    this.accountId = data.getAccountId();
    this.nameFirst = data.getNameFirst();
    this.nameLast = data.getNameLast();

    if (data.getGoogleEmails() != null) {
      this.googleEmails = data.getGoogleEmails();
    } else {
      this.googleEmails = null;
    }

    this.typeAdmin = data.getTypeAdmin();
    this.typeConsultant = data.getTypeConsultant();
    this.typeTeacher = data.getTypeTeacher();
    this.typeParent = data.getTypeParent();
    this.typeStudent = data.getTypeStudent();

    this.parentPeopleIds = data.getParentsPeopleIds();
    this.teachersPeopleIds = data.getTeachersStudentPeopleIds();
    this.consultantsPeopleIds = data.getConsultantsSchoolPeopleIds();



    this.plans = data.getPlans();
    this.courses = data.getCourses();
    this.schoolIds = data.getSchoolIds();
    
    setSearch(data);
  }

  /**
   * set the possible search [words|chars] up. 
   * TODO add soundex
   * @param data
   */
  private void setSearch(PeopleData data) {
    if (data == null) {
      return;
    }
    HashSet<String> hs = new HashSet<String>();
    
    String ln = data.getNameLast();
    if (ln != null && ln.trim().length() > 0) {
      hs.add(ln.toLowerCase());
      
      if (ln.length() > 1) {
        
        for (int i=1; i < ln.length(); i++) {
          String s = ln.substring(0, i);
          hs.add(s.toLowerCase());
        }
      }
    }

    String fn = data.getNameFirst();
    if (fn != null && fn.trim().length() > 0) {
      hs.add(fn.toLowerCase());
      
      if (fn.length() > 1) {
        for (int i=1; i < fn.length(); i++) {
          String s = fn.substring(0, i);
          hs.add(s.toLowerCase());
        }
      }
    }
    
    LinkedHashSet<String> ems = data.getGoogleEmails();
    Iterator<String> itr = ems.iterator();
    while(itr.hasNext()) {
      String e = itr.next();
      if (e != null && e.trim().length() > 0) {
        hs.add(e.toLowerCase());
        
        if (e.length() > 1) {
          for (int i=1; i < e.length(); i++) {
            String s = e.substring(0, i);
            hs.add(s.toLowerCase());
          }
        }
      }
    }
    
    // set to entity
    search = hs;
  }

  public PeopleData getData() {
    PeopleData s = new PeopleData();
    s.setId(getId());
    s.setAccountId(accountId);
    s.setName(nameFirst, nameLast);
    s.setGoogleEmails(googleEmails);
    s.setTypes(typeAdmin, typeConsultant, typeTeacher, typeParent, typeStudent);

    s.setPlans(plans);
    s.setCourses(courses);
    s.setSchoolIds(schoolIds);

    s.setParentsSchoolPeopleIDs(parentPeopleIds);
    s.setTeachersSchoolPeopleIDs(teachersPeopleIds);
    s.setConsultantsSchoolPeopleIDs(consultantsPeopleIds);
    return s;
  }

  public PeoplesData query(PeopleDataFilter filter) {
    if (sp.getUserId() == 0) {
      return null;
    }

    PreparedQuery pqTotal = queryTotal1(filter);

    PeoplesData sd = new PeoplesData();
    sd.setSchoolPeopleData(queryData(filter));
    sd.setTotal(queryTotal2(pqTotal));

    return sd;
  }

  private PeopleData[] queryData(PeopleDataFilter filter) {

    String qfilter = null;

    // filter a batch of ids
    List<Key> keysList = null;
    if (filter.getUseIds() == true) {
      keysList = getFilterForIds(filter);
      if (keysList != null) {
        qfilter = ":keys.contains(key)";
      }
      if (keysList == null || keysList.size() == 0) {
        return null;
      }
    }
    
    if (filter.getSearch() != null) {
      for (int i=0; i < filter.getSearch().length; i++) {
        if (qfilter == null) {
          qfilter = "";
        }
        qfilter += " search==\"" + filter.getSearch()[i].toLowerCase().trim() + "\"";
        if (i < filter.getSearch().length - 1) {
          qfilter += " && ";
        }
      }
    }

    if (filter.getTypeStudent() != null) {
      if (qfilter != null && qfilter.length() > 0) {
        qfilter += " && ";
      }
      qfilter += "typeStudent=" + filter.getTypeStudent();
    }
    
    if (filter.getTypeTeacher() != null) {
      if (qfilter != null && qfilter.length() > 0) {
        qfilter += " && ";
      }
      qfilter += "typeTeacher=" + filter.getTypeTeacher();
    }
    
    ArrayList<PeopleData> a = new ArrayList<PeopleData>(); 
    PersistenceManager pm = sp.getPersistenceManager();
    try {
      Query q = pm.newQuery("select from " + PeopleJdo.class.getName());
      if (qfilter != null) {
        q.setFilter(qfilter);
      }
      q.setOrdering("nameLast asc");
      q.setRange(filter.getRangeStart(), filter.getRangeFinish());

      List<PeopleJdo> ids = null;
      if (filter.getUseIds() == true && keysList != null) {
        ids = (List<PeopleJdo>) q.execute(keysList);

      } else {
        ids = (List<PeopleJdo>) q.execute();
      }

      Iterator<PeopleJdo> itr = ids.iterator();
      while (itr.hasNext()) {
        PeopleJdo j = itr.next();
        if (j != null) {
          PeopleJdo detatched = pm.detachCopy(j);
          a.add(detatched.getData());
        }
      }
      q.closeAll();
    } catch (Exception e) { 
      e.printStackTrace();
      log.log(Level.SEVERE, "", e);
    } finally {
      pm.close();
    }
    if (a.size() == 0) {
      return null;
    }
    PeopleData[] r = new PeopleData[a.size()];
    a.toArray(r);

    return r;
  }

  public PreparedQuery queryTotal1(PeopleDataFilter filter) {

    if (filter == null) {
      log.severe("ERROR: Set a filter");
      return null;
    }

    PreparedQuery pq = null;
    try {
      AsyncDatastoreService datastore = DatastoreServiceFactory.getAsyncDatastoreService();
      com.google.appengine.api.datastore.Query q = new com.google.appengine.api.datastore.Query("PeopleJdo");

      // multiple ids
      q = addfilterByIds(q, filter); 
      
      if (filter.getSearch() != null) {
        //List<String> list = Arrays.asList(filter.getSearch());
        for (int i=0; i < filter.getSearch().length; i++) {
          String s = filter.getSearch()[i];
          q.addFilter("search", FilterOperator.EQUAL, s);
        }
      } 
      
      if (filter.getTypeStudent() != null) {
        q.addFilter("typeStudent", FilterOperator.EQUAL, filter.getTypeStudent());
      }
      
      if (filter.getTypeTeacher() != null) {
        q.addFilter("typeTeacher", FilterOperator.EQUAL, filter.getTypeTeacher());
      }

      pq = datastore.prepare(q);

      // start the prefetching
      pq.asIterable(); 

    } catch (Exception e) {
      log.log(Level.SEVERE, "", e);
      e.printStackTrace();
    }
    return pq;
  }

  private com.google.appengine.api.datastore.Query addfilterByIds(com.google.appengine.api.datastore.Query q, PeopleDataFilter filter) {
    if (filter == null || filter.getUseIds() == false || filter.getIds() == null || filter.getIds().size() == 0) {
      return q;
    }
    HashSet<Long> hs = filter.getIds();
    Iterator<Long> itr = hs.iterator();
    while (itr.hasNext()) {
      Long id = itr.next();
      if (id != null) {
        Key k = KeyFactory.createKey(PeopleJdo.class.getSimpleName(), id);
        q.addFilter(Entity.KEY_RESERVED_PROPERTY, FilterOperator.EQUAL, k);
      }
    }
    return q;
  }
  public long queryTotal2(PreparedQuery pq) {
    long l = pq.countEntities(FetchOptions.Builder.withDefaults());
    return l;
  }

  public boolean delete(Long id) {
    if (id == null) {
      return true;
    }
    boolean success = false;
    PersistenceManager pm = sp.getPersistenceManager();
    Transaction tx = pm.currentTransaction();
    try {
      PeopleJdo j = pm.getObjectById(PeopleJdo.class, id);
      if (sp.getIsSchoolAdmin() == true) { 
        deletePeopleItems(j.getData());
        tx.begin();
        pm.deletePersistent(j);
        tx.commit();
        success = true;
      }
    } catch (Exception e) {
      success = false;
    }  finally {
      if (tx.isActive()) {
        tx.rollback();
      }
      pm.close();
    }
    return success;
  }

  public PeopleData query(Long id) {
    if (id == null) {
      return null;
    }
    PeopleData d = null;
    PersistenceManager pm = sp.getPersistenceManager();
    Transaction tx = pm.currentTransaction();
    try {
      PeopleJdo j = pm.getObjectById(PeopleJdo.class, id);
      PeopleJdo detatched = pm.detachCopy(j);
      d = detatched.getData();
    } catch (Exception e) {

    }  finally {
      if (tx.isActive()) {
        tx.rollback();
      }
      pm.close();
    }

    if (d != null) {
      d.setEmailCounts(queryEmailCounts(d.getGoogleEmails()));
    }

    return d;
  }


  private void deletePeopleItems(PeopleData data) {
    if (data == null) {
      return;
    }

    boolean b = false;

    // plans
    try {
      b = new LearningPlanJdo(sp).delete(data.getPlans());
    } catch (Exception e) {
      log.severe("deletePeopleItems() error: 1: " + e.toString());
      e.printStackTrace();
    }


    // don't do
    // parentsPeopleIds
    // teacherPeopleIds
    // consultantPeopleIds
  }

  private List<Key> getFilterForIds(PeopleDataFilter filter) {
    if (filter == null || filter.getUseIds() == false || filter.getIds() == null || filter.getIds().size() == 0) {
      return null;
    }
    ArrayList<Key> k = new ArrayList<Key>();
    HashSet<Long> ids = filter.getIds();
    Iterator<Long> itr = ids.iterator();
    while(itr.hasNext()) {
      Long id = itr.next();
      if (id != null) {
        Key kk = KeyFactory.createKey(PeopleJdo.class.getSimpleName(), id);
        k.add(kk);
      }
    }
    return k;
  }

  /**
   * query by google email - will only look up the first
   * 
   * @param googleEmail
   * @return
   */
  public PeopleData query(String googleEmail) {
    if (googleEmail == null || googleEmail.trim().length() == 0) {
      return null;
    }
    String qfilter = "googleEmail==\"" + googleEmail + "\"";
    PeopleData r = null;
    PersistenceManager pm = sp.getPersistenceManager();
    try {
      Query q = pm.newQuery("select from " + PeopleJdo.class.getName());
      q.setRange(0, 1);
      q.setFilter(qfilter);
      List<PeopleJdo> query = (List<PeopleJdo>) q.execute();
      Iterator<PeopleJdo> itr = query.iterator();
      while (itr.hasNext()) {
        PeopleJdo j = itr.next();
        if (j != null) {
          PeopleJdo detatched = pm.detachCopy(j);
          r = detatched.getData();
        }
        break;
      }
      q.closeAll();
    } catch (Exception e) { 
      e.printStackTrace();
      log.log(Level.SEVERE, "", e);
    } finally {
      pm.close();
    }
    return r;
  }


  private LinkedHashSet<Integer> queryEmailCounts(LinkedHashSet<String> googleEmails) {
    if (googleEmails == null || googleEmails.size() == 0) {
      return null;
    }
    LinkedHashSet<Integer> r = new LinkedHashSet<Integer>();
    Iterator<String> itr = googleEmails.iterator();
    while (itr.hasNext()) {
      String email = itr.next();
      r.add(queryEmailCount(email));
    }
    return r;
  }


  public Integer queryEmailCount(String googleEmail) {
    if (googleEmail == null || googleEmail.trim().length() == 0) {
      return null;
    }
    String qfilter = "googleEmails==\"" + googleEmail + "\"";
    Integer r = null;
    PersistenceManager pm = sp.getPersistenceManager();
    try {
      Query q = pm.newQuery("select from " + PeopleJdo.class.getName());
      q.setRange(0, 1);
      q.setFilter(qfilter);
      List<PeopleJdo> query = (List<PeopleJdo>) q.execute();
      r = query.size();
      q.closeAll();
    } catch (Exception e) { 
      e.printStackTrace();
      log.log(Level.SEVERE, "", e);
    } finally {
      pm.close();
    }
    return r;
  }

}
```


&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;

