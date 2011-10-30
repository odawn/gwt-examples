package org.gonevertical.core.client.html;

import com.google.gwt.safehtml.shared.SafeHtml;

/**
 * A string wrapped as an object of type {@link SafeHtml}.
 *
 * <p>
 * This class is package-private and intended for internal use by the
 * {@link com.google.gwt.safehtml} package.
 *
 * <p>
 * All implementors must implement .equals and .hashCode so that they operate
 * just like String.equals() and String.hashCode().
 */
class SafeHtmlString implements SafeHtml {
  private String html;

  /**
   * Constructs a {@link SafeHtmlString} from a string. Callers are responsible
   * for ensuring that the string passed as the argument to this constructor
   * satisfies the constraints of the contract imposed by the {@link SafeHtml}
   * interface.
   *
   * @param html the string to be wrapped as a {@link SafeHtml}
   */
  SafeHtmlString(String html) {
    if (html == null) {
      throw new NullPointerException("html is null");
    }
    this.html = html;
  }

  /**
   * No-arg constructor for compatibility with GWT serialization.
   */
  @SuppressWarnings("unused")
  private SafeHtmlString() {
  }

  /**
   * {@inheritDoc}
   */
  public String asString() {
    return html;
  }

  /**
   * Compares this string to the specified object.
   */
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof SafeHtml)) {
      return false;
    }
    return html.equals(((SafeHtml) obj).asString());
  }

  /**
   * Returns a hash code for this string.
   */
  @Override
  public int hashCode() {
    return html.hashCode();
  }
}