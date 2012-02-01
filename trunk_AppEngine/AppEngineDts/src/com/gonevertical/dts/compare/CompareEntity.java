package com.gonevertical.dts.compare;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import org.gonevertical.dts.lib.FileUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gonevertical.dts.ClientFactory;
import com.gonevertical.dts.download.kind.DownloadKind;
import com.gonevertical.dts.download.totype.DownloadToFile;
import com.gonevertical.dts.utils.ObjectUtils;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.tools.remoteapi.RemoteApiInstaller;

public class CompareEntity {

  private static final Logger log = LoggerFactory.getLogger(DownloadKind.class);

  private ClientFactory cf;

  /**
   * Entity kind
   */
  private String kind;

  private long id;

  private RemoteApiInstaller datastoreAccess;

  public CompareEntity(ClientFactory cf) {
    this.cf = cf;
  }

  public void setEntity(String kind, long id) {
    this.kind = kind;
    this.id = id;
  }

  public void run() {
    if (kind == null) {
      return;
    }

    open();


    int year = 2012 - 1900;
    int month = 2;
    int day = 1;
    Date date = new Date(year, month, day);



    Key key = KeyFactory.createKey(kind, id);

    Entity entityApp = getAppEngineEntity(key);

    Entity entityFile = getFileEntity(key, date);
    
    // TODO compare
    
    update(entityFile);


    close();
  }

  private void update(Entity entityFile) {
    
  }

  private void open() {
    try {
      datastoreAccess = cf.getAppEngineUtils().open();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private void close() {
    cf.getAppEngineUtils().close(datastoreAccess);
  }

  private Entity getAppEngineEntity(Key key) {
    Entity entity = null;    
    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
    try {
      entity = ds.get(key);
    } catch (Exception e) {
      e.printStackTrace();
      log.error("error", e);
    }  
    return entity;
  }

  private Entity getFileEntity(Key key, Date date) {

    String skey = ObjectUtils.convertObjectToString(key);

    String regex = "(" + skey + ")";
    regex = regex.replaceAll("\\{", "\\\\{");
    regex = regex.replaceAll("\\}", "\\\\}");
    
    // TODO file iteration??
    File file = new File("/Users/branflake2267/tmp/slp2012-2-1/2012-02-01__00-00-08___PeopleJdo"); //cf.getFile(kind, date);
    
    String line = FileUtil.findInFileAndReturnLine(file, regex);
    
    Entity e = ObjectUtils.stringToObject(Entity.class, line);
    
    return e;
  }





}
