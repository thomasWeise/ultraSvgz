package thomasWeise.ultraSvgz;

import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import thomasWeise.tools.Configuration;
import thomasWeise.tools.ConsoleIO;
import thomasWeise.tools.EProcessStream;
import thomasWeise.tools.ExternalProcessExecutor;
import thomasWeise.tools.TempDir;

/** use the scour tooling support */
final class _RSVG {

  /** the rsvg-convert path */
  static final Path _RSVG_CONVERT_PATH =
      Configuration.getExecutable("rsvg-convert"); //$NON-NLS-1$

  /** can we use this tool? */
  static final boolean _CAN_USE =
      (_RSVG._RSVG_CONVERT_PATH != null);

  /**
   * apply the rsvg-convert
   *
   * @param in
   *          the input
   * @return the output
   */
  static final byte[] _apply(final byte[] in) {
    if (in == null) {
      return null;
    }
    if (_RSVG._RSVG_CONVERT_PATH != null) {
      try (final TempDir temp = new TempDir()) {
        final Path dir = temp.getPath();

        final Path input =
            Files.createTempFile(dir, null, ".svg").normalize();//$NON-NLS-1$
        try (final OutputStream bos =
            Files.newOutputStream(input)) {
          bos.write(in);
        }

        final Path output =
            Files.createTempFile(dir, null, ".svg").normalize();//$NON-NLS-1$

        final int res =
            ExternalProcessExecutor.getInstance().get()//
                .setExecutable(_RSVG._RSVG_CONVERT_PATH)//
                .setDirectory(dir)//
                .addPathArgument(input)//
                .addStringArgument("-f")//$NON-NLS-1$
                .addStringArgument("svg")//$NON-NLS-1$
                .addStringArgument("-o")//$NON-NLS-1$
                .addPathArgument(output)
                .setStdErr(EProcessStream.INHERIT)
                .setStdOut(EProcessStream.IGNORE)
                .setStdIn(EProcessStream.IGNORE).get().waitFor();
        if (res == 0) {
          final byte[] data = Files.readAllBytes(output);
          if (data.length > 0) {
            ConsoleIO.stdout(
                "rsvg completed successful, produced svg of "//$NON-NLS-1$
                    + data.length + " bytes.");//$NON-NLS-1$
            return data;
          } else {
            ConsoleIO.stderr("rsvg created empty file.", //$NON-NLS-1$
                null);
          }
        } else {
          ConsoleIO.stderr("could not complete rsvg, exit code "//$NON-NLS-1$
              + res, null);
        }
      } catch (final Throwable error) {
        ConsoleIO.stderr("error when applying rsvg-convert"//$NON-NLS-1$
            , error);
      }
    }

    return null;
  }
}
