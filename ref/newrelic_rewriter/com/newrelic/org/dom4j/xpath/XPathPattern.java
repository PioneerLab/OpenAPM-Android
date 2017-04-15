// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.xpath;

import com.newrelic.org.dom4j.XPathException;
import org.jaxen.NamespaceContext;
import org.jaxen.dom4j.DocumentNavigator;
import org.jaxen.SimpleVariableContext;
import org.jaxen.XPathFunctionContext;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.ContextSupport;
import org.jaxen.VariableContext;
import org.jaxen.JaxenException;
import java.util.List;
import java.util.ArrayList;
import com.newrelic.org.dom4j.Node;
import org.jaxen.saxpath.SAXPathException;
import com.newrelic.org.dom4j.InvalidXPathException;
import org.jaxen.pattern.PatternParser;
import org.jaxen.Context;
import com.newrelic.org.dom4j.rule.Pattern;

public class XPathPattern implements Pattern
{
    private String text;
    private org.jaxen.pattern.Pattern pattern;
    private Context context;
    
    public XPathPattern(final org.jaxen.pattern.Pattern pattern) {
        this.pattern = pattern;
        this.text = pattern.getText();
        this.context = new Context(this.getContextSupport());
    }
    
    public XPathPattern(final String text) {
        this.text = text;
        this.context = new Context(this.getContextSupport());
        try {
            this.pattern = PatternParser.parse(text);
        }
        catch (SAXPathException e) {
            throw new InvalidXPathException(text, e.getMessage());
        }
        catch (Throwable t) {
            throw new InvalidXPathException(text, t);
        }
    }
    
    public boolean matches(final Node node) {
        try {
            final ArrayList list = new ArrayList(1);
            list.add(node);
            this.context.setNodeSet((List)list);
            return this.pattern.matches((Object)node, this.context);
        }
        catch (JaxenException e) {
            this.handleJaxenException(e);
            return false;
        }
    }
    
    public String getText() {
        return this.text;
    }
    
    public double getPriority() {
        return this.pattern.getPriority();
    }
    
    public Pattern[] getUnionPatterns() {
        final org.jaxen.pattern.Pattern[] patterns = this.pattern.getUnionPatterns();
        if (patterns != null) {
            final int size = patterns.length;
            final XPathPattern[] answer = new XPathPattern[size];
            for (int i = 0; i < size; ++i) {
                answer[i] = new XPathPattern(patterns[i]);
            }
            return answer;
        }
        return null;
    }
    
    public short getMatchType() {
        return this.pattern.getMatchType();
    }
    
    public String getMatchesNodeName() {
        return this.pattern.getMatchesNodeName();
    }
    
    public void setVariableContext(final VariableContext variableContext) {
        this.context.getContextSupport().setVariableContext(variableContext);
    }
    
    public String toString() {
        return "[XPathPattern: text: " + this.text + " Pattern: " + this.pattern + "]";
    }
    
    protected ContextSupport getContextSupport() {
        return new ContextSupport((NamespaceContext)new SimpleNamespaceContext(), XPathFunctionContext.getInstance(), (VariableContext)new SimpleVariableContext(), DocumentNavigator.getInstance());
    }
    
    protected void handleJaxenException(final JaxenException exception) throws XPathException {
        throw new XPathException(this.text, (Exception)exception);
    }
}
