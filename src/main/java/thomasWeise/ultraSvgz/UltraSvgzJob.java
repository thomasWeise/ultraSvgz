package thomasWeise.ultraSvgz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.function.Function;

import org.w3c.dom.Document;

import thomasWeise.tools.ConsoleIO;
import thomasWeise.tools.Execute;
import thomasWeise.ultraGzip.UltraGzip;

/** The ultra svgz job. */
public final class UltraSvgzJob implements Callable<byte[]> {

  /** the smallest gzipped doc */
  private static final int DOC_SMALLEST_GZIPPED = 0;
  /** the document with the fewest bytes */
  private static final int DOC_FEWEST_BYTES = 1;

  /** the smallest known gzipped raw character data */
  private static final int RAW_SMALLEST_GZIPPED = 0;
  /** the raw character data with the fewest bytes */
  private static final int RAW_FEWEST_BYTES = 1;

  /** the best svgz value has been improved */
  private static final int IMPROVED_BEST_SVGZ_BYTES = 1;
  /** the best svg value has been improved */
  private static final int IMPROVED_BEST_SVG_BYTES = 2;
  /** the best svgz document value has been improved */
  private static final int IMPROVED_BEST_SVGZ_DOC = 4;
  /** the best svg document value has been improved */
  private static final int IMPROVED_BEST_SVG_DOC = 8;

  /** the data */
  private final byte[][] m_data;

  /** the data names */
  private final String[] m_dataNames;

  /** the best compression */
  private volatile byte[] m_best;

  /** the smallest known document */
  private final Document[] m_doc;

  /** the doc names */
  private final String[] m_docNames;

  /** the document size */
  private final int[] m_docSize;

  /** the internal synchronizer */
  private final Object m_synch;

  /** did we improve */
  private volatile int m_improved;

  /** the name */
  private final String m_name;

  /** the cache */
  private final LinkedHashMap<__Hash, Boolean> m_cache;

  /** the maximum cache size */
  final int m_maxCacheSize;

  /**
   * create the ultra svgz job
   *
   * @param data
   *          the data to compress
   * @param name
   *          the name of the data
   */
  UltraSvgzJob(final byte[] data, final String name) {
    super();

    UltraSvgzJobBuilder._checkData(data);
    this.m_data = new byte[][] { data, data };
    this.m_name = UltraSvgzJobBuilder._checkName(name);
    this.m_dataNames = new String[] { this.m_name, this.m_name };
    this.m_synch = new Object();
    this.m_doc = new Document[2];
    this.m_docNames = new String[2];
    this.m_docSize = new int[2];

    this.m_maxCacheSize = Math.max(4,
        (int) (Math.min(1_000_000,
            Runtime.getRuntime().freeMemory()
                / (data.length << 3))));

    this.m_cache =
        new LinkedHashMap<__Hash, Boolean>(this.m_maxCacheSize) {
          /** ignore */
          private static final long serialVersionUID = 1L;

          /** {@inheritDoc} */
          @Override
          protected final boolean removeEldestEntry(
              final Map.Entry<__Hash, Boolean> eldest) {
            return (this
                .size() > UltraSvgzJob.this.m_maxCacheSize);
          }
        };
  }

  /**
   * Register a transformed DOM node
   *
   * @param name
   *          the name of the registration
   * @param result
   *          the dom node
   */
  private final void __register(final Document result,
      final String name) {
    if (result == null) {
      return;
    }
    final byte[] res = _Tools._toBytes(result);
    if (res != null) {
      this.__register(res, name, result, false);
    }
  }

  /**
   * register a given data array
   *
   * @param data
   *          the data
   * @param name
   *          the name
   * @param doc
   *          the document to register
   * @param passThrough
   *          should we pass through?
   */
  private final void __register(final byte[] data,
      final String name, final Document doc,
      final boolean passThrough) {
    if (data == null) {
      return;
    }

    final byte[] alt = _CleanseWhitespace._apply(data);
    if ((alt != null) && (alt.length < data.length)) {
      if (this.__register2(alt, "noWS:" + name, doc, //$NON-NLS-1$
          false)) {
        if ((alt.length + 16) <= data.length) {
          // If the alternative version is more than 16 bytes
          // shorter than data, then the compressed version of
          // alt will probably smaller than data abd we don't
          // test compressing data. Otherwise, both versions are
          // similar in size, so they may indeed compress to
          // different sizes.
          return;
        }
      }
    }

    this.__register2(data, name, doc, passThrough);
  }

