// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.compile;

import com.newrelic.org.objectweb.asm.ClassVisitor;

public abstract class ClassVisitorFactory
{
    private final boolean retransformOkay;
    
    public ClassVisitorFactory(final boolean retransformOkay) {
        this.retransformOkay = retransformOkay;
    }
    
    public boolean isRetransformOkay() {
        return this.retransformOkay;
    }
    
    public abstract ClassVisitor create(final ClassVisitor p0);
}
