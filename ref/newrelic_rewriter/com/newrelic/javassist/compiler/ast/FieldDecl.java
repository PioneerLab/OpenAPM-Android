// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.javassist.compiler.ast;

import com.newrelic.javassist.compiler.CompileError;

public class FieldDecl extends ASTList
{
    public FieldDecl(final ASTree _head, final ASTList _tail) {
        super(_head, _tail);
    }
    
    public ASTList getModifiers() {
        return (ASTList)this.getLeft();
    }
    
    public Declarator getDeclarator() {
        return (Declarator)this.tail().head();
    }
    
    public ASTree getInit() {
        return this.sublist(2).head();
    }
    
    public void accept(final Visitor v) throws CompileError {
        v.atFieldDecl(this);
    }
}
