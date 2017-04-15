// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.tree;

import java.util.Collections;
import java.util.ArrayList;
import com.newrelic.org.dom4j.Branch;
import com.newrelic.org.dom4j.CharacterData;
import com.newrelic.org.dom4j.Text;
import java.util.Map;
import com.newrelic.org.dom4j.Entity;
import com.newrelic.org.dom4j.Comment;
import com.newrelic.org.dom4j.CDATA;
import com.newrelic.org.dom4j.ProcessingInstruction;
import com.newrelic.org.dom4j.IllegalAddException;
import org.xml.sax.Attributes;
import com.newrelic.org.dom4j.QName;
import com.newrelic.org.dom4j.Node;
import com.newrelic.org.dom4j.Attribute;
import com.newrelic.org.dom4j.Visitor;
import java.io.IOException;
import java.io.Writer;
import com.newrelic.org.dom4j.io.XMLWriter;
import com.newrelic.org.dom4j.io.OutputFormat;
import java.io.StringWriter;
import com.newrelic.org.dom4j.Namespace;
import com.newrelic.org.dom4j.Document;
import java.util.Iterator;
import java.util.List;
import com.newrelic.org.dom4j.DocumentFactory;
import com.newrelic.org.dom4j.Element;

public abstract class AbstractElement extends AbstractBranch implements Element
{
    private static final DocumentFactory DOCUMENT_FACTORY;
    protected static final List EMPTY_LIST;
    protected static final Iterator EMPTY_ITERATOR;
    protected static final boolean VERBOSE_TOSTRING = false;
    protected static final boolean USE_STRINGVALUE_SEPARATOR = false;
    
    public short getNodeType() {
        return 1;
    }
    
    public boolean isRootElement() {
        final Document document = this.getDocument();
        if (document != null) {
            final Element root = document.getRootElement();
            if (root == this) {
                return true;
            }
        }
        return false;
    }
    
    public void setName(final String name) {
        this.setQName(this.getDocumentFactory().createQName(name));
    }
    
    public void setNamespace(final Namespace namespace) {
        this.setQName(this.getDocumentFactory().createQName(this.getName(), namespace));
    }
    
    public String getXPathNameStep() {
        final String uri = this.getNamespaceURI();
        if (uri == null || uri.length() == 0) {
            return this.getName();
        }
        final String prefix = this.getNamespacePrefix();
        if (prefix == null || prefix.length() == 0) {
            return "*[name()='" + this.getName() + "']";
        }
        return this.getQualifiedName();
    }
    
    public String getPath(final Element context) {
        if (this == context) {
            return ".";
        }
        final Element parent = this.getParent();
        if (parent == null) {
            return "/" + this.getXPathNameStep();
        }
        if (parent == context) {
            return this.getXPathNameStep();
        }
        return parent.getPath(context) + "/" + this.getXPathNameStep();
    }
    
    public String getUniquePath(final Element context) {
        final Element parent = this.getParent();
        if (parent == null) {
            return "/" + this.getXPathNameStep();
        }
        final StringBuffer buffer = new StringBuffer();
        if (parent != context) {
            buffer.append(parent.getUniquePath(context));
            buffer.append("/");
        }
        buffer.append(this.getXPathNameStep());
        final List mySiblings = parent.elements(this.getQName());
        if (mySiblings.size() > 1) {
            int idx = mySiblings.indexOf(this);
            if (idx >= 0) {
                buffer.append("[");
                buffer.append(Integer.toString(++idx));
                buffer.append("]");
            }
        }
        return buffer.toString();
    }
    
    public String asXML() {
        try {
            final StringWriter out = new StringWriter();
            final XMLWriter writer = new XMLWriter(out, new OutputFormat());
            writer.write(this);
            writer.flush();
            return out.toString();
        }
        catch (IOException e) {
            throw new RuntimeException("IOException while generating textual representation: " + e.getMessage());
        }
    }
    
    public void write(final Writer out) throws IOException {
        final XMLWriter writer = new XMLWriter(out, new OutputFormat());
        writer.write(this);
    }
    
    public void accept(final Visitor visitor) {
        visitor.visit(this);
        for (int i = 0, size = this.attributeCount(); i < size; ++i) {
            final Attribute attribute = this.attribute(i);
            visitor.visit(attribute);
        }
        for (int i = 0, size = this.nodeCount(); i < size; ++i) {
            final Node node = this.node(i);
            node.accept(visitor);
        }
    }
    
