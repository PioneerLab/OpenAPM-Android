// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.jaxb;

import com.newrelic.org.dom4j.Element;
import com.newrelic.org.dom4j.ElementPath;
import com.newrelic.org.dom4j.ElementHandler;
import java.net.URL;
import org.xml.sax.InputSource;
import java.io.Reader;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.nio.charset.Charset;
import com.newrelic.org.dom4j.DocumentException;
import com.newrelic.org.dom4j.Document;
import java.io.File;
import com.newrelic.org.dom4j.io.SAXReader;

public class JAXBReader extends JAXBSupport
{
    private SAXReader reader;
    private boolean pruneElements;
    
    public JAXBReader(final String contextPath) {
        super(contextPath);
    }
    
    public JAXBReader(final String contextPath, final ClassLoader classloader) {
        super(contextPath, classloader);
    }
    
    public Document read(final File source) throws DocumentException {
        return this.getReader().read(source);
    }
    
    public Document read(final File file, final Charset charset) throws DocumentException {
        try {
            final Reader xmlReader = new InputStreamReader(new FileInputStream(file), charset);
            return this.getReader().read(xmlReader);
        }
        catch (JAXBRuntimeException ex) {
            final Throwable cause = ex.getCause();
            throw new DocumentException(cause.getMessage(), cause);
        }
        catch (FileNotFoundException ex2) {
            throw new DocumentException(ex2.getMessage(), ex2);
        }
    }
    
    public Document read(final InputSource source) throws DocumentException {
        try {
            return this.getReader().read(source);
        }
        catch (JAXBRuntimeException ex) {
            final Throwable cause = ex.getCause();
            throw new DocumentException(cause.getMessage(), cause);
        }
    }
    
    public Document read(final InputStream source) throws DocumentException {
        try {
            return this.getReader().read(source);
        }
        catch (JAXBRuntimeException ex) {
            final Throwable cause = ex.getCause();
            throw new DocumentException(cause.getMessage(), cause);
        }
    }
    
    public Document read(final InputStream source, final String systemId) throws DocumentException {
        try {
            return this.getReader().read(source);
        }
        catch (JAXBRuntimeException ex) {
            final Throwable cause = ex.getCause();
            throw new DocumentException(cause.getMessage(), cause);
        }
    }
    
    public Document read(final Reader source) throws DocumentException {
        try {
            return this.getReader().read(source);
        }
        catch (JAXBRuntimeException ex) {
            final Throwable cause = ex.getCause();
            throw new DocumentException(cause.getMessage(), cause);
        }
    }
    
    public Document read(final Reader source, final String systemId) throws DocumentException {
        try {
            return this.getReader().read(source);
        }
        catch (JAXBRuntimeException ex) {
            final Throwable cause = ex.getCause();
            throw new DocumentException(cause.getMessage(), cause);
        }
    }
    
    public Document read(final String source) throws DocumentException {
        try {
            return this.getReader().read(source);
        }
        catch (JAXBRuntimeException ex) {
            final Throwable cause = ex.getCause();
            throw new DocumentException(cause.getMessage(), cause);
        }
    }
    
    public Document read(final URL source) throws DocumentException {
        try {
            return this.getReader().read(source);
        }
        catch (JAXBRuntimeException ex) {
            final Throwable cause = ex.getCause();
            throw new DocumentException(cause.getMessage(), cause);
        }
    }
    
    public void addObjectHandler(final String path, final JAXBObjectHandler handler) {
        final ElementHandler eHandler = new UnmarshalElementHandler(this, handler);
        this.getReader().addHandler(path, eHandler);
    }
    
    public void removeObjectHandler(final String path) {
        this.getReader().removeHandler(path);
    }
    
    public void addHandler(final String path, final ElementHandler handler) {
        this.getReader().addHandler(path, handler);
    }
    
    public void removeHandler(final String path) {
        this.getReader().removeHandler(path);
    }
    
    public void resetHandlers() {
        this.getReader().resetHandlers();
    }
    
    public boolean isPruneElements() {
        return this.pruneElements;
    }
    
    public void setPruneElements(final boolean pruneElements) {
        this.pruneElements = pruneElements;
        if (pruneElements) {
            this.getReader().setDefaultHandler(new PruningElementHandler());
        }
    }
    
    private SAXReader getReader() {
        if (this.reader == null) {
            this.reader = new SAXReader();
        }
        return this.reader;
    }
    
    private class UnmarshalElementHandler implements ElementHandler
    {
        private JAXBReader jaxbReader;
        private JAXBObjectHandler handler;
        
        public UnmarshalElementHandler(final JAXBReader documentReader, final JAXBObjectHandler handler) {
            this.jaxbReader = documentReader;
            this.handler = handler;
        }
        
        public void onStart(final ElementPath elementPath) {
        }
        
        public void onEnd(final ElementPath elementPath) {
            try {
                final Element elem = elementPath.getCurrent();
                final javax.xml.bind.Element jaxbObject = this.jaxbReader.unmarshal(elem);
                if (this.jaxbReader.isPruneElements()) {
                    elem.detach();
                }
                this.handler.handleObject(jaxbObject);
            }
            catch (Exception ex) {
                throw new JAXBRuntimeException(ex);
            }
        }
    }
    
    private class PruningElementHandler implements ElementHandler
    {
        public void onStart(final ElementPath parm1) {
        }
        
        public void onEnd(final ElementPath elementPath) {
            Element elem = elementPath.getCurrent();
            elem.detach();
            elem = null;
        }
    }
}
