// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.xpath;

import com.newrelic.org.dom4j.Namespace;
import com.newrelic.org.dom4j.Node;
import com.newrelic.org.dom4j.Document;
import com.newrelic.org.dom4j.Element;
import java.io.Serializable;
import org.jaxen.NamespaceContext;

public class DefaultNamespaceContext implements NamespaceContext, Serializable
{
    private final Element element;
    
    public DefaultNamespaceContext(final Element element) {
        this.element = element;
    }
    
    public static DefaultNamespaceContext create(final Object node) {
        Element element = null;
        if (node instanceof Element) {
            element = (Element)node;
        }
        else if (node instanceof Document) {
            final Document doc = (Document)node;
            element = doc.getRootElement();
        }
        else if (node instanceof Node) {
            element = ((Node)node).getParent();
        }
        if (element != null) {
            return new DefaultNamespaceContext(element);
        }
        return null;
    }
    
    public String translateNamespacePrefixToUri(final String prefix) {
        if (prefix != null && prefix.length() > 0) {
            final Namespace ns = this.element.getNamespaceForPrefix(prefix);
            if (ns != null) {
                return ns.getURI();
            }
        }
        return null;
    }
}
