// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j;

import java.io.PrintWriter;
import java.io.PrintStream;

public class DocumentException extends Exception
{
    private Throwable nestedException;
    
    public DocumentException() {
        super("Error occurred in DOM4J application.");
    }
    
    public DocumentException(final String message) {
        super(message);
    }
    
    public DocumentException(final Throwable nestedException) {
        super(nestedException.getMessage());
        this.nestedException = nestedException;
    }
    
    public DocumentException(final String message, final Throwable nestedException) {
        super(message);
        this.nestedException = nestedException;
    }
    
    public Throwable getNestedException() {
        return this.nestedException;
    }
    
    public String getMessage() {
        if (this.nestedException != null) {
            return super.getMessage() + " Nested exception: " + this.nestedException.getMessage();
        }
        return super.getMessage();
    }
    
    public void printStackTrace() {
        super.printStackTrace();
        if (this.nestedException != null) {
            System.err.print("Nested exception: ");
            this.nestedException.printStackTrace();
        }
    }
    
    public void printStackTrace(final PrintStream out) {
        super.printStackTrace(out);
        if (this.nestedException != null) {
            out.println("Nested exception: ");
            this.nestedException.printStackTrace(out);
        }
    }
    
    public void printStackTrace(final PrintWriter writer) {
        super.printStackTrace(writer);
        if (this.nestedException != null) {
            writer.println("Nested exception: ");
            this.nestedException.printStackTrace(writer);
        }
    }
}
