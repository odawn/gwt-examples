
&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" scrolling=0 /&gt;


# Tasking with App Engine #
> This is how I'm starting tasks with app engine. I use one servlet to coordinate the starts. I also use another class on the GWT client side to monitor the task through polling and looking at the TaskId.

```
public class Db_Task {
  
  private static final Logger log = Logger.getLogger(Db_Task.class.getName());

  public enum TaskType {
    TYPE_GEOCODE,
    TYPE_TRANSFORM,
    TYPE_DEFAULTS,
    TYPE_WRITETOBLOB, 
    TYPE_DELETETMPBLOBALL, 
    TYPE_MARKFILESPUBLIC;
    public String value() {
        return name();
    }
    public static TaskType fromValue(String v) {
        return valueOf(v);
    }
  }
  
  private ServerPersistence sp;

  public Db_Task(ServerPersistence sp) {
    this.sp = sp;
  }
  
  private String getTaskType(TaskType type) {
    String s = null;
    switch (type) {
    case TYPE_GEOCODE:
      s = "geocode";
      break;
    case TYPE_TRANSFORM:
      s = "transform";
      break;
    case TYPE_DEFAULTS:
      s = "defaults";
      break;
    case TYPE_WRITETOBLOB:
      s = "writetoblob";
      break;
    case TYPE_DELETETMPBLOBALL:
      s = "deletetmpbloball";
      break;
    case TYPE_MARKFILESPUBLIC:
      s = "markfilespublic";
      break;
    }
    return s;
  }
  
  /**
   * start task
   * 
   * @param thingId - attatch task to this thing
   * @param taskType - what task to run
   * @param urlParams - (all lower case) url parameters to add into task specific to task - no need for ownerid
   * @return
   */
  public long dotask(long thingId, TaskType taskType, HashMap<String,String> urlParams) {
    
    String tt = getTaskType(taskType);
    if (tt == null) {
      return -1;
    }
    
    String urlp = getParams(urlParams);
    
    String url = "/task?task=" + tt;
    url += "&ownerid=" + sp.getUserThingId();
    url += urlp;

    String taskName = StringUtils.getRandomString(5) + "_" + url;
    
    // save url 
    long taskId = new Db_TaskMonitor(sp).saveTask(thingId, taskName);

    // add taskId on after saving
    url = url + "&taskid=" + taskId;

    if (sp.getHeader("Cookie") == null || sp.getHeader("Cookie").trim().length() == 0) {
      log.severe("writeTmpBlob(): ERROR: No Cookie exists. This happens when you login.");
      return -1;
    }

    // Don't forget to send in the Google Authorized Cookie!!!
    try {
      TaskOptions taskOptions = TaskOptions.Builder.withUrl(url).header("Cookie", sp.getHeader("Cookie")).method(Method.GET);
      Queue queue = QueueFactory.getDefaultQueue();
      queue.add(taskOptions);
    } catch (Exception e) {
      new Db_TaskMonitor(sp).deleteTask(taskId);
      log.warning("Db_Task(): Error: setting up task in que. deleting task. " + e.toString());
      return -1;
    }
    
    return taskId;
  }

  private String getParams(HashMap<String,String> params) {
    if (params == null) {
      return "";
    }
    String s = "";
    Collection<String> keys = params.keySet();
    for(String key: keys){
        String value = params.get(key);
        if (key.equals("ownerid") == false) { // no need for ownerid
          s  += "&" + key + "=" + value;
        }
    } 
    return s;
  }
   
}
```

> web.xml setup - tells the servlet container to listen for this url and serve the servlet.
```
  <servlet-mapping>
    <servlet-name>Task</servlet-name>
    <url-pattern>/task</url-pattern>
  </servlet-mapping>
  <servlet>
    <servlet-name>Task</servlet-name>
    <servlet-class>com.gonevertical.server.servlet.Servlet_Task</servlet-class>
  </servlet>
```

> This is where the tasks start in the servlet.
```
public class Servlet_Task extends HttpServlet {
  
  private static final Logger log = Logger.getLogger(Servlet_Task.class.getName());

  private ServerPersistence sp = new ServerPersistence();
  
  /**
   * GET request asking to do a task
   */
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    sp.start(request, getServletContext());
    
    if ((sp.getLoginData() == null || sp.getLoginData().getGoogleLoggedIn() == false) && request.getParameter("task") != null && request.getParameter("task").contains("defaults") == false) {
      log.warning("Servlet_Task.doGet(): ERROR: sp.getLoginData() is NULL or not logged in");
      deleteTaskId(request);
      return;
    }
    
    // check owner (but not on setting defaults)
    // TODO disable later
    long ownerId = 0;
    try {
      ownerId = Long.parseLong(request.getParameter("ownerid"));
    } catch (Exception e) {
      log.warning("Servlet_Task.doGet(): ownerid=[ownerid] given incorrectly. " + e.toString());
      deleteTaskId(request);
      return;
    }
    if (ownerId != sp.getLoginData().getUserThingId() && request.getParameter("task") != null && request.getParameter("task").contains("defaults") == false) {
      log.warning("Servlet_Task.doGet(): OwnerId given does not match logged in. taskid=" + request.getParameter("taskid") + " task=" + request.getParameter("task"));
      deleteTaskId(request);
      return;
    }
    
    // associate a taskid for tracking, this needs to be given
    long taskId = 0;
    try {
      taskId = Long.parseLong(request.getParameter("taskid"));
    } catch (Exception e) {
      log.warning("Servlet_Task.start(): ERROR: taskid=[taskid] taskid=" + taskId + " parameter not given incorrectly " + e.toString() + " task=" + request.getParameter("task"));
      deleteTaskId(request);
      return;
    }
    
    if (taskId == 0 && request.getParameter("task") != null && request.getParameter("task").contains("defaults") == false) {
      log.warning("Servlet_Task.start(): taskid=[taskid] parameter not given. task=" + request.getParameter("task"));
      deleteTaskId(request);
      return;
    }
    
    // get type of task to preform
    String doTaskType = request.getParameter("task");
    if (doTaskType == null || doTaskType.trim().length() == 0) {
      log.warning("Servlet_Task.doGet(): no 'task=[type]' parameter given. task=" + doTaskType);
      deleteTaskId(request);
      return;
    }

    // start the requested task
    if (doTaskType.equals("geocode") == true) {
      startGeocoding(request, taskId);
      
    } else if (doTaskType.equals("transform") == true) {
      startTransform(request, taskId);
      
    } else if (doTaskType.equals("defaults") == true && GoneVertical.ENABLE_SETDEFAULTS == true) {
      startDefaults(request, taskId);
      
    } else if (doTaskType.equals("writetoblob") == true) {
      startWriteToBlob(request, taskId);
      
    } else if (doTaskType.equals("deletetmpbloball") == true) {
      startDeleteTmpBlobAll(request, taskId);
      
    } else if (doTaskType.equals("deletetmpblob") == true) {
      startDeleteTmpBlob(request, taskId);
      
    } else if (doTaskType.equals("markfilespublic") == true) {
      markFilesPublic(request, taskId);
    }
    
    sp.end();
  }

  //...
}
```

&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;
