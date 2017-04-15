// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.datatype;

import java.util.Iterator;
import com.newrelic.org.dom4j.Element;
import com.sun.msv.datatype.xsd.XSDatatype;
import com.newrelic.org.dom4j.QName;
import java.util.HashMap;
import com.newrelic.org.dom4j.DocumentFactory;
import java.util.Map;

class NamedTypeResolver
{
    protected Map complexTypeMap;
    protected Map simpleTypeMap;
    protected Map typedElementMap;
    protected Map elementFactoryMap;
    protected DocumentFactory documentFactory;
    
    NamedTypeResolver(final DocumentFactory documentFactory) {
        this.complexTypeMap = new HashMap();
        this.simpleTypeMap = new HashMap();
        this.typedElementMap = new HashMap();
        this.elementFactoryMap = new HashMap();
        this.documentFactory = documentFactory;
    }
    
    void registerComplexType(final QName type, final DocumentFactory factory) {
        this.complexTypeMap.put(type, factory);
    }
    
    void registerSimpleType(final QName type, final XSDatatype datatype) {
        this.simpleTypeMap.put(type, datatype);
    }
    
    void registerTypedElement(final Element element, final QName type, final DocumentFactory parentFactory) {
        this.typedElementMap.put(element, type);
        this.elementFactoryMap.put(element, parentFactory);
    }
    
    void resolveElementTypes() {
        for (final Element element : this.typedElementMap.keySet()) {
            final QName elementQName = this.getQNameOfSchemaElement(element);
            final QName type = this.typedElementMap.get(element);
            if (this.complexTypeMap.containsKey(type)) {
                final DocumentFactory factory = this.complexTypeMap.get(type);
                elementQName.setDocumentFactory(factory);
            }
            else {
                if (!this.simpleTypeMap.containsKey(type)) {
                    continue;
                }
                final XSDatatype datatype = this.simpleTypeMap.get(type);
                final DocumentFactory factory2 = this.elementFactoryMap.get(element);
                if (!(factory2 instanceof DatatypeElementFactory)) {
                    continue;
                }
                ((DatatypeElementFactory)factory2).setChildElementXSDatatype(elementQName, datatype);
            }
        }
    }
    
    void resolveNamedTypes() {
        this.resolveElementTypes();
    }
    
    private QName getQNameOfSchemaElement(final Element element) {
        final String name = element.attributeValue("name");
        return this.getQName(name);
    }
    
    private QName getQName(final String name) {
        return this.documentFactory.createQName(name);
    }
}
