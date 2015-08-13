
&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;

# Table of Contents #


# Eclipse #
  * [Eclipse\_Advantages](Eclipse_Advantages.md) - Why Eclipse kicks butt as an IDE for java.

# Reference #
  * [Eclipse IDE Home](http://eclipse.org)

# Install #

## Ubuntu Linux ##
  * 0. Install Java Jdk. I like using OpenJDK for Eclipse. I run GWT in ia32-sun-jre.
  * 1. [Download Eclipse](http://www.eclipse.org/downloads/)
  * 2. Extract eclipse into a directory like /opt/eclipse
  * 3. Create eclipse launcher shortcut for desktop.

> installing java
```
 sudo apt-get install openjdk-6-jre

 # for 64bit systems, you will also need to run GWT in 32 jre vm 
 sudo apt-get install ia32-sun-java6-bin
```

## Microsoft Windows ##
  * 0. [Download Java](http://java.sun.com/javaee/downloads/index.jsp?intcmp=1282) Install Java SDK
  * 1. [Download Eclipse](http://www.eclipse.org/downloads/)
  * 2. Extract into directory like C:\archive\eclipse
  * 3. Create shortcut for desktop of eclipse.exe

## Recommendations ##
  * I recommend starting with **Eclipse for Java Developers EE** download. You can install more features later that you may desire with software updates.

# SVN - Subversion #
  * Install Subclipse
  * [Subclipse](http://subclipse.tigris.org/servlets/ProjectProcess?pageID=p4wYuA)
  * [1.6.x subclipse update site](http://subclipse.tigris.org/update_1.6.x)
  * Using subclipse takes less steps to install than polarions team provider connector

## SVN From Polarion ##
> Every upgrade of Eclipse or Polarion's Team Provider plugin has corrupted my eclipse install to the point I have to reinstall eclipse. I have had to do it so many times, I finally just gave Subclipse a try, and it works great. In fact its faster loading the svn repository than polarion's plugin.

## Create SVN Repository ##

### Linux ###
  * reference Ubuntu: [Ubuntu SVN Info](https://help.ubuntu.com/community/Subversion)
```
sudo mkdir /home/svn
cd /home/svn
sudo mkdir myproject
sudo chown -R www-data myproject
sudo chgrp -R subversion myproject
sudo chmod -R g+rws myproject
sudo svnadmin create /home/svn/myproject
```

### Microsoft Windows ###
> TODO

# Javascript Editor #
> Get a better javascript editor for eclipse.
  * [Download](http://www.interaktonline.com/Products/Eclipse/JSEclipse/Installation-Update/)
  * Help > Software Updates > Add Site > [Update Site](http://download.macromedia.com/pub/labs/jseclipse/autoinstall/site.xml) > (select new feature, JSEclipse) > Install
  * Use: Right click on file and open with JSEcplise (editor)

# Code Metrics #
> How many lines of code have you written for your application? How is your code?
  * [metrics site](http://metrics.sourceforge.net/)
  * [Metrics Update Site](http://metrics.sourceforge.net/update)

# Proxy #
> For intranet proxies in corporations.
  * 1. Window > Prefrences > (type filter Proxy) > General > Network Connections > Setup your intranet proxy

# Google Plugins #
  * [Eclipse Install](http://code.google.com/appengine/docs/java/tools/eclipse.html) - Google Eclipse plugins
  * [Eclipse update site](http://dl.google.com/eclipse/plugin/3.4) - auto install via update site

# GWT Project Debugging #

## On a 64bit System ##
> On a 64bit operating system, you will need to configure an alternate JRE to test your gwt application with.
  * 1. (Right Click on Project) > Debug As > Debug Configurations > Java Application > ProjectName > JRE > Installed JREs > Add > (ubuntu linux: /usr/lib/jvm/ia32...)

## Larger Memory Space ##
> GWT project creation now sets this.
  * 1. (Right Click on Project) > Debug As > Debug Configurations > Java Application > ProjectName > Arguments > VM arguments > -Xmx256M

# External Jar Libraries #
> Adding external libraries onto your gwt project can increase the funcationality for both the client and the server side. Keep in mind that adding jars and using there classes don't always work for the client side.
  * 1. (Right Click on Project) > Build Path > Libraries > Add External Jars


# Keyboard Shortcuts #
> TODO

# Trouble Shooting #

## Project Build Error ##
> If you compiled your project and you get an rpc error, you may want to clean your project. This will clean and build your compiled classes. > bin folder
  * Goto Main Menu > Project > Clean

## Java Heap Size / JVM Memory ##
> If you get a java heap size error you will need to incease it or rewrite your application to use less.
  * Remember to close JDBC statements
  * Remember to close JDBC resultSets
  * On Large JDBC recordsets, read row by row.
```
// read row by row, and don't load the entire recordset
Statement select = connection.createStatement(java.sql.ResultSet.TYPE_FORWARD_ONLY, java.sql.ResultSet.CONCUR_READ_ONLY);
// fetch size - how many rows to read at a time. 
select.setFetchSize(100);  
ResultSet result = select.executeQuery(query);
```

## JavaHL Dependency Error ##
> If you get a JavaHL dependency error. I sovled it by uninstalling JavaHL stuff.
  * Help > Software Udpates > Installed Software > (control-F "JavaHL") > (select JavaHL(s)) > Uninstall
```
Unsatisfied dependency: [org.polarion.eclipse.team.svn.connector.javahl.win32.feature.group 2.0.7.I20081222-1900] requiredCapability: org.eclipse.equinox.p2.iu/org.polarion.eclipse.team.svn.connector.javahl.win32/[2.0.7.I20081222-1900,2.0.7.I20081222-1900]
Unsatisfied dependency: [org.polarion.eclipse.team.svn.connector.javahl15.win32.feature.group 2.0.7.I20081222-1900] requiredCapability: org.eclipse.equinox.p2.iu/org.polarion.eclipse.team.svn.connector.javahl15.win32/[2.0.7.I20081222-1900,2.0.7.I20081222-1900]
```

## TODOs ##
> - check v1 docs in this wiki for these things
  * TODO Out of memory error
  * TODO files open limit
  * TODO 64bit/32bit erros


&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;
