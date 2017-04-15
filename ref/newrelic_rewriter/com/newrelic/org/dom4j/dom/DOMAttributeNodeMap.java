// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.dom;

import org.w3c.dom.Attr;
import org.w3c.dom.Node;
import org.w3c.dom.DOMException;
import org.w3c.dom.NamedNodeMap;

public class DOMAttributeNodeMap implements NamedNodeMap
{
    private DOMElement element;
    
    public DOMAttributeNodeMap(final DOMElement element) {
        this.element = element;
    }
    
    public void foo() throws DOMException {
        DOMNodeHelper.notSupported();
    }
    
    public Node getNamedItem(final String name) {
        return this.element.getAttributeNode(name);
    }
    
    public Node setNamedItem(final Node arg) throws DOMException {
        if (arg instanceof Attr) {
            return this.element.setAttributeNode((Attr)arg);
        }
        throw new DOMException((short)9, "Node is not an Attr: " + arg);
    }
    
    public Node removeNamedItem(final String name) throws DOMException {
        final Attr attr = this.element.getAttributeNode(name);
        if (attr == null) {
            throw new DOMException((short)8, "No attribute named " + name);
        }
        return this.element.removeAttributeNode(attr);
    }
    
    public Node item(final int index) {
        return DOMNodeHelper.asDOMAttr(this.element.attribute(index));
    }
    
    public int getLength() {
        return this.element.attributeCount();
    }
    
    public Node getNamedItemNS(final String namespaceURI, final String localName) {
        return this.element.getAttributeNodeNS(namespaceURI, localName);
    }
    
    public Node setNamedItemNS(final Node arg) throws DOMException {
        if (arg instanceof Attr) {
            return this.element.setAttributeNodeNS((Attr)arg);
        }
        throw new DOMException((short)9, "Node is not an Attr: " + arg);
    }
    
    public Node removeNamedItemNS(final String namespaceURI, final String localName) throws DOMException {
        final Attr attr = this.element.getAttributeNodeNS(namespaceURI, localName);
        if (attr != null) {
            return this.element.removeAttributeNode(attr);
        }
        return attr;
    }
}
