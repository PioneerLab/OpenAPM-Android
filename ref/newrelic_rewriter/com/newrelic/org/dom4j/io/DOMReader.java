// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.io;

import com.newrelic.org.dom4j.Namespace;
import java.util.List;
import com.newrelic.org.dom4j.QName;
import org.w3c.dom.NamedNodeMap;
import java.util.ArrayList;
import org.w3c.dom.DocumentType;
import com.newrelic.org.dom4j.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.newrelic.org.dom4j.Branch;
import org.w3c.dom.Document;
import com.newrelic.org.dom4j.tree.NamespaceStack;
import com.newrelic.org.dom4j.DocumentFactory;

public class DOMReader
{
    private DocumentFactory factory;
    private NamespaceStack namespaceStack;
    
    public DOMReader() {
        this.factory = DocumentFactory.getInstance();
        this.namespaceStack = new NamespaceStack(this.factory);
    }
    
    public DOMReader(final DocumentFactory factory) {
        this.factory = factory;
        this.namespaceStack = new NamespaceStack(factory);
    }
    
    public DocumentFactory getDocumentFactory() {
        return this.factory;
    }
    
    public void setDocumentFactory(final DocumentFactory docFactory) {
        this.factory = docFactory;
        this.namespaceStack.setDocumentFactory(this.factory);
    }
    
    public com.newrelic.org.dom4j.Document read(final Document domDocument) {
        if (domDocument instanceof com.newrelic.org.dom4j.Document) {
            return (com.newrelic.org.dom4j.Document)domDocument;
        }
        final com.newrelic.org.dom4j.Document document = this.createDocument();
        this.clearNamespaceStack();
        final NodeList nodeList = domDocument.getChildNodes();
        for (int i = 0, size = nodeList.getLength(); i < size; ++i) {
            this.readTree(nodeList.item(i), document);
        }
        return document;
    }
    
    protected void readTree(final Node node, final Branch current) {
        Element element = null;
        com.newrelic.org.dom4j.Document document = null;
        if (current instanceof Element) {
            element = (Element)current;
        }
        else {
            document = (com.newrelic.org.dom4j.Document)current;
        }
        switch (node.getNodeType()) {
            case 1: {
                this.readElement(node, current);
                break;
            }
            case 7: {
                if (current instanceof Element) {
                    final Element currentEl = (Element)current;
                    currentEl.addProcessingInstruction(node.getNodeName(), node.getNodeValue());
                    break;
                }
                final com.newrelic.org.dom4j.Document currentDoc = (com.newrelic.org.dom4j.Document)current;
                currentDoc.addProcessingInstruction(node.getNodeName(), node.getNodeValue());
                break;
            }
            case 8: {
                if (current instanceof Element) {
                    ((Element)current).addComment(node.getNodeValue());
                    break;
                }
                ((com.newrelic.org.dom4j.Document)current).addComment(node.getNodeValue());
                break;
            }
            case 10: {
                final DocumentType domDocType = (DocumentType)node;
                document.addDocType(domDocType.getName(), domDocType.getPublicId(), domDocType.getSystemId());
                break;
            }
            case 3: {
                element.addText(node.getNodeValue());
                break;
            }
            case 4: {
                element.addCDATA(node.getNodeValue());
                break;
            }
            case 5: {
                final Node firstChild = node.getFirstChild();
                if (firstChild != null) {
                    element.addEntity(node.getNodeName(), firstChild.getNodeValue());
                    break;
                }
                element.addEntity(node.getNodeName(), "");
                break;
            }
            case 6: {
                element.addEntity(node.getNodeName(), node.getNodeValue());
                break;
            }
            default: {
                System.out.println("WARNING: Unknown DOM node type: " + node.getNodeType());
                break;
            }
        }
    }
    
    protected void readElement(final Node node, final Branch current) {
        final int previouslyDeclaredNamespaces = this.namespaceStack.size();
        String namespaceUri = node.getNamespaceURI();
        String elementPrefix = node.getPrefix();
        if (elementPrefix == null) {
            elementPrefix = "";
        }
        final NamedNodeMap attributeList = node.getAttributes();
        if (attributeList != null && namespaceUri == null) {
            final Node attribute = attributeList.getNamedItem("xmlns");
            if (attribute != null) {
                namespaceUri = attribute.getNodeValue();
                elementPrefix = "";
            }
        }
        final QName qName = this.namespaceStack.getQName(namespaceUri, node.getLocalName(), node.getNodeName());
        final Element element = current.addElement(qName);
        if (attributeList != null) {
            int size = attributeList.getLength();
            final List attributes = new ArrayList(size);
            for (int i = 0; i < size; ++i) {
                final Node attribute2 = attributeList.item(i);
                final String name = attribute2.getNodeName();
                if (name.startsWith("xmlns")) {
                    final String prefix = this.getPrefix(name);
                    final String uri = attribute2.getNodeValue();
                    final Namespace namespace = this.namespaceStack.addNamespace(prefix, uri);
                    element.add(namespace);
                }
                else {
                    attributes.add(attribute2);
                }
            }
            size = attributes.size();
            for (int i = 0; i < size; ++i) {
                final Node attribute2 = attributes.get(i);
                final QName attributeQName = this.namespaceStack.getQName(attribute2.getNamespaceURI(), attribute2.getLocalName(), attribute2.getNodeName());
                element.addAttribute(attributeQName, attribute2.getNodeValue());
            }
        }
        final NodeList children = node.getChildNodes();
        for (int j = 0, size2 = children.getLength(); j < size2; ++j) {
            final Node child = children.item(j);
            this.readTree(child, element);
        }
        while (this.namespaceStack.size() > previouslyDeclaredNamespaces) {
            this.namespaceStack.pop();
        }
    }
    
    protected Namespace getNamespace(final String prefix, final String uri) {
        return this.getDocumentFactory().createNamespace(prefix, uri);
    }
    
    protected com.newrelic.org.dom4j.Document createDocument() {
        return this.getDocumentFactory().createDocument();
    }
    
    protected void clearNamespaceStack() {
        this.namespaceStack.clear();
        if (!this.namespaceStack.contains(Namespace.XML_NAMESPACE)) {
            this.namespaceStack.push(Namespace.XML_NAMESPACE);
        }
    }
    
    private String getPrefix(final String xmlnsDecl) {
        final int index = xmlnsDecl.indexOf(58, 5);
        if (index != -1) {
            return xmlnsDecl.substring(index + 1);
        }
        return "";
    }
}
