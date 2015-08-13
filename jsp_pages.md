
&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;

# JSP Page Hosting #

# GWT 2.0+ GAE #
> For JSP to compile correctly, settings in the web.xml file have to be done so the servlet container knows how to deal with the page.

"virtualtours" is the project.
```
<!-- project.gwt.xml -->
<servlet path='/VirtualTours.jsp' class='org.apache.jasper.servlet.JspServlet' />

<!-- WEB-INF/web.xml - configuration -->
<servlet>
  <servlet-name>jspServlet1</servlet-name>
  <servlet-class>org.apache.jasper.servlet.JspServlet</servlet-class>
</servlet>
<servlet-mapping>
  <servlet-name>jspServlet1</servlet-name>
  <url-pattern>/virtualtours/VirtualTours.jsp</url-pattern>
</servlet-mapping>

<welcome-file-list>
   <welcome-file>VirtualTours.html</welcome-file>
   <welcome-file>VirtualTours.jsp</welcome-file>
</welcome-file-list>
```

# Tomcat #
  * http://tomcat.apache.org/download-55.cgi - tomcat 5.5  (don't use tomcat 6 jasper jars yet)

> ## Setup ##
> Host JSP pages in your gwt application. You can test them in hosted mode this way, by including the tomcat 5.5 jars below.
  * [click Project](Right.md) > Build Path > Configure Build Path > Libraries > Add External Jar > [Jar](Select.md)
  * Windows users, download and install tomcat 5.5, and find the jasper jars and add them to your class path.
```
<!-- project.gwt.xml -->
<servlet path='/test.jsp' class='org.apache.jasper.servlet.JspServlet' />
<servlet path='/xml/chart.jsp' class='org.apache.jasper.servlet.JspServlet' />

```

> ## Before 1.6 Set Jsp ##

> Add the mime type to the. This will allow jsp files to be used.
  * project > tomcat/conf/gwt/web.xml > add mime type
```
<mime-mapping>
  <extension>jsp</extension>
  <mime-type>text/html</mime-type>
</mime-mapping> 
```