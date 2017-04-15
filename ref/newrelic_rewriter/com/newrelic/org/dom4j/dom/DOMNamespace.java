// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.dom;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.DOMException;
import com.newrelic.org.dom4j.Element;
import org.w3c.dom.Node;
import com.newrelic.org.dom4j.tree.DefaultNamespace;

public class DOMNamespace extends DefaultNamespace implements Node
{
    public DOMNamespace(final String prefix, final String uri) {
        super(prefix, uri);
    }
    
    public DOMNamespace(final Element parent, final String prefix, final String uri) {
        super(parent, prefix, uri);
    }
    
    public boolean supports(final String feature, final String version) {
        return DOMNodeHelper.supports(this, feature, version);
    }
    
    public String getNamespaceURI() {
        return DOMNodeHelper.getNamespaceURI(this);
    }
    
    public void setPrefix(final String prefix) throws DOMException {
        DOMNodeHelper.setPrefix(this, prefix);
    }
    
    public String getLocalName() {
        return DOMNodeHelper.getLocalName(this);
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
    
    public Node getParentNode() {
        return DOMNodeHelper.getParentNode(this);
    }
    
    public NodeList getChildNodes() {
        return DOMNodeHelper.getChildNodes(this);
    }
    
    public Node getFirstChild() {
        return DOMNodeHelper.getFirstChild(this);
    }
    
    public Node getLastChild() {
        return DOMNodeHelper.getLastChild(this);
    }
    
    public Node getPreviousSibling() {
        return DOMNodeHelper.getPreviousSibling(this);
    }
    
    public Node getNextSibling() {
        return DOMNodeHelper.getNextSibling(this);
    }
    
    public NamedNodeMap getAttributes() {
        return DOMNodeHelper.getAttributes(this);
    }
    
    public Document getOwnerDocument() {
        return DOMNodeHelper.getOwnerDocument(this);
    }
    
    public Node insertBefore(final Node newChild, final Node refChild) throws DOMException {
        return DOMNodeHelper.insertBefore(this, newChild, refChild);
    }
    
    public Node replaceChild(final Node newChild, final Node oldChild) throws DOMException {
        return DOMNodeHelper.replaceChild(this, newChild, oldChild);
    }
    
    public Node removeChild(final Node oldChild) throws DOMException {
        return DOMNodeHelper.removeChild(this, oldChild);
    }
    
    public Node appendChild(final Node newChild) throws DOMException {
        return DOMNodeHelper.appendChild(this, newChild);
    }
    
    public boolean hasChildNodes() {
        return DOMNodeHelper.hasChildNodes(this);
    }
    
    public Node cloneNode(final boolean deep) {
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
}
