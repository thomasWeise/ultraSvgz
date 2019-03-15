package thomasWeise.ultraSvgz;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * An ID translator proceeds in two phases: First, all IDs need
 * to be registered. We count their frequencies. Then, they get
 * new names, computed based on their frequencies. Then, you can
 * use these new names to translate the IDs.
 */
final class _IDTranslator {

  /** the comparator */
  private static final Comparator<
      Map.Entry<String, AtomicLong>> CMP = (a, b) -> {
        final int res = Long.compare(b.getValue().longValue(),
            a.getValue().longValue());
        if (res != 0) {
          return res;
        }
        return (a.getKey().compareTo(b.getKey()));
      };

  /** the manager */
  private _NumberSystem m_numberSystem;

  /** the id counters */
  @SuppressWarnings("rawtypes")
  private final HashMap m_map;

  /**
   * create the id manager
   *
   * @param manager
   *          the id manager
   */
  _IDTranslator(final _NumberSystem manager) {
    super();
    this.m_numberSystem = manager;
    this.m_map = new HashMap<>();
  }

  /**
   * tick a given ID
   *
   * @param id
   *          the id
   */
  @SuppressWarnings("unchecked")
  final void _tickID(final String id) {
    AtomicLong l = ((AtomicLong) (this.m_map.get(id)));
    if (l == null) {
      l = new AtomicLong(1L);
      this.m_map.put(id, l);
    } else {
      l.incrementAndGet();
    }
  }

  /** transform the collected IDs */
  @SuppressWarnings("unchecked")
  final void _translate() {
    final Map.Entry<String, AtomicLong>[] list = //
        ((Map.Entry<String, AtomicLong>[]) (this.m_map//
            .entrySet()
            .toArray(new Map.Entry[this.m_map.size()])));
    Arrays.sort(list, _IDTranslator.CMP);
    this.m_map.clear();
    int i = 0;
    for (final Map.Entry<String, AtomicLong> e : list) {
      this.m_map.put(e.getKey(),
          this.m_numberSystem._getID(i++));
    }
    this.m_numberSystem = null;
  }

  /**
   * translate the given ID
   *
   * @param id
   *          the id
   * @return the translated id
   */
  final String _translateID(final String id) {
    final String s = ((String) (this.m_map.get(id)));
    if (s == null) {
      throw new IllegalStateException("id '" //$NON-NLS-1$
          + id + "' unknown");//$NON-NLS-1$
    }
    return s;
  }
}
