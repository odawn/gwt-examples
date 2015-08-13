
&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;

# Table of Contents #


# Demo and Source #
  * [Demo](http://demogwtdatetimegae.appspot.com/) - Demo DateTime Conversion
  * [Source Main](http://code.google.com/p/gwt-examples/source/browse/#svn/trunk/DemoGwtDateTime_Source)
  * [Source GAE](http://code.google.com/p/gwt-examples/source/browse/#svn/trunk/DemoGwtDateTime_GAE)
  * [Source Gadget](http://code.google.com/p/gwt-examples/source/browse/#svn/trunk/DemoGwtDateTime_Gadget)

# Client vs Server #
> Its ok to use the Date Class, its going no were in the Java versions GWT supports.
  * Calendar class is not yet available on the client side (yet). It's emulation planned for development.
  * Its ok to use Date on client side, its going no were in Java 1.5 which GWT now supports.
  * Below in my examples if you see Calendar, its on the server side.

# How to use Date/Time with/in GWT #
  * get unix time stamp
  * get year, month, day, date, today, current date, timezone offset from gmt, hours, minutes, seconds, milliseconds
  * convert from unix time stamp to java time stamp
  * get the start of day unix time stamp (0:00:00) (12:00:00am)
  * get the end of day unix time stamp (23:59:59) (11:59:59pm)
  * GMT Convert
  * Timezone

> ## Documentation Date/Time Links ##
    * [Java Date Object](http://java.sun.com/j2se/1.4.2/docs/api/java/util/Date.html) - Reference
    * [GWT Date Time Format](http://google-web-toolkit.googlecode.com/svn/javadoc/1.4/com/google/gwt/i18n/client/DateTimeFormat.html) - Reference
    * [Class DateTimeFormat](http://www.gwtapps.com/doc/html/com.google.gwt.i18n.client.DateTimeFormat.html) - Documentation
    * [GWT Date Time Format Incubation](http://code.google.com/p/bunsenandbeaker/wiki/DevGuideDateAndNumberFormat) - Documentation


# Unix Time Stamp #
> Seconds since the epoch. Get unix time stamp from java.util.Date
```
private int getUnixTimeStamp() {
	Date date = new Date();
	int iTimeStamp = (int) (date.getTime() * .001);
	return iTimeStamp;
}
```

> Get milliseconds from epoch.
```
private long getTimeStampMilli() {
	Date date = new Date();
	return date.getTime();
}
```


# Get Client's Time Zone #
> This is how I get the client's time zone.

> You can only do this on the server side. Although this is nice todo, you can not do this on the client side..
```
//will not work on the client side
TimeZone tz = TimeZone.getDefault();
String timezone = tz.getID(); //like "America/Vancouver"
```

```
/**
 * get client's time zone
 * @param type
 * @return
 */
private String getTimeZone(int type) {
	Date today = new Date();
	String timezone = null;
	switch (type) {
		case 1: // date timeZoneMinOffset in minutes
			timezone = Integer.toString(today.getTimezoneOffset()); //like "420"
			break;
		case 2: // time zone RFC822
			timezone = DateTimeFormat.getFormat("Z").format(today); //like "-0700"
			break;
		case 3: //time zone TextShort
			timezone = DateTimeFormat.getFormat("v").format(today); //like "GMT-07:00"
			break;
		case 4: //time zone Name 

                        //THIS WONT COMPILE - WORKS IN DEBUGGER - this will not work on client side
			//TimeZone tz = TimeZone.getDefault();
			//timezone = tz.getID(); //like "America/Vancouver"
                        timezone = DateTimeFormat.getFormat("z").format(today); //like "GMT-07:00";

			break;
		case 5: //timezone TextLong
			timezone = DateTimeFormat.getLongDateTimeFormat().format(today); //like "March 22, 2008 5:39:22 PM GMT-07:00"
			break;
	}
	return timezone;
}
```

# Format Minutes to GMT #
> Format minutes to gmt GMT[+|-]hh:mm. The "Date" object timezone minutes offset is positive when it should be negative therefore, would have to change logic below.

```
/**
 * format date TimeZone (minutes) to "GMT[+|-]hh:mm"
 * create GMT ID for calendar object to use
 * 
 * @param gmtOffSetMinutes - gmt offset in minutes "[+|-]int"
 * @return gmt string
 */
public static String formatGmtID(int gmtOffSetMinutes) {
	int min = gmtOffSetMinutes;
	
	//test
	//int min = 450; //minutes (Positive or negative Int) (Date Object TimeZone OffSet in Minutes)

	int hh = (int) min / 60; //get hour
	int mm = (int) min % 60; //get minutes (minutes remainder)
	
	int phh = Math.abs(hh); //convert to positive int
	int pmm = Math.abs(mm); //convert to positive int
	
	String shh = getDoubleDigit(phh); //convert it to double digit string '1' = '01', '2' = '02'
	String smm = getDoubleDigit(pmm);
	
	//is int positive or negative?
	String sIsN = Integer.toString(hh);
	boolean isNegative = sIsN.matches("[-].*"); //look for negative symbol in int
	
	//add pos/neg symbol to gmt string
	String addNeg = "";
	if (isNegative == true) {
		addNeg = "-";
	} else {
		addNeg = "+";
	}
	
	//make GMT ID - for calendar object
	String gmt = "GMT" + addNeg + shh + ":" + smm;
	
	//debug
	System.out.println("gmt Test: " + gmt);

	return gmt;
}

//You will also need this method for the above to work
/**
 * get a double digit int from a single
 * @param i
 * @return
 */
protected static String getDoubleDigit(int i) {
	String newI = null;
	switch (i) {
	case 0:
		newI = "00";
		break;
	case 1:
		newI = "01";
		break;
	case 2:
		newI = "02";
		break;
	case 3:
		newI = "03";
		break;
	case 4:
		newI = "04";
		break;
	case 5:
		newI = "05";
		break;
	case 6:
		newI = "06";
		break;
	case 7:
		newI = "07";
		break;
	case 8:
		newI = "08";
		break;
	case 9:
		newI = "09";
		break;
	default:
		newI = Integer.toString(i);
	}
	return newI;
}

```

# Server - Linux Timezone #
> The timezone ID matches a file in /usr/share/zoneinfo.

> For Instance if you get use PDT (not a correct timezone name(ID)) for Timezone ID, it will cause an error to be thrown.
```
//This will cause a file I/O Error b/c PDT doesn't exist as a timezone file
DateFormat df1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

//will error
df1.setTimeZone(TimeZone.getTimeZone("PDT"));

//correct
df1.setTimeZone(TimeZone.getTimeZone("America/Vancouver"));
```

# Server - Example Date TimeZone Parser #
> I would use getID instead of this. But I wanted to add this incase it was useful to anybody.
```
/**
 * parse timezone from date.toString();
 * 
 * @param sDate
 * @return
 */
public static String parseTimeZone(String sDate) {
	//test string
    //CharSequence inputStr = "Fri Mar 21 16:03:21 PDT 2008";
	CharSequence inputStr = sDate;
   
	//get timezone out of string from date
    String patternStr = ":[0-9]{2}\040(.*?)\040[0-9]{4}$";
    
    // Compile and use regular expression
    Pattern pattern = Pattern.compile(patternStr);
    Matcher matcher = pattern.matcher(inputStr);
    boolean matchFound = matcher.find();
    
    String timezone = null;
    if (matchFound == true) {
        // Get all groups for this match
        for (int i=0; i<=matcher.groupCount(); i++) {
        	timezone = matcher.group(i);
        }
    }
    
    timezone.trim();
    
	return timezone;
}
```

# Server GMT Time #
> Get the servers GMT Time
```
//get the servers GMT Time
Date date = new Date();
TimeZone tzGMT = TimeZone.getTimeZone("GMT");
Calendar cal = Calendar.getInstance();
cal.setTime(date);
cal.setTimeZone(tzGMT);

//GMT hour and minute
int hour = cal.get(Calendar.HOUR_OF_DAY);
int min = cal.get(Calendar.MINUTE);

System.out.println("now: h:" + hour + " m:" + min);
```

# Subtract Months #
> Subtract 12 months from current date and get that 12 months prior timestamp.
```
private long getMinus12Months(int month, int year) {
	
	for (int i=0; i < 12; i++) {
		
		if (month == 1) { // jan
			year--;
			month = 12;
		} else {
			month--;
		}
	}
	
	Date date = new Date();
	date.setMonth(month-1);
	date.setYear(year-1900);
	return date.getTime();
}

```

# Server Side - TimeStamp - Start and End Of Day #
> Get the beginning/start of the day in seconds - using Calendar (calendar works on server side only)
  * MySql: SELECT UNIX\_TIMESTAMP(MAKEDATE( EXTRACT(YEAR FROM CURDATE()), DAYOFYEAR(CURDATE())));
```
/**
 * get Start of Day Time Stamp in seconds
 * 
 * @param int TimeStamp - unix time stamp
 * @return unix time stamp (seconds)
 */
protected static String getDateStart_TimeStamp(int TimeStamp) {

	long ltime = (long) (TimeStamp / .001); //my unix time stamp in seconds

	Date date = new Date(ltime);
	TimeZone tzGMT = TimeZone.getTimeZone("GMT");
	Calendar cal = Calendar.getInstance();
	cal.setTimeZone(tzGMT);
	cal.setTime(date);
	cal.set(Calendar.HOUR_OF_DAY, 0);
	cal.set(Calendar.MINUTE, 0);
	cal.set(Calendar.SECOND, 0);
	
	
	//proof
	//int h = cal.get(Calendar.HOUR_OF_DAY);
	//int m = cal.get(Calendar.MINUTE);
	//int d = cal.get(Calendar.DATE);
	//System.out.println("time: day:" + d +" " + h + ":" + m);
	
	//change to seconds
	long ltimeM = cal.getTimeInMillis();
	int iTimeStamp = (int) (ltimeM * .001);

	return Integer.toString(iTimeStamp);
}
```

> Get the end of the day in seconds
```
/**
 * Get End of Day Time Stamp in seconds
 * 
 * @param int TimeStamp - unix time stamp
 * @return unix time stamp (seconds)
 */
protected static String getDateEnd_TimeStamp(int TimeStamp) {

	long ltime = (long) (TimeStamp / .001); //my unix time stamp in seconds

	Date date = new Date(ltime);
	TimeZone tzGMT = TimeZone.getTimeZone("GMT");
	Calendar cal = Calendar.getInstance();
	cal.setTimeZone(tzGMT);
	cal.setTime(date);
	cal.set(Calendar.HOUR_OF_DAY, 23);
	cal.set(Calendar.MINUTE, 59);
	cal.set(Calendar.SECOND, 59);
	
	//proof
	//int h = cal.get(Calendar.HOUR_OF_DAY);
	//int m = cal.get(Calendar.MINUTE);
	//int d = cal.get(Calendar.DATE);
	//System.out.println("time: day:" + d +" " + h + ":" + m);
	
	//change to seconds
	long ltimeM = cal.getTimeInMillis();
	int iTimeStamp = (int) (ltimeM * .001);

	return Integer.toString(iTimeStamp);
}

```

> Get the start of the day 0:00:00 am in seconds
```
/**
 * get Start of Day Time Stamp
 * @return unix time stamp (seconds)
 */
private String getDateStart_TimeStamp() {
	
	long ltime = (long) (this.TimeStamp / .001); //my unix time stamp in seconds
	
	//Figure out Unix time stamps for the start of the day and the end of the day
	Date DC = new Date(ltime); //get current Year, Month
	int year = DC.getYear(); //current Year
	int month = DC.getMonth(); //current Month
	int day = DC.getDate();// current Day
	
	Date dateStart = new Date(); //Today @ 0:00:00 am to unix time stamp 
	dateStart.setYear(year); //set year current Year
	dateStart.setMonth(month); //set month current Month
	dateStart.setDate(day); //set current day of month
	dateStart.setMinutes(0); // minute 0
	dateStart.setHours(0);	 // hour 0
	dateStart.setSeconds(0); // second 0
	int iTimeStamp = (int) (dateStart.getTime() * .001);
	String sStart_TimeStamp = Integer.toString(iTimeStamp);
	
	//helps debugging
	System.out.println("this.Time::: " + this.TimeStamp);
	System.out.println("Start Time::: " + sStart_TimeStamp);
	
	return sStart_TimeStamp;
}
```

> Get the end of the day 23:59:59 in seconds
```
/**
 * Get End of Day Time Stamp
 * @return unix time stamp (seconds)
 */
private String getDateEnd_TimeStamp() {
	
	long ltime = (long) (this.TimeStamp / .001); //my unix time stamp in seconds
	
	//Figure out Unix time stamps for the start of the day and the end of the day
	Date DC = new Date(ltime); //get current Year, Month
	int year = DC.getYear(); //current Year
	int month = DC.getMonth(); //current Month
	int day = DC.getDate();// current Day
	
	Date dateEnd = new Date(); //Today @ 23:59:59 to unix time stamp 
	dateEnd.setYear(year); //set year current Year
	dateEnd.setMonth(month); //set month current Month
	dateEnd.setDate(day); //set current day of month
	dateEnd.setHours(23);	// hour 23
	dateEnd.setMinutes(59); // minute 59
	dateEnd.setSeconds(59); // second 59
	int iTimeStamp = (int) (dateEnd.getTime() * .001);
	String sEnd_TimeStamp = Integer.toString(iTimeStamp);
	
	return sEnd_TimeStamp;
}
```

&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;
