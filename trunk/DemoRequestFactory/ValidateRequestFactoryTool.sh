#!/bin/sh
# made by Brandon Donnelson
# http://gwt-examples.googlecode.com

# Project Path to Web-inf folder (with no trailing /)
BASE="/Users/branflake2267/Documents/workspace/DemoRequestFactory/war/WEB-INF";

#----------------------------
# don't have to edit below - unless you have more request factories you like to add.

OUTPUT="$BASE/classes";

CP="";
CP="$CP$BASE/classes:";
CP="$CP$BASE/lib/*:"; # '*' will only work with java 1.6+

# these need to be in there
#CP="$CP$BASE/lib/requestfactory-apt.jar:";
#CP="$CP$BASE/lib/requestfactory-server+src.jar:";

echo "DEBUG ClassPath: $CP";

java -cp $CP com.google.web.bindery.requestfactory.apt.ValidationTool $OUTPUT \
  org.gonevertical.client.requestfactory.ApplicationRequestFactory # \ more?
  # org.gonevertical.client.requestfactory.ApplicationRequestFactory2 more factories?
  

# the end
exit;