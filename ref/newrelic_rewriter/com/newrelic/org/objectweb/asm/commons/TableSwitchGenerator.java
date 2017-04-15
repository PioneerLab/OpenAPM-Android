// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.objectweb.asm.commons;

import com.newrelic.org.objectweb.asm.Label;

public interface TableSwitchGenerator
{
    void generateCase(final int p0, final Label p1);
    
    void generateDefault();
}
