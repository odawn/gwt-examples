
&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;

# Eclipse Setup to Inherit Other Source modules #

  1. Right click project > select properties > Libraries tab > Add other projects ./src folder
  1. then goto Projects tab > Add other project
  1. put this in the project.gwt.xml file -> 

&lt;inherits name='com.gawkat.gwt.project'/&gt;


  1. goto debug configurations > project > classpath > user entries > add project
  1. then add src folder > goto Advanced > add folder > select src folder


# TODO #
  * how to configure ant to add sub modules classpath to ant build system
  * how to configure ant to copy other module classes to main module war directory
  * how to configure ant to compile classes and move them to main module
  * how to configure ant to compile sub module and move it to the main module war directory