// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.tree;

import com.newrelic.org.dom4j.Node;
import com.newrelic.org.dom4j.Element;
import com.newrelic.org.dom4j.Visitor;
import java.io.IOException;
import java.io.Writer;
import com.newrelic.org.dom4j.Namespace;
import com.newrelic.org.dom4j.Attribute;

public abstract class AbstractAttribute extends AbstractNode implements Attribute
{
    public short getNodeType() {
        return 2;
    }
    
    public void setNamespace(final Namespace namespace) {
        final String msg = "This Attribute is read only and cannot be changed";
        throw new UnsupportedOperationException(msg);
    }
    
    public String getText() {
        return this.getValue();
    }
    
    public void setText(final String text) {
        this.setValue(text);
    }
    
    public void setValue(final String value) {
        final String msg = "This Attribute is read only and cannot be changed";
        throw new UnsupportedOperationException(msg);
    }
    
    public Object getData() {
        return this.getValue();
    }
    
    public void setData(final Object data) {
        this.setValue((data == null) ? null : data.toString());
    }
    
    public String toString() {
        return super.toString() + " [Attribute: name " + this.getQualifiedName() + " value \"" + this.getValue() + "\"]";
    }
    
    public String asXML() {
        return this.getQualifiedName() + "=\"" + this.getValue() + "\"";
    }
    
    public void write(final Writer writer) throws IOException {
        writer.write(this.getQualifiedName());
        writer.write("=\"");
        writer.write(this.getValue());
        writer.write("\"");
    }
    
    public void accept(final Visitor visitor) {
        visitor.visit(this);
    }
    
    public Namespace getNamespace() {
        return this.getQName().getNamespace();
    }
    
    public String getName() {
        return this.getQName().getName();
    }
    
    public String getNamespacePrefix() {
        return this.getQName().getNamespacePrefix();
    }
    
    public String getNamespaceURI() {
        return this.getQName().getNamespaceURI();
    }
    
    public String getQualifiedName() {
        return this.getQName().getQualifiedName();
    }
    
    public String getPath(final Element context) {
        final StringBuffer result = new StringBuffer();
        final Element parent = this.getParent();
        if (parent != null && parent != context) {
            result.append(parent.getPath(context));
            result.append("/");
        }
        result.append("@");
        final String uri = this.getNamespaceURI();
        final String prefix = this.getNamespacePrefix();
        if (uri == null || uri.length() == 0 || prefix == null || prefix.length() == 0) {
            result.append(this.getName());
        }
        else {
            result.append(this.getQualifiedName());
        }
        return result.toString();
    }
    
    public String getUniquePath(final Element context) {
        final StringBuffer result = new StringBuffer();
        final Element parent = this.getParent();
        if (parent != null && parent != context) {
            result.append(parent.getUniquePath(context));
            result.append("/");
        }
        result.append("@");
        final String uri = this.getNamespaceURI();
        final String prefix = this.getNamespacePrefix();
        if (uri == null || uri.length() == 0 || prefix == null || prefix.length() == 0) {
            result.append(this.getName());
        }
        else {
            result.append(this.getQualifiedName());
        }
        return result.toString();
    }
    
    protected Node createXPathResult(final Element parent) {
        return new DefaultAttribute(parent, this.getQName(), this.getValue());
    }
}
