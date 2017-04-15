// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.com.google.common.io;

import java.io.IOException;

@Deprecated
public interface OutputSupplier<T>
{
    T getOutput() throws IOException;
}
