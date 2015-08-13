
&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;

# Verison 2 - tomcat6 Documentation #
[tomcat6](tomcat6.md)

# Table of Contents #


# Tomcat Deployment #
> Tomcat, has got to be the easiest platform to deploy GWT applications on and the most efficient system. If you switch to Tomcat, you'll love it.

> ## My Screenshots ##
> [Screenshots](http://tribling.com/tomcat.php) - Tomcat install screenshots for reference

> ## Tomcat Reference ##
  * http://tomcat.apache.org/tomcat-6.0-doc/ - Tomcat Documentation
  * http://tomcat.apache.org/tomcat-6.0-doc/config/index.html - Apache Tomcat Configuration Reference
  * /var/lib/tomcat5.5 - (Ubuntu) some of the files reside here
  * /usr/share/tomcat5.5 - (Ubuntu)
  * /etc/tomcat5.5 - (Ubuntu) configuration files, tomcat-users.xml, server.xml
  * /usr/share/tomcat5.5-webapps (Ubuntu) - example web aps directory
  * http://www.gwtapps.com/?p=41 - How much can one server take?

# Tomcat Setup #
> This is my install on Ubuntu Linux which is quite simple. When running a servlet with jdbc connector I got socket access denied. This stumped me for a bit and I found that Tomcat controls socket access.

> ## Install Java ##
```
sudo apt-get install sun-java6-bin sun-java6-jdk sun-java6-jre sun-java6-fonts
```
> > ### Optional - Choose JVM ###
> > > On ubuntu, use galternatives to choose your JVM - use this to change it if need be.
```
#run this
galternatives
```


> ## Install Tomcat5.5 ##
```
sudo apt-get install tomcat5.5 tomcat5.5-admin tomcat5.5-webapps
```

> ## Add Your User Name To Tomcat Config ##
> You will have to find your tomcat users and add yourself, and delete anybody else. Make sure your role has admin, manager, so you can use those activities.
```
//In /etc/tomcat5.5/tomcat-users.xml
<user username="branflake2267" password="password" fullName="" roles="admin,manager,role1,tomcat"/>
```

> ## Restart Tomcat ##
```
sudo /etc/init.d/tomcat5.5 force-reload
```

> ## Test Tomcat Install ##
> > load tomcat via http
> > http://localhost:8180
      * /admin - tomcat administrator
      * /manager - tomcat manager - deploy war by upload



> ## Test Servlet ##
> To test your url-pattern in your servlet to see if you have the correct path context.
  * Goto http://localhost:8180/YourProject/Url-Pattern
  * If you get Page 405 error, you got url-pattern error (url=context). (page 405 error, cant use Get)

> ## Install Servlet Permissions ##
> Be sure you have the correct 'java -version' running on your system. Either a sun-jdk/jre or open-jdk/jre
```
// fileName: /etc/tomcat5.5/policy.d/60gwt.policy
// this will get read on next tomcat restart

// my gwt policy
// be sure! your 'java -version' is a 1.5+ sun-jdk/jre or open-jdk/jre


//codeBase is the location of my gwt projects

//servlet permission for mysql access/connection
grant codeBase "file:${catalina.home}/webapps/-" {
      permission java.net.SocketPermission "192.168.12.81:3306", "connect";
};


//servlet permission for all security, so you can do all with servlet.
grant codeBase "file:${catalina.home}/webapps/-" {
  permission java.security.AllPermission;
};


// restart tomcat
// sudo /etc/init.d/tomcat5.5 restart
```

# JDBC Socket Permission - Security Error #
> Fix this problem: java.security.AccessControlException access denied (java.net.SocketPermission localhost resolve)
> You will need to change Tomcat configuration.
  * Look below in virtual hosting category for more.

```
//On my debian/ubuntu tomcat config
//add to /etc/tomcat5.5/policy.d/04webapps.policy
grant codeBase "file:${catalina.home}/webapps/-" {
      permission java.net.SocketPermission "192.168.12.81:3306", "connect";
};

//reload your tomcat config 
//sudo /etc/init.d/tomcat5.5 force-reload
```

# Security Failure/Error (RPC Failure 500) #
> Are you getting errors in your apache server log? Do they look like mysql authentication problem? If you get a rpc failure on a production server and it works in the debugger, it might be this. I found it by using firefox tamper data, finding the page request died at 500 internal server error. This guy narrowed it down http://rdews.com/?cat=20. It has to do with security of the serialization. This will fix it if you add it to your tomcat configuration.
```
//add to /etc/tomcat5.5/policy.d/04webapps.policy
grant codeBase "file:${catalina.home}/webapps/-" {
  permission java.security.AllPermission;
};

//reload your tomcat config 
//sudo /etc/init.d/tomcat5.5 force-reload
```

  * OR
> This will fix it. Make sure all variables in the object you are going to pass are Public. Like so:
```
package com.tribling.gwt.test.loginmanager.client;
import com.google.gwt.user.client.rpc.IsSerializable;

public class SignInStatus implements IsSerializable{
        //NOT private String SessionID;
        //NOT private String DisplayError;
	public String SessionID;
	public String DisplayError;
	
	public SignInStatus() {
	}
	
	public void setSessionID(String SessionID) {
		this.SessionID = SessionID;
	}
	
	public void setDisplayError(String DisplayError) {
		this.DisplayError = DisplayError;
	}
	
	public String getSessionID() {
		return this.SessionID;
	}
	
	public String getDisplayError() {
		return this.DisplayError;
	}

}
```

# Tomcat Apache and Regular Apache On The Same Server #
> I was wondering how I could run both Apache Tomcat and the regular Apache on the same server. How do you run multiple instances of Apache with the same port? What you do is, use different IP addresses for Apache Tomcat and Apache.

> You need to setup another IP in interfaces.
```

#main web server
# The primary network interface
auto eth0
iface eth0 inet static
address 192.168.12.82
netmask 255.255.255.0
gateway 192.168.12.1
broadcast 192.168.12.255
network 192.168.12.0

#Secondary IP / Alias
#For Apache Tomcat
auto eth0:1
iface eth0:1 inet static
address 192.168.12.83
netmask 255.255.255.0
```


> Apache Tomcat configuration
```
#Both the ip and port have changed from default setup
#In this case the ip is a class C private IP, on private network behind a firewall.
<Connector address="192.168.12.83" port="80" maxHttpHeaderSize="8192"
               maxThreads="150" minSpareThreads="25" maxSpareThreads="75"
               enableLookups="false" redirectPort="8443" acceptCount="100"
               connectionTimeout="20000" disableUploadTimeout="true" />
```

> Apache Configuration
```
<VirtualHost 192.168.12.82:80>
 #~...
</VirutalHost>
```


# War File #
> Check the example war file for correct servlet configuration. [Downloads](http://code.google.com/p/gwt-examples/downloads/list)
  * Or try using my TomcatWarBuilder. [gwtTomcatWarBuilder](gwtTomcatWarBuilder.md)

# Ubuntu Tomcat Server.xml #
> Here is my ubuntu 7.10 tomcat server.xml configuration.
```
<Server port="8005" shutdown="SHUTDOWN">

  <GlobalNamingResources>

	<!-- user database -->
	<Resource name="UserDatabase" 
		auth="Container" 
		type="org.apache.catalina.UserDatabase"
		description="User database that can be updated and saved"
		factory="org.apache.catalina.users.MemoryUserDatabaseFactory"  
		pathname="conf/tomcat-users.xml" />

  </GlobalNamingResources>

  <!-- Define the Tomcat Stand-Alone Service -->
  <Service name="Catalina">

    <!-- Define a non-SSL HTTP/1.1 Connector on port 8180 -->
    <Connector address="74.94.77.107" 
		port="80" 
		maxHttpHeaderSize="8192"
		maxThreads="150" 
		minSpareThreads="25" 
		maxSpareThreads="75"
               	enableLookups="false" 
		redirectPort="8443" 
		acceptCount="100"
               	connectionTimeout="20000" 
		disableUploadTimeout="true" />
 
    <!-- Define a SSL HTTP/1.1 Connector on port 443 -->
    <!--
    <Connector address="74.94.77.107" port="443" maxHttpHeaderSize="8192"
               maxThreads="150" minSpareThreads="25" maxSpareThreads="75"
               enableLookups="false" disableUploadTimeout="true"
               acceptCount="100" scheme="https" secure="true"
               clientAuth="false" sslProtocol="TLS" />
    -->

    <!-- Define the top level container in our container hierarchy -->
    <Engine name="Catalina" defaultHost="localhost" debug="1">

	<Realm className="org.apache.catalina.realm.UserDatabaseRealm" resourceName="UserDatabase" />

  	<!-- Define the default virtual host -->
	<Host name="localhost"
		appBase="webapps" 
		unpackWARs="true" 
	     	autoDeploy="true" 
		xmlValidation="false" 
		xmlNamespaceAware="false"
		debug="1" >

        </Host>

    </Engine>

  </Service>

</Server>
```

# Tomcat Virtual Hosting #
> Now this seemed hard at first, but its the easiest. Use the Tomcat Web Server Administration tool to add virtual hosts. You won't have to edit a bit a xml code to configure this.
  * [Tomcat vhosts setup Images](http://tribling.com/tomcat.php) - These are snapshots of my tomcat setup. I installed a virtual host and installed the manager in each virtual host to quickly deploy and maintain the wars.
  * Note - vhosts directory path "/path/to/vhost/webapps" has correct permissions, and doesn't have trailing slash.

> ## Security Errors ##
> You will have to enable security permissions with tomcat policies. If you have any private fields in your serialized RPC Class it will throw a security error. This is what I do to fix it.

> I made my own policy file to make it easy to track.
```
// /etc/tomcat5.5/policy.d/myapps.policy
// for my virtual host gawkat.com I enable permssions

// gawkat.com vhost
grant codeBase "file:/srv/vhosts/gawkat.com/tomcat/-" {
  permission java.security.AllPermission;
};

grant codeBase "file:/srv/vhosts/gawkat.com/tomcat/-" {
      permission java.net.SocketPermission "192.168.12.81:3306", "connect";
};
```

> ## ROOT application ##
> Name your root application ROOT.war and it will be the root app for the virtual host.

> ## Virtual Domain Listening ##

> This is a snippet from my server.xml file setup. Be sure to have the folder with the domain your listening to in full in the Catalina folder. This is where the server tells it self whats setup. So I have folders "gonevertical.com", "gawkat.com", ... in Catalina folder. I also put the manager.xml file in each domain folder so I can use the manager to upload the ROOT.war or project.war into the system very easily.
```
       <Host name="localhost"  appBase="webapps"
            unpackWARs="true" autoDeploy="true"
            xmlValidation="false" xmlNamespaceAware="false">
        <Valve className="org.apache.catalina.valves.AccessLogValve" directory="logs"  
               prefix="localhost_access_log." suffix=".txt" pattern="common" resolveHosts="false"/>
      </Host>


      <Host name="gonevertical.com"  appBase="/var/lib/tomcat6/webapps_gv"
            unpackWARs="true" autoDeploy="true"
            xmlValidation="false" xmlNamespaceAware="false">
        <Valve className="org.apache.catalina.valves.AccessLogValve" directory="logs"
               prefix="gonevertical_access_log." suffix=".txt" pattern="common" resolveHosts="false"/>
      </Host>

      <Host name="gawkat.com"  appBase="/var/lib/tomcat6/webapps_ga"
            unpackWARs="true" autoDeploy="true"
            xmlValidation="false" xmlNamespaceAware="false">
        <Valve className="org.apache.catalina.valves.AccessLogValve" directory="logs"
               prefix="gawkat_access_log." suffix=".txt" pattern="common" resolveHosts="false"/>
      </Host>

      <Host name="warmbeachwoodworking.net"  appBase="/var/lib/tomcat6/webapps_wb"
            unpackWARs="true" autoDeploy="true"
            xmlValidation="false" xmlNamespaceAware="false">
        <Valve className="org.apache.catalina.valves.AccessLogValve" directory="logs"
               prefix="warmbeachwoodworking_access_log." suffix=".txt" pattern="common" resolveHosts="false"/>
      </Host>

      <Host name="gonevertical.org"  appBase="/var/lib/tomcat6/webapps_gvo"
            unpackWARs="true" autoDeploy="true"
            xmlValidation="false" xmlNamespaceAware="false">
        <Valve className="org.apache.catalina.valves.AccessLogValve" directory="logs"
               prefix="gonevertical_org_access_log." suffix=".txt" pattern="common" resolveHosts="false"/>
      </Host>

```

> This is my 100gwt.policy in policy folder. Now this is bad form, but what can I say, I took some shortcuts. I really should just open the 3306 ports to mysql and the folders that need classpath access. Anyway, this is good for debugging, to make sure security isn't throwing you off.
```
// gonevertical.com vhost
grant codeBase "file:/var/lib/tomcat6/webapps_gv/-" {
  permission java.security.AllPermission;
};

// gawkat.com vhost
grant codeBase "file:/var/lib/tomcat6/webapps_ga/-" {
  permission java.security.AllPermission;
};

// warmbeachwoodworking.net
grant codeBase "file:/var/lib/tomcat6/webapps_wb/-" {
  permission java.security.AllPermission;
};

// gonevertical.org vhost
grant codeBase "file:/var/lib/tomcat6/webapps_gvo/-" {
  permission java.security.AllPermission;
};
```

> ## Virtual Domain Specific Context ##
> I also enable project specific context when needed.

> I add this to the context.xml file. This will allow each project to have a context. This is so much easier to deal with when using the manager to upload the war. And you can save your context in SVN which makes for overal testing much easier all around. I use this type of context most with JDBC pooling.
```
<WatchedResource>META-INF/context.xml</WatchedResource>
```

> I then use project/META-INF/context.xml like this:
```
<Context>

    <!-- #1 pooling jdbc. (now there are several other things that have to be in place for this to work)-->
    <Resource name="jdbc/ark01" auth="Container" type="javax.sql.DataSource"
	       removeAbandoned="true" removeAbandonedTimeout="90" logAbandoned="true"
	       maxActive="5000" maxIdle="30" maxWait="1"
	       username="Web" password="asdf?H" driverClassName="com.mysql.jdbc.Driver"
	       url="jdbc:mysql://ark/survey?autoReconnect=true" 
	       testOnBorrow="true" testOnReturn="true" testWhileIdle="true"  />

</Context>
```
  * http://codintips.blogspot.com/2010/02/make-dbcp-connection-pool-using-tomcat.html - more stuff I wrote on DBCP pooling
  * http://codintips.blogspot.com/2010/02/rough-java-dbcp-connection-pool-context.html
  * http://codintips.blogspot.com/2010/02/mysql-communication-failure-and-aborted.html
  * http://codintips.blogspot.com/2010/02/mysql-open-tables-and-max-linux-open.html
  * http://code.google.com/p/parsecsv2sql/source/browse/trunk/Csv2Sql/src/org/gonevertical/dts/lib/pooling/SetupInitialContext.java - another project I wrote that manages my pooling.

&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="95" width="735" border="0" /&gt;