// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.datatype;

import com.sun.msv.datatype.xsd.DatatypeFactory;
import org.relaxng.datatype.ValidationContext;
import org.relaxng.datatype.DatatypeException;
import com.newrelic.org.dom4j.util.AttributeHelper;
import com.sun.msv.datatype.xsd.TypeIncubator;
import com.newrelic.org.dom4j.Attribute;
import com.sun.msv.datatype.xsd.XSDatatype;
import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;
import java.util.Iterator;
import com.newrelic.org.dom4j.io.SAXReader;
import com.newrelic.org.dom4j.Element;
import com.newrelic.org.dom4j.Document;
import com.newrelic.org.dom4j.DocumentFactory;
import java.util.HashMap;
import java.util.Map;
import com.newrelic.org.dom4j.QName;
import com.newrelic.org.dom4j.Namespace;

public class SchemaParser
{
    private static final Namespace XSD_NAMESPACE;
    private static final QName XSD_ELEMENT;
    private static final QName XSD_ATTRIBUTE;
    private static final QName XSD_SIMPLETYPE;
    private static final QName XSD_COMPLEXTYPE;
    private static final QName XSD_RESTRICTION;
    private static final QName XSD_SEQUENCE;
    private static final QName XSD_CHOICE;
    private static final QName XSD_ALL;
    private static final QName XSD_INCLUDE;
    private DatatypeDocumentFactory documentFactory;
    private Map dataTypeCache;
    private NamedTypeResolver namedTypeResolver;
    private Namespace targetNamespace;
    
    public SchemaParser() {
        this(DatatypeDocumentFactory.singleton);
    }
    
    public SchemaParser(final DatatypeDocumentFactory documentFactory) {
        this.dataTypeCache = new HashMap();
        this.documentFactory = documentFactory;
        this.namedTypeResolver = new NamedTypeResolver(documentFactory);
    }
    
    public void build(final Document schemaDocument) {
        this.targetNamespace = null;
        this.internalBuild(schemaDocument);
    }
    
    public void build(final Document schemaDocument, final Namespace namespace) {
        this.targetNamespace = namespace;
        this.internalBuild(schemaDocument);
    }
    
    private synchronized void internalBuild(final Document schemaDocument) {
        final Element root = schemaDocument.getRootElement();
        if (root != null) {
            final Iterator includeIter = root.elementIterator(SchemaParser.XSD_INCLUDE);
            while (includeIter.hasNext()) {
                final Element includeElement = includeIter.next();
                final String inclSchemaInstanceURI = includeElement.attributeValue("schemaLocation");
                final EntityResolver resolver = schemaDocument.getEntityResolver();
                try {
                    if (resolver == null) {
                        final String msg = "No EntityResolver available";
                        throw new InvalidSchemaException(msg);
                    }
                    final InputSource inputSource = resolver.resolveEntity(null, inclSchemaInstanceURI);
                    if (inputSource == null) {
                        final String msg2 = "Could not resolve the schema URI: " + inclSchemaInstanceURI;
                        throw new InvalidSchemaException(msg2);
                    }
                    final SAXReader reader = new SAXReader();
                    final Document inclSchemaDocument = reader.read(inputSource);
                    this.build(inclSchemaDocument);
                }
                catch (Exception e) {
                    System.out.println("Failed to load schema: " + inclSchemaInstanceURI);
                    System.out.println("Caught: " + e);
                    e.printStackTrace();
                    throw new InvalidSchemaException("Failed to load schema: " + inclSchemaInstanceURI);
                }
            }
            Iterator iter = root.elementIterator(SchemaParser.XSD_ELEMENT);
            while (iter.hasNext()) {
                this.onDatatypeElement(iter.next(), this.documentFactory);
            }
            iter = root.elementIterator(SchemaParser.XSD_SIMPLETYPE);
            while (iter.hasNext()) {
                this.onNamedSchemaSimpleType(iter.next());
            }
            iter = root.elementIterator(SchemaParser.XSD_COMPLEXTYPE);
            while (iter.hasNext()) {
                this.onNamedSchemaComplexType(iter.next());
            }
            this.namedTypeResolver.resolveNamedTypes();
        }
    }
    
