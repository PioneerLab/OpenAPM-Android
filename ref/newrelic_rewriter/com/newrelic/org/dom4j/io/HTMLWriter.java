// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.io;

import com.newrelic.org.dom4j.Document;
import com.newrelic.org.dom4j.DocumentHelper;
import java.io.StringWriter;
import com.newrelic.org.dom4j.DocumentException;
import com.newrelic.org.dom4j.Element;
import java.util.Iterator;
import java.util.Set;
import com.newrelic.org.dom4j.Entity;
import java.io.IOException;
import org.xml.sax.SAXException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Stack;
import java.util.HashSet;

public class HTMLWriter extends XMLWriter
{
    private static String lineSeparator;
    protected static final HashSet DEFAULT_PREFORMATTED_TAGS;
    protected static final OutputFormat DEFAULT_HTML_FORMAT;
    private Stack formatStack;
    private String lastText;
    private int tagsOuput;
    private int newLineAfterNTags;
    private HashSet preformattedTags;
    private HashSet omitElementCloseSet;
    
    public HTMLWriter(final Writer writer) {
        super(writer, HTMLWriter.DEFAULT_HTML_FORMAT);
        this.formatStack = new Stack();
        this.lastText = "";
        this.tagsOuput = 0;
        this.newLineAfterNTags = -1;
        this.preformattedTags = HTMLWriter.DEFAULT_PREFORMATTED_TAGS;
    }
    
    public HTMLWriter(final Writer writer, final OutputFormat format) {
        super(writer, format);
        this.formatStack = new Stack();
        this.lastText = "";
        this.tagsOuput = 0;
        this.newLineAfterNTags = -1;
        this.preformattedTags = HTMLWriter.DEFAULT_PREFORMATTED_TAGS;
    }
    
    public HTMLWriter() throws UnsupportedEncodingException {
        super(HTMLWriter.DEFAULT_HTML_FORMAT);
        this.formatStack = new Stack();
        this.lastText = "";
        this.tagsOuput = 0;
        this.newLineAfterNTags = -1;
        this.preformattedTags = HTMLWriter.DEFAULT_PREFORMATTED_TAGS;
    }
    
    public HTMLWriter(final OutputFormat format) throws UnsupportedEncodingException {
        super(format);
        this.formatStack = new Stack();
        this.lastText = "";
        this.tagsOuput = 0;
        this.newLineAfterNTags = -1;
        this.preformattedTags = HTMLWriter.DEFAULT_PREFORMATTED_TAGS;
    }
    
    public HTMLWriter(final OutputStream out) throws UnsupportedEncodingException {
        super(out, HTMLWriter.DEFAULT_HTML_FORMAT);
        this.formatStack = new Stack();
        this.lastText = "";
        this.tagsOuput = 0;
        this.newLineAfterNTags = -1;
        this.preformattedTags = HTMLWriter.DEFAULT_PREFORMATTED_TAGS;
    }
    
    public HTMLWriter(final OutputStream out, final OutputFormat format) throws UnsupportedEncodingException {
        super(out, format);
        this.formatStack = new Stack();
        this.lastText = "";
        this.tagsOuput = 0;
        this.newLineAfterNTags = -1;
        this.preformattedTags = HTMLWriter.DEFAULT_PREFORMATTED_TAGS;
    }
    
    public void startCDATA() throws SAXException {
    }
    
    public void endCDATA() throws SAXException {
    }
    
    protected void writeCDATA(final String text) throws IOException {
        if (this.getOutputFormat().isXHTML()) {
            super.writeCDATA(text);
        }
        else {
            this.writer.write(text);
        }
        this.lastOutputNodeType = 4;
    }
    
    protected void writeEntity(final Entity entity) throws IOException {
        this.writer.write(entity.getText());
        this.lastOutputNodeType = 5;
    }
    
    protected void writeDeclaration() throws IOException {
    }
    
    protected void writeString(final String text) throws IOException {
        if (text.equals("\n")) {
            if (!this.formatStack.empty()) {
                super.writeString(HTMLWriter.lineSeparator);
            }
            return;
        }
        this.lastText = text;
        if (this.formatStack.empty()) {
            super.writeString(text.trim());
        }
        else {
            super.writeString(text);
        }
    }
    
    protected void writeClose(final String qualifiedName) throws IOException {
        if (!this.omitElementClose(qualifiedName)) {
            super.writeClose(qualifiedName);
        }
    }
    
    protected void writeEmptyElementClose(final String qualifiedName) throws IOException {
        if (this.getOutputFormat().isXHTML()) {
            if (this.omitElementClose(qualifiedName)) {
                this.writer.write(" />");
            }
            else {
                super.writeEmptyElementClose(qualifiedName);
            }
        }
        else if (this.omitElementClose(qualifiedName)) {
            this.writer.write(">");
        }
        else {
            super.writeEmptyElementClose(qualifiedName);
        }
    }
    
    protected boolean omitElementClose(final String qualifiedName) {
        return this.internalGetOmitElementCloseSet().contains(qualifiedName.toUpperCase());
    }
    
    private HashSet internalGetOmitElementCloseSet() {
        if (this.omitElementCloseSet == null) {
            this.loadOmitElementCloseSet(this.omitElementCloseSet = new HashSet());
        }
        return this.omitElementCloseSet;
    }
    
    protected void loadOmitElementCloseSet(final Set set) {
        set.add("AREA");
        set.add("BASE");
        set.add("BR");
        set.add("COL");
        set.add("HR");
        set.add("IMG");
        set.add("INPUT");
        set.add("LINK");
        set.add("META");
        set.add("P");
        set.add("PARAM");
    }
    