  /**
   * register a given data array
   *
   * @param data
   *          the data
   * @param name
   *          the name
   * @param doc
   *          the document to register
   * @param passThrough
   *          should we pass-through?
   * @return {@code true} if everything is ok
   */
  private final boolean __register2(final byte[] data,
      final String name, final Document doc,
      final boolean passThrough) {
    if ((data == null) || (data.length <= 2)) {
      return false;
    }

    final __Hash h = new __Hash(data);

    if (!passThrough) {
      boolean known = false;
      synchronized (this.m_synch) {
        final Boolean b = this.m_cache.get(h);
        if (b != null) {
          known = false;
        } else {
          this.m_cache.put(h, Boolean.TRUE);
        }
      }

      if (known) {
        ConsoleIO.stdout("result of job "//$NON-NLS-1$
            + name + " is identical to result of an old job.");//$NON-NLS-1$
        return true;
      }
    }

    final byte[] res = UltraGzip.getInstance().get()
        .setData(data).setName("gzip:" //$NON-NLS-1$
            + name)
        .get().call();
    if ((res == null) || (res.length <= 2)) {
      return false;
    }
    if (!_Checker._check(res, name)) {
      return false;
    }

    int result = 0;

    synchronized (this.m_synch) {
      if (passThrough) {
        this.m_cache.put(h, Boolean.TRUE);
      }

      if ((this.m_best == null)
          || ((res.length < this.m_best.length)
              && !(Arrays.equals(res, this.m_best)))) {
        result = UltraSvgzJob.IMPROVED_BEST_SVGZ_BYTES;
        this.m_best = res;
        this.m_data[UltraSvgzJob.RAW_SMALLEST_GZIPPED] = data;
        this.m_dataNames[UltraSvgzJob.RAW_SMALLEST_GZIPPED] =
            name;
      }

      if (doc != null) {
        if ((this.m_doc[UltraSvgzJob.DOC_FEWEST_BYTES] == null)
            || ((this.m_docSize[UltraSvgzJob.DOC_FEWEST_BYTES] > data.length)
                && (!(Objects.equals(
                    this.m_doc[UltraSvgzJob.DOC_FEWEST_BYTES],
                    doc))))) {
          result |= UltraSvgzJob.IMPROVED_BEST_SVG_DOC;
          this.m_doc[UltraSvgzJob.DOC_FEWEST_BYTES] = doc;
          this.m_docNames[UltraSvgzJob.DOC_FEWEST_BYTES] = name;
          this.m_docSize[UltraSvgzJob.DOC_FEWEST_BYTES] =
              data.length;
        }
        if ((this.m_doc[UltraSvgzJob.DOC_SMALLEST_GZIPPED] == null)
            || ((this.m_docSize[UltraSvgzJob.DOC_SMALLEST_GZIPPED] > res.length)
                && (!(Objects.equals(
                    this.m_doc[UltraSvgzJob.DOC_SMALLEST_GZIPPED],
                    doc))))) {
          result |= UltraSvgzJob.IMPROVED_BEST_SVGZ_DOC;
          this.m_doc[UltraSvgzJob.DOC_SMALLEST_GZIPPED] = doc;
          this.m_docNames[UltraSvgzJob.DOC_SMALLEST_GZIPPED] =
              name;
          this.m_docSize[UltraSvgzJob.DOC_SMALLEST_GZIPPED] =
              res.length;
        }
      }

      if ((this.m_data[UltraSvgzJob.RAW_FEWEST_BYTES].length > data.length)
          && (!(Arrays.equals(
              this.m_data[UltraSvgzJob.RAW_FEWEST_BYTES],
              data)))) {
        result |= UltraSvgzJob.IMPROVED_BEST_SVG_BYTES;
        this.m_data[UltraSvgzJob.RAW_FEWEST_BYTES] = data;
        this.m_dataNames[UltraSvgzJob.RAW_FEWEST_BYTES] = name;
      }

      this.m_improved |= result;
    }

    if (result != 0) {
      final StringBuilder sb = new StringBuilder();
      sb.append("Job '");//$NON-NLS-1$
      sb.append(name);
      sb.append("' created SVG of ");//$NON-NLS-1$
      sb.append(data.length);
      sb.append("B corresponding to a ");//$NON-NLS-1$
      sb.append(res.length);
      sb.append("B SVGZ. This reduces"); //$NON-NLS-1$

      final int length = sb.length();
      if ((result
          & UltraSvgzJob.IMPROVED_BEST_SVGZ_BYTES) != 0) {
        sb.append(" the overall smallest SVGZ size"); //$NON-NLS-1$
      }
      if ((result & UltraSvgzJob.IMPROVED_BEST_SVG_BYTES) != 0) {
        if (sb.length() > length) {
          sb.append(" and"); //$NON-NLS-1$
        }
        sb.append(" the SVG size"); //$NON-NLS-1$
      }
      if ((result & UltraSvgzJob.IMPROVED_BEST_SVGZ_DOC) != 0) {
        if (sb.length() > length) {
          sb.append(" and"); //$NON-NLS-1$
        }
        sb.append(" the DOM-based SVGZ size"); //$NON-NLS-1$
      }
      if ((result & UltraSvgzJob.IMPROVED_BEST_SVG_DOC) != 0) {
        if (sb.length() > length) {
          sb.append(" and"); //$NON-NLS-1$
        }
        sb.append(" the DOM-based SVG size"); //$NON-NLS-1$
      }
      ConsoleIO.stdout(sb.append('.').toString());
    }

    return true;
  }

