
&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;

# Table of Contents #


# Demos #
> Sample an application using MySql on Apache Tomcat server.

## MySql Connection Demo ##
  * [Demo](http://gonevertical.org/DemoMySqlConn/) - MySql conn application demo using GWT 2.03+ - hosted on tomcat
  * [Source](http://code.google.com/p/gwt-examples/source/browse/#svn/trunk/DemoMySqlConn/src/org/gonevertical/demo/client) - See the code in the packages

## MySql FeedBack Demo ##
  * [Demo](http://gonevertical.org/DemoFeedback/) - Click on the button in the demo. Its hosted on my tomcat server
  * [Source](http://code.google.com/p/gwt-examples/source/browse/#svn/trunk/DemoFeedback/src/org/gonevertical/demo/client/feedback) - See the code in the packages

# The MySql JDBC Connector #
  * 1. Move the JDBC library to your ./WEB-INF/lib folder
  * 2. Add the library to your build path. Right click >  Add to build path.

## Reference links ##
  * [Connector Downloads](http://www.mysql.com/products/connector/j/) - Get the MySql JDBC connector here
  * [Bible Database](http://sourceforge.net/project/showfiles.php?group_id=186532&package_id=249676&release_id=548680) - Bible table I used for the example

## Connection Pooling ##
> One of the things that slows down mysql is the authorization process which can take up 400ms of latency. Pooling connections keeps them alive, warm and ready for the next request, especially when you have many coming and going. I recommend using connection pooling, it will speed up your application tremendously. Below I list links to some of my code that I use connection pooling.

> ### Project Example (Called Sand at moment) ###
  * [Server Source](http://code.google.com/p/gwt-examples/source/browse/trunk/sand/src/com/gawkat/server/GreetingServiceImpl.java) - Connection Setup
  * [web.xml](http://code.google.com/p/gwt-examples/source/browse/trunk/sand/war/WEB-INF/web.xml) - web.xml Context setup example

> ### My ParseCsv2Sql System Use ###
> > If your wondering what the heck this is. Well I got sick and tired of dealing with jdbc connections to multiple database types at one time. So I wrote this library of code, to deal with it using guice dependency injection I can run my sql connections quite effectively. The below snippet shows the generation of a connection, which simplifies it for me to access a database. If you browse the code of the parsecsv2sql, which grew to more than parsing csv automatically. it can run the mysql system, in and out of tomcat. I can use pooling inside tomcat. Or I can use pooling inside my own application.

  * [context.xml](http://code.google.com/p/parsecsv2sql/source/browse/trunk/Csv2Sql/war/META-INF/context.xml) - Another context setup example in my parseCsv2sql library
  * [My Connection Systems source](http://code.google.com/p/parsecsv2sql/source/browse/trunk/Csv2Sql/src/org/gonevertical/dts/data/DatabaseData.java) - I use this library to control all my JDBC connections when I use mysql and GWT. It can pool int and out of tomcat.
> > Using the ParseCsv2Sql code. [My Connection Systems source](http://code.google.com/p/parsecsv2sql/source/browse/trunk/Csv2Sql/src/org/gonevertical/dts/data/DatabaseData.java)
```
// paired down code example. 
String serverInfo = getServletContext().getServerInfo();
Context context = DatabaseData.initContext();
DatabaseData dd = DatabaseData(DatabaseData.TYPE_MYSQL, "ark_home", "3306", "test", "test#", "test"); // when using context, the parameters here don't matter.
dd.setServletContext(serverInfo, context, "jdbc/contextNameInContextXml"); //set context, authorization parameters are retrieved from context.xml file.
// dd.setReadOnly(true); // for slaves, no write
Connection conn = dd.getConnection(); // to use the connection
...
```


> ### Some of My Pooling Challenges ###
  * Its very important to escape your strings before inserting or updating, b/c it can kill a thread.
  * You can hit OS limits like open sockets and aborted connection failures. [More on that](http://codintips.blogspot.com/2010/02/mysql-communication-failure-and-aborted.html)
  * I recommend threading queries to mysql, page request, like mapping in GAE. I do this in ParseCsv2Sql to speed processing
  * On a windows machine, it may look like a denial of service (DOS) attack and you will have to change the registry. I've wrote about this somewhere too. Beware for aborted connections.

## Tomcat Setup Notes ##
> When you use tomcat, the servlet container has to be given access outside of its container.
  * [gwtTomcat](gwtTomcat.md) - about tomcat setup, install and more.

  * Fix this problem: java.security.AccessControlException access denied (java.net.SocketPermission localhost resolve)
  * More information on this here: [gwtTomcat](gwtTomcat.md)
> You will need to change Tomcat configuration.
```
#for debian/ubuntu
# I added to /etc/tomcat5.5/policy.d/04webapps.policy
# Or make your own /etc/tomcat5.5/policy.d/myGWTPolicys.policy
# you can also change file to: file:/apps/directory/yourGWTTomcatApps/- 
grant codeBase "file:${catalina.home}/webapps/-" {
      permission java.net.SocketPermission "192.168.12.81:3306", "connect";
};
```

## My Redundancy Notes ##
> I recommend at least setting up two mysql servers at least, one master and one slave, one for fail over to in the case of one of the failures. If your really worried about it, I would switch to google app engine. SQL has its limitations with large datasets and huge amount of hits all at once.


# MySql Code Snippets Below #

> ## MySQL Connection ##
> Connect to db. Don't forget to change the parameters.
```
/**
 * db conn
 * 
 * Make sure you add a reference library (external jar in build path) JDBC Connector - 
 * You will see I put it in /opt/gwt-linux/mysql-connector-java-5.0.8-bin.jar
 * 
 * @return Connection
 */
private Connection getConn() {

  	    Connection conn	= null;
	    String url 		= "jdbc:mysql://192.168.12.81:3306/";
	    String db 		= "hostdb";
	    String driver 	= "com.mysql.jdbc.Driver";
	    String user 	= "";
	    String pass 	= "";
		
        try {
                Class.forName(driver).newInstance();
        } catch (InstantiationException e) {
                e.printStackTrace();
        } catch (IllegalAccessException e) {
                e.printStackTrace();
        } catch (ClassNotFoundException e) {
                e.printStackTrace();
        }
        try {
        		
        		conn = DriverManager.getConnection(url+db, user, pass);
        } catch (SQLException e) {
        		System.err.println("Mysql Connection Error: ");
                e.printStackTrace();
        }
		return conn;
}
```

> ## Get Row Count ##
> Use this to size an array for your values
```
/*
 * get row count
 */
protected static int getResultSetSize(ResultSet resultSet) {
    int size = -1;

    try {
        resultSet.last();
        size = resultSet.getRow();
        resultSet.beforeFirst();
    } catch(SQLException e) {
        return size;
    }

    return size;
}
```

> ## Query ##
> Query statement, using connection, statement, resultset, and get record. Setup a connection first.
```
String query = "SELECT * FROM table";
 
try {
    Connection conn = this.getConn();
    Statement select = conn.createStatement();
    ResultSet result = select.executeQuery(query);
    while (result.next()) {
    	String s = result.getString(1);
    }
    select.close();
    result.close();
    conn.close();
} catch(SQLException e) {
	System.err.println("Mysql Statement Error: " + query);
	e.printStackTrace();
}
```

> ## Update / Insert ##
> Update and Insert into database, MySql or MsSql or whatever database you have. Setup a connection first
```
//save session data to table 
String query = "INSERT INTO `session` (UserID, SessionID, LastAccessed, DateCreated) " +
			   "VALUES ('" + this.dbUserID + "', '" + this.SessionID + "' , UNIX_TIMESTAMP(), UNIX_TIMESTAMP());";

try {
    Connection conn = this.getConn();
    Statement update = conn.createStatement();
    update.executeUpdate(query);
    
    //get last id 
    ResultSet result = update.getGeneratedKeys(); 
    if (result != null && result.next()) { 
        int rsId = result.getInt(1);  
    }

    result.close();
    update.close();
    conn.close();
} catch(SQLException e) {
    System.err.println("Mysql Statement Error: " + query);
    e.printStackTrace();
}
```


> ## Escape String For SQL ##
> Apache has some cool functions for escaping strings. This can only be run on server side, preparing the sql data for insert. You don't have to unescape b/c it comes out as is. It just allows you to save with "'" '"' in the string.
  * Right Click Project > Build Path > Configure Build Path > Libraries > Add External Jar
  * Link to Apache jar documentation: [Classes Documentation](http://commons.apache.org/lang/api-release/index.html?org/apache/commons/lang/StringEscapeUtils.html)
  * Download Jar: [Goto Download](http://commons.apache.org/lang/)
```
import org.apache.commons.lang.StringEscapeUtils;

/**
 * escape string to db
 * @param s
 * @return
 */
protected static String escapeForSql(String s) {

	String rtn = StringEscapeUtils.escapeSql(s);
	
	return rtn;
}
```

> Improved Escape method for fixing data before its stuck into db
```
/**
 * escape string to db
 * 
 * remove harmfull db content
 * remove harmfull tags
 *
 * @param s
 * @return
 */
protected static String escapeForSql(String s) {
	
	//remove harmful HTML tags
	if (s != null) {
		s = s.replaceAll("(?i)</?(HTML|SCRIPT|HEAD|CSS)\\b[^>]*>", "");	
	}

	String rtn = StringEscapeUtils.escapeSql(s);
	
	//escape utils returns null if null
	if (rtn == null) {
		rtn = "";
	}

	return rtn;
}
```

# Trasporting Data / Recordsets Around #
> This is an example of how I transport data from server to client and back. This is my favorite way and by far the most efficient and easiest way to do it.

> I use a class like this to store MySQL record set data into an object array. This makes it very easy to pass around data. Now if you use private methods and/or fields in the class that goes from server to client, you will have have to change Tomcat security. See more in [gwtTomcat](gwtTomcat.md).
```
/**
 * I use this class to store my mysql recordset in an object that is an array.
 * This will give an example of how I pass data from the server to client in an object, 
 * one of my favorites for its simplicity.
 * 
 * @author branflake2267
 *
 */
public class BibleData implements IsSerializable {

	// fields to store data
	public String book;
	public int howManyChapters;
	public int howManyVerses;
	
	/**
	 * constructor
	 */
	public BibleData() {
		// nothing to do when transporting
	}
	
}
```

> This is an example of how I spool the data into the Object Array from MySQL.
```
public BibleData[] getBibleInfo() {
	
	String query = "SELECT bid, en FROM book;";
	
	// prepare for rpc transport
	BibleData[] bibleData = null;
	
    try {
    	Connection connection = getConn();
        Statement select = connection.createStatement();
        ResultSet result = select.executeQuery(query);
       
        // init object into the size we need, like a recordset
        int rsSize = getResultSetSize(result); //size the array
        bibleData = new BibleData[rsSize];
        
        int i = 0;
        while (result.next()) {
        	
        	// init each object in the array !!!!
        	bibleData[i] = new BibleData(); // <-THIS IS CRITICAL TO REMEMBER!!!! init each array with the object type (I forget to do this so often)
        	
        	int bid = result.getInt(1);
        	bibleData[i].book = result.getString(2);
        	bibleData[i].howManyChapters = getHowManyChapters(bid);
        	bibleData[i].howManyVerses = getHowManyVerses(bid);
        	
            i++;
        }
        
        // clean up
        result.close();
        connection.close();
        
    } catch(Exception e) {
    	
    	System.err.println("Mysql Statement Error: " + query);
    	e.printStackTrace();
    	
    }
	
    // return the array
    return bibleData;
}
```

# Feed Back Widget #
The feedback widget in the repository gathers up the inputs and sends them to the server and inserts them into MySql.

  * [Feedback Application Demo](http://gonevertical.org/DemoFeedback/)
  * [Source](http://code.google.com/p/gwt-examples/source/browse/#svn/trunk/DemoFeedback/src/org/gonevertical/demo/client/feedback)

> My Feedback Table
```
CREATE TABLE `gwt_feedback` (
  `ID` int(11) NOT NULL AUTO_INCREMENT,
  `UserID` int(11) DEFAULT '0',
  `FromEmail` varchar(150) DEFAULT NULL,
  `FromName` varchar(150) DEFAULT NULL,
  `Subject` varchar(250) DEFAULT NULL,
  `Message` text,
  `Suggestion` tinyint(1) DEFAULT '0',
  `Comment` tinyint(1) DEFAULT '0',
  `Problem` tinyint(1) DEFAULT '0',
  `Other` tinyint(1) DEFAULT '0',
  `Post` tinyint(1) DEFAULT NULL,
  `DateCreated` datetime DEFAULT NULL,
  `LastUpdated` datetime DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=MyISAM 
```

&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;
