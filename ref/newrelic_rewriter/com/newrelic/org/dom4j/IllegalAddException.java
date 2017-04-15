// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j;

public class IllegalAddException extends IllegalArgumentException
{
    public IllegalAddException(final String reason) {
        super(reason);
    }
    
    public IllegalAddException(final Element parent, final Node node, final String reason) {
        super("The node \"" + node.toString() + "\" could not be added to the element \"" + parent.getQualifiedName() + "\" because: " + reason);
    }
    
    public IllegalAddException(final Branch parent, final Node node, final String reason) {
        super("The node \"" + node.toString() + "\" could not be added to the branch \"" + parent.getName() + "\" because: " + reason);
    }
}
