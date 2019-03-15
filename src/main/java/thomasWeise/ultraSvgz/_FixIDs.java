package thomasWeise.ultraSvgz;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/** a tool job for fixing IDs */
final class _FixIDs {

  /** the xlink namespace */
  private static final String XLINK =
      "http://www.w3.org/1999/xlink";//$NON-NLS-1$

  /** the svg namespace */
  private static final String SVG = "http://www.w3.org/2000/svg";//$NON-NLS-1$

  /** the href NS */
  private static final String[] HREF_NS =
      { null, _FixIDs.XLINK, _FixIDs.SVG };

  /** the prefix */
  private static final String XMLNS = "xmlns";//$NON-NLS-1$

  /** ignore this attribute */
  private static final String IGNORE_PREFIX = "xml";//$NON-NLS-1$

  /**
   * Fix the IDs of a given document
   *
   * @param in
   *          the input document
   * @return the new document
   */
  static final Document _apply(final Document in) {
    final Document copy = _Tools._clone(in);

    for (int i = 4; (--i) >= 0;) {
      final _NumberSystem system = new _NumberSystem(copy);
      _FixIDs.__fixIDs(copy, system);
      _FixIDs.__fixNamespaces(copy, system);
    }
    return copy;
  }

  /**
   * fix the IDs used in a document
   *
   * @param doc
   *          the document
   * @param system
   *          the number system
   */
  private static final void __fixIDs(final Document doc,
      final _NumberSystem system) {
    final _IDTranslator trans = new _IDTranslator(system);

    _FixIDs.__collectIDs(doc.getDocumentElement(), trans);
    trans._translate();
    _FixIDs.__translateIDs(doc.getDocumentElement(), trans);
  }

  /**
   * collect all ids
   *
   * @param node
   *          the node
   * @param trans
   *          the translator
   */
  private static final void __collectIDs(final Element node,
      final _IDTranslator trans) {
    final String s = node.getAttribute("id");//$NON-NLS-1$
    if ((s != null) && (!(s.isEmpty()))) {
      trans._tickID(s);
    }
    final NodeList l = node.getChildNodes();
    if (l != null) {
      for (int i = l.getLength(); (--i) >= 0;) {
        final Node n = l.item(i);
        if (n != null) {
          if (n.getNodeType() == Node.ELEMENT_NODE) {
            _FixIDs.__collectIDs((Element) n, trans);
          }
        }
      }
    }
  }

  /**
   * translate all ids
   *
   * @param node
   *          the node
   * @param trans
   *          the translator
   */
  private static final void __translateIDs(final Element node,
      final _IDTranslator trans) {
    String s = node.getAttribute("id");//$NON-NLS-1$
    if ((s != null) && (!(s.isEmpty()))) {
      node.setAttribute("id", trans._translateID(s));//$NON-NLS-1$
    }

    for (final String ns : _FixIDs.HREF_NS) {
      final Attr a = node.getAttributeNodeNS(ns, "href");//$NON-NLS-1$
      if (a != null) {
        final String v = a.getValue();
        if (!(v.isEmpty())) {
          if (v.charAt(0) == '#') {
            a.setValue('#' + trans._translateID(v.substring(1)));
          }
        }
      }
    }

    final NamedNodeMap attrs = node.getAttributes();
    if (attrs != null) {
      for (int i = attrs.getLength(); (--i) >= 0;) {
        final Node a = attrs.item(i);
        if ((a != null)
            && (a.getNodeType() == Node.ATTRIBUTE_NODE)) {
          s = a.getTextContent();
          if ((s != null) && (!(s.isEmpty()))) {
            if (s.startsWith("url(")) {
              final int l = s.length() - 1;
              if (s.charAt(l) == ')') {
                s = s.substring(4, l).trim();
                if ((!(s.isEmpty())) && (s.charAt(0) == '#')) {
                  a.setTextContent("url(#"//$NON-NLS-1$
                      + trans._translateID(s.substring(1).trim())
                      + ')');
                }
              }
            }
          }
        }
      }
    }

    final NodeList l = node.getChildNodes();
    if (l != null) {
      for (int i = l.getLength(); (--i) >= 0;) {
        final Node n = l.item(i);
        if (n != null) {
          if (n.getNodeType() == Node.ELEMENT_NODE) {
            _FixIDs.__translateIDs((Element) n, trans);
          }
        }
      }
    }
  }

