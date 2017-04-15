// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.xpp;

import java.util.List;
import java.util.ArrayList;
import com.newrelic.org.dom4j.tree.AbstractElement;
import org.gjt.xpp.XmlPullParserException;
import com.newrelic.org.dom4j.QName;
import java.util.Iterator;
import com.newrelic.org.dom4j.Attribute;
import com.newrelic.org.dom4j.DocumentFactory;
import com.newrelic.org.dom4j.Element;
import org.gjt.xpp.XmlStartTag;

public class ProxyXmlStartTag implements XmlStartTag
{
    private Element element;
    private DocumentFactory factory;
    
    public ProxyXmlStartTag() {
        this.factory = DocumentFactory.getInstance();
    }
    
    public ProxyXmlStartTag(final Element element) {
        this.factory = DocumentFactory.getInstance();
        this.element = element;
    }
    
    public void resetStartTag() {
        this.element = null;
    }
    
    public int getAttributeCount() {
        return (this.element != null) ? this.element.attributeCount() : 0;
    }
    
    public String getAttributeNamespaceUri(final int index) {
        if (this.element != null) {
            final Attribute attribute = this.element.attribute(index);
            if (attribute != null) {
                return attribute.getNamespaceURI();
            }
        }
        return null;
    }
    
    public String getAttributeLocalName(final int index) {
        if (this.element != null) {
            final Attribute attribute = this.element.attribute(index);
            if (attribute != null) {
                return attribute.getName();
            }
        }
        return null;
    }
    
    public String getAttributePrefix(final int index) {
        if (this.element != null) {
            final Attribute attribute = this.element.attribute(index);
            if (attribute != null) {
                final String prefix = attribute.getNamespacePrefix();
                if (prefix != null && prefix.length() > 0) {
                    return prefix;
                }
            }
        }
        return null;
    }
    
    public String getAttributeRawName(final int index) {
        if (this.element != null) {
            final Attribute attribute = this.element.attribute(index);
            if (attribute != null) {
                return attribute.getQualifiedName();
            }
        }
        return null;
    }
    
    public String getAttributeValue(final int index) {
        if (this.element != null) {
            final Attribute attribute = this.element.attribute(index);
            if (attribute != null) {
                return attribute.getValue();
            }
        }
        return null;
    }
    
    public String getAttributeValueFromRawName(final String rawName) {
        if (this.element != null) {
            final Iterator iter = this.element.attributeIterator();
            while (iter.hasNext()) {
                final Attribute attribute = iter.next();
                if (rawName.equals(attribute.getQualifiedName())) {
                    return attribute.getValue();
                }
            }
        }
        return null;
    }
    
    public String getAttributeValueFromName(final String namespaceURI, final String localName) {
        if (this.element != null) {
            final Iterator iter = this.element.attributeIterator();
            while (iter.hasNext()) {
                final Attribute attribute = iter.next();
                if (namespaceURI.equals(attribute.getNamespaceURI()) && localName.equals(attribute.getName())) {
                    return attribute.getValue();
                }
            }
        }
        return null;
    }
    
    public boolean isAttributeNamespaceDeclaration(final int index) {
        if (this.element != null) {
            final Attribute attribute = this.element.attribute(index);
            if (attribute != null) {
                return "xmlns".equals(attribute.getNamespacePrefix());
            }
        }
        return false;
    }
    
    public void addAttribute(final String namespaceURI, final String localName, final String rawName, final String value) throws XmlPullParserException {
        final QName qname = QName.get(rawName, namespaceURI);
        this.element.addAttribute(qname, value);
    }
    
    public void addAttribute(final String namespaceURI, final String localName, final String rawName, final String value, final boolean isNamespaceDeclaration) throws XmlPullParserException {
        if (isNamespaceDeclaration) {
            String prefix = "";
            final int idx = rawName.indexOf(58);
            if (idx > 0) {
                prefix = rawName.substring(0, idx);
            }
            this.element.addNamespace(prefix, namespaceURI);
        }
        else {
            final QName qname = QName.get(rawName, namespaceURI);
            this.element.addAttribute(qname, value);
        }
    }
    
    public void ensureAttributesCapacity(final int minCapacity) throws XmlPullParserException {
        if (this.element instanceof AbstractElement) {
            final AbstractElement elementImpl = (AbstractElement)this.element;
            elementImpl.ensureAttributesCapacity(minCapacity);
        }
    }
    
    public void removeAtttributes() throws XmlPullParserException {
        this.removeAttributes();
    }
    
    public void removeAttributes() throws XmlPullParserException {
        if (this.element != null) {
            this.element.setAttributes(new ArrayList());
        }
    }
    
    public String getLocalName() {
        return this.element.getName();
    }
    
    public String getNamespaceUri() {
        return this.element.getNamespaceURI();
    }
    
    public String getPrefix() {
        return this.element.getNamespacePrefix();
    }
    
    public String getRawName() {
        return this.element.getQualifiedName();
    }
    
    public void modifyTag(final String namespaceURI, final String lName, final String rawName) {
        this.element = this.factory.createElement(rawName, namespaceURI);
    }
    
    public void resetTag() {
        this.element = null;
    }
    
    public boolean removeAttributeByName(final String namespaceURI, final String localName) throws XmlPullParserException {
        if (this.element != null) {
            final QName qname = QName.get(localName, namespaceURI);
            final Attribute attribute = this.element.attribute(qname);
            return this.element.remove(attribute);
        }
        return false;
    }
    
    public boolean removeAttributeByRawName(final String rawName) throws XmlPullParserException {
        if (this.element != null) {
            Attribute attribute = null;
            final Iterator it = this.element.attributeIterator();
            while (it.hasNext()) {
                final Attribute current = it.next();
                if (current.getQualifiedName().equals(rawName)) {
                    attribute = current;
                    break;
                }
            }
            return this.element.remove(attribute);
        }
        return false;
    }
    
    public DocumentFactory getDocumentFactory() {
        return this.factory;
    }
    
    public void setDocumentFactory(final DocumentFactory documentFactory) {
        this.factory = documentFactory;
    }
    
    public Element getElement() {
        return this.element;
    }
}
