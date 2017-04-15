// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.rule;

import java.util.HashMap;
import com.newrelic.org.dom4j.Document;
import com.newrelic.org.dom4j.Attribute;
import com.newrelic.org.dom4j.Element;
import com.newrelic.org.dom4j.Node;
import java.util.Map;

public class Mode
{
    private RuleSet[] ruleSets;
    private Map elementNameRuleSets;
    private Map attributeNameRuleSets;
    
    public Mode() {
        this.ruleSets = new RuleSet[14];
    }
    
    public void fireRule(final Node node) throws Exception {
        if (node != null) {
            final Rule rule = this.getMatchingRule(node);
            if (rule != null) {
                final Action action = rule.getAction();
                if (action != null) {
                    action.run(node);
                }
            }
        }
    }
    
    public void applyTemplates(final Element element) throws Exception {
        for (int i = 0, size = element.attributeCount(); i < size; ++i) {
            final Attribute attribute = element.attribute(i);
            this.fireRule(attribute);
        }
        for (int i = 0, size = element.nodeCount(); i < size; ++i) {
            final Node node = element.node(i);
            this.fireRule(node);
        }
    }
    
    public void applyTemplates(final Document document) throws Exception {
        for (int i = 0, size = document.nodeCount(); i < size; ++i) {
            final Node node = document.node(i);
            this.fireRule(node);
        }
    }
    
    public void addRule(final Rule rule) {
        int matchType = rule.getMatchType();
        final String name = rule.getMatchesNodeName();
        if (name != null) {
            if (matchType == 1) {
                this.elementNameRuleSets = this.addToNameMap(this.elementNameRuleSets, name, rule);
            }
            else if (matchType == 2) {
                this.attributeNameRuleSets = this.addToNameMap(this.attributeNameRuleSets, name, rule);
            }
        }
        if (matchType >= 14) {
            matchType = 0;
        }
        if (matchType == 0) {
            for (int i = 1, size = this.ruleSets.length; i < size; ++i) {
                final RuleSet ruleSet = this.ruleSets[i];
                if (ruleSet != null) {
                    ruleSet.addRule(rule);
                }
            }
        }
        this.getRuleSet(matchType).addRule(rule);
    }
    
    public void removeRule(final Rule rule) {
        int matchType = rule.getMatchType();
        final String name = rule.getMatchesNodeName();
        if (name != null) {
            if (matchType == 1) {
                this.removeFromNameMap(this.elementNameRuleSets, name, rule);
            }
            else if (matchType == 2) {
                this.removeFromNameMap(this.attributeNameRuleSets, name, rule);
            }
        }
        if (matchType >= 14) {
            matchType = 0;
        }
        this.getRuleSet(matchType).removeRule(rule);
        if (matchType != 0) {
            this.getRuleSet(0).removeRule(rule);
        }
    }
    
    public Rule getMatchingRule(final Node node) {
        int matchType = node.getNodeType();
        if (matchType == 1) {
            if (this.elementNameRuleSets != null) {
                final String name = node.getName();
                final RuleSet ruleSet = this.elementNameRuleSets.get(name);
                if (ruleSet != null) {
                    final Rule answer = ruleSet.getMatchingRule(node);
                    if (answer != null) {
                        return answer;
                    }
                }
            }
        }
        else if (matchType == 2 && this.attributeNameRuleSets != null) {
            final String name = node.getName();
            final RuleSet ruleSet = this.attributeNameRuleSets.get(name);
            if (ruleSet != null) {
                final Rule answer = ruleSet.getMatchingRule(node);
                if (answer != null) {
                    return answer;
                }
            }
        }
        if (matchType < 0 || matchType >= this.ruleSets.length) {
            matchType = 0;
        }
        Rule answer2 = null;
        RuleSet ruleSet = this.ruleSets[matchType];
        if (ruleSet != null) {
            answer2 = ruleSet.getMatchingRule(node);
        }
        if (answer2 == null && matchType != 0) {
            ruleSet = this.ruleSets[0];
            if (ruleSet != null) {
                answer2 = ruleSet.getMatchingRule(node);
            }
        }
        return answer2;
    }
    
    protected RuleSet getRuleSet(final int matchType) {
        RuleSet ruleSet = this.ruleSets[matchType];
        if (ruleSet == null) {
            ruleSet = new RuleSet();
            this.ruleSets[matchType] = ruleSet;
            if (matchType != 0) {
                final RuleSet allRules = this.ruleSets[0];
                if (allRules != null) {
                    ruleSet.addAll(allRules);
                }
            }
        }
        return ruleSet;
    }
    
    protected Map addToNameMap(Map map, final String name, final Rule rule) {
        if (map == null) {
            map = (HashMap<String, RuleSet>)new HashMap<Object, Object>();
        }
        RuleSet ruleSet = (RuleSet)map.get(name);
        if (ruleSet == null) {
            ruleSet = new RuleSet();
            map.put(name, ruleSet);
        }
        ruleSet.addRule(rule);
        return map;
    }
    
    protected void removeFromNameMap(final Map map, final String name, final Rule rule) {
        if (map != null) {
            final RuleSet ruleSet = map.get(name);
            if (ruleSet != null) {
                ruleSet.removeRule(rule);
            }
        }
    }
}
