
&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;

# Including Two Projects/Modules Together #
> Including Two GWT Projects/Modules together, so you can reuse code is not to hard. But it can be frustrating at first.

## What To Do ##
> This is how I do it.
  1. Build two projects independently, be sure the stand on there own and run.
  1. Be sure both projects have different servlet context for there rpc calls, in RpcService.java and ./war/WEB-INF/web.xml
  1. In the project.xml file, inherit the other project, then go to build path and add the project to the build path. Another way is to link the source to its path.

## Example ##
> Coming shortly