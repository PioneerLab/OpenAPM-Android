// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.io;

public class OutputFormat implements Cloneable
{
    protected static final String STANDARD_INDENT = "  ";
    private boolean suppressDeclaration;
    private boolean newLineAfterDeclaration;
    private String encoding;
    private boolean omitEncoding;
    private String indent;
    private boolean expandEmptyElements;
    private boolean newlines;
    private String lineSeparator;
    private boolean trimText;
    private boolean padText;
    private boolean doXHTML;
    private int newLineAfterNTags;
    private char attributeQuoteChar;
    
    public OutputFormat() {
        this.suppressDeclaration = false;
        this.newLineAfterDeclaration = true;
        this.encoding = "UTF-8";
        this.omitEncoding = false;
        this.indent = null;
        this.expandEmptyElements = false;
        this.newlines = false;
        this.lineSeparator = "\n";
        this.trimText = false;
        this.padText = false;
        this.doXHTML = false;
        this.newLineAfterNTags = 0;
        this.attributeQuoteChar = '\"';
    }
    
    public OutputFormat(final String indent) {
        this.suppressDeclaration = false;
        this.newLineAfterDeclaration = true;
        this.encoding = "UTF-8";
        this.omitEncoding = false;
        this.indent = null;
        this.expandEmptyElements = false;
        this.newlines = false;
        this.lineSeparator = "\n";
        this.trimText = false;
        this.padText = false;
        this.doXHTML = false;
        this.newLineAfterNTags = 0;
        this.attributeQuoteChar = '\"';
        this.indent = indent;
    }
    
    public OutputFormat(final String indent, final boolean newlines) {
        this.suppressDeclaration = false;
        this.newLineAfterDeclaration = true;
        this.encoding = "UTF-8";
        this.omitEncoding = false;
        this.indent = null;
        this.expandEmptyElements = false;
        this.newlines = false;
        this.lineSeparator = "\n";
        this.trimText = false;
        this.padText = false;
        this.doXHTML = false;
        this.newLineAfterNTags = 0;
        this.attributeQuoteChar = '\"';
        this.indent = indent;
        this.newlines = newlines;
    }
    
    public OutputFormat(final String indent, final boolean newlines, final String encoding) {
        this.suppressDeclaration = false;
        this.newLineAfterDeclaration = true;
        this.encoding = "UTF-8";
        this.omitEncoding = false;
        this.indent = null;
        this.expandEmptyElements = false;
        this.newlines = false;
        this.lineSeparator = "\n";
        this.trimText = false;
        this.padText = false;
        this.doXHTML = false;
        this.newLineAfterNTags = 0;
        this.attributeQuoteChar = '\"';
        this.indent = indent;
        this.newlines = newlines;
        this.encoding = encoding;
    }
    
    public String getLineSeparator() {
        return this.lineSeparator;
    }
    
    public void setLineSeparator(final String separator) {
        this.lineSeparator = separator;
    }
    
    public boolean isNewlines() {
        return this.newlines;
    }
    
    public void setNewlines(final boolean newlines) {
        this.newlines = newlines;
    }
    
    public String getEncoding() {
        return this.encoding;
    }
    
    public void setEncoding(final String encoding) {
        if (encoding != null) {
            this.encoding = encoding;
        }
    }
    
    public boolean isOmitEncoding() {
        return this.omitEncoding;
    }
    
    public void setOmitEncoding(final boolean omitEncoding) {
        this.omitEncoding = omitEncoding;
    }
    
    public void setSuppressDeclaration(final boolean suppressDeclaration) {
        this.suppressDeclaration = suppressDeclaration;
    }
    
    public boolean isSuppressDeclaration() {
        return this.suppressDeclaration;
    }
    
    public void setNewLineAfterDeclaration(final boolean newLineAfterDeclaration) {
        this.newLineAfterDeclaration = newLineAfterDeclaration;
    }
    
    public boolean isNewLineAfterDeclaration() {
        return this.newLineAfterDeclaration;
    }
    
    public boolean isExpandEmptyElements() {
        return this.expandEmptyElements;
    }
    
    public void setExpandEmptyElements(final boolean expandEmptyElements) {
        this.expandEmptyElements = expandEmptyElements;
    }
    
    public boolean isTrimText() {
        return this.trimText;
    }
    
    public void setTrimText(final boolean trimText) {
        this.trimText = trimText;
    }
    
    public boolean isPadText() {
        return this.padText;
    }
    
    public void setPadText(final boolean padText) {
        this.padText = padText;
    }
    
    public String getIndent() {
        return this.indent;
    }
    
    public void setIndent(String indent) {
        if (indent != null && indent.length() <= 0) {
            indent = null;
        }
        this.indent = indent;
    }
    
    public void setIndent(final boolean doIndent) {
        if (doIndent) {
            this.indent = "  ";
        }
        else {
            this.indent = null;
        }
    }
    
    public void setIndentSize(final int indentSize) {
        final StringBuffer indentBuffer = new StringBuffer();
        for (int i = 0; i < indentSize; ++i) {
            indentBuffer.append(" ");
        }
        this.indent = indentBuffer.toString();
    }
    
    public boolean isXHTML() {
        return this.doXHTML;
    }
    
    public void setXHTML(final boolean xhtml) {
        this.doXHTML = xhtml;
    }
    
    public int getNewLineAfterNTags() {
        return this.newLineAfterNTags;
    }
    
    public void setNewLineAfterNTags(final int tagCount) {
        this.newLineAfterNTags = tagCount;
    }
    
    public char getAttributeQuoteCharacter() {
        return this.attributeQuoteChar;
    }
    
    public void setAttributeQuoteCharacter(final char quoteChar) {
        if (quoteChar == '\'' || quoteChar == '\"') {
            this.attributeQuoteChar = quoteChar;
            return;
        }
        throw new IllegalArgumentException("Invalid attribute quote character (" + quoteChar + ")");
    }
    
    public int parseOptions(final String[] args, int i) {
        for (int size = args.length; i < size; ++i) {
            if (args[i].equals("-suppressDeclaration")) {
                this.setSuppressDeclaration(true);
            }
            else if (args[i].equals("-omitEncoding")) {
                this.setOmitEncoding(true);
            }
            else if (args[i].equals("-indent")) {
                this.setIndent(args[++i]);
            }
            else if (args[i].equals("-indentSize")) {
                this.setIndentSize(Integer.parseInt(args[++i]));
            }
            else if (args[i].startsWith("-expandEmpty")) {
                this.setExpandEmptyElements(true);
            }
            else if (args[i].equals("-encoding")) {
                this.setEncoding(args[++i]);
            }
            else if (args[i].equals("-newlines")) {
                this.setNewlines(true);
            }
            else if (args[i].equals("-lineSeparator")) {
                this.setLineSeparator(args[++i]);
            }
            else if (args[i].equals("-trimText")) {
                this.setTrimText(true);
            }
            else if (args[i].equals("-padText")) {
                this.setPadText(true);
            }
            else {
                if (!args[i].startsWith("-xhtml")) {
                    return i;
                }
                this.setXHTML(true);
            }
        }
        return i;
    }
    
    public static OutputFormat createPrettyPrint() {
        final OutputFormat format = new OutputFormat();
        format.setIndentSize(2);
        format.setNewlines(true);
        format.setTrimText(true);
        format.setPadText(true);
        return format;
    }
    
    public static OutputFormat createCompactFormat() {
        final OutputFormat format = new OutputFormat();
        format.setIndent(false);
        format.setNewlines(false);
        format.setTrimText(true);
        return format;
    }
}
