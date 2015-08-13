
&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;

# Bubble Up #
> Here is another example of bubble up events.
  * [Demo](http://demobubbleup.appspot.com) - This has parent and child widgets. You can check the source out for my strategies of events. I also use Client Persistence Widget.
  * [Source](http://code.google.com/p/gwt-examples/source/browse/#svn%2Ftrunk%2FDemoBubbleUp%2Fsrc%2Forg%2Fgonevertical%2Fbubbleup%2Fclient) - source code for the bubbler

# Click Handler Demo #
> I use bubble up change events to tell tell the parents whats going on.
  * [Demo](http://gwtexample.appspot.com) - demo application
  * [Source](http://code.google.com/p/gwt-examples/source/browse/#svn/trunk/gwt_handler_example/src/com/gawkat/gwt/example_handler/client) - source for the demo application

# Math Flash Card Demo #
> I use bubble up change events here also.
  * [Demo](http://mathflashcard.appspot.com/) - demo application
  * [Source](http://code.google.com/p/gwt-examples/source/browse/#svn/trunk/FlashCard/src/com/gawkat/flashcard/client) - source for the demo application

# My Goal Here #
> Show the way I manage Application navigation and state as well as widget navigation and state. And one more thing, I also consider state management with and without the URL address system, as to you can run your application standalone.



# Application Navigation Visualization #
## Here is how I see it ##
> This could use some adjusting to transpose it into the new event system. Although the point is the same.
![http://gwt-examples.googlecode.com/files/GWT_Application_Navigation.png](http://gwt-examples.googlecode.com/files/GWT_Application_Navigation.png)

## Personal Notes ##
> I don't think its possible or always prudent to send the entire application interaction variables through the querystring. In most cases I only need a few variables to setup the applications widget in the history anchor. I see three event types, URL, User, and Push to create application interaction. All three blend together at some stage of the applications state. Although some of the time, you do not need all the event parameters to render the application.

> I like to tie together the entire application with a central core that controls navigation. Although doing this creates a challenge in tracking all the events because each widget has a different combination of parameters that makes up is interactions. I don't think there magical way to create a method to control all three interactions except through trial an error.

> Trial and Error is the root of all efficiency and truly effective application. You can only tune through trial and error. Also, I have remind myself constantly to never get hung up on a current code write because I can tune it later. Tuning may mean rewriting it which I think is burden-sum although very rewarding.

### Fundamental Difference Web 1.0 and Web 2.0 ###
> I consider application interaction the fundamental difference between Web 1.0 and Web 2.0. Also, for me this was one of most challenging things to adapt to after learning static web page design. Understanding calling the server for information and having the server call back when it is done, resembles parallel processing to me. Creating the logic to understand the interaction is paramount to a fantastic web application, but even more, is trying to understand how the user thinks and making that application intuitive, simple, and as automatic as possible. For instance when I worked in a labor trade I tried think whats next while I was working on the current task. If I am cutting a board to size to fit, the next cut is queued in the brain, which allows to accomplish the task faster. Comparing that to the user interface, anticipating the next move or contemplating how to achieve the same result with less steps is a utmost desire.

> Applications should think more and have the user do less to accomplish the same task. There is so much opportunity to improve with automated efficiency it astounds me. When I look at a web page, I see opportunity for improvements to interact with the user.


&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;