    public Set getOmitElementCloseSet() {
        return (Set)this.internalGetOmitElementCloseSet().clone();
    }
    
    public void setOmitElementCloseSet(final Set newSet) {
        this.omitElementCloseSet = new HashSet();
        if (newSet != null) {
            this.omitElementCloseSet = new HashSet();
            for (final Object aTag : newSet) {
                if (aTag != null) {
                    this.omitElementCloseSet.add(aTag.toString().toUpperCase());
                }
            }
        }
    }
    
    public Set getPreformattedTags() {
        return (Set)this.preformattedTags.clone();
    }
    
    public void setPreformattedTags(final Set newSet) {
        this.preformattedTags = new HashSet();
        if (newSet != null) {
            for (final Object aTag : newSet) {
                if (aTag != null) {
                    this.preformattedTags.add(aTag.toString().toUpperCase());
                }
            }
        }
    }
    
    public boolean isPreformattedTag(final String qualifiedName) {
        return this.preformattedTags != null && this.preformattedTags.contains(qualifiedName.toUpperCase());
    }
    
    protected void writeElement(final Element element) throws IOException {
        if (this.newLineAfterNTags == -1) {
            this.lazyInitNewLinesAfterNTags();
        }
        if (this.newLineAfterNTags > 0 && this.tagsOuput > 0 && this.tagsOuput % this.newLineAfterNTags == 0) {
            super.writer.write(HTMLWriter.lineSeparator);
        }
        ++this.tagsOuput;
        final String qualifiedName = element.getQualifiedName();
        final String saveLastText = this.lastText;
        final int size = element.nodeCount();
        if (this.isPreformattedTag(qualifiedName)) {
            final OutputFormat currentFormat = this.getOutputFormat();
            final boolean saveNewlines = currentFormat.isNewlines();
            final boolean saveTrimText = currentFormat.isTrimText();
            final String currentIndent = currentFormat.getIndent();
            this.formatStack.push(new FormatState(saveNewlines, saveTrimText, currentIndent));
            try {
                super.writePrintln();
                if (saveLastText.trim().length() == 0 && currentIndent != null && currentIndent.length() > 0) {
                    super.writer.write(this.justSpaces(saveLastText));
                }
                currentFormat.setNewlines(false);
                currentFormat.setTrimText(false);
                currentFormat.setIndent("");
                super.writeElement(element);
            }
            finally {
                final FormatState state = this.formatStack.pop();
                currentFormat.setNewlines(state.isNewlines());
                currentFormat.setTrimText(state.isTrimText());
                currentFormat.setIndent(state.getIndent());
            }
        }
        else {
            super.writeElement(element);
        }
    }
    
    private String justSpaces(final String text) {
        final int size = text.length();
        final StringBuffer res = new StringBuffer(size);
        for (int i = 0; i < size; ++i) {
            final char c = text.charAt(i);
            switch (c) {
                case '\n':
                case '\r': {
                    break;
                }
                default: {
                    res.append(c);
                    break;
                }
            }
        }
        return res.toString();
    }
    
    private void lazyInitNewLinesAfterNTags() {
        if (this.getOutputFormat().isNewlines()) {
            this.newLineAfterNTags = 0;
        }
        else {
            this.newLineAfterNTags = this.getOutputFormat().getNewLineAfterNTags();
        }
    }
    
    public static String prettyPrintHTML(final String html) throws IOException, UnsupportedEncodingException, DocumentException {
        return prettyPrintHTML(html, true, true, false, true);
    }
    
    public static String prettyPrintXHTML(final String html) throws IOException, UnsupportedEncodingException, DocumentException {
        return prettyPrintHTML(html, true, true, true, false);
    }
    
    public static String prettyPrintHTML(final String html, final boolean newlines, final boolean trim, final boolean isXHTML, final boolean expandEmpty) throws IOException, UnsupportedEncodingException, DocumentException {
        final StringWriter sw = new StringWriter();
        final OutputFormat format = OutputFormat.createPrettyPrint();
        format.setNewlines(newlines);
        format.setTrimText(trim);
        format.setXHTML(isXHTML);
        format.setExpandEmptyElements(expandEmpty);
        final HTMLWriter writer = new HTMLWriter(sw, format);
        final Document document = DocumentHelper.parseText(html);
        writer.write(document);
        writer.flush();
        return sw.toString();
    }
    
    static {
        HTMLWriter.lineSeparator = System.getProperty("line.separator");
        (DEFAULT_PREFORMATTED_TAGS = new HashSet()).add("PRE");
        HTMLWriter.DEFAULT_PREFORMATTED_TAGS.add("SCRIPT");
        HTMLWriter.DEFAULT_PREFORMATTED_TAGS.add("STYLE");
        HTMLWriter.DEFAULT_PREFORMATTED_TAGS.add("TEXTAREA");
        (DEFAULT_HTML_FORMAT = new OutputFormat("  ", true)).setTrimText(true);
        HTMLWriter.DEFAULT_HTML_FORMAT.setSuppressDeclaration(true);
    }
    
    private class FormatState
    {
        private boolean newlines;
        private boolean trimText;
        private String indent;
        
        public FormatState(final boolean newLines, final boolean trimText, final String indent) {
            this.newlines = false;
            this.trimText = false;
            this.indent = "";
            this.newlines = newLines;
            this.trimText = trimText;
            this.indent = indent;
        }
        
        public boolean isNewlines() {
            return this.newlines;
        }
        
        public boolean isTrimText() {
            return this.trimText;
        }
        
        public String getIndent() {
            return this.indent;
        }
    }
}
