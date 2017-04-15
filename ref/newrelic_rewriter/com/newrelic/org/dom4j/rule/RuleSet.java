// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.rule;

import java.util.List;
import java.util.Collections;
import java.util.Collection;
import com.newrelic.org.dom4j.Node;
import java.util.ArrayList;

public class RuleSet
{
    private ArrayList rules;
    private Rule[] ruleArray;
    
    public RuleSet() {
        this.rules = new ArrayList();
    }
    
    public String toString() {
        return super.toString() + " [RuleSet: " + this.rules + " ]";
    }
    
    public Rule getMatchingRule(final Node node) {
        final Rule[] matches = this.getRuleArray();
        for (int i = matches.length - 1; i >= 0; --i) {
            final Rule rule = matches[i];
            if (rule.matches(node)) {
                return rule;
            }
        }
        return null;
    }
    
    public void addRule(final Rule rule) {
        this.rules.add(rule);
        this.ruleArray = null;
    }
    
    public void removeRule(final Rule rule) {
        this.rules.remove(rule);
        this.ruleArray = null;
    }
    
    public void addAll(final RuleSet that) {
        this.rules.addAll(that.rules);
        this.ruleArray = null;
    }
    
    protected Rule[] getRuleArray() {
        if (this.ruleArray == null) {
            Collections.sort((List<Comparable>)this.rules);
            final int size = this.rules.size();
            this.ruleArray = new Rule[size];
            this.rules.toArray(this.ruleArray);
        }
        return this.ruleArray;
    }
}
