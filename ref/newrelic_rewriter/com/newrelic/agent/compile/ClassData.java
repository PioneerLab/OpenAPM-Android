// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.compile;

public class ClassData
{
    private final byte[] mainClassBytes;
    private final String shimClassName;
    private final byte[] shimClassBytes;
    private final boolean modified;
    
    public ClassData(final byte[] mainClassBytes, final String shimClassName, final byte[] shimClassBytes, final boolean modified) {
        this.mainClassBytes = mainClassBytes;
        this.shimClassName = shimClassName;
        this.shimClassBytes = shimClassBytes;
        this.modified = modified;
    }
    
    public ClassData(final byte[] mainClassBytes, final boolean modified) {
        this(mainClassBytes, null, null, modified);
    }
    
    public byte[] getMainClassBytes() {
        return this.mainClassBytes;
    }
    
    public String getShimClassName() {
        return this.shimClassName;
    }
    
    public byte[] getShimClassBytes() {
        return this.shimClassBytes;
    }
    
    public boolean isShimPresent() {
        return this.shimClassName != null;
    }
    
    public boolean isModified() {
        return this.modified;
    }
}
