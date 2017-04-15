// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.dom;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.DOMException;
import com.newrelic.org.dom4j.Node;
import com.newrelic.org.dom4j.Element;
import com.newrelic.org.dom4j.QName;
import org.w3c.dom.Attr;
import com.newrelic.org.dom4j.tree.DefaultAttribute;

public class DOMAttribute extends DefaultAttribute implements Attr
{
    public DOMAttribute(final QName qname) {
        super(qname);
    }
    
    public DOMAttribute(final QName qname, final String value) {
        super(qname, value);
    }
    
    public DOMAttribute(final Element parent, final QName qname, final String value) {
        super(parent, qname, value);
    }
    
    public boolean supports(final String feature, final String version) {
        return DOMNodeHelper.supports(this, feature, version);
    }
    
    public String getNamespaceURI() {
        return this.getQName().getNamespaceURI();
    }
    
    public String getPrefix() {
        return this.getQName().getNamespacePrefix();
    }
    
    public void setPrefix(final String prefix) throws DOMException {
        DOMNodeHelper.setPrefix(this, prefix);
    }
    
    public String getLocalName() {
        return this.getQName().getName();
    }
    
    public String getNodeName() {
        return this.getName();
    }
    
    public String getNodeValue() throws DOMException {
        return DOMNodeHelper.getNodeValue(this);
    }
    
    public void setNodeValue(final String nodeValue) throws DOMException {
        DOMNodeHelper.setNodeValue(this, nodeValue);
    }
    
    public org.w3c.dom.Node getParentNode() {
        return null;
    }
    
    public NodeList getChildNodes() {
        return DOMNodeHelper.getChildNodes(this);
    }
    
    public org.w3c.dom.Node getFirstChild() {
        return DOMNodeHelper.getFirstChild(this);
    }
    
    public org.w3c.dom.Node getLastChild() {
        return DOMNodeHelper.getLastChild(this);
    }
    
    public org.w3c.dom.Node getPreviousSibling() {
        return DOMNodeHelper.getPreviousSibling(this);
    }
    
    public org.w3c.dom.Node getNextSibling() {
        return DOMNodeHelper.getNextSibling(this);
    }
    
    public NamedNodeMap getAttributes() {
        return null;
    }
    
    public Document getOwnerDocument() {
        return DOMNodeHelper.getOwnerDocument(this);
    }
    
    public org.w3c.dom.Node insertBefore(final org.w3c.dom.Node newChild, final org.w3c.dom.Node refChild) throws DOMException {
        this.checkNewChildNode(newChild);
        return DOMNodeHelper.insertBefore(this, newChild, refChild);
    }
    
    public org.w3c.dom.Node replaceChild(final org.w3c.dom.Node newChild, final org.w3c.dom.Node oldChild) throws DOMException {
        this.checkNewChildNode(newChild);
        return DOMNodeHelper.replaceChild(this, newChild, oldChild);
    }
    
    public org.w3c.dom.Node removeChild(final org.w3c.dom.Node oldChild) throws DOMException {
        return DOMNodeHelper.removeChild(this, oldChild);
    }
    
    public org.w3c.dom.Node appendChild(final org.w3c.dom.Node newChild) throws DOMException {
        this.checkNewChildNode(newChild);
        return DOMNodeHelper.appendChild(this, newChild);
    }
    
    private void checkNewChildNode(final org.w3c.dom.Node newChild) throws DOMException {
        final int nodeType = newChild.getNodeType();
        if (nodeType != 3 && nodeType != 5) {
            throw new DOMException((short)3, "The node cannot be a child of attribute");
        }
    }
    
    public boolean hasChildNodes() {
        return DOMNodeHelper.hasChildNodes(this);
    }
    
    public org.w3c.dom.Node cloneNode(final boolean deep) {
        return DOMNodeHelper.cloneNode(this, deep);
    }
    
    public void normalize() {
        DOMNodeHelper.normalize(this);
    }
    
    public boolean isSupported(final String feature, final String version) {
        return DOMNodeHelper.isSupported(this, feature, version);
    }
    
    public boolean hasAttributes() {
        return DOMNodeHelper.hasAttributes(this);
    }
    
    public boolean getSpecified() {
        return true;
    }
    
    public org.w3c.dom.Element getOwnerElement() {
        return DOMNodeHelper.asDOMElement(this.getParent());
    }
}
