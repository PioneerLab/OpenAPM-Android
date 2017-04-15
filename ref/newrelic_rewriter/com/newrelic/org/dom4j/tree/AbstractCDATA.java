// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.tree;

import com.newrelic.org.dom4j.Visitor;
import java.io.IOException;
import java.io.Writer;
import java.io.StringWriter;
import com.newrelic.org.dom4j.CDATA;

public abstract class AbstractCDATA extends AbstractCharacterData implements CDATA
{
    public short getNodeType() {
        return 4;
    }
    
    public String toString() {
        return super.toString() + " [CDATA: \"" + this.getText() + "\"]";
    }
    
    public String asXML() {
        final StringWriter writer = new StringWriter();
        try {
            this.write(writer);
        }
        catch (IOException ex) {}
        return writer.toString();
    }
    
    public void write(final Writer writer) throws IOException {
        writer.write("<![CDATA[");
        if (this.getText() != null) {
            writer.write(this.getText());
        }
        writer.write("]]>");
    }
    
    public void accept(final Visitor visitor) {
        visitor.visit(this);
    }
}
