// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.javassist.compiler.ast;

import com.newrelic.javassist.compiler.CompileError;

public class ArrayInit extends ASTList
{
    public ArrayInit(final ASTree firstElement) {
        super(firstElement);
    }
    
    public void accept(final Visitor v) throws CompileError {
        v.atArrayInit(this);
    }
    
    public String getTag() {
        return "array";
    }
}
