// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.rule;

import com.newrelic.org.dom4j.Node;

public class Rule implements Comparable
{
    private String mode;
    private int importPrecedence;
    private double priority;
    private int appearenceCount;
    private Pattern pattern;
    private Action action;
    
    public Rule() {
        this.priority = 0.5;
    }
    
    public Rule(final Pattern pattern) {
        this.pattern = pattern;
        this.priority = pattern.getPriority();
    }
    
    public Rule(final Pattern pattern, final Action action) {
        this(pattern);
        this.action = action;
    }
    
    public Rule(final Rule that, final Pattern pattern) {
        this.mode = that.mode;
        this.importPrecedence = that.importPrecedence;
        this.priority = that.priority;
        this.appearenceCount = that.appearenceCount;
        this.action = that.action;
        this.pattern = pattern;
    }
    
    public boolean equals(final Object that) {
        return that instanceof Rule && this.compareTo((Rule)that) == 0;
    }
    
    public int hashCode() {
        return this.importPrecedence + this.appearenceCount;
    }
    
    public int compareTo(final Object that) {
        if (that instanceof Rule) {
            return this.compareTo((Rule)that);
        }
        return this.getClass().getName().compareTo(that.getClass().getName());
    }
    
    public int compareTo(final Rule that) {
        int answer = this.importPrecedence - that.importPrecedence;
        if (answer == 0) {
            answer = (int)Math.round(this.priority - that.priority);
            if (answer == 0) {
                answer = this.appearenceCount - that.appearenceCount;
            }
        }
        return answer;
    }
    
    public String toString() {
        return super.toString() + "[ pattern: " + this.getPattern() + " action: " + this.getAction() + " ]";
    }
    
    public final boolean matches(final Node node) {
        return this.pattern.matches(node);
    }
    
    public Rule[] getUnionRules() {
        final Pattern[] patterns = this.pattern.getUnionPatterns();
        if (patterns == null) {
            return null;
        }
        final int size = patterns.length;
        final Rule[] answer = new Rule[size];
        for (int i = 0; i < size; ++i) {
            answer[i] = new Rule(this, patterns[i]);
        }
        return answer;
    }
    
    public final short getMatchType() {
        return this.pattern.getMatchType();
    }
    
    public final String getMatchesNodeName() {
        return this.pattern.getMatchesNodeName();
    }
    
    public String getMode() {
        return this.mode;
    }
    
    public void setMode(final String mode) {
        this.mode = mode;
    }
    
    public int getImportPrecedence() {
        return this.importPrecedence;
    }
    
    public void setImportPrecedence(final int importPrecedence) {
        this.importPrecedence = importPrecedence;
    }
    
    public double getPriority() {
        return this.priority;
    }
    
    public void setPriority(final double priority) {
        this.priority = priority;
    }
    
    public int getAppearenceCount() {
        return this.appearenceCount;
    }
    
    public void setAppearenceCount(final int appearenceCount) {
        this.appearenceCount = appearenceCount;
    }
    
    public Pattern getPattern() {
        return this.pattern;
    }
    
    public void setPattern(final Pattern pattern) {
        this.pattern = pattern;
    }
    
    public Action getAction() {
        return this.action;
    }
    
    public void setAction(final Action action) {
        this.action = action;
    }
}
