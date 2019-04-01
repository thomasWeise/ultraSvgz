package thomasWeise.ultraSvgz;

import java.nio.file.Path;

import thomasWeise.tools.IOJobBuilder;

/** Build a job for the ultrag svgz I/O tool. */
public final class UltraSvgzIOJobBuilder extends IOJobBuilder {

  /** create */
  UltraSvgzIOJobBuilder() {
    super();
  }

  /** {@inheritDoc} */
  @Override
  public final UltraSvgzIOJob get() {
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
}
