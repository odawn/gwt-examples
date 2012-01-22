package org.gonevertical.dts.lib.sql;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gonevertical.dts.data.DatabaseData;
import org.gonevertical.dts.lib.pooling.ResourceData;
import org.gonevertical.dts.lib.sql.columnlib.ColumnLib;
import org.gonevertical.dts.lib.sql.columnmulti.ColumnLibFactory;
import org.gonevertical.dts.lib.sql.querylib.QueryLib;
import org.gonevertical.dts.lib.sql.querymulti.QueryLibFactory;
import org.gonevertical.dts.lib.sql.transformlib.TransformLib;
import org.gonevertical.dts.lib.sql.transformmulti.TransformLibFactory;

/**
 * TODO - on server-id=2 , deal with relay log setup, and mysql-bin disabling
 * TODO - do a test at the end to verify setup
 * 
 * 
 * @author brandon.a.donnelson
 *
 */
public class MySqlReplication {

  private static final Logger log = LoggerFactory.getLogger(MySqlReplication.class);

  // supporting libraries
  private QueryLib ql = null;
  private TransformLib tl = null;
  private ColumnLib cl = null;

  // source database
  private DatabaseData dd_src = null;

  // destination database
  private DatabaseData dd_des = null;

  // tmp directory to store the dump, so to sync
  private File tmpDir;

  // master status
  private String statusFile = null;
  private String statusPosition = null;

  // replication user credentials
  private String replicationUsername = null;
  private String replicationPassword = null;
  private String masterHost = null;

  private int dryRun = 0;

  private ArrayList<String> ignoreTables = new ArrayList<String>();
  
  private ArrayList<String> onlyDatabase = null;

  /**
   * constructor, init setup 
   * 
   * @param dd_src
   * @param dd_des
   * @param tmpDir
   */
  public MySqlReplication(DatabaseData dd_src, DatabaseData dd_des, File tmpDir, 
      String masterHost,  String replicationUsername, String replicationPassword) {
    this.dd_src = dd_src;
    this.dd_des = dd_des;
    this.tmpDir = tmpDir;
    this.replicationUsername = replicationUsername;
    this.replicationPassword = replicationPassword;
    this.masterHost = masterHost;
    setSupportingLibraries();
  }

  /**
   * guice injects the libraries needed for the database
   */
  private void setSupportingLibraries() {
    // get query library
    ql = QueryLibFactory.getLib(DatabaseData.TYPE_MYSQL);

    // get column library
    cl = ColumnLibFactory.getLib(DatabaseData.TYPE_MYSQL);

    // get tranformation library
    tl = TransformLibFactory.getLib(DatabaseData.TYPE_MYSQL);
  }

  public void setDryRun() {
    dryRun = 1;
  }

  public void run() {

    // setup the replication user
    setupReplicationUser();

    // stop slave just in case doing it again
    stopSlave();

    // setup mysql.cnf on master
    setupMasterConf();

    // setup mysql.cnf on slave
    setupSlaveConf();

    // lock tables up so to get status
    lockTables();

    // get master's status
    getMasterStatus();

    // use this for syncing slave
    dumpMaster();

    // unlock master tables after dump
    unlockTables();

    // change to master
    setUpSlave();

    // load slave
    loadSlave();

    // start the slave threads
    startSlave();

    // delete tmp file at the end
    deleteTmpFile();

    // show slave status
    showSlaveStatus();
    
    System.out.println("Finished... Exiting...");
  }

  /**
   * 
   */
  private void setupReplicationUser() {

    // setup the replication user
    // TODO - if user is already created this will error
    String sqlCreateUser = "CREATE USER '" + replicationUsername + "'@'"+ dd_des.getHost() +"' IDENTIFIED BY '" + replicationPassword + "';";
    System.out.println(sqlCreateUser);
    ql.update(dd_src, sqlCreateUser);

    // setup replication user privileges
    String sqlCreateUserPerm = "GRANT REPLICATION SLAVE ON *.*  TO '" + replicationUsername + "'@'" + dd_des.getHost() + "' IDENTIFIED BY '" + replicationPassword + "';";
    System.out.println(sqlCreateUserPerm);
    ql.update(dd_src, sqlCreateUserPerm);

    // flush privileges;
    String sql = "FLUSH PRIVILEGES;";
    System.out.println(sql);
    ql.update(dd_src, sql);

    // TODO - maybe query the user to see if things are correct?
  }

