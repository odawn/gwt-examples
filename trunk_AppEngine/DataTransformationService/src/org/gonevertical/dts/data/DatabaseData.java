package org.gonevertical.dts.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseData {
  
  private static final Logger log = LoggerFactory.getLogger(DatabaseData.class);
	
  // location
  private String host;
  private String port;
  
  // credentials
  private String username;
  private String password;
  
  // database
  private String database;
  
  // ms uses this for [table_schema].[table]
  private String tableSchema;
  
  // what type of database is it? Mysql, Jdo, ...
  public static final int TYPE_MYSQL = 1;
  public static final int TYPE_MSSQL = 2;
  public static final int TYPE_JDO = 3;
	public static final int TYPE_ORACLE = 4;
	
  private int databaseType = 0;
  
  // setup a connection and store it in this object for easy reference to
  //private Connection conn = null;
  
  // pooling datasource
  private DataSource dataSourcePool = null;
  
  // servlet context - used for connection pooling
  private Context context = null;
  
  // data connection pool resource name
  // like "jdbc/TestDB" - needs to be in web.xml and server.xml
  private String contextRefName = null;
  
  // server info from servlet thread - getServletContext().getServerInfo();
  // to use this connection pooling servlet needs to be running on tomcat
  private String serverInfo = null;
  
  // load balance master, slave, slave...
  private boolean autoReconnect = false;
  private boolean roundRobinLoadBalance = false;
  private boolean readOnly = false;
  
  // for profiling connection times
  private long startTime = 0;
  private boolean profile = false;
	private int recursionCheck;
  
	private String url = null;

  private HashMap<String, String> properties;
	
  /**
   * set database location and credentials
   * 
   * @param host
   * @param port
   * @param username
   * @param password
   * @param database
   */
  public DatabaseData(int databaseType, String host, String port, String username, String password, String database) {
    this.databaseType = databaseType;
    this.host = host;
    this.port = port;
    this.username = username;
    this.password = password;
    this.database = database;
    
    if (databaseType == DatabaseData.TYPE_MSSQL) {
    	System.out.println("~~~~~~~~~~~~~~ Use the other contructor to set the table schema!!!! ~~~~~~~~~~~~~~");
    	System.out.println("~~~~~~~~~~~~~~ Use the other contructor to set the table schema!!!! ~~~~~~~~~~~~~~");
    }
  }
  
  public DatabaseData(int databaseType, String host, String port, String username, String password, String database, String tableSchema) {
    this.databaseType = databaseType;
    this.host = host;
    this.port = port;
    this.username = username;
    this.password = password;
    this.database = database;
    this.tableSchema = tableSchema;
  }
  
  public DatabaseData(int databaseType, HashMap<String, String> properties) {
    this.databaseType = databaseType;
    this.host = properties.get("host");
    this.port = properties.get("port");;
    this.username = properties.get("username");
    this.password = properties.get("password");
    this.database = properties.get("database");
    this.tableSchema = properties.get("tableSchema");
    this.properties = properties;
  }

  /**
   * http://dev.mysql.com/doc/refman/5.4/en/connector-j-reference-configuration-properties.html
   */
  public void setLoadBalance(boolean b) {
    if (b == true) {
      autoReconnect = true;
      roundRobinLoadBalance = true;
    } else {
      autoReconnect = true;
      roundRobinLoadBalance = false;
    }
  }
  
  public int getDatabaseType() {
    return databaseType;
  }
  
  public String getHost() {
    return host;
  }
  
  public String getPort() {
    return port;
  }

  public String getUsername() {
    return username;
  }
  
  public String getPassword() {
    return password;
  }
  
  public String getDatabase() {
    return database;
  }
  
  public void setTableSchema(String tableSchema) {
  	this.tableSchema = tableSchema;
  }
  
  public String getTableSchema() {
  	return tableSchema;
  }
    
  /**
   * open connection
   * @return
   */
  public Connection openConnection() {
    Connection conn = null;
  	// get connection by jndi context
    if (context != null && contextRefName != null) { // no longer needed - && serverInfo.toLowerCase().contains("tomcat")
      conn = getConnetionByConext();
      
    } else {
    	
    	// get connection by default
      if (databaseType == TYPE_MYSQL) {
        conn = getConn_MySql();
        
      } else if (databaseType == TYPE_MSSQL) {
        conn = getConn_MsSql();
        
      } else if (databaseType == TYPE_ORACLE) {
      	conn = getConn_Oracle();
      	
      }
    }
    return conn;
  }
  
  /**
   * same as open connection
   * @return
   */
  public Connection getConnection() {
    return openConnection();
  } 
    
  /**
   * close the database connection
   *  Don't really need to use this.
   *  conn.close();
   */
  public void closeConnection() {
    //if (conn == null) {
      //return;
    //}
    
    //try {
      //conn.close();
    //} catch (SQLException e) {
      //e.printStackTrace();
    //} finally {
      //conn = null;
    //
  }
  
  /**
   * get a mysql database connection
   * 
   * jdbc:mysql://[host][,failoverhost...][:port]/[database] » [?propertyName1][=propertyValue1][&propertyName2][=propertyValue2]...
   * 
   * parameters list
   * http://dev.mysql.com/doc/refman/5.4/en/connector-j-reference-configuration-properties.html
   * 
   * @return
   */
  private Connection getConn_MySql() {

    Connection conn = null;
    
    String loadBalance = "";
    if (roundRobinLoadBalance == true) {
      loadBalance = "?roundRobinLoadBalance=true&autoReconnect=true";
    }
    
    String url = "jdbc:mysql://" + host + ":" + port + "/" + database + loadBalance;

    String driver = "com.mysql.jdbc.Driver";
    if (roundRobinLoadBalance == true) {
      driver = "com.mysql.jdbc.ReplicationDriver";
    }
    // System.out.println("getConn_MySql: url:" + url + " user: " + username + " driver: " + driver);
    
    profileTime("connection to mysql url: " + url);
    try {
      Class.forName(driver).newInstance();
      conn = DriverManager.getConnection(url, username, password);
    } catch (Exception e) {
      System.err.println("~~~~~ " + url + "~~~~ ERROR: getConn_MySql(): connection error: " + e.getMessage() + " " + "getConn_MySql: url:" + url + " user: " + username + " driver: " + driver);
      e.printStackTrace();
      log.error("DatabaseData.getConn_MySql(): ", e);
      recursionCheck++;
      if (recursionCheck < 3) {
      	System.out.println(recursionCheck + ". Trying connection agian");
      	conn = getConn_MySql();
      }
    }
    profileTime("connected  to mysql url: " + url);
    
    if (roundRobinLoadBalance == true) {
      try {
        conn.setReadOnly(true);
      } catch (SQLException e) {
        e.printStackTrace();
        log.error("DatabaseData.getConn_MySql(): ", e);
      }
    }
    
    if (readOnly == true) {
      try {
        conn.setReadOnly(readOnly);
      } catch (SQLException e) {
      }
    }
    
    return conn;
  }
  
  /**
   * get ms sql connection
   * 
   * Driver is different for 2000 vs 2005, this may change
   * 
   * @return
   */
  private Connection getConn_MsSql() {
    Connection conn = null;
    
    String url = "jdbc:sqlserver://" + host + ":" + port +  ";user=" + username + ";password=" + password + ";databaseName=" + database + ";";
    String driver = "com.microsoft.sqlserver.jdbc.SQLServerDriver";
    //System.out.println("getConn_MsSql: url:" + url + " user: " + username + " driver: " + driver);

    try {
      Class.forName(driver);
      conn = DriverManager.getConnection(url);
    } catch (Exception e) {
      System.err.println("ERROR: getConn_MsSql(): connection error: " + e.getMessage());
      e.printStackTrace();
      log.error("DatabaseData.getConn_MsSql(): ", e);
    }
    return conn;
  }
  
  /**
   * Oracle jdbc connection
   * 
   * @return
   */
  private Connection getConn_Oracle() {
    
    String url = "jdbc:oracle:thin:"+username+"/"+password+"@" + host + ":" + port + ":" + database;
    String driver = "oracle.jdbc.OracleDriver";
    System.out.println("getConn_Oracle: url= " + url + " user= " + username + " driver= " + driver);

    Connection conn = null;
    try {
      Class.forName(driver);
      conn = DriverManager.getConnection(url);
    } catch (Exception e) {
      System.err.println("ERROR: getConn_Oracle(): connection error: " + e.getMessage());
      e.printStackTrace();
      log.error("DatabaseData.getConn_Oracle(): ", e);
    }
    
    if (conn == null) {
      System.out.println("Oracle get Connection exiting");
      System.exit(1);
    }
    
    return conn;
  }
  
  /**
   * set initial context for servlet connection pooling 
   * 
   * @param serverInfo String serverInfo = getServletContext().getServerInfo();
   * @param context - inital jndi context
   * @param contextRefName - resource name to connection pool connection like "jdbc/mydbresname"
   *    this must be in server.xml and web.xml 
   */
  public void setServletContext(String serverInfo, Context context, String contextRefName) {
    this.serverInfo = serverInfo;
    this.context = context;
    this.contextRefName = contextRefName;
  }
  
  public void setServletContext(Context context, String contextRefName) {
    this.context = context;
    this.contextRefName = contextRefName;
  }
  
  /**
   * get servlet connection for tomcat6 connection pooling 
   * 
   *  make sure /usr/share/tomcat6/lib has jdbc driver
   * 
   * @return
   */
  public Connection getConnetionByConext() {
  
    if (context == null) {
      System.out.println("ERROR: getServletConnetion(): no context set");
      return null;
    }
  
    if (contextRefName == null) {
      System.out.println("ERROR: getContextConnetion(): no contextRefName set");
      return null;
    }
    
    // the first call setup the dataSource Pool
    if (dataSourcePool == null) { // && serverInfo.toLowerCase().contains("tomcat") == true
    	
      try {
        dataSourcePool = (DataSource) context.lookup(contextRefName); 
      } catch (NamingException e) {
        System.out.println("ERROR: getContextConnetion(): NO datasource");
        e.printStackTrace();
        log.error("DatabaseData.getconnectionByContext(): ", e);
      }

      
    }
    
    // use the datasource to get the connection
    Connection conn = null;
    try {
      conn = dataSourcePool.getConnection();
      
    } catch (SQLException e) {
      System.out.println("ERROR: getContextConnetion(): couldn't get servlet connection: " + contextRefName);
      e.printStackTrace();
      log.error("DatabaseData.getConnectionByContext(): ", e);
    }
    
    //BasicDataSource bds = (BasicDataSource) dataSourcePool;
    //System.out.println(contextRefName + ": Active Conn: " + bds.getNumActive() + " max: " + bds.getMaxActive());
    //System.out.println(contextRefName + ": Idle Conn: " + bds.getNumIdle());
    
    return conn;
  }
  
  /**
   * init a context for use with servlet connection - don't do this over and over
   *  Only do on tomcat(servlet container)
   * @return
   */
  public static Context initContext() {	
  	String sctx = "java:/comp/env";
    Context initContext = null;
    try {
      initContext = new InitialContext();
    } catch (NamingException e) {
      System.out.println("ERROR: initContext(): Could not get InitalContext");
      e.printStackTrace();
      log.error("DatabaseData.initContext(): ", e);
    }
  
    Context ctx = null;
    try {
      ctx = (Context) initContext.lookup(sctx);
    } catch (NamingException e) {
      System.out.println("ERROR: initContext(): Could not init Context");
      e.printStackTrace();
      System.out.println("DatabaseData().initContext() Exiting!!!!! Setup the context: " + sctx);
      log.error("DatabaseData.initContext(): Exiting:", e);
      System.exit(1);
    }
    return ctx;
  }
  
  /**
   * can't remember?
   * 
   * @param context_url
   * @return
   */
  public static Context initContext(String context_url) {
    Context initContext = null;
    try {
      initContext = new InitialContext();
    } catch (NamingException e) {
      System.out.println("ERROR: initContext(): Could not get InitalContext");
      e.printStackTrace();
      log.error("DatabaseData.initContext(): ", e);
    }
    Context ctx = null;
    try {
      ctx = (Context) initContext.lookup(context_url);
    } catch (NamingException e) {
      System.out.println("ERROR: initContext(): Could not init Context");
      e.printStackTrace();
      System.out.println("DatabaseData().initContext() Exiting!!!!! Setup the context: " + context_url);
      log.error("DatabaseData.initContext(): Exiting", e);
      System.exit(1);
    }

    return ctx;
  }
  
  public boolean getLoadBalance() {
    return roundRobinLoadBalance;
  }

  public void setReadOnly(boolean b) {
    this.readOnly  = b;
  }
  
  public void setProfileTime(long startTime) {
    this.startTime = startTime;
  }
  
  public void startProfileTime() {
    startTime = System.currentTimeMillis();
    profile = true;
  }
  
  public void profileTime(String msg) {
    if (profile == false) {
      return;
    }
     
    long endTime = System.currentTimeMillis();
    long howLong = endTime - startTime;
    System.out.println(msg + " " + Long.toString(howLong) + "ms");
  }
  
  public String getServer() {
  	return "Server: " + host + ":" + port + "/" + database;
  }
  
  public void setDatabase(String database) {
  	this.database = database;
  }
  
  public String getDbCatalogAndTable() {
  	String db = database;
  	if (tableSchema != null && tableSchema.length() > 0) {
  		db += "." + tableSchema;
  	}
  	return db;
  }

  public String getResourceName() {
    return properties.get("name");
  }

  public HashMap<String, String> getProperties() {
    return properties;
  }
}
