// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.xpath;

import org.jaxen.dom4j.Dom4jXPath;
import java.util.Iterator;
import java.util.HashSet;
import java.util.Comparator;
import java.util.HashMap;
import com.newrelic.org.dom4j.XPathException;
import com.newrelic.org.dom4j.Node;
import java.util.Collections;
import java.util.List;
import org.jaxen.JaxenException;
import org.jaxen.VariableContext;
import org.jaxen.SimpleNamespaceContext;
import java.util.Map;
import org.jaxen.FunctionContext;
import com.newrelic.org.dom4j.InvalidXPathException;
import org.jaxen.NamespaceContext;
import java.io.Serializable;
import com.newrelic.org.dom4j.NodeFilter;
import com.newrelic.org.dom4j.XPath;

public class DefaultXPath implements XPath, NodeFilter, Serializable
{
    private String text;
    private org.jaxen.XPath xpath;
    private NamespaceContext namespaceContext;
    
    public DefaultXPath(final String text) throws InvalidXPathException {
        this.text = text;
        this.xpath = parse(text);
    }
    
    public String toString() {
        return "[XPath: " + this.xpath + "]";
    }
    
    public String getText() {
        return this.text;
    }
    
    public FunctionContext getFunctionContext() {
        return this.xpath.getFunctionContext();
    }
    
    public void setFunctionContext(final FunctionContext functionContext) {
        this.xpath.setFunctionContext(functionContext);
    }
    
    public NamespaceContext getNamespaceContext() {
        return this.namespaceContext;
    }
    
    public void setNamespaceURIs(final Map map) {
        this.setNamespaceContext((NamespaceContext)new SimpleNamespaceContext(map));
    }
    
    public void setNamespaceContext(final NamespaceContext namespaceContext) {
        this.namespaceContext = namespaceContext;
        this.xpath.setNamespaceContext(namespaceContext);
    }
    
    public VariableContext getVariableContext() {
        return this.xpath.getVariableContext();
    }
    
    public void setVariableContext(final VariableContext variableContext) {
        this.xpath.setVariableContext(variableContext);
    }
    
    public Object evaluate(final Object context) {
        try {
            this.setNSContext(context);
            final List answer = this.xpath.selectNodes(context);
            if (answer != null && answer.size() == 1) {
                return answer.get(0);
            }
            return answer;
        }
        catch (JaxenException e) {
            this.handleJaxenException(e);
            return null;
        }
    }
    
    public Object selectObject(final Object context) {
        return this.evaluate(context);
    }
    
    public List selectNodes(final Object context) {
        try {
            this.setNSContext(context);
            return this.xpath.selectNodes(context);
        }
        catch (JaxenException e) {
            this.handleJaxenException(e);
            return Collections.EMPTY_LIST;
        }
    }
    
    public List selectNodes(final Object context, final XPath sortXPath) {
        final List answer = this.selectNodes(context);
        sortXPath.sort(answer);
        return answer;
    }
    
    public List selectNodes(final Object context, final XPath sortXPath, final boolean distinct) {
        final List answer = this.selectNodes(context);
        sortXPath.sort(answer, distinct);
        return answer;
    }
    
    public Node selectSingleNode(final Object context) {
        try {
            this.setNSContext(context);
            final Object answer = this.xpath.selectSingleNode(context);
            if (answer instanceof Node) {
                return (Node)answer;
            }
            if (answer == null) {
                return null;
            }
            throw new XPathException("The result of the XPath expression is not a Node. It was: " + answer + " of type: " + answer.getClass().getName());
        }
        catch (JaxenException e) {
            this.handleJaxenException(e);
            return null;
        }
    }
    
    public String valueOf(final Object context) {
        try {
            this.setNSContext(context);
            return this.xpath.stringValueOf(context);
        }
        catch (JaxenException e) {
            this.handleJaxenException(e);
            return "";
        }
    }
    
    public Number numberValueOf(final Object context) {
        try {
            this.setNSContext(context);
            return this.xpath.numberValueOf(context);
        }
        catch (JaxenException e) {
            this.handleJaxenException(e);
            return null;
        }
    }
    
    public boolean booleanValueOf(final Object context) {
        try {
            this.setNSContext(context);
            return this.xpath.booleanValueOf(context);
        }
        catch (JaxenException e) {
            this.handleJaxenException(e);
            return false;
        }
    }
    
    public void sort(final List list) {
        this.sort(list, false);
    }
    
    public void sort(final List list, final boolean distinct) {
        if (list != null && !list.isEmpty()) {
            final int size = list.size();
            final HashMap sortValues = new HashMap(size);
            for (int i = 0; i < size; ++i) {
                final Object object = list.get(i);
                if (object instanceof Node) {
                    final Node node = (Node)object;
                    final Object expression = this.getCompareValue(node);
                    sortValues.put(node, expression);
                }
            }
            this.sort(list, sortValues);
            if (distinct) {
                this.removeDuplicates(list, sortValues);
            }
        }
    }
    
    public boolean matches(final Node node) {
        try {
            this.setNSContext(node);
            final List answer = this.xpath.selectNodes((Object)node);
            if (answer == null || answer.size() <= 0) {
                return false;
            }
            final Object item = answer.get(0);
            if (item instanceof Boolean) {
                return (boolean)item;
            }
            return answer.contains(node);
        }
        catch (JaxenException e) {
            this.handleJaxenException(e);
            return false;
        }
    }
    
    protected void sort(final List list, final Map sortValues) {
        Collections.sort((List<Object>)list, new Comparator() {
            public int compare(Object o1, Object o2) {
                o1 = sortValues.get(o1);
                o2 = sortValues.get(o2);
                if (o1 == o2) {
                    return 0;
                }
                if (o1 instanceof Comparable) {
                    final Comparable c1 = (Comparable)o1;
                    return c1.compareTo(o2);
                }
                if (o1 == null) {
                    return 1;
                }
                if (o2 == null) {
                    return -1;
                }
                return o1.equals(o2) ? 0 : -1;
            }
        });
    }
    
    protected void removeDuplicates(final List list, final Map sortValues) {
        final HashSet distinctValues = new HashSet();
        final Iterator iter = list.iterator();
        while (iter.hasNext()) {
            final Object node = iter.next();
            final Object value = sortValues.get(node);
            if (distinctValues.contains(value)) {
                iter.remove();
            }
            else {
                distinctValues.add(value);
            }
        }
    }
    
    protected Object getCompareValue(final Node node) {
        return this.valueOf(node);
    }
    
    protected static org.jaxen.XPath parse(final String text) {
        try {
            return (org.jaxen.XPath)new Dom4jXPath(text);
        }
        catch (JaxenException e) {
            throw new InvalidXPathException(text, e.getMessage());
        }
        catch (Throwable t) {
            throw new InvalidXPathException(text, t);
        }
    }
    
    protected void setNSContext(final Object context) {
        if (this.namespaceContext == null) {
            this.xpath.setNamespaceContext((NamespaceContext)DefaultNamespaceContext.create(context));
        }
    }
    
    protected void handleJaxenException(final JaxenException exception) throws XPathException {
        throw new XPathException(this.text, (Exception)exception);
    }
}
