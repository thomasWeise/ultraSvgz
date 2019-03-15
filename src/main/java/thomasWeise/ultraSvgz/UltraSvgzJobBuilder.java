package thomasWeise.ultraSvgz;

import java.util.function.Supplier;

/** Build a job for the ultrag svgz. */
public final class UltraSvgzJobBuilder
    implements Supplier<UltraSvgzJob> {

  /** the data */
  private byte[] m_data;

  /** the job's name */
  private String m_name;

  /** create */
  UltraSvgzJobBuilder() {
    super();
  }

  /**
   * Check a name string
   *
   * @param name
   *          the name string
   * @return the prepared name
   */
  static final String _checkName(final String name) {
    final String str;

    str = name.trim();
    if (str.isEmpty()) {
      throw new IllegalArgumentException(//
          "Name cannot be empty string, null, or just composed white space, but '" //$NON-NLS-1$
              + name + "' falls into this category."); //$NON-NLS-1$
    }
    return str;
  }

  /**
   * Set the name of the data element to be packed
   *
   * @param name
   *          the name of the data element to be packed
   * @return this builder
   */
  public final UltraSvgzJobBuilder setName(final String name) {
    this.m_name = UltraSvgzJobBuilder._checkName(name);
    return this;
  }

  /**
   * Check the data to be compressed.
   *
   * @param data
   *          the data
   */
  static final void _checkData(final byte[] data) {
    if (data == null) {
      throw new IllegalArgumentException(
          "Data to be gzip-compressed cannot be null."); //$NON-NLS-1$
    }
    if (data.length <= 0) {
      throw new IllegalArgumentException(
          "The data to be gzip-compressed cannot be zero-lengthed.");//$NON-NLS-1$
    }
  }

  /**
   * Set the data to be compressed
   *
   * @param data
   *          the data to be compressed
   * @return this builder
   */
  public final UltraSvgzJobBuilder setData(final byte[] data) {
    UltraSvgzJobBuilder._checkData(data);
    this.m_data = data;
    return this;
  }

  /** {@inheritDoc} */
  @Override
  public final UltraSvgzJob get() {
    return new UltraSvgzJob(this.m_data, this.m_name);
  }
}
