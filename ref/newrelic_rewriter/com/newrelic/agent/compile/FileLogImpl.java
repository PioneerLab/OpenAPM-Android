// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.compile;

import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.util.Map;
import java.io.PrintWriter;

final class FileLogImpl extends Log
{
    private final PrintWriter writer;
    
    public FileLogImpl(final Map<String, String> agentOptions, final String logFileName) {
        super(agentOptions);
        try {
            this.writer = new PrintWriter(new FileOutputStream(logFileName));
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    protected void log(final String level, final String message) {
        synchronized (this) {
            this.writer.write("[newrelic." + level.toLowerCase() + "] " + message + "\n");
            this.writer.flush();
        }
    }
    
    @Override
    public void warning(final String message, final Throwable cause) {
        if (this.logLevel >= LogLevel.WARN.getValue()) {
            this.log("warn", message);
            cause.printStackTrace(this.writer);
            this.writer.flush();
        }
    }
    
    @Override
    public void error(final String message, final Throwable cause) {
        if (this.logLevel >= LogLevel.ERROR.getValue()) {
            this.log("error", message);
            cause.printStackTrace(this.writer);
            this.writer.flush();
        }
    }
}
