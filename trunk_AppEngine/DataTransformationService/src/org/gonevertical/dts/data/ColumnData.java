package org.gonevertical.dts.data;

import java.math.BigDecimal;

import org.apache.commons.lang3.text.WordUtils;
import org.gonevertical.dts.lib.StringUtil;
import org.gonevertical.dts.lib.datetime.DateTimeParser;
import org.gonevertical.dts.lib.sql.transformlib.MySqlTransformLib;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ColumnData implements Cloneable {
  
  private static final Logger log = LoggerFactory.getLogger(ColumnData.class);
  
  /**
   * supported column types. 
   * keeping things somewhat generic to start with. 
   * 
   * note: ColumnLib.createSqlColumnSyntax(ColumnData) is uses this
   * note: optimise also has a need for this (this method nees to move to ColumnLib.
   */
  public static enum Type {
    
    /**
     * place holder
     */
    EMPTY("CHAR", 0),
    
    /**
     * 000001234
     */
    ZEROFILL("INTEGER", 11),
    
    /**
     * true|false
     */
    BOOLEAN("BOOLEAN"),
    
    /**
     * date
     */
    DATETIME("DATETIME"), 
    
    /**
     * up to 255
     */
    VARCHAR("VARCHAR", 255),
    
    /**
     * int
     */
    INTEGER("INTEGER", 11),
    
    /**
     * 11234.1234
     * {@link http://dev.mysql.com/doc/refman/5.1/en/precision-math-decimal-changes.html}
     * {@link https://www.google.com/webhp?sourceid=chrome-instant&ie=UTF-8&ion=1#sclient=psy-ab&hl=en&site=webhp&source=hp&q=double%2064%20bit&pbx=1&oq=&aq=&aqi=&aql=&gs_sm=&gs_upl=&fp=918a461f06fdbea1&ion=1&ion=1&bav=on.2,or.r_gc.r_pw.r_cp.,cf.osb&fp=918a461f06fdbea1&ion=1&biw=977&bih=1102}
     */
    DECIMAL("DECIMAL", 65,17),
    
    /**
     * 
     */
    TEXT("TEXT"),
    
    /**
     * binary
     */
    BLOB("BLOB"),
    
    /**
     * BIG INT
     */
    BIGINT("BIGINT", 20),
    
    /**
     * Tiny Int 
     */
    TINYINT("TINYINT", 1),
    
    /**
     * char
     */
    CHAR("CHAR", 1);
    
    
    private String sqlType;
    private Integer left;
    private Integer right;
    
    Type(String sqlType) {
      this.sqlType = sqlType;
    }
    
    Type(String sqlType, Integer length) {
      this.sqlType = sqlType;
      this.left = length;
    }
    
    Type(String sqlType, Integer left, Integer right) {
      this.sqlType = sqlType;
      this.left = left;
      this.right = right;
    }
    
    public String value() {
      return name();
    }

    public static Type fromValue(String type) {
      if (type.contains("(") == true) {
       type = type.substring(0, type.indexOf("("));
      }
      return valueOf(type.toUpperCase());
    }

    public String toString() {
      return name();
    }
    
    public Integer getLeft() {
      return left;
    }
    
    public Integer getRight() {
      return right;
    }
    
    public String getSqlType() {
      return sqlType;
    }
  }
	
  // change value case
  public final static int CHANGECASE_LOWER = 1;
  public final static int CHANGECASE_UPPER = 2;
  public final static int CHANGECASE_SENTENCE = 3;

  // column data types 
  //public static final int FIELDTYPE_DATETIME = 1;
  //public static final int FIELDTYPE_VARCHAR = 2;
  //public static final int FIELDTYPE_INT_ZEROFILL = 3;
  //public static final int FIELDTYPE_INT = 4;
  //public static final int FIELDTYPE_DECIMAL = 5;
  //public static final int FIELDTYPE_EMPTY = 6;
  //public static final int FIELDTYPE_TEXT = 7;
  //public static final int FIELDTYPE_BLOB = 8;
  //public static final int FIELDTYPE_BOOLEAN = 9;


  // type of index
  public static final int INDEXKIND_DEFAULT = 1;
  public static final int INDEXKIND_FULLTEXT = 2;

  
  
  
  // is this column a primary key?
  private boolean isPrimaryKey = false;

  // when using multiple columns for identity, for similarity matching
  private boolean usedForIdentity = false;

  /**
   * column name
   * TODO public access to this var is deprecated, changing to method access
   */
  private String name = "";

  private String columnAsSql;

  // column field type - like INTEGER DEFAULT 0
  // TODO public access to this var is deprecated, changing to method access
  //private String columnSqlType;
  
  // new var to show what type of column it is
  private Type type = Type.TEXT;

  // column field length for the given column type
  // when type is set, this is discovered
  private int lengthChar = 0;

  /**
   * for decimal left of .
   */
  
  private int lengthChar_left = 0;
  /**
   * for decimal right of .
   */
  private int lengthChar_right = 0;

  // columns associated value
  private String value = null;

  // set the value as a function
  // this will replace value when looking for retreval
  private String valueIsFunction = null;

  // true:overwrite any value false:only update on blank
  private boolean overwriteOnlyWhenBlank = false;

  // true: if a zero shows up its ok to overwrite
  private boolean overwriteOnlyWhenZero = false;

  // set the value using regex
  private String regex = null;

  // table that the column resides in, optional
  private String table = null;

  // set with static constant above
  private int changeCase = 0;

  // test the value type matches Sql Type throw
  private boolean testTypeThrow = false;
	private boolean deleteExistingValue;
  
  /**
   * constructor
   */
  public ColumnData() {
  }

  /**
   * constructor - create a column object 
   * 
   * @param table table name
   * @param name column name
   * @param columnSqlType columnType ie. [TEXT DEFAULT NULL]
   */
  public ColumnData(String table, String name, String columnSqlType) {
    setTable(table);
    setColumnName(name);
    setType(columnSqlType);
  }
  
  /**
   * create a new column
   * @param table
   * @param name
   * @param type
   */
  public static ColumnData newInstance(String table, String name, Type type) {
    ColumnData c = new ColumnData();
    c.setTable(table);
    c.setColumnName(name);
    c.setType(type);
    return c;
  }

  /**
   * set column {@link Type}
   * @param fieldtype
   */
  private void setType(Type fieldtype) {
    this.type = fieldtype;
  }
  
  /**
   * get the Column {@link Type}
   * @return
   */
  public Type getType() {
    return type;
  }
  
  /**
   * set chars to left for decimal 1234.0
   * @param lengthChar_left
   */
  public void setDecimalPositionLeft(int lengthChar_left) {
    this.lengthChar_left = lengthChar_left;
  }
  
  /**
   * get chars to left for decimal
   * @return
   */
  public int getDecimalPositionLeft() {
    return lengthChar_left;
  }
  
  /**
   * set decimal chars to right 0.1234
   * @param lengthChar_right
   */
  public void setDecimalPositionRight(int lengthChar_right) {
    this.lengthChar_right = lengthChar_right;
  }
  
  /**
   * get decimal chars to right 0.1234
   * @return
   */
  public int getDecimalPositionRight() {
    return lengthChar_right;
  }

  /**
   * set value of column 
   * 
   *  NOTE: by default, all empty (white space) values get turned into null
   * 
   *  Optional: ChangeCase
   *  Optional: run regex on value
   * 
   * @param value
   */
  public void setValue(String value) {
  	
  	try {
  		
	    if (value != null) {
	    	value = value.trim();
	    }
	    
	    // always set empty to null 
	    // TODO make this optional to turn this off
	    if (value != null && value.length() == 0) {
	    	value = null;
	    }

	    // optional: if change case is set on, change the case of the value
	    if (value != null && changeCase > 0) {
	      value = changeCase(value);
	    }

	    // optional: run regex on the value
	    if (value != null && regex != null) {
	      value = StringUtil.getValue(regex, value);
	    }
	    
    } catch (Exception e) {
    	this.value = null;
	    e.printStackTrace();
	    log.error("ColumnData.setValue(): Value Error:", e);
    }
    
    this.value = value;
  }

  /**
   * set value of column
   * 
   * @param value
   */
  public void setValue(Long value) {
    try {
    	if (value != null) {
    		this.value = Long.toString(value);
    	} else {
    		this.value = null;
    	}
    } catch (Exception e) {
	    e.printStackTrace();
	    log.error("setValue(Long): Long Value Error: ", e);
	    this.value = null;
    }
  }

  /**
   * get value
   *   will return valueIsFunction if set, this will overide value
   * 
   * @return
   */
  public String getValue() {
    String v = null;

    try {
	    if (valueIsFunction != null) {
	      v = valueIsFunction;
	    } else {
	      v = this.value;
	    }

	    // the next only affect given types
	    // if type is int, check to be sure its an int
	    if (type == Type.INTEGER || type == Type.DECIMAL) {
	      v = getValueAsInt(value);
	    }

	    // if type is datetime, lets deal with it now
	    if (type == Type.DATETIME) {
	      v = getValueAsDateTime(value);
	    }
	    
	    if (v != null && v.trim().length() == 0) {
	      v = null;
	    }
	    
    } catch (Exception e) {
	    e.printStackTrace();
	    log.error("ColumnData.getValue() Error v=" + v, e);
	    v = null;
    }

    return v;
  }
  
  /**
   * get a value that hasn't been tranformed
   * @return
   */
  public String getValueRaw() {
    return this.value;
  }
  
  /**
   * does the value(string) fit the sql type
   * @return
   */
  public boolean getTestTypeThrow() {
    testTypeThrow = false;
    getValue();
    return testTypeThrow;
  }

  /**
   * when column type is datetime, transform it to datetime and be sure datetime correct on the way out
   *    TODO on setting datetime, do I parse it too? 
   * 
   * @param value
   * @return
   */
  private String getValueAsDateTime(String value) {
    if (type == Type.DATETIME) {
      if (value == null) {
        value = null;
      } else if (value.trim().length() == 0) {
        value = null;
      } else {
        // TODO move dtp to class instance var
        DateTimeParser dtp = new DateTimeParser();
        value = dtp.getDateMysql(value);
        if (dtp.isDate == false) {
          value = null;
          testTypeThrow = true;
        }
      }
    }
    return value;
  }

  /**
   * when column type is int - be sure to check the value is really an int
   * 
   * @param value
   * @return
   */
  public String getValueAsInt(String value) {

    if (type == Type.INTEGER) {
      if (value == null) {
        value = null;
      } else if (value.trim().length() == 0) {
        value = null;
      } else {
        try {
        	if (value != null && value.equals(".")) {
        		value = "0";
        	}
        	
          // change (1234) to negative
          if (value != null && value.matches("[\\(].*[\\)]")) {
            value = value.replaceAll("[\\)\\(]", "");
            value = "-" + value;
          }
          // take out all non digit characters except . - and 0-9
          if (value != null) {
            value = value.replaceAll("[^0-9\\.\\-]", ""); 
          }
          BigDecimal bd = new BigDecimal(value);
          value = bd.toString();
        } catch (NumberFormatException e) {
          value = null;
          testTypeThrow = true;
        }
      }
    } else if (type == Type.DECIMAL) {
      if (value == null) {
        value = null;
      } else if (value.trim().length() == 0) {
        value = null;
      } else {
        try { 
        	
        	// when retrieving a value it can come like "." representing "0.0"
        	if (value != null && value.equals(".")) {
        		value = "0.0";
        	}
        	
          // change (1234) to negative
          if (value != null && value.matches("[\\(].*[\\)]")) {
            value = value.replaceAll("[\\)\\(]", "");
            value = "-" + value;
          }
          // take out all non digit characters except . - and 0-9
          if (value != null) {
            value = value.replaceAll("[^0-9\\.\\-]", "");
          }
          BigDecimal bd = new BigDecimal(value);
          value = bd.toString();
        } catch (NumberFormatException e) {
          value = null;
          testTypeThrow = true;
        }
      }
    }

    return value;
  }

  public int getValueLength() {
    int l = 0;
    if (value != null) {
      l = value.length();
    }
    return l;
  }

  public String getName() {
    return name;
  }
  
  public String getColumnName() {
    return name;
  }

  public void setColumnAsSql() {
    if (name.toLowerCase().matches(".*[\040]as[\040].*") == false) {
      return;
    }

    // save (select *...) as sql
    columnAsSql = name;

    String regex = ".*[\040]as[\040](.*)";
    String c = StringUtil.getValue(regex, name.toLowerCase());
    if (c != null) {  
      name = c.trim();
    }

  }

  public String getColumnAsSql() {
    return columnAsSql;
  }

  public void setName(String name) {
    setColumnName(name);
  }

  public void setColumnName(String name) {
    this.name = name;
    fixName();
    setColumnAsSql(); // in case it has sql AS in it
  }

  public void setIsPrimaryKey(boolean b) {
    isPrimaryKey = b;
  }

  public boolean getIsPrimaryKey() {
    return isPrimaryKey;
  }

  public void setOverwriteWhenBlank(boolean b) {
    this.overwriteOnlyWhenBlank = b;
  }

  public boolean getOverwriteWhenBlank() {
    return this.overwriteOnlyWhenBlank;
  }

  public void setOverwriteWhenZero(boolean b) {
    this.overwriteOnlyWhenZero = b;
  }

  public boolean getOverwriteWhenZero() {
    return this.overwriteOnlyWhenZero;
  }

  public void setRegex(String regex) {
    this.regex = regex;
  }

  public String getRegex() {
    return regex;
  }

  public void setTable(String table) {
    this.table = table;
  }

  public String getTable() {
    return this.table;
  }

  /**
   * set with constant
   * 
   * @param changeCase
   */
  public void setCase(int changeCase) {
    this.changeCase = changeCase;
  }

  /**
   * set the value as function
   * 
   * @param sqlfunction
   */
  public void setValueAsFunction(String sqlfunction) {
    this.valueIsFunction = sqlfunction;
  }

  /**
   * get the value 
   */
  public String getValueAsFunction() {
    return valueIsFunction;
  }

  /**
   * is the value set as a function
   * @return
   */
  public boolean isFunctionSetForValue() {
    boolean b = false;
    if (valueIsFunction != null) {
      b = true;
    }
    return b;
  }

  /**
   * set this column as use as an identity, like three columns get set for similarity matching
   * 
   * @param b
   */
  public void setIdentity(boolean b) {
    this.usedForIdentity = b;
  }

  /**
   * is this column used for identity
   * 
   * @return
   */
  public boolean getIdentityUse() {
    return usedForIdentity;
  }

  /**
   * set column type and extract length
   * 
   * @param columnType
   */
  public void setType(String columnType) {
    type = Type.fromValue(columnType);
    setCharLength(columnType);
  }

  /**
   * set the lengths INT(10) or decimal(10,2)
   * 
   * @param columnType
   */
  public void setCharLength(String columnType) {

    if (columnType.contains(",")) { // decimal column type
      lengthChar_left = 0;
      lengthChar_right = 0;
      setDecimalLengths(columnType);
    } else {
      String len = StringUtil.getValue("([0-9]+)", columnType);
      if (len != null && len.length() > 0) {
        lengthChar = Integer.parseInt(len);
      } else {
        lengthChar = 0;
      }
    }

  }

  private void setDecimalLengths(String s) {
    int l = 0;
    int r = 0;
    if (s.contains(",")) {
      String sv = StringUtil.getValue("([0-9]+,[0-9]+|[0-9]+,[\040]+?[0-9]+)", s);
      String[] a = sv.split(",");
      if (a != null) {
        if (a[0] != null) {
          l = Integer.parseInt(a[0].trim());
        }
        if (a[1] != null) {
          r = Integer.parseInt(a[1].trim());
        }
      } else {
        l = 0;
        r = 0;
      }
      
    } else {
      l = s.length();
    }
    lengthChar = 0;
    lengthChar_left = l;
    lengthChar_right = r;
  }

  /**
   * get type with no changes
   * @return
   */
  public String getSqlType() {
    return type.getSqlType();
  }

  /**
   * get the length of varchar(lengthChar)
   * 
   * @return
   */
  public int getCharLength() {
    return lengthChar;
  }

  /**
   * change the lengthChar
   *   used for altering smaller
   * @param length
   */
  public void setCharLength(int length) {
    this.lengthChar = length;
  }

  /**
   * TODO - add all the column types
   * 
   * @return
   */
  public boolean doesValueFitIntoColumn() {
    String columnSqlType = type.getSqlType().toLowerCase();

    boolean b = false;
    if (columnSqlType.contains("text") == true) {
      b = doesValueFit_Text();
    } else if (columnSqlType.contains("varchar") == true) {
      b = doesValueFit_Varchar();
    } else if (columnSqlType.contains("bigint") == true) {
      b = doesValueFit_BigInt();
    } else if (columnSqlType.contains("tinyint") == true) {
      b = doesValueFit_TinyInt();
    } else if (columnSqlType.contains("int") == true) {
      b = doesValueFit_Int();
    }  else if (columnSqlType.contains("dec") == true) {
      b = doesValueFit_Decimal();
    } else if (columnSqlType.contains("datetime") == true) {
      b = true; // date doesn't change
    } else {
      b = true;
    }
    // TODO - add more types

    return b;
  }

  private boolean doesValueFit_Text() {
    boolean b = false;
    try {
	    if (value == null) {
	      b = true;
	    } else if (value != null && value.length() <= 65536) { //65536 bytes 2^16
	      b = true;
	    }
    } catch (Exception e) {
	    e.printStackTrace();
    }
    return b;
  }

  private boolean doesValueFit_Varchar() {
    boolean b = false;
    if (value == null) {
      b = true;
    } else if (value.length() <= lengthChar) { // 255 bytes
      b = true;
    }
    return b;
  }

  private boolean doesValueFit_Int() {
    boolean b = false;
    if (value != null && value.length() <= 9) {
      b = true;
    }
    return b;
  }

  private boolean doesValueFit_TinyInt() {
    boolean b = false;
    if (value != null && value.length() <= 1) {
      b = true;
    }
    return b;
  }

  private boolean doesValueFit_BigInt() {
    boolean b = false;
    if (value != null && value.length() < 20) {
      b = true;
    }
    return b;
  }

  // TODO - this needs adjustment of the left value, and total values are different
  private boolean doesValueFit_Decimal() {
    //System.out.println("current left: " + lengthChar_left + " right: " + lengthChar_right);
    boolean b = true;
    char per = ".".charAt(0);
    if (value != null && value.contains(Character.toString(per))) {
      String[] a = value.split("\\.");
      int l = a[0].trim().length();
      int r = a[1].trim().length();

      //System.out.println("l: " + l + " r: " + r);
      if (r > lengthChar_right) {
        lengthChar_right = r;
        b = false;
      }

      l = l + lengthChar_right;

      if (l > lengthChar_left) {
        lengthChar_left = l;
        b = false;
      }
      //System.out.println("new left: " + lengthChar_left + " right: " + lengthChar_right);
    }
    return b;
  }

  /**
   * alter the column size if need be
   */
  public void alterColumnSizeBiggerIfNeedBe(DatabaseData dd) {
    if (value == null) {
      return;
    }
    // will the data fit
    boolean b = doesValueFitIntoColumn();
    if (b == true) {
      return;
    }
    // alter column size
    alterColumnToBiggerSize(dd);
  }

  private void alterColumnToBiggerSize(DatabaseData dd) {
    int l = 0;
    if (value == null) {
      l = 255;
    } else {
      l = value.getBytes().length;
    }
    
    String columnSqlType = type.getSqlType().toLowerCase();
    
    if (columnSqlType.contains("text") == true) {
      if (l >= 255) {
        setType("TEXT DEFAULT NULL");
      } else if (columnSqlType.contains("varchar") == true) {
        setType("VARCHAR(" + l + ") DEFAULT NULL");
      }
    } else if (columnSqlType.contains("varchar") == true) {
      if (l >= 255) {
        setType("TEXT DEFAULT NULL");
      } else if (columnSqlType.contains("varchar") == true) {
        setType("VARCHAR(" + l + ") DEFAULT NULL");
      }
    } else if (columnSqlType.contains("int") == true) {
      if (value != null && value.length() < 8) {
        setType("INT DEFAULT 0");
      } else if (value != null && value.length() >= 8) {
        setType("BIGINT DEFAULT 0");
      } 
    } else if (columnSqlType.contains("dec") == true) {
      setType("DECIMAL(" + lengthChar_left + "," + lengthChar_right + ") DEFAULT 0.0");

    } else if (columnSqlType.contains("datetime") == true) {
      // never needed
    } else {
      System.out.println("skipping alterColumnToBiggerSize()");
      return;
    }

    new MySqlTransformLib().alterColumn(dd, this);
  }

  /**
   * fix the column name < 64 and characters that are SQL friendly
   */
  public void fixName() {
    if (name.length() > 64) {
      name = name.substring(0, 63);
    }
    name = name.trim();
    name = name.replaceAll("#", "_Num");
    name = name.replaceAll("%", "_per");
    name = name.replaceAll("\\.", "_");
    name = name.replaceAll(" ", "_");
    name = name.replaceAll("[^\\w]", "");
    name = name.replaceAll("[\r\n\t]", "");
    name = name.replaceAll("(\\W)", "");
  }

  /**
   * TODO - finish this
   * 
   * @param columnType
   * @return
   */
  public Type getFieldType(String columnType) {

    String type = columnType.toLowerCase();

    Type fieldType = null;
    if (type.contains("text")) {
      fieldType = Type.VARCHAR; // varchar
    } else if (type.contains("date")) {
      fieldType = Type.DATETIME;
    } else if (type.contains("varchar")) {
      fieldType = Type.VARCHAR;
    } else if (type.contains("int")) {
      fieldType = Type.INTEGER;
    } else if (type.contains("double") || type.contains("decimal")) {
      fieldType = Type.DECIMAL;
    } else if (type.length() == 0) {
      fieldType = Type.EMPTY;
    }

    return fieldType;
  }

  private String changeCase(String value) {
    if (changeCase == CHANGECASE_LOWER) {
      value = value.toLowerCase();
    } else if (changeCase == CHANGECASE_UPPER) {
      value = value.toUpperCase();
    } else if (changeCase == CHANGECASE_SENTENCE) {
      value = WordUtils.capitalizeFully(value);
    }
    return value;
  }

  /**
   * test value length to see if it will fit
   * 
   * @param value
   * @return
   */
  @Deprecated
  public int testSizeOfValue(String value) {
    String columnSqlType = type.getSqlType().toLowerCase();
    int resize = 0;
    if (columnSqlType.contains("text")) {
      resize = testSize_Text(value);

    } else if (columnSqlType.contains("varchar")) {
      resize = testSize_Varchar(value);
    } 
    return resize;
  }

  /**
   * test text, nothing to do here
   * 
   * @param value
   * @return
   */
  @Deprecated
  public int testSize_Text(String value) {
    // TODO - add the other types of text (length sizes?)
    // TODO - what types of text are there?
    return 0;
  }

  /**
   * test varchar length
   * 
   * @param value
   * @return
   */
  @Deprecated
  public int testSize_Varchar(String value) {
    int resize = 0;
    if (value.length() > lengthChar) {
      resize = value.length();
    }
    return resize;
  }

  /**
   * clone ColumnData object
   */
  @Override
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException e) {
      throw new InternalError(e.toString());
    }
  }

  /**
   * for transfer only 
   * 
   * @param deleteExisting
   */
	public void setDeleteExisting(boolean deleteExisting) {
	  this.deleteExistingValue = deleteExisting;
  }

	public boolean getDeleteExisting() {
	  return deleteExistingValue;
  }

}
