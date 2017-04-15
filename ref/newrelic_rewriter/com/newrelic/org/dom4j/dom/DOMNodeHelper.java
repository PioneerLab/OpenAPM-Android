// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.dom;

import org.w3c.dom.Attr;
import org.w3c.dom.Text;
import com.newrelic.org.dom4j.DocumentType;
import com.newrelic.org.dom4j.CharacterData;
import java.util.List;
import com.newrelic.org.dom4j.Branch;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import com.newrelic.org.dom4j.Element;
import org.w3c.dom.DOMException;
import com.newrelic.org.dom4j.Node;
import org.w3c.dom.NodeList;

public class DOMNodeHelper
{
    public static final NodeList EMPTY_NODE_LIST;
    
    public static boolean supports(final Node node, final String feature, final String version) {
        return false;
    }
    
    public static String getNamespaceURI(final Node node) {
        return null;
    }
    
    public static String getPrefix(final Node node) {
        return null;
    }
    
    public static String getLocalName(final Node node) {
        return null;
    }
    
    public static void setPrefix(final Node node, final String prefix) throws DOMException {
        notSupported();
    }
    
    public static String getNodeValue(final Node node) throws DOMException {
        return node.getText();
    }
    
    public static void setNodeValue(final Node node, final String nodeValue) throws DOMException {
        node.setText(nodeValue);
    }
    
    public static org.w3c.dom.Node getParentNode(final Node node) {
        return asDOMNode(node.getParent());
    }
    
    public static NodeList getChildNodes(final Node node) {
        return DOMNodeHelper.EMPTY_NODE_LIST;
    }
    
    public static org.w3c.dom.Node getFirstChild(final Node node) {
        return null;
    }
    
    public static org.w3c.dom.Node getLastChild(final Node node) {
        return null;
    }
    
    public static org.w3c.dom.Node getPreviousSibling(final Node node) {
        final Element parent = node.getParent();
        if (parent != null) {
            final int index = parent.indexOf(node);
            if (index > 0) {
                final Node previous = parent.node(index - 1);
                return asDOMNode(previous);
            }
        }
        return null;
    }
    
    public static org.w3c.dom.Node getNextSibling(final Node node) {
        final Element parent = node.getParent();
        if (parent != null) {
            int index = parent.indexOf(node);
            if (index >= 0 && ++index < parent.nodeCount()) {
                final Node next = parent.node(index);
                return asDOMNode(next);
            }
        }
        return null;
    }
    
    public static NamedNodeMap getAttributes(final Node node) {
        return null;
    }
    
    public static Document getOwnerDocument(final Node node) {
        return asDOMDocument(node.getDocument());
    }
    
    public static org.w3c.dom.Node insertBefore(final Node node, final org.w3c.dom.Node newChild, final org.w3c.dom.Node refChild) throws DOMException {
        if (node instanceof Branch) {
            final Branch branch = (Branch)node;
            final List list = branch.content();
            final int index = list.indexOf(refChild);
            if (index < 0) {
                branch.add((Node)newChild);
            }
            else {
                list.add(index, newChild);
            }
            return newChild;
        }
        throw new DOMException((short)3, "Children not allowed for this node: " + node);
    }
    
    public static org.w3c.dom.Node replaceChild(final Node node, final org.w3c.dom.Node newChild, final org.w3c.dom.Node oldChild) throws DOMException {
        if (!(node instanceof Branch)) {
            throw new DOMException((short)3, "Children not allowed for this node: " + node);
        }
        final Branch branch = (Branch)node;
        final List list = branch.content();
        final int index = list.indexOf(oldChild);
        if (index < 0) {
            throw new DOMException((short)8, "Tried to replace a non existing child for node: " + node);
        }
        list.set(index, newChild);
        return oldChild;
    }
    
