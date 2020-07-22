package thomasWeise.ultraSvgz;

import java.util.Arrays;
import java.util.Comparator;

import org.w3c.dom.Document;

/**
 * A number system assigns strings to numbers. It is based on the
 * characters permitted in XML as well as the frequency of
 * characters in a given DOM document. The goal is to compute
 * numbers which are both compact and compress well.
 */
final class _NumberSystem {

  /** the valid id starts and ends */
  private static final char[][] VALID_ID;
  /** the default ordering of the letters */
  private static final char[] ORDER;

  static {
    final String s =
        "etaoinsrhdlucmfywgpbvkxqjzETAOINSRHDLUCMFYWGPBVKXQJZ";//$NON-NLS-1$

    final char[] start = s.toCharArray();
    ORDER = start.clone();
    Arrays.sort(start);

    final char[] end = (s + "0123456789_.-").toCharArray();//$NON-NLS-1$
    Arrays.sort(end);

    VALID_ID = new char[][] { start, end };
  }

  /** the code */
  private final char[][] m_code;

  /**
   * create the id manager
   *
   * @param doc
   *          the input document
   */
  _NumberSystem(final Document doc) {
    super();
    this.m_code = _NumberSystem.__getCode(doc);
  }

  /**
   * get the id for the specified value
   *
   * @param value
   *          the value
   * @return the id
   */
  String _getID(final int value) {
    final StringBuilder sb = new StringBuilder();

    char[] use = this.m_code[0];
    int val = value;
    do {
      sb.append(use[val % use.length]);
      val /= use.length;
      use = this.m_code[1];
    } while (val != 0);

    return (sb.toString());
  }

  /** the comparator */
  private static final Comparator<long[]> LCMP = (a, b) -> {
    final int res = Long.compare(b[1], a[1]);
    if (res != 0) {
      return res;
    }
    int ii = 0;
    for (final char ch1 : _NumberSystem.ORDER) {
      if (ch1 == a[0]) {
        break;
      }
      ii++;
    }
    int jj = 0;
    for (final char ch2 : _NumberSystem.ORDER) {
      if (ch2 == b[0]) {
        break;
      }
      jj++;
    }
    return Integer.compare(ii, jj);
  };

  /**
   * get a sub-set of characters to be used for encoding names
   * and ids
   *
   * @param document
   *          the document
   * @return the subset: two arrays, the first one with the valid
   *         characters for starting the ID and the second one
   *         with the valid characters for all further IDs
   */
  private static char[][] __getCode(final Document document) {
    final long[] hist = new long[1
        + _NumberSystem.VALID_ID[1][_NumberSystem.VALID_ID[1].length
            - 1]];
    for (final char ch : _Tools._toChars(document)) {
      if ((ch >= 0) && (ch < hist.length)) {
        ++hist[ch];
      }
    }

    final char[][] result = new char[2][];
    for (int i = 2; (--i) >= 0;) {
      char[] can = _NumberSystem.VALID_ID[i];
      final long[][] l = new long[can.length][2];
      int j = 0;
      for (final char ch : can) {
        l[j][0] = ch;
        l[j][1] = hist[ch];
        j++;
      }
      Arrays.sort(l, _NumberSystem.LCMP);

      int len = 0;
      for (final long[] ll : l) {
        if (ll[1] <= 0L) {
          break;
        }
        len++;
      }

      can = new char[len];
      for (; (--len) >= 0;) {
        can[len] = ((char) (l[len][0]));
      }
      result[i] = can;
    }

    return result;
  }
}
