// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.io;

import org.w3c.dom.DocumentType;
import org.w3c.dom.EntityReference;
import org.w3c.dom.CDATASection;
import com.newrelic.org.dom4j.Attribute;
import com.newrelic.org.dom4j.Namespace;
import com.newrelic.org.dom4j.ProcessingInstruction;
import com.newrelic.org.dom4j.Entity;
import com.newrelic.org.dom4j.Comment;
import com.newrelic.org.dom4j.CDATA;
import com.newrelic.org.dom4j.Text;
import com.newrelic.org.dom4j.Element;
import java.util.List;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Node;
import com.newrelic.org.dom4j.Document;
import com.newrelic.org.dom4j.DocumentException;
import com.newrelic.org.dom4j.tree.NamespaceStack;

public class DOMWriter
{
    private static boolean loggedWarning;
    private static final String[] DEFAULT_DOM_DOCUMENT_CLASSES;
    private Class domDocumentClass;
    private NamespaceStack namespaceStack;
    static /* synthetic */ Class class$org$dom4j$io$DOMWriter;
    
    public DOMWriter() {
        this.namespaceStack = new NamespaceStack();
    }
    
    public DOMWriter(final Class domDocumentClass) {
        this.namespaceStack = new NamespaceStack();
        this.domDocumentClass = domDocumentClass;
    }
    
    public Class getDomDocumentClass() throws DocumentException {
        Class result = this.domDocumentClass;
        if (result == null) {
            for (int size = DOMWriter.DEFAULT_DOM_DOCUMENT_CLASSES.length, i = 0; i < size; ++i) {
                try {
                    final String name = DOMWriter.DEFAULT_DOM_DOCUMENT_CLASSES[i];
                    result = Class.forName(name, true, ((DOMWriter.class$org$dom4j$io$DOMWriter == null) ? (DOMWriter.class$org$dom4j$io$DOMWriter = class$("com.newrelic.org.dom4j.io.DOMWriter")) : DOMWriter.class$org$dom4j$io$DOMWriter).getClassLoader());
                    if (result != null) {
                        break;
                    }
                }
                catch (Exception ex) {}
            }
        }
        return result;
    }
    
    public void setDomDocumentClass(final Class domDocumentClass) {
        this.domDocumentClass = domDocumentClass;
    }
    
    public void setDomDocumentClassName(final String name) throws DocumentException {
        try {
            this.domDocumentClass = Class.forName(name, true, ((DOMWriter.class$org$dom4j$io$DOMWriter == null) ? (DOMWriter.class$org$dom4j$io$DOMWriter = class$("com.newrelic.org.dom4j.io.DOMWriter")) : DOMWriter.class$org$dom4j$io$DOMWriter).getClassLoader());
        }
        catch (Exception e) {
            throw new DocumentException("Could not load the DOM Document class: " + name, e);
        }
    }
    
    public org.w3c.dom.Document write(final Document document) throws DocumentException {
        if (document instanceof org.w3c.dom.Document) {
            return (org.w3c.dom.Document)document;
        }
        this.resetNamespaceStack();
        final org.w3c.dom.Document domDocument = this.createDomDocument(document);
        this.appendDOMTree(domDocument, domDocument, document.content());
        this.namespaceStack.clear();
        return domDocument;
    }
    
    public org.w3c.dom.Document write(final Document document, final DOMImplementation domImpl) throws DocumentException {
        if (document instanceof org.w3c.dom.Document) {
            return (org.w3c.dom.Document)document;
        }
        this.resetNamespaceStack();
        final org.w3c.dom.Document domDocument = this.createDomDocument(document, domImpl);
        this.appendDOMTree(domDocument, domDocument, document.content());
        this.namespaceStack.clear();
        return domDocument;
    }
    
    protected void appendDOMTree(final org.w3c.dom.Document domDocument, final Node domCurrent, final List content) {
        for (int size = content.size(), i = 0; i < size; ++i) {
            final Object object = content.get(i);
            if (object instanceof Element) {
                this.appendDOMTree(domDocument, domCurrent, (Element)object);
            }
            else if (object instanceof String) {
                this.appendDOMTree(domDocument, domCurrent, (String)object);
            }
            else if (object instanceof Text) {
                final Text text = (Text)object;
                this.appendDOMTree(domDocument, domCurrent, text.getText());
            }
            else if (object instanceof CDATA) {
                this.appendDOMTree(domDocument, domCurrent, (CDATA)object);
            }
            else if (object instanceof Comment) {
                this.appendDOMTree(domDocument, domCurrent, (Comment)object);
            }
            else if (object instanceof Entity) {
                this.appendDOMTree(domDocument, domCurrent, (Entity)object);
            }
            else if (object instanceof ProcessingInstruction) {
                this.appendDOMTree(domDocument, domCurrent, (ProcessingInstruction)object);
            }
        }
    }
    
