// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.io;

import java.io.Serializable;
import com.newrelic.org.dom4j.ElementHandler;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.ContentHandler;
import java.io.Reader;
import java.net.URL;
import java.io.FileNotFoundException;
import com.newrelic.org.dom4j.DocumentException;
import java.io.InputStream;
import org.xml.sax.InputSource;
import java.io.FileInputStream;
import com.newrelic.org.dom4j.Document;
import java.io.File;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.XMLFilter;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.XMLReader;
import com.newrelic.org.dom4j.DocumentFactory;

public class SAXReader
{
    private static final String SAX_STRING_INTERNING = "http://xml.org/sax/features/string-interning";
    private static final String SAX_NAMESPACE_PREFIXES = "http://xml.org/sax/features/namespace-prefixes";
    private static final String SAX_NAMESPACES = "http://xml.org/sax/features/namespaces";
    private static final String SAX_DECL_HANDLER = "http://xml.org/sax/properties/declaration-handler";
    private static final String SAX_LEXICAL_HANDLER = "http://xml.org/sax/properties/lexical-handler";
    private static final String SAX_LEXICALHANDLER = "http://xml.org/sax/handlers/LexicalHandler";
    private DocumentFactory factory;
    private XMLReader xmlReader;
    private boolean validating;
    private DispatchHandler dispatchHandler;
    private ErrorHandler errorHandler;
    private EntityResolver entityResolver;
    private boolean stringInternEnabled;
    private boolean includeInternalDTDDeclarations;
    private boolean includeExternalDTDDeclarations;
    private boolean mergeAdjacentText;
    private boolean stripWhitespaceText;
    private boolean ignoreComments;
    private String encoding;
    private XMLFilter xmlFilter;
    
    public SAXReader() {
        this.stringInternEnabled = true;
        this.includeInternalDTDDeclarations = false;
        this.includeExternalDTDDeclarations = false;
        this.mergeAdjacentText = false;
        this.stripWhitespaceText = false;
        this.ignoreComments = false;
        this.encoding = null;
    }
    
    public SAXReader(final boolean validating) {
        this.stringInternEnabled = true;
        this.includeInternalDTDDeclarations = false;
        this.includeExternalDTDDeclarations = false;
        this.mergeAdjacentText = false;
        this.stripWhitespaceText = false;
        this.ignoreComments = false;
        this.encoding = null;
        this.validating = validating;
    }
    
    public SAXReader(final DocumentFactory factory) {
        this.stringInternEnabled = true;
        this.includeInternalDTDDeclarations = false;
        this.includeExternalDTDDeclarations = false;
        this.mergeAdjacentText = false;
        this.stripWhitespaceText = false;
        this.ignoreComments = false;
        this.encoding = null;
        this.factory = factory;
    }
    
    public SAXReader(final DocumentFactory factory, final boolean validating) {
        this.stringInternEnabled = true;
        this.includeInternalDTDDeclarations = false;
        this.includeExternalDTDDeclarations = false;
        this.mergeAdjacentText = false;
        this.stripWhitespaceText = false;
        this.ignoreComments = false;
        this.encoding = null;
        this.factory = factory;
        this.validating = validating;
    }
    
    public SAXReader(final XMLReader xmlReader) {
        this.stringInternEnabled = true;
        this.includeInternalDTDDeclarations = false;
        this.includeExternalDTDDeclarations = false;
        this.mergeAdjacentText = false;
        this.stripWhitespaceText = false;
        this.ignoreComments = false;
        this.encoding = null;
        this.xmlReader = xmlReader;
    }
    
    public SAXReader(final XMLReader xmlReader, final boolean validating) {
        this.stringInternEnabled = true;
        this.includeInternalDTDDeclarations = false;
        this.includeExternalDTDDeclarations = false;
        this.mergeAdjacentText = false;
        this.stripWhitespaceText = false;
        this.ignoreComments = false;
        this.encoding = null;
        this.xmlReader = xmlReader;
        this.validating = validating;
    }
    
    public SAXReader(final String xmlReaderClassName) throws SAXException {
        this.stringInternEnabled = true;
        this.includeInternalDTDDeclarations = false;
        this.includeExternalDTDDeclarations = false;
        this.mergeAdjacentText = false;
        this.stripWhitespaceText = false;
        this.ignoreComments = false;
        this.encoding = null;
        if (xmlReaderClassName != null) {
            this.xmlReader = XMLReaderFactory.createXMLReader(xmlReaderClassName);
        }
    }
    
    public SAXReader(final String xmlReaderClassName, final boolean validating) throws SAXException {
        this.stringInternEnabled = true;
        this.includeInternalDTDDeclarations = false;
        this.includeExternalDTDDeclarations = false;
        this.mergeAdjacentText = false;
        this.stripWhitespaceText = false;
        this.ignoreComments = false;
        this.encoding = null;
        if (xmlReaderClassName != null) {
            this.xmlReader = XMLReaderFactory.createXMLReader(xmlReaderClassName);
        }
        this.validating = validating;
    }
    
