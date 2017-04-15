// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.io;

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.io.StringWriter;
import java.io.Reader;
import com.newrelic.org.dom4j.Document;
import org.xml.sax.InputSource;

class DocumentInputSource extends InputSource
{
    private Document document;
    
    public DocumentInputSource() {
    }
    
    public DocumentInputSource(final Document document) {
        this.document = document;
        this.setSystemId(document.getName());
    }
    
    public Document getDocument() {
        return this.document;
    }
    
    public void setDocument(final Document document) {
        this.document = document;
        this.setSystemId(document.getName());
    }
    
    public void setCharacterStream(final Reader characterStream) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
    
    public Reader getCharacterStream() {
        try {
            final StringWriter out = new StringWriter();
            final XMLWriter writer = new XMLWriter(out);
            writer.write(this.document);
            writer.flush();
            return new StringReader(out.toString());
        }
        catch (IOException e) {
            return new Reader() {
                public int read(final char[] ch, final int offset, final int length) throws IOException {
                    throw e;
                }
                
                public void close() throws IOException {
                }
            };
        }
    }
}
