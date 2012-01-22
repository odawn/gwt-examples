package org.gonevertical.dts.data;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * match source field and transform them into the destination field
 * 
 * RESERVED COLUMNS [ImportID, DateCreated, DateUpdated]
 * 
 * @author branflake2267
 *
 */
public class FieldData implements Comparable<FieldData> {
	
  private static final Logger log = LoggerFactory.getLogger(FieldData.class);
  
  public final static int CHANGECASE_LOWER = 1;
  public final static int CHANGECASE_UPPER = 2;
  public final static int CHANGECASE_SENTENCE = 3;
  
  // source field name
	public String sourceField;
	
	// destination field name
	public String destinationField;
	
	// when using identity insert index on a beginning type
	// you can force a column type for optimization of the field in the beginning
	// otherwise an identity column starts with varchar(50)
	public String destinationField_ColumnType = null; // like VARCHAR(50)
	
	// Transfer: this designates this column is used as the primary key
	public boolean isPrimaryKey = false;
	
	// NOT USING YET: Transfer: this designates this field has multiple fields that are needed to make the record unique, instead of using a primary key
	//public boolean isIdentity = false;
	
	// Transfer: on copy when dest value is blank. (Zero is not blank, and is something)
	public boolean onlyOverwriteBlank = true;
	
	// Transfer: if a zero shows up, it can be overwritten.
	public boolean onlyOverwriteZero = true;
	
	// Transfer: only take this from the source field
	public String regexSourceField = null;
	
	// Transfer: different destination table for this value
	public String differentDestinationTable = null;
	
	public int changeCase = 0;
	
	// Transfer: different destination table hard code column + values - one to many definition
	public HashMap<String, String> hardOneToMany = null; // new HashMap<String, String>(); 
	
	public boolean updateOnlyWhenBigger = false;
	
	public boolean deleteExisting = false;
	
	/**
	 * constructor
	 */
	public FieldData() {
	}

	/**
	 * sort the fields by source field
	 */
	public int compareTo(FieldData b) {
		
		if (sourceField == null) {
			return 0;
		}
		
		if (b == null) {
			return 0;
		}
		
		return sourceField.compareToIgnoreCase(b.sourceField);
	}

	/**
	 * get source field index
	 * 
	 * @param fieldData
	 * @param sourceField
	 * @return
	 */
	public static int getSourceFieldIndex(FieldData[] fieldData, String sourceField) {
	  if (fieldData == null) {
	    return -1;
	  }
	  Comparator<FieldData> sort = new FieldDataComparator();
	  Arrays.sort(fieldData);
	  
	  FieldData key = new FieldData();
	  key.sourceField = sourceField;
	  
	  int index = Arrays.binarySearch(fieldData, key, sort);
	  return index;
	}

  public void setData(String sourceField, String destinationField) {
    this.sourceField = sourceField;
    this.destinationField = destinationField;
  }

	
}
