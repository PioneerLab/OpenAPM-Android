// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.datatype;

import org.relaxng.datatype.DatatypeException;
import com.newrelic.org.dom4j.Node;
import com.newrelic.org.dom4j.Element;
import com.sun.msv.datatype.DatabindableDatatype;
import com.newrelic.org.dom4j.Namespace;
import com.newrelic.org.dom4j.QName;
import com.sun.msv.datatype.xsd.XSDatatype;
import org.relaxng.datatype.ValidationContext;
import com.sun.msv.datatype.SerializationContext;
import com.newrelic.org.dom4j.tree.DefaultElement;

public class DatatypeElement extends DefaultElement implements SerializationContext, ValidationContext
{
    private XSDatatype datatype;
    private Object data;
    
    public DatatypeElement(final QName qname, final XSDatatype datatype) {
        super(qname);
        this.datatype = datatype;
    }
    
    public DatatypeElement(final QName qname, final int attributeCount, final XSDatatype type) {
        super(qname, attributeCount);
        this.datatype = type;
    }
    
    public String toString() {
        return this.getClass().getName() + this.hashCode() + " [Element: <" + this.getQualifiedName() + " attributes: " + this.attributeList() + " data: " + this.getData() + " />]";
    }
    
    public XSDatatype getXSDatatype() {
        return this.datatype;
    }
    
    public String getNamespacePrefix(final String uri) {
        final Namespace namespace = this.getNamespaceForURI(uri);
        return (namespace != null) ? namespace.getPrefix() : null;
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
        final Namespace namespace = this.getNamespaceForPrefix(prefix);
        if (namespace != null) {
            return namespace.getURI();
        }
        return null;
    }
    
    public Object getData() {
        if (this.data == null) {
            final String text = this.getTextTrim();
            if (text != null && text.length() > 0) {
                if (this.datatype instanceof DatabindableDatatype) {
                    final DatabindableDatatype bind = (DatabindableDatatype)this.datatype;
                    this.data = bind.createJavaObject(text, (ValidationContext)this);
                }
                else {
                    this.data = this.datatype.createValue(text, (ValidationContext)this);
                }
            }
        }
        return this.data;
    }
    
    public void setData(final Object data) {
        final String s = this.datatype.convertToLexicalValue(data, (SerializationContext)this);
        this.validate(s);
        this.data = data;
        this.setText(s);
    }
    
    public Element addText(final String text) {
        this.validate(text);
        return super.addText(text);
    }
    
    public void setText(final String text) {
        this.validate(text);
        super.setText(text);
    }
    
    protected void childAdded(final Node node) {
        this.data = null;
        super.childAdded(node);
    }
    
    protected void childRemoved(final Node node) {
        this.data = null;
        super.childRemoved(node);
    }
    
    protected void validate(final String text) throws IllegalArgumentException {
        try {
            this.datatype.checkValid(text, (ValidationContext)this);
        }
        catch (DatatypeException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }
}
