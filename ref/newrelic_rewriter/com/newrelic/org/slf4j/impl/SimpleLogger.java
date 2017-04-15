// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.slf4j.impl;

import com.newrelic.org.slf4j.helpers.FormattingTuple;
import com.newrelic.org.slf4j.helpers.MessageFormatter;
import com.newrelic.org.slf4j.helpers.MarkerIgnoringBase;

public class SimpleLogger extends MarkerIgnoringBase
{
    private static final long serialVersionUID = -6560244151660620173L;
    private static long startTime;
    public static final String LINE_SEPARATOR;
    private static String INFO_STR;
    private static String WARN_STR;
    private static String ERROR_STR;
    
    SimpleLogger(final String name) {
        this.name = name;
    }
    
    public boolean isTraceEnabled() {
        return false;
    }
    
    public void trace(final String msg) {
    }
    
    public void trace(final String format, final Object param1) {
    }
    
    public void trace(final String format, final Object param1, final Object param2) {
    }
    
    public void trace(final String format, final Object[] argArray) {
    }
    
    public void trace(final String msg, final Throwable t) {
    }
    
    public boolean isDebugEnabled() {
        return false;
    }
    
    public void debug(final String msg) {
    }
    
    public void debug(final String format, final Object param1) {
    }
    
    public void debug(final String format, final Object param1, final Object param2) {
    }
    
    public void debug(final String format, final Object[] argArray) {
    }
    
    public void debug(final String msg, final Throwable t) {
    }
    
    private void log(final String level, final String message, final Throwable t) {
        final StringBuffer buf = new StringBuffer();
        final long millis = System.currentTimeMillis();
        buf.append(millis - SimpleLogger.startTime);
        buf.append(" [");
        buf.append(Thread.currentThread().getName());
        buf.append("] ");
        buf.append(level);
        buf.append(" ");
        buf.append(this.name);
        buf.append(" - ");
        buf.append(message);
        buf.append(SimpleLogger.LINE_SEPARATOR);
        System.err.print(buf.toString());
        if (t != null) {
            t.printStackTrace(System.err);
        }
        System.err.flush();
    }
    
    private void formatAndLog(final String level, final String format, final Object arg1, final Object arg2) {
        final FormattingTuple tp = MessageFormatter.format(format, arg1, arg2);
        this.log(level, tp.getMessage(), tp.getThrowable());
    }
    
    private void formatAndLog(final String level, final String format, final Object[] argArray) {
        final FormattingTuple tp = MessageFormatter.arrayFormat(format, argArray);
        this.log(level, tp.getMessage(), tp.getThrowable());
    }
    
    public boolean isInfoEnabled() {
        return true;
    }
    
    public void info(final String msg) {
        this.log(SimpleLogger.INFO_STR, msg, null);
    }
    
    public void info(final String format, final Object arg) {
        this.formatAndLog(SimpleLogger.INFO_STR, format, arg, null);
    }
    
    public void info(final String format, final Object arg1, final Object arg2) {
        this.formatAndLog(SimpleLogger.INFO_STR, format, arg1, arg2);
    }
    
    public void info(final String format, final Object[] argArray) {
        this.formatAndLog(SimpleLogger.INFO_STR, format, argArray);
    }
    
    public void info(final String msg, final Throwable t) {
        this.log(SimpleLogger.INFO_STR, msg, t);
    }
    
    public boolean isWarnEnabled() {
        return true;
    }
    
    public void warn(final String msg) {
        this.log(SimpleLogger.WARN_STR, msg, null);
    }
    
    public void warn(final String format, final Object arg) {
        this.formatAndLog(SimpleLogger.WARN_STR, format, arg, null);
    }
    
    public void warn(final String format, final Object arg1, final Object arg2) {
        this.formatAndLog(SimpleLogger.WARN_STR, format, arg1, arg2);
    }
    
    public void warn(final String format, final Object[] argArray) {
        this.formatAndLog(SimpleLogger.WARN_STR, format, argArray);
    }
    
    public void warn(final String msg, final Throwable t) {
        this.log(SimpleLogger.WARN_STR, msg, t);
    }
    
    public boolean isErrorEnabled() {
        return true;
    }
    
    public void error(final String msg) {
        this.log(SimpleLogger.ERROR_STR, msg, null);
    }
    
    public void error(final String format, final Object arg) {
        this.formatAndLog(SimpleLogger.ERROR_STR, format, arg, null);
    }
    
    public void error(final String format, final Object arg1, final Object arg2) {
        this.formatAndLog(SimpleLogger.ERROR_STR, format, arg1, arg2);
    }
    
    public void error(final String format, final Object[] argArray) {
        this.formatAndLog(SimpleLogger.ERROR_STR, format, argArray);
    }
    
    public void error(final String msg, final Throwable t) {
        this.log(SimpleLogger.ERROR_STR, msg, t);
    }
    
    static {
        SimpleLogger.startTime = System.currentTimeMillis();
        LINE_SEPARATOR = System.getProperty("line.separator");
        SimpleLogger.INFO_STR = "INFO";
        SimpleLogger.WARN_STR = "WARN";
        SimpleLogger.ERROR_STR = "ERROR";
    }
}
