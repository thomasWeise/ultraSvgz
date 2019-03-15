package thomasWeise.ultraSvgz;

import org.w3c.dom.DOMConfiguration;
import org.w3c.dom.Document;

import thomasWeise.tools.ConsoleIO;

/** a tool job for canonicalizing the DOM document IDs */
final class _Canonicalize {

  /**
   * Canonicalize a document
   *
   * @param in
   *          the input document
   * @return the new document
   */
  static final Document _apply(final Document in) {
    try {
      final Document copy = _Tools._clone(in);

      final DOMConfiguration cfg = copy.getDomConfig();
      cfg.canSetParameter("canonical-form", Boolean.TRUE);
      cfg.canSetParameter("comments", Boolean.FALSE);
      copy.normalizeDocument();
      return copy;
    } catch (final Throwable error) {
      ConsoleIO.stderr("error when canonicalizing document",
          error);
    }
    return null;
  }
}
