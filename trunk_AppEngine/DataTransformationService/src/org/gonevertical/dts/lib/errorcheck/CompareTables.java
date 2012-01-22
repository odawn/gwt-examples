package org.gonevertical.dts.lib.errorcheck;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gonevertical.dts.data.ColumnData;
import org.gonevertical.dts.data.DatabaseData;
import org.gonevertical.dts.lib.csv.Csv;
import org.gonevertical.dts.lib.sql.columnlib.ColumnLib;
import org.gonevertical.dts.lib.sql.columnmulti.ColumnLibFactory;
import org.gonevertical.dts.lib.sql.querylib.QueryLib;
import org.gonevertical.dts.lib.sql.querymulti.QueryLibFactory;
import org.gonevertical.dts.lib.sql.transformlib.TransformLib;
import org.gonevertical.dts.lib.sql.transformmulti.TransformLibFactory;

public class CompareTables {

  private static final Logger log = LoggerFactory.getLogger(CompareTables.class);
	
	private long limitOffset = 5000;
	
	//source database settings
  private DatabaseData database_src = null;
  
  // destination database settings
  private DatabaseData database_dest = null;
	
  private QueryLib ql_src = null;
  private TransformLib tl_src = null;
  private ColumnLib cl_src = null;
  private QueryLib ql_des = null;
  private TransformLib tl_des = null;
  private ColumnLib cl_des = null;
  
  private ColumnData[] columnData_src = null;
  private ColumnData[] columnData_des = null;
  
  private String tableLeft = null;
  private String tableRight = null;
  
  private String srcWhere = null;
  
  private boolean matches = false;

	private ColumnData[] columnIdentities;

	private ColumnData[] columnSkip;

	private String sqlOrderBy;

	private boolean failure = false;
	private int failureCount = 0;

	private long index;

	private int totalThreadCount = 1;
  
	public CompareTables(DatabaseData database_src, DatabaseData database_dest) {
    this.database_src = database_src;
    this.database_dest = database_dest;
    setSupportingLibraries();
  }
	
	/**
   * guice injects the libraries needed for the database
   */
  private void setSupportingLibraries() {
    ql_src = QueryLibFactory.getLib(database_src.getDatabaseType());
    cl_src = ColumnLibFactory.getLib(database_src.getDatabaseType());
    tl_src = TransformLibFactory.getLib(database_src.getDatabaseType());
    ql_des = QueryLibFactory.getLib(database_dest.getDatabaseType());
    cl_des = ColumnLibFactory.getLib(database_dest.getDatabaseType());
    tl_des = TransformLibFactory.getLib(database_dest.getDatabaseType());
  }
	
  public void checkTablesCounts(String[] tables) {
  	for (int i=0; i < tables.length; i++) {
  		checkTableCount(tables[i]);
  	}
  }
  
  public void checkTableCounts(String tableLeft, String tableRight) {
  	failure = false;
  	failureCount = 0;
  	checkTableCount(tableLeft, tableRight);
  }
  
  private void checkTableCount(String leftTable, String rightTable) {
  	matches = false;
  	
  	String where = " WHERE (1=1) ";
  	where += getSrcWhere();
  	
  	String sqlL = "SELECT COUNT(*) AS t FROM " + leftTable + where;
  	String sqlR = "SELECT COUNT(*) AS t FROM " + rightTable + where;
  	
  	System.out.println(sqlL);
  	
  	long left = ql_src.queryLong(database_src, sqlL);
  	long right = ql_des.queryLong(database_dest, sqlR);
  
  	String match = "";
  	if (left == right) {
  		 match = "TRUE";
  		 matches = true;
  	} else {
  		match = "FALSE";
  		matches = false;
  	}
  	if (matches == false) {
  		log.error("CompareTables.checkTableCount(): " +
  				"LeftTable: " + leftTable + " left: " + left + " RightTable: "+ rightTable + " right: " + right + " " + match + " offby: " + (left-right));
  	}
  	
  	log.info("CompareTables.checkTableCount(): " +
				"LeftTable: " + leftTable + " left: " + left + " RightTable: "+ rightTable + " right: " + right + " " + match + " offby: " + (left-right));
  }
  