    public void setProperty(final String name, final Object value) throws SAXException {
        this.getXMLReader().setProperty(name, value);
    }
    
    public void setFeature(final String name, final boolean value) throws SAXException {
        this.getXMLReader().setFeature(name, value);
    }
    
    public Document read(final File file) throws DocumentException {
        try {
            final InputSource source = new InputSource(new FileInputStream(file));
            if (this.encoding != null) {
                source.setEncoding(this.encoding);
            }
            String path = file.getAbsolutePath();
            if (path != null) {
                final StringBuffer sb = new StringBuffer("file://");
                if (!path.startsWith(File.separator)) {
                    sb.append("/");
                }
                path = path.replace('\\', '/');
                sb.append(path);
                source.setSystemId(sb.toString());
            }
            return this.read(source);
        }
        catch (FileNotFoundException e) {
            throw new DocumentException(e.getMessage(), e);
        }
    }
    
    public Document read(final URL url) throws DocumentException {
        final String systemID = url.toExternalForm();
        final InputSource source = new InputSource(systemID);
        if (this.encoding != null) {
            source.setEncoding(this.encoding);
        }
        return this.read(source);
    }
    
    public Document read(final String systemId) throws DocumentException {
        final InputSource source = new InputSource(systemId);
        if (this.encoding != null) {
            source.setEncoding(this.encoding);
        }
        return this.read(source);
    }
    
    public Document read(final InputStream in) throws DocumentException {
        final InputSource source = new InputSource(in);
        if (this.encoding != null) {
            source.setEncoding(this.encoding);
        }
        return this.read(source);
    }
    
    public Document read(final Reader reader) throws DocumentException {
        final InputSource source = new InputSource(reader);
        if (this.encoding != null) {
            source.setEncoding(this.encoding);
        }
        return this.read(source);
    }
    
    public Document read(final InputStream in, final String systemId) throws DocumentException {
        final InputSource source = new InputSource(in);
        source.setSystemId(systemId);
        if (this.encoding != null) {
            source.setEncoding(this.encoding);
        }
        return this.read(source);
    }
    
    public Document read(final Reader reader, final String systemId) throws DocumentException {
        final InputSource source = new InputSource(reader);
        source.setSystemId(systemId);
        if (this.encoding != null) {
            source.setEncoding(this.encoding);
        }
        return this.read(source);
    }
    
    public Document read(final InputSource in) throws DocumentException {
        try {
            XMLReader reader = this.getXMLReader();
            reader = this.installXMLFilter(reader);
            EntityResolver thatEntityResolver = this.entityResolver;
            if (thatEntityResolver == null) {
                thatEntityResolver = this.createDefaultEntityResolver(in.getSystemId());
                this.entityResolver = thatEntityResolver;
            }
            reader.setEntityResolver(thatEntityResolver);
            final SAXContentHandler contentHandler = this.createContentHandler(reader);
            contentHandler.setEntityResolver(thatEntityResolver);
            contentHandler.setInputSource(in);
            final boolean internal = this.isIncludeInternalDTDDeclarations();
            final boolean external = this.isIncludeExternalDTDDeclarations();
            contentHandler.setIncludeInternalDTDDeclarations(internal);
            contentHandler.setIncludeExternalDTDDeclarations(external);
            contentHandler.setMergeAdjacentText(this.isMergeAdjacentText());
            contentHandler.setStripWhitespaceText(this.isStripWhitespaceText());
            contentHandler.setIgnoreComments(this.isIgnoreComments());
            reader.setContentHandler(contentHandler);
            this.configureReader(reader, contentHandler);
            reader.parse(in);
            return contentHandler.getDocument();
        }
        catch (Exception e) {
            if (e instanceof SAXParseException) {
                final SAXParseException parseException = (SAXParseException)e;
                String systemId = parseException.getSystemId();
                if (systemId == null) {
                    systemId = "";
                }
                final String message = "Error on line " + parseException.getLineNumber() + " of document " + systemId + " : " + parseException.getMessage();
                throw new DocumentException(message, e);
            }
            throw new DocumentException(e.getMessage(), e);
        }
    }
    
    public boolean isValidating() {
        return this.validating;
    }
    
    public void setValidation(final boolean validation) {
        this.validating = validation;
    }
    
    public boolean isIncludeInternalDTDDeclarations() {
        return this.includeInternalDTDDeclarations;
    }
    
    public void setIncludeInternalDTDDeclarations(final boolean include) {
        this.includeInternalDTDDeclarations = include;
    }
    
    public boolean isIncludeExternalDTDDeclarations() {
        return this.includeExternalDTDDeclarations;
    }
    
    public void setIncludeExternalDTDDeclarations(final boolean include) {
        this.includeExternalDTDDeclarations = include;
    }
    
    public boolean isStringInternEnabled() {
        return this.stringInternEnabled;
    }
    
