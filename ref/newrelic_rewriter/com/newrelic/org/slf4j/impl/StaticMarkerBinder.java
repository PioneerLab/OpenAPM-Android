// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.slf4j.impl;

import com.newrelic.org.slf4j.helpers.BasicMarkerFactory;
import com.newrelic.org.slf4j.IMarkerFactory;
import com.newrelic.org.slf4j.spi.MarkerFactoryBinder;

public class StaticMarkerBinder implements MarkerFactoryBinder
{
    public static final StaticMarkerBinder SINGLETON;
    final IMarkerFactory markerFactory;
    static /* synthetic */ Class class$org$slf4j$helpers$BasicMarkerFactory;
    
    private StaticMarkerBinder() {
        this.markerFactory = new BasicMarkerFactory();
    }
    
    public IMarkerFactory getMarkerFactory() {
        return this.markerFactory;
    }
    
    public String getMarkerFactoryClassStr() {
        return ((StaticMarkerBinder.class$org$slf4j$helpers$BasicMarkerFactory == null) ? (StaticMarkerBinder.class$org$slf4j$helpers$BasicMarkerFactory = class$("com.newrelic.org.slf4j.helpers.BasicMarkerFactory")) : StaticMarkerBinder.class$org$slf4j$helpers$BasicMarkerFactory).getName();
    }
    
    static /* synthetic */ Class class$(final String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x) {
            throw new NoClassDefFoundError(x.getMessage());
        }
    }
    
    static {
        SINGLETON = new StaticMarkerBinder();
    }
}