    public String toString() {
        final String uri = this.getNamespaceURI();
        if (uri != null && uri.length() > 0) {
            return super.toString() + " [Element: <" + this.getQualifiedName() + " uri: " + uri + " attributes: " + this.attributeList() + "/>]";
        }
        return super.toString() + " [Element: <" + this.getQualifiedName() + " attributes: " + this.attributeList() + "/>]";
    }
    
    public Namespace getNamespace() {
        return this.getQName().getNamespace();
    }
    
    public String getName() {
        return this.getQName().getName();
    }
    
    public String getNamespacePrefix() {
        return this.getQName().getNamespacePrefix();
    }
    
    public String getNamespaceURI() {
        return this.getQName().getNamespaceURI();
    }
    
    public String getQualifiedName() {
        return this.getQName().getQualifiedName();
    }
    
    public Object getData() {
        return this.getText();
    }
    
    public void setData(final Object data) {
    }
    
    public Node node(final int index) {
        if (index >= 0) {
            final List list = this.contentList();
            if (index >= list.size()) {
                return null;
            }
            final Object node = list.get(index);
            if (node != null) {
                if (node instanceof Node) {
                    return (Node)node;
                }
                return this.getDocumentFactory().createText(node.toString());
            }
        }
        return null;
    }
    
    public int indexOf(final Node node) {
        return this.contentList().indexOf(node);
    }
    
    public int nodeCount() {
        return this.contentList().size();
    }
    
    public Iterator nodeIterator() {
        return this.contentList().iterator();
    }
    
    public Element element(final String name) {
        final List list = this.contentList();
        for (int size = list.size(), i = 0; i < size; ++i) {
            final Object object = list.get(i);
            if (object instanceof Element) {
                final Element element = (Element)object;
                if (name.equals(element.getName())) {
                    return element;
                }
            }
        }
        return null;
    }
    
    public Element element(final QName qName) {
        final List list = this.contentList();
        for (int size = list.size(), i = 0; i < size; ++i) {
            final Object object = list.get(i);
            if (object instanceof Element) {
                final Element element = (Element)object;
                if (qName.equals(element.getQName())) {
                    return element;
                }
            }
        }
        return null;
    }
    
    public Element element(final String name, final Namespace namespace) {
        return this.element(this.getDocumentFactory().createQName(name, namespace));
    }
    
    public List elements() {
        final List list = this.contentList();
        final BackedList answer = this.createResultList();
        for (int size = list.size(), i = 0; i < size; ++i) {
            final Object object = list.get(i);
            if (object instanceof Element) {
                answer.addLocal(object);
            }
        }
        return answer;
    }
    
    public List elements(final String name) {
        final List list = this.contentList();
        final BackedList answer = this.createResultList();
        for (int size = list.size(), i = 0; i < size; ++i) {
            final Object object = list.get(i);
            if (object instanceof Element) {
                final Element element = (Element)object;
                if (name.equals(element.getName())) {
                    answer.addLocal(element);
                }
            }
        }
        return answer;
    }
    
    public List elements(final QName qName) {
        final List list = this.contentList();
        final BackedList answer = this.createResultList();
        for (int size = list.size(), i = 0; i < size; ++i) {
            final Object object = list.get(i);
            if (object instanceof Element) {
                final Element element = (Element)object;
                if (qName.equals(element.getQName())) {
                    answer.addLocal(element);
                }
            }
        }
        return answer;
    }
    
    public List elements(final String name, final Namespace namespace) {
        return this.elements(this.getDocumentFactory().createQName(name, namespace));
    }
    
    public Iterator elementIterator() {
        final List list = this.elements();
        return list.iterator();
    }
    
    public Iterator elementIterator(final String name) {
        final List list = this.elements(name);
        return list.iterator();
    }
    
    public Iterator elementIterator(final QName qName) {
        final List list = this.elements(qName);
        return list.iterator();
    }
    
    public Iterator elementIterator(final String name, final Namespace ns) {
        return this.elementIterator(this.getDocumentFactory().createQName(name, ns));
    }
    
    public List attributes() {
        return new ContentListFacade(this, this.attributeList());
    }
    
    public Iterator attributeIterator() {
        return this.attributeList().iterator();
    }
    
    public Attribute attribute(final int index) {
        return this.attributeList().get(index);
    }
    
    public int attributeCount() {
        return this.attributeList().size();
    }
    
