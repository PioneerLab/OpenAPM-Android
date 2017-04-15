// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.io;

import org.xml.sax.XMLFilter;
import org.xml.sax.InputSource;
import com.newrelic.org.dom4j.Document;
import com.newrelic.org.dom4j.Node;
import org.xml.sax.XMLReader;
import javax.xml.transform.sax.SAXSource;

public class DocumentSource extends SAXSource
{
    public static final String DOM4J_FEATURE = "http://org.dom4j.io.DoucmentSource/feature";
    private XMLReader xmlReader;
    
    public DocumentSource(final Node node) {
        this.xmlReader = new SAXWriter();
        this.setDocument(node.getDocument());
    }
    
    public DocumentSource(final Document document) {
        this.xmlReader = new SAXWriter();
        this.setDocument(document);
    }
    
    public Document getDocument() {
        final DocumentInputSource source = (DocumentInputSource)this.getInputSource();
        return source.getDocument();
    }
    
    public void setDocument(final Document document) {
        super.setInputSource(new DocumentInputSource(document));
    }
    
    public XMLReader getXMLReader() {
        return this.xmlReader;
    }
    
    public void setInputSource(final InputSource inputSource) throws UnsupportedOperationException {
        if (inputSource instanceof DocumentInputSource) {
            super.setInputSource(inputSource);
            return;
        }
        throw new UnsupportedOperationException();
    }
    
    public void setXMLReader(final XMLReader reader) throws UnsupportedOperationException {
        if (reader instanceof SAXWriter) {
            this.xmlReader = reader;
        }
        else {
            if (!(reader instanceof XMLFilter)) {
                throw new UnsupportedOperationException();
            }
            XMLFilter filter = (XMLFilter)reader;
            while (true) {
                final XMLReader parent = filter.getParent();
                if (!(parent instanceof XMLFilter)) {
                    break;
                }
                filter = (XMLFilter)parent;
            }
            filter.setParent(this.xmlReader);
            this.xmlReader = filter;
        }
    }
}
