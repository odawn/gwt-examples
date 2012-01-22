package org.gonevertical.dts.lib.experimental.install;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


import org.gonevertical.dts.data.DatabaseData;
import org.gonevertical.dts.lib.FileUtil;
import org.gonevertical.dts.lib.ShellUtil;
import org.gonevertical.dts.lib.sql.transformlib.MySqlTransformLib;

/**
 * set a second instance up of mysql on ubuntu
 * 
 * @author branflake2267
 *
 */
public class MySqlSecond {
  
  public static void main(String[] args) {
    new MySqlSecond().run();
  }
  
  private MySqlTransformLib tl = new MySqlTransformLib();
  private FileUtil fu = new FileUtil();
  private ShellUtil st = new ShellUtil();
  
  private int instanceNumber = 2;
  
  public MySqlSecond() {
  }
  
  /**
   * run as root
   */
  public void run() {
    
    // inferred for the moment
    // TODO is this ubuntu?
    // TODO - is there a first instance of MySql?
    // TODO - is there a mysql user? I suppose so since its instance already
    // TODO - is instance running?
    
    // copy conf files
    copyConfFiles();
    
    // copy service conf files
    copyServiceFile();
    
    // setup new my.cnf attributes
    setMySqlConf();
    
    // setup the cluster service stuff
    setMysqlConfNd();
    
    // setup new debian attributes
    setDebianStart();
    setDebianCnf();
    
    // create new data dir
    createNewDataDir();
    
    // init data dir
    setNewDataDir();
    
    // create new place to put pid and socket
    createSocketPidDir();
    
    // setup service conf
    setServiceConf();
    
    // TODO set apparmor - finish this later
    setApparmorConf();
    
    // set the service to run on boot
    setNewService();
    
    // start new instance
    startNewInstance();
    
    // add debian maintance user from FILE: /etc/init.d/mysql2/debian.cnf
    createDebianUser();
    
    // TODO - deal with root no password user
    // TODO - start cluster stuff for second instance?
    
  }
  
  private void createSocketPidDir() {
    File file = new File("/var/run/mysqld" + instanceNumber);
    file.mkdir();
  }
  
  /**
   * create debian user - using the root no password
   */
  private void createDebianUser() {
    File file = new File("/etc/mysql" + instanceNumber + "/debian.cnf");
    String host = "localhost";
    
    // TODO - get property
    //String username = getProperty(file, "user");
    //String password = getProperty(file, "password");
    //DatabaseData dd = new DatabaseData(DatabaseData.TYPE_MYSQL, "localhost", getPort(), "test", "test#", "mysql");
    //tl.createUser(dd, username, password, host);
  }
  
  private void startNewInstance() {
    String file = "/etc/init.d/mysql" + instanceNumber;
    
    String cmd1 = "chmod 755 " + file;
    st.exec_output(cmd1);
    
    String cmd2 = file + " start &";
    st.exec_output(cmd2);
  }
  
  
  /**
   * setup new service
   */
  private void setNewService() {
    String cmd = "update-rc.d /etc/init.d/mysql"  + instanceNumber + " defaults";
    //st.exec_output(cmd);
  }
  
  private void createNewDataDir() {
    File file = new File("/var/lib/mysql" + instanceNumber);
    file.mkdir();
    
    String sfile = file.getAbsolutePath();
    String cmd = "chown -R mysql " + sfile;
    String cmd2 = "chgrp -R mysql " + sfile;
    st.exec_output(cmd);
    st.exec_output(cmd2);
  }
  
  /**
   * init the new data directory
   */
  private void setNewDataDir() {
    String cmd = "/usr/bin/mysql_install_db --user:mysql --datadir:/var/lib/mysql" + instanceNumber;
    st.exec_output(cmd);
  }
  
  private void setApparmorConf() {
    File file = new File("/etc/apparmor.d/usr.sbin.mysqld");
    
    // TODO add /et/mysql2/*.pem r,
    // TODO add /etc/mysql2/conf.d r,
    // TODO add /etc/mysql2/conf.d/ r,
    // TODO add /etc/mysql2/my.cnf r,
    // TODO add /var/run/mysqld/mysqld2.pid w,
    // TODO add /var/run/mysqld/mysqld2.sock w,
  }
  
  private void setMysqlConfNd() {
    File file = new File("/etc/init.d/mysql-ndb" + instanceNumber+"");
    
    if (file.exists() == false) {
      return;
    }
    
    // set CONF and export...
    fu.replaceInFileByLine(file, "/etc/mysql/", "/etc/mysql" + instanceNumber + "/");
    
    file = new File("/etc/init.d/mysql-ndb-mgm" + instanceNumber+"");
    
    // set CONF and export
    fu.replaceInFileByLine(file, "/etc/mysql/", "/etc/mysql" + instanceNumber + "/");
  }
  
