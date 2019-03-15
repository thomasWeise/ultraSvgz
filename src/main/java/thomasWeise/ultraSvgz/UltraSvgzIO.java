package thomasWeise.ultraSvgz;

import java.util.function.Supplier;

/** A tool which provdes UltraSvgz */
public final class UltraSvgzIO
    implements Supplier<UltraSvgzIOJobBuilder> {

  /** create */
  UltraSvgzIO() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public final UltraSvgzIOJobBuilder get() {
    return new UltraSvgzIOJobBuilder();
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return "UltraSvgzIO"; //$NON-NLS-1$
  }

  /**
   * Get the globally shared instance of the Ultra Svgz tool
   *
   * @return the globally shared instance of the Ultra Svgz tool
   */
  public static final UltraSvgzIO getInstance() {
    return __UltraSvgzHolder.INSTANCE;
  }

  /** the ultra svgz tool */
  private static final class __UltraSvgzHolder {
    /** the globally shared instance */
    static final UltraSvgzIO INSTANCE = new UltraSvgzIO();
  }
}
