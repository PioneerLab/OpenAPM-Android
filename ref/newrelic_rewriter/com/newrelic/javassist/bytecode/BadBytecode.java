// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.javassist.bytecode;

public class BadBytecode extends Exception
{
    public BadBytecode(final int opcode) {
        super("bytecode " + opcode);
    }
    
    public BadBytecode(final String msg) {
        super(msg);
    }
    
    public BadBytecode(final String msg, final Throwable cause) {
        super(msg, cause);
    }
}