  /**
   * set my.cnf attributes
   *
   * FILE: /etc/init.d/mysql2/my.cnf
   */
  private void setMySqlConf() {
    File file = new File("/etc/mysql" + instanceNumber+"/my.cnf");
    
    // set port  3306 to 3307
    changeProperty(file, "port", getPort());
    
    // set datadir /var/lib/mysql to /var/lib/mysql2
    changeProperty(file, "datadir", "/var/lib/mysql" + instanceNumber);
    
    // set socket /var/run/mysqld/mysqld.sock to /var/run/mysqld/mysqld2.sock
    changeProperty(file, "socket", "/var/run/mysqld" + instanceNumber + "/mysqld.sock");
    
    // set pid-file /var/run/mysqld/mysqld.pid to /var/run/mysqld/mysqld2.pid
    changeProperty(file, "pid-file", "/var/run/mysqld" + instanceNumber + "/mysqld.pid");
    
    // log_bin /var/log/mysql/mysql-bin.log to /var/log/mysql/mysql-bin2.log
    fu.replaceInFileByLine(file, "/mysql-bin.log", "/mysql-bin" + instanceNumber + ".log");
    
    // regex entire properties file /etc/init.d/mysql to /etc/init.d/mysql
    fu.replaceInFileByLine(file, "/etc/mysql ", "/etc/mysql" + instanceNumber + " ");
    fu.replaceInFileByLine(file, "/etc/mysql/", "/etc/mysql" + instanceNumber + "/");
  }
  
  /**
   * TODO - is the port already taken???
   * @return
   */
  private String getPort() {
    String sNewPort = Integer.toString(3305 + instanceNumber); 
    return sNewPort;
  }
  
  /**
   * setup the debain start attributes
   * 
   * FILE: /etc/mysql/debian-start
   */
  private void setDebianStart() {
    File file = new File("/etc/mysql" + instanceNumber + "/debian-start");
    
    // set socket from /var/run/mysqld/mysqld.sock to /var/run/mysqld/mysqld2.sock
    //fu.replaceInFileByLine(file, "/mysqld.sock", "/mysqld" + instanceNumber + ".sock");
    // set socket /var/run/mysqld/mysqld.sock to /var/run/mysqld/mysqld2.sock
    changeProperty(file, "socket", "/var/run/mysqld" + instanceNumber + "/mysqld.sock");
    
    // set regex /etc/mysql to /etc/mysql2
    fu.replaceInFileByLine(file, "/etc/mysql", "/etc/mysql"+instanceNumber);
  }
  
  private void setDebianCnf() {
    File file = new File("/etc/mysql" + instanceNumber + "/debian.cnf");
    //fu.replaceInFileByLine(file, "/mysqld\\.sock", "/mysqld" + instanceNumber + ".sock");
    changeProperty(file, "socket", "/var/run/mysqld" + instanceNumber + "/mysqld.sock");
  }
  
  /**
   * set service start properties
   * 
   *  FILE: /etc/init.d/mysql2
   */
  private void setServiceConf() {
    File file = new File("/etc/init.d/mysql" + instanceNumber);
    
    // set MYADMIN="/usr/bin/mysqladmin --defaults-file=/etc/mysql/debian.cnf" 
    // set CONF=/etc/mysql/my.cnf to CONF=/etc/mysql2/my.cnf
    // set by regex: /etc/mysql to /etc/mysql2
    // set by regex: /etc/init.d/mysql to /etc/init.d/mysql2
    fu.replaceInFileByLine(file, "/mysql ", "/mysql" + instanceNumber + " ");
    fu.replaceInFileByLine(file, "/mysql/", "/mysql" + instanceNumber + "/");
  }
  
  private void copyConfFiles() {
    String newDir = "/etc/mysql" + instanceNumber + "";
    File fdir = new File(newDir);
    fdir.mkdir();
    
    String command = "cp -R /etc/mysql/. " + newDir + "";
    System.out.println(command);
    st.exec_output(command);
  }
  
  private void copyServiceFile() {
    File src = new File("/etc/init.d/mysql");
    File dst = new File("/etc/init.d/mysql" + instanceNumber);
    copyFile(src, dst);
    
    src = new File("/etc/init.d/mysql-ndb");
    dst = new File("/etc/init.d/mysql-ndb" + instanceNumber);
    copyFile(src, dst);
    
    src = new File("/etc/init.d/mysql-ndb-mgm");
    dst = new File("/etc/init.d/mysql-ndb-mgm" + instanceNumber);
    copyFile(src, dst);
  }
  
  private void copyFile(File src, File dst) {
    if (src.exists() == false) {
      System.out.println("skipping file copy: " + src.getAbsolutePath());
      return;
    }
    if (dst.exists() == false) {
      try {
        dst.createNewFile();
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    }
    InputStream in = null;
    try {
      in = new FileInputStream(src);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    OutputStream out = null;
    try {
      out = new FileOutputStream(dst);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    byte[] buf = new byte[1024];
    int len;
    try {
      while ((len = in.read(buf)) > 0) {
        out.write(buf, 0, len);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      in.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    try {
      out.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
  
  private void changeProperty(File file, String key, String value) {
    String regexFind = key + ".*?=.*";
    String regexReplace = key + " = " + value;
    fu.replaceInFileByLine(file, regexFind, regexReplace);
  }
  /*
  private void changeProperty_old(File file, String key, String value) {
    PropertiesConfiguration config = null;
    try {
      config = new PropertiesConfiguration(file);
    } catch (ConfigurationException e) {
      e.printStackTrace();
    }
    config.setProperty(key, value);
    try {
      config.save();
    } catch (ConfigurationException e) {
      e.printStackTrace();
    }
  }
  
  private String getProperty(File file, String key) {
    PropertiesConfiguration config = null;
    try {
      config = new PropertiesConfiguration(file);
    } catch (ConfigurationException e) {
      e.printStackTrace();
    }
    String value = (String) config.getProperty(key);
    return value;
  }
  */
  
  private void createNewFile(File file) {
    String sfile = file.getAbsolutePath();
    String cmd = "touch " + sfile;
    st.exec_output(cmd);
  }
}
