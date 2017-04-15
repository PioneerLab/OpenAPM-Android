// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.tree;

import com.newrelic.org.dom4j.Visitor;
import java.io.IOException;
import java.io.Writer;
import com.newrelic.org.dom4j.Element;
import com.newrelic.org.dom4j.Entity;

public abstract class AbstractEntity extends AbstractNode implements Entity
{
    public short getNodeType() {
        return 5;
    }
    
    public String getPath(final Element context) {
        final Element parent = this.getParent();
        return (parent != null && parent != context) ? (parent.getPath(context) + "/text()") : "text()";
    }
    
    public String getUniquePath(final Element context) {
        final Element parent = this.getParent();
        return (parent != null && parent != context) ? (parent.getUniquePath(context) + "/text()") : "text()";
    }
    
    public String toString() {
        return super.toString() + " [Entity: &" + this.getName() + ";]";
    }
    
    public String getStringValue() {
        return "&" + this.getName() + ";";
    }
    
    public String asXML() {
        return "&" + this.getName() + ";";
    }
    
    public void write(final Writer writer) throws IOException {
        writer.write("&");
        writer.write(this.getName());
        writer.write(";");
    }
    
    public void accept(final Visitor visitor) {
        visitor.visit(this);
    }
}
