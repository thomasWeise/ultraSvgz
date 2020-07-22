package thomasWeise.ultraSvgz;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import thomasWeise.tools.ConsoleIO;
import thomasWeise.tools.EProcessStream;
import thomasWeise.tools.ExternalProcessExecutor;
import thomasWeise.tools.TempDir;

/** use the svgcleaner tooling support */
final class _SVGCleaner {

  /** can we use this tool? */
  static final boolean _CAN_USE =
      System.getProperty("os.name").toLowerCase()//$NON-NLS-1$
          .contains("nux");//$NON-NLS-1$

  /** the resources to use */
  private static final String RES = "svgcleaner"; //$NON-NLS-1$

  /**
   * apply the svg cleaner transformation
   *
   * @param in
   *          the input
   * @return the output
   */
  static byte[] _apply(final byte[] in) {
    if (!_SVGCleaner._CAN_USE) {
      return null;
    }
    try (final TempDir temp = new TempDir()) {
      final Path dir = temp.getPath();

      final Path binary =
          Files.createTempFile(dir, null, null).normalize();
      try (final InputStream is = _SVGCleaner.class
          .getResourceAsStream(_SVGCleaner.RES)) {
        Files.copy(is, binary,
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

      Thread.sleep(100L);
      binary.toFile().setExecutable(true);
      Thread.sleep(100L);

      final int res = ExternalProcessExecutor.getInstance().get()//
          .setExecutable(binary)//
          .setDirectory(dir)//
          .addPathArgument(input)//
          .addPathArgument(output)//
          .addStringArgument("--remove-comments")//$NON-NLS-1$
          .addStringArgument("--remove-nonsvg-elements")//$NON-NLS-1$
          .addStringArgument("--remove-unused-defs")//$NON-NLS-1$
          .addStringArgument("--convert-shapes")//$NON-NLS-1$
          .addStringArgument("--remove-title")//$NON-NLS-1$
          .addStringArgument("--remove-desc")//$NON-NLS-1$
          .addStringArgument("--remove-metadata")//$NON-NLS-1$
          .addStringArgument("--remove-dupl-lineargradient")//$NON-NLS-1$
          .addStringArgument("--remove-dupl-radialgradient")//$NON-NLS-1$
          .addStringArgument("--remove-dupl-fegaussianblur")//$NON-NLS-1$
          .addStringArgument("--ungroup-groups")//$NON-NLS-1$
          .addStringArgument("--ungroup-defs")//$NON-NLS-1$
          .addStringArgument("--group-by-style")//$NON-NLS-1$
          .addStringArgument("--merge-gradients")//$NON-NLS-1$
          .addStringArgument("--regroup-gradient-stops")//$NON-NLS-1$
          .addStringArgument("--remove-invalid-stops")//$NON-NLS-1$
          .addStringArgument("--remove-invisible-elements")//$NON-NLS-1$
          .addStringArgument("--resolve-use")//$NON-NLS-1$
          .addStringArgument("--remove-nonsvg-attributes")//$NON-NLS-1$
          .addStringArgument("--remove-unreferenced-ids")//$NON-NLS-1$
          .addStringArgument("--trim-ids")//$NON-NLS-1$
          .addStringArgument("--remove-unused-coordinates")//$NON-NLS-1$
          .addStringArgument("--remove-default-attributes")//$NON-NLS-1$
          .addStringArgument("--remove-xmlns-xlink-attribute")//$NON-NLS-1$
          .addStringArgument("--remove-needless-attributes")//$NON-NLS-1$
          .addStringArgument("--remove-gradient-attributes")//$NON-NLS-1$
          .addStringArgument("--apply-transform-to-gradients")//$NON-NLS-1$
          .addStringArgument("--apply-transform-to-shapes")//$NON-NLS-1$
          .addStringArgument("--remove-unresolved-classes")//$NON-NLS-1$
          .addStringArgument("--paths-to-relative")//$NON-NLS-1$
          .addStringArgument("--remove-unused-segments")//$NON-NLS-1$
          .addStringArgument("--convert-segments")//$NON-NLS-1$
          .addStringArgument("--apply-transform-to-paths")//$NON-NLS-1$
          .addStringArgument("--trim-paths")//$NON-NLS-1$
          .addStringArgument("--join-arcto-flags")//$NON-NLS-1$
          .addStringArgument("--remove-dupl-cmd-in-paths")//$NON-NLS-1$
          .addStringArgument("--use-implicit-cmds")//$NON-NLS-1$
          .addStringArgument("--trim-colors")//$NON-NLS-1$
          .addStringArgument("--simplify-transforms")//$NON-NLS-1$
          .addStringArgument("--multipass")//$NON-NLS-1$
          .addStringArgument("--allow-bigger-file")//$NON-NLS-1$
          .setStdErr(EProcessStream.INHERIT)
          .setStdOut(EProcessStream.IGNORE)
          .setStdIn(EProcessStream.IGNORE).get().waitFor();
      if (res == 0) {
        final byte[] data = Files.readAllBytes(output);
        if (data.length > 0) {
          ConsoleIO.stdout(
              "svgcleaner completed successful, produced svg of "//$NON-NLS-1$
                  + data.length + " bytes.");//$NON-NLS-1$
          return data;
        } else {
          ConsoleIO.stderr(
              "data of zero length resulting from svgcleaner", //$NON-NLS-1$
              null);
        }
      } else {
        ConsoleIO
            .stderr("could not complete svgcleaner, exit code "//$NON-NLS-1$
                + res, null);
      }
    } catch (final Throwable error) {
      ConsoleIO.stderr("error when applying svgcleaner", error); //$NON-NLS-1$
    }

    return null;
  }
}
