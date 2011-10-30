#!/bin/sh
# created by Brandon Donnelson
#
# Help with the download and setup of GWT source
#
# http://code.google.com/webtoolkit/makinggwtbetter.html
# http://code.google.com/p/google-web-toolkit/source/browse/trunk/eclipse/README.txt
#

echo "starting gwt source download and setup..."

# goto home directory - not needed, but useful to show root location
cd ~

# remove gwt working directory
rm -rf ~/gwt

# make a working directory
mkdir ~/gwt

# go into that directory
cd ~/gwt

# download thirdparty source libs
svn checkout http://google-web-toolkit.googlecode.com/svn/tools/ tools

# download gwt source
svn checkout http://google-web-toolkit.googlecode.com/svn/trunk/ trunk


# TODO add export to enviroment
export GWT_TOOLS=~/gwt/tools

# move to working dir
cd ~/gwt/trunk

# get everything ready
ant


# now you should be ready for this:
# http://code.google.com/p/google-web-toolkit/source/browse/trunk/eclipse/README.txt#178 - Importing the GWT core projects


echo "Finished downloading GWT source and setup.";