  /**
   * apply the given transformation to the specified document
   *
   * @param doc
   *          the document
   * @param transformer
   *          the transformer
   * @param name
   *          the name
   * @return the result
   */
  private final Future<Void> __transform(final Document doc,
      final Function<Document, Document> transformer,
      final String name) {
    return Execute.parallel(() -> {
      final Document res = transformer.apply(doc);
      if ((res != null) && (res != doc)
          && (!(Objects.equals(res, doc)))) {
        this.__register(res, name);
      }
    });
  }

  /**
   * apply the given transformation to the specified document
   *
   * @param raw
   *          the raw data
   * @param transformer
   *          the transformer
   * @param name
   *          the name
   * @param passThrough
   *          should we pass through?
   * @return the result
   */
  private final Future<Void> __transformBB(final byte[] raw,
      final Function<byte[], byte[]> transformer,
      final String name, final boolean passThrough) {
    return Execute.parallel(() -> {
      final byte[] result = transformer.apply(raw);
      if ((result != null) && (passThrough || ((result != raw)
          && (!(Arrays.equals(result, raw)))))) {
        this.__register(result, name, null, passThrough);
      }
    });
  }

  /**
   * apply the given transformation to the specified document
   *
   * @param raw
   *          the raw data
   * @param transformer
   *          the transformer
   * @param name
   *          the name
   * @return the result
   */
  private final Future<Void> __transformBD(final byte[] raw,
      final Function<byte[], Document> transformer,
      final String name) {
    return Execute.parallel(() -> {
      final Document result = transformer.apply(raw);
      if (result != null) {
        final byte[] data = _Tools._toBytes(result);
        if (data != null) {
          this.__register(data, name, result, false);
        }
      }
    });
  }

