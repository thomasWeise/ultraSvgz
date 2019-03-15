package thomasWeise.ultraSvgz;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

import thomasWeise.tools.Configuration;

/** A class for testing UltraZip */
public class UltraSvgzTest {

  static {
    Configuration.putInteger("gzipIntensity", 0);//$NON-NLS-1$
    Configuration.putInteger("svgIntensity", 3);//$NON-NLS-1$
  }

  /**
   * read an input stream
   *
   * @param is
   *          the input stream
   * @return the byte array
   * @throws IOException
   *           if i/o fails
   */
  private static final byte[] __readAll(final InputStream is)
      throws IOException {
    try (final ByteArrayOutputStream bos =
        new ByteArrayOutputStream()) {
      final byte[] buffer = new byte[1024];
      int i;
      while ((i = is.read(buffer)) > 0) {
        bos.write(buffer, 0, i);
      }
      Assert.assertTrue(bos.size() > 0);
      final byte[] res = bos.toByteArray();
      Assert.assertTrue(res.length > 0);
      return res;
    }
  }

  /**
   * Test processing the given resource
   *
   * @param resource
   *          the resource
   * @throws IOException
   *           if it fails
   */
  private final void __test(final String resource)
      throws IOException {
    byte[] data = null;
    try (final InputStream bis =
        UltraSvgzTest.class.getResourceAsStream(resource)) {
      data = UltraSvgzTest.__readAll(bis);
    }

    final byte[] result = UltraSvgz.getInstance().get()
        .setData(data).setName(resource).get().call();
    Assert.assertNotNull(result);
    Assert.assertTrue(result.length <= data.length);

    byte[] unc1 = null;
    try (
        final ByteArrayInputStream bis =
            new ByteArrayInputStream(result);
        final java.util.zip.GZIPInputStream gis =
            new java.util.zip.GZIPInputStream(bis)) {
      unc1 = UltraSvgzTest.__readAll(gis);
    }

    byte[] unc2 = null;
    try (
        final ByteArrayInputStream bis =
            new ByteArrayInputStream(result);
        final com.jcraft.jzlib.GZIPInputStream gis =
            new com.jcraft.jzlib.GZIPInputStream(bis)) {
      unc2 = UltraSvgzTest.__readAll(gis);
    }

    Assert.assertArrayEquals(unc1, unc2);
    Assert.assertTrue(unc1.length > 0);
    Assert.assertTrue(unc1.length <= data.length);
  }

  /**
   * Test resource a.svg
   *
   * @throws IOException
   *           if i/o fails
   */
  @Test(timeout = 900000)
  public final void testA() throws IOException {
    this.__test("a.svg");//$NON-NLS-1$
  }

  /**
   * Test resource b.svg
   *
   * @throws IOException
   *           if i/o fails
   */
  @Test(timeout = 900000)
  public final void testB() throws IOException {
    this.__test("b.svg");//$NON-NLS-1$
  }
}
