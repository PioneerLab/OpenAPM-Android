// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.tree;

import com.newrelic.org.dom4j.Visitor;
import java.io.IOException;
import java.io.Writer;
import com.newrelic.org.dom4j.Text;

public abstract class AbstractText extends AbstractCharacterData implements Text
{
    public short getNodeType() {
        return 3;
    }
    
    public String toString() {
        return super.toString() + " [Text: \"" + this.getText() + "\"]";
    }
    
    public String asXML() {
        return this.getText();
    }
    
    public void write(final Writer writer) throws IOException {
        writer.write(this.getText());
    }
    
    public void accept(final Visitor visitor) {
        visitor.visit(this);
    }
}
