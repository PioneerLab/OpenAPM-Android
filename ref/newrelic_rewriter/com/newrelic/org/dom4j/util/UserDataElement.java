// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.util;

import com.newrelic.org.dom4j.Element;
import com.newrelic.org.dom4j.QName;
import com.newrelic.org.dom4j.tree.DefaultElement;

public class UserDataElement extends DefaultElement
{
    private Object data;
    
    public UserDataElement(final String name) {
        super(name);
    }
    
    public UserDataElement(final QName qname) {
        super(qname);
    }
    
    public Object getData() {
        return this.data;
    }
    
    public void setData(final Object data) {
        this.data = data;
    }
    
    public String toString() {
        return super.toString() + " userData: " + this.data;
    }
    
    public Object clone() {
        final UserDataElement answer = (UserDataElement)super.clone();
        if (answer != this) {
            answer.data = this.getCopyOfUserData();
        }
        return answer;
    }
    
    protected Object getCopyOfUserData() {
        return this.data;
    }
    
    protected Element createElement(final String name) {
        final Element answer = this.getDocumentFactory().createElement(name);
        answer.setData(this.getCopyOfUserData());
        return answer;
    }
    
    protected Element createElement(final QName qName) {
        final Element answer = this.getDocumentFactory().createElement(qName);
        answer.setData(this.getCopyOfUserData());
        return answer;
    }
}