    public static org.w3c.dom.Node removeChild(final Node node, final org.w3c.dom.Node oldChild) throws DOMException {
        if (node instanceof Branch) {
            final Branch branch = (Branch)node;
            branch.remove((Node)oldChild);
            return oldChild;
        }
        throw new DOMException((short)3, "Children not allowed for this node: " + node);
    }
    
    public static org.w3c.dom.Node appendChild(final Node node, final org.w3c.dom.Node newChild) throws DOMException {
        if (node instanceof Branch) {
            final Branch branch = (Branch)node;
            final org.w3c.dom.Node previousParent = newChild.getParentNode();
            if (previousParent != null) {
                previousParent.removeChild(newChild);
            }
            branch.add((Node)newChild);
            return newChild;
        }
        throw new DOMException((short)3, "Children not allowed for this node: " + node);
    }
    
    public static boolean hasChildNodes(final Node node) {
        return false;
    }
    
    public static org.w3c.dom.Node cloneNode(final Node node, final boolean deep) {
        return asDOMNode((Node)node.clone());
    }
    
    public static void normalize(final Node node) {
        notSupported();
    }
    
    public static boolean isSupported(final Node n, final String feature, final String version) {
        return false;
    }
    
    public static boolean hasAttributes(final Node node) {
        return node != null && node instanceof Element && ((Element)node).attributeCount() > 0;
    }
    
    public static String getData(final CharacterData charData) throws DOMException {
        return charData.getText();
    }
    
    public static void setData(final CharacterData charData, final String data) throws DOMException {
        charData.setText(data);
    }
    
    public static int getLength(final CharacterData charData) {
        final String text = charData.getText();
        return (text != null) ? text.length() : 0;
    }
    
    public static String substringData(final CharacterData charData, final int offset, final int count) throws DOMException {
        if (count < 0) {
            throw new DOMException((short)1, "Illegal value for count: " + count);
        }
        final String text = charData.getText();
        final int length = (text != null) ? text.length() : 0;
        if (offset < 0 || offset >= length) {
            throw new DOMException((short)1, "No text at offset: " + offset);
        }
        if (offset + count > length) {
            return text.substring(offset);
        }
        return text.substring(offset, offset + count);
    }
    
    public static void appendData(final CharacterData charData, final String arg) throws DOMException {
        if (charData.isReadOnly()) {
            throw new DOMException((short)7, "CharacterData node is read only: " + charData);
        }
        final String text = charData.getText();
        if (text == null) {
            charData.setText(text);
        }
        else {
            charData.setText(text + arg);
        }
    }
    
    public static void insertData(final CharacterData data, final int offset, final String arg) throws DOMException {
        if (data.isReadOnly()) {
            throw new DOMException((short)7, "CharacterData node is read only: " + data);
        }
        final String text = data.getText();
        if (text == null) {
            data.setText(arg);
        }
        else {
            final int length = text.length();
            if (offset < 0 || offset > length) {
                throw new DOMException((short)1, "No text at offset: " + offset);
            }
            final StringBuffer buffer = new StringBuffer(text);
            buffer.insert(offset, arg);
            data.setText(buffer.toString());
        }
    }
    
    public static void deleteData(final CharacterData charData, final int offset, final int count) throws DOMException {
        if (charData.isReadOnly()) {
            throw new DOMException((short)7, "CharacterData node is read only: " + charData);
        }
        if (count < 0) {
            throw new DOMException((short)1, "Illegal value for count: " + count);
        }
        final String text = charData.getText();
        if (text != null) {
            final int length = text.length();
            if (offset < 0 || offset >= length) {
                throw new DOMException((short)1, "No text at offset: " + offset);
            }
            final StringBuffer buffer = new StringBuffer(text);
            buffer.delete(offset, offset + count);
            charData.setText(buffer.toString());
        }
    }
    
