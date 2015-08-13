
&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;

# Verion 2 Documentation For Eclipse #
[eclipse](eclipse.md) - version 2 documentation

# Table of Contents #


# Eclipse FAQs #

> ## Promoting ##
> > ![http://tribling.com/brainstorm1265.png](http://tribling.com/brainstorm1265.png) [Vote For Eclipse in Ubuntu](http://brainstorm.ubuntu.com/idea/1265/)


> ## Reference ##
    * http://www.eclipse.org/ - Eclipse home
    * http://www.eclipse.org/subversive/downloads.php - Eclipse subversion installation

> ## Eclipse Docs ##
    * [Eclipse Official Site Help](http://help.eclipse.org/help33/index.jsp?topic=/org.eclipse.platform.doc.user/tasks/running_eclipse.htm)
    * [Coding in the Shade: Using Eclipse with Google Data APIs](http://code.google.com/support/bin/answer.py?answer=93348&topic=11369)

> ## Extending Eclipse ##
    * [Link To MySQL](http://dev.mysql.com/downloads/connector/j/5.1.html) - JDBC Connector
    * http://metrics.sourceforge.net - Code Metrics - How many lines of code do you have?


# Importing My Projects From SVN #
  1. You can import these projects into your Eclipse.org IDE. SVN URL: http://gwt-examples.googlecode.com/svn/trunk/
  1. Once imported, change your project's build path to the correct libraries. Change the build path library for gwt-linux home directory. I set mine to **/opt/gwt-linux** which is linked to the current Google Web toolkit download that I have.
  1. Location: Eclipse > Right Click Project > Build Path > Libraries Tab >
  1. Open the debug dialog in eclipse and look for java application. Select the gwt application and hit debug.

# Tips #
  * [Eclipse\_Advantages](Eclipse_Advantages.md)- Why Eclipse kicks butt as an IDE.

## Get Your Configuration Details ##
> goto: Help > About Eclipse Platform > Configuration Details (button)

## Make Eclipse Faster ##
> Goto your task manager Linux or Windows and change the processing priority. Beware it could lock up other resources.

## JDBC Connector ##
> If you want to use this project's mysql jdbc connector make sure you have it in **/opt/gwt-linux**. /opt/gwt-linux/mysql-connector-java-5.0.8-bin.jar
> Or change the build path by right clicking on the eclipse project, select "Build Path", then change or add the mysql jdb connectory library.
    1. Get JConnector MySQL JDBC [Link To MySQL](http://dev.mysql.com/downloads/connector/j/5.1.html)
    1. Eclipse > Right Click Project > Build Path > Libraries Tab > Add External Jar > Add /opt/gwt-linux/mysql-connector-java-5.0.8-bin.jar

## GWT\_Home Directory ##
> I put GWT Home Directory system link as **/opt/gwt-linux** which links to the current version in **/opt/google/gwt/gwt-linux.version**. Or right click on the eclipse project and select _"Build Path"_, and change the library to the correct location for your GWT Home directory.

## Error: To Many Files Open ##
> You have to run eclipse from shell to get this error output.

> Test how many files can be open during your session
```
ulimit -n
```

> Enviroment Vars
```
#In /etc/security/limits.conf (on my ubuntu linux box)
#sudo pico /etc/security/limits.conf - edit it from shell

#I adjusted this so eclipse will work with more java files
*       soft    nofile          10000
*       hard    nofile          10000

#user specific
#username hard nofile 10000
```

## Setting Enviromental Variable GWT\_HOME ##
> If you want to use a enviromental variable
    1. Edit /etc/environment (sudo gedit /etc/environment)
    1. Add this
```
GWT_HOME="/opt/gwt-linux" 
```
    1. Or Add it to ~/.profile
```
 export GWT_HOME="/opt/gwt-linux" 
```
    1. Test it in bash/shell
```
echo $GWT_HOME
```

## Eclispe Build Path To gwt-user.jar ##
> This is my gwt-linux home directory location, and I use this build path for my projects.
    * Eclipse > Right Click Project > Build Path > Libraries Tab > Add External Jar > Add /opt/gwt-linux/gwt-user.jar

## GWT\_EXTERNAL\_BROWSER ##
> How to add GWT External Browser linux environment variable for Eclipse.

  1. Edit /etc/environment (sudo gedit /etc/environment)
  1. Add this
```
# restart user session for this to take effect
# GWT_EXTERNAL_BROWSER="/usr/bin/firefox"
GWT_EXTERNAL_BROWSER="/opt/google/chrome/google-chrome"
```
  1. or add it to ~/.profile (user home directory)
```
export GWT_EXTERNAL_BROWSER="/usr/bin/firefox"
```
  1. Test it in bash/shell
```
echo $GWT_EXTERNAL_BROWSER
```



## Running 32bit JVM for GWT ##
> Use a 64bit JVM with Eclipse IDE and run your application in a separate 32bit JVM. You will need change your projects debug configuration to a 32bit JVM.

  * You can use Eclipse with a 64-bit System JVM
  * You can use separate JVM for each eclipse Project.

> ### Other References ###
> > http://code.google.com/p/google-web-toolkit/issues/detail?id=135#c30 - Another explanation


> ### Change Projects Running JVM ###
    * This is the easiest way for 32 bit JVM setup for GWT.
    * Change or add a 32bit JVM in Eclipse project preferences.
    * install a 32bit jvm from your linux repository. like _ia32-sun-java6-bin_
    * Goto: Eclipse > Window (Top Menu) > Preferences > Java > Installed JREs > Add JVM
> > Or
    * GoTo: Debug Configurations > Java Application > GWT Project > (Project Tab) JRE > Alternate JRE > Select JRE Or Install JRE


> OR
> ### Changing the default System JVM (Not Recommended) ###
    1. for ubuntu - use galternatives
    1. Install ia32-sun-java6-bin (install 32bit JVM)
    1. Install galternatives (this is how we will change the default JVM)
    1. Run galternatives
    1. Find Java in the List and choose the ia32 JVM
```
#Ubuntu method of getting galterantives
sudo apt-get galternatives

#run
galternatives

#OR

#Another way to do it in shell
sudo update-alternatives --config java
```

  * Ubuntu jvms located in: /usr/lib/jvm

> ### Problems With GWT and 64bit JVM ###
> > You will get problems trying to run your GWT application in a 64bit JVM during development.


> Get this error trying to run a GWT project? like: wrong ELF class: ELFCLASS32
```
/gwt-linux/libswt-pi-gtk-3235.so: wrong ELF class: ELFCLASS32 (Possible cause: architecture word width mismatch)
```

  * This Error comes from running 64bit JVM and then GWT tries to use 32bit compiled libraries.
  * Use a 64bit IDE with and run your application in a 32bit JVM

> #### GWT 64-bit Linux Issue ####
    * http://code.google.com/p/google-web-toolkit/issues/detail?id=134 - GWT hosted web browser does not work in 64-bit Linux
    * http://code.google.com/p/google-web-toolkit/issues/detail?id=135 - Duplicate



## Configuring Eclipse Memory ##
> Configuring more memory for your java JVM to use for your running java application, in this case eclipse.

  * Are you getting a eclipse out of memory error?
  * [Eclipse Reference Help Link](http://help.eclipse.org/help33/index.jsp?topic=/org.eclipse.platform.doc.user/tasks/running_eclipse.htm)

  * Change paramenters in ~/.eclipse/eclipserc
  * This is great for picking the java jvm without editing any files that have strictor permissions
```
# ~/.eclipse/eclipserc

#Running Eclipse Options
#http://help.eclipse.org/help31/index.jsp?topic=/org.eclipse.platform.doc.isv/reference/misc/runtime-options.html
#http://help.eclipse.org/help21/index.jsp?topic=/org.eclipse.platform.doc.user/tasks/running_eclipse.htm

# Which JRE to run Eclipse
#JAVA_HOME=/usr/lib/jvm/java-6-openjdk - libjvm problematic frame
#JAVA_HOME=/usr/lib/jvm/java-6-sun - libjvm problematic frame

# no crashes yet
JAVA_HOME=/usr/lib/jvm/java-1.5.0-sun

# Arguments to Java Machine running Eclipse
VMARGS="-Xmx1024m"
```

> OR

> Change your file "/usr/lib/eclipse/eclipse.ini". This will change the startup parameters.
```
# you may have to change permissions to the file - "sudo chmod 777 /usr/lib/eclipse/eclipse.ini"
#eclipse.ini file - changes start up parameters
-vmargs
-Xms512m
-Xmx1024m
-XX:PermSize=128m
-XX:MaxPermSize=512m
```

> Or

> Change the way it start up with "/usr/bin/eclipse"
```
# you may have to change permissions to the file - "sudo chmod 777 /usr/bin/eclipse"
#/usr/bin/eclipse - bottom of file
exec /usr/lib/eclipse/eclipse \
    -vm "${JAVACMD}" \
    -install "${INSTALL}" \
    -startup "${STARTUP}" \
    ${CMDLINEARGS} \
    -vmargs -Djava.library.path=/usr/lib/jni \
            -Dgnu.gcj.precompiled.db.path=/var/lib/gcj-4.2/classmap.db \
            -Dgnu.gcj.runtime.VMClassLoader.library_control=never \
            -Dosgi.locking=none ${VMARGS} \ # <-add "\" too
            -XX:PermSize=512M -XX:MaxPermSize=1024M 
#ADD -> -XX:PermSize=512M -XX:MaxPermSize=1024M like the line above
#this is the one I use on my ubuntu amd64, force quit to see if it loaded
#(memory sizes represent start with minimum MB to and go to maximum MB)
```

> Or

> Change the eclipse parameters from command line. This a good way to trace a class not loading correctly.
```
#command line
eclipse -XX:PermSize=128m -XX:MaxPermSize=512m
```

> Or
> ### For apple/mac ###
> > Goto Macintosh HD Root>Applications>Library>eclipse>Show package contents of Eclipse Application icon>Contents>MacOs>eclipse.ini



# Debugging #


> ## Prevent Crashes ##
```
# fredsa - http://code.google.com/p/google-web-toolkit/issues/detail?id=135#c30
LIBXCB_ALLOW_SLOPPY_LOCK=true
```

> ## Watching Traces of Eclipse Crashes ##
> > Does eclipse glitch when you load it. Does eclipse fail to load? Does eclipse crash on loading up or in the middle of operating?

  * Run eclipse from a shell, it will output trace on crash.
  * You can also check log in: /usr/lib/eclipse/configuration
  * User settings are in and user log: ~/workspace/.metadata
  * I suggest only installing one feature at a time then testing it. If you get carried away  you could find yourself reinstalling eclipse.
  * Just a tip I have learned. Install the features from the Europa or Eclipse update sites first, then, install the other features.




## Eclipse 3.3 Update ##

> Update eclipse to europa 3.3.
> http://update.eclipse.org/updates/3.3/ Put this in eclipse update manager.
  * sudo chmod 777 -R /usr/lib/eclipse /usr/local/lib/eclipse - so eclipse can update itself

## Problematic frame crashes in Hardy ##
> When eclipse starts crashing after upgrade. I recommend a clean install using the stand alone version. If you get eclipse problems, its easier just to backup your project and reinstall eclipse. I have wasted many hours trying to figure out why these things happen, coming to find out, its easier with a clean stand alone install.

> ## libjvm.so problematic frame crash ##
> libjvm.so problematic frame crash. Ubuntu Hardy Crashes.
  * https://bugs.launchpad.net/ubuntu/+source/openjdk-6/+bug/206620
  * Use sun-java-1.5, this seems to fix it.

> change this in your ~/.eclipse/eclipserc file - example above
```
 JAVA_HOME=/usr/lib/jvm/java-1.5.0-sun
```

> ## libxcb.so problematic frame crash ##
> > I got rid of this problematic frame by uninstalling eclipse and reinstalling it.


> ## Clean Install ##
> > Instead of dealing with messing with your System, try a Eclipse Standalone Clean Install. Don't forget to backup your projects. After you installed your system, move back your workspace directory.

# Extending Eclipse #

> Adding tools to eclipse to help your GWT development.

## Subversive - SVN Team Provider ##
> Installing subversion for eclipse. Select team provider.
  * [Eclipse Update Site](http://www.eclipse.org/subversive/downloads.php)
  * [Ganymede SVN connectors](http://www.polarion.org/projects/subversive/download/eclipse/2.0/ganymede-site/)
  * [Directions](http://www.eclipse.org/subversive/documentation/gettingStarted/aboutSubversive/install.php) - directions with screen shots

> ### Ganymede Install ###
> > Subversion is a part of Ganymede Update Site.
    * Help > Software Updates > Available Software > Ganymede > Collaboration Tools > subversion
    * Also: add SVN connectors separately - [Ganymede SVN connectors](http://www.polarion.org/projects/subversive/download/eclipse/2.0/ganymede-site/)



> ### Reasons to Use Subversion ###
    * Backup remotely easily and quickly.
    * Develop on multiple PCs
    * Make a mistake and and check your commit history. Revert back.
    * Develop using a team
    * and so much more.

> #### Subversion Setup ####
> > Setup the svn connector to connect to the svn repository. You will need to configure team settings in preferences.
    * eclipse menu > Window > Preferences > Team > SVN > (tab) SVN Client > select "SVN Kit"


> ## Bugzilla Integration ##
> > Integrate buzilla tasklist with eclipse ide.
    * http://www.eclipse.org/mylyn/ - Mylyn Tasklist with bugzilla integration
    * Eclipse IDE > Help (menu) > Software Updates > Find and Install > Search for new features to install > Mylyn (archived site) > Finished (click) > Mylyn (in window) > Mylyn Integration > Bugzilla Integration (select)


> ## Eclipse Metrics ##
> > This is an excellent metrics system to figure out how many lines of code you have and find out several other things with your code base.
    * [http://metrics.sourceforge.net/] - Install the update site into eclipse.