package org.gonevertical.dts.lib.experimental;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecordAggregator {
  
  private static final Logger log = LoggerFactory.getLogger(RecordAggregator.class);
  
  private String[] groupByFields = null;
  
  // count fields
  private String[] countFields = null;

  // regex count fields
  private String[] regex = null;
  
  private ArrayList<Record> records = new ArrayList<Record>();
  
  // sort and search by
  private Comparator<Record> sort = new RecordSort();
  

  
  public RecordAggregator() {
  }
  
  public void setGroupByFields(String[] fields) {
    this.groupByFields = fields;
  }
  
  public void setCountFields(String[] countfields) {
    this.countFields = countfields;
  }
  
  public void setRegexOnCountFields(String[] regex) {
    this.regex = regex;
  }
  
  public void setData(ResultSet rs) {
    
    String[] gbvalues = new String[groupByFields.length];
    for (int i=0; i < groupByFields.length; i++) {
      try {
        gbvalues[i] = rs.getString(groupByFields[i]);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    
    double[] cfvalues = new double[countFields.length];
    for (int i=0; i < countFields.length; i++) {
      try {
        cfvalues[i] = rs.getDouble(countFields[i]);
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    
    setData(rs, gbvalues, cfvalues);
  }

  private void setData(ResultSet rs, String[] gbvalues, double[] cfvalues) {
    
    if (records.size() == 0) {
      Record r = new Record();
      r.setRegex(regex);
      r.setData(rs, gbvalues, cfvalues);
      records.add(r);
      return;
    }
    
    // sort the collection first before looking into it
    Collections.sort(records, sort);

    // search key
    Record searchKey = new Record();
    searchKey.setData(rs, gbvalues, null);
    
    // search for group values
    int foundIndex = Collections.binarySearch(records, searchKey, sort);
    if (foundIndex > -1) {
      add(cfvalues, foundIndex);
    } else {
      Record r = new Record();
      r.setData(rs, gbvalues, cfvalues);
      records.add(r);
    }
  }

  private void add(double[] cfvalues, int foundIndex) {
    Record foundRecord = records.get(foundIndex);
    foundRecord.sumCounts(cfvalues);
  }


  
  
  
  
  
}
