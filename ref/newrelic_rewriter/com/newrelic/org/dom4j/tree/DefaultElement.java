// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.tree;

import com.newrelic.org.dom4j.IllegalAddException;
import com.newrelic.org.dom4j.Attribute;
import com.newrelic.org.dom4j.Node;
import java.util.Iterator;
import com.newrelic.org.dom4j.ProcessingInstruction;
import java.util.List;
import com.newrelic.org.dom4j.Document;
import com.newrelic.org.dom4j.Element;
import com.newrelic.org.dom4j.Namespace;
import java.util.ArrayList;
import com.newrelic.org.dom4j.Branch;
import com.newrelic.org.dom4j.QName;
import com.newrelic.org.dom4j.DocumentFactory;

public class DefaultElement extends AbstractElement
{
    private static final transient DocumentFactory DOCUMENT_FACTORY;
    private QName qname;
    private Branch parentBranch;
    private Object content;
    private Object attributes;
    
    public DefaultElement(final String name) {
        this.qname = DefaultElement.DOCUMENT_FACTORY.createQName(name);
    }
    
    public DefaultElement(final QName qname) {
        this.qname = qname;
    }
    
    public DefaultElement(final QName qname, final int attributeCount) {
        this.qname = qname;
        if (attributeCount > 1) {
            this.attributes = new ArrayList(attributeCount);
        }
    }
    
    public DefaultElement(final String name, final Namespace namespace) {
        this.qname = DefaultElement.DOCUMENT_FACTORY.createQName(name, namespace);
    }
    
    public Element getParent() {
        Element result = null;
        if (this.parentBranch instanceof Element) {
            result = (Element)this.parentBranch;
        }
        return result;
    }
    
    public void setParent(final Element parent) {
        if (this.parentBranch instanceof Element || parent != null) {
            this.parentBranch = parent;
        }
    }
    
    public Document getDocument() {
        if (this.parentBranch instanceof Document) {
            return (Document)this.parentBranch;
        }
        if (this.parentBranch instanceof Element) {
            final Element parent = (Element)this.parentBranch;
            return parent.getDocument();
        }
        return null;
    }
    
    public void setDocument(final Document document) {
        if (this.parentBranch instanceof Document || document != null) {
            this.parentBranch = document;
        }
    }
    
    public boolean supportsParent() {
        return true;
    }
    
    public QName getQName() {
        return this.qname;
    }
    
    public void setQName(final QName name) {
        this.qname = name;
    }
    
    public String getText() {
        final Object contentShadow = this.content;
        if (contentShadow instanceof List) {
            return super.getText();
        }
        if (contentShadow != null) {
            return this.getContentAsText(contentShadow);
        }
        return "";
    }
    
    public String getStringValue() {
        final Object contentShadow = this.content;
        if (contentShadow instanceof List) {
            final List list = (List)contentShadow;
            final int size = list.size();
            if (size > 0) {
                if (size == 1) {
                    return this.getContentAsStringValue(list.get(0));
                }
                final StringBuffer buffer = new StringBuffer();
                for (int i = 0; i < size; ++i) {
                    final Object node = list.get(i);
                    final String string = this.getContentAsStringValue(node);
                    if (string.length() > 0) {
                        buffer.append(string);
                    }
                }
                return buffer.toString();
            }
        }
        else if (contentShadow != null) {
            return this.getContentAsStringValue(contentShadow);
        }
        return "";
    }
    
    public Object clone() {
        final DefaultElement answer = (DefaultElement)super.clone();
        if (answer != this) {
            answer.content = null;
            answer.attributes = null;
            answer.appendAttributes(this);
            answer.appendContent(this);
        }
        return answer;
    }
    