  /**
   * fix the IDs used in a document
   *
   * @param doc
   *          the document
   * @param system
   *          the number system
   */
  private static final void __fixNamespaces(final Document doc,
      final _NumberSystem system) {
    final _IDTranslator trans = new _IDTranslator(system);

    _FixIDs.__collectNamespacess(doc.getDocumentElement(),
        trans);
    trans._translate();
    _FixIDs.__translateNamespacess(doc.getDocumentElement(),
        trans);
  }

  /**
   * collect all namespaces
   *
   * @param node
   *          the node
   * @param trans
   *          the translator
   */
  private static final void __collectNamespacess(
      final Element node, final _IDTranslator trans) {

    String s = node.getPrefix();
    if ((s != null) && (!(s.isEmpty()))) {
      s = node.getNamespaceURI();
      if ((s != null) && (!(s.isEmpty()))
          && (!(_FixIDs.SVG.equals(s)
              || _FixIDs.XLINK.equals(s)))) {
        trans._tickID(s);
      }
    }

    final NamedNodeMap attrs = node.getAttributes();
    if (attrs != null) {
      for (int i = attrs.getLength(); (--i) >= 0;) {
        final Node a = attrs.item(i);
        if ((a != null)
            && (a.getNodeType() == Node.ATTRIBUTE_NODE)) {
          s = a.getPrefix();
          if ((s != null) && (!(s.isEmpty()))
              && (!_FixIDs.IGNORE_PREFIX.equals(s))
              && (!(_FixIDs.XMLNS.equals(s)))) {
            s = a.getNamespaceURI();
            if ((s != null) && (!(s.isEmpty()))
                && (!(_FixIDs.SVG.equals(s)
                    || _FixIDs.XLINK.equals(s)))) {
              trans._tickID(s);
            }
          }
        }
      }
    }

    final NodeList l = node.getChildNodes();
    if (l != null) {
      for (int i = l.getLength(); (--i) >= 0;) {
        final Node n = l.item(i);
        if (n != null) {
          if (n.getNodeType() == Node.ELEMENT_NODE) {
            _FixIDs.__collectNamespacess((Element) n, trans);
          }
        }
      }
    }
  }

  /**
   * translate all namespaces
   *
   * @param node
   *          the node
   * @param trans
   *          the translator
   */
  private static final void __translateNamespacess(
      final Element node, final _IDTranslator trans) {

    String s;
    String t = node.getPrefix();
    if ((t != null) && (!(t.isEmpty()))) {
      s = node.getNamespaceURI();
      if ((s != null) && (!(s.isEmpty()))
          && (!(_FixIDs.SVG.equals(s)
              || _FixIDs.XLINK.equals(s)))) {
        node.setPrefix(trans._translateID(t));
      }
    }

    final NamedNodeMap attrs = node.getAttributes();
    if (attrs != null) {
      for (int i = attrs.getLength(); (--i) >= 0;) {
        final Node a = attrs.item(i);
        t = a.getPrefix();
        if ((t != null) && (!(t.isEmpty()))) {
          if ((a != null)
              && (a.getNodeType() == Node.ATTRIBUTE_NODE)) {
            if (_FixIDs.XMLNS.equals(t)) {
              final String ns = a.getTextContent();
              if (!_FixIDs.XLINK.equals(ns)) {
                attrs.removeNamedItemNS(a.getNamespaceURI(),
                    a.getLocalName());
                final Attr v = node.getOwnerDocument()
                    .createAttribute(_FixIDs.XMLNS + ':'
                        + trans._translateID(ns));
                v.setTextContent(ns);
                node.setAttributeNode(v);
              }
            } else {
              if (!_FixIDs.IGNORE_PREFIX.equals(t)) {
                s = a.getNamespaceURI();
                if ((s != null) && (!(s.isEmpty()))
                    && (!(_FixIDs.SVG.equals(s)
                        || _FixIDs.XLINK.equals(s)))) {
                  a.setPrefix(trans._translateID(s));
                }
              }
            }
          }
        }
      }
    }

    final NodeList l = node.getChildNodes();
    if (l != null) {
      for (int i = l.getLength(); (--i) >= 0;) {
        final Node n = l.item(i);
        if (n != null) {
          if (n.getNodeType() == Node.ELEMENT_NODE) {
            _FixIDs.__translateNamespacess((Element) n, trans);
          }
        }
      }
    }
  }
}