    public void setStringInternEnabled(final boolean stringInternEnabled) {
        this.stringInternEnabled = stringInternEnabled;
    }
    
    public boolean isMergeAdjacentText() {
        return this.mergeAdjacentText;
    }
    
    public void setMergeAdjacentText(final boolean mergeAdjacentText) {
        this.mergeAdjacentText = mergeAdjacentText;
    }
    
    public boolean isStripWhitespaceText() {
        return this.stripWhitespaceText;
    }
    
    public void setStripWhitespaceText(final boolean stripWhitespaceText) {
        this.stripWhitespaceText = stripWhitespaceText;
    }
    
    public boolean isIgnoreComments() {
        return this.ignoreComments;
    }
    
    public void setIgnoreComments(final boolean ignoreComments) {
        this.ignoreComments = ignoreComments;
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
    
    public ErrorHandler getErrorHandler() {
        return this.errorHandler;
    }
    
    public void setErrorHandler(final ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }
    
    public EntityResolver getEntityResolver() {
        return this.entityResolver;
    }
    
    public void setEntityResolver(final EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }
    
    public XMLReader getXMLReader() throws SAXException {
        if (this.xmlReader == null) {
            this.xmlReader = this.createXMLReader();
        }
        return this.xmlReader;
    }
    
    public void setXMLReader(final XMLReader reader) {
        this.xmlReader = reader;
    }
    
    public String getEncoding() {
        return this.encoding;
    }
    
    public void setEncoding(final String encoding) {
        this.encoding = encoding;
    }
    
    public void setXMLReaderClassName(final String xmlReaderClassName) throws SAXException {
        this.setXMLReader(XMLReaderFactory.createXMLReader(xmlReaderClassName));
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
    
    public void resetHandlers() {
        this.getDispatchHandler().resetHandlers();
    }
    
    public XMLFilter getXMLFilter() {
        return this.xmlFilter;
    }
    
    public void setXMLFilter(final XMLFilter filter) {
        this.xmlFilter = filter;
    }
    
    protected XMLReader installXMLFilter(final XMLReader reader) {
        final XMLFilter filter = this.getXMLFilter();
        if (filter != null) {
            XMLFilter root = filter;
            while (true) {
                final XMLReader parent = root.getParent();
                if (!(parent instanceof XMLFilter)) {
                    break;
                }
                root = (XMLFilter)parent;
            }
            root.setParent(reader);
            return filter;
        }
        return reader;
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
    
    protected XMLReader createXMLReader() throws SAXException {
        return SAXHelper.createXMLReader(this.isValidating());
    }
    
    protected void configureReader(final XMLReader reader, final DefaultHandler handler) throws DocumentException {
        SAXHelper.setParserProperty(reader, "http://xml.org/sax/handlers/LexicalHandler", handler);
        SAXHelper.setParserProperty(reader, "http://xml.org/sax/properties/lexical-handler", handler);
        if (this.includeInternalDTDDeclarations || this.includeExternalDTDDeclarations) {
            SAXHelper.setParserProperty(reader, "http://xml.org/sax/properties/declaration-handler", handler);
        }
        SAXHelper.setParserFeature(reader, "http://xml.org/sax/features/namespaces", true);
        SAXHelper.setParserFeature(reader, "http://xml.org/sax/features/namespace-prefixes", false);
        SAXHelper.setParserFeature(reader, "http://xml.org/sax/features/string-interning", this.isStringInternEnabled());
        SAXHelper.setParserFeature(reader, "http://xml.org/sax/features/use-locator2", true);
        try {
            reader.setFeature("http://xml.org/sax/features/validation", this.isValidating());
            if (this.errorHandler != null) {
                reader.setErrorHandler(this.errorHandler);
            }
            else {
                reader.setErrorHandler(handler);
            }
        }
        catch (Exception e) {
            if (this.isValidating()) {
                throw new DocumentException("Validation not supported for XMLReader: " + reader, e);
            }
        }
    }
    
    protected SAXContentHandler createContentHandler(final XMLReader reader) {
        return new SAXContentHandler(this.getDocumentFactory(), this.dispatchHandler);
    }
    
    protected EntityResolver createDefaultEntityResolver(final String systemId) {
        String prefix = null;
        if (systemId != null && systemId.length() > 0) {
            final int idx = systemId.lastIndexOf(47);
            if (idx > 0) {
                prefix = systemId.substring(0, idx + 1);
            }
        }
        return new SAXEntityResolver(prefix);
    }
    
    protected static class SAXEntityResolver implements EntityResolver, Serializable
    {
        protected String uriPrefix;
        
        public SAXEntityResolver(final String uriPrefix) {
            this.uriPrefix = uriPrefix;
        }
        
        public InputSource resolveEntity(final String publicId, String systemId) {
            if (systemId != null && systemId.length() > 0 && this.uriPrefix != null && systemId.indexOf(58) <= 0) {
                systemId = this.uriPrefix + systemId;
            }
            return new InputSource(systemId);
        }
    }
}
