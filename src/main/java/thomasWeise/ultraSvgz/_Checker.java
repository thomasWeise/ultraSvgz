package thomasWeise.ultraSvgz;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import thomasWeise.tools.ConsoleIO;
import thomasWeise.tools.EProcessStream;
import thomasWeise.tools.ExternalProcessExecutor;
import thomasWeise.tools.TempDir;

/** use the scour tooling support */
final class _Checker {

  /** the rsvg-convert path */
  private static final Path __RSVG_CONVERT_PATH =
      _RSVG._RSVG_CONVERT_PATH;

  /**
   * check whether the given bytes can be converted
   *
   * @param name
   *          the producer name
   * @param in
   *          the input
   * @return the output
   */
  static boolean _check(final byte[] in, final String name) {
    if (in == null) {
      return false;
    }
    if (in.length <= 3) {
      ConsoleIO.stdout(name + " produced too small of a svgz!"); //$NON-NLS-1$
      return false;
    }
    if (_Checker.__RSVG_CONVERT_PATH != null) {
      try (final TempDir temp = new TempDir()) {
        final Path dir = temp.getPath();

        final Path input =
            Files.createTempFile(dir, null, ".svgz").normalize();//$NON-NLS-1$
        try (final OutputStream bos =
            Files.newOutputStream(input)) {
          bos.write(in);
        }

        final Path output =
            Files.createTempFile(dir, null, ".pdf").normalize();//$NON-NLS-1$

        final int res =
            ExternalProcessExecutor.getInstance().get()//
                .setExecutable(_Checker.__RSVG_CONVERT_PATH)//
                .setDirectory(dir)//
                .addPathArgument(input)//
                .addStringArgument("-f")//$NON-NLS-1$
                .addStringArgument("pdf")//$NON-NLS-1$
                .addStringArgument("-o")//$NON-NLS-1$
                .addPathArgument(output)
                .setStdErr(EProcessStream.INHERIT)
                .setStdOut(EProcessStream.IGNORE)
                .setStdIn(EProcessStream.IGNORE).get().waitFor();
        if (res == 0) {
          if (Files.size(output) >= 5) {
            ConsoleIO.stdout(name + " SVGZ (size " + in.length //$NON-NLS-1$
                + "B) verified successfully by rsvg-convert.");//$NON-NLS-1$
          } else {
            return false;
          }
        } else {
          ConsoleIO.stderr(name + //
              " could not complete rsvg-convert, exit code "//$NON-NLS-1$
              + res, null);
          return false;
        }
      } catch (final Throwable error) {
        ConsoleIO.stderr("error when applying rsvg-convert to"//$NON-NLS-1$
            + name, error);
        return false;
      }
    }

    return true;
  }
}