  /**
   * apply a transformation to the dom trees
   *
   * @param transformer
   *          the transformer
   * @param name
   *          the name
   */
  private final void __applyDOM(
      final Function<Document, Document> transformer,
      final String name) {
    final Document a;
    final Document b;
    final String nameA;
    final String nameB;

    synchronized (this.m_synch) {
      a = this.m_doc[UltraSvgzJob.DOC_FEWEST_BYTES];
      b = this.m_doc[UltraSvgzJob.DOC_SMALLEST_GZIPPED];
      nameA = this.m_docNames[UltraSvgzJob.DOC_FEWEST_BYTES];
      nameB = this.m_docNames[UltraSvgzJob.DOC_SMALLEST_GZIPPED];
    }

    final Future<Void> fa;
    final Future<Void> fb;

    if (a != null) {
      fa = this.__transform(a, transformer, name + ':' + nameA);
    } else {
      fa = null;
    }

    if ((b != null) && (b != a)) {
      fb = this.__transform(b, transformer, name + ':' + nameB);
    } else {
      fb = null;
    }

    if ((fa != null) && (fb != null)) {
      Execute.join(fa, fb);
    } else {
      UltraSvgzJob.__join1(fa);
      UltraSvgzJob.__join1(fb);
    }
  }

  /**
   * apply a transformation to the data chars
   *
   * @param transformer
   *          the transformer
   * @param name
   *          the name
   * @param passThrough
   *          should we pass-through?
   */
  private final void __applyBB(
      final Function<byte[], byte[]> transformer,
      final String name, final boolean passThrough) {
    final byte[] a;
    final byte[] b;
    final String an;
    final String bn;

    synchronized (this.m_synch) {
      a = this.m_data[UltraSvgzJob.RAW_FEWEST_BYTES];
      b = this.m_data[UltraSvgzJob.RAW_SMALLEST_GZIPPED];
      an = this.m_dataNames[UltraSvgzJob.RAW_FEWEST_BYTES];
      bn = this.m_dataNames[UltraSvgzJob.RAW_SMALLEST_GZIPPED];
    }

    final Future<Void> fa;
    final Future<Void> fb;
    boolean pt = passThrough;

    if (a != null) {
      fa = this.__transformBB(a, transformer, name + ':' + an,
          pt);
      pt = false;
    } else {
      fa = null;
    }

    if ((b != null) && (b != a)) {
      fb = this.__transformBB(b, transformer, name + ':' + bn,
          pt);
    } else {
      fb = null;
    }

    if ((fa != null) && (fb != null)) {
      Execute.join(fa, fb);
    } else {
      UltraSvgzJob.__join1(fa);
      UltraSvgzJob.__join1(fb);
    }
  }

  /**
   * apply a transformation to the data chars
   *
   * @param transformer
   *          the transformer
   * @param name
   *          the name
   */
  private final void __applyBD(
      final Function<byte[], Document> transformer,
      final String name) {
    final byte[] a;
    final byte[] b;
    final String an;
    final String bn;

    synchronized (this.m_synch) {
      a = this.m_data[UltraSvgzJob.RAW_FEWEST_BYTES];
      b = this.m_data[UltraSvgzJob.RAW_SMALLEST_GZIPPED];
      an = this.m_dataNames[UltraSvgzJob.RAW_FEWEST_BYTES];
      bn = this.m_dataNames[UltraSvgzJob.RAW_SMALLEST_GZIPPED];
    }

    final Future<Void> fa;
    final Future<Void> fb;

    if (a != null) {
      fa = this.__transformBD(a, transformer, name + ':' + an);
    } else {
      fa = null;
    }

    if ((b != null) && (b != a)) {
      fb = this.__transformBD(b, transformer, name + ':' + bn);
    } else {
      fb = null;
    }

    if ((fa != null) && (fb != null)) {
      Execute.join(fa, fb);
    } else {
      UltraSvgzJob.__join1(fa);
      UltraSvgzJob.__join1(fb);
    }
  }

