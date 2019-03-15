package thomasWeise.ultraSvgz;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

import thomasWeise.tools.ConsoleIO;

/** use the https://www.svgminify.com SVG minifier */
final class _SVGMinify {

  /** the base url */
  private static final String HOST = "www.svgminify.com";//$NON-NLS-1$

  /** the char array */
  private static final char[] SEP =
      "acdghjknopqvwxyzABCDEFGHIKLMNOPQRSTUVWXYZ"//$NON-NLS-1$
          .toCharArray();

  /** 10 second connection timeout */
  private static final int CONNECT_TIMEOUT = 1000 * 10;
  /** 30 second r/w timeout */
  private static final int RW_TIMEOUT = 1000 * 30;

  /**
   * find a separator
   *
   * @param body
   *          the body
   * @return the separator
   */
  private static final char[] __sep(final byte[] body) {
    for (int i = 1;; i++) {
      final char[] dst = new char[i];
      if (_SVGMinify.__sep(body, dst, 0)) {
        final char[] res = new char[i + 2];
        res[0] = '-';
        res[1] = '-';
        System.arraycopy(dst, 0, res, 2, i);
        return res;
      }
    }
  }

  /**
   * create the separator
   *
   * @param body
   *          the body
   * @param sb
   *          the string builder
   * @param index
   *          the current character index
   * @return {@code true} if we are done, {@code false} otherwise
   */
  private static final boolean __sep(final byte[] body,
      final char[] sb, final int index) {

    outer: for (final char ch : _SVGMinify.SEP) {
      sb[index] = ch;

      final int l = index + 1;
      if (l >= sb.length) {

        finder: for (int i = body.length - index; (--i) >= 0;) {
          for (int k = l; (--k) >= 0;) {
            if (sb[k] != body[i + k]) {
              continue finder;
            }
          }
          continue outer;
        }
        return true;
      } else {
        return (_SVGMinify.__sep(body, sb, l));
      }
    }
    return false;
  }

  /**
   * read the answer from a socket
   *
   * @param socket
   *          the socket
   * @return the answer
   * @throws IOException
   *           if i/o fails
   */
  private static final String __getResult(final Socket socket)
      throws IOException {
    String answer = null;
    try (final InputStream is = socket.getInputStream()) {
      try (ByteArrayOutputStream result =
          new ByteArrayOutputStream()) {
        final byte[] buffer = new byte[1024];
        int length;
        while ((length = is.read(buffer)) != -1) {
          result.write(buffer, 0, length);
        }
        if (result.size() <= 0) {
          return null;
        }
        answer = result.toString(StandardCharsets.UTF_8.name());
      }
    }
    if (answer == null) {
      return null;
    }
    if (answer.length() <= 10) {
      return null;
    }
    if (answer.contains("200 OK")) {//$NON-NLS-1$
      return answer;
    }
    return null;
  }

