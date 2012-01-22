package org.gonevertical.dts.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ColumnDataTest {

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testColumnDataStringStringString() {
    String columnTable = "table";
    String columnName = "field";
    String columnType = "VARCHAR(25) DEFAULT NULL";
    ColumnData c = new ColumnData(columnTable, columnName, columnType);
    
    String rtable = c.getTable();
    String rname = c.getName();
    String rtype = c.getSqlType();
    
    assertEquals(columnTable, rtable);
    assertEquals(columnName, rname);
    assertEquals(columnType.toLowerCase(), rtype);
  }

  @Test
  public void testSetValueString() {
    String columnTable = "table";
    String columnName = "field";
    String columnType = "VARCHAR(25) DEFAULT NULL";
    String value = "abcdefghijklmnopqrs";
    ColumnData c = new ColumnData(columnTable, columnName, columnType);
    c.setValue(value);
    String rvalue = c.getValue();
    assertEquals(value, rvalue);
    value = null;
    c.setValue(value);
    rvalue = c.getValue();
    assertEquals(value, rvalue);
    value = "";
    c.setValue(value);
    rvalue = c.getValue();
    assertEquals(null, rvalue);
  }

  @Test
  public void testSetValueLong() {
    String columnTable = "table";
    String columnName = "field";
    String columnType = "VARCHAR(25) DEFAULT NULL";
    long value = 1234567890;
    ColumnData c = new ColumnData(columnTable, columnName, columnType);
    c.setValue(value);
    long rvalue = Long.parseLong(c.getValue());
    assertEquals(value, rvalue);
  }

  @Test
  public void testGetValue_Varchar() {
  	String columnTable = "table";
    String columnName = "field";
    String columnType = "VARCHAR(25) DEFAULT NULL";
    String value = "abcdefHKLKJ*(&^/";
    ColumnData c = new ColumnData(columnTable, columnName, columnType);
    c.setValue(value);
    String rvalue = c.getValue();
    assertEquals(value, rvalue);
  }
  
  @Test
  public void testGetValue_DateTime() {
  	String columnTable = "table";
    String columnName = "field";
    String columnType = "DATETIME DEFAULT NULL";
    String value = "2010-03-30 01:12:05.0";
    ColumnData c = new ColumnData(columnTable, columnName, columnType);
    c.setValue(value);
    String rvalue = c.getValue();
    assertEquals("2010-03-30 01:12:05", rvalue);
  }
  
  @Test
  public void testGetValue_DateTime2() {
  	String columnTable = "table";
    String columnName = "field";
    String columnType = "DATETIME DEFAULT NULL";
    String value = "3/30/2010";
    ColumnData c = new ColumnData(columnTable, columnName, columnType);
    c.setValue(value);
    String rvalue = c.getValue();
    assertEquals("2010-03-30 00:00:00", rvalue);
  }
  
  @Test
  public void testGetValue_DateTime3() {
  	String columnTable = "table";
    String columnName = "field";
    String columnType = "DATETIME DEFAULT NULL";
    String value = "3/30/2010 1:03am";
    ColumnData c = new ColumnData(columnTable, columnName, columnType);
    c.setValue(value);
    String rvalue = c.getValue();
    assertEquals("2010-03-30 01:03:00", rvalue);
  }
  
  @Test
  public void testGetValue_Null() {
  	String columnTable = "table";
    String columnName = "field";
    String columnType = "VARCHAR(25) DEFAULT NULL";
    String value = null;
    ColumnData c = new ColumnData(columnTable, columnName, columnType);
    c.setValue(value);
    String rvalue = c.getValue();
    assertEquals(value, rvalue);
  }
  
  @Test
  public void testGetValue_Null2() {
  	String columnTable = "table";
    String columnName = "field";
    String columnType = "VARCHAR(25) DEFAULT NULL";
    String value = "";
    ColumnData c = new ColumnData(columnTable, columnName, columnType);
    c.setValue(value);
    String rvalue = c.getValue();
    assertEquals(null, rvalue);
  }
  
  @Test
  public void testGetValue_Null3() {
  	String columnTable = "table";
    String columnName = "field";
    String columnType = "VARCHAR(25) DEFAULT NULL";
    String value = "  ";
    ColumnData c = new ColumnData(columnTable, columnName, columnType);
    c.setValue(value);
    String rvalue = c.getValue();
    assertEquals(null, rvalue);
  }

  @Test
  public void testGetValueRaw() {
  	String columnTable = "table";
    String columnName = "field";
    String columnType = "DATETIME DEFAULT NULL";
    String value = "3/30/2010 1:03am";
    ColumnData c = new ColumnData(columnTable, columnName, columnType);
    c.setValue(value);
    String rvalue = c.getValueRaw(); 
    assertEquals(value, rvalue);
  }

  @Test
  public void testGetTestTypeThrow_Int() {
  	String columnTable = "table";
    String columnName = "field";
    String columnType = "INTEGER DEFAULT NULL";
    String value = "string";
    ColumnData c = new ColumnData(columnTable, columnName, columnType);
    c.setValue(value);
    boolean rb = c.getTestTypeThrow();
    assertEquals(true, rb);
  }
  
  @Test
  public void testGetTestTypeThrow_Int2() {
  	String columnTable = "table";
    String columnName = "field";
    String columnType = "INTEGER DEFAULT NULL";
    String value = "012d3456";
    ColumnData c = new ColumnData(columnTable, columnName, columnType);
    c.setValue(value);
    boolean rb = c.getTestTypeThrow();
    assertEquals(false, rb);
  }
  
  @Test
  public void testGetTestTypeThrow_Decimal() {
  	String columnTable = "table";
    String columnName = "field";
    String columnType = "DECIMAL(4,2) DEFAULT NULL";
    String value = "string";
    ColumnData c = new ColumnData(columnTable, columnName, columnType);
    c.setValue(value);
    boolean rb = c.getTestTypeThrow();
    assertEquals(true, rb);
  }
  
  @Test
  public void testGetTestTypeThrow_Decimal2() {
  	String columnTable = "table";
    String columnName = "field";
    String columnType = "DECIMAL(4,2) DEFAULT NULL";
    String value = "1234.02";
    ColumnData c = new ColumnData(columnTable, columnName, columnType);
    c.setValue(value);
    boolean rb = c.getTestTypeThrow();
    assertEquals(false, rb);
  }
  
  @Test
  public void testGetTestTypeThrow_DateTime() {
  	String columnTable = "table";
    String columnName = "field";
    String columnType = "DATETIME DEFAULT NULL";
    String value = "0123456aa";
    ColumnData c = new ColumnData(columnTable, columnName, columnType);
    c.setValue(value);
    boolean rb = c.getTestTypeThrow();
    assertEquals(true, rb);
  }
  
  @Test
  public void testGetTestTypeThrow_DateTime2() {
  	String columnTable = "table";
    String columnName = "field";
    String columnType = "DATETIME DEFAULT NULL";
    String value = "3/30/2010 1:03am";
    ColumnData c = new ColumnData(columnTable, columnName, columnType);
    c.setValue(value);
    boolean rb = c.getTestTypeThrow();
    assertEquals(false, rb);
  }
  
  @Test
  public void testGetValueAsInt() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetValueLength() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetName() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetColumnName() {
    fail("Not yet implemented");
  }

  @Test
  public void testSetColumnAsSql() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetColumnAsSql() {
    fail("Not yet implemented");
  }

  @Test
  public void testSetName() {
    fail("Not yet implemented");
  }

  @Test
  public void testSetColumnName() {
    fail("Not yet implemented");
  }

  @Test
  public void testSetIsPrimaryKey() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetIsPrimaryKey() {
    fail("Not yet implemented");
  }

  @Test
  public void testSetOverwriteWhenBlank() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetOverwriteWhenBlank() {
    fail("Not yet implemented");
  }

  @Test
  public void testSetOverwriteWhenZero() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetOverwriteWhenZero() {
    fail("Not yet implemented");
  }

  @Test
  public void testSetRegex() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetRegex() {
    fail("Not yet implemented");
  }

  @Test
  public void testSetTable() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetTable() {
    fail("Not yet implemented");
  }

  @Test
  public void testSetCase() {
    fail("Not yet implemented");
  }

  @Test
  public void testSetValueAsFunction() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetValueAsFunction() {
    fail("Not yet implemented");
  }

  @Test
  public void testIsFunctionSetForValue() {
    fail("Not yet implemented");
  }

  @Test
  public void testSetIdentity() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetIdentityUse() {
    fail("Not yet implemented");
  }

  @Test
  public void testSetType() {
    fail("Not yet implemented");
  }

  @Test
  public void testSetCharLengthString() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetType() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetCharLength() {
    fail("Not yet implemented");
  }

  @Test
  public void testSetCharLengthInt() {
    fail("Not yet implemented");
  }

  @Test
  public void testDoesValueFitIntoColumn() {
    fail("Not yet implemented");
  }

  @Test
  public void testAlterColumnSizeBiggerIfNeedBe() {
    fail("Not yet implemented");
  }

  @Test
  public void testFixName() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetFieldType() {
    fail("Not yet implemented");
  }

  @Test
  public void testTestSizeOfValue() {
    fail("Not yet implemented");
  }

  @Test
  public void testTestSize_Text() {
    fail("Not yet implemented");
  }

  @Test
  public void testTestSize_Varchar() {
    fail("Not yet implemented");
  }

}
