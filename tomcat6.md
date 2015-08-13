
&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;


# Table of Contents #


# Apache Tomcat 6 Servlet Container #

# Install #

##  ##
> When you need more memory for tomcat, set /etc/init.d/tomcat6 java opts
```
if [ -z "$JAVA_OPTS" ]; then
        JAVA_OPTS="-Djava.awt.headless=true -Xmx2048M -XX:MaxPermSize=256M"
fi
```

## Ubuntu Linux ##
  * 0. Install Java Jdk
  * 1. sudo apt-get install tomcat6 tomcat6-common tomcat6-admin tomcat6-user tomcat6-docs tomcat6-examples
  * 2. add tomcat manager user. For more information [Manager App HOW-TO](http://tomcat.apache.org/tomcat-6.0-doc/manager-howto.html)
  * 3. sudo /etc/init.d/tomcat6 restart
  * 4. goto http://localhost:8080

> apt-get
```
 sudo apt-get install tomcat6 tomcat6-common tomcat6-admin tomcat6-user tomcat6-docs tomcat6-examples
```

> add manager role, and user in /etc/tomcat6/tomcat-users.xml
```
 <role rolename="manager"/>
 <user username="tomcat" password="s3cret" roles="manager"/>
```

> tomcat restart
```
 sudo /etc/init.d/tomcat6 restart
```

## Microsoft Windows ##
  * 0. Install Java Jdk
  * 1. goto windows installer - [download](http://tomcat.apache.org/download-60.cgi)
  * 2. follow instructions - record username and port
  * 3. goto http://locahost:8080
  * 4. goto Tomcat Manager Link and login as the user

# Quick Localhost Links #
  * [localhost manager](http://localhost:8080/manager/html)
  * [localhost admin](http://localhost:8080/host-manager/html)
  * [localhost docs](http://localhost:8080/docs/)

# Domain.tld/Archive.war Servlet Naming #
> Naming your archive.war will determine your servlet context. Or you could say archive.war represents the folder/directory compared to html /directory/page.jsp
  * for domian.tld/directory = domain.tld/archive, or archive.war deployed
  * for domain.tld/(root) = domain.tld/ROOT, or ROOT.war deployed

# Troubleshooting #
TODO

&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;
