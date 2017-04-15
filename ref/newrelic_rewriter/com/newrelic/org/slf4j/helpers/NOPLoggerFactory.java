// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.slf4j.helpers;

import com.newrelic.org.slf4j.Logger;
import com.newrelic.org.slf4j.ILoggerFactory;

public class NOPLoggerFactory implements ILoggerFactory
{
    public Logger getLogger(final String name) {
        return NOPLogger.NOP_LOGGER;
    }
}
