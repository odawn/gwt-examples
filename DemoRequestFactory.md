
&lt;wiki:gadget url="http://gwt-examples.googlecode.com/svn/trunk/DemoGadgetXml/war/gadget-aff.xml" height="100" width="740" border="0" /&gt;

# Demo Request Factory Notes #
> Setting up request factory in gwt

  * [Demo](http://demogwtrequestfactory.appspot.com) - Working Appspot Demo
  * [Demo's Source](http://code.google.com/p/gwt-examples/source/browse/#svn%2Ftrunk%2FDemoRequestFactory) - Find the latest code here

## Another Demo and Source Here ##
> [DemoActivitiesAndPlaces](DemoActivitiesAndPlaces.md) - More extensive demo using RequestFactory

## Errors ##
  * [Group Thread](https://groups.google.com/d/topic/google-web-toolkit/vBFmzz7R56M/discussion) - Errors I had during building this project

## Reference ##
  * [Validation Tool Setup](http://code.google.com/p/google-web-toolkit/wiki/RequestFactoryInterfaceValidation) - Setup the RequestFactory annotation processer
  * [ObjectDB JPA/JDO Reference](http://www.objectdb.com/api/java/jpa) - good JPA annotation reference here
  * [Datanuclueas JPA Annotations](http://www.datanucleus.org/products/accessplatform_3_0/jpa/annotations.html) - Annotation defs, @Entity or @Transient annotation reference here
  * [Toplink jpa annotations](http://www.oracle.com/technetwork/middleware/ias/toplink-jpa-annotations-096251.html) - Annotation defs, @Entity or @Transient annotation reference here

## Be Sure To Have ##
  * Add Additional Compile Arguments - "-extra war/WEB-INF/deploy"
  * [META-INF/Persistence.xml](http://code.google.com/p/gwt-examples/source/browse/trunk/DemoRequestFactory/src/META-INF/persistence.xml) - For the JPA AppEngine persistence. JDO persistence and Objectify could be used instead.
  * war/WEB-INF/lib/requestfactor-server.jar
  * war/WEB-INF/lib/gwt-servlet-deps.jar - you'll need the org/json|org.json classes in this...
  * It is mandatory to have in the server entity method find[EntityClass](EntityClass.md)(Long id) - [see source](http://code.google.com/p/gwt-examples/source/browse/trunk/DemoRequestFactory/src/org/gonevertical/server/domain/NameData.java)
  * Setup the servlet mapping!!! Otherwise you get a 404 during the request.

## Request Factory Validation Tool - Automatic ##
> Setup the project to do it automatically. Right click on project > goto properties > Java Compiler > Annotation Processer... then follow the eclipse screen shot [Setup](http://code.google.com/p/google-web-toolkit/wiki/RequestFactoryInterfaceValidation) directions.

  * [Setup RequestFactory Annotation Processer](http://code.google.com/p/google-web-toolkit/wiki/RequestFactoryInterfaceValidation) - Screen shots on how to setup up the validation tool and annotation processer.
  * [Annotation Processer Setup 2](http://code.google.com/p/google-web-toolkit/wiki/RequestFactory_2_4#RequestFactorySource_and_Annotation_Processing) - How to setup the client processor.


## Request Factory Validation Tool - Manual ##
> You'll have to run the validation tool to setup the RequestFactory piping!
  * You know this worked if more files show up in your class path - next to the request factory
  * [See Source](http://code.google.com/p/gwt-examples/source/browse/trunk/DemoRequestFactory/ValidateRequestFactoryTool.sh) - See the latest source of the Validator Tool Builder .sh
  * [Wiki Ref doc](http://code.google.com/p/google-web-toolkit/wiki/RequestFactoryInterfaceValidation) - notes on whats going on here
```
#!/bin/sh
# made by Brandon Donnelson
# http://gwt-examples.googlecode.com

# Project Path to Web-inf folder (with no trailing /)
BASE="/Users/branflake2267/Documents/workspace/DemoRequestFactory/war/WEB-INF";

#----------------------------
# don't have to edit below - unless you have more Application Request Factories

OUTPUT="$BASE/classes";

CP="";
CP="$CP$BASE/classes:";
CP="$CP$BASE/lib/*:"; # '*' will only work with java 1.6+

# these need to be in there
#CP="$CP$BASE/lib/requestfactory-apt.jar:";
#CP="$CP$BASE/lib/requestfactory-server+src.jar:";

echo "DEBUG ClassPath: $CP";

java -cp $CP com.google.web.bindery.requestfactory.apt.ValidationTool $OUTPUT \
  org.gonevertical.client.requestfactory.ApplicationRequestFactory 
  # Do you need more Request Factories???
  

# the end
exit;
```


## Random Notes ##
> Some random stuff I used in this project:
  * [Custom Event Handler Source](http://code.google.com/p/gwt-examples/source/browse/#svn%2Ftrunk%2FDemoRequestFactory%2Fsrc%2Forg%2Fgonevertical%2Fclient%2Fnamelist) - Custom GWT Event Handler(s)

## Modify ##
> notes on modifying a proxyDto object
  * You get a frozen freeze error after the request object returns and you try to modify it again.
  * creating and new proxyDto, do it after you grab the request context, otherwise you get a cross update error
> [Source Location](http://code.google.com/p/gwt-examples/source/browse/trunk/WalletInventory/src/com/gonevertical/client/views/impl/WalletEditViewImpl.java#164)
```
  private void save() {
    
    WalletDataRequest req = appFactory.getRequestFactory().getWalletDataRequest();
    
    WalletDataProxy data = null;
    if (walletData == null) { // insert|create
      data = req.create(WalletDataProxy.class);
      
    } else { // update|edit
      data = req.edit(walletData);
    }
    
    data.setName(getName());
    
    req.persist().using(data).fire(new Receiver<WalletDataProxy>() {
      public void onSuccess(WalletDataProxy walletData) {
        process(walletData);
      }
      public void onFailure(ServerFailure error) {
        super.onFailure(error);
      }
    });
  }
```

&lt;wiki:gadget url="http://gwt-examples.googlecode.com/svn/trunk/DemoGadgetXml/war/gadget-aff.xml" height="100" width="740" border="0" /&gt;
