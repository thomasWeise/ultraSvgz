package thomasWeise.ultraSvgz;

import java.nio.charset.Charset;
import java.util.regex.Pattern;

/** a filter for removing white space */
final class _CleanseWhitespace {

  /** the charset */
  private static final Charset __UTF8_CHARSET =
      Charset.forName("UTF-8"); //$NON-NLS-1$

  /** the double whitespace pattern */
  private static final Pattern WS2 = Pattern.compile("\\s+");//$NON-NLS-1$
  /** the double whitespace pattern */
  private static final Pattern BEFORE1 =
      Pattern.compile("\\s+\\<");//$NON-NLS-1$
  /** the double whitespace pattern */
  private static final Pattern BEFORE2 =
      Pattern.compile("\\<\\s+");//$NON-NLS-1$
  /** the double whitespace pattern */
  private static final Pattern AFTER1 =
      Pattern.compile("\\>\\s+");//$NON-NLS-1$
  /** the double whitespace pattern */
  private static final Pattern AFTER2 =
      Pattern.compile("\\s+\\>");//$NON-NLS-1$
  /** the double whitespace pattern */
  private static final Pattern AFTER3 =
      Pattern.compile("\\s+\\/\\>");//$NON-NLS-1$
  /** the double comment pattern */
  private static final Pattern COMMENT =
      Pattern.compile("\\<\\!\\-\\-.+\\-\\-\\>");//$NON-NLS-1$

  /**
   * remove all white space from string and add it to a buffer
   *
   * @param dest
   *          the destination
   * @param add
   *          the string
   */
  private static void __noWS(final StringBuilder dest,
      final String add) {
    String a = _CleanseWhitespace.WS2.matcher(add.trim())
        .replaceAll(" ");//$NON-NLS-1$
    a = _CleanseWhitespace.COMMENT.matcher(a).replaceAll("");//$NON-NLS-1$
    a = _CleanseWhitespace.BEFORE1.matcher(a).replaceAll("<");//$NON-NLS-1$
    a = _CleanseWhitespace.BEFORE2.matcher(a).replaceAll("<");//$NON-NLS-1$
    a = _CleanseWhitespace.AFTER1.matcher(a).replaceAll(">");//$NON-NLS-1$
    a = _CleanseWhitespace.AFTER2.matcher(a).replaceAll(">");//$NON-NLS-1$
    a = _CleanseWhitespace.AFTER3.matcher(a).replaceAll("/>");//$NON-NLS-1$
    dest.append(a.trim());
  }

  /**
   * remove all white space
   *
   * @param in
   *          the input document
   * @return the output document
   */
  static byte[] _apply(final byte[] in) {
    final String input =
        new String(in, _CleanseWhitespace.__UTF8_CHARSET);
    final StringBuilder result =
        new StringBuilder(input.length());

    int start = 0;
    for (;;) {
      int end = input.length();
      String next = null;

      int s1 = input.indexOf("<text", start); //$NON-NLS-1$
      if (s1 >= start) {
        next = "</text"; //$NON-NLS-1$
        end = s1;
      }

      s1 = input.indexOf("<tspan", start); //$NON-NLS-1$
      if ((s1 >= start) && (s1 < end)) {
        next = "</tspan"; //$NON-NLS-1$
        end = s1;
      }

      s1 = input.indexOf("<![CDATA[", start); //$NON-NLS-1$
      if ((s1 >= start) && (s1 < end)) {
        next = "]]>"; //$NON-NLS-1$
        end = s1;
      }

      _CleanseWhitespace.__noWS(result,
          input.substring(start, end));
      if (end >= input.length()) {
        break;
      }

      final int s3 = input.indexOf(next, end);
      if (s3 <= end) {
        return null;
      }
      final int s4 = input.indexOf('>', s3);
      if (s4 <= s3) {
        return null;
      }
      start = s4 + 1;
      result.append(input.substring(end, start));
      if (start >= input.length()) {
        break;
      }
    }

    final String output = result.toString().trim();

    if (output.length() >= input.length()) {
      return null;
    }

    return output.getBytes(_CleanseWhitespace.__UTF8_CHARSET);
  }
}
