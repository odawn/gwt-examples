
&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;

# Updated 10/22/2011 #
> I have made this better. Try out the demo - [Demo](http://demogwttextexpand.appspot.com/)

# Auto Text Box Growth or Expansion #
TextBox and TextArea auto expand dynamically changing size during typing into the inputs. Grow the textbox as one types.

  * [Demo](http://demogwttextexpand.appspot.com/) - Textbox Expansion Growth Demo
  * [WiseTextArea.java Source](http://code.google.com/p/gwt-examples/source/browse/trunk/GoneVertical-Core/src/org/gonevertical/core/client/input/WiseTextArea.java)
  * [WiseTextBox.java Source](http://code.google.com/p/gwt-examples/source/browse/trunk/GoneVertical-Core/src/org/gonevertical/core/client/input/WiseTextBox.java)
  * [WiseRichTextArea.java Source](http://code.google.com/p/gwt-examples/source/browse/trunk/GoneVertical-Core/src/org/gonevertical/core/client/input/richtext/WiseRichTextArea.java)

## Cloning CSS and Calculating Size ##
> The challenge is to clone the css from the textbox to a html span and figure out the size and then set it back to the textbox. A textbox is easy to change the size, but the TextArea I have found more difficult because there are more variables to deal with.

# RichTextArea #
> Couple things I like to note for RichTextArea
  * I've added paste intercepting. This way pasting doesn't add any formatting. Although, firefox/mozilla doesn't like you digging into the clipboard api.
  * [ClipBoard Api Source Directory](http://code.google.com/p/gwt-examples/source/browse/#svn%2Ftrunk%2FGoneVertical-Core%2Fsrc%2Forg%2Fgonevertical%2Fcore%2Fclient%2Finput%2Fclipboardapi) - My clipboard api cross browser impl. (well...sorta)
  * [RichTextArea Source Directory](http://code.google.com/p/gwt-examples/source/browse/#svn%2Ftrunk%2FGoneVertical-Core%2Fsrc%2Forg%2Fgonevertical%2Fcore%2Fclient%2Finput%2Frichtext%2Fworkaround) - Hacking RichTextArea to work with paste and double clicking.


&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;