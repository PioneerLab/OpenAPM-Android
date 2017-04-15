// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.io;

import com.newrelic.org.dom4j.Document;
import com.newrelic.org.dom4j.Node;
import com.newrelic.org.dom4j.ElementPath;
import com.newrelic.org.dom4j.Element;
import com.newrelic.org.dom4j.ElementHandler;

class SAXModifyElementHandler implements ElementHandler
{
    private ElementModifier elemModifier;
    private Element modifiedElement;
    
    public SAXModifyElementHandler(final ElementModifier elemModifier) {
        this.elemModifier = elemModifier;
    }
    
    public void onStart(final ElementPath elementPath) {
        this.modifiedElement = elementPath.getCurrent();
    }
    
    public void onEnd(final ElementPath elementPath) {
        try {
            final Element origElement = elementPath.getCurrent();
            final Element currentParent = origElement.getParent();
            if (currentParent != null) {
                final Element clonedElem = (Element)origElement.clone();
                this.modifiedElement = this.elemModifier.modifyElement(clonedElem);
                if (this.modifiedElement != null) {
                    this.modifiedElement.setParent(origElement.getParent());
                    this.modifiedElement.setDocument(origElement.getDocument());
                    final int contentIndex = currentParent.indexOf(origElement);
                    currentParent.content().set(contentIndex, this.modifiedElement);
                }
                origElement.detach();
            }
            else if (origElement.isRootElement()) {
                final Element clonedElem = (Element)origElement.clone();
                this.modifiedElement = this.elemModifier.modifyElement(clonedElem);
                if (this.modifiedElement != null) {
                    this.modifiedElement.setDocument(origElement.getDocument());
                    final Document doc = origElement.getDocument();
                    doc.setRootElement(this.modifiedElement);
                }
                origElement.detach();
            }
            if (elementPath instanceof ElementStack) {
                final ElementStack elementStack = (ElementStack)elementPath;
                elementStack.popElement();
                elementStack.pushElement(this.modifiedElement);
            }
        }
        catch (Exception ex) {
            throw new SAXModifyException(ex);
        }
    }
    
    protected Element getModifiedElement() {
        return this.modifiedElement;
    }
}