    private void onDatatypeElement(final Element xsdElement, final DocumentFactory parentFactory) {
        final String name = xsdElement.attributeValue("name");
        final String type = xsdElement.attributeValue("type");
        final QName qname = this.getQName(name);
        final DatatypeElementFactory factory = this.getDatatypeElementFactory(qname);
        if (type != null) {
            final XSDatatype dataType = this.getTypeByName(type);
            if (dataType != null) {
                factory.setChildElementXSDatatype(qname, dataType);
            }
            else {
                final QName typeQName = this.getQName(type);
                this.namedTypeResolver.registerTypedElement(xsdElement, typeQName, parentFactory);
            }
            return;
        }
        final Element xsdSimpleType = xsdElement.element(SchemaParser.XSD_SIMPLETYPE);
        if (xsdSimpleType != null) {
            final XSDatatype dataType2 = this.loadXSDatatypeFromSimpleType(xsdSimpleType);
            if (dataType2 != null) {
                factory.setChildElementXSDatatype(qname, dataType2);
            }
        }
        final Element schemaComplexType = xsdElement.element(SchemaParser.XSD_COMPLEXTYPE);
        if (schemaComplexType != null) {
            this.onSchemaComplexType(schemaComplexType, factory);
        }
        final Iterator iter = xsdElement.elementIterator(SchemaParser.XSD_ATTRIBUTE);
        if (iter.hasNext()) {
            do {
                this.onDatatypeAttribute(xsdElement, factory, iter.next());
            } while (iter.hasNext());
        }
    }
    
    private void onNamedSchemaComplexType(final Element schemaComplexType) {
        final Attribute nameAttr = schemaComplexType.attribute("name");
        if (nameAttr == null) {
            return;
        }
        final String name = nameAttr.getText();
        final QName qname = this.getQName(name);
        final DatatypeElementFactory factory = this.getDatatypeElementFactory(qname);
        this.onSchemaComplexType(schemaComplexType, factory);
        this.namedTypeResolver.registerComplexType(qname, factory);
    }
    
    private void onSchemaComplexType(final Element schemaComplexType, final DatatypeElementFactory elementFactory) {
        final Iterator iter = schemaComplexType.elementIterator(SchemaParser.XSD_ATTRIBUTE);
        while (iter.hasNext()) {
            final Element xsdAttribute = iter.next();
            final String name = xsdAttribute.attributeValue("name");
            final QName qname = this.getQName(name);
            final XSDatatype dataType = this.dataTypeForXsdAttribute(xsdAttribute);
            if (dataType != null) {
                elementFactory.setAttributeXSDatatype(qname, dataType);
            }
        }
        final Element schemaSequence = schemaComplexType.element(SchemaParser.XSD_SEQUENCE);
        if (schemaSequence != null) {
            this.onChildElements(schemaSequence, elementFactory);
        }
        final Element schemaChoice = schemaComplexType.element(SchemaParser.XSD_CHOICE);
        if (schemaChoice != null) {
            this.onChildElements(schemaChoice, elementFactory);
        }
        final Element schemaAll = schemaComplexType.element(SchemaParser.XSD_ALL);
        if (schemaAll != null) {
            this.onChildElements(schemaAll, elementFactory);
        }
    }
    
    private void onChildElements(final Element element, final DatatypeElementFactory fact) {
        final Iterator iter = element.elementIterator(SchemaParser.XSD_ELEMENT);
        while (iter.hasNext()) {
            final Element xsdElement = iter.next();
            this.onDatatypeElement(xsdElement, fact);
        }
    }
    
    private void onDatatypeAttribute(final Element xsdElement, final DatatypeElementFactory elementFactory, final Element xsdAttribute) {
        final String name = xsdAttribute.attributeValue("name");
        final QName qname = this.getQName(name);
        final XSDatatype dataType = this.dataTypeForXsdAttribute(xsdAttribute);
        if (dataType != null) {
            elementFactory.setAttributeXSDatatype(qname, dataType);
        }
        else {
            final String type = xsdAttribute.attributeValue("type");
            System.out.println("Warning: Couldn't find XSDatatype for type: " + type + " attribute: " + name);
        }
    }
    
    private XSDatatype dataTypeForXsdAttribute(final Element xsdAttribute) {
        final String type = xsdAttribute.attributeValue("type");
        XSDatatype dataType = null;
        if (type != null) {
            dataType = this.getTypeByName(type);
        }
        else {
            final Element xsdSimpleType = xsdAttribute.element(SchemaParser.XSD_SIMPLETYPE);
            if (xsdSimpleType == null) {
                final String name = xsdAttribute.attributeValue("name");
                final String msg = "The attribute: " + name + " has no type attribute and does not contain a " + "<simpleType/> element";
                throw new InvalidSchemaException(msg);
            }
            dataType = this.loadXSDatatypeFromSimpleType(xsdSimpleType);
        }
        return dataType;
    }
    
    private void onNamedSchemaSimpleType(final Element schemaSimpleType) {
        final Attribute nameAttr = schemaSimpleType.attribute("name");
        if (nameAttr == null) {
            return;
        }
        final String name = nameAttr.getText();
        final QName qname = this.getQName(name);
        final XSDatatype datatype = this.loadXSDatatypeFromSimpleType(schemaSimpleType);
        this.namedTypeResolver.registerSimpleType(qname, datatype);
    }
    
