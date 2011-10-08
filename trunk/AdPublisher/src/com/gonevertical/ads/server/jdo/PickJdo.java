package com.gonevertical.ads.server.jdo;

import javax.jdo.PersistenceManager;
import javax.jdo.Transaction;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;


@PersistenceCapable(detachable="true")
public class PickJdo {

  @PrimaryKey
  @Persistent
  private String id;

  @Persistent
  private int pick;
  
  public PickJdo() {
    
  }
  
  public static PersistenceManager getPersistenceManager() {
    return PMF.get().getPersistenceManager();
  }

  public static int queryPick(String id, int max) {

    int n = 0;
    
    PersistenceManager pm = getPersistenceManager();
    Transaction tx = pm.currentTransaction();
    try {
      PickJdo j = null;
      
      tx.begin();
      
      try {
        j = pm.getObjectById(PickJdo.class, id);
      } catch (Exception e) {
        j = new PickJdo();
        j.setId(id);
      } 
      
      n = j.getPick();
 
      int nn = n + 1;
      if (nn > max) {
        nn = 0;
      }
      
      j.setPick(nn);
      pm.makePersistent(j);
      tx.commit();
    } catch (Exception e) {
      e.printStackTrace();
    }  finally {
      if (tx.isActive()) {
        tx.rollback();
      }
      pm.close();
    }

    if (n > max) {
      n = 0;
    }
    
    System.out.println("queryPick=" + n);

    return n;
  }


  private void setId(String id) {
    this.id = id;
  }

  private void setPick(int i) {
    pick = i;
  }
  private int getPick() {
    return pick;
  }
}