    public Namespace getNamespaceForPrefix(String prefix) {
        if (prefix == null) {
            prefix = "";
        }
        if (prefix.equals(this.getNamespacePrefix())) {
            return this.getNamespace();
        }
        if (prefix.equals("xml")) {
            return Namespace.XML_NAMESPACE;
        }
        final Object contentShadow = this.content;
        if (contentShadow instanceof List) {
            final List list = (List)contentShadow;
            for (int size = list.size(), i = 0; i < size; ++i) {
                final Object object = list.get(i);
                if (object instanceof Namespace) {
                    final Namespace namespace = (Namespace)object;
                    if (prefix.equals(namespace.getPrefix())) {
                        return namespace;
                    }
                }
            }
        }
        else if (contentShadow instanceof Namespace) {
            final Namespace namespace2 = (Namespace)contentShadow;
            if (prefix.equals(namespace2.getPrefix())) {
                return namespace2;
            }
        }
        final Element parent = this.getParent();
        if (parent != null) {
            final Namespace answer = parent.getNamespaceForPrefix(prefix);
            if (answer != null) {
                return answer;
            }
        }
        if (prefix == null || prefix.length() <= 0) {
            return Namespace.NO_NAMESPACE;
        }
        return null;
    }
    
    public Namespace getNamespaceForURI(final String uri) {
        if (uri == null || uri.length() <= 0) {
            return Namespace.NO_NAMESPACE;
        }
        if (uri.equals(this.getNamespaceURI())) {
            return this.getNamespace();
        }
        final Object contentShadow = this.content;
        if (contentShadow instanceof List) {
            final List list = (List)contentShadow;
            for (int size = list.size(), i = 0; i < size; ++i) {
                final Object object = list.get(i);
                if (object instanceof Namespace) {
                    final Namespace namespace = (Namespace)object;
                    if (uri.equals(namespace.getURI())) {
                        return namespace;
                    }
                }
            }
        }
        else if (contentShadow instanceof Namespace) {
            final Namespace namespace2 = (Namespace)contentShadow;
            if (uri.equals(namespace2.getURI())) {
                return namespace2;
            }
        }
        final Element parent = this.getParent();
        if (parent != null) {
            return parent.getNamespaceForURI(uri);
        }
        return null;
    }
    
    public List declaredNamespaces() {
        final BackedList answer = this.createResultList();
        final Object contentShadow = this.content;
        if (contentShadow instanceof List) {
            final List list = (List)contentShadow;
            for (int size = list.size(), i = 0; i < size; ++i) {
                final Object object = list.get(i);
                if (object instanceof Namespace) {
                    answer.addLocal(object);
                }
            }
        }
        else if (contentShadow instanceof Namespace) {
            answer.addLocal(contentShadow);
        }
        return answer;
    }
    
    public List additionalNamespaces() {
        final Object contentShadow = this.content;
        if (contentShadow instanceof List) {
            final List list = (List)contentShadow;
            final int size = list.size();
            final BackedList answer = this.createResultList();
            for (int i = 0; i < size; ++i) {
                final Object object = list.get(i);
                if (object instanceof Namespace) {
                    final Namespace namespace = (Namespace)object;
                    if (!namespace.equals(this.getNamespace())) {
                        answer.addLocal(namespace);
                    }
                }
            }
            return answer;
        }
        if (!(contentShadow instanceof Namespace)) {
            return this.createEmptyList();
        }
        final Namespace namespace2 = (Namespace)contentShadow;
        if (namespace2.equals(this.getNamespace())) {
            return this.createEmptyList();
        }
        return this.createSingleResultList(namespace2);
    }
    
    public List additionalNamespaces(final String defaultNamespaceURI) {
        final Object contentShadow = this.content;
        if (contentShadow instanceof List) {
            final List list = (List)contentShadow;
            final BackedList answer = this.createResultList();
            for (int size = list.size(), i = 0; i < size; ++i) {
                final Object object = list.get(i);
                if (object instanceof Namespace) {
                    final Namespace namespace = (Namespace)object;
                    if (!defaultNamespaceURI.equals(namespace.getURI())) {
                        answer.addLocal(namespace);
                    }
                }
            }
            return answer;
        }
        if (contentShadow instanceof Namespace) {
            final Namespace namespace2 = (Namespace)contentShadow;
            if (!defaultNamespaceURI.equals(namespace2.getURI())) {
                return this.createSingleResultList(namespace2);
            }
        }
        return this.createEmptyList();
    }
    
