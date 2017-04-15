// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.datatype;

import com.newrelic.org.dom4j.Attribute;
import com.newrelic.org.dom4j.Element;
import com.sun.msv.datatype.xsd.XSDatatype;
import java.util.HashMap;
import java.util.Map;
import com.newrelic.org.dom4j.QName;
import com.newrelic.org.dom4j.DocumentFactory;

public class DatatypeElementFactory extends DocumentFactory
{
    private QName elementQName;
    private Map attributeXSDatatypes;
    private Map childrenXSDatatypes;
    
    public DatatypeElementFactory(final QName elementQName) {
        this.attributeXSDatatypes = new HashMap();
        this.childrenXSDatatypes = new HashMap();
        this.elementQName = elementQName;
    }
    
    public QName getQName() {
        return this.elementQName;
    }
    
    public XSDatatype getAttributeXSDatatype(final QName attributeQName) {
        return this.attributeXSDatatypes.get(attributeQName);
    }
    
    public void setAttributeXSDatatype(final QName attributeQName, final XSDatatype type) {
        this.attributeXSDatatypes.put(attributeQName, type);
    }
    
    public XSDatatype getChildElementXSDatatype(final QName qname) {
        return this.childrenXSDatatypes.get(qname);
    }
    
    public void setChildElementXSDatatype(final QName qname, final XSDatatype dataType) {
        this.childrenXSDatatypes.put(qname, dataType);
    }
    
    public Element createElement(final QName qname) {
        XSDatatype dataType = this.getChildElementXSDatatype(qname);
        if (dataType != null) {
            return new DatatypeElement(qname, dataType);
        }
        final DocumentFactory factory = qname.getDocumentFactory();
        if (factory instanceof DatatypeElementFactory) {
            final DatatypeElementFactory dtFactory = (DatatypeElementFactory)factory;
            dataType = dtFactory.getChildElementXSDatatype(qname);
            if (dataType != null) {
                return new DatatypeElement(qname, dataType);
            }
        }
        return super.createElement(qname);
    }
    
    public Attribute createAttribute(final Element owner, final QName qname, final String value) {
        final XSDatatype dataType = this.getAttributeXSDatatype(qname);
        if (dataType == null) {
            return super.createAttribute(owner, qname, value);
        }
        return new DatatypeAttribute(qname, dataType, value);
    }
}
