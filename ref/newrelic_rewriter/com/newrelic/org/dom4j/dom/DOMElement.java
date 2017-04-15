// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.dom;

import com.newrelic.org.dom4j.Branch;
import java.util.List;
import java.util.ArrayList;
import org.w3c.dom.Attr;
import com.newrelic.org.dom4j.Attribute;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.DOMException;
import com.newrelic.org.dom4j.Node;
import com.newrelic.org.dom4j.Namespace;
import com.newrelic.org.dom4j.QName;
import com.newrelic.org.dom4j.DocumentFactory;
import org.w3c.dom.Element;
import com.newrelic.org.dom4j.tree.DefaultElement;

public class DOMElement extends DefaultElement implements Element
{
    private static final DocumentFactory DOCUMENT_FACTORY;
    
    public DOMElement(final String name) {
        super(name);
    }
    
    public DOMElement(final QName qname) {
        super(qname);
    }
    
    public DOMElement(final QName qname, final int attributeCount) {
        super(qname, attributeCount);
    }
    
    public DOMElement(final String name, final Namespace namespace) {
        super(name, namespace);
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
        return null;
    }
    
    public void setNodeValue(final String nodeValue) throws DOMException {
    }
    
    public org.w3c.dom.Node getParentNode() {
        return DOMNodeHelper.getParentNode(this);
    }
    
    public NodeList getChildNodes() {
        return DOMNodeHelper.createNodeList(this.content());
    }
    
    public org.w3c.dom.Node getFirstChild() {
        return DOMNodeHelper.asDOMNode(this.node(0));
    }
    
    public org.w3c.dom.Node getLastChild() {
        return DOMNodeHelper.asDOMNode(this.node(this.nodeCount() - 1));
    }
    
    public org.w3c.dom.Node getPreviousSibling() {
        return DOMNodeHelper.getPreviousSibling(this);
    }
    
    public org.w3c.dom.Node getNextSibling() {
        return DOMNodeHelper.getNextSibling(this);
    }
    
