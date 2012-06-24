#!/bin/sh
# created by Brandon Donnelson
#
# Help with the download and setup of GWT source
# http://code.google.com/p/gwt-examples/wiki/GWTAutoSourceSetup - this script is here
#
# http://code.google.com/webtoolkit/makinggwtbetter.html
# http://code.google.com/p/google-web-toolkit/source/browse/trunk/eclipse/README.txt
#

WORKING_DIRECTORY=~/workspace-source/gwt

if [ "$1" = "-h" ]
then
    echo "sh ./gwt_source_setup.sh -o [-o=overrite|replace previous download]"
fi 

echo "starting GWT source download and setup..."

# install svn?
hash svn 2>&- || { echo >&2 "Oops, but you need svn. Install svn.  Aborting setup, Exiting."; exit 1; }

# install ant?
hash ant 2>&- || { echo >&2 "Oops, but you need ant. Install ant.  Aborting setup, Exiting."; exit 1; }


if [ ! -d $WORKING_DIRECTORY ]; then
    mkdir $WORKING_DIRECTORY
    cd $WORKING_DIRECTORY
    
    # download third party source libs & gwt source
    svn checkout http://google-web-toolkit.googlecode.com/svn/tools/ tools
    svn checkout http://google-web-toolkit.googlecode.com/svn/trunk/ trunk

    else 
    cd $WORKING_DIRECTORY
    svn update 
fi


# TODO add export to enviroment
export GWT_TOOLS=$WORKING_DIRECTORY/tools
echo "exported GWT_TOOLS=$GWT_TOOLS to session only"

# move to working dir
cd $WORKING_DIRECTORY/trunk

# build gwt
ant


# now you should be ready for this:
# http://code.google.com/p/google-web-toolkit/source/browse/trunk/eclipse/README.txt#178 - Importing the GWT core projects


echo "Finished downloading GWT source and setup.";
