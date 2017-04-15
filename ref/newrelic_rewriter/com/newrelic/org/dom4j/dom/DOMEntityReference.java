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
import org.w3c.dom.EntityReference;
import com.newrelic.org.dom4j.tree.DefaultEntity;

public class DOMEntityReference extends DefaultEntity implements EntityReference
{
    public DOMEntityReference(final String name) {
        super(name);
    }
    
    public DOMEntityReference(final String name, final String text) {
        super(name, text);
    }
    
    public DOMEntityReference(final Element parent, final String name, final String text) {
        super(parent, name, text);
    }
    
    public boolean supports(final String feature, final String version) {
        return DOMNodeHelper.supports(this, feature, version);
    }
    
    public String getNamespaceURI() {
        return DOMNodeHelper.getNamespaceURI(this);
    }
    
    public String getPrefix() {
        return DOMNodeHelper.getPrefix(this);
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
        return null;
    }
    
    public void setNodeValue(final String nodeValue) throws DOMException {
    }
    
    public org.w3c.dom.Node getParentNode() {
        return DOMNodeHelper.getParentNode(this);
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
        if (nodeType != 1 && nodeType != 3 && nodeType != 8 && nodeType != 7 && nodeType != 4 && nodeType != 5) {
            throw new DOMException((short)3, "Given node cannot be a child of an entity reference");
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
}
