// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.dom;

import org.w3c.dom.EntityReference;
import com.newrelic.org.dom4j.QName;
import org.w3c.dom.Attr;
import org.w3c.dom.ProcessingInstruction;
import org.w3c.dom.CDATASection;
import org.w3c.dom.Comment;
import org.w3c.dom.Text;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.DOMImplementation;
import com.newrelic.org.dom4j.Branch;
import java.util.List;
import java.util.ArrayList;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.DOMException;
import com.newrelic.org.dom4j.Node;
import com.newrelic.org.dom4j.DocumentFactory;
import com.newrelic.org.dom4j.DocumentType;
import com.newrelic.org.dom4j.Element;
import org.w3c.dom.Document;
import com.newrelic.org.dom4j.tree.DefaultDocument;

public class DOMDocument extends DefaultDocument implements Document
{
    private static final DOMDocumentFactory DOCUMENT_FACTORY;
    
    public DOMDocument() {
        this.init();
    }
    
    public DOMDocument(final String name) {
        super(name);
        this.init();
    }
    
    public DOMDocument(final DOMElement rootElement) {
        super(rootElement);
        this.init();
    }
    
    public DOMDocument(final DOMDocumentType docType) {
        super(docType);
        this.init();
    }
    
    public DOMDocument(final DOMElement rootElement, final DOMDocumentType docType) {
        super(rootElement, docType);
        this.init();
    }
    
    public DOMDocument(final String name, final DOMElement rootElement, final DOMDocumentType docType) {
        super(name, rootElement, docType);
        this.init();
    }
    
    private void init() {
        this.setDocumentFactory(DOMDocument.DOCUMENT_FACTORY);
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
        return "#document";
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
        return null;
    }
    
    public Document getOwnerDocument() {
        return null;
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
        if (nodeType != 1 && nodeType != 8 && nodeType != 7 && nodeType != 10) {
            throw new DOMException((short)3, "Given node cannot be a child of document");
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
    
    public NodeList getElementsByTagName(final String name) {
        final ArrayList list = new ArrayList();
        DOMNodeHelper.appendElementsByTagName(list, this, name);
        return DOMNodeHelper.createNodeList(list);
    }
    
    public NodeList getElementsByTagNameNS(final String namespace, final String name) {
        final ArrayList list = new ArrayList();
        DOMNodeHelper.appendElementsByTagNameNS(list, this, namespace, name);
        return DOMNodeHelper.createNodeList(list);
    }
    
    public org.w3c.dom.DocumentType getDoctype() {
        return DOMNodeHelper.asDOMDocumentType(this.getDocType());
    }
    
    public DOMImplementation getImplementation() {
        if (this.getDocumentFactory() instanceof DOMImplementation) {
            return (DOMImplementation)this.getDocumentFactory();
        }
        return DOMDocument.DOCUMENT_FACTORY;
    }
    
    public org.w3c.dom.Element getDocumentElement() {
        return DOMNodeHelper.asDOMElement(this.getRootElement());
    }
    
    public org.w3c.dom.Element createElement(final String name) throws DOMException {
        return (org.w3c.dom.Element)this.getDocumentFactory().createElement(name);
    }
    
    public DocumentFragment createDocumentFragment() {
        DOMNodeHelper.notSupported();
        return null;
    }
    
    public Text createTextNode(final String data) {
        return (Text)this.getDocumentFactory().createText(data);
    }
    
    public Comment createComment(final String data) {
        return (Comment)this.getDocumentFactory().createComment(data);
    }
    
    public CDATASection createCDATASection(final String data) throws DOMException {
        return (CDATASection)this.getDocumentFactory().createCDATA(data);
    }
    
    public ProcessingInstruction createProcessingInstruction(final String target, final String data) throws DOMException {
        return (ProcessingInstruction)this.getDocumentFactory().createProcessingInstruction(target, data);
    }
    
    public Attr createAttribute(final String name) throws DOMException {
        final QName qname = this.getDocumentFactory().createQName(name);
        return (Attr)this.getDocumentFactory().createAttribute(null, qname, "");
    }
    
    public EntityReference createEntityReference(final String name) throws DOMException {
        return (EntityReference)this.getDocumentFactory().createEntity(name, null);
    }
    
    public org.w3c.dom.Node importNode(final org.w3c.dom.Node importedNode, final boolean deep) throws DOMException {
        DOMNodeHelper.notSupported();
        return null;
    }
    
    public org.w3c.dom.Element createElementNS(final String namespaceURI, final String qualifiedName) throws DOMException {
        final QName qname = this.getDocumentFactory().createQName(qualifiedName, namespaceURI);
        return (org.w3c.dom.Element)this.getDocumentFactory().createElement(qname);
    }
    
    public Attr createAttributeNS(final String namespaceURI, final String qualifiedName) throws DOMException {
        final QName qname = this.getDocumentFactory().createQName(qualifiedName, namespaceURI);
        return (Attr)this.getDocumentFactory().createAttribute(null, qname, null);
    }
    
    public org.w3c.dom.Element getElementById(final String elementId) {
        return DOMNodeHelper.asDOMElement(this.elementByID(elementId));
    }
    
    protected DocumentFactory getDocumentFactory() {
        if (super.getDocumentFactory() == null) {
            return DOMDocument.DOCUMENT_FACTORY;
        }
        return super.getDocumentFactory();
    }
    
    static {
        DOCUMENT_FACTORY = (DOMDocumentFactory)DOMDocumentFactory.getInstance();
    }
}
