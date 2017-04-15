// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.tree;

import java.util.List;

public class DefaultDocumentType extends AbstractDocumentType
{
    protected String elementName;
    private String publicID;
    private String systemID;
    private List internalDeclarations;
    private List externalDeclarations;
    
    public DefaultDocumentType() {
    }
    
    public DefaultDocumentType(final String elementName, final String systemID) {
        this.elementName = elementName;
        this.systemID = systemID;
    }
    
    public DefaultDocumentType(final String elementName, final String publicID, final String systemID) {
        this.elementName = elementName;
        this.publicID = publicID;
        this.systemID = systemID;
    }
    
    public String getElementName() {
        return this.elementName;
    }
    
    public void setElementName(final String elementName) {
        this.elementName = elementName;
    }
    
    public String getPublicID() {
        return this.publicID;
    }
    
    public void setPublicID(final String publicID) {
        this.publicID = publicID;
    }
    
    public String getSystemID() {
        return this.systemID;
    }
    
    public void setSystemID(final String systemID) {
        this.systemID = systemID;
    }
    
    public List getInternalDeclarations() {
        return this.internalDeclarations;
    }
    
    public void setInternalDeclarations(final List internalDeclarations) {
        this.internalDeclarations = internalDeclarations;
    }
    
    public List getExternalDeclarations() {
        return this.externalDeclarations;
    }
    
    public void setExternalDeclarations(final List externalDeclarations) {
        this.externalDeclarations = externalDeclarations;
    }
}