  /**
   * join a single future
   *
   * @param f
   *          the future
   */
  private static final void __join1(final Future<Void> f) {
    if (f != null) {
      try {
        f.get();
      } catch (final Throwable error) {
        ConsoleIO.stderr("error when waiting for parallel jobs", //$NON-NLS-1$
            error);
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public final byte[] call() {

// initiate dom-based compression
    Execute.join(Execute.parallel(
        () -> this.__applyBB(Objects::requireNonNull, "raw", //$NON-NLS-1$
            true)));

    final int maxIt;
    if (UltraSvgz._getIntensity() <= 5) {
      maxIt = 1 + UltraSvgz._getIntensity();
    } else {
      if (UltraSvgz._getIntensity() < 10) {
        maxIt = (1 << (UltraSvgz._getIntensity() - 3));
      } else {
        maxIt = Integer.MAX_VALUE;
      }
    }

    final ArrayList<Future<Void>> jobs = new ArrayList<>();
    int i = 1;
    mainLoop: for (;;) {
      ConsoleIO.stdout("beginning iteration " //$NON-NLS-1$
          + i + " of " + this.m_name); //$NON-NLS-1$

      synchronized (this.m_synch) {
        this.m_improved = 0;
      }

      if (_Scour._CAN_USE) {
        jobs.add(Execute.parallel(
            () -> this.__applyBB(_Scour::_apply, "scour", //$NON-NLS-1$
                false)));
      }

      jobs.add(Execute.parallel(() -> //
      this.__applyBD(_Tools::_fromBytes, "domParsing"))); //$NON-NLS-1$
      jobs.add(Execute.parallel(() -> //
      this.__applyDOM(_FixIDs::_apply, "fixIDs"))); //$NON-NLS-1$
      jobs.add(Execute.parallel(() -> //
      this.__applyDOM(_Canonicalize::_apply, "canon"))); //$NON-NLS-1$
      jobs.add(Execute.parallel(() -> //
      this.__applyBB(_SVGCleaner::_apply, "svgcleaner", //$NON-NLS-1$
          false)));
      jobs.add(Execute.parallel(() -> //
      this.__applyBB(_SVGMinify::_apply, "svgminify", //$NON-NLS-1$
          false)));

      Execute.join(jobs);
      jobs.clear();

      int improved;
      synchronized (this.m_synch) {
        improved = this.m_improved;
        this.m_improved = 0;
      }

      if (improved == 0) {
        ConsoleIO.stdout("iteration " + i //$NON-NLS-1$
            + " of " + this.m_name + //$NON-NLS-1$
            " ended without improvement, so we can quit.");//$NON-NLS-1$
        break;
      }

      if (improved == 0) {
        ConsoleIO.stdout("iteration " + i //$NON-NLS-1$
            + " of " + this.m_name + //$NON-NLS-1$
            " ended without improvement, so we can quit.");//$NON-NLS-1$
        break mainLoop;
      }

      if (i >= maxIt) {
        ConsoleIO.stdout("iteration " + i //$NON-NLS-1$
            + " of " + this.m_name + //$NON-NLS-1$
            " ended with improvement, but we hit the iteraiton limit, so we quit.");//$NON-NLS-1$
        break mainLoop;
      }

      ConsoleIO.stdout("iteration " + i //$NON-NLS-1$
          + " of " + this.m_name + //$NON-NLS-1$
          " ended with improvement, so we do another iteration.");//$NON-NLS-1$
      ++i;
    }

    synchronized (this.m_synch) {
      return this.m_best;
    }
  }

  /** the hash wrapper */
  private static final class __Hash
      implements Comparable<__Hash> {
    /** the array */
    private final byte[] m_array;
    /** the hash code */
    private final int m_hc;

    /**
     * create the array
     *
     * @param array
     *          the array
     */
    __Hash(final byte[] array) {
      super();
      this.m_array = array;
      this.m_hc = Arrays.hashCode(array);
    }

    /** {@inheritDoc} */
    @Override
    public final boolean equals(final Object o) {
      if (o != this) {
        final __Hash h = ((__Hash) o);
        if (h.m_hc != this.m_hc) {
          return false;
        }
        return Arrays.equals(this.m_array, h.m_array);
      }
      return true;
    }

    /** {@inheritDoc} */
    @Override
    public final int hashCode() {
      return this.m_hc;
    }

    /** {@inheritDoc} */
    @Override
    public final int compareTo(final __Hash o) {
      int r = Integer.compare(this.m_hc, o.m_hc);
      if (r != 0) {
        return r;
      }
      final byte[] a = this.m_array;
      final byte[] b = o.m_array;
      final int la = a.length;
      final int lb = b.length;
      final int l = Math.min(la, lb);
      for (int i = 0; i < l; i++) {
        r = Byte.compare(a[i], b[i]);
        if (r != 0) {
          return r;
        }
      }
      return Integer.compare(la, lb);
    }
  }
}
