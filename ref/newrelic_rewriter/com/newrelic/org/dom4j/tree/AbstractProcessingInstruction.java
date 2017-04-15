// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.tree;

import java.util.StringTokenizer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import com.newrelic.org.dom4j.Visitor;
import java.io.IOException;
import java.io.Writer;
import com.newrelic.org.dom4j.Element;
import com.newrelic.org.dom4j.ProcessingInstruction;

public abstract class AbstractProcessingInstruction extends AbstractNode implements ProcessingInstruction
{
    public short getNodeType() {
        return 7;
    }
    
    public String getPath(final Element context) {
        final Element parent = this.getParent();
        return (parent != null && parent != context) ? (parent.getPath(context) + "/processing-instruction()") : "processing-instruction()";
    }
    
    public String getUniquePath(final Element context) {
        final Element parent = this.getParent();
        return (parent != null && parent != context) ? (parent.getUniquePath(context) + "/processing-instruction()") : "processing-instruction()";
    }
    
    public String toString() {
        return super.toString() + " [ProcessingInstruction: &" + this.getName() + ";]";
    }
    
    public String asXML() {
        return "<?" + this.getName() + " " + this.getText() + "?>";
    }
    
    public void write(final Writer writer) throws IOException {
        writer.write("<?");
        writer.write(this.getName());
        writer.write(" ");
        writer.write(this.getText());
        writer.write("?>");
    }
    
    public void accept(final Visitor visitor) {
        visitor.visit(this);
    }
    
    public void setValue(final String name, final String value) {
        throw new UnsupportedOperationException("This PI is read-only and cannot be modified");
    }
    
    public void setValues(final Map data) {
        throw new UnsupportedOperationException("This PI is read-only and cannot be modified");
    }
    
    public String getName() {
        return this.getTarget();
    }
    
    public void setName(final String name) {
        this.setTarget(name);
    }
    
    public boolean removeValue(final String name) {
        return false;
    }
    
    protected String toString(final Map values) {
        final StringBuffer buffer = new StringBuffer();
        for (final Map.Entry entry : values.entrySet()) {
            final String name = entry.getKey();
            final String value = entry.getValue();
            buffer.append(name);
            buffer.append("=\"");
            buffer.append(value);
            buffer.append("\" ");
        }
        buffer.setLength(buffer.length() - 1);
        return buffer.toString();
    }
    
    protected Map parseValues(final String text) {
        final Map data = new HashMap();
        final StringTokenizer s = new StringTokenizer(text, " ='\"", true);
        while (s.hasMoreTokens()) {
            final String name = this.getName(s);
            if (s.hasMoreTokens()) {
                final String value = this.getValue(s);
                data.put(name, value);
            }
        }
        return data;
    }
    
    private String getName(final StringTokenizer tokenizer) {
        String token = tokenizer.nextToken();
        final StringBuffer name = new StringBuffer(token);
        while (tokenizer.hasMoreTokens()) {
            token = tokenizer.nextToken();
            if (token.equals("=")) {
                break;
            }
            name.append(token);
        }
        return name.toString().trim();
    }
    
    private String getValue(final StringTokenizer tokenizer) {
        String token = tokenizer.nextToken();
        final StringBuffer value = new StringBuffer();
        while (tokenizer.hasMoreTokens() && !token.equals("'") && !token.equals("\"")) {
            token = tokenizer.nextToken();
        }
        final String quote = token;
        while (tokenizer.hasMoreTokens()) {
            token = tokenizer.nextToken();
            if (quote.equals(token)) {
                break;
            }
            value.append(token);
        }
        return value.toString();
    }
}