  /**
   * apply the scour transformation
   *
   * @param in
   *          the input
   * @return the output
   */
  static final byte[] _apply(final byte[] in) {

    try {
      InetAddress addr = InetAddress.getByName(_SVGMinify.HOST);

      final SSLSocketFactory fact =
          ((SSLSocketFactory) (SSLSocketFactory.getDefault()));

      String answer = null;

      // upload the data
      try (final SSLSocket socket =
          ((SSLSocket) (fact.createSocket()))) {
        socket.connect(new InetSocketAddress(addr, 443),
            _SVGMinify.CONNECT_TIMEOUT);
        socket.setSoTimeout(_SVGMinify.RW_TIMEOUT);
        socket.startHandshake();

        addr = socket.getInetAddress();
        final char[] sep = _SVGMinify.__sep(in);

        try (final OutputStream os = socket.getOutputStream();
            final OutputStreamWriter osw =
                new OutputStreamWriter(os)) {
          osw.write("POST / HTTP/1.1\r\nHost: ");//$NON-NLS-1$
          osw.write(_SVGMinify.HOST);
          osw.write("\r\nAccept: */*");//$NON-NLS-1$

          try (
              final ByteArrayOutputStream bos =
                  new ByteArrayOutputStream();
              final OutputStreamWriter osw2 =
                  new OutputStreamWriter(bos)) {
            osw2.write("--");
            osw2.write(sep);
            osw2.write(
                "\r\nContent-Disposition: form-data; name=\"submit\"\r\n\r\nsubmit\r\n--");//$NON-NLS-1$
            osw2.write(sep);
            osw2.write(
                "\r\nContent-Disposition: form-data; name=\"userfile\"; filename=\"a.svg\"\r\nContent-Type: image/svg+xml\r\n\r\n");//$NON-NLS-1$
            osw2.flush();
            bos.write(in);
            bos.flush();
            osw2.write("\r\n--");//$NON-NLS-1$
            osw2.write(sep);
            osw2.write("--\r\n");//$NON-NLS-1$
            osw2.flush();

            osw.write("\r\nContent-Length: ");//$NON-NLS-1$
            osw.write(Integer.toString(bos.size()));
            osw.write(
                "\r\nContent-Type: multipart/form-data; boundary=");//$NON-NLS-1$
            osw.write(sep);
            osw.write("\r\n\r\n");//$NON-NLS-1$
            osw.flush();
            bos.writeTo(os);
            os.flush();
          }

          answer = _SVGMinify.__getResult(socket);
        }
      }

      // got an answer
      if (answer == null) {
        ConsoleIO.stderr("did not get a reasonable answer from "//$NON-NLS-1$
            + _SVGMinify.HOST, null);
        return null;
      } else {
        ConsoleIO.stdout("uploaded SVG and received "//$NON-NLS-1$
            + answer.length() + " characters from "//$NON-NLS-1$
            + _SVGMinify.HOST);
      }

      String search = "var url = '";//$NON-NLS-1$
      int start = answer.indexOf(search);
      if (start <= 0) {
        ConsoleIO.stderr("answer from " + _SVGMinify.HOST //$NON-NLS-1$
            + " does not contain valid reference", null);//$NON-NLS-1$
        return null;
      }
      start += search.length();
      int end = answer.indexOf('\'', (start + 1));
      if (end <= start) {
        ConsoleIO.stderr("answer from " + _SVGMinify.HOST//$NON-NLS-1$
            + " does not contain valid reference end", null);
        return null;
      }

      answer = answer.substring(start, end).trim();

      // download the data
      try (final SSLSocket socket =
          ((SSLSocket) (fact.createSocket()))) {
        socket.connect(new InetSocketAddress(addr, 443),
            _SVGMinify.CONNECT_TIMEOUT);
        socket.setSoTimeout(_SVGMinify.RW_TIMEOUT);
        socket.startHandshake();

        try (final OutputStream os = socket.getOutputStream();
            final OutputStreamWriter osw =
                new OutputStreamWriter(os)) {
          osw.write("GET ");//$NON-NLS-1$
          osw.write(answer);
          osw.write(" HTTP/1.1\r\nHost: ");//$NON-NLS-1$
          osw.write(_SVGMinify.HOST);
          osw.write("\r\nAccept: */*\r\n\r\n");//$NON-NLS-1$
          osw.flush();
          answer = _SVGMinify.__getResult(socket);
        }
      }

      // got an answer
      if (answer == null) {
        ConsoleIO.stderr("did not get minified contents from " //$NON-NLS-1$
            + _SVGMinify.HOST, null);
        return null;
      } else {
        ConsoleIO.stdout("received "//$NON-NLS-1$
            + answer.length()
            + " content characters of svg minimization answer from "//$NON-NLS-1$
            + _SVGMinify.HOST);
      }

      search = "<textarea id=\"result-textarea\">";//$NON-NLS-1$
      start = answer.indexOf(search);
      if (start <= 0) {
        ConsoleIO.stderr("answer from " + _SVGMinify.HOST//$NON-NLS-1$
            + " does not contain valid svg", //$NON-NLS-1$
            null);
        return null;
      }
      start += search.length();
      end = answer.indexOf("</textarea>", (start + 1));//$NON-NLS-1$
      if (end <= start) {
        ConsoleIO.stderr("answer from " + _SVGMinify.HOST//$NON-NLS-1$
            + " does not contain valid svg end", //$NON-NLS-1$
            null);
        return null;
      }

      answer = answer.substring(start, end).trim();

      end = answer.lastIndexOf("</svg>");//$NON-NLS-1$
      if (end <= 0) {
        return null;
      }
      answer = answer.substring(0, end + 6).trim();

      ConsoleIO.stdout(_SVGMinify.HOST + " produced "//$NON-NLS-1$
          + answer.length() + " char svg.");//$NON-NLS-1$
      return (answer.getBytes(StandardCharsets.UTF_8));
    } catch (final Throwable error) {
      ConsoleIO.stderr("error when using minifier service at " //$NON-NLS-1$
          + _SVGMinify.HOST, error);
    }

    return null;
  }
}
