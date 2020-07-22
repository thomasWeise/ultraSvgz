package thomasWeise.ultraSvgz;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.CharArrayWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import thomasWeise.tools.ByteBuffers;
import thomasWeise.tools.ConsoleIO;

/** convert the data */
final class _Tools {

  /** the transformer factory */
  private static final TransformerFactory FACT =
      TransformerFactory.newInstance();

  /** the transformer */
  private static final ThreadLocal<Transformer> TRAFO =
      ThreadLocal.withInitial(() -> {
        try {
          final Transformer trafo = _Tools.FACT.newTransformer();
          trafo.setOutputProperty(OutputKeys.INDENT, "no");//$NON-NLS-1$
          trafo.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC,
              "yes");//$NON-NLS-1$
          return (trafo);
        } catch (final Throwable error) {
          ConsoleIO.stderr("error when creating XML transformer", //$NON-NLS-1$
              error);
        }
        return null;
      });

  /** the char array writers */
  private static final ThreadLocal<CharArrayWriter> CAW =
      ThreadLocal.withInitial(CharArrayWriter::new);

  /**
   * Transform an XML Document to bytes
   *
   * @param document
   *          the documents
   * @return the bytes
   */
  static byte[] _toBytes(final Document document) {
    try (final ByteArrayOutputStream bos =
        ByteBuffers.get().getBufferedOutputStream()) {
      document.setXmlStandalone(true);
      _Tools.TRAFO.get().transform(new DOMSource(document),
          new StreamResult(bos));
      return bos.toByteArray();
    } catch (final Throwable error) {
      ConsoleIO.stderr("error when transforming xml to bytes", //$NON-NLS-1$
          error);
    }
    return null;
  }

  /**
   * Transform an XML Document to chars
   *
   * @param document
   *          the documents
   * @return the chars
   */
  static char[] _toChars(final Document document) {
    try (final CharArrayWriter bos = _Tools.CAW.get()) {
      document.setXmlStandalone(true);
      _Tools.TRAFO.get().transform(new DOMSource(document),
          new StreamResult(bos));
      final char[] res = bos.toCharArray();
      bos.reset();
      return res;
    } catch (final Throwable error) {
      ConsoleIO.stderr("error when transforming xml to text", //$NON-NLS-1$
          error);
    }
    return null;
  }

  /** the document builders */
  private static final ThreadLocal<
      DocumentBuilder> DOC_BUILDERS =
          ThreadLocal.withInitial(() -> {
            try {
              final DocumentBuilderFactory dbf =
                  DocumentBuilderFactory.newInstance();

              dbf.setValidating(false);

              dbf.setIgnoringComments(true);
              dbf.setIgnoringElementContentWhitespace(true);
              dbf.setNamespaceAware(true);
              dbf.setCoalescing(true);
              dbf.setExpandEntityReferences(true);

              final DocumentBuilder db =
                  dbf.newDocumentBuilder();
              db.setEntityResolver(null);
              return (db);
            } catch (final Throwable error) {
              ConsoleIO.stderr(
                  "error when creating DOM document builder", //$NON-NLS-1$
                  error);
            }
            return null;
          });

  /**
   * parse a document from bytes
   *
   * @param data
   *          the data
   * @return the document
   */
  static Document _fromBytes(final byte[] data) {
    try (final ByteArrayInputStream bis =
        new ByteArrayInputStream(data)) {
      return _Tools.DOC_BUILDERS.get().parse(bis);
    } catch (final Throwable error) {
      ConsoleIO.stderr(
          "error when creating parsing xml document", //$NON-NLS-1$
          error);
    }
    return null;
  }

  /**
   * clone a DOM document
   *
   * @param doc
   *          the document
   * @return the copy
   */
  static Document _clone(final Document doc) {
    final Document clonedDoc =
        _Tools.DOC_BUILDERS.get().newDocument();
    clonedDoc.appendChild(
        clonedDoc.importNode(doc.getDocumentElement(), true));
    return clonedDoc;
  }
}
