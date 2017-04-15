// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.tree;

import com.newrelic.org.dom4j.Branch;
import com.newrelic.org.dom4j.IllegalAddException;
import com.newrelic.org.dom4j.QName;
import java.util.Map;
import com.newrelic.org.dom4j.ProcessingInstruction;
import com.newrelic.org.dom4j.Comment;
import com.newrelic.org.dom4j.Text;
import java.util.Iterator;
import java.util.List;
import com.newrelic.org.dom4j.DocumentType;
import com.newrelic.org.dom4j.Node;
import com.newrelic.org.dom4j.Visitor;
import java.io.IOException;
import java.io.Writer;
import com.newrelic.org.dom4j.io.XMLWriter;
import java.io.StringWriter;
import com.newrelic.org.dom4j.io.OutputFormat;
import com.newrelic.org.dom4j.Element;
import com.newrelic.org.dom4j.Document;

public abstract class AbstractDocument extends AbstractBranch implements Document
{
    protected String encoding;
    
    public short getNodeType() {
        return 9;
    }
    
    public String getPath(final Element context) {
        return "/";
    }
    
    public String getUniquePath(final Element context) {
        return "/";
    }
    
    public Document getDocument() {
        return this;
    }
    
    public String getXMLEncoding() {
        return null;
    }
    
    public String getStringValue() {
        final Element root = this.getRootElement();
        return (root != null) ? root.getStringValue() : "";
    }
    
    public String asXML() {
        final OutputFormat format = new OutputFormat();
        format.setEncoding(this.encoding);
        try {
            final StringWriter out = new StringWriter();
            final XMLWriter writer = new XMLWriter(out, format);
            writer.write(this);
            writer.flush();
            return out.toString();
        }
        catch (IOException e) {
            throw new RuntimeException("IOException while generating textual representation: " + e.getMessage());
        }
    }
    
    public void write(final Writer out) throws IOException {
        final OutputFormat format = new OutputFormat();
        format.setEncoding(this.encoding);
        final XMLWriter writer = new XMLWriter(out, format);
        writer.write(this);
    }
    
    public void accept(final Visitor visitor) {
        visitor.visit(this);
        final DocumentType docType = this.getDocType();
        if (docType != null) {
            visitor.visit(docType);
        }
        final List content = this.content();
        if (content != null) {
            for (final Object object : content) {
                if (object instanceof String) {
                    final Text text = this.getDocumentFactory().createText((String)object);
                    visitor.visit(text);
                }
                else {
                    final Node node = (Node)object;
                    node.accept(visitor);
                }
            }
        }
    }
    
    public String toString() {
        return super.toString() + " [Document: name " + this.getName() + "]";
    }
    
    public void normalize() {
        final Element element = this.getRootElement();
        if (element != null) {
            element.normalize();
        }
    }
    
    public Document addComment(final String comment) {
        final Comment node = this.getDocumentFactory().createComment(comment);
        this.add(node);
        return this;
    }
    
    public Document addProcessingInstruction(final String target, final String data) {
        final ProcessingInstruction node = this.getDocumentFactory().createProcessingInstruction(target, data);
        this.add(node);
        return this;
    }
    
    public Document addProcessingInstruction(final String target, final Map data) {
        final ProcessingInstruction node = this.getDocumentFactory().createProcessingInstruction(target, data);
        this.add(node);
        return this;
    }
    
    public Element addElement(final String name) {
        final Element element = this.getDocumentFactory().createElement(name);
        this.add(element);
        return element;
    }
    
    public Element addElement(final String qualifiedName, final String namespaceURI) {
        final Element element = this.getDocumentFactory().createElement(qualifiedName, namespaceURI);
        this.add(element);
        return element;
    }
    
    public Element addElement(final QName qName) {
        final Element element = this.getDocumentFactory().createElement(qName);
        this.add(element);
        return element;
    }
    
    public void setRootElement(final Element rootElement) {
        this.clearContent();
        if (rootElement != null) {
            super.add(rootElement);
            this.rootElementAdded(rootElement);
        }
    }
    
    public void add(final Element element) {
        this.checkAddElementAllowed(element);
        super.add(element);
        this.rootElementAdded(element);
    }
    
    public boolean remove(final Element element) {
        final boolean answer = super.remove(element);
        final Element root = this.getRootElement();
        if (root != null && answer) {
            this.setRootElement(null);
        }
        element.setDocument(null);
        return answer;
    }
    
    public Node asXPathResult(final Element parent) {
        return this;
    }
    
    protected void childAdded(final Node node) {
        if (node != null) {
            node.setDocument(this);
        }
    }
    
    protected void childRemoved(final Node node) {
        if (node != null) {
            node.setDocument(null);
        }
    }
    
    protected void checkAddElementAllowed(final Element element) {
        final Element root = this.getRootElement();
        if (root != null) {
            throw new IllegalAddException(this, element, "Cannot add another element to this Document as it already has a root element of: " + root.getQualifiedName());
        }
    }
    
    protected abstract void rootElementAdded(final Element p0);
    
    public void setXMLEncoding(final String enc) {
        this.encoding = enc;
    }
}
