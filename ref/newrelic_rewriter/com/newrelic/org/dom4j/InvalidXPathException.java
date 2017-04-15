// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j;

public class InvalidXPathException extends IllegalArgumentException
{
    private static final long serialVersionUID = 3257009869058881592L;
    
    public InvalidXPathException(final String xpath) {
        super("Invalid XPath expression: " + xpath);
    }
    
    public InvalidXPathException(final String xpath, final String reason) {
        super("Invalid XPath expression: " + xpath + " " + reason);
    }
    
    public InvalidXPathException(final String xpath, final Throwable t) {
        super("Invalid XPath expression: '" + xpath + "'. Caused by: " + t.getMessage());
    }
}
