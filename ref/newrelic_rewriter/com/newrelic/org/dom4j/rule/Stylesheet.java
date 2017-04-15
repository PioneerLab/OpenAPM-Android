// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.rule;

import com.newrelic.org.dom4j.Document;
import com.newrelic.org.dom4j.Element;
import java.util.Iterator;
import com.newrelic.org.dom4j.XPath;
import java.util.List;
import com.newrelic.org.dom4j.Node;

public class Stylesheet
{
    private RuleManager ruleManager;
    private String modeName;
    
    public Stylesheet() {
        this.ruleManager = new RuleManager();
    }
    
    public void addRule(final Rule rule) {
        this.ruleManager.addRule(rule);
    }
    
    public void removeRule(final Rule rule) {
        this.ruleManager.removeRule(rule);
    }
    
    public void run(final Object input) throws Exception {
        this.run(input, this.modeName);
    }
    
    public void run(final Object input, final String mode) throws Exception {
        if (input instanceof Node) {
            this.run((Node)input, mode);
        }
        else if (input instanceof List) {
            this.run((List)input, mode);
        }
    }
    
    public void run(final List list) throws Exception {
        this.run(list, this.modeName);
    }
    
    public void run(final List list, final String mode) throws Exception {
        for (int i = 0, size = list.size(); i < size; ++i) {
            final Object object = list.get(i);
            if (object instanceof Node) {
                this.run((Node)object, mode);
            }
        }
    }
    
    public void run(final Node node) throws Exception {
        this.run(node, this.modeName);
    }
    
    public void run(final Node node, final String mode) throws Exception {
        final Mode mod = this.ruleManager.getMode(mode);
        mod.fireRule(node);
    }
    
    public void applyTemplates(final Object input, final XPath xpath) throws Exception {
        this.applyTemplates(input, xpath, this.modeName);
    }
    
    public void applyTemplates(final Object input, final XPath xpath, final String mode) throws Exception {
        final Mode mod = this.ruleManager.getMode(mode);
        final List list = xpath.selectNodes(input);
        for (final Node current : list) {
            mod.fireRule(current);
        }
    }
    
    public void applyTemplates(final Object input, final org.jaxen.XPath xpath) throws Exception {
        this.applyTemplates(input, xpath, this.modeName);
    }
    
    public void applyTemplates(final Object input, final org.jaxen.XPath xpath, final String mode) throws Exception {
        final Mode mod = this.ruleManager.getMode(mode);
        final List list = xpath.selectNodes(input);
        for (final Node current : list) {
            mod.fireRule(current);
        }
    }
    
    public void applyTemplates(final Object input) throws Exception {
        this.applyTemplates(input, this.modeName);
    }
    
    public void applyTemplates(final Object input, final String mode) throws Exception {
        final Mode mod = this.ruleManager.getMode(mode);
        if (input instanceof Element) {
            final Element element = (Element)input;
            for (int i = 0, size = element.nodeCount(); i < size; ++i) {
                final Node node = element.node(i);
                mod.fireRule(node);
            }
        }
        else if (input instanceof Document) {
            final Document document = (Document)input;
            for (int i = 0, size = document.nodeCount(); i < size; ++i) {
                final Node node = document.node(i);
                mod.fireRule(node);
            }
        }
        else if (input instanceof List) {
            final List list = (List)input;
            for (int i = 0, size = list.size(); i < size; ++i) {
                final Object object = list.get(i);
                if (object instanceof Element) {
                    this.applyTemplates(object, mode);
                }
                else if (object instanceof Document) {
                    this.applyTemplates(object, mode);
                }
            }
        }
    }
    
    public void clear() {
        this.ruleManager.clear();
    }
    
    public String getModeName() {
        return this.modeName;
    }
    
    public void setModeName(final String modeName) {
        this.modeName = modeName;
    }
    
    public Action getValueOfAction() {
        return this.ruleManager.getValueOfAction();
    }
    
    public void setValueOfAction(final Action valueOfAction) {
        this.ruleManager.setValueOfAction(valueOfAction);
    }
}