  private String getSrcWhere() {
    String s = "";
    if (srcWhere != null && srcWhere.length() > 0) {
      s = " AND " + srcWhere;
    }
    return s; 
  }
  
  private void checkTableCount(String table) {
  	matches = false;
  	
  	String sql = "SELECT COUNT(*) AS t FROM " + table;
  	
  	long left = ql_src.queryLong(database_src, sql);
  	long right = ql_des.queryLong(database_dest, sql);
  
  	String match = "";
  	if (left == right) {
  		 match = "TRUE";
  		 matches = true;
  	} else {
  		match = "FALSE";
  		matches = false;
  	}
  	log.info("table: " + table + " left: " + left + " right: " + right + " " + match + " offby: " + (left-right));
  }
	
  public void compareTableData(String tableLeft, String tableRight, ColumnData[] columnIdentities, ColumnData[] columnSkip) {
  	this.columnIdentities = columnIdentities;
  	this.columnSkip = columnSkip;
  	compareTableData(tableLeft, tableRight);
  }
  
  public void compareTableData(String tableLeft, String tableRight) {
  	this.tableLeft = tableLeft;
  	this.tableRight = tableRight;
  	
  	failure = false;
  	failureCount = 0;
  	
  	processSrc();
  }
  
  private void processSrc() {
    
    String where = " WHERE (1=1) ";
    where += getSrcWhere();

  	String sql = "SELECT COUNT(*) AS t FROM `" + tableLeft + "` " + where;

  	log.info(sql);

  	long total = ql_src.queryLong(database_src, sql);
  	index = total;
  	long lim = limitOffset;
  	long totalPages = (total / lim);
  	if (totalPages == 0) {
  		totalPages = 1;
  	}

  	CompareTablesThread[] ctt = new CompareTablesThread[totalThreadCount];
  	Thread[] threads = new Thread[totalThreadCount];

  	long offset = 0;
  	long limit = 0;

  	for (int i=0; i < totalPages; i++) { // loop through pages

  		for (int t=0; t < totalThreadCount; t++) { // loop through pages at a time on threads

  			if (i==0) {
  				offset = 0;
  				limit = lim;
  			} else {
  				offset = ((i + 1 ) * lim) - lim;
  				limit = lim;
  			}
  			
  			ctt[t] = new CompareTablesThread(database_src, database_dest);
  			ctt[t].setData(tableLeft, tableRight, srcWhere, columnIdentities, columnSkip);
    		ctt[t].setOffset(offset, limit, index);
    		threads[t] = new Thread(ctt[t]);

    		// increment count on inner loop of threads
  			if (totalThreadCount > 1) {
  				i++;
  			}

  			index = index - lim;
  		}
  		
  		for (int t=0; t < totalThreadCount; t++) {
    		threads[t].start();
    	}
    	
    	for (int t=0; t < totalThreadCount; t++) {
    		try {
  	      threads[t].join();
        } catch (InterruptedException e) {
  	      e.printStackTrace();
  	      log.error("couldnt join thread");
        }
    	}
    	
    	// count the failures in each thread
    	for (int t=0; t < totalThreadCount; t++) {
    		failureCount += ctt[t].getFailureCount();
    	}

  	} // end for

  } // end processSrc

	public void setWhere(String where) {
  	this.srcWhere = where;
  }
  
  public boolean getMatches() {
  	return matches;
  }

	public void setOrderBy(String sqlOrderBy) {
	  this.sqlOrderBy = sqlOrderBy;
  }

	public boolean getFailure() {
	  return failure;
  }
	
	public int getFailureCount() {
		return failureCount;
	}

	public void setThreads(int totalThreadCount) {
		this.totalThreadCount = totalThreadCount;
  }
  
}
