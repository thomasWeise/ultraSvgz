package thomasWeise.ultraSvgz;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import thomasWeise.tools.Configuration;
import thomasWeise.tools.ConsoleIO;
import thomasWeise.tools.EProcessStream;
import thomasWeise.tools.ExternalProcessExecutor;
import thomasWeise.tools.TempDir;

/** use the scour tooling support */
final class _Scour {

  /** the GZIP executable */
  private static final Path __PYTHON_PATH;

  static {
    Path p = Configuration.getExecutable("python3"); //$NON-NLS-1$
    if (p == null) {
      p = Configuration.getExecutable("python"); //$NON-NLS-1$
      if (p == null) {
        p = Configuration.getExecutable("python2"); //$NON-NLS-1$
      }
    }

    __PYTHON_PATH = p;
  }

  /** the resources */
  private static final String[] __RES = { //
      "scour/scour.py", //$NON-NLS-1$
      "scour/__init__.py", //$NON-NLS-1$
      "scour/svg_regex.py", //$NON-NLS-1$
      "scour/svg_transform.py", //$NON-NLS-1$
      "scour/yocto_css.py", //$NON-NLS-1$
  };

  /** can we use scour? */
  static final boolean _CAN_USE = (_Scour.__PYTHON_PATH != null);

  /**
   * apply the scour transformation
   *
   * @param in
   *          the input
   * @return the output
   */
  static byte[] _apply(final byte[] in) {
    try (final TempDir temp = new TempDir()) {
      final Path dir = temp.getPath();

      // copy binaries
      Files.createDirectories(dir.resolve("scour"));//$NON-NLS-1$
      for (final String s : _Scour.__RES) {
        final Path dest = dir.resolve(s).normalize();
        try (final InputStream is =
            _Scour.class.getResourceAsStream(s)) {
          Files.copy(is, dest);
        }
      }
      final String scourMain = "scourMain.py";//$NON-NLS-1$
      final Path main = Files.createTempFile(dir, null, //
          ".py").normalize();//$NON-NLS-1$
      try (final InputStream is =
          _Scour.class.getResourceAsStream(scourMain)) {
        Files.copy(is, main,
            StandardCopyOption.REPLACE_EXISTING);
      }

      final Path input =
          Files.createTempFile(dir, null, ".svg").normalize();//$NON-NLS-1$
      try (final OutputStream bos =
          Files.newOutputStream(input)) {
        bos.write(in);
      }

      final Path output =
          Files.createTempFile(dir, null, ".svg").normalize();//$NON-NLS-1$
      Files.delete(output);

      final int res = ExternalProcessExecutor.getInstance().get()//
          .setExecutable(_Scour.__PYTHON_PATH)//
          .setDirectory(dir)//
          .addPathArgument(main)//
          .addStringArgument("-i")//$NON-NLS-1$
          .addPathArgument(input)//
          .addStringArgument("-o")//$NON-NLS-1$
          .addPathArgument(output)//
          .addStringArgument("--enable-viewboxing")//$NON-NLS-1$
          .addStringArgument("--enable-id-stripping")//$NON-NLS-1$
          .addStringArgument("--enable-comment-stripping")//$NON-NLS-1$
          .addStringArgument("--shorten-ids")//$NON-NLS-1$
          .addStringArgument("--indent=none")//$NON-NLS-1$
          .setStdErr(EProcessStream.INHERIT)
          .setStdOut(EProcessStream.IGNORE)
          .setStdIn(EProcessStream.IGNORE).get().waitFor();
      if (res == 0) {
        final byte[] data = Files.readAllBytes(output);
        if (data.length > 0) {
          ConsoleIO.stdout(
              "scour completed successful, produced svg of "//$NON-NLS-1$
                  + data.length + " bytes.");//$NON-NLS-1$
          return data;
        } else {
          ConsoleIO.stderr("scour created empty file.", //$NON-NLS-1$
              null);
        }
      } else {
        ConsoleIO.stderr("could not complete scour, exit code "//$NON-NLS-1$
            + res, null);
      }
    } catch (final Throwable error) {
      ConsoleIO.stderr("error when applying scour", error); //$NON-NLS-1$
    }

    return null;
  }
}
