
```
[ERROR] Failed to execute goal org.codehaus.mojo:gwt-maven-plugin:2.7.0-SNAPSHOT:compile (default) on project MyGxtProject31: Failed to resolve artifact: Some problems were encountered while processing the POMs:
[ERROR] [ERROR] Non-resolvable import POM: Could not find artifact org.ow2.asm:asm-parent:pom:5.0.3 @ com.google.gwt:gwt:2.7.0-SNAPSHOT, /Users/branflake2267/.m2/repository/com/google/gwt/gwt/2.7.0-SNAPSHOT/gwt-2.7.0-SNAPSHOT.pom, line 74, column 25
[ERROR] for project
[ERROR] -> [Help 1]
org.apache.maven.lifecycle.LifecycleExecutionException: Failed to execute goal org.codehaus.mojo:gwt-maven-plugin:2.7.0-SNAPSHOT:compile (default) on project MyGxtProject31: Failed to resolve artifact
	at org.apache.maven.lifecycle.internal.MojoExecutor.execute(MojoExecutor.java:216)
	at org.apache.maven.lifecycle.internal.MojoExecutor.execute(MojoExecutor.java:153)
	at org.apache.maven.lifecycle.internal.MojoExecutor.execute(MojoExecutor.java:145)
	at org.apache.maven.lifecycle.internal.LifecycleModuleBuilder.buildProject(LifecycleModuleBuilder.java:108)
	at org.apache.maven.lifecycle.internal.LifecycleModuleBuilder.buildProject(LifecycleModuleBuilder.java:76)
	at org.apache.maven.lifecycle.internal.builder.singlethreaded.SingleThreadedBuilder.build(SingleThreadedBuilder.java:51)
	at org.apache.maven.lifecycle.internal.LifecycleStarter.execute(LifecycleStarter.java:116)
	at org.apache.maven.DefaultMaven.doExecute(DefaultMaven.java:361)
	at org.apache.maven.DefaultMaven.execute(DefaultMaven.java:155)
	at org.apache.maven.cli.MavenCli.execute(MavenCli.java:584)
	at org.apache.maven.cli.MavenCli.doMain(MavenCli.java:213)
	at org.apache.maven.cli.MavenCli.main(MavenCli.java:157)
	at sun.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at sun.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:57)
	at sun.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.lang.reflect.Method.invoke(Method.java:606)
	at org.codehaus.plexus.classworlds.launcher.Launcher.launchEnhanced(Launcher.java:289)
	at org.codehaus.plexus.classworlds.launcher.Launcher.launch(Launcher.java:229)
	at org.codehaus.plexus.classworlds.launcher.Launcher.mainWithExitCode(Launcher.java:415)
	at org.codehaus.plexus.classworlds.launcher.Launcher.main(Launcher.java:356)
Caused by: org.apache.maven.plugin.MojoExecutionException: Failed to resolve artifact
	at org.codehaus.mojo.gwt.AbstractGwtMojo.getJarFiles(AbstractGwtMojo.java:289)
	at org.codehaus.mojo.gwt.AbstractGwtMojo.getGwtUserJar(AbstractGwtMojo.java:263)
	at org.codehaus.mojo.gwt.shell.CompileMojo.compile(CompileMojo.java:436)
	at org.codehaus.mojo.gwt.shell.CompileMojo.doExecute(CompileMojo.java:405)
	at org.codehaus.mojo.gwt.shell.AbstractGwtShellMojo.execute(AbstractGwtShellMojo.java:159)
	at org.apache.maven.plugin.DefaultBuildPluginManager.executeMojo(DefaultBuildPluginManager.java:133)
	at org.apache.maven.lifecycle.internal.MojoExecutor.execute(MojoExecutor.java:208)
	... 19 more
Caused by: org.apache.maven.project.InvalidProjectModelException: Some problems were encountered while processing the POMs:
[ERROR] Non-resolvable import POM: Could not find artifact org.ow2.asm:asm-parent:pom:5.0.3 @ com.google.gwt:gwt:2.7.0-SNAPSHOT, /Users/branflake2267/.m2/repository/com/google/gwt/gwt/2.7.0-SNAPSHOT/gwt-2.7.0-SNAPSHOT.pom, line 74, column 25

```