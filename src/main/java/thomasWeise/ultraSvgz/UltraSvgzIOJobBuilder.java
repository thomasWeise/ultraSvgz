package thomasWeise.ultraSvgz;

import java.nio.file.Path;

import thomasWeise.tools.Configuration;
import thomasWeise.tools.IOJobBuilder;

/** Build a job for the ultrag svgz I/O tool. */
public final class UltraSvgzIOJobBuilder extends IOJobBuilder {

  /** don't cleanse white space */
  public static final String PARAM_DONT_CLEANSE_WHITESPACE =
      "dontCleanseWhiteSpace";

  /** should white space be removed? */
  private boolean m_cleanseWhiteSpace;

  /** create */
  UltraSvgzIOJobBuilder() {
    super();
    this.m_cleanseWhiteSpace = true;
  }

  /** {@inheritDoc} */
  @Override
  public UltraSvgzIOJob get() {
    if (!this.isUsingStdOut()) {
      if (this.getOutputPath() == null) {
        final Path p = this.getInputPath();
        if (p != null) {
          final Path dir = p.getParent();
          String name = p.getFileName().toString();
          final int dot = name.indexOf('.');
          if (dot > 0) {
            name = name.substring(0, dot);
          }
          this.setOutputPath(dir.resolve(name + ".svgz")); //$NON-NLS-1$
        }
      }
    }
    return new UltraSvgzIOJob(this);
  }

  /**
   * set whether we should cleanse white space?
   *
   * @param cleanseWhiteSpace
   *          should we cleanse white space?
   * @return this
   */
  public UltraSvgzIOJobBuilder
      setCleanseWhiteSpace(final boolean cleanseWhiteSpace) {
    this.m_cleanseWhiteSpace = cleanseWhiteSpace;
    return this;
  }

  /**
   * should we cleanse white space?
   *
   * @return should we cleanse white space?
   */
  public boolean getCleanseWhiteSpace() {
    return this.m_cleanseWhiteSpace;
  }

  /**
   * configure the job from a configuration
   *
   * @return this instance
   */
  @Override
  public UltraSvgzIOJobBuilder configure() {
    super.configure();
    if (Configuration.getBoolean(
        UltraSvgzIOJobBuilder.PARAM_DONT_CLEANSE_WHITESPACE)) {
      this.setCleanseWhiteSpace(false);
    }
    return this;
  }
}
