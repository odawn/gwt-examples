
&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;

# Project Building in GWT 1.6+ #
> You can quickly build your application for your production server with Ant.

  1. Project > right click build.xml > Run As > Select 'External Tools Configurations'
  1. (Don't forget to: External Tools Configuration > goto Targets > select War 'create a war file' - Green Arrow with toolbox icon)
  1. Look at > Targets (tab) - compare that with the build.xml editor window
  1. Select Run

> OR

  1. Play button arrow with toolbox in graphics buttons menu > External Tools configurations (for First Time) | Select Project


## Eclipse Plugin ##
> The eclipse plugin has the compile feature options built in.

  1. Right click on project > Debug As > Web Application
  1. OR
  1. Hit the Red"G" Box icon top left, and compile options will appear
  1. create a war, by zipping up the war directory and rename it ROOT.war or Project.war (move libraries if need be, if there linked and not in the library folder)
  1. (on ubuntu, select all files and folders in war, right click and Create Archive)
