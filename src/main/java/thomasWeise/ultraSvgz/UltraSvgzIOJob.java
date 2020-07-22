package thomasWeise.ultraSvgz;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

import thomasWeise.tools.ByteBuffers;
import thomasWeise.tools.ConsoleIO;
import thomasWeise.tools.IOJob;

/** The job for the ultra svgz I/O tool. */
public final class UltraSvgzIOJob extends IOJob {

  /** should white space be removed? */
  private final boolean m_cleanseWhiteSpace;

  /**
   * create
   *
   * @param ugo
   *          the job builder
   */
  UltraSvgzIOJob(final UltraSvgzIOJobBuilder ugo) {
    super(ugo);
    this.m_cleanseWhiteSpace = ugo.getCleanseWhiteSpace();
  }

  /** run! */
  @Override
  public void run() {
    byte[] data;
    byte[] out;
    int size;
    String name;

    if (this.isUsingStdIn()) {
      name = "stdin"; //$NON-NLS-1$
    } else {
      name = this.getInputPath().getFileName().toString();
    }
    if (this.isUsingStdOut()) {
      name += "->stdout"; //$NON-NLS-1$
    } else {
      name = (((name + '-') + '>')
          + this.getOutputPath().getFileName().toString());
    }

    ConsoleIO.stdout(name + " is now loading input data."); //$NON-NLS-1$
    try {

      try (
          final InputStream is = (this.isUsingStdIn() ? System.in//
              : Files.newInputStream(this.getInputPath()))) {
        data = ByteBuffers.get().load(is);
      }

      ConsoleIO.stdout(
          name + " has loaded input data - now compressing."); //$NON-NLS-1$

      out = UltraSvgz.getInstance().get().setData(data)//
          .setName(name)
          .setCleanseWhiteSpace(this.m_cleanseWhiteSpace)//
          .get().call();

      ConsoleIO.stdout(
          name + " has compressed the input data down to " //$NON-NLS-1$
              + out.length
              + "B, which will now be written to the output."); //$NON-NLS-1$

      try (final OutputStream os =
          (this.isUsingStdOut() ? System.out : //
              Files.newOutputStream(this.getOutputPath()))) {//
        os.write(out);
        size = out.length;
        data = null;
      }

      ConsoleIO.stdout(name + " has written all " + size //$NON-NLS-1$
          + "B to the output and, hence, completed its task."); //$NON-NLS-1$
    } catch (final Throwable error) {
      ConsoleIO.stderr(name + " has failed.", error);//$NON-NLS-1$
    }
  }
}