    public List processingInstructions() {
        final Object contentShadow = this.content;
        if (contentShadow instanceof List) {
            final List list = (List)contentShadow;
            final BackedList answer = this.createResultList();
            for (int size = list.size(), i = 0; i < size; ++i) {
                final Object object = list.get(i);
                if (object instanceof ProcessingInstruction) {
                    answer.addLocal(object);
                }
            }
            return answer;
        }
        if (contentShadow instanceof ProcessingInstruction) {
            return this.createSingleResultList(contentShadow);
        }
        return this.createEmptyList();
    }
    
    public List processingInstructions(final String target) {
        final Object shadow = this.content;
        if (shadow instanceof List) {
            final List list = (List)shadow;
            final BackedList answer = this.createResultList();
            for (int size = list.size(), i = 0; i < size; ++i) {
                final Object object = list.get(i);
                if (object instanceof ProcessingInstruction) {
                    final ProcessingInstruction pi = (ProcessingInstruction)object;
                    if (target.equals(pi.getName())) {
                        answer.addLocal(pi);
                    }
                }
            }
            return answer;
        }
        if (shadow instanceof ProcessingInstruction) {
            final ProcessingInstruction pi2 = (ProcessingInstruction)shadow;
            if (target.equals(pi2.getName())) {
                return this.createSingleResultList(pi2);
            }
        }
        return this.createEmptyList();
    }
    
    public ProcessingInstruction processingInstruction(final String target) {
        final Object shadow = this.content;
        if (shadow instanceof List) {
            final List list = (List)shadow;
            for (int size = list.size(), i = 0; i < size; ++i) {
                final Object object = list.get(i);
                if (object instanceof ProcessingInstruction) {
                    final ProcessingInstruction pi = (ProcessingInstruction)object;
                    if (target.equals(pi.getName())) {
                        return pi;
                    }
                }
            }
        }
        else if (shadow instanceof ProcessingInstruction) {
            final ProcessingInstruction pi2 = (ProcessingInstruction)shadow;
            if (target.equals(pi2.getName())) {
                return pi2;
            }
        }
        return null;
    }
    
    public boolean removeProcessingInstruction(final String target) {
        final Object shadow = this.content;
        if (shadow instanceof List) {
            final List list = (List)shadow;
            final Iterator iter = list.iterator();
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
        }
        else if (shadow instanceof ProcessingInstruction) {
            final ProcessingInstruction pi2 = (ProcessingInstruction)shadow;
            if (target.equals(pi2.getName())) {
                this.content = null;
                return true;
            }
        }
        return false;
    }
    
    public Element element(final String name) {
        final Object contentShadow = this.content;
        if (contentShadow instanceof List) {
            final List list = (List)contentShadow;
            for (int size = list.size(), i = 0; i < size; ++i) {
                final Object object = list.get(i);
                if (object instanceof Element) {
                    final Element element = (Element)object;
                    if (name.equals(element.getName())) {
                        return element;
                    }
                }
            }
        }
        else if (contentShadow instanceof Element) {
            final Element element2 = (Element)contentShadow;
            if (name.equals(element2.getName())) {
                return element2;
            }
        }
        return null;
    }
    
    public Element element(final QName qName) {
        final Object contentShadow = this.content;
        if (contentShadow instanceof List) {
            final List list = (List)contentShadow;
            for (int size = list.size(), i = 0; i < size; ++i) {
                final Object object = list.get(i);
                if (object instanceof Element) {
                    final Element element = (Element)object;
                    if (qName.equals(element.getQName())) {
                        return element;
                    }
                }
            }
        }
        else if (contentShadow instanceof Element) {
            final Element element2 = (Element)contentShadow;
            if (qName.equals(element2.getQName())) {
                return element2;
            }
        }
        return null;
    }
    
    public Element element(final String name, final Namespace namespace) {
        return this.element(this.getDocumentFactory().createQName(name, namespace));
    }
    
