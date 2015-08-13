&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;


# Deprecated This Page #
> I am going to stop support on the TomcatWarBuilder shortly. I didn't realize at the time that Ant could do the job so much better. Google has come up with an excellent system in 1.6+. I am excited about using future releases because of the entire change.
  * 1. gwt 1.6+ ant can build your project automatically by a click of a button.
  * 2. gwt 1.6+ project code can be copied to different platforms and be built quickly

# [AntWarBuilding](AntWarBuilding.md) - New Location to goto for building project #


## References ##
[gwtTomcat](gwtTomcat.md) - gwt tomcat setup

# Tomcat War Builder #
> Auto Build or Compile a GWT project for tomcat server. Compile your project in a ready to go War archive. Deploy it by uploading the war file to your tomcat server. Quickly deploy your application for testing.

  * It time stamps the display name so you know when you deployed the gwt module.
  * It auto compiles the files into a ~/production folder ready for archiving
  * It auto Imports your dependency jars into the ~/production/WEB-INF/libs folder
  * It auto imports your dependency classes into the ~/production/WEB-INF/classes folder
  * It auto creates a index.jsp home page for easy navigation to project - this is used just in case now
  * Make your servlet application private, configure var in script.
  * Choose the context of your application - ROOT.war or anyName.war to setup the context of your virtual host
  * sets up the welcome page, so it loads whatever.html as the homepage for your applications context.

> ## Directions ##
  1. Import this Project into eclipse or other java IDE
  1. Compile your GWT project in the eclipse GWT debugger before running this script.
  1. Set this script's variable ProjectDirectory="/home/branflake2267/workspace/gwt-Project"; to your project's home
  1. Run It, In Eclipse Right click on TomcatWarBuilder\_java and hit Debug as java application
  1. Watch Console for log of events
  1. Tweak script to your like

> ## Tomcat Setup ##
> [gwtTomcat](gwtTomcat.md) - how to setup tomcat up.

> ## SVN Location ##
> Download the latest version from SVN. [Source Code](http://code.google.com/p/gwt-examples/source/browse/trunk/gwt-TomcatWarBuilder/src/com/tribling/gwt/TomcatWarBuilder/TomcatWarBuilder.java)

> ## Linux OS ##
> This builder works great with linux.

> ## Windows OS ##
> It now works great with Windows.

> ## Example War Created ##
> [Downloads](http://code.google.com/p/gwt-examples/downloads/list) - War file created from the RPC Advanced Eclipse project

> ## Added ##
  * 03/14/2008 - added gwt 1.5 support for directory /std ouput
  * 01/18/2008 - Added time stamp in the name so I could tell when I deployed it. [Download Most Recent From SVN Repository](http://gwt-examples.googlecode.com/svn/trunk/gwt-TomcatWarBuilder/src/com/tribling/gwt/test/TomcatWarBuilder/TomcatProductionBuilder.java)
  * 02/20/2008 - added servlet application security. fixed regexp.
  * 04/02/2008 - added War rename, so that I could rename application context (like ROOT.war)
  * 04/02/2008 - fixed code up a bit
  * 04/02/2008 - added welcome file list, and or directly redirects to application whatever the name is - doesn't have to be index.jsp, it can be whatever.html as the welcome page
  * works with Milestone 1 and Milestone 2
  * 05/01/2008 - added servlet comments
  * 05/02/2008 - moved around option vars
  * 10/29/2008 - Rewrote the code, and it now compiles on windows too!


# Easy Setup and Run #
Example setup of a main class that runs the builder.
```
public class Run_WarBuilder_RpcBasic {

	/**
	 * run this from console or debug configuration in eclipse
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		
		// refer to this object for variable options
		WarBuilderData data = new WarBuilderData();
		
		// choose your operating system
		// [0=linux, 1=windows]
		data.os = 0;
		
		// project directory (examples below)
		data.projectDirectory = "/home/branflake2267/workspace/gwt-test-RPC-basic";

		// rename war file to [ROOT.war | project.war]
		// ROOT.war is the virtual host root servlet context "/" 
		data.renameWarFile = false; //true = turn on will rename the servlet context (filename)
		
		// if renameWarFile = true then rename to:
		data.renameWarFileNameTo = "ROOT.war"; 
		

		/********** OPTIONAL ***********/
		
		// go to application instead of going to index.jsp true is recommended
		data.goDirectlyToApplication = true;
		
		// Ask for credentials to use application 
		data.askForLogin = false;
		
		data.deleteTempFolder = false;
		
		/********** OPTIONAL ***********/
		
		
		// set up the object that will build the war 
		TomcatWarBuilder build = new TomcatWarBuilder();
		build.setTomcatCompileData(data);
		build.start();
	}

}
```

Builder Options:
```
public class WarBuilderData {
	
	// operating system
	// [0:linux, 1:windows]
	public int os = 0;
	
	// project directory - absolute path to your project root
	public String projectDirectory;

	// rename war file at end
	public boolean renameWarFile = true;
	
	// rename war file to [ROOT.war | project.war]
	// ROOT.war is the virtual host root servlet context "/" 
	public String renameWarFileNameTo = "ROOT.war"; 

	
	
	/********** OPTIONAL ***********/
	
	// temp build folder
	public String tempBuildFolderName = "production";
	
	// Ask for credentials to use gwt application 
	public boolean askForLogin = false;
	
	public boolean deletetempBuildFolder = true;
	
	//default location is project root. Set this for another location
	//set with no trailing slash like "/home/branflake2267/warFiles"
	//private  String TempWarFileLocation = "/home/branflake2267"; 
	public String tempWarFileLocation = null;

	// after all done - delete the temp folder
	public boolean deleteTempFolder = true; 
	
	// go to application instead of going to index.jsp - true is recommended
	// you can set this to false if you want, when you intend to include your compilation of code to another site,
	// although, you can directly copy the code from the www directory and include it in your sites setup
	public boolean  goDirectlyToApplication = true;
	
	/********** OPTIONAL END ***********/
}
```