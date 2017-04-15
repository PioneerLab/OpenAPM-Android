// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.tree;

import com.newrelic.org.dom4j.rule.Pattern;
import com.newrelic.org.dom4j.NodeFilter;
import java.util.List;
import com.newrelic.org.dom4j.XPath;
import java.io.IOException;
import java.io.Writer;
import com.newrelic.org.dom4j.Element;
import com.newrelic.org.dom4j.Document;
import com.newrelic.org.dom4j.DocumentFactory;
import java.io.Serializable;
import com.newrelic.org.dom4j.Node;

public abstract class AbstractNode implements Node, Cloneable, Serializable
{
    protected static final String[] NODE_TYPE_NAMES;
    private static final DocumentFactory DOCUMENT_FACTORY;
    
    public short getNodeType() {
        return 14;
    }
    
    public String getNodeTypeName() {
        final int type = this.getNodeType();
        if (type < 0 || type >= AbstractNode.NODE_TYPE_NAMES.length) {
            return "Unknown";
        }
        return AbstractNode.NODE_TYPE_NAMES[type];
    }
    
    public Document getDocument() {
        final Element element = this.getParent();
        return (element != null) ? element.getDocument() : null;
    }
    
    public void setDocument(final Document document) {
    }
    
    public Element getParent() {
        return null;
    }
    
    public void setParent(final Element parent) {
    }
    
    public boolean supportsParent() {
        return false;
    }
    
    public boolean isReadOnly() {
        return true;
    }
    
    public boolean hasContent() {
        return false;
    }
    
    public String getPath() {
        return this.getPath(null);
    }
    
    public String getUniquePath() {
        return this.getUniquePath(null);
    }
    
    public Object clone() {
        if (this.isReadOnly()) {
            return this;
        }
        try {
            final Node answer = (Node)super.clone();
            answer.setParent(null);
            answer.setDocument(null);
            return answer;
        }
        catch (CloneNotSupportedException e) {
            throw new RuntimeException("This should never happen. Caught: " + e);
        }
    }
    
    public Node detach() {
        final Element parent = this.getParent();
        if (parent != null) {
            parent.remove(this);
        }
        else {
            final Document document = this.getDocument();
            if (document != null) {
                document.remove(this);
            }
        }
        this.setParent(null);
        this.setDocument(null);
        return this;
    }
    
    public String getName() {
        return null;
    }
    
    public void setName(final String name) {
        throw new UnsupportedOperationException("This node cannot be modified");
    }
    
    public String getText() {
        return null;
    }
    
    public String getStringValue() {
        return this.getText();
    }
    
    public void setText(final String text) {
        throw new UnsupportedOperationException("This node cannot be modified");
    }
    
    public void write(final Writer writer) throws IOException {
        writer.write(this.asXML());
    }
    
    public Object selectObject(final String xpathExpression) {
        final XPath xpath = this.createXPath(xpathExpression);
        return xpath.evaluate(this);
    }
    
    public List selectNodes(final String xpathExpression) {
        final XPath xpath = this.createXPath(xpathExpression);
        return xpath.selectNodes(this);
    }
    
    public List selectNodes(final String xpathExpression, final String comparisonXPathExpression) {
        return this.selectNodes(xpathExpression, comparisonXPathExpression, false);
    }
    
    public List selectNodes(final String xpathExpression, final String comparisonXPathExpression, final boolean removeDuplicates) {
        final XPath xpath = this.createXPath(xpathExpression);
        final XPath sortBy = this.createXPath(comparisonXPathExpression);
        return xpath.selectNodes(this, sortBy, removeDuplicates);
    }
    
    public Node selectSingleNode(final String xpathExpression) {
        final XPath xpath = this.createXPath(xpathExpression);
        return xpath.selectSingleNode(this);
    }
    
    public String valueOf(final String xpathExpression) {
        final XPath xpath = this.createXPath(xpathExpression);
        return xpath.valueOf(this);
    }
    
    public Number numberValueOf(final String xpathExpression) {
        final XPath xpath = this.createXPath(xpathExpression);
        return xpath.numberValueOf(this);
    }
    
    public boolean matches(final String patternText) {
        final NodeFilter filter = this.createXPathFilter(patternText);
        return filter.matches(this);
    }
    
    public XPath createXPath(final String xpathExpression) {
        return this.getDocumentFactory().createXPath(xpathExpression);
    }
    
    public NodeFilter createXPathFilter(final String patternText) {
        return this.getDocumentFactory().createXPathFilter(patternText);
    }
    
    public Pattern createPattern(final String patternText) {
        return this.getDocumentFactory().createPattern(patternText);
    }
    
    public Node asXPathResult(final Element parent) {
        if (this.supportsParent()) {
            return this;
        }
        return this.createXPathResult(parent);
    }
    
    protected DocumentFactory getDocumentFactory() {
        return AbstractNode.DOCUMENT_FACTORY;
    }
    
    protected Node createXPathResult(final Element parent) {
        throw new RuntimeException("asXPathResult() not yet implemented fully for: " + this);
    }
    
    static {
        NODE_TYPE_NAMES = new String[] { "Node", "Element", "Attribute", "Text", "CDATA", "Entity", "Entity", "ProcessingInstruction", "Comment", "Document", "DocumentType", "DocumentFragment", "Notation", "Namespace", "Unknown" };
        DOCUMENT_FACTORY = DocumentFactory.getInstance();
    }
}
