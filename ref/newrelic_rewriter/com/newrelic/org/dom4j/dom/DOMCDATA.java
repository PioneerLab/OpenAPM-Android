// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.dom;

import com.newrelic.org.dom4j.CDATA;
import org.w3c.dom.Text;
import com.newrelic.org.dom4j.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.w3c.dom.DOMException;
import com.newrelic.org.dom4j.Node;
import com.newrelic.org.dom4j.Element;
import org.w3c.dom.CDATASection;
import com.newrelic.org.dom4j.tree.DefaultCDATA;

public class DOMCDATA extends DefaultCDATA implements CDATASection
{
    public DOMCDATA(final String text) {
        super(text);
    }
    
    public DOMCDATA(final Element parent, final String text) {
        super(parent, text);
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
        return "#cdata-section";
    }
    
    public String getNodeValue() throws DOMException {
        return DOMNodeHelper.getNodeValue(this);
    }
    
    public void setNodeValue(final String nodeValue) throws DOMException {
        DOMNodeHelper.setNodeValue(this, nodeValue);
    }
    
    public org.w3c.dom.Node getParentNode() {
        return DOMNodeHelper.getParentNode(this);
    }
    
    public NodeList getChildNodes() {
        return DOMNodeHelper.getChildNodes(this);
    }
    
    public org.w3c.dom.Node getFirstChild() {
        return DOMNodeHelper.getFirstChild(this);
    }
    
    public org.w3c.dom.Node getLastChild() {
        return DOMNodeHelper.getLastChild(this);
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
        return DOMNodeHelper.getOwnerDocument(this);
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
        throw new DOMException((short)3, "CDATASection nodes cannot have children");
    }
    
    public boolean hasChildNodes() {
        return DOMNodeHelper.hasChildNodes(this);
    }
    
    public org.w3c.dom.Node cloneNode(final boolean deep) {
        return DOMNodeHelper.cloneNode(this, deep);
    }
    
    public void normalize() {
        DOMNodeHelper.normalize(this);
    }
    
    public boolean isSupported(final String feature, final String version) {
        return DOMNodeHelper.isSupported(this, feature, version);
    }
    
    public boolean hasAttributes() {
        return DOMNodeHelper.hasAttributes(this);
    }
    
    public String getData() throws DOMException {
        return DOMNodeHelper.getData(this);
    }
    
    public void setData(final String data) throws DOMException {
        DOMNodeHelper.setData(this, data);
    }
    
    public int getLength() {
        return DOMNodeHelper.getLength(this);
    }
    
    public String substringData(final int offset, final int count) throws DOMException {
        return DOMNodeHelper.substringData(this, offset, count);
    }
    
    public void appendData(final String arg) throws DOMException {
        DOMNodeHelper.appendData(this, arg);
    }
    
    public void insertData(final int offset, final String arg) throws DOMException {
        DOMNodeHelper.insertData(this, offset, arg);
    }
    
    public void deleteData(final int offset, final int count) throws DOMException {
        DOMNodeHelper.deleteData(this, offset, count);
    }
    
    public void replaceData(final int offset, final int count, final String arg) throws DOMException {
        DOMNodeHelper.replaceData(this, offset, count, arg);
    }
    
    public Text splitText(final int offset) throws DOMException {
        if (this.isReadOnly()) {
            throw new DOMException((short)7, "CharacterData node is read only: " + this);
        }
        final String text = this.getText();
        final int length = (text != null) ? text.length() : 0;
        if (offset < 0 || offset >= length) {
            throw new DOMException((short)1, "No text at offset: " + offset);
        }
        final String start = text.substring(0, offset);
        final String rest = text.substring(offset);
        this.setText(start);
        final Element parent = this.getParent();
        final CDATA newText = this.createCDATA(rest);
        if (parent != null) {
            parent.add(newText);
        }
        return DOMNodeHelper.asDOMText(newText);
    }
    
    protected CDATA createCDATA(final String text) {
        return new DOMCDATA(text);
    }
}
