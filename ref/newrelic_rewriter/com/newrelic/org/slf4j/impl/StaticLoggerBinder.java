// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.slf4j.impl;

import com.newrelic.org.slf4j.ILoggerFactory;
import com.newrelic.org.slf4j.spi.LoggerFactoryBinder;

public class StaticLoggerBinder implements LoggerFactoryBinder
{
    private static final StaticLoggerBinder SINGLETON;
    public static String REQUESTED_API_VERSION;
    private static final String loggerFactoryClassStr;
    private final ILoggerFactory loggerFactory;
    static /* synthetic */ Class class$org$slf4j$impl$SimpleLoggerFactory;
    
    public static final StaticLoggerBinder getSingleton() {
        return StaticLoggerBinder.SINGLETON;
    }
    
    private StaticLoggerBinder() {
        this.loggerFactory = new SimpleLoggerFactory();
    }
    
    public ILoggerFactory getLoggerFactory() {
        return this.loggerFactory;
    }
    
    public String getLoggerFactoryClassStr() {
        return StaticLoggerBinder.loggerFactoryClassStr;
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
        SINGLETON = new StaticLoggerBinder();
        StaticLoggerBinder.REQUESTED_API_VERSION = "1.6";
        loggerFactoryClassStr = ((StaticLoggerBinder.class$org$slf4j$impl$SimpleLoggerFactory == null) ? (StaticLoggerBinder.class$org$slf4j$impl$SimpleLoggerFactory = class$("com.newrelic.org.slf4j.impl.SimpleLoggerFactory")) : StaticLoggerBinder.class$org$slf4j$impl$SimpleLoggerFactory).getName();
    }
}
