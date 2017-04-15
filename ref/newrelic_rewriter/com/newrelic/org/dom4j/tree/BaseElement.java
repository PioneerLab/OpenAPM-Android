// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.tree;

import com.newrelic.org.dom4j.Document;
import com.newrelic.org.dom4j.Element;
import com.newrelic.org.dom4j.Namespace;
import java.util.List;
import com.newrelic.org.dom4j.Branch;
import com.newrelic.org.dom4j.QName;

public class BaseElement extends AbstractElement
{
    private QName qname;
    private Branch parentBranch;
    protected List content;
    protected List attributes;
    
    public BaseElement(final String name) {
        this.qname = this.getDocumentFactory().createQName(name);
    }
    
    public BaseElement(final QName qname) {
        this.qname = qname;
    }
    
    public BaseElement(final String name, final Namespace namespace) {
        this.qname = this.getDocumentFactory().createQName(name, namespace);
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
    
    public void clearContent() {
        this.contentList().clear();
    }
    
    public void setContent(final List content) {
        this.content = content;
        if (content instanceof ContentListFacade) {
            this.content = ((ContentListFacade)content).getBackingList();
        }
    }
    
    public void setAttributes(final List attributes) {
        this.attributes = attributes;
        if (attributes instanceof ContentListFacade) {
            this.attributes = ((ContentListFacade)attributes).getBackingList();
        }
    }
    
    protected List contentList() {
        if (this.content == null) {
            this.content = this.createContentList();
        }
        return this.content;
    }
    
    protected List attributeList() {
        if (this.attributes == null) {
            this.attributes = this.createAttributeList();
        }
        return this.attributes;
    }
    
    protected List attributeList(final int size) {
        if (this.attributes == null) {
            this.attributes = this.createAttributeList(size);
        }
        return this.attributes;
    }
    
    protected void setAttributeList(final List attributeList) {
        this.attributes = attributeList;
    }
}
