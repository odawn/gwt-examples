TODO - change

#!/bin/sh


BASE="/home/design/workspace/Metrics/war/WEB-INF";


# class path setup
CP="";
CP="$CP$BASE/classes:";
CP="$CP$BASE/lib:";

CP="$CP$BASE/lib/commons-codec-1.3.jar:";
CP="$CP$BASE/lib/mysql-connector-java-5.1.5-bin.jar:";

# debug classpath
echo "\nDEBUG ClassPath: $CP";

# run the java
java -cp $CP com...TODO;

exit;