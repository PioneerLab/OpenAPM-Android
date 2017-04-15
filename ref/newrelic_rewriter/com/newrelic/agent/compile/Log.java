// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.compile;

import java.util.HashMap;
import java.util.Map;

public abstract class Log
{
    public static Log LOGGER;
    protected final int logLevel;
    
    public Log(final Map<String, String> agentOptions) {
        final String logLevelOpt = agentOptions.get("loglevel");
        if (logLevelOpt != null) {
            this.logLevel = LogLevel.valueOf(logLevelOpt).getValue();
        }
        else {
            this.logLevel = LogLevel.WARN.getValue();
        }
        Log.LOGGER = this;
    }
    
    public void info(final String message) {
        if (this.logLevel >= LogLevel.INFO.getValue()) {
            this.log("info", message);
        }
    }
    
    public void debug(final String message) {
        if (this.logLevel >= LogLevel.DEBUG.getValue()) {
            synchronized (this) {
                this.log("debug", message);
            }
        }
    }
    
    public void warning(final String message) {
        if (this.logLevel >= LogLevel.WARN.getValue()) {
            this.log("warn", message);
        }
    }
    
    public void error(final String message) {
        if (this.logLevel >= LogLevel.ERROR.getValue()) {
            this.log("error", message);
        }
    }
    
    protected void log(final String level, final String message) {
    }
    
    public void warning(final String message, final Throwable cause) {
    }
    
    public void error(final String message, final Throwable cause) {
    }
    
    static {
        Log.LOGGER = new Log(new HashMap()) {};
    }
    
    public enum LogLevel
    {
        DEBUG(5), 
        VERBOSE(4), 
        INFO(3), 
        WARN(2), 
        ERROR(1);
        
        private final int value;
        
        private LogLevel(final int newValue) {
            this.value = newValue;
        }
        
        public int getValue() {
            return this.value;
        }
    }
}