  /**
   * setup master conf
   * 
   * TODO - automate later, manual setup for now
   */
  private void setupMasterConf() {

    // TODO - add to master my.cnf
    // [mysqld]
    // log-bin=mysql-bin
    // server-id=1

    int slaveId = getServerId(1);
    if (slaveId != 1) {
      System.out.println("master server_id variable in my.cnf not set correctly. Its set as on master server_id=" + slaveId);
      System.out.println("Exiting....");
      System.exit(1);
    } else {
      System.out.println("master serverid = 1, it passes.");
    }

  }

  /**
   * setup slave conf
   * 
   * TODO - automate later, manual setup for now
   */
  private void setupSlaveConf() {

    // TODO - add to slave my.cnf
    // server-id=2

    int slaveId = getServerId(2);
    if (slaveId == 1) {
      System.out.println("slave server_id variable in my.cnf not set correctly. It's current setting is server_id=" + slaveId);
      System.out.println("Set Slave server_id=>2 (greater than 1). Exiting....");
      System.exit(1);
    } else {
      System.out.println("slave server_id=" + slaveId + ", it passes.");
    }
  }

  private void lockTables() {

    if (dryRun == 1) {
      System.out.println("Won't lock tables now. Skipping b/c of dry run.......");
      return;
    }

    String sql = "FLUSH TABLES WITH READ LOCK;";
    System.out.println(sql);
    ql.update(dd_src, sql);
  }

  private void unlockTables() {
    String sql = "UNLOCK TABLES;";
    System.out.println(sql);
    ql.update(dd_src, sql);
  }

  private void getMasterStatus() {

    String sql = "SHOW MASTER STATUS;";
    System.out.println(sql);
    try {
      Connection conn = dd_src.getConnection();
      Statement select = conn.createStatement();
      ResultSet result = select.executeQuery(sql);
      while (result.next()) {
        statusFile = result.getString(1);
        statusPosition = result.getString(2);
      }
      select.close();
      select = null;
      result.close();
      result = null;
      conn.close();
    } catch (SQLException e) {
      System.err.println("Error: Can't get master status: " + sql);
      e.printStackTrace();
    } 
  }

  private void dumpMaster() {
    String lock = "";
    if (dryRun != 1) {
      lock = "--lock-all-tables";
    }

    String it = getIgnoreTables();

    String databases = getDatabasesForDumping();

    String cmd = "mysqldump " +
    "-h" + dd_src.getHost() + " " +
    "-u" + dd_src.getUsername() + " " +
    "-p" + dd_src.getPassword() + " " +
    "" + it + " " + 
    "" + lock + " " +
    " " + databases + " > " + getTmpPath() + "/master_dump.sql";
    System.out.println(cmd);
    runShell(cmd);
  }

  private String getDatabasesForDumping() {
    String s = "--all-databases";
    if (onlyDatabase != null) {
      s = "--databases";
      for (int i=0; i < onlyDatabase.size(); i++) {
        s += onlyDatabase.get(i);
        if (i < onlyDatabase.size() - 1) {
          s += " ";
        }
      }
    }
    return s;
  }

  /**
   * get database for replicating only 
   */
  private String getDatabasesForReplicating() {
    String s = "--all-databases";
    if (onlyDatabase != null) {
      s = "";
      for (int i=0; i < onlyDatabase.size(); i++) {
        s += "--replicate-do-db=" + onlyDatabase.get(i);
        if (i < onlyDatabase.size() - 1) {
          s += " ";
        }
      }
    }
    return s;
  }
  
  /**
   * load slave with the dump from master
   * 
   * useing -f for passing on errors
   */
  private void loadSlave() {
    String cmd = "mysql -h" + dd_des.getHost() + " -u" + dd_des.getUsername() + " -p" + dd_des.getPassword() + " -f  < " + getTmpPath() + "/master_dump.sql";
    System.out.println(cmd);
    runShell(cmd);
  }

