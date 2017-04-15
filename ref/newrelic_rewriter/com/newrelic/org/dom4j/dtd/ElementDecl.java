// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.dtd;

public class ElementDecl
{
    private String name;
    private String model;
    
    public ElementDecl() {
    }
    
    public ElementDecl(final String name, final String model) {
        this.name = name;
        this.model = model;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public String getModel() {
        return this.model;
    }
    
    public void setModel(final String model) {
        this.model = model;
    }
    
    public String toString() {
        return "<!ELEMENT " + this.name + " " + this.model + ">";
    }
}