    public Attribute attribute(final String name) {
        final List list = this.attributeList();
        for (int size = list.size(), i = 0; i < size; ++i) {
            final Attribute attribute = list.get(i);
            if (name.equals(attribute.getName())) {
                return attribute;
            }
        }
        return null;
    }
    
    public Attribute attribute(final QName qName) {
        final List list = this.attributeList();
        for (int size = list.size(), i = 0; i < size; ++i) {
            final Attribute attribute = list.get(i);
            if (qName.equals(attribute.getQName())) {
                return attribute;
            }
        }
        return null;
    }
    
    public Attribute attribute(final String name, final Namespace namespace) {
        return this.attribute(this.getDocumentFactory().createQName(name, namespace));
    }
    
    public void setAttributes(final Attributes attributes, final NamespaceStack namespaceStack, final boolean noNamespaceAttributes) {
        final int size = attributes.getLength();
        if (size > 0) {
            final DocumentFactory factory = this.getDocumentFactory();
            if (size == 1) {
                final String name = attributes.getQName(0);
                if (noNamespaceAttributes || !name.startsWith("xmlns")) {
                    final String attributeURI = attributes.getURI(0);
                    final String attributeLocalName = attributes.getLocalName(0);
                    final String attributeValue = attributes.getValue(0);
                    final QName attributeQName = namespaceStack.getAttributeQName(attributeURI, attributeLocalName, name);
                    this.add(factory.createAttribute(this, attributeQName, attributeValue));
                }
            }
            else {
                final List list = this.attributeList(size);
                list.clear();
                for (int i = 0; i < size; ++i) {
                    final String attributeName = attributes.getQName(i);
                    if (noNamespaceAttributes || !attributeName.startsWith("xmlns")) {
                        final String attributeURI2 = attributes.getURI(i);
                        final String attributeLocalName2 = attributes.getLocalName(i);
                        final String attributeValue2 = attributes.getValue(i);
                        final QName attributeQName2 = namespaceStack.getAttributeQName(attributeURI2, attributeLocalName2, attributeName);
                        final Attribute attribute = factory.createAttribute(this, attributeQName2, attributeValue2);
                        list.add(attribute);
                        this.childAdded(attribute);
                    }
                }
            }
        }
    }
    
    public String attributeValue(final String name) {
        final Attribute attrib = this.attribute(name);
        if (attrib == null) {
            return null;
        }
        return attrib.getValue();
    }
    
    public String attributeValue(final QName qName) {
        final Attribute attrib = this.attribute(qName);
        if (attrib == null) {
            return null;
        }
        return attrib.getValue();
    }
    
    public String attributeValue(final String name, final String defaultValue) {
        final String answer = this.attributeValue(name);
        return (answer != null) ? answer : defaultValue;
    }
    
    public String attributeValue(final QName qName, final String defaultValue) {
        final String answer = this.attributeValue(qName);
        return (answer != null) ? answer : defaultValue;
    }
    
    public void setAttributeValue(final String name, final String value) {
        this.addAttribute(name, value);
    }
    