  private void setUpSlave() {

    String sql = "";
    sql += "CHANGE MASTER TO ";
    sql += "MASTER_HOST='" + masterHost + "', "; // master_host_name
    sql += "MASTER_USER='" + replicationUsername + "', "; // replication_user_name 
    sql += "MASTER_PASSWORD='" + replicationPassword + "', "; // replication_password
    sql += "MASTER_LOG_FILE='" + statusFile + "', "; // recorded_log_file_name
    sql += "MASTER_LOG_POS=" + statusPosition + "; "; // recorded_log_position

    System.out.println(sql);
    ql.update(dd_des, sql);
  }

  private void startSlave() {
    String sql = "START SLAVE;";
    System.out.println(sql);
    ql.update(dd_des, sql);
  }

  /**
   * stop and reset slave before trying anything
   * http://dev.mysql.com/doc/refman/5.4/en/reset-slave.html
   */
  private void stopSlave() {
    String sql = "STOP SLAVE;";
    System.out.println(sql);
    ql.update(dd_des, sql);

    sql = "RESET SLAVE;";
    System.out.println(sql);
    ql.update(dd_des, sql);

  }

  private String getTmpPath() {
    String path = tmpDir.getPath();
    return path;
  }

  private int getServerId(int srcdes) {

    String sql = "SHOW VARIABLES like 'server_id'";
    System.out.println(sql);

    DatabaseData dd = null;
    if (srcdes == 1) {
      dd = dd_src; 
    } else {
      dd = dd_des;
    }

    String sid = null;
    try {
      Connection conn = dd.getConnection();
      Statement select = conn.createStatement();
      ResultSet result = select.executeQuery(sql);
      while (result.next()) {
        sid = result.getString(2);
      }
      select.close();
      select = null;
      result.close();
      result = null;
      conn.close();
    } catch (SQLException e) {
      System.err.println("Error: Can't get master status: " + sql);
      e.printStackTrace();
    }
    return Integer.parseInt(sid);
  }

  /**
   * run shell command
   * 
   * @param cmd
   */
  private void runShell(String cmd) {
    ProcessBuilder pb = new ProcessBuilder("bash", "-c", cmd);
    pb.redirectErrorStream(true); 
    Process shell = null;
    try {
      shell = pb.start();
    } catch (IOException e) {
      e.printStackTrace();
    }
    InputStream shellIn = shell.getInputStream();                 
    try {
      int shellExitStatus = shell.waitFor();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } 
    int c;
    try {
      while ((c = shellIn.read()) != -1) {
        System.out.write(c);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      shellIn.close();
    } catch (IOException ignoreMe) {
    }
  }

  private void deleteTmpFile() {
    System.out.println("deleting mysql dump file.");
    File tmpFile = new File(tmpDir.getPath() + "master_dump.sql");
    tmpFile.delete();
  }

  public void addIgnoreTable(String table) {
    ignoreTables.add(table);
  }

  private String getIgnoreTables() {
    if (ignoreTables.size() == 0) {
      return "";
    }
    String s = "";
    for (int i=0; i < ignoreTables.size(); i++) {
      s += " --ignore-table=" + ignoreTables.get(i) + " ";
    }
    return s;
  }
  
  /**
   * only replicate this database
   * @param database
   */
  public void addReplicateDatabase(String database) {
    if (onlyDatabase == null) {
      onlyDatabase = new ArrayList<String>();
    }
    onlyDatabase.add(database);
  }

  private void showSlaveStatus() {
    String sql = "SHOW SLAVE STATUS;";
    DatabaseData dd = dd_des;
    try {
      Connection conn = dd.getConnection();
      Statement select = conn.createStatement();
      ResultSet result = select.executeQuery(sql);
      while (result.next()) {
        System.out.println(result.toString());
      }
      select.close();
      select = null;
      result.close();
      result = null;
      conn.close();
    } catch (SQLException e) {
      System.err.println("Error: can't show slave status " + sql);
      e.printStackTrace();
    }
  }
  
}

