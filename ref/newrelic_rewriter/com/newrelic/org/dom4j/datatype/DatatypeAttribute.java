// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.datatype;

import com.sun.msv.datatype.DatabindableDatatype;
import org.relaxng.datatype.DatatypeException;
import com.newrelic.org.dom4j.Namespace;
import com.sun.msv.datatype.xsd.XSDatatype;
import com.newrelic.org.dom4j.QName;
import com.newrelic.org.dom4j.Element;
import org.relaxng.datatype.ValidationContext;
import com.sun.msv.datatype.SerializationContext;
import com.newrelic.org.dom4j.tree.AbstractAttribute;

public class DatatypeAttribute extends AbstractAttribute implements SerializationContext, ValidationContext
{
    private Element parent;
    private QName qname;
    private XSDatatype datatype;
    private Object data;
    private String text;
    
    public DatatypeAttribute(final QName qname, final XSDatatype datatype) {
        this.qname = qname;
        this.datatype = datatype;
    }
    
    public DatatypeAttribute(final QName qname, final XSDatatype datatype, final String text) {
        this.qname = qname;
        this.datatype = datatype;
        this.text = text;
        this.data = this.convertToValue(text);
    }
    
    public String toString() {
        return this.getClass().getName() + this.hashCode() + " [Attribute: name " + this.getQualifiedName() + " value \"" + this.getValue() + "\" data: " + this.getData() + "]";
    }
    
    public XSDatatype getXSDatatype() {
        return this.datatype;
    }
    
    public String getNamespacePrefix(final String uri) {
        final Element parentElement = this.getParent();
        if (parentElement != null) {
            final Namespace namespace = parentElement.getNamespaceForURI(uri);
            if (namespace != null) {
                return namespace.getPrefix();
            }
        }
        return null;
    }
    
    public String getBaseUri() {
        return null;
    }
    
    public boolean isNotation(final String notationName) {
        return false;
    }
    
    public boolean isUnparsedEntity(final String entityName) {
        return true;
    }
    
    public String resolveNamespacePrefix(final String prefix) {
        if (prefix.equals(this.getNamespacePrefix())) {
            return this.getNamespaceURI();
        }
        final Element parentElement = this.getParent();
        if (parentElement != null) {
            final Namespace namespace = parentElement.getNamespaceForPrefix(prefix);
            if (namespace != null) {
                return namespace.getURI();
            }
        }
        return null;
    }
    
    public QName getQName() {
        return this.qname;
    }
    
    public String getValue() {
        return this.text;
    }
    
    public void setValue(final String value) {
        this.validate(value);
        this.text = value;
        this.data = this.convertToValue(value);
    }
    
    public Object getData() {
        return this.data;
    }
    
    public void setData(final Object data) {
        final String s = this.datatype.convertToLexicalValue(data, (SerializationContext)this);
        this.validate(s);
        this.text = s;
        this.data = data;
    }
    
    public Element getParent() {
        return this.parent;
    }
    
    public void setParent(final Element parent) {
        this.parent = parent;
    }
    
    public boolean supportsParent() {
        return true;
    }
    
    public boolean isReadOnly() {
        return false;
    }
    
    protected void validate(final String txt) throws IllegalArgumentException {
        try {
            this.datatype.checkValid(txt, (ValidationContext)this);
        }
        catch (DatatypeException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
    
    protected Object convertToValue(final String txt) {
        if (this.datatype instanceof DatabindableDatatype) {
            final DatabindableDatatype bindable = (DatabindableDatatype)this.datatype;
            return bindable.createJavaObject(txt, (ValidationContext)this);
        }
        return this.datatype.createValue(txt, (ValidationContext)this);
    }
}
