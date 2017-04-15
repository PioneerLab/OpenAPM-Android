// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.tree;

import com.newrelic.org.dom4j.Visitor;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import com.newrelic.org.dom4j.Element;
import com.newrelic.org.dom4j.DocumentType;

public abstract class AbstractDocumentType extends AbstractNode implements DocumentType
{
    public short getNodeType() {
        return 10;
    }
    
    public String getName() {
        return this.getElementName();
    }
    
    public void setName(final String name) {
        this.setElementName(name);
    }
    
    public String getPath(final Element context) {
        return "";
    }
    
    public String getUniquePath(final Element context) {
        return "";
    }
    
    public String getText() {
        final List list = this.getInternalDeclarations();
        if (list != null && list.size() > 0) {
            final StringBuffer buffer = new StringBuffer();
            final Iterator iter = list.iterator();
            if (iter.hasNext()) {
                Object decl = iter.next();
                buffer.append(decl.toString());
                while (iter.hasNext()) {
                    decl = iter.next();
                    buffer.append("\n");
                    buffer.append(decl.toString());
                }
            }
            return buffer.toString();
        }
        return "";
    }
    
    public String toString() {
        return super.toString() + " [DocumentType: " + this.asXML() + "]";
    }
    
    public String asXML() {
        final StringBuffer buffer = new StringBuffer("<!DOCTYPE ");
        buffer.append(this.getElementName());
        boolean hasPublicID = false;
        final String publicID = this.getPublicID();
        if (publicID != null && publicID.length() > 0) {
            buffer.append(" PUBLIC \"");
            buffer.append(publicID);
            buffer.append("\"");
            hasPublicID = true;
        }
        final String systemID = this.getSystemID();
        if (systemID != null && systemID.length() > 0) {
            if (!hasPublicID) {
                buffer.append(" SYSTEM");
            }
            buffer.append(" \"");
            buffer.append(systemID);
            buffer.append("\"");
        }
        buffer.append(">");
        return buffer.toString();
    }
    
    public void write(final Writer writer) throws IOException {
        writer.write("<!DOCTYPE ");
        writer.write(this.getElementName());
        boolean hasPublicID = false;
        final String publicID = this.getPublicID();
        if (publicID != null && publicID.length() > 0) {
            writer.write(" PUBLIC \"");
            writer.write(publicID);
            writer.write("\"");
            hasPublicID = true;
        }
        final String systemID = this.getSystemID();
        if (systemID != null && systemID.length() > 0) {
            if (!hasPublicID) {
                writer.write(" SYSTEM");
            }
            writer.write(" \"");
            writer.write(systemID);
            writer.write("\"");
        }
        final List list = this.getInternalDeclarations();
        if (list != null && list.size() > 0) {
            writer.write(" [");
            for (final Object decl : list) {
                writer.write("\n  ");
                writer.write(decl.toString());
            }
            writer.write("\n]");
        }
        writer.write(">");
    }
    
    public void accept(final Visitor visitor) {
        visitor.visit(this);
    }
}
