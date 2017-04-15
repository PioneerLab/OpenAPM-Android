// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.javassist.compiler;

import com.newrelic.javassist.NotFoundException;
import com.newrelic.javassist.CannotCompileException;

public class CompileError extends Exception
{
    private Lex lex;
    private String reason;
    
    public CompileError(final String s, final Lex l) {
        this.reason = s;
        this.lex = l;
    }
    
    public CompileError(final String s) {
        this.reason = s;
        this.lex = null;
    }
    
    public CompileError(final CannotCompileException e) {
        this(e.getReason());
    }
    
    public CompileError(final NotFoundException e) {
        this("cannot find " + e.getMessage());
    }
    
    public Lex getLex() {
        return this.lex;
    }
    
    public String getMessage() {
        return this.reason;
    }
    
    public String toString() {
        return "compile error: " + this.reason;
    }
}