    protected void appendDOMTree(final org.w3c.dom.Document domDocument, final Node domCurrent, final Element element) {
        final String elUri = element.getNamespaceURI();
        final String elName = element.getQualifiedName();
        final org.w3c.dom.Element domElement = domDocument.createElementNS(elUri, elName);
        final int stackSize = this.namespaceStack.size();
        final Namespace elementNamespace = element.getNamespace();
        if (this.isNamespaceDeclaration(elementNamespace)) {
            this.namespaceStack.push(elementNamespace);
            this.writeNamespace(domElement, elementNamespace);
        }
        final List declaredNamespaces = element.declaredNamespaces();
        for (int i = 0, size = declaredNamespaces.size(); i < size; ++i) {
            final Namespace namespace = declaredNamespaces.get(i);
            if (this.isNamespaceDeclaration(namespace)) {
                this.namespaceStack.push(namespace);
                this.writeNamespace(domElement, namespace);
            }
        }
        for (int i = 0, size = element.attributeCount(); i < size; ++i) {
            final Attribute attribute = element.attribute(i);
            final String attUri = attribute.getNamespaceURI();
            final String attName = attribute.getQualifiedName();
            final String value = attribute.getValue();
            domElement.setAttributeNS(attUri, attName, value);
        }
        this.appendDOMTree(domDocument, domElement, element.content());
        domCurrent.appendChild(domElement);
        while (this.namespaceStack.size() > stackSize) {
            this.namespaceStack.pop();
        }
    }
    
    protected void appendDOMTree(final org.w3c.dom.Document domDocument, final Node domCurrent, final CDATA cdata) {
        final CDATASection domCDATA = domDocument.createCDATASection(cdata.getText());
        domCurrent.appendChild(domCDATA);
    }
    
    protected void appendDOMTree(final org.w3c.dom.Document domDocument, final Node domCurrent, final Comment comment) {
        final org.w3c.dom.Comment domComment = domDocument.createComment(comment.getText());
        domCurrent.appendChild(domComment);
    }
    
    protected void appendDOMTree(final org.w3c.dom.Document domDocument, final Node domCurrent, final String text) {
        final org.w3c.dom.Text domText = domDocument.createTextNode(text);
        domCurrent.appendChild(domText);
    }
    
    protected void appendDOMTree(final org.w3c.dom.Document domDocument, final Node domCurrent, final Entity entity) {
        final EntityReference domEntity = domDocument.createEntityReference(entity.getName());
        domCurrent.appendChild(domEntity);
    }
    
    protected void appendDOMTree(final org.w3c.dom.Document domDoc, final Node domCurrent, final ProcessingInstruction pi) {
        final org.w3c.dom.ProcessingInstruction domPI = domDoc.createProcessingInstruction(pi.getTarget(), pi.getText());
        domCurrent.appendChild(domPI);
    }
    
    protected void writeNamespace(final org.w3c.dom.Element domElement, final Namespace namespace) {
        final String attributeName = this.attributeNameForNamespace(namespace);
        domElement.setAttribute(attributeName, namespace.getURI());
    }
    
    protected String attributeNameForNamespace(final Namespace namespace) {
        final String xmlns = "xmlns";
        final String prefix = namespace.getPrefix();
        if (prefix.length() > 0) {
            return xmlns + ":" + prefix;
        }
        return xmlns;
    }
    
    protected org.w3c.dom.Document createDomDocument(final Document document) throws DocumentException {
        org.w3c.dom.Document result = null;
        if (this.domDocumentClass != null) {
            try {
                result = this.domDocumentClass.newInstance();
                return result;
            }
            catch (Exception e) {
                throw new DocumentException("Could not instantiate an instance of DOM Document with class: " + this.domDocumentClass.getName(), e);
            }
        }
        result = this.createDomDocumentViaJAXP();
        if (result == null) {
            final Class theClass = this.getDomDocumentClass();
            try {
                result = theClass.newInstance();
            }
            catch (Exception e2) {
                throw new DocumentException("Could not instantiate an instance of DOM Document with class: " + theClass.getName(), e2);
            }
        }
        return result;
    }
    
    protected org.w3c.dom.Document createDomDocumentViaJAXP() throws DocumentException {
        try {
            return JAXPHelper.createDocument(false, true);
        }
        catch (Throwable e) {
            if (!DOMWriter.loggedWarning) {
                DOMWriter.loggedWarning = true;
                if (SAXHelper.isVerboseErrorReporting()) {
                    System.out.println("Warning: Caught exception attempting to use JAXP to create a W3C DOM document");
                    System.out.println("Warning: Exception was: " + e);
                    e.printStackTrace();
                }
                else {
                    System.out.println("Warning: Error occurred using JAXP to create a DOM document.");
                }
            }
            return null;
        }
    }
    
    protected org.w3c.dom.Document createDomDocument(final Document document, final DOMImplementation domImpl) throws DocumentException {
        final String namespaceURI = null;
        final String qualifiedName = null;
        final DocumentType docType = null;
        return domImpl.createDocument(namespaceURI, qualifiedName, docType);
    }
    
    protected boolean isNamespaceDeclaration(final Namespace ns) {
        if (ns != null && ns != Namespace.NO_NAMESPACE && ns != Namespace.XML_NAMESPACE) {
            final String uri = ns.getURI();
            if (uri != null && uri.length() > 0 && !this.namespaceStack.contains(ns)) {
                return true;
            }
        }
        return false;
    }
    
    protected void resetNamespaceStack() {
        this.namespaceStack.clear();
        this.namespaceStack.push(Namespace.XML_NAMESPACE);
    }
    
    static /* synthetic */ Class class$(final String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x) {
            throw new NoClassDefFoundError(x.getMessage());
        }
    }
    
    static {
        DOMWriter.loggedWarning = false;
        DEFAULT_DOM_DOCUMENT_CLASSES = new String[] { "org.apache.xerces.dom.DocumentImpl", "gnu.xml.dom.DomDocument", "org.apache.crimson.tree.XmlDocument", "com.sun.xml.tree.XmlDocument", "oracle.xml.parser.v2.XMLDocument", "oracle.xml.parser.XMLDocument", "com.newrelic.org.dom4j.dom.DOMDocument" };
    }
}
