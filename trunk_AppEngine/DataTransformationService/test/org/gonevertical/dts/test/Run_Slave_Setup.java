package org.gonevertical.dts.test;

import java.io.File;

import org.gonevertical.dts.data.DatabaseData;
import org.gonevertical.dts.lib.sql.MySqlReplication;


public class Run_Slave_Setup {

  public static void main(String[] args) {
    
    DatabaseData dd_src = new DatabaseData(DatabaseData.TYPE_MYSQL, "ark", "3306", "user", "pass", "");
    DatabaseData dd_des = new DatabaseData(DatabaseData.TYPE_MYSQL, "sapphire", "3306", "user", "pass", "");
    
    File tmpDir = new File("/home/branflake2267/files/backup/mysql/replicate");
    
    String replicationUsername = "Replication";
    String replicationPassword = "ReplicationPass";
    String masterHost = "192.168.70.99";
    
    MySqlReplication replicate = new MySqlReplication(dd_src, dd_des, tmpDir, replicationUsername, replicationPassword, masterHost);
    replicate.setDryRun();
    replicate.run();
    
  }
  
}
