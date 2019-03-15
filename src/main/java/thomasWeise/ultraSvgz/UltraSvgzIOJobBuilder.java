package thomasWeise.ultraSvgz;

import java.nio.file.Path;
import java.util.function.Supplier;

import thomasWeise.tools.Configuration;

/** Build a job for the ultrag svgz I/O tool. */
public final class UltraSvgzIOJobBuilder
    implements Supplier<UltraSvgzIOJob> {

  /** use the standard in */
  static final String PARAM_STDIN = "si"; //$NON-NLS-1$

  /** the input path */
  static final String PARAM_IN = "in";//$NON-NLS-1$

  /** use the standard out */
  static final String PARAM_STDOUT = "so"; //$NON-NLS-1$

  /** the output path */
  static final String PARAM_OUT = "out";//$NON-NLS-1$

  /** the input path */
  Path m_input;

  /** should we use stdin instead of an input file? */
  boolean m_useStdIn;

  /** the output path */
  Path m_output;

  /** should we use stdout instead of an output file? */
  boolean m_useStdOut;

  /** create */
  UltraSvgzIOJobBuilder() {
    super();
  }

  /**
   * configure the job from a configuration
   *
   * @return this instance
   */
  public final UltraSvgzIOJobBuilder configure() {
    Configuration.synchronizedConfig(() -> {
      if (Configuration
          .getBoolean(UltraSvgzIOJobBuilder.PARAM_STDIN)) {
        this.setUseStdIn(true);
      }

      Path path =
          Configuration.getPath(UltraSvgzIOJobBuilder.PARAM_IN);
      if (path != null) {
        this.setInputPath(path);
      }

      if (Configuration
          .getBoolean(UltraSvgzIOJobBuilder.PARAM_STDOUT)) {
        this.setUseStdOut(true);
      }
      path =
          Configuration.getPath(UltraSvgzIOJobBuilder.PARAM_OUT);
      if (path != null) {
        this.setOutputPath(path);
      }
    });

    return this;
  }

  /**
   * Set the input path
   *
   * @param path
   *          the path
   * @return this builder
   */
  public final UltraSvgzIOJobBuilder
      setInputPath(final Path path) {
    final Path use;
    if (path == null) {
      throw new IllegalArgumentException(
          "Input path cannot be null"); //$NON-NLS-1$
    }
    use = path.normalize();
    if (use == null) {
      throw new IllegalArgumentException(//
          "Input path cannot normalize to null, but '" + //$NON-NLS-1$
              path + "' does."); //$NON-NLS-1$
    }
    this.m_input = use;
    this.m_useStdIn = false;
    return this;
  }

  /**
   * Set whether data should be loaded from stdin instead from a
   * file
   *
   * @param useStdIn
   *          the standard input choice
   * @return this builder
   */
  public final UltraSvgzIOJobBuilder
      setUseStdIn(final boolean useStdIn) {
    if (useStdIn) {
      this.m_input = null;
    }
    this.m_useStdIn = useStdIn;
    return this;
  }

  /**
   * Set the ouput path
   *
   * @param path
   *          the path
   * @return this builder
   */
  public final UltraSvgzIOJobBuilder
      setOutputPath(final Path path) {
    final Path use;
    if (path == null) {
      throw new IllegalArgumentException(
          "Output path cannot be null"); //$NON-NLS-1$
    }
    use = path.normalize();
    if (use == null) {
      throw new IllegalArgumentException(//
          "Output path cannot normalize to null, but '" + //$NON-NLS-1$
              path + "' does."); //$NON-NLS-1$
    }
    this.m_output = use;
    this.m_useStdOut = false;
    return this;
  }

  /**
   * Set whether data should be loaded from stdout instead of a
   * file
   *
   * @param useStdOut
   *          the standard input choice
   * @return this builder
   */
  public final UltraSvgzIOJobBuilder
      setUseStdOut(final boolean useStdOut) {
    if (useStdOut) {
      this.m_output = null;
    }
    this.m_useStdOut = useStdOut;
    return this;
  }

  /**
   * Validate
   *
   * @param input
   *          the input path
   * @param stdin
   *          use stdin instead?
   * @param output
   *          the output path
   * @param stdout
   *          use stdout instead?
   */
  static final void _validate(final Path input,
      final boolean stdin, final Path output,
      final boolean stdout) {
    if (stdin) {
      if (input != null) {
        throw new IllegalArgumentException(
            "Cannot use both, stdin and path '" + input + '\'' //$NON-NLS-1$
                + '.');
      }
    } else {
      if (input == null) {
        throw new IllegalArgumentException(
            "Must either specify input file or stdin."); //$NON-NLS-1$
      }
    }
    if (stdout) {
      if (output != null) {
        throw new IllegalArgumentException(
            "Cannot use both, stdout and path '" + output + '\'' //$NON-NLS-1$
                + '.');
      }
    } else {
      if (output == null) {
        throw new IllegalArgumentException(
            "Must either specify output file or stdout."); //$NON-NLS-1$
      }
    }
  }

  /** {@inheritDoc} */
  @Override
  public final UltraSvgzIOJob get() {
    if (!this.m_useStdOut) {
      if (this.m_output == null) {
        final Path p = this.m_input;
        if (p != null) {
          final Path dir = p.getParent();
          String name = p.getFileName().toString();
          final int dot = name.indexOf('.');
          if (dot > 0) {
            name = name.substring(0, dot);
          }
          this.m_output = dir.resolve(name + ".svgz") //$NON-NLS-1$
              .normalize();
        }
      }
    }

    return new UltraSvgzIOJob(this);
  }
}
