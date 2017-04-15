// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.compile;

import java.util.Map;

public final class SystemErrLog extends Log
{
    public SystemErrLog(final Map<String, String> agentOptions) {
        super(agentOptions);
    }
    
    @Override
    protected void log(final String level, final String message) {
        synchronized (this) {
            System.out.println("[newrelic." + level.toLowerCase() + "] " + message);
        }
    }
    
    @Override
    public void warning(final String message, final Throwable cause) {
        if (this.logLevel >= LogLevel.WARN.getValue()) {
            synchronized (this) {
                this.log("warn", message);
                cause.printStackTrace(System.err);
            }
        }
    }
    
    @Override
    public void error(final String message, final Throwable cause) {
        if (this.logLevel >= LogLevel.WARN.getValue()) {
            synchronized (this) {
                this.log("error", message);
                cause.printStackTrace(System.err);
            }
        }
    }
}
