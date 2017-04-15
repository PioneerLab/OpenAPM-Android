// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.tree;

import com.newrelic.org.dom4j.IllegalAddException;
import java.util.ArrayList;
import com.newrelic.org.dom4j.Comment;
import com.newrelic.org.dom4j.Namespace;
import com.newrelic.org.dom4j.QName;
import com.newrelic.org.dom4j.Element;
import java.util.Iterator;
import com.newrelic.org.dom4j.ProcessingInstruction;
import java.util.StringTokenizer;
import com.newrelic.org.dom4j.Node;
import java.util.List;
import com.newrelic.org.dom4j.Branch;

public abstract class AbstractBranch extends AbstractNode implements Branch
{
    protected static final int DEFAULT_CONTENT_LIST_SIZE = 5;
    
    public boolean isReadOnly() {
        return false;
    }
    
    public boolean hasContent() {
        return this.nodeCount() > 0;
    }
    
    public List content() {
        final List backingList = this.contentList();
        return new ContentListFacade(this, backingList);
    }
    
    public String getText() {
        final List content = this.contentList();
        if (content != null) {
            final int size = content.size();
            if (size >= 1) {
                final Object first = content.get(0);
                final String firstText = this.getContentAsText(first);
                if (size == 1) {
                    return firstText;
                }
                final StringBuffer buffer = new StringBuffer(firstText);
                for (int i = 1; i < size; ++i) {
                    final Object node = content.get(i);
                    buffer.append(this.getContentAsText(node));
                }
                return buffer.toString();
            }
        }
        return "";
    }
    
    protected String getContentAsText(final Object content) {
        if (content instanceof Node) {
            final Node node = (Node)content;
            switch (node.getNodeType()) {
                case 3:
                case 4:
                case 5: {
                    return node.getText();
                }
            }
        }
        else if (content instanceof String) {
            return (String)content;
        }
        return "";
    }
    
    protected String getContentAsStringValue(final Object content) {
        if (content instanceof Node) {
            final Node node = (Node)content;
            switch (node.getNodeType()) {
                case 1:
                case 3:
                case 4:
                case 5: {
                    return node.getStringValue();
                }
            }
        }
        else if (content instanceof String) {
            return (String)content;
        }
        return "";
    }
    
    public String getTextTrim() {
        final String text = this.getText();
        final StringBuffer textContent = new StringBuffer();
        final StringTokenizer tokenizer = new StringTokenizer(text);
        while (tokenizer.hasMoreTokens()) {
            final String str = tokenizer.nextToken();
            textContent.append(str);
            if (tokenizer.hasMoreTokens()) {
                textContent.append(" ");
            }
        }
        return textContent.toString();
    }
    
    public void setProcessingInstructions(final List listOfPIs) {
        for (final ProcessingInstruction pi : listOfPIs) {
            this.addNode(pi);
        }
    }
    
    public Element addElement(final String name) {
        final Element node = this.getDocumentFactory().createElement(name);
        this.add(node);
        return node;
    }
    
    public Element addElement(final String qualifiedName, final String namespaceURI) {
        final Element node = this.getDocumentFactory().createElement(qualifiedName, namespaceURI);
        this.add(node);
        return node;
    }
    
    public Element addElement(final QName qname) {
        final Element node = this.getDocumentFactory().createElement(qname);
        this.add(node);
        return node;
    }
    
    public Element addElement(final String name, final String prefix, final String uri) {
        final Namespace namespace = Namespace.get(prefix, uri);
        final QName qName = this.getDocumentFactory().createQName(name, namespace);
        return this.addElement(qName);
    }
    
    public void add(final Node node) {
        switch (node.getNodeType()) {
            case 1: {
                this.add((Element)node);
                break;
            }
            case 8: {
                this.add((Comment)node);
                break;
            }
            case 7: {
                this.add((ProcessingInstruction)node);
                break;
            }
            default: {
                this.invalidNodeTypeAddException(node);
                break;
            }
        }
    }
    
    public boolean remove(final Node node) {
        switch (node.getNodeType()) {
            case 1: {
                return this.remove((Element)node);
            }
            case 8: {
                return this.remove((Comment)node);
            }
            case 7: {
                return this.remove((ProcessingInstruction)node);
            }
            default: {
                this.invalidNodeTypeAddException(node);
                return false;
            }
        }
    }
    
    public void add(final Comment comment) {
        this.addNode(comment);
    }
    
    public void add(final Element element) {
        this.addNode(element);
    }
    
    public void add(final ProcessingInstruction pi) {
        this.addNode(pi);
    }
    
    public boolean remove(final Comment comment) {
        return this.removeNode(comment);
    }
    
    public boolean remove(final Element element) {
        return this.removeNode(element);
    }
    
    public boolean remove(final ProcessingInstruction pi) {
        return this.removeNode(pi);
    }
    
    public Element elementByID(final String elementID) {
        for (int i = 0, size = this.nodeCount(); i < size; ++i) {
            final Node node = this.node(i);
            if (node instanceof Element) {
                Element element = (Element)node;
                final String id = this.elementID(element);
                if (id != null && id.equals(elementID)) {
                    return element;
                }
                element = element.elementByID(elementID);
                if (element != null) {
                    return element;
                }
            }
        }
        return null;
    }
    
    public void appendContent(final Branch branch) {
        for (int i = 0, size = branch.nodeCount(); i < size; ++i) {
            final Node node = branch.node(i);
            this.add((Node)node.clone());
        }
    }
    
    public Node node(final int index) {
        final Object object = this.contentList().get(index);
        if (object instanceof Node) {
            return (Node)object;
        }
        if (object instanceof String) {
            return this.getDocumentFactory().createText(object.toString());
        }
        return null;
    }
    
    public int nodeCount() {
        return this.contentList().size();
    }
    
    public int indexOf(final Node node) {
        return this.contentList().indexOf(node);
    }
    
    public Iterator nodeIterator() {
        return this.contentList().iterator();
    }
    
    protected String elementID(final Element element) {
        return element.attributeValue("ID");
    }
    
    protected abstract List contentList();
    
    protected List createContentList() {
        return new ArrayList(5);
    }
    
    protected List createContentList(final int size) {
        return new ArrayList(size);
    }
    
    protected BackedList createResultList() {
        return new BackedList(this, this.contentList());
    }
    
    protected List createSingleResultList(final Object result) {
        final BackedList list = new BackedList(this, this.contentList(), 1);
        list.addLocal(result);
        return list;
    }
    
    protected List createEmptyList() {
        return new BackedList(this, this.contentList(), 0);
    }
    
    protected abstract void addNode(final Node p0);
    
    protected abstract void addNode(final int p0, final Node p1);
    
    protected abstract boolean removeNode(final Node p0);
    
    protected abstract void childAdded(final Node p0);
    
    protected abstract void childRemoved(final Node p0);
    
    protected void contentRemoved() {
        final List content = this.contentList();
        for (int i = 0, size = content.size(); i < size; ++i) {
            final Object object = content.get(i);
            if (object instanceof Node) {
                this.childRemoved((Node)object);
            }
        }
    }
    
    protected void invalidNodeTypeAddException(final Node node) {
        throw new IllegalAddException("Invalid node type. Cannot add node: " + node + " to this branch: " + this);
    }
}
