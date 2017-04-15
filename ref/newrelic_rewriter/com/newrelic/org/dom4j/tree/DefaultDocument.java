// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.tree;

import java.util.Collections;
import com.newrelic.org.dom4j.IllegalAddException;
import com.newrelic.org.dom4j.Node;
import com.newrelic.org.dom4j.ProcessingInstruction;
import com.newrelic.org.dom4j.Branch;
import com.newrelic.org.dom4j.Document;
import org.xml.sax.EntityResolver;
import com.newrelic.org.dom4j.DocumentFactory;
import com.newrelic.org.dom4j.DocumentType;
import com.newrelic.org.dom4j.Element;
import java.util.Iterator;
import java.util.List;

public class DefaultDocument extends AbstractDocument
{
    protected static final List EMPTY_LIST;
    protected static final Iterator EMPTY_ITERATOR;
    private String name;
    private Element rootElement;
    private List content;
    private DocumentType docType;
    private DocumentFactory documentFactory;
    private transient EntityResolver entityResolver;
    
    public DefaultDocument() {
        this.documentFactory = DocumentFactory.getInstance();
    }
    
    public DefaultDocument(final String name) {
        this.documentFactory = DocumentFactory.getInstance();
        this.name = name;
    }
    
    public DefaultDocument(final Element rootElement) {
        this.documentFactory = DocumentFactory.getInstance();
        this.rootElement = rootElement;
    }
    
    public DefaultDocument(final DocumentType docType) {
        this.documentFactory = DocumentFactory.getInstance();
        this.docType = docType;
    }
    
    public DefaultDocument(final Element rootElement, final DocumentType docType) {
        this.documentFactory = DocumentFactory.getInstance();
        this.rootElement = rootElement;
        this.docType = docType;
    }
    
    public DefaultDocument(final String name, final Element rootElement, final DocumentType docType) {
        this.documentFactory = DocumentFactory.getInstance();
        this.name = name;
        this.rootElement = rootElement;
        this.docType = docType;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public Element getRootElement() {
        return this.rootElement;
    }
    
    public DocumentType getDocType() {
        return this.docType;
    }
    
    public void setDocType(final DocumentType docType) {
        this.docType = docType;
    }
    
    public Document addDocType(final String docTypeName, final String publicId, final String systemId) {
        this.setDocType(this.getDocumentFactory().createDocType(docTypeName, publicId, systemId));
        return this;
    }
    
    public String getXMLEncoding() {
        return this.encoding;
    }
    
    public EntityResolver getEntityResolver() {
        return this.entityResolver;
    }
    
    public void setEntityResolver(final EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }
    
    public Object clone() {
        final DefaultDocument document = (DefaultDocument)super.clone();
        document.rootElement = null;
        document.content = null;
        document.appendContent(this);
        return document;
    }
    
    public List processingInstructions() {
        final List source = this.contentList();
        final List answer = this.createResultList();
        for (int size = source.size(), i = 0; i < size; ++i) {
            final Object object = source.get(i);
            if (object instanceof ProcessingInstruction) {
                answer.add(object);
            }
        }
        return answer;
    }
    
    public List processingInstructions(final String target) {
        final List source = this.contentList();
        final List answer = this.createResultList();
        for (int size = source.size(), i = 0; i < size; ++i) {
            final Object object = source.get(i);
            if (object instanceof ProcessingInstruction) {
                final ProcessingInstruction pi = (ProcessingInstruction)object;
                if (target.equals(pi.getName())) {
                    answer.add(pi);
                }
            }
        }
        return answer;
    }
    
    public ProcessingInstruction processingInstruction(final String target) {
        final List source = this.contentList();
        for (int size = source.size(), i = 0; i < size; ++i) {
            final Object object = source.get(i);
            if (object instanceof ProcessingInstruction) {
                final ProcessingInstruction pi = (ProcessingInstruction)object;
                if (target.equals(pi.getName())) {
                    return pi;
                }
            }
        }
        return null;
    }
    
    public boolean removeProcessingInstruction(final String target) {
        final List source = this.contentList();
        final Iterator iter = source.iterator();
        while (iter.hasNext()) {
            final Object object = iter.next();
            if (object instanceof ProcessingInstruction) {
                final ProcessingInstruction pi = (ProcessingInstruction)object;
                if (target.equals(pi.getName())) {
                    iter.remove();
                    return true;
                }
                continue;
            }
        }
        return false;
    }
    
    public void setContent(List content) {
        this.rootElement = null;
        this.contentRemoved();
        if (content instanceof ContentListFacade) {
            content = ((ContentListFacade)content).getBackingList();
        }
        if (content == null) {
            this.content = null;
        }
        else {
            final int size = content.size();
            final List newContent = this.createContentList(size);
            for (int i = 0; i < size; ++i) {
                final Object object = content.get(i);
                if (object instanceof Node) {
                    Node node = (Node)object;
                    final Document doc = node.getDocument();
                    if (doc != null && doc != this) {
                        node = (Node)node.clone();
                    }
                    if (node instanceof Element) {
                        if (this.rootElement != null) {
                            throw new IllegalAddException("A document may only contain one root element: " + content);
                        }
                        this.rootElement = (Element)node;
                    }
                    newContent.add(node);
                    this.childAdded(node);
                }
            }
            this.content = newContent;
        }
    }
    
    public void clearContent() {
        this.contentRemoved();
        this.content = null;
        this.rootElement = null;
    }
    
    public void setDocumentFactory(final DocumentFactory documentFactory) {
        this.documentFactory = documentFactory;
    }
    
    protected List contentList() {
        if (this.content == null) {
            this.content = this.createContentList();
            if (this.rootElement != null) {
                this.content.add(this.rootElement);
            }
        }
        return this.content;
    }
    
    protected void addNode(final Node node) {
        if (node != null) {
            final Document document = node.getDocument();
            if (document != null && document != this) {
                final String message = "The Node already has an existing document: " + document;
                throw new IllegalAddException(this, node, message);
            }
            this.contentList().add(node);
            this.childAdded(node);
        }
    }
    
    protected void addNode(final int index, final Node node) {
        if (node != null) {
            final Document document = node.getDocument();
            if (document != null && document != this) {
                final String message = "The Node already has an existing document: " + document;
                throw new IllegalAddException(this, node, message);
            }
            this.contentList().add(index, node);
            this.childAdded(node);
        }
    }
    
    protected boolean removeNode(final Node node) {
        if (node == this.rootElement) {
            this.rootElement = null;
        }
        if (this.contentList().remove(node)) {
            this.childRemoved(node);
            return true;
        }
        return false;
    }
    
    protected void rootElementAdded(final Element element) {
        (this.rootElement = element).setDocument(this);
    }
    
    protected DocumentFactory getDocumentFactory() {
        return this.documentFactory;
    }
    
    static {
        EMPTY_LIST = Collections.EMPTY_LIST;
        EMPTY_ITERATOR = DefaultDocument.EMPTY_LIST.iterator();
    }
}
