// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.tree;

import com.newrelic.org.dom4j.Visitor;
import java.io.IOException;
import java.io.Writer;
import com.newrelic.org.dom4j.Element;
import com.newrelic.org.dom4j.Comment;

public abstract class AbstractComment extends AbstractCharacterData implements Comment
{
    public short getNodeType() {
        return 8;
    }
    
    public String getPath(final Element context) {
        final Element parent = this.getParent();
        return (parent != null && parent != context) ? (parent.getPath(context) + "/comment()") : "comment()";
    }
    
    public String getUniquePath(final Element context) {
        final Element parent = this.getParent();
        return (parent != null && parent != context) ? (parent.getUniquePath(context) + "/comment()") : "comment()";
    }
    
    public String toString() {
        return super.toString() + " [Comment: \"" + this.getText() + "\"]";
    }
    
    public String asXML() {
        return "<!--" + this.getText() + "-->";
    }
    
    public void write(final Writer writer) throws IOException {
        writer.write("<!--");
        writer.write(this.getText());
        writer.write("-->");
    }
    
    public void accept(final Visitor visitor) {
        visitor.visit(this);
    }
}
