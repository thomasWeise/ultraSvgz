package thomasWeise.ultraSvgz;

import java.io.PrintStream;
import java.util.function.Supplier;

import thomasWeise.tools.Configuration;
import thomasWeise.tools.ConsoleIO;
import thomasWeise.ultraGzip.UltraGzip;

/** The ultra svgz tool. */
public final class UltraSvgz
    implements Supplier<UltraSvgzJobBuilder> {

  /** the intensity parameter */
  private static final String PARAM_INTENSITY = "svgIntensity"; //$NON-NLS-1$

  /** the UtralGzip Version */
  static final String VERSION = "0.8.0"; //$NON-NLS-1$

  /** create */
  UltraSvgz() {
    super();
  }

  /**
   * Get the intensity level
   *
   * @return the intensity level
   */
  static final int _getIntensity() {
    return __Intensity.INTENSITY;
  }

  /** {@inheritDoc} */
  @Override
  public final String toString() {
    return "UltraSvgz"; //$NON-NLS-1$
  }

  /**
   * print the command line arguments
   *
   * @param out
   *          the print stream to write to
   */
  public static final void printArgs(final PrintStream out) {
    out.println(' ' + UltraSvgz.PARAM_INTENSITY
        + "=0(min)..10(max), default 5: intensity - the higher the slower");//$NON-NLS-1$
    UltraGzip.printArgs(out);
  }

  /**
   * print the licensing message
   *
   * @param out
   *          the print stream to write to
   */
  public static final void printLicense(final PrintStream out) {
    out.print("UltraSvgz "); //$NON-NLS-1$
    out.print(UltraSvgz.VERSION);
    out.println(
        " is under the GPL 3 license and published at http://github.com/thomasWeise/ultraSvgz.");//$NON-NLS-1$

    out.println(
        "Our software includes the code of scour (http://github.com/scour-project/scour), which is under the Apache License Version 2.0."); //$NON-NLS-1$

    out.println(
        "Our software includes the linux binary version of svgcleaner (http://github.com/RazrFalcon/svgcleaner), which is under the GPL 2."); //$NON-NLS-1$

    out.print("UltraSvgz "); //$NON-NLS-1$
    out.print(UltraSvgz.VERSION);
    out.println(
        " relies on UltraGzip to perform the gzip compression.");
    UltraGzip.printLicense(out);
  }

  /** {@inheritDoc} */
  @Override
  public final UltraSvgzJobBuilder get() {
    return new UltraSvgzJobBuilder();
  }

  /**
   * Get the globally shared instance of the Ultra Svgz tool
   *
   * @return the globally shared instance of the Ultra Svgz tool
   */
  public static final UltraSvgz getInstance() {
    return __UltraSvgzHolder.INSTANCE;
  }

  /** the ultra svgz tool */
  private static final class __UltraSvgzHolder {
    /** the globally shared instance */
    static final UltraSvgz INSTANCE = new UltraSvgz();
  }

  /** the intensity holder class */
  private static final class __Intensity {

    /** the intensity */
    static final int INTENSITY;

    static {
      final int[] d = new int[] { 5 };
      Configuration.synchronizedConfig(() -> {
        final Integer inten =
            Configuration.getInteger(UltraSvgz.PARAM_INTENSITY);
        if (inten != null) {
          d[0] = Math.max(0, Math.min(10, inten.intValue()));
        }
        Configuration.putInteger(UltraSvgz.PARAM_INTENSITY,
            d[0]);
      });
      INTENSITY = d[0];

      ConsoleIO.stdout(
          ("UltraSvgz " + UltraSvgz.VERSION + ':') + ' ' + //$NON-NLS-1$
              UltraSvgz.PARAM_INTENSITY + " (0:min...10:max) is " //$NON-NLS-1$
              + __Intensity.INTENSITY);
    }
  }
}
