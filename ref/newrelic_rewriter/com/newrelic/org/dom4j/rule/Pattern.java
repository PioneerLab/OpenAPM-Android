// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.rule;

import com.newrelic.org.dom4j.Node;
import com.newrelic.org.dom4j.NodeFilter;

public interface Pattern extends NodeFilter
{
    public static final short ANY_NODE = 0;
    public static final short NONE = 9999;
    public static final short NUMBER_OF_TYPES = 14;
    public static final double DEFAULT_PRIORITY = 0.5;
    
    boolean matches(final Node p0);
    
    double getPriority();
    
    Pattern[] getUnionPatterns();
    
    short getMatchType();
    
    String getMatchesNodeName();
}
