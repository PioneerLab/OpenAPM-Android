// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.objectweb.asm;

public final class Handle
{
    final int a;
    final String b;
    final String c;
    final String d;
    final boolean e;
    
    public Handle(final int n, final String s, final String s2, final String s3) {
        this(n, s, s2, s3, n == 9);
    }
    
    public Handle(final int a, final String b, final String c, final String d, final boolean e) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.e = e;
    }
    
    public int getTag() {
        return this.a;
    }
    
    public String getOwner() {
        return this.b;
    }
    
    public String getName() {
        return this.c;
    }
    
    public String getDesc() {
        return this.d;
    }
    
    public boolean isInterface() {
        return this.e;
    }
    
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Handle)) {
            return false;
        }
        final Handle handle = (Handle)o;
        return this.a == handle.a && this.e == handle.e && this.b.equals(handle.b) && this.c.equals(handle.c) && this.d.equals(handle.d);
    }
    
    public int hashCode() {
        return this.a + (this.e ? 64 : 0) + this.b.hashCode() * this.c.hashCode() * this.d.hashCode();
    }
    
    public String toString() {
        return this.b + '.' + this.c + this.d + " (" + this.a + (this.e ? " itf" : "") + ')';
    }
}
