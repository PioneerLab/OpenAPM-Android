// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.javassist.expr;

import com.newrelic.javassist.CtConstructor;
import com.newrelic.javassist.NotFoundException;
import com.newrelic.javassist.CtMethod;
import com.newrelic.javassist.bytecode.MethodInfo;
import com.newrelic.javassist.CtClass;
import com.newrelic.javassist.bytecode.CodeIterator;

public class ConstructorCall extends MethodCall
{
    protected ConstructorCall(final int pos, final CodeIterator i, final CtClass decl, final MethodInfo m) {
        super(pos, i, decl, m);
    }
    
    public String getMethodName() {
        return this.isSuper() ? "super" : "this";
    }
    
    public CtMethod getMethod() throws NotFoundException {
        throw new NotFoundException("this is a constructor call.  Call getConstructor().");
    }
    
    public CtConstructor getConstructor() throws NotFoundException {
        return this.getCtClass().getConstructor(this.getSignature());
    }
    
    public boolean isSuper() {
        return super.isSuper();
    }
}
