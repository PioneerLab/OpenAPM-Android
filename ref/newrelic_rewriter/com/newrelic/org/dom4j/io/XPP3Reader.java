// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.io;

import java.io.InputStreamReader;
import com.newrelic.org.dom4j.QName;
import com.newrelic.org.dom4j.Element;
import com.newrelic.org.dom4j.ElementHandler;
import java.io.CharArrayReader;
import java.io.InputStream;
import java.net.URL;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import com.newrelic.org.dom4j.DocumentException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import com.newrelic.org.dom4j.Document;
import java.io.File;
import org.xmlpull.v1.XmlPullParserFactory;
import org.xmlpull.v1.XmlPullParser;
import com.newrelic.org.dom4j.DocumentFactory;

public class XPP3Reader
{
    private DocumentFactory factory;
    private XmlPullParser xppParser;
    private XmlPullParserFactory xppFactory;
    private DispatchHandler dispatchHandler;
    
    public XPP3Reader() {
    }
    
    public XPP3Reader(final DocumentFactory factory) {
        this.factory = factory;
    }
    
    public Document read(final File file) throws DocumentException, IOException, XmlPullParserException {
        final String systemID = file.getAbsolutePath();
        return this.read(new BufferedReader(new FileReader(file)), systemID);
    }
    
    public Document read(final URL url) throws DocumentException, IOException, XmlPullParserException {
        final String systemID = url.toExternalForm();
        return this.read(this.createReader(url.openStream()), systemID);
    }
    
    public Document read(final String systemID) throws DocumentException, IOException, XmlPullParserException {
        if (systemID.indexOf(58) >= 0) {
            return this.read(new URL(systemID));
        }
        return this.read(new File(systemID));
    }
    
    public Document read(final InputStream in) throws DocumentException, IOException, XmlPullParserException {
        return this.read(this.createReader(in));
    }
    
    public Document read(final Reader reader) throws DocumentException, IOException, XmlPullParserException {
        this.getXPPParser().setInput(reader);
        return this.parseDocument();
    }
    
    public Document read(final char[] text) throws DocumentException, IOException, XmlPullParserException {
        this.getXPPParser().setInput((Reader)new CharArrayReader(text));
        return this.parseDocument();
    }
    
    public Document read(final InputStream in, final String systemID) throws DocumentException, IOException, XmlPullParserException {
        return this.read(this.createReader(in), systemID);
    }
    
    public Document read(final Reader reader, final String systemID) throws DocumentException, IOException, XmlPullParserException {
        final Document document = this.read(reader);
        document.setName(systemID);
        return document;
    }
    
    public XmlPullParser getXPPParser() throws XmlPullParserException {
        if (this.xppParser == null) {
            this.xppParser = this.getXPPFactory().newPullParser();
        }
        return this.xppParser;
    }
    
    public XmlPullParserFactory getXPPFactory() throws XmlPullParserException {
        if (this.xppFactory == null) {
            this.xppFactory = XmlPullParserFactory.newInstance();
        }
        this.xppFactory.setNamespaceAware(true);
        return this.xppFactory;
    }
    
    public void setXPPFactory(final XmlPullParserFactory xPPfactory) {
        this.xppFactory = xPPfactory;
    }
    
    public DocumentFactory getDocumentFactory() {
        if (this.factory == null) {
            this.factory = DocumentFactory.getInstance();
        }
        return this.factory;
    }
    
    public void setDocumentFactory(final DocumentFactory documentFactory) {
        this.factory = documentFactory;
    }
    
    public void addHandler(final String path, final ElementHandler handler) {
        this.getDispatchHandler().addHandler(path, handler);
    }
    
    public void removeHandler(final String path) {
        this.getDispatchHandler().removeHandler(path);
    }
    
    public void setDefaultHandler(final ElementHandler handler) {
        this.getDispatchHandler().setDefaultHandler(handler);
    }
    
    protected Document parseDocument() throws DocumentException, IOException, XmlPullParserException {
        final DocumentFactory df = this.getDocumentFactory();
        final Document document = df.createDocument();
        Element parent = null;
        final XmlPullParser pp = this.getXPPParser();
        pp.setFeature("http://xmlpull.org/v1/doc/features.html#process-namespaces", true);
        while (true) {
            final int type = pp.nextToken();
            switch (type) {
                case 8: {
                    final String text = pp.getText();
                    final int loc = text.indexOf(" ");
                    if (loc >= 0) {
                        final String target = text.substring(0, loc);
                        final String txt = text.substring(loc + 1);
                        document.addProcessingInstruction(target, txt);
                        continue;
                    }
                    document.addProcessingInstruction(text, "");
                    continue;
                }
                case 9: {
                    if (parent != null) {
                        parent.addComment(pp.getText());
                        continue;
                    }
                    document.addComment(pp.getText());
                    continue;
                }
                case 5: {
                    if (parent != null) {
                        parent.addCDATA(pp.getText());
                        continue;
                    }
                    final String msg = "Cannot have text content outside of the root document";
                    throw new DocumentException(msg);
                }
                case 6: {
                    continue;
                }
                case 1: {
                    return document;
                }
                case 2: {
                    final QName qname = (pp.getPrefix() == null) ? df.createQName(pp.getName(), pp.getNamespace()) : df.createQName(pp.getName(), pp.getPrefix(), pp.getNamespace());
                    final Element newElement = df.createElement(qname);
                    final int nsStart = pp.getNamespaceCount(pp.getDepth() - 1);
                    for (int nsEnd = pp.getNamespaceCount(pp.getDepth()), i = nsStart; i < nsEnd; ++i) {
                        if (pp.getNamespacePrefix(i) != null) {
                            newElement.addNamespace(pp.getNamespacePrefix(i), pp.getNamespaceUri(i));
                        }
                    }
                    for (int i = 0; i < pp.getAttributeCount(); ++i) {
                        final QName qa = (pp.getAttributePrefix(i) == null) ? df.createQName(pp.getAttributeName(i)) : df.createQName(pp.getAttributeName(i), pp.getAttributePrefix(i), pp.getAttributeNamespace(i));
                        newElement.addAttribute(qa, pp.getAttributeValue(i));
                    }
                    if (parent != null) {
                        parent.add(newElement);
                    }
                    else {
                        document.add(newElement);
                    }
                    parent = newElement;
                    continue;
                }
                case 3: {
                    if (parent != null) {
                        parent = parent.getParent();
                        continue;
                    }
                    continue;
                }
                case 4: {
                    final String text = pp.getText();
                    if (parent != null) {
                        parent.addText(text);
                        continue;
                    }
                    final String msg2 = "Cannot have text content outside of the root document";
                    throw new DocumentException(msg2);
                }
            }
        }
    }
    
    protected DispatchHandler getDispatchHandler() {
        if (this.dispatchHandler == null) {
            this.dispatchHandler = new DispatchHandler();
        }
        return this.dispatchHandler;
    }
    
    protected void setDispatchHandler(final DispatchHandler dispatchHandler) {
        this.dispatchHandler = dispatchHandler;
    }
    
    protected Reader createReader(final InputStream in) throws IOException {
        return new BufferedReader(new InputStreamReader(in));
    }
}