    public NamedNodeMap getAttributes() {
        return new DOMAttributeNodeMap(this);
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
            throw new DOMException((short)3, "Given node cannot be a child of element");
        }
    }
    
    public boolean hasChildNodes() {
        return this.nodeCount() > 0;
    }
    
    public org.w3c.dom.Node cloneNode(final boolean deep) {
        return DOMNodeHelper.cloneNode(this, deep);
    }
    
    public boolean isSupported(final String feature, final String version) {
        return DOMNodeHelper.isSupported(this, feature, version);
    }
    
    public boolean hasAttributes() {
        return DOMNodeHelper.hasAttributes(this);
    }
    
    public String getTagName() {
        return this.getName();
    }
    
    public String getAttribute(final String name) {
        final String answer = this.attributeValue(name);
        return (answer != null) ? answer : "";
    }
    
    public void setAttribute(final String name, final String value) throws DOMException {
        this.addAttribute(name, value);
    }
    
    public void removeAttribute(final String name) throws DOMException {
        final Attribute attribute = this.attribute(name);
        if (attribute != null) {
            this.remove(attribute);
        }
    }
    
    public Attr getAttributeNode(final String name) {
        return DOMNodeHelper.asDOMAttr(this.attribute(name));
    }
    
    public Attr setAttributeNode(final Attr newAttr) throws DOMException {
        if (this.isReadOnly()) {
            throw new DOMException((short)7, "No modification allowed");
        }
        final Attribute attribute = this.attribute(newAttr);
        if (attribute != newAttr) {
            if (newAttr.getOwnerElement() != null) {
                throw new DOMException((short)10, "Attribute is already in use");
            }
            final Attribute newAttribute = this.createAttribute(newAttr);
            if (attribute != null) {
                attribute.detach();
            }
            this.add(newAttribute);
        }
        return DOMNodeHelper.asDOMAttr(attribute);
    }
    
    public Attr removeAttributeNode(final Attr oldAttr) throws DOMException {
        final Attribute attribute = this.attribute(oldAttr);
        if (attribute != null) {
            attribute.detach();
            return DOMNodeHelper.asDOMAttr(attribute);
        }
        throw new DOMException((short)8, "No such attribute");
    }
    
    public String getAttributeNS(final String namespaceURI, final String localName) {
        final Attribute attribute = this.attribute(namespaceURI, localName);
        if (attribute != null) {
            final String answer = attribute.getValue();
            if (answer != null) {
                return answer;
            }
        }
        return "";
    }
    
    public void setAttributeNS(final String namespaceURI, final String qualifiedName, final String value) throws DOMException {
        final Attribute attribute = this.attribute(namespaceURI, qualifiedName);
        if (attribute != null) {
            attribute.setValue(value);
        }
        else {
            final QName qname = this.getQName(namespaceURI, qualifiedName);
            this.addAttribute(qname, value);
        }
    }
    
    public void removeAttributeNS(final String namespaceURI, final String localName) throws DOMException {
        final Attribute attribute = this.attribute(namespaceURI, localName);
        if (attribute != null) {
            this.remove(attribute);
        }
    }
    
    public Attr getAttributeNodeNS(final String namespaceURI, final String localName) {
        final Attribute attribute = this.attribute(namespaceURI, localName);
        if (attribute != null) {
            DOMNodeHelper.asDOMAttr(attribute);
        }
        return null;
    }
    
    public Attr setAttributeNodeNS(final Attr newAttr) throws DOMException {
        Attribute attribute = this.attribute(newAttr.getNamespaceURI(), newAttr.getLocalName());
        if (attribute != null) {
            attribute.setValue(newAttr.getValue());
        }
        else {
            attribute = this.createAttribute(newAttr);
            this.add(attribute);
        }
        return DOMNodeHelper.asDOMAttr(attribute);
    }
    
    public NodeList getElementsByTagName(final String name) {
        final ArrayList list = new ArrayList();
        DOMNodeHelper.appendElementsByTagName(list, this, name);
        return DOMNodeHelper.createNodeList(list);
    }
    
    public NodeList getElementsByTagNameNS(final String namespace, final String lName) {
        final ArrayList list = new ArrayList();
        DOMNodeHelper.appendElementsByTagNameNS(list, this, namespace, lName);
        return DOMNodeHelper.createNodeList(list);
    }
    
    public boolean hasAttribute(final String name) {
        return this.attribute(name) != null;
    }
    
    public boolean hasAttributeNS(final String namespaceURI, final String localName) {
        return this.attribute(namespaceURI, localName) != null;
    }
    
    protected DocumentFactory getDocumentFactory() {
        final DocumentFactory factory = this.getQName().getDocumentFactory();
        return (factory != null) ? factory : DOMElement.DOCUMENT_FACTORY;
    }
    
    protected Attribute attribute(final Attr attr) {
        return this.attribute(DOMElement.DOCUMENT_FACTORY.createQName(attr.getLocalName(), attr.getPrefix(), attr.getNamespaceURI()));
    }
    
    protected Attribute attribute(final String namespaceURI, final String localName) {
        final List attributes = this.attributeList();
        for (int size = attributes.size(), i = 0; i < size; ++i) {
            final Attribute attribute = attributes.get(i);
            if (localName.equals(attribute.getName()) && (((namespaceURI == null || namespaceURI.length() == 0) && (attribute.getNamespaceURI() == null || attribute.getNamespaceURI().length() == 0)) || (namespaceURI != null && namespaceURI.equals(attribute.getNamespaceURI())))) {
                return attribute;
            }
        }
        return null;
    }
    
    protected Attribute createAttribute(final Attr newAttr) {
        QName qname = null;
        String name = newAttr.getLocalName();
        if (name != null) {
            final String prefix = newAttr.getPrefix();
            final String uri = newAttr.getNamespaceURI();
            qname = this.getDocumentFactory().createQName(name, prefix, uri);
        }
        else {
            name = newAttr.getName();
            qname = this.getDocumentFactory().createQName(name);
        }
        return new DOMAttribute(qname, newAttr.getValue());
    }
    
    protected QName getQName(final String namespace, final String qualifiedName) {
        final int index = qualifiedName.indexOf(58);
        String prefix = "";
        String localName = qualifiedName;
        if (index >= 0) {
            prefix = qualifiedName.substring(0, index);
            localName = qualifiedName.substring(index + 1);
        }
        return this.getDocumentFactory().createQName(localName, prefix, namespace);
    }
    
    static {
        DOCUMENT_FACTORY = DOMDocumentFactory.getInstance();
    }
}
