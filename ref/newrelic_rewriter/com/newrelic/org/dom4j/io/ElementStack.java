// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.io;

import com.newrelic.org.dom4j.ElementHandler;
import com.newrelic.org.dom4j.Element;
import com.newrelic.org.dom4j.ElementPath;

class ElementStack implements ElementPath
{
    protected Element[] stack;
    protected int lastElementIndex;
    private DispatchHandler handler;
    
    public ElementStack() {
        this(50);
    }
    
    public ElementStack(final int defaultCapacity) {
        this.lastElementIndex = -1;
        this.handler = null;
        this.stack = new Element[defaultCapacity];
    }
    
    public void setDispatchHandler(final DispatchHandler dispatchHandler) {
        this.handler = dispatchHandler;
    }
    
    public DispatchHandler getDispatchHandler() {
        return this.handler;
    }
    
    public void clear() {
        this.lastElementIndex = -1;
    }
    
    public Element peekElement() {
        if (this.lastElementIndex < 0) {
            return null;
        }
        return this.stack[this.lastElementIndex];
    }
    
    public Element popElement() {
        if (this.lastElementIndex < 0) {
            return null;
        }
        return this.stack[this.lastElementIndex--];
    }
    
    public void pushElement(final Element element) {
        final int length = this.stack.length;
        if (++this.lastElementIndex >= length) {
            this.reallocate(length * 2);
        }
        this.stack[this.lastElementIndex] = element;
    }
    
    protected void reallocate(final int size) {
        final Element[] oldStack = this.stack;
        System.arraycopy(oldStack, 0, this.stack = new Element[size], 0, oldStack.length);
    }
    
    public int size() {
        return this.lastElementIndex + 1;
    }
    
    public Element getElement(final int depth) {
        Element element;
        try {
            element = this.stack[depth];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            element = null;
        }
        return element;
    }
    
    public String getPath() {
        if (this.handler == null) {
            this.setDispatchHandler(new DispatchHandler());
        }
        return this.handler.getPath();
    }
    
    public Element getCurrent() {
        return this.peekElement();
    }
    
    public void addHandler(final String path, final ElementHandler elementHandler) {
        this.handler.addHandler(this.getHandlerPath(path), elementHandler);
    }
    
    public void removeHandler(final String path) {
        this.handler.removeHandler(this.getHandlerPath(path));
    }
    
    public boolean containsHandler(final String path) {
        return this.handler.containsHandler(path);
    }
    
    private String getHandlerPath(final String path) {
        if (this.handler == null) {
            this.setDispatchHandler(new DispatchHandler());
        }
        String handlerPath;
        if (path.startsWith("/")) {
            handlerPath = path;
        }
        else if (this.getPath().equals("/")) {
            handlerPath = this.getPath() + path;
        }
        else {
            handlerPath = this.getPath() + "/" + path;
        }
        return handlerPath;
    }
}
