// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.slf4j.impl;

import com.newrelic.org.slf4j.Logger;
import java.util.HashMap;
import java.util.Map;
import com.newrelic.org.slf4j.ILoggerFactory;

public class SimpleLoggerFactory implements ILoggerFactory
{
    static final SimpleLoggerFactory INSTANCE;
    Map loggerMap;
    
    public SimpleLoggerFactory() {
        this.loggerMap = new HashMap();
    }
    
    public Logger getLogger(final String name) {
        Logger slogger = null;
        synchronized (this) {
            slogger = this.loggerMap.get(name);
            if (slogger == null) {
                slogger = new SimpleLogger(name);
                this.loggerMap.put(name, slogger);
            }
        }
        return slogger;
    }
    
    static {
        INSTANCE = new SimpleLoggerFactory();
    }
}
