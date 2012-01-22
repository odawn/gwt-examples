package org.gonevertical.dts.lib.address;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.gonevertical.dts.lib.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddressParser {
	
  private static final Logger log = LoggerFactory.getLogger(AddressParser.class);

  // states
  private String statesAb = "AA,AE,AF,AK,AL,AP,AR,AS,AZ,CA,CO,CT,DC,DE,FL,FM,GA,HI,IA,ID,IL,IN,KS,KY,LA,MA,MD,ME,MH,MI,MN,MO,MP,MS,MT,NC,ND,NE,NH,NJ,NM,NV,NY,OH,OK,OR,PA,PR,PW,RI,SC,SD,TN,TX,UT,VA,VI,VT,WA,WI,WV,WY";
  private String statesName = "Alabama,Alaska,American Samoa,Arizona,Arkansas,Armed Forces - Africa,Armed Forces - Americas,Armed Forces - Europe,Armed Forces - Pacific,California,Colorado,Connecticut,Delaware,District of Columbia,Federated States of Micronesia,Florida,Georgia,Hawaii,Idaho,Illinois,Indiana,Iowa,Kansas,Kentucky,Louisiana,Maine,Marshall Islands,Maryland,Massachusetts,Michigan,Minnesota,Mississippi,Missouri,Montana,Nebraska,Nevada,New Hampshire,New Jersey,New Mexico,New York,North Carolina,North Dakota,Northern Mariana Islands,Ohio,Oklahoma,Oregon,Palau,Pennsylvania,Puerto Rico,Rhode Island,South Carolina,South Dakota,Tennessee,Texas,Utah,Vermont,Virgin Islands,Virginia,Washington,West Virginia,Wisconsin,Wyoming";

  
  private String street = null;
  
  private String city = null;
  
  private String state = null;
  
  private String zip = null;

  private String fulladdress = null;
  
  /**
   * constructor
   */
  public AddressParser() {
  }
  
  public boolean parseAddress(String fulladdress) {
    this.fulladdress  = fulladdress;
    
    if (fulladdress == null) {
      System.out.println("No Address given");
      return false;
    }
    
    boolean found = false;
    if (checkFormat_Standard1() == true) {
      found = true;
    }
    
    // TODO - add more formats
    // cross check for city and state to database
    
    return found;
  }
  
  public String getState(String fulladdress) {
    
    String[] m = fulladdress.split("\040|,");

    int c = m.length;
    if (c > 0) {
      for(int i=0; i < c; i++) {
        if (isState(m[i]) == true) {
          // pick the last matched word as state (NE northeast can be matched as a state)
        }
      }
    } else {
      return null;
    }
    
    return state;
  }
  
  public String getZip(String fulladdress) {
    String[] m = fulladdress.split("\040|,");

    int c = m.length;
    if (c > 0) {
      for(int i=0; i < c; i++) {
        if (isZip(m[i]) == true) {
          // pick the last matched zip code, street number will match too
        }
      }
    } else {
      return null;
    }
    
    return zip; 
  }
  
  public boolean isZip(String s) {
    
    s = s.replace("/", "");
    s = s.replace(",", "");
    s = s.toLowerCase().trim();
    
    boolean found = false;
    if (s.matches("[0-9]+-[0-9]+|[0-9]+") == true) {
      found = true;
      zip = s;
    }
    
    return found;
  }
  
  private boolean checkFormat_Standard1() {
    
    // 4003 246th st ne, arlington, wa, 98223
    String re = "^(.*?),(.*?),(.*?),(.*?)$";
    Pattern p = Pattern.compile(re);
    Matcher m = p.matcher(fulladdress);
    boolean found = m.find();

    if (found == true) {
      street = m.group(1);
      city = m.group(2);
      state = m.group(3);
      zip = m.group(4);
      
      if (street == null | city == null | state == null | zip == null) {
        return false;
      }
      
    } else {
      return false;
    }
    
    return true;
  }
  
  private boolean isState(String s) {

    if (s == null) {
      return false;
    }
    
    s = s.replace("-", "");
    s = s.replace("/", "");
    s = s.replace(",", "");
    s = s.toLowerCase().trim();
    
    String[] st1 = statesAb.toLowerCase().split(",");
    String[] st2 = statesName.toLowerCase().split(",");

    boolean found = false;
    if (Arrays.binarySearch(st1, s) > -1) {
      found = true;
    } else if (Arrays.binarySearch(st2, s) > -1) {
      found = true;
    }
    
    if (found == true) {
      state = s;
    }
    
    return found;
  }
}
