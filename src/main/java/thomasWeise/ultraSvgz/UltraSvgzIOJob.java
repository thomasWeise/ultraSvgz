package thomasWeise.ultraSvgz;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import thomasWeise.tools.ByteBuffers;
import thomasWeise.tools.ConsoleIO;

/** The job for the ultra svgz I/O tool. */
public final class UltraSvgzIOJob implements Runnable {

  /** the input path */
  private final Path m_input;

  /** should we use stdin instead of an input file? */
  private final boolean m_useStdIn;

  /** the output path */
  private final Path m_output;

  /** should we use stdout instead of an output file? */
  private final boolean m_useStdOut;

  /**
   * create
   *
   * @param ugo
   *          the job builder
   */
  UltraSvgzIOJob(final UltraSvgzIOJobBuilder ugo) {
    super();

    this.m_input = ugo.m_input;
    this.m_useStdIn = ugo.m_useStdIn;
    this.m_output = ugo.m_output;
    this.m_useStdOut = ugo.m_useStdOut;
    UltraSvgzIOJobBuilder._validate(this.m_input,
        this.m_useStdIn, this.m_output, this.m_useStdOut);
  }

  /** run! */
  @Override
  public final void run() {
    byte[] data;
    byte[] out;
    int size;
    String name;

    if (this.m_useStdIn) {
      name = "stdin"; //$NON-NLS-1$
    } else {
      name = this.m_input.getFileName().toString();
    }
    if (this.m_useStdOut) {
      name += "->stdout"; //$NON-NLS-1$
    } else {
      name = (((name + '-') + '>')
          + this.m_output.getFileName().toString());
    }

    ConsoleIO.stdout(name + " is now loading input data."); //$NON-NLS-1$
    try {

      try (final InputStream is = (this.m_useStdIn ? System.in//
          : Files.newInputStream(this.m_input))) {
        data = ByteBuffers.get().load(is);
      }

      ConsoleIO.stdout(
          name + " has loaded input data - now compressing."); //$NON-NLS-1$

      out = UltraSvgz.getInstance().get().setData(data)//
          .setName(name).get().call();

      ConsoleIO.stdout(
          name + " has compressed the input data down to " //$NON-NLS-1$
              + out.length
              + "B, which will now be written to the output."); //$NON-NLS-1$

      try (final OutputStream os =
          (this.m_useStdOut ? System.out : //
              Files.newOutputStream(this.m_output))) {//
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
