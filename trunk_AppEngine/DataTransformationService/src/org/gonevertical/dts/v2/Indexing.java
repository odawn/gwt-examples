package org.gonevertical.dts.v2;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gonevertical.dts.data.ColumnData;
import org.gonevertical.dts.lib.sql.columnlib.ColumnLib;
import org.gonevertical.dts.lib.sql.columnmulti.ColumnLibFactory;
import org.gonevertical.dts.lib.sql.querylib.QueryLib;
import org.gonevertical.dts.lib.sql.querymulti.QueryLibFactory;
import org.gonevertical.dts.lib.sql.transformlib.TransformLib;
import org.gonevertical.dts.lib.sql.transformmulti.TransformLibFactory;


public class Indexing {
	
  private static final Logger log = LoggerFactory.getLogger(Indexing.class);

  // supporting libraries
  private QueryLib ql = null;
  private TransformLib tl = null;
  private ColumnLib cl = null;
  
  private DestinationData destinationData = null;
  
  private ColumnData[] columns = null;
  
  private ArrayList<String> index = new ArrayList<String>();

  // full text indexing
  private boolean fullTextIndex;
  
  public Indexing(DestinationData destinationData) {
    this.destinationData = destinationData;
    // setup injector libraries
    setSupportingLibraries();
  }
  
  /**
   * guice injects the libraries needed for the database
   */
  private void setSupportingLibraries() {
    // get query library
    ql = QueryLibFactory.getLib(destinationData.databaseData.getDatabaseType());
    
    // get column library
    cl = ColumnLibFactory.getLib(destinationData.databaseData.getDatabaseType());
    
    // get tranformation library
    tl = TransformLibFactory.getLib(destinationData.databaseData.getDatabaseType());
  }
  
  public void runIndexColumns(ColumnData[] indexColumns) {
    if (indexColumns == null) {
      return;
    }
    this.columns = indexColumns;  
    indexColumns();
    
    index();
  }
  
  public void setFullText(boolean b) {
    this.fullTextIndex = b;
  }
  
  private void indexColumns() {
    for (int i=0; i < columns.length; i++) {
      if (columns[i]== null) {
        // skip
      } else if (columns[i].getColumnName().matches("Auto_.*") == true) {
        // skip
      } else {
        String in = getIndex(i, columns[i]);
        if (in != null) {
          index.add(in);
        }
      }
    }
  }
  
  private String getIndex(int i, ColumnData columnData) {
    
    String ft = "";
    if (fullTextIndex == true) {
      ft = "ft_";
    }
    
    String nm = "";
    if (columnData.getColumnName().length() < 4) {
      nm = columnData.getColumnName();
    } else {
      nm = columnData.getColumnName().substring(0,4);
    }
    
    String cn = columnData.getColumnName();
    String indexName = "`auto_" + ft + nm + "_"+ i + "`";
    
    // does the index already exist?
    boolean exists = tl.doesIndexExist(destinationData.databaseData, columnData.getTable(), indexName);
    if (exists == true) {
      return null;
    }

    String len = "";
    if (columnData.getSqlType().contains("text") == true) {
      len = "(900)";
    } 
    
    String kind = "";
    if (fullTextIndex == true) {
      kind = "FULLTEXT";
      len = "";
    }
    String sql = "ADD " + kind + " INDEX " + indexName + "(`" + cn + "`" + len + ")";
    
    return sql;
  }
  
  private void index() {
    
    if (index.size() == 0) {
      return;
    }
    String table = columns[0].getTable();
    String sql = "ALTER TABLE " + table + " ";
    for (int i=0; i < index.size(); i++) {
      sql += index.get(i); 
      if (i < index.size() - 1) {
        sql += ",";
      }
    }
    
    System.out.println("index: " + sql);
    ql.update(destinationData.databaseData, sql);
  }
}
