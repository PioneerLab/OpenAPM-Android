// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j;

import java.util.StringTokenizer;
import java.io.Reader;
import org.xml.sax.InputSource;
import java.io.StringReader;
import com.newrelic.org.dom4j.io.SAXReader;
import java.util.List;
import com.newrelic.org.dom4j.rule.Pattern;
import org.jaxen.VariableContext;
import java.util.Map;

public final class DocumentHelper
{
    private static DocumentFactory getDocumentFactory() {
        return DocumentFactory.getInstance();
    }
    
    public static Document createDocument() {
        return getDocumentFactory().createDocument();
    }
    
    public static Document createDocument(final Element rootElement) {
        return getDocumentFactory().createDocument(rootElement);
    }
    
    public static Element createElement(final QName qname) {
        return getDocumentFactory().createElement(qname);
    }
    
    public static Element createElement(final String name) {
        return getDocumentFactory().createElement(name);
    }
    
    public static Attribute createAttribute(final Element owner, final QName qname, final String value) {
        return getDocumentFactory().createAttribute(owner, qname, value);
    }
    
    public static Attribute createAttribute(final Element owner, final String name, final String value) {
        return getDocumentFactory().createAttribute(owner, name, value);
    }
    
    public static CDATA createCDATA(final String text) {
        return DocumentFactory.getInstance().createCDATA(text);
    }
    
    public static Comment createComment(final String text) {
        return DocumentFactory.getInstance().createComment(text);
    }
    
    public static Text createText(final String text) {
        return DocumentFactory.getInstance().createText(text);
    }
    
    public static Entity createEntity(final String name, final String text) {
        return DocumentFactory.getInstance().createEntity(name, text);
    }
    
    public static Namespace createNamespace(final String prefix, final String uri) {
        return DocumentFactory.getInstance().createNamespace(prefix, uri);
    }
    
    public static ProcessingInstruction createProcessingInstruction(final String pi, final String d) {
        return getDocumentFactory().createProcessingInstruction(pi, d);
    }
    
    public static ProcessingInstruction createProcessingInstruction(final String pi, final Map data) {
        return getDocumentFactory().createProcessingInstruction(pi, data);
    }
    
    public static QName createQName(final String localName, final Namespace namespace) {
        return getDocumentFactory().createQName(localName, namespace);
    }
    
    public static QName createQName(final String localName) {
        return getDocumentFactory().createQName(localName);
    }
    
    public static XPath createXPath(final String xpathExpression) throws InvalidXPathException {
        return getDocumentFactory().createXPath(xpathExpression);
    }
    
    public static XPath createXPath(final String xpathExpression, final VariableContext context) throws InvalidXPathException {
        return getDocumentFactory().createXPath(xpathExpression, context);
    }
    
    public static NodeFilter createXPathFilter(final String xpathFilterExpression) {
        return getDocumentFactory().createXPathFilter(xpathFilterExpression);
    }
    
    public static Pattern createPattern(final String xpathPattern) {
        return getDocumentFactory().createPattern(xpathPattern);
    }
    
    public static List selectNodes(final String xpathFilterExpression, final List nodes) {
        final XPath xpath = createXPath(xpathFilterExpression);
        return xpath.selectNodes(nodes);
    }
    
    public static List selectNodes(final String xpathFilterExpression, final Node node) {
        final XPath xpath = createXPath(xpathFilterExpression);
        return xpath.selectNodes(node);
    }
    
    public static void sort(final List list, final String xpathExpression) {
        final XPath xpath = createXPath(xpathExpression);
        xpath.sort(list);
    }
    
    public static void sort(final List list, final String expression, final boolean distinct) {
        final XPath xpath = createXPath(expression);
        xpath.sort(list, distinct);
    }
    
    public static Document parseText(final String text) throws DocumentException {
        Document result = null;
        final SAXReader reader = new SAXReader();
        final String encoding = getEncoding(text);
        final InputSource source = new InputSource(new StringReader(text));
        source.setEncoding(encoding);
        result = reader.read(source);
        if (result.getXMLEncoding() == null) {
            result.setXMLEncoding(encoding);
        }
        return result;
    }
    
    private static String getEncoding(final String text) {
        String result = null;
        final String xml = text.trim();
        if (xml.startsWith("<?xml")) {
            final int end = xml.indexOf("?>");
            final String sub = xml.substring(0, end);
            final StringTokenizer tokens = new StringTokenizer(sub, " =\"'");
            while (tokens.hasMoreTokens()) {
                final String token = tokens.nextToken();
                if ("encoding".equals(token)) {
                    if (tokens.hasMoreTokens()) {
                        result = tokens.nextToken();
                        break;
                    }
                    break;
                }
            }
        }
        return result;
    }
    
    public static Element makeElement(final Branch source, final String path) {
        final StringTokenizer tokens = new StringTokenizer(path, "/");
        Element parent;
        if (source instanceof Document) {
            final Document document = (Document)source;
            parent = document.getRootElement();
            final String name = tokens.nextToken();
            if (parent == null) {
                parent = document.addElement(name);
            }
        }
        else {
            parent = (Element)source;
        }
        Element element = null;
        while (tokens.hasMoreTokens()) {
            final String name = tokens.nextToken();
            if (name.indexOf(58) > 0) {
                element = parent.element(parent.getQName(name));
            }
            else {
                element = parent.element(name);
            }
            if (element == null) {
                element = parent.addElement(name);
            }
            parent = element;
        }
        return element;
    }
}
