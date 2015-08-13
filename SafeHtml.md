
&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;



# My Use Of SafeHTML #
> fromString escapes the string.

```
  private void test() {
    
    String s = "<script>//TODO nothing</script> This is my text.";
    
    System.out.println("raw s=" + s);
    
    SafeHtml s_safe = SafeHtmlUtils.fromString(s);

    System.out.println("escaped now -> SafeHtml=" + s_safe.asString());
    
    RichTextArea rta = new RichTextArea();
    rta.setHTML(s_safe);
    
    RootPanel.get().add(rta);
  }
```

# Black List HTML Tags #
> Tags to black list from dynamic html content:

```
applet
base
basefont
bgsound  
body
embed  
frame
frameset
head
html
id
iframe
ilayer
layer
link
isindex  
meta
name
noframes  
noscript  
object  
param
script
style  
title
xml
```

# White List HTML Tags #
> Tags ok to set

```
a
abbr
acronym
address
area
b
big
blockquote
br
button
caption
center
cite
code
col
colgroup
dd
del
dfn
dir
div
dl
dt
em
fieldset
font
form
h1
h2
h3
h4
h5
h6
hr
i
img
input
ins
kbd
label
legend
li
map
menu
ol
optgroup
option
p
pre
q
s
samp
select
small
span
strike
strong
sub
sup
table
tbody
td
textarea
tfoot
th
thead
tr
tt
u
ul
var
```

# Attributes #
> Hidden javascript in attributes
```
style="background: url(javascript:window.location='http://test.org/')"
style="any: expression(window.location='http://example.org/')"
style="&#97;&#110;&#121;&#58;..."
style="&#x61;&#x6e;&#x79;&#x3a;..."
```

# Sanitizing HTML #
> I hacked the simple sanitizer. I added style as approved and check it for & chars. I also added tags to the list. I also had to fix the white list tag comparison to nix attributes.

```
public class HtmlSanitizerUtil implements HtmlSanitizer {

  private static final HtmlSanitizerUtil INSTANCE = new HtmlSanitizerUtil();

  private static final Set<String> TAG_WHITELIST = new HashSet<String>(
      Arrays.asList("a", "abbr", "acronym", "address", "area", "b", "big", "blockquote", "br", 
          "button", "caption", "center", "cite", "code", "col", "colgroup", "dd", "del", 
          "dfn", "dir", "div", "dl", "dt", "em", "fieldset", "font", "form", "h1", "h2", 
          "h3", "h4", "h5", "h6", "hr", "i", "img", "input", "ins", "kbd", "label", "legend", 
          "li", "map", "menu", "ol", "optgroup", "option", "p", "pre", "q", "s", "samp", 
          "select", "small", "span", "strike", "strong", "style", "sub", "sup", "table", "tbody", "td", 
          "textarea", "tfoot", "th", "thead", "tr", "tt", "u", "ul", "var"));

  /**
   * Return a singleton SimpleHtmlSanitizer instance.
   *
   * @return the instance
   */
  public static HtmlSanitizerUtil getInstance() {
    return INSTANCE;
  }

  /**
   * HTML-sanitizes a string.
   *
   * <p>
   * The input string is processed as described above. The result of sanitizing
   * the string is guaranteed to be safe to use (with respect to XSS
   * vulnerabilities) in HTML contexts, and is returned as an instance of the
   * {@link SafeHtml} type.
   *
   * @param html the input String
   * @return a sanitized SafeHtml instance
   */
  public static SafeHtml sanitizeHtml(String html) {
    if (html == null) {
      throw new NullPointerException("html is null");
    }
    return new SafeHtmlString(simpleSanitize(html));
  }

  /*
   * Sanitize a string containing simple HTML markup as defined above. The
   * approach is as follows: We split the string at each occurence of '<'. Each
   * segment thus obtained is inspected to determine if the leading '<' was
   * indeed the start of a whitelisted tag or not. If so, the tag is emitted
   * unescaped, and the remainder of the segment (which cannot contain any
   * additional tags) is emitted in escaped form. Otherwise, the entire segment
   * is emitted in escaped form.
   *
   * In either case, EscapeUtils.htmlEscapeAllowEntities is used to escape,
   * which escapes HTML but does not double escape existing syntactially valid
   * HTML entities.
   */
  // TODO(xtof): should this be in a utils class?
  private static String simpleSanitize(String text) {
    StringBuilder sanitized = new StringBuilder();
    
    boolean firstSegment = true;
    for (String segment : text.split("<", -1)) {
      if (firstSegment) {
        /*
         *  the first segment is never part of a valid tag; note that if the
         *  input string starts with a tag, we will get an empty segment at the
         *  beginning.
         */
        firstSegment = false;
        sanitized.append(SafeHtmlUtils.htmlEscapeAllowEntities(segment));
        continue;
      }

      /*
       *  determine if the current segment is the start of an attribute-free tag
       *  or end-tag in our whitelist
       */
      int tagStart = 0; // will be 1 if this turns out to be an end tag.
      int tagEnd = segment.indexOf('>');
      String tag = null;
      boolean isValidTag = false;
      if (tagEnd > 0) {
        if (segment.charAt(0) == '/') {
          tagStart = 1;
        }
        tag = segment.substring(tagStart, tagEnd);
        String exist = tag.replaceAll("\040.*", ""); // for attributes
        if (TAG_WHITELIST.contains(exist)) {
          isValidTag = true;
        } else {
          System.out.println("not in whitelist tag=" + tag);
        }
        
      }

      if (isValidTag) {
        // append the tag, not escaping it
        if (tagStart == 0) {
          sanitized.append('<');
        } else {
          // we had seen an end-tag
          sanitized.append("</");
        }
        sanitized.append(tag).append('>');

        // append the rest of the segment, escaping it
        sanitized.append(SafeHtmlUtils.htmlEscapeAllowEntities(
            segment.substring(tagEnd + 1)));
      } else {
        // just escape the whole segment
        sanitized.append("&lt;").append(
            SafeHtmlUtils.htmlEscapeAllowEntities(segment));
      }
    }
    String s = sanitized.toString();
    
    // get rid weird style stuff - style="&#97;&#110;&#121;&#58;..." or style="&#x61;&#x6e;&#x79;&#x3a;..."
    s = s.replaceAll("style=\".*?&.*?\"", "");
    s = s.replaceAll("style=\".*?java.*?\"", "");
     
    return s;
  }

  /*
   * Note: We purposely do not provide a method to create a SafeHtml from
   * another (arbitrary) SafeHtml via sanitization, as this would permit the
   * construction of SafeHtml objects that are not stable in the sense that for
   * a {@code SafeHtml s} it may not be true that {@code s.asString()} equals
   * {@code SimpleHtmlSanitizer.sanitizeHtml(s.asString()).asString()}. While
   * this is not currently an issue, it might become one and result in
   * unexpected behavior if this class were to become serializable and enforce
   * its class invariant upon deserialization.
   */

  // prevent external instantiation
  private HtmlSanitizerUtil() {
  }

  public SafeHtml sanitize(String html) {
    return sanitizeHtml(html);
  }
}
```

## Escape and Unescape ##
> Escape and Unescape, or Encode and decode html for safe url parameter. This is one way to do it.

```
 public native String encodeURIComponent(String s) /*-{
    return encodeURIComponent(s);
  }-*/;
  
  public native String decodeURIComponent(String s) /*-{
    return decodeURIComponent(s);
  }-*/;
```

&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;