    public void setAttributeValue(final QName qName, final String value) {
        this.addAttribute(qName, value);
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
            this.attributeList().add(attribute);
            this.childAdded(attribute);
        }
    }
    
    public boolean remove(final Attribute attribute) {
        final List list = this.attributeList();
        boolean answer = list.remove(attribute);
        if (answer) {
            this.childRemoved(attribute);
        }
        else {
            final Attribute copy = this.attribute(attribute.getQName());
            if (copy != null) {
                list.remove(copy);
                answer = true;
            }
        }
        return answer;
    }
    
    public List processingInstructions() {
        final List list = this.contentList();
        final BackedList answer = this.createResultList();
        for (int size = list.size(), i = 0; i < size; ++i) {
            final Object object = list.get(i);
            if (object instanceof ProcessingInstruction) {
                answer.addLocal(object);
            }
        }
        return answer;
    }
    
    public List processingInstructions(final String target) {
        final List list = this.contentList();
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
    
    public ProcessingInstruction processingInstruction(final String target) {
        final List list = this.contentList();
        for (int size = list.size(), i = 0; i < size; ++i) {
            final Object object = list.get(i);
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
        final List list = this.contentList();
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
        return false;
    }
    
    public Node getXPathResult(final int index) {
        final Node answer = this.node(index);
        if (answer != null && !answer.supportsParent()) {
            return answer.asXPathResult(this);
        }
        return answer;
    }
    
    public Element addAttribute(final String name, final String value) {
        final Attribute attribute = this.attribute(name);
        if (value != null) {
            if (attribute == null) {
                this.add(this.getDocumentFactory().createAttribute(this, name, value));
            }
            else if (attribute.isReadOnly()) {
                this.remove(attribute);
                this.add(this.getDocumentFactory().createAttribute(this, name, value));
            }
            else {
                attribute.setValue(value);
            }
        }
        else if (attribute != null) {
            this.remove(attribute);
        }
        return this;
    }
    
    public Element addAttribute(final QName qName, final String value) {
        final Attribute attribute = this.attribute(qName);
        if (value != null) {
            if (attribute == null) {
                this.add(this.getDocumentFactory().createAttribute(this, qName, value));
            }
            else if (attribute.isReadOnly()) {
                this.remove(attribute);
                this.add(this.getDocumentFactory().createAttribute(this, qName, value));
            }
            else {
                attribute.setValue(value);
            }
        }
        else if (attribute != null) {
            this.remove(attribute);
        }
        return this;
    }
    
    public Element addCDATA(final String cdata) {
        final CDATA node = this.getDocumentFactory().createCDATA(cdata);
        this.addNewNode(node);
        return this;
    }
    
    public Element addComment(final String comment) {
        final Comment node = this.getDocumentFactory().createComment(comment);
        this.addNewNode(node);
        return this;
    }
    
    public Element addElement(final String name) {
        final DocumentFactory factory = this.getDocumentFactory();
        final int index = name.indexOf(":");
        String prefix = "";
        String localName = name;
        Namespace namespace = null;
        if (index > 0) {
            prefix = name.substring(0, index);
            localName = name.substring(index + 1);
            namespace = this.getNamespaceForPrefix(prefix);
            if (namespace == null) {
                throw new IllegalAddException("No such namespace prefix: " + prefix + " is in scope on: " + this + " so cannot add element: " + name);
            }
        }
        else {
            namespace = this.getNamespaceForPrefix("");
        }
        Element node;
        if (namespace != null) {
            final QName qname = factory.createQName(localName, namespace);
            node = factory.createElement(qname);
        }
        else {
            node = factory.createElement(name);
        }
        this.addNewNode(node);
        return node;
    }
    
    public Element addEntity(final String name, final String text) {
        final Entity node = this.getDocumentFactory().createEntity(name, text);
        this.addNewNode(node);
        return this;
    }
    
    public Element addNamespace(final String prefix, final String uri) {
        final Namespace node = this.getDocumentFactory().createNamespace(prefix, uri);
        this.addNewNode(node);
        return this;
    }
    
    public Element addProcessingInstruction(final String target, final String data) {
        final ProcessingInstruction node = this.getDocumentFactory().createProcessingInstruction(target, data);
        this.addNewNode(node);
        return this;
    }
    
    public Element addProcessingInstruction(final String target, final Map data) {
        final ProcessingInstruction node = this.getDocumentFactory().createProcessingInstruction(target, data);
        this.addNewNode(node);
        return this;
    }
    
    public Element addText(final String text) {
        final Text node = this.getDocumentFactory().createText(text);
        this.addNewNode(node);
        return this;
    }
    
    public void add(final Node node) {
        switch (node.getNodeType()) {
            case 1: {
                this.add((Element)node);
                break;
            }
            case 2: {
                this.add((Attribute)node);
                break;
            }
            case 3: {
                this.add((Text)node);
                break;
            }
            case 4: {
                this.add((CDATA)node);
                break;
            }
            case 5: {
                this.add((Entity)node);
                break;
            }
            case 7: {
                this.add((ProcessingInstruction)node);
                break;
            }
            case 8: {
                this.add((Comment)node);
                break;
            }
            case 13: {
                this.add((Namespace)node);
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
            case 2: {
                return this.remove((Attribute)node);
            }
            case 3: {
                return this.remove((Text)node);
            }
            case 4: {
                return this.remove((CDATA)node);
            }
            case 5: {
                return this.remove((Entity)node);
            }
            case 7: {
                return this.remove((ProcessingInstruction)node);
            }
            case 8: {
                return this.remove((Comment)node);
            }
            case 13: {
                return this.remove((Namespace)node);
            }
            default: {
                return false;
            }
        }
    }
    
    public void add(final CDATA cdata) {
        this.addNode(cdata);
    }
    
    public void add(final Comment comment) {
        this.addNode(comment);
    }
    
    public void add(final Element element) {
        this.addNode(element);
    }
    
    public void add(final Entity entity) {
        this.addNode(entity);
    }
    
    public void add(final Namespace namespace) {
        this.addNode(namespace);
    }
    
    public void add(final ProcessingInstruction pi) {
        this.addNode(pi);
    }
    
    public void add(final Text text) {
        this.addNode(text);
    }
    
    public boolean remove(final CDATA cdata) {
        return this.removeNode(cdata);
    }
    
    public boolean remove(final Comment comment) {
        return this.removeNode(comment);
    }
    
    public boolean remove(final Element element) {
        return this.removeNode(element);
    }
    
    public boolean remove(final Entity entity) {
        return this.removeNode(entity);
    }
    
    public boolean remove(final Namespace namespace) {
        return this.removeNode(namespace);
    }
    
    public boolean remove(final ProcessingInstruction pi) {
        return this.removeNode(pi);
    }
    
    public boolean remove(final Text text) {
        return this.removeNode(text);
    }
    
    public boolean hasMixedContent() {
        final List content = this.contentList();
        if (content == null || content.isEmpty() || content.size() < 2) {
            return false;
        }
        Class prevClass = null;
        for (final Object object : content) {
            final Class newClass = object.getClass();
            if (newClass != prevClass) {
                if (prevClass != null) {
                    return true;
                }
                prevClass = newClass;
            }
        }
        return false;
    }
    
    public boolean isTextOnly() {
        final List content = this.contentList();
        if (content == null || content.isEmpty()) {
            return true;
        }
        for (final Object object : content) {
            if (!(object instanceof CharacterData) && !(object instanceof String)) {
                return false;
            }
        }
        return true;
    }
    
    public void setText(final String text) {
        final List allContent = this.contentList();
        if (allContent != null) {
            final Iterator it = allContent.iterator();
            while (it.hasNext()) {
                final Node node = it.next();
                switch (node.getNodeType()) {
                    case 3:
                    case 4:
                    case 5: {
                        it.remove();
                        continue;
                    }
                }
            }
        }
        this.addText(text);
    }
    
    public String getStringValue() {
        final List list = this.contentList();
        final int size = list.size();
        if (size <= 0) {
            return "";
        }
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
    
    public void normalize() {
        final List content = this.contentList();
        Text previousText = null;
        int i = 0;
        while (i < content.size()) {
            final Node node = content.get(i);
            if (node instanceof Text) {
                final Text text = (Text)node;
                if (previousText != null) {
                    previousText.appendText(text.getText());
                    this.remove(text);
                }
                else {
                    final String value = text.getText();
                    if (value == null || value.length() <= 0) {
                        this.remove(text);
                    }
                    else {
                        previousText = text;
                        ++i;
                    }
                }
            }
            else {
                if (node instanceof Element) {
                    final Element element = (Element)node;
                    element.normalize();
                }
                previousText = null;
                ++i;
            }
        }
    }
    
    public String elementText(final String name) {
        final Element element = this.element(name);
        return (element != null) ? element.getText() : null;
    }
    
    public String elementText(final QName qName) {
        final Element element = this.element(qName);
        return (element != null) ? element.getText() : null;
    }
    
    public String elementTextTrim(final String name) {
        final Element element = this.element(name);
        return (element != null) ? element.getTextTrim() : null;
    }
    
    public String elementTextTrim(final QName qName) {
        final Element element = this.element(qName);
        return (element != null) ? element.getTextTrim() : null;
    }
    
    public void appendAttributes(final Element element) {
        for (int i = 0, size = element.attributeCount(); i < size; ++i) {
            final Attribute attribute = element.attribute(i);
            if (attribute.supportsParent()) {
                this.addAttribute(attribute.getQName(), attribute.getValue());
            }
            else {
                this.add(attribute);
            }
        }
    }
    
    public Element createCopy() {
        final Element clone = this.createElement(this.getQName());
        clone.appendAttributes(this);
        clone.appendContent(this);
        return clone;
    }
    
    public Element createCopy(final String name) {
        final Element clone = this.createElement(name);
        clone.appendAttributes(this);
        clone.appendContent(this);
        return clone;
    }
    
    public Element createCopy(final QName qName) {
        final Element clone = this.createElement(qName);
        clone.appendAttributes(this);
        clone.appendContent(this);
        return clone;
    }
    
    public QName getQName(final String qualifiedName) {
        String prefix = "";
        String localName = qualifiedName;
        final int index = qualifiedName.indexOf(":");
        if (index > 0) {
            prefix = qualifiedName.substring(0, index);
            localName = qualifiedName.substring(index + 1);
        }
        final Namespace namespace = this.getNamespaceForPrefix(prefix);
        if (namespace != null) {
            return this.getDocumentFactory().createQName(localName, namespace);
        }
        return this.getDocumentFactory().createQName(localName);
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
        final List list = this.contentList();
        for (int size = list.size(), i = 0; i < size; ++i) {
            final Object object = list.get(i);
            if (object instanceof Namespace) {
                final Namespace namespace = (Namespace)object;
                if (prefix.equals(namespace.getPrefix())) {
                    return namespace;
                }
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
        final List list = this.contentList();
        for (int size = list.size(), i = 0; i < size; ++i) {
            final Object object = list.get(i);
            if (object instanceof Namespace) {
                final Namespace namespace = (Namespace)object;
                if (uri.equals(namespace.getURI())) {
                    return namespace;
                }
            }
        }
        return null;
    }
    
    public List getNamespacesForURI(final String uri) {
        final BackedList answer = this.createResultList();
        final List list = this.contentList();
        for (int size = list.size(), i = 0; i < size; ++i) {
            final Object object = list.get(i);
            if (object instanceof Namespace && ((Namespace)object).getURI().equals(uri)) {
                answer.addLocal(object);
            }
        }
        return answer;
    }
    
    public List declaredNamespaces() {
        final BackedList answer = this.createResultList();
        final List list = this.contentList();
        for (int size = list.size(), i = 0; i < size; ++i) {
            final Object object = list.get(i);
            if (object instanceof Namespace) {
                answer.addLocal(object);
            }
        }
        return answer;
    }
    
    public List additionalNamespaces() {
        final List list = this.contentList();
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
    
    public List additionalNamespaces(final String defaultNamespaceURI) {
        final List list = this.contentList();
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
    
    public void ensureAttributesCapacity(final int minCapacity) {
        if (minCapacity > 1) {
            final List list = this.attributeList();
            if (list instanceof ArrayList) {
                final ArrayList arrayList = (ArrayList)list;
                arrayList.ensureCapacity(minCapacity);
            }
        }
    }
    
    protected Element createElement(final String name) {
        return this.getDocumentFactory().createElement(name);
    }
    
    protected Element createElement(final QName qName) {
        return this.getDocumentFactory().createElement(qName);
    }
    
    protected void addNode(final Node node) {
        if (node.getParent() != null) {
            final String message = "The Node already has an existing parent of \"" + node.getParent().getQualifiedName() + "\"";
            throw new IllegalAddException(this, node, message);
        }
        this.addNewNode(node);
    }
    
    protected void addNode(final int index, final Node node) {
        if (node.getParent() != null) {
            final String message = "The Node already has an existing parent of \"" + node.getParent().getQualifiedName() + "\"";
            throw new IllegalAddException(this, node, message);
        }
        this.addNewNode(index, node);
    }
    
    protected void addNewNode(final Node node) {
        this.contentList().add(node);
        this.childAdded(node);
    }
    
    protected void addNewNode(final int index, final Node node) {
        this.contentList().add(index, node);
        this.childAdded(node);
    }
    
    protected boolean removeNode(final Node node) {
        final boolean answer = this.contentList().remove(node);
        if (answer) {
            this.childRemoved(node);
        }
        return answer;
    }
    
    protected void childAdded(final Node node) {
        if (node != null) {
            node.setParent(this);
        }
    }
    
    protected void childRemoved(final Node node) {
        if (node != null) {
            node.setParent(null);
            node.setDocument(null);
        }
    }
    
    protected abstract List attributeList();
    
    protected abstract List attributeList(final int p0);
    
    protected DocumentFactory getDocumentFactory() {
        final QName qName = this.getQName();
        if (qName != null) {
            final DocumentFactory factory = qName.getDocumentFactory();
            if (factory != null) {
                return factory;
            }
        }
        return AbstractElement.DOCUMENT_FACTORY;
    }
    
    protected List createAttributeList() {
        return this.createAttributeList(5);
    }
    
    protected List createAttributeList(final int size) {
        return new ArrayList(size);
    }
    
    protected Iterator createSingleIterator(final Object result) {
        return new SingleIterator(result);
    }
    
    static {
        DOCUMENT_FACTORY = DocumentFactory.getInstance();
        EMPTY_LIST = Collections.EMPTY_LIST;
        EMPTY_ITERATOR = AbstractElement.EMPTY_LIST.iterator();
    }
}
