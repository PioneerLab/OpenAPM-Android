// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.io;

import com.newrelic.org.dom4j.ElementPath;
import com.newrelic.org.dom4j.Element;
import com.newrelic.org.dom4j.ElementHandler;

class PruningElementStack extends ElementStack
{
    private ElementHandler elementHandler;
    private String[] path;
    private int matchingElementIndex;
    
    public PruningElementStack(final String[] path, final ElementHandler elementHandler) {
        this.path = path;
        this.elementHandler = elementHandler;
        this.checkPath();
    }
    
    public PruningElementStack(final String[] path, final ElementHandler elementHandler, final int defaultCapacity) {
        super(defaultCapacity);
        this.path = path;
        this.elementHandler = elementHandler;
        this.checkPath();
    }
    
    public Element popElement() {
        final Element answer = super.popElement();
        if (this.lastElementIndex == this.matchingElementIndex && this.lastElementIndex >= 0 && this.validElement(answer, this.lastElementIndex + 1)) {
            Element parent = null;
            for (int i = 0; i <= this.lastElementIndex; ++i) {
                parent = this.stack[i];
                if (!this.validElement(parent, i)) {
                    parent = null;
                    break;
                }
            }
            if (parent != null) {
                this.pathMatches(parent, answer);
            }
        }
        return answer;
    }
    
    protected void pathMatches(final Element parent, final Element selectedNode) {
        this.elementHandler.onEnd(this);
        parent.remove(selectedNode);
    }
    
    protected boolean validElement(final Element element, final int index) {
        final String requiredName = this.path[index];
        final String name = element.getName();
        return requiredName == name || (requiredName != null && name != null && requiredName.equals(name));
    }
    
    private void checkPath() {
        if (this.path.length < 2) {
            throw new RuntimeException("Invalid path of length: " + this.path.length + " it must be greater than 2");
        }
        this.matchingElementIndex = this.path.length - 2;
    }
}
