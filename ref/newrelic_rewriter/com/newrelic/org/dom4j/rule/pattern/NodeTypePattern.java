// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.rule.pattern;

import com.newrelic.org.dom4j.Node;
import com.newrelic.org.dom4j.rule.Pattern;

public class NodeTypePattern implements Pattern
{
    public static final NodeTypePattern ANY_ATTRIBUTE;
    public static final NodeTypePattern ANY_COMMENT;
    public static final NodeTypePattern ANY_DOCUMENT;
    public static final NodeTypePattern ANY_ELEMENT;
    public static final NodeTypePattern ANY_PROCESSING_INSTRUCTION;
    public static final NodeTypePattern ANY_TEXT;
    private short nodeType;
    
    public NodeTypePattern(final short nodeType) {
        this.nodeType = nodeType;
    }
    
    public boolean matches(final Node node) {
        return node.getNodeType() == this.nodeType;
    }
    
    public double getPriority() {
        return 0.5;
    }
    
    public Pattern[] getUnionPatterns() {
        return null;
    }
    
    public short getMatchType() {
        return this.nodeType;
    }
    
    public String getMatchesNodeName() {
        return null;
    }
    
    static {
        ANY_ATTRIBUTE = new NodeTypePattern((short)2);
        ANY_COMMENT = new NodeTypePattern((short)8);
        ANY_DOCUMENT = new NodeTypePattern((short)9);
        ANY_ELEMENT = new NodeTypePattern((short)1);
        ANY_PROCESSING_INSTRUCTION = new NodeTypePattern((short)7);
        ANY_TEXT = new NodeTypePattern((short)3);
    }
}
