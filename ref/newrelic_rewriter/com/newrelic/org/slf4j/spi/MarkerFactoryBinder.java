// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.slf4j.spi;

import com.newrelic.org.slf4j.IMarkerFactory;

public interface MarkerFactoryBinder
{
    IMarkerFactory getMarkerFactory();
    
    String getMarkerFactoryClassStr();
}
