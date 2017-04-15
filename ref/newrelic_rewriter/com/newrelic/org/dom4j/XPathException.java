// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j;

public class XPathException extends RuntimeException
{
    private String xpath;
    
    public XPathException(final String xpath) {
        super("Exception occurred evaluting XPath: " + xpath);
        this.xpath = xpath;
    }
    
    public XPathException(final String xpath, final String reason) {
        super("Exception occurred evaluting XPath: " + xpath + " " + reason);
        this.xpath = xpath;
    }
    
    public XPathException(final String xpath, final Exception e) {
        super("Exception occurred evaluting XPath: " + xpath + ". Exception: " + e.getMessage());
        this.xpath = xpath;
    }
    
    public String getXPath() {
        return this.xpath;
    }
}
