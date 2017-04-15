// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.rule;

import com.newrelic.org.dom4j.rule.pattern.NodeTypePattern;
import com.newrelic.org.dom4j.Document;
import com.newrelic.org.dom4j.Element;
import com.newrelic.org.dom4j.Node;
import java.util.HashMap;

public class RuleManager
{
    private HashMap modes;
    private int appearenceCount;
    private Action valueOfAction;
    
    public RuleManager() {
        this.modes = new HashMap();
    }
    
    public Mode getMode(final String modeName) {
        Mode mode = this.modes.get(modeName);
        if (mode == null) {
            mode = this.createMode();
            this.modes.put(modeName, mode);
        }
        return mode;
    }
    
    public void addRule(final Rule rule) {
        rule.setAppearenceCount(++this.appearenceCount);
        final Mode mode = this.getMode(rule.getMode());
        final Rule[] childRules = rule.getUnionRules();
        if (childRules != null) {
            for (int i = 0, size = childRules.length; i < size; ++i) {
                mode.addRule(childRules[i]);
            }
        }
        else {
            mode.addRule(rule);
        }
    }
    
    public void removeRule(final Rule rule) {
        final Mode mode = this.getMode(rule.getMode());
        final Rule[] childRules = rule.getUnionRules();
        if (childRules != null) {
            for (int i = 0, size = childRules.length; i < size; ++i) {
                mode.removeRule(childRules[i]);
            }
        }
        else {
            mode.removeRule(rule);
        }
    }
    
    public Rule getMatchingRule(final String modeName, final Node node) {
        final Mode mode = this.modes.get(modeName);
        if (mode != null) {
            return mode.getMatchingRule(node);
        }
        System.out.println("Warning: No Mode for mode: " + mode);
        return null;
    }
    
    public void clear() {
        this.modes.clear();
        this.appearenceCount = 0;
    }
    
    public Action getValueOfAction() {
        return this.valueOfAction;
    }
    
    public void setValueOfAction(final Action valueOfAction) {
        this.valueOfAction = valueOfAction;
    }
    
    protected Mode createMode() {
        final Mode mode = new Mode();
        this.addDefaultRules(mode);
        return mode;
    }
    
    protected void addDefaultRules(final Mode mode) {
        final Action applyTemplates = new Action() {
            public void run(final Node node) throws Exception {
                if (node instanceof Element) {
                    mode.applyTemplates((Element)node);
                }
                else if (node instanceof Document) {
                    mode.applyTemplates((Document)node);
                }
            }
        };
        final Action valueOf = this.getValueOfAction();
        this.addDefaultRule(mode, NodeTypePattern.ANY_DOCUMENT, applyTemplates);
        this.addDefaultRule(mode, NodeTypePattern.ANY_ELEMENT, applyTemplates);
        if (valueOf != null) {
            this.addDefaultRule(mode, NodeTypePattern.ANY_ATTRIBUTE, valueOf);
            this.addDefaultRule(mode, NodeTypePattern.ANY_TEXT, valueOf);
        }
    }
    
    protected void addDefaultRule(final Mode mode, final Pattern pattern, final Action action) {
        final Rule rule = this.createDefaultRule(pattern, action);
        mode.addRule(rule);
    }
    
    protected Rule createDefaultRule(final Pattern pattern, final Action action) {
        final Rule rule = new Rule(pattern, action);
        rule.setImportPrecedence(-1);
        return rule;
    }
}
