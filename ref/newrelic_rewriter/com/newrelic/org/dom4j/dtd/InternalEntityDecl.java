// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.dtd;

public class InternalEntityDecl
{
    private String name;
    private String value;
    
    public InternalEntityDecl() {
    }
    
    public InternalEntityDecl(final String name, final String value) {
        this.name = name;
        this.value = value;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public void setValue(final String value) {
        this.value = value;
    }
    
    public String toString() {
        final StringBuffer buffer = new StringBuffer("<!ENTITY ");
        if (this.name.startsWith("%")) {
            buffer.append("% ");
            buffer.append(this.name.substring(1));
        }
        else {
            buffer.append(this.name);
        }
        buffer.append(" \"");
        buffer.append(this.escapeEntityValue(this.value));
        buffer.append("\">");
        return buffer.toString();
    }
    
    private String escapeEntityValue(final String text) {
        final StringBuffer result = new StringBuffer();
        for (int i = 0; i < text.length(); ++i) {
            final char c = text.charAt(i);
            switch (c) {
                case '<': {
                    result.append("&#38;#60;");
                    break;
                }
                case '>': {
                    result.append("&#62;");
                    break;
                }
                case '&': {
                    result.append("&#38;#38;");
                    break;
                }
                case '\'': {
                    result.append("&#39;");
                    break;
                }
                case '\"': {
                    result.append("&#34;");
                    break;
                }
                default: {
                    if (c < ' ') {
                        result.append("&#" + (int)c + ";");
                        break;
                    }
                    result.append(c);
                    break;
                }
            }
        }
        return result.toString();
    }
}