    public static void replaceData(final CharacterData charData, final int offset, final int count, final String arg) throws DOMException {
        if (charData.isReadOnly()) {
            throw new DOMException((short)7, "CharacterData node is read only: " + charData);
        }
        if (count < 0) {
            throw new DOMException((short)1, "Illegal value for count: " + count);
        }
        final String text = charData.getText();
        if (text != null) {
            final int length = text.length();
            if (offset < 0 || offset >= length) {
                throw new DOMException((short)1, "No text at offset: " + offset);
            }
            final StringBuffer buffer = new StringBuffer(text);
            buffer.replace(offset, offset + count, arg);
            charData.setText(buffer.toString());
        }
    }
    
    public static void appendElementsByTagName(final List list, final Branch parent, final String name) {
        final boolean isStar = "*".equals(name);
        for (int i = 0, size = parent.nodeCount(); i < size; ++i) {
            final Node node = parent.node(i);
            if (node instanceof Element) {
                final Element element = (Element)node;
                if (isStar || name.equals(element.getName())) {
                    list.add(element);
                }
                appendElementsByTagName(list, element, name);
            }
        }
    }
    
    public static void appendElementsByTagNameNS(final List list, final Branch parent, final String namespace, final String localName) {
        final boolean isStarNS = "*".equals(namespace);
        final boolean isStar = "*".equals(localName);
        for (int i = 0, size = parent.nodeCount(); i < size; ++i) {
            final Node node = parent.node(i);
            if (node instanceof Element) {
                final Element element = (Element)node;
                if ((isStarNS || ((namespace == null || namespace.length() == 0) && (element.getNamespaceURI() == null || element.getNamespaceURI().length() == 0)) || (namespace != null && namespace.equals(element.getNamespaceURI()))) && (isStar || localName.equals(element.getName()))) {
                    list.add(element);
                }
                appendElementsByTagNameNS(list, element, namespace, localName);
            }
        }
    }
    
    public static NodeList createNodeList(final List list) {
        return new NodeList() {
            public org.w3c.dom.Node item(final int index) {
                if (index >= this.getLength()) {
                    return null;
                }
                return DOMNodeHelper.asDOMNode(list.get(index));
            }
            
            public int getLength() {
                return list.size();
            }
        };
    }
    
    public static org.w3c.dom.Node asDOMNode(final Node node) {
        if (node == null) {
            return null;
        }
        if (node instanceof org.w3c.dom.Node) {
            return (org.w3c.dom.Node)node;
        }
        System.out.println("Cannot convert: " + node + " into a W3C DOM Node");
        notSupported();
        return null;
    }
    
    public static Document asDOMDocument(final com.newrelic.org.dom4j.Document document) {
        if (document == null) {
            return null;
        }
        if (document instanceof Document) {
            return (Document)document;
        }
        notSupported();
        return null;
    }
    
    public static org.w3c.dom.DocumentType asDOMDocumentType(final DocumentType dt) {
        if (dt == null) {
            return null;
        }
        if (dt instanceof org.w3c.dom.DocumentType) {
            return (org.w3c.dom.DocumentType)dt;
        }
        notSupported();
        return null;
    }
    
    public static Text asDOMText(final CharacterData text) {
        if (text == null) {
            return null;
        }
        if (text instanceof Text) {
            return (Text)text;
        }
        notSupported();
        return null;
    }
    
    public static org.w3c.dom.Element asDOMElement(final Node element) {
        if (element == null) {
            return null;
        }
        if (element instanceof org.w3c.dom.Element) {
            return (org.w3c.dom.Element)element;
        }
        notSupported();
        return null;
    }
    
    public static Attr asDOMAttr(final Node attribute) {
        if (attribute == null) {
            return null;
        }
        if (attribute instanceof Attr) {
            return (Attr)attribute;
        }
        notSupported();
        return null;
    }
    
    public static void notSupported() {
        throw new DOMException((short)9, "Not supported yet");
    }
    
    static {
        EMPTY_NODE_LIST = new EmptyNodeList();
    }
    
    public static class EmptyNodeList implements NodeList
    {
        public org.w3c.dom.Node item(final int index) {
            return null;
        }
        
        public int getLength() {
            return 0;
        }
    }
}
