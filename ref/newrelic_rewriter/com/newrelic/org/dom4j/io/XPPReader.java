// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.io;

import java.io.InputStreamReader;
import org.gjt.xpp.XmlEndTag;
import com.newrelic.org.dom4j.Element;
import org.gjt.xpp.XmlStartTag;
import com.newrelic.org.dom4j.xpp.ProxyXmlStartTag;
import com.newrelic.org.dom4j.ElementHandler;
import java.io.InputStream;
import java.net.URL;
import org.gjt.xpp.XmlPullParserException;
import java.io.IOException;
import com.newrelic.org.dom4j.DocumentException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import com.newrelic.org.dom4j.Document;
import java.io.File;
import org.gjt.xpp.XmlPullParserFactory;
import org.gjt.xpp.XmlPullParser;
import com.newrelic.org.dom4j.DocumentFactory;

public class XPPReader
{
    private DocumentFactory factory;
    private XmlPullParser xppParser;
    private XmlPullParserFactory xppFactory;
    private DispatchHandler dispatchHandler;
    
    public XPPReader() {
    }
    
    public XPPReader(final DocumentFactory factory) {
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
        this.getXPPParser().setInput(text);
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
        return this.xppFactory;
    }
    
    public void setXPPFactory(final XmlPullParserFactory xPPFactory) {
        this.xppFactory = xPPFactory;
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
        final Document document = this.getDocumentFactory().createDocument();
        Element parent = null;
        final XmlPullParser parser = this.getXPPParser();
        parser.setNamespaceAware(true);
        final ProxyXmlStartTag startTag = new ProxyXmlStartTag();
        final XmlEndTag endTag = this.xppFactory.newEndTag();
        while (true) {
            final int type = parser.next();
            switch (type) {
                case 1: {
                    return document;
                }
                case 2: {
                    parser.readStartTag((XmlStartTag)startTag);
                    final Element newElement = startTag.getElement();
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
                    parser.readEndTag(endTag);
                    if (parent != null) {
                        parent = parent.getParent();
                        continue;
                    }
                    continue;
                }
                case 4: {
                    final String text = parser.readContent();
                    if (parent != null) {
                        parent.addText(text);
                        continue;
                    }
                    final String msg = "Cannot have text content outside of the root document";
                    throw new DocumentException(msg);
                }
                default: {
                    throw new DocumentException("Error: unknown type: " + type);
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