    private XSDatatype loadXSDatatypeFromSimpleType(final Element xsdSimpleType) {
        final Element xsdRestriction = xsdSimpleType.element(SchemaParser.XSD_RESTRICTION);
        if (xsdRestriction != null) {
            final String base = xsdRestriction.attributeValue("base");
            if (base != null) {
                final XSDatatype baseType = this.getTypeByName(base);
                if (baseType != null) {
                    return this.deriveSimpleType(baseType, xsdRestriction);
                }
                this.onSchemaError("Invalid base type: " + base + " when trying to build restriction: " + xsdRestriction);
            }
            else {
                final Element xsdSubType = xsdSimpleType.element(SchemaParser.XSD_SIMPLETYPE);
                if (xsdSubType != null) {
                    return this.loadXSDatatypeFromSimpleType(xsdSubType);
                }
                final String msg = "The simpleType element: " + xsdSimpleType + " must contain a base attribute or simpleType" + " element";
                this.onSchemaError(msg);
            }
        }
        else {
            this.onSchemaError("No <restriction>. Could not create XSDatatype for simpleType: " + xsdSimpleType);
        }
        return null;
    }
    
    private XSDatatype deriveSimpleType(final XSDatatype baseType, final Element xsdRestriction) {
        final TypeIncubator incubator = new TypeIncubator(baseType);
        final ValidationContext context = null;
        try {
            final Iterator iter = xsdRestriction.elementIterator();
            while (iter.hasNext()) {
                final Element element = iter.next();
                final String name = element.getName();
                final String value = element.attributeValue("value");
                final boolean fixed = AttributeHelper.booleanValue(element, "fixed");
                incubator.addFacet(name, value, fixed, context);
            }
            final String newTypeName = null;
            return (XSDatatype)incubator.derive("", newTypeName);
        }
        catch (DatatypeException e) {
            this.onSchemaError("Invalid restriction: " + e.getMessage() + " when trying to build restriction: " + xsdRestriction);
            return null;
        }
    }
    
    private DatatypeElementFactory getDatatypeElementFactory(final QName name) {
        DatatypeElementFactory factory = this.documentFactory.getElementFactory(name);
        if (factory == null) {
            factory = new DatatypeElementFactory(name);
            name.setDocumentFactory(factory);
        }
        return factory;
    }
    
    private XSDatatype getTypeByName(final String type) {
        XSDatatype dataType = this.dataTypeCache.get(type);
        if (dataType == null) {
            final int idx = type.indexOf(58);
            if (idx >= 0) {
                final String localName = type.substring(idx + 1);
                try {
                    dataType = DatatypeFactory.getTypeByName(localName);
                }
                catch (DatatypeException ex) {}
            }
            if (dataType == null) {
                try {
                    dataType = DatatypeFactory.getTypeByName(type);
                }
                catch (DatatypeException ex2) {}
            }
            if (dataType == null) {
                final QName typeQName = this.getQName(type);
                dataType = this.namedTypeResolver.simpleTypeMap.get(typeQName);
            }
            if (dataType != null) {
                this.dataTypeCache.put(type, dataType);
            }
        }
        return dataType;
    }
    
    private QName getQName(final String name) {
        if (this.targetNamespace == null) {
            return this.documentFactory.createQName(name);
        }
        return this.documentFactory.createQName(name, this.targetNamespace);
    }
    
    private void onSchemaError(final String message) {
        throw new InvalidSchemaException(message);
    }
    
    static {
        XSD_NAMESPACE = Namespace.get("xsd", "http://www.w3.org/2001/XMLSchema");
        XSD_ELEMENT = QName.get("element", SchemaParser.XSD_NAMESPACE);
        XSD_ATTRIBUTE = QName.get("attribute", SchemaParser.XSD_NAMESPACE);
        XSD_SIMPLETYPE = QName.get("simpleType", SchemaParser.XSD_NAMESPACE);
        XSD_COMPLEXTYPE = QName.get("complexType", SchemaParser.XSD_NAMESPACE);
        XSD_RESTRICTION = QName.get("restriction", SchemaParser.XSD_NAMESPACE);
        XSD_SEQUENCE = QName.get("sequence", SchemaParser.XSD_NAMESPACE);
        XSD_CHOICE = QName.get("choice", SchemaParser.XSD_NAMESPACE);
        XSD_ALL = QName.get("all", SchemaParser.XSD_NAMESPACE);
        XSD_INCLUDE = QName.get("include", SchemaParser.XSD_NAMESPACE);
    }
}
