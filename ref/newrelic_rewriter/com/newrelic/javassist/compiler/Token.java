// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.javassist.compiler;

class Token
{
    public Token next;
    public int tokenId;
    public long longValue;
    public double doubleValue;
    public String textValue;
    
    Token() {
        this.next = null;
    }
}
