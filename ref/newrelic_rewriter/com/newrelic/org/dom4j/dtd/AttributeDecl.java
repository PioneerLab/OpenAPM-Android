// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.dtd;

public class AttributeDecl
{
    private String elementName;
    private String attributeName;
    private String type;
    private String value;
    private String valueDefault;
    
    public AttributeDecl() {
    }
    
    public AttributeDecl(final String elementName, final String attributeName, final String type, final String valueDefault, final String value) {
        this.elementName = elementName;
        this.attributeName = attributeName;
        this.type = type;
        this.value = value;
        this.valueDefault = valueDefault;
    }
    
    public String getElementName() {
        return this.elementName;
    }
    
    public void setElementName(final String elementName) {
        this.elementName = elementName;
    }
    
    public String getAttributeName() {
        return this.attributeName;
    }
    
    public void setAttributeName(final String attributeName) {
        this.attributeName = attributeName;
    }
    
    public String getType() {
        return this.type;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
    
    public String getValue() {
        return this.value;
    }
    
    public void setValue(final String value) {
        this.value = value;
    }
    
    public String getValueDefault() {
        return this.valueDefault;
    }
    
    public void setValueDefault(final String valueDefault) {
        this.valueDefault = valueDefault;
    }
    
    public String toString() {
        final StringBuffer buffer = new StringBuffer("<!ATTLIST ");
        buffer.append(this.elementName);
        buffer.append(" ");
        buffer.append(this.attributeName);
        buffer.append(" ");
        buffer.append(this.type);
        buffer.append(" ");
        if (this.valueDefault != null) {
            buffer.append(this.valueDefault);
            if (this.valueDefault.equals("#FIXED")) {
                buffer.append(" \"");
                buffer.append(this.value);
                buffer.append("\"");
            }
        }
        else {
            buffer.append("\"");
            buffer.append(this.value);
            buffer.append("\"");
        }
        buffer.append(">");
        return buffer.toString();
    }
}