    public void setContent(List content) {
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
                    final Element parent = node.getParent();
                    if (parent != null && parent != this) {
                        node = (Node)node.clone();
                    }
                    newContent.add(node);
                    this.childAdded(node);
                }
                else if (object != null) {
                    final String text = object.toString();
                    final Node node2 = this.getDocumentFactory().createText(text);
                    newContent.add(node2);
                    this.childAdded(node2);
                }
            }
            this.content = newContent;
        }
    }
    
    public void clearContent() {
        if (this.content != null) {
            this.contentRemoved();
            this.content = null;
        }
    }
    
    public Node node(final int index) {
        if (index >= 0) {
            final Object contentShadow = this.content;
            Object node;
            if (contentShadow instanceof List) {
                final List list = (List)contentShadow;
                if (index >= list.size()) {
                    return null;
                }
                node = list.get(index);
            }
            else {
                node = ((index == 0) ? contentShadow : null);
            }
            if (node != null) {
                if (node instanceof Node) {
                    return (Node)node;
                }
                return new DefaultText(node.toString());
            }
        }
        return null;
    }
    
    public int indexOf(final Node node) {
        final Object contentShadow = this.content;
        if (contentShadow instanceof List) {
            final List list = (List)contentShadow;
            return list.indexOf(node);
        }
        if (contentShadow != null && contentShadow.equals(node)) {
            return 0;
        }
        return -1;
    }
    
    public int nodeCount() {
        final Object contentShadow = this.content;
        if (contentShadow instanceof List) {
            final List list = (List)contentShadow;
            return list.size();
        }
        return (contentShadow != null) ? 1 : 0;
    }
    
    public Iterator nodeIterator() {
        final Object contentShadow = this.content;
        if (contentShadow instanceof List) {
            final List list = (List)contentShadow;
            return list.iterator();
        }
        if (contentShadow != null) {
            return this.createSingleIterator(contentShadow);
        }
        return DefaultElement.EMPTY_ITERATOR;
    }
    
    public List attributes() {
        return new ContentListFacade(this, this.attributeList());
    }
    
    public void setAttributes(List attributes) {
        if (attributes instanceof ContentListFacade) {
            attributes = ((ContentListFacade)attributes).getBackingList();
        }
        this.attributes = attributes;
    }
    
    public Iterator attributeIterator() {
        final Object attributesShadow = this.attributes;
        if (attributesShadow instanceof List) {
            final List list = (List)attributesShadow;
            return list.iterator();
        }
        if (attributesShadow != null) {
            return this.createSingleIterator(attributesShadow);
        }
        return DefaultElement.EMPTY_ITERATOR;
    }
    
    public Attribute attribute(final int index) {
        final Object attributesShadow = this.attributes;
        if (attributesShadow instanceof List) {
            final List list = (List)attributesShadow;
            return list.get(index);
        }
        if (attributesShadow != null && index == 0) {
            return (Attribute)attributesShadow;
        }
        return null;
    }
    
    public int attributeCount() {
        final Object attributesShadow = this.attributes;
        if (attributesShadow instanceof List) {
            final List list = (List)attributesShadow;
            return list.size();
        }
        return (attributesShadow != null) ? 1 : 0;
    }
    
    public Attribute attribute(final String name) {
        final Object attributesShadow = this.attributes;
        if (attributesShadow instanceof List) {
            final List list = (List)attributesShadow;
            for (int size = list.size(), i = 0; i < size; ++i) {
                final Attribute attribute = list.get(i);
                if (name.equals(attribute.getName())) {
                    return attribute;
                }
            }
        }
        else if (attributesShadow != null) {
            final Attribute attribute2 = (Attribute)attributesShadow;
            if (name.equals(attribute2.getName())) {
                return attribute2;
            }
        }
        return null;
    }
    
    public Attribute attribute(final QName qName) {
        final Object attributesShadow = this.attributes;
        if (attributesShadow instanceof List) {
            final List list = (List)attributesShadow;
            for (int size = list.size(), i = 0; i < size; ++i) {
                final Attribute attribute = list.get(i);
                if (qName.equals(attribute.getQName())) {
                    return attribute;
                }
            }
        }
        else if (attributesShadow != null) {
            final Attribute attribute2 = (Attribute)attributesShadow;
            if (qName.equals(attribute2.getQName())) {
                return attribute2;
            }
        }
        return null;
    }
    
    public Attribute attribute(final String name, final Namespace namespace) {
        return this.attribute(this.getDocumentFactory().createQName(name, namespace));
    }
    
    public void add(final Attribute attribute) {
        if (attribute.getParent() != null) {
            final String message = "The Attribute already has an existing parent \"" + attribute.getParent().getQualifiedName() + "\"";
            throw new IllegalAddException(this, attribute, message);
        }
        if (attribute.getValue() == null) {
            final Attribute oldAttribute = this.attribute(attribute.getQName());
            if (oldAttribute != null) {
                this.remove(oldAttribute);
            }
        }
        else {
            if (this.attributes == null) {
                this.attributes = attribute;
            }
            else {
                this.attributeList().add(attribute);
            }
            this.childAdded(attribute);
        }
    }
    
    public boolean remove(final Attribute attribute) {
        boolean answer = false;
        final Object attributesShadow = this.attributes;
        if (attributesShadow instanceof List) {
            final List list = (List)attributesShadow;
            answer = list.remove(attribute);
            if (!answer) {
                final Attribute copy = this.attribute(attribute.getQName());
                if (copy != null) {
                    list.remove(copy);
                    answer = true;
                }
            }
        }
        else if (attributesShadow != null) {
            if (attribute.equals(attributesShadow)) {
                this.attributes = null;
                answer = true;
            }
            else {
                final Attribute other = (Attribute)attributesShadow;
                if (attribute.getQName().equals(other.getQName())) {
                    this.attributes = null;
                    answer = true;
                }
            }
        }
        if (answer) {
            this.childRemoved(attribute);
        }
        return answer;
    }
    
    protected void addNewNode(final Node node) {
        final Object contentShadow = this.content;
        if (contentShadow == null) {
            this.content = node;
        }
        else if (contentShadow instanceof List) {
            final List list = (List)contentShadow;
            list.add(node);
        }
        else {
            final List list = this.createContentList();
            list.add(contentShadow);
            list.add(node);
            this.content = list;
        }
        this.childAdded(node);
    }
    
    protected boolean removeNode(final Node node) {
        boolean answer = false;
        final Object contentShadow = this.content;
        if (contentShadow != null) {
            if (contentShadow == node) {
                this.content = null;
                answer = true;
            }
            else if (contentShadow instanceof List) {
                final List list = (List)contentShadow;
                answer = list.remove(node);
            }
        }
        if (answer) {
            this.childRemoved(node);
        }
        return answer;
    }
    
    protected List contentList() {
        final Object contentShadow = this.content;
        if (contentShadow instanceof List) {
            return (List)contentShadow;
        }
        final List list = this.createContentList();
        if (contentShadow != null) {
            list.add(contentShadow);
        }
        return (List)(this.content = list);
    }
    
    protected List attributeList() {
        final Object attributesShadow = this.attributes;
        if (attributesShadow instanceof List) {
            return (List)attributesShadow;
        }
        if (attributesShadow != null) {
            final List list = this.createAttributeList();
            list.add(attributesShadow);
            return (List)(this.attributes = list);
        }
        final List list = this.createAttributeList();
        return (List)(this.attributes = list);
    }
    
    protected List attributeList(final int size) {
        final Object attributesShadow = this.attributes;
        if (attributesShadow instanceof List) {
            return (List)attributesShadow;
        }
        if (attributesShadow != null) {
            final List list = this.createAttributeList(size);
            list.add(attributesShadow);
            return (List)(this.attributes = list);
        }
        final List list = this.createAttributeList(size);
        return (List)(this.attributes = list);
    }
    
    protected void setAttributeList(final List attributeList) {
        this.attributes = attributeList;
    }
    
    protected DocumentFactory getDocumentFactory() {
        final DocumentFactory factory = this.qname.getDocumentFactory();
        return (factory != null) ? factory : DefaultElement.DOCUMENT_FACTORY;
    }
    
    static {
        DOCUMENT_FACTORY = DocumentFactory.getInstance();
    }
}
