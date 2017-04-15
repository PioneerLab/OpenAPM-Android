// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.datatype;

import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;
import com.newrelic.org.dom4j.Attribute;
import com.newrelic.org.dom4j.Element;
import com.newrelic.org.dom4j.Document;
import com.newrelic.org.dom4j.io.SAXReader;
import com.newrelic.org.dom4j.QName;
import com.newrelic.org.dom4j.Namespace;
import com.newrelic.org.dom4j.DocumentFactory;

public class DatatypeDocumentFactory extends DocumentFactory
{
    private static final boolean DO_INTERN_QNAME = false;
    protected static transient DatatypeDocumentFactory singleton;
    private static final Namespace XSI_NAMESPACE;
    private static final QName XSI_SCHEMA_LOCATION;
    private static final QName XSI_NO_SCHEMA_LOCATION;
    private SchemaParser schemaBuilder;
    private SAXReader xmlSchemaReader;
    private boolean autoLoadSchema;
    
    public DatatypeDocumentFactory() {
        this.xmlSchemaReader = new SAXReader();
        this.autoLoadSchema = true;
        this.schemaBuilder = new SchemaParser(this);
    }
    
    public static DocumentFactory getInstance() {
        return DatatypeDocumentFactory.singleton;
    }
    
    public void loadSchema(final Document schemaDocument) {
        this.schemaBuilder.build(schemaDocument);
    }
    
    public void loadSchema(final Document schemaDocument, final Namespace targetNamespace) {
        this.schemaBuilder.build(schemaDocument, targetNamespace);
    }
    
    public DatatypeElementFactory getElementFactory(final QName elementQName) {
        DatatypeElementFactory result = null;
        final DocumentFactory factory = elementQName.getDocumentFactory();
        if (factory instanceof DatatypeElementFactory) {
            result = (DatatypeElementFactory)factory;
        }
        return result;
    }
    
    public Attribute createAttribute(final Element owner, final QName qname, final String value) {
        if (this.autoLoadSchema && qname.equals(DatatypeDocumentFactory.XSI_NO_SCHEMA_LOCATION)) {
            final Document document = (owner != null) ? owner.getDocument() : null;
            this.loadSchema(document, value);
        }
        else if (this.autoLoadSchema && qname.equals(DatatypeDocumentFactory.XSI_SCHEMA_LOCATION)) {
            final Document document = (owner != null) ? owner.getDocument() : null;
            final String uri = value.substring(0, value.indexOf(32));
            final Namespace namespace = owner.getNamespaceForURI(uri);
            this.loadSchema(document, value.substring(value.indexOf(32) + 1), namespace);
        }
        return super.createAttribute(owner, qname, value);
    }
    
    protected void loadSchema(final Document document, final String schemaInstanceURI) {
        try {
            final EntityResolver resolver = document.getEntityResolver();
            if (resolver == null) {
                final String msg = "No EntityResolver available for resolving URI: ";
                throw new InvalidSchemaException(msg + schemaInstanceURI);
            }
            final InputSource inputSource = resolver.resolveEntity(null, schemaInstanceURI);
            if (resolver == null) {
                throw new InvalidSchemaException("Could not resolve the URI: " + schemaInstanceURI);
            }
            final Document schemaDocument = this.xmlSchemaReader.read(inputSource);
            this.loadSchema(schemaDocument);
        }
        catch (Exception e) {
            System.out.println("Failed to load schema: " + schemaInstanceURI);
            System.out.println("Caught: " + e);
            e.printStackTrace();
            throw new InvalidSchemaException("Failed to load schema: " + schemaInstanceURI);
        }
    }
    
    protected void loadSchema(final Document document, final String schemaInstanceURI, final Namespace namespace) {
        try {
            final EntityResolver resolver = document.getEntityResolver();
            if (resolver == null) {
                final String msg = "No EntityResolver available for resolving URI: ";
                throw new InvalidSchemaException(msg + schemaInstanceURI);
            }
            final InputSource inputSource = resolver.resolveEntity(null, schemaInstanceURI);
            if (resolver == null) {
                throw new InvalidSchemaException("Could not resolve the URI: " + schemaInstanceURI);
            }
            final Document schemaDocument = this.xmlSchemaReader.read(inputSource);
            this.loadSchema(schemaDocument, namespace);
        }
        catch (Exception e) {
            System.out.println("Failed to load schema: " + schemaInstanceURI);
            System.out.println("Caught: " + e);
            e.printStackTrace();
            throw new InvalidSchemaException("Failed to load schema: " + schemaInstanceURI);
        }
    }
    
    static {
        DatatypeDocumentFactory.singleton = new DatatypeDocumentFactory();
        XSI_NAMESPACE = Namespace.get("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        XSI_SCHEMA_LOCATION = QName.get("schemaLocation", DatatypeDocumentFactory.XSI_NAMESPACE);
        XSI_NO_SCHEMA_LOCATION = QName.get("noNamespaceSchemaLocation", DatatypeDocumentFactory.XSI_NAMESPACE);
    }
}
