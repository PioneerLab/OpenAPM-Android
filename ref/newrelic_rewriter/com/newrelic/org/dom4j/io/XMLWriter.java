// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.io;

import org.xml.sax.XMLReader;
import java.util.Iterator;
import java.util.StringTokenizer;
import org.xml.sax.Attributes;
import java.util.HashMap;
import org.xml.sax.Locator;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXException;
import org.xml.sax.InputSource;
import java.util.List;
import com.newrelic.org.dom4j.Text;
import com.newrelic.org.dom4j.ProcessingInstruction;
import com.newrelic.org.dom4j.Entity;
import com.newrelic.org.dom4j.DocumentType;
import com.newrelic.org.dom4j.Comment;
import com.newrelic.org.dom4j.CDATA;
import com.newrelic.org.dom4j.Element;
import com.newrelic.org.dom4j.Node;
import com.newrelic.org.dom4j.Document;
import com.newrelic.org.dom4j.Attribute;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.BufferedWriter;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import com.newrelic.org.dom4j.Namespace;
import java.util.Map;
import com.newrelic.org.dom4j.tree.NamespaceStack;
import java.io.Writer;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLFilterImpl;

public class XMLWriter extends XMLFilterImpl implements LexicalHandler
{
    private static final String PAD_TEXT = " ";
    protected static final String[] LEXICAL_HANDLER_NAMES;
    protected static final OutputFormat DEFAULT_FORMAT;
    private boolean resolveEntityRefs;
    protected int lastOutputNodeType;
    private boolean lastElementClosed;
    protected boolean preserve;
    protected Writer writer;
    private NamespaceStack namespaceStack;
    private OutputFormat format;
    private boolean escapeText;
    private int indentLevel;
    private StringBuffer buffer;
    private boolean charsAdded;
    private char lastChar;
    private boolean autoFlush;
    private LexicalHandler lexicalHandler;
    private boolean showCommentsInDTDs;
    private boolean inDTD;
    private Map namespacesMap;
    private int maximumAllowedCharacter;
    
    public XMLWriter(final Writer writer) {
        this(writer, XMLWriter.DEFAULT_FORMAT);
    }
    
    public XMLWriter(final Writer writer, final OutputFormat format) {
        this.resolveEntityRefs = true;
        this.lastElementClosed = false;
        this.preserve = false;
        this.namespaceStack = new NamespaceStack();
        this.escapeText = true;
        this.indentLevel = 0;
        this.buffer = new StringBuffer();
        this.charsAdded = false;
        this.writer = writer;
        this.format = format;
        this.namespaceStack.push(Namespace.NO_NAMESPACE);
    }
    
    public XMLWriter() {
        this.resolveEntityRefs = true;
        this.lastElementClosed = false;
        this.preserve = false;
        this.namespaceStack = new NamespaceStack();
        this.escapeText = true;
        this.indentLevel = 0;
        this.buffer = new StringBuffer();
        this.charsAdded = false;
        this.format = XMLWriter.DEFAULT_FORMAT;
        this.writer = new BufferedWriter(new OutputStreamWriter(System.out));
        this.autoFlush = true;
        this.namespaceStack.push(Namespace.NO_NAMESPACE);
    }
    
    public XMLWriter(final OutputStream out) throws UnsupportedEncodingException {
        this.resolveEntityRefs = true;
        this.lastElementClosed = false;
        this.preserve = false;
        this.namespaceStack = new NamespaceStack();
        this.escapeText = true;
        this.indentLevel = 0;
        this.buffer = new StringBuffer();
        this.charsAdded = false;
        this.format = XMLWriter.DEFAULT_FORMAT;
        this.writer = this.createWriter(out, this.format.getEncoding());
        this.autoFlush = true;
        this.namespaceStack.push(Namespace.NO_NAMESPACE);
    }
    
    public XMLWriter(final OutputStream out, final OutputFormat format) throws UnsupportedEncodingException {
        this.resolveEntityRefs = true;
        this.lastElementClosed = false;
        this.preserve = false;
        this.namespaceStack = new NamespaceStack();
        this.escapeText = true;
        this.indentLevel = 0;
        this.buffer = new StringBuffer();
        this.charsAdded = false;
        this.format = format;
        this.writer = this.createWriter(out, format.getEncoding());
        this.autoFlush = true;
        this.namespaceStack.push(Namespace.NO_NAMESPACE);
    }
    
    public XMLWriter(final OutputFormat format) throws UnsupportedEncodingException {
        this.resolveEntityRefs = true;
        this.lastElementClosed = false;
        this.preserve = false;
        this.namespaceStack = new NamespaceStack();
        this.escapeText = true;
        this.indentLevel = 0;
        this.buffer = new StringBuffer();
        this.charsAdded = false;
        this.format = format;
        this.writer = this.createWriter(System.out, format.getEncoding());
        this.autoFlush = true;
        this.namespaceStack.push(Namespace.NO_NAMESPACE);
    }
    
    public void setWriter(final Writer writer) {
        this.writer = writer;
        this.autoFlush = false;
    }
    
    public void setOutputStream(final OutputStream out) throws UnsupportedEncodingException {
        this.writer = this.createWriter(out, this.format.getEncoding());
        this.autoFlush = true;
    }
    
    public boolean isEscapeText() {
        return this.escapeText;
    }
    
    public void setEscapeText(final boolean escapeText) {
        this.escapeText = escapeText;
    }
    
    public void setIndentLevel(final int indentLevel) {
        this.indentLevel = indentLevel;
    }
    
    public int getMaximumAllowedCharacter() {
        if (this.maximumAllowedCharacter == 0) {
            this.maximumAllowedCharacter = this.defaultMaximumAllowedCharacter();
        }
        return this.maximumAllowedCharacter;
    }
    
    public void setMaximumAllowedCharacter(final int maximumAllowedCharacter) {
        this.maximumAllowedCharacter = maximumAllowedCharacter;
    }
    
    public void flush() throws IOException {
        this.writer.flush();
    }
    
    public void close() throws IOException {
        this.writer.close();
    }
    
    public void println() throws IOException {
        this.writer.write(this.format.getLineSeparator());
    }
    
    public void write(final Attribute attribute) throws IOException {
        this.writeAttribute(attribute);
        if (this.autoFlush) {
            this.flush();
        }
    }
    
    public void write(final Document doc) throws IOException {
        this.writeDeclaration();
        if (doc.getDocType() != null) {
            this.indent();
            this.writeDocType(doc.getDocType());
        }
        for (int i = 0, size = doc.nodeCount(); i < size; ++i) {
            final Node node = doc.node(i);
            this.writeNode(node);
        }
        this.writePrintln();
        if (this.autoFlush) {
            this.flush();
        }
    }
    
    public void write(final Element element) throws IOException {
        this.writeElement(element);
        if (this.autoFlush) {
            this.flush();
        }
    }
    
    public void write(final CDATA cdata) throws IOException {
        this.writeCDATA(cdata.getText());
        if (this.autoFlush) {
            this.flush();
        }
    }
    
    public void write(final Comment comment) throws IOException {
        this.writeComment(comment.getText());
        if (this.autoFlush) {
            this.flush();
        }
    }
    
    public void write(final DocumentType docType) throws IOException {
        this.writeDocType(docType);
        if (this.autoFlush) {
            this.flush();
        }
    }
    
    public void write(final Entity entity) throws IOException {
        this.writeEntity(entity);
        if (this.autoFlush) {
            this.flush();
        }
    }
    
    public void write(final Namespace namespace) throws IOException {
        this.writeNamespace(namespace);
        if (this.autoFlush) {
            this.flush();
        }
    }
    
    public void write(final ProcessingInstruction processingInstruction) throws IOException {
        this.writeProcessingInstruction(processingInstruction);
        if (this.autoFlush) {
            this.flush();
        }
    }
    
    public void write(final String text) throws IOException {
        this.writeString(text);
        if (this.autoFlush) {
            this.flush();
        }
    }
    
    public void write(final Text text) throws IOException {
        this.writeString(text.getText());
        if (this.autoFlush) {
            this.flush();
        }
    }
    
    public void write(final Node node) throws IOException {
        this.writeNode(node);
        if (this.autoFlush) {
            this.flush();
        }
    }
    
    public void write(final Object object) throws IOException {
        if (object instanceof Node) {
            this.write((Node)object);
        }
        else if (object instanceof String) {
            this.write((String)object);
        }
        else if (object instanceof List) {
            final List list = (List)object;
            for (int i = 0, size = list.size(); i < size; ++i) {
                this.write(list.get(i));
            }
        }
        else if (object != null) {
            throw new IOException("Invalid object: " + object);
        }
    }
    
    public void writeOpen(final Element element) throws IOException {
        this.writer.write("<");
        this.writer.write(element.getQualifiedName());
        this.writeAttributes(element);
        this.writer.write(">");
    }
    
    public void writeClose(final Element element) throws IOException {
        this.writeClose(element.getQualifiedName());
    }
    
    public void parse(final InputSource source) throws IOException, SAXException {
        this.installLexicalHandler();
        super.parse(source);
    }
    
    public void setProperty(final String name, final Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        for (int i = 0; i < XMLWriter.LEXICAL_HANDLER_NAMES.length; ++i) {
            if (XMLWriter.LEXICAL_HANDLER_NAMES[i].equals(name)) {
                this.setLexicalHandler((LexicalHandler)value);
                return;
            }
        }
        super.setProperty(name, value);
    }
    
    public Object getProperty(final String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        for (int i = 0; i < XMLWriter.LEXICAL_HANDLER_NAMES.length; ++i) {
            if (XMLWriter.LEXICAL_HANDLER_NAMES[i].equals(name)) {
                return this.getLexicalHandler();
            }
        }
        return super.getProperty(name);
    }
    
    public void setLexicalHandler(final LexicalHandler handler) {
        if (handler == null) {
            throw new NullPointerException("Null lexical handler");
        }
        this.lexicalHandler = handler;
    }
    
    public LexicalHandler getLexicalHandler() {
        return this.lexicalHandler;
    }
    
    public void setDocumentLocator(final Locator locator) {
        super.setDocumentLocator(locator);
    }
    
    public void startDocument() throws SAXException {
        try {
            this.writeDeclaration();
            super.startDocument();
        }
        catch (IOException e) {
            this.handleException(e);
        }
    }
    
    public void endDocument() throws SAXException {
        super.endDocument();
        if (this.autoFlush) {
            try {
                this.flush();
            }
            catch (IOException ex) {}
        }
    }
    
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        if (this.namespacesMap == null) {
            this.namespacesMap = new HashMap();
        }
        this.namespacesMap.put(prefix, uri);
        super.startPrefixMapping(prefix, uri);
    }
    
    public void endPrefixMapping(final String prefix) throws SAXException {
        super.endPrefixMapping(prefix);
    }
    
    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes attributes) throws SAXException {
        try {
            this.charsAdded = false;
            this.writePrintln();
            this.indent();
            this.writer.write("<");
            this.writer.write(qName);
            this.writeNamespaces();
            this.writeAttributes(attributes);
            this.writer.write(">");
            ++this.indentLevel;
            this.lastOutputNodeType = 1;
            this.lastElementClosed = false;
            super.startElement(namespaceURI, localName, qName, attributes);
        }
        catch (IOException e) {
            this.handleException(e);
        }
    }
    
    public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {
        try {
            this.charsAdded = false;
            --this.indentLevel;
            if (this.lastElementClosed) {
                this.writePrintln();
                this.indent();
            }
            final boolean hadContent = true;
            if (hadContent) {
                this.writeClose(qName);
            }
            else {
                this.writeEmptyElementClose(qName);
            }
            this.lastOutputNodeType = 1;
            this.lastElementClosed = true;
            super.endElement(namespaceURI, localName, qName);
        }
        catch (IOException e) {
            this.handleException(e);
        }
    }
    
    public void characters(final char[] ch, final int start, final int length) throws SAXException {
        if (ch == null || ch.length == 0 || length <= 0) {
            return;
        }
        try {
            String string = String.valueOf(ch, start, length);
            if (this.escapeText) {
                string = this.escapeElementEntities(string);
            }
            if (this.format.isTrimText()) {
                if (this.lastOutputNodeType == 3 && !this.charsAdded) {
                    this.writer.write(32);
                }
                else if (this.charsAdded && Character.isWhitespace(this.lastChar)) {
                    this.writer.write(32);
                }
                else if (this.lastOutputNodeType == 1 && this.format.isPadText() && this.lastElementClosed && Character.isWhitespace(ch[0])) {
                    this.writer.write(" ");
                }
                String delim = "";
                final StringTokenizer tokens = new StringTokenizer(string);
                while (tokens.hasMoreTokens()) {
                    this.writer.write(delim);
                    this.writer.write(tokens.nextToken());
                    delim = " ";
                }
            }
            else {
                this.writer.write(string);
            }
            this.charsAdded = true;
            this.lastChar = ch[start + length - 1];
            this.lastOutputNodeType = 3;
            super.characters(ch, start, length);
        }
        catch (IOException e) {
            this.handleException(e);
        }
    }
    
    public void ignorableWhitespace(final char[] ch, final int start, final int length) throws SAXException {
        super.ignorableWhitespace(ch, start, length);
    }
    
    public void processingInstruction(final String target, final String data) throws SAXException {
        try {
            this.indent();
            this.writer.write("<?");
            this.writer.write(target);
            this.writer.write(" ");
            this.writer.write(data);
            this.writer.write("?>");
            this.writePrintln();
            this.lastOutputNodeType = 7;
            super.processingInstruction(target, data);
        }
        catch (IOException e) {
            this.handleException(e);
        }
    }
    
    public void notationDecl(final String name, final String publicID, final String systemID) throws SAXException {
        super.notationDecl(name, publicID, systemID);
    }
    
    public void unparsedEntityDecl(final String name, final String publicID, final String systemID, final String notationName) throws SAXException {
        super.unparsedEntityDecl(name, publicID, systemID, notationName);
    }
    
    public void startDTD(final String name, final String publicID, final String systemID) throws SAXException {
        this.inDTD = true;
        try {
            this.writeDocType(name, publicID, systemID);
        }
        catch (IOException e) {
            this.handleException(e);
        }
        if (this.lexicalHandler != null) {
            this.lexicalHandler.startDTD(name, publicID, systemID);
        }
    }
    
    public void endDTD() throws SAXException {
        this.inDTD = false;
        if (this.lexicalHandler != null) {
            this.lexicalHandler.endDTD();
        }
    }
    
    public void startCDATA() throws SAXException {
        try {
            this.writer.write("<![CDATA[");
        }
        catch (IOException e) {
            this.handleException(e);
        }
        if (this.lexicalHandler != null) {
            this.lexicalHandler.startCDATA();
        }
    }
    
    public void endCDATA() throws SAXException {
        try {
            this.writer.write("]]>");
        }
        catch (IOException e) {
            this.handleException(e);
        }
        if (this.lexicalHandler != null) {
            this.lexicalHandler.endCDATA();
        }
    }
    
    public void startEntity(final String name) throws SAXException {
        try {
            this.writeEntityRef(name);
        }
        catch (IOException e) {
            this.handleException(e);
        }
        if (this.lexicalHandler != null) {
            this.lexicalHandler.startEntity(name);
        }
    }
    
    public void endEntity(final String name) throws SAXException {
        if (this.lexicalHandler != null) {
            this.lexicalHandler.endEntity(name);
        }
    }
    
    public void comment(final char[] ch, final int start, final int length) throws SAXException {
        Label_0044: {
            if (!this.showCommentsInDTDs) {
                if (this.inDTD) {
                    break Label_0044;
                }
            }
            try {
                this.charsAdded = false;
                this.writeComment(new String(ch, start, length));
            }
            catch (IOException e) {
                this.handleException(e);
            }
        }
        if (this.lexicalHandler != null) {
            this.lexicalHandler.comment(ch, start, length);
        }
    }
    
    protected void writeElement(final Element element) throws IOException {
        final int size = element.nodeCount();
        final String qualifiedName = element.getQualifiedName();
        this.writePrintln();
        this.indent();
        this.writer.write("<");
        this.writer.write(qualifiedName);
        final int previouslyDeclaredNamespaces = this.namespaceStack.size();
        final Namespace ns = element.getNamespace();
        if (this.isNamespaceDeclaration(ns)) {
            this.namespaceStack.push(ns);
            this.writeNamespace(ns);
        }
        boolean textOnly = true;
        for (int i = 0; i < size; ++i) {
            final Node node = element.node(i);
            if (node instanceof Namespace) {
                final Namespace additional = (Namespace)node;
                if (this.isNamespaceDeclaration(additional)) {
                    this.namespaceStack.push(additional);
                    this.writeNamespace(additional);
                }
            }
            else if (node instanceof Element) {
                textOnly = false;
            }
            else if (node instanceof Comment) {
                textOnly = false;
            }
        }
        this.writeAttributes(element);
        this.lastOutputNodeType = 1;
        if (size <= 0) {
            this.writeEmptyElementClose(qualifiedName);
        }
        else {
            this.writer.write(">");
            if (textOnly) {
                this.writeElementContent(element);
            }
            else {
                ++this.indentLevel;
                this.writeElementContent(element);
                --this.indentLevel;
                this.writePrintln();
                this.indent();
            }
            this.writer.write("</");
            this.writer.write(qualifiedName);
            this.writer.write(">");
        }
        while (this.namespaceStack.size() > previouslyDeclaredNamespaces) {
            this.namespaceStack.pop();
        }
        this.lastOutputNodeType = 1;
    }
    
    protected final boolean isElementSpacePreserved(final Element element) {
        final Attribute attr = element.attribute("space");
        boolean preserveFound = this.preserve;
        if (attr != null) {
            preserveFound = ("xml".equals(attr.getNamespacePrefix()) && "preserve".equals(attr.getText()));
        }
        return preserveFound;
    }
    
    protected void writeElementContent(final Element element) throws IOException {
        boolean trim = this.format.isTrimText();
        final boolean oldPreserve = this.preserve;
        if (trim) {
            this.preserve = this.isElementSpacePreserved(element);
            trim = !this.preserve;
        }
        if (trim) {
            Text lastTextNode = null;
            StringBuffer buff = null;
            boolean textOnly = true;
            for (int i = 0, size = element.nodeCount(); i < size; ++i) {
                final Node node = element.node(i);
                if (node instanceof Text) {
                    if (lastTextNode == null) {
                        lastTextNode = (Text)node;
                    }
                    else {
                        if (buff == null) {
                            buff = new StringBuffer(lastTextNode.getText());
                        }
                        buff.append(((Text)node).getText());
                    }
                }
                else {
                    if (!textOnly && this.format.isPadText()) {
                        char firstChar = 'a';
                        if (buff != null) {
                            firstChar = buff.charAt(0);
                        }
                        else if (lastTextNode != null) {
                            firstChar = lastTextNode.getText().charAt(0);
                        }
                        if (Character.isWhitespace(firstChar)) {
                            this.writer.write(" ");
                        }
                    }
                    if (lastTextNode != null) {
                        if (buff != null) {
                            this.writeString(buff.toString());
                            buff = null;
                        }
                        else {
                            this.writeString(lastTextNode.getText());
                        }
                        if (this.format.isPadText()) {
                            char lastTextChar = 'a';
                            if (buff != null) {
                                lastTextChar = buff.charAt(buff.length() - 1);
                            }
                            else if (lastTextNode != null) {
                                final String txt = lastTextNode.getText();
                                lastTextChar = txt.charAt(txt.length() - 1);
                            }
                            if (Character.isWhitespace(lastTextChar)) {
                                this.writer.write(" ");
                            }
                        }
                        lastTextNode = null;
                    }
                    textOnly = false;
                    this.writeNode(node);
                }
            }
            if (lastTextNode != null) {
                if (!textOnly && this.format.isPadText()) {
                    char firstChar2 = 'a';
                    if (buff != null) {
                        firstChar2 = buff.charAt(0);
                    }
                    else {
                        firstChar2 = lastTextNode.getText().charAt(0);
                    }
                    if (Character.isWhitespace(firstChar2)) {
                        this.writer.write(" ");
                    }
                }
                if (buff != null) {
                    this.writeString(buff.toString());
                    buff = null;
                }
                else {
                    this.writeString(lastTextNode.getText());
                }
                lastTextNode = null;
            }
        }
        else {
            Node lastTextNode2 = null;
            for (int j = 0, size2 = element.nodeCount(); j < size2; ++j) {
                final Node node2 = element.node(j);
                if (node2 instanceof Text) {
                    this.writeNode(node2);
                    lastTextNode2 = node2;
                }
                else {
                    if (lastTextNode2 != null && this.format.isPadText()) {
                        final String txt2 = lastTextNode2.getText();
                        final char lastTextChar2 = txt2.charAt(txt2.length() - 1);
                        if (Character.isWhitespace(lastTextChar2)) {
                            this.writer.write(" ");
                        }
                    }
                    this.writeNode(node2);
                    lastTextNode2 = null;
                }
            }
        }
        this.preserve = oldPreserve;
    }
    
    protected void writeCDATA(final String text) throws IOException {
        this.writer.write("<![CDATA[");
        if (text != null) {
            this.writer.write(text);
        }
        this.writer.write("]]>");
        this.lastOutputNodeType = 4;
    }
    
    protected void writeDocType(final DocumentType docType) throws IOException {
        if (docType != null) {
            docType.write(this.writer);
            this.writePrintln();
        }
    }
    
    protected void writeNamespace(final Namespace namespace) throws IOException {
        if (namespace != null) {
            this.writeNamespace(namespace.getPrefix(), namespace.getURI());
        }
    }
    
    protected void writeNamespaces() throws IOException {
        if (this.namespacesMap != null) {
            for (final Map.Entry entry : this.namespacesMap.entrySet()) {
                final String prefix = entry.getKey();
                final String uri = entry.getValue();
                this.writeNamespace(prefix, uri);
            }
            this.namespacesMap = null;
        }
    }
    
    protected void writeNamespace(final String prefix, final String uri) throws IOException {
        if (prefix != null && prefix.length() > 0) {
            this.writer.write(" xmlns:");
            this.writer.write(prefix);
            this.writer.write("=\"");
        }
        else {
            this.writer.write(" xmlns=\"");
        }
        this.writer.write(uri);
        this.writer.write("\"");
    }
    
    protected void writeProcessingInstruction(final ProcessingInstruction pi) throws IOException {
        this.writer.write("<?");
        this.writer.write(pi.getName());
        this.writer.write(" ");
        this.writer.write(pi.getText());
        this.writer.write("?>");
        this.writePrintln();
        this.lastOutputNodeType = 7;
    }
    
    protected void writeString(String text) throws IOException {
        if (text != null && text.length() > 0) {
            if (this.escapeText) {
                text = this.escapeElementEntities(text);
            }
            if (this.format.isTrimText()) {
                boolean first = true;
                final StringTokenizer tokenizer = new StringTokenizer(text);
                while (tokenizer.hasMoreTokens()) {
                    final String token = tokenizer.nextToken();
                    if (first) {
                        first = false;
                        if (this.lastOutputNodeType == 3) {
                            this.writer.write(" ");
                        }
                    }
                    else {
                        this.writer.write(" ");
                    }
                    this.writer.write(token);
                    this.lastOutputNodeType = 3;
                    this.lastChar = token.charAt(token.length() - 1);
                }
            }
            else {
                this.lastOutputNodeType = 3;
                this.writer.write(text);
                this.lastChar = text.charAt(text.length() - 1);
            }
        }
    }
    
    protected void writeNodeText(final Node node) throws IOException {
        String text = node.getText();
        if (text != null && text.length() > 0) {
            if (this.escapeText) {
                text = this.escapeElementEntities(text);
            }
            this.lastOutputNodeType = 3;
            this.writer.write(text);
            this.lastChar = text.charAt(text.length() - 1);
        }
    }
    
    protected void writeNode(final Node node) throws IOException {
        final int nodeType = node.getNodeType();
        switch (nodeType) {
            case 1: {
                this.writeElement((Element)node);
                break;
            }
            case 2: {
                this.writeAttribute((Attribute)node);
                break;
            }
            case 3: {
                this.writeNodeText(node);
                break;
            }
            case 4: {
                this.writeCDATA(node.getText());
                break;
            }
            case 5: {
                this.writeEntity((Entity)node);
                break;
            }
            case 7: {
                this.writeProcessingInstruction((ProcessingInstruction)node);
                break;
            }
            case 8: {
                this.writeComment(node.getText());
                break;
            }
            case 9: {
                this.write((Document)node);
                break;
            }
            case 10: {
                this.writeDocType((DocumentType)node);
                break;
            }
            case 13: {
                break;
            }
            default: {
                throw new IOException("Invalid node type: " + node);
            }
        }
    }
    
    protected void installLexicalHandler() {
        final XMLReader parent = this.getParent();
        if (parent == null) {
            throw new NullPointerException("No parent for filter");
        }
        for (int i = 0; i < XMLWriter.LEXICAL_HANDLER_NAMES.length; ++i) {
            try {
                parent.setProperty(XMLWriter.LEXICAL_HANDLER_NAMES[i], this);
                break;
            }
            catch (SAXNotRecognizedException ex) {}
            catch (SAXNotSupportedException ex2) {}
        }
    }
    
    protected void writeDocType(final String name, final String publicID, final String systemID) throws IOException {
        boolean hasPublic = false;
        this.writer.write("<!DOCTYPE ");
        this.writer.write(name);
        if (publicID != null && !publicID.equals("")) {
            this.writer.write(" PUBLIC \"");
            this.writer.write(publicID);
            this.writer.write("\"");
            hasPublic = true;
        }
        if (systemID != null && !systemID.equals("")) {
            if (!hasPublic) {
                this.writer.write(" SYSTEM");
            }
            this.writer.write(" \"");
            this.writer.write(systemID);
            this.writer.write("\"");
        }
        this.writer.write(">");
        this.writePrintln();
    }
    
    protected void writeEntity(final Entity entity) throws IOException {
        if (!this.resolveEntityRefs()) {
            this.writeEntityRef(entity.getName());
        }
        else {
            this.writer.write(entity.getText());
        }
    }
    
    protected void writeEntityRef(final String name) throws IOException {
        this.writer.write("&");
        this.writer.write(name);
        this.writer.write(";");
        this.lastOutputNodeType = 5;
    }
    
    protected void writeComment(final String text) throws IOException {
        if (this.format.isNewlines()) {
            this.println();
            this.indent();
        }
        this.writer.write("<!--");
        this.writer.write(text);
        this.writer.write("-->");
        this.lastOutputNodeType = 8;
    }
    
    protected void writeAttributes(final Element element) throws IOException {
        for (int i = 0, size = element.attributeCount(); i < size; ++i) {
            final Attribute attribute = element.attribute(i);
            final Namespace ns = attribute.getNamespace();
            if (ns != null && ns != Namespace.NO_NAMESPACE && ns != Namespace.XML_NAMESPACE) {
                final String prefix = ns.getPrefix();
                final String uri = this.namespaceStack.getURI(prefix);
                if (!ns.getURI().equals(uri)) {
                    this.writeNamespace(ns);
                    this.namespaceStack.push(ns);
                }
            }
            final String attName = attribute.getName();
            if (attName.startsWith("xmlns:")) {
                final String prefix2 = attName.substring(6);
                if (this.namespaceStack.getNamespaceForPrefix(prefix2) == null) {
                    final String uri2 = attribute.getValue();
                    this.namespaceStack.push(prefix2, uri2);
                    this.writeNamespace(prefix2, uri2);
                }
            }
            else if (attName.equals("xmlns")) {
                if (this.namespaceStack.getDefaultNamespace() == null) {
                    final String uri = attribute.getValue();
                    this.namespaceStack.push(null, uri);
                    this.writeNamespace(null, uri);
                }
            }
            else {
                final char quote = this.format.getAttributeQuoteCharacter();
                this.writer.write(" ");
                this.writer.write(attribute.getQualifiedName());
                this.writer.write("=");
                this.writer.write(quote);
                this.writeEscapeAttributeEntities(attribute.getValue());
                this.writer.write(quote);
            }
        }
    }
    
    protected void writeAttribute(final Attribute attribute) throws IOException {
        this.writer.write(" ");
        this.writer.write(attribute.getQualifiedName());
        this.writer.write("=");
        final char quote = this.format.getAttributeQuoteCharacter();
        this.writer.write(quote);
        this.writeEscapeAttributeEntities(attribute.getValue());
        this.writer.write(quote);
        this.lastOutputNodeType = 2;
    }
    
    protected void writeAttributes(final Attributes attributes) throws IOException {
        for (int i = 0, size = attributes.getLength(); i < size; ++i) {
            this.writeAttribute(attributes, i);
        }
    }
    
    protected void writeAttribute(final Attributes attributes, final int index) throws IOException {
        final char quote = this.format.getAttributeQuoteCharacter();
        this.writer.write(" ");
        this.writer.write(attributes.getQName(index));
        this.writer.write("=");
        this.writer.write(quote);
        this.writeEscapeAttributeEntities(attributes.getValue(index));
        this.writer.write(quote);
    }
    
    protected void indent() throws IOException {
        final String indent = this.format.getIndent();
        if (indent != null && indent.length() > 0) {
            for (int i = 0; i < this.indentLevel; ++i) {
                this.writer.write(indent);
            }
        }
    }
    
    protected void writePrintln() throws IOException {
        if (this.format.isNewlines()) {
            final String seperator = this.format.getLineSeparator();
            if (this.lastChar != seperator.charAt(seperator.length() - 1)) {
                this.writer.write(this.format.getLineSeparator());
            }
        }
    }
    
    protected Writer createWriter(final OutputStream outStream, final String encoding) throws UnsupportedEncodingException {
        return new BufferedWriter(new OutputStreamWriter(outStream, encoding));
    }
    
    protected void writeDeclaration() throws IOException {
        final String encoding = this.format.getEncoding();
        if (!this.format.isSuppressDeclaration()) {
            if (encoding.equals("UTF8")) {
                this.writer.write("<?xml version=\"1.0\"");
                if (!this.format.isOmitEncoding()) {
                    this.writer.write(" encoding=\"UTF-8\"");
                }
                this.writer.write("?>");
            }
            else {
                this.writer.write("<?xml version=\"1.0\"");
                if (!this.format.isOmitEncoding()) {
                    this.writer.write(" encoding=\"" + encoding + "\"");
                }
                this.writer.write("?>");
            }
            if (this.format.isNewLineAfterDeclaration()) {
                this.println();
            }
        }
    }
    
    protected void writeClose(final String qualifiedName) throws IOException {
        this.writer.write("</");
        this.writer.write(qualifiedName);
        this.writer.write(">");
    }
    
    protected void writeEmptyElementClose(final String qualifiedName) throws IOException {
        if (!this.format.isExpandEmptyElements()) {
            this.writer.write("/>");
        }
        else {
            this.writer.write("></");
            this.writer.write(qualifiedName);
            this.writer.write(">");
        }
    }
    
    protected boolean isExpandEmptyElements() {
        return this.format.isExpandEmptyElements();
    }
    
    protected String escapeElementEntities(final String text) {
        char[] block = null;
        int last = 0;
        int size;
        int i;
        for (size = text.length(), i = 0; i < size; ++i) {
            String entity = null;
            final char c = text.charAt(i);
            switch (c) {
                case '<': {
                    entity = "&lt;";
                    break;
                }
                case '>': {
                    entity = "&gt;";
                    break;
                }
                case '&': {
                    entity = "&amp;";
                    break;
                }
                case '\t':
                case '\n':
                case '\r': {
                    if (this.preserve) {
                        entity = String.valueOf(c);
                        break;
                    }
                    break;
                }
                default: {
                    if (c < ' ' || this.shouldEncodeChar(c)) {
                        entity = "&#" + (int)c + ";";
                        break;
                    }
                    break;
                }
            }
            if (entity != null) {
                if (block == null) {
                    block = text.toCharArray();
                }
                this.buffer.append(block, last, i - last);
                this.buffer.append(entity);
                last = i + 1;
            }
        }
        if (last == 0) {
            return text;
        }
        if (last < size) {
            if (block == null) {
                block = text.toCharArray();
            }
            this.buffer.append(block, last, i - last);
        }
        final String answer = this.buffer.toString();
        this.buffer.setLength(0);
        return answer;
    }
    
    protected void writeEscapeAttributeEntities(final String txt) throws IOException {
        if (txt != null) {
            final String escapedText = this.escapeAttributeEntities(txt);
            this.writer.write(escapedText);
        }
    }
    
    protected String escapeAttributeEntities(final String text) {
        final char quote = this.format.getAttributeQuoteCharacter();
        char[] block = null;
        int last = 0;
        int size;
        int i;
        for (size = text.length(), i = 0; i < size; ++i) {
            String entity = null;
            final char c = text.charAt(i);
            switch (c) {
                case '<': {
                    entity = "&lt;";
                    break;
                }
                case '>': {
                    entity = "&gt;";
                    break;
                }
                case '\'': {
                    if (quote == '\'') {
                        entity = "&apos;";
                        break;
                    }
                    break;
                }
                case '\"': {
                    if (quote == '\"') {
                        entity = "&quot;";
                        break;
                    }
                    break;
                }
                case '&': {
                    entity = "&amp;";
                    break;
                }
                case '\t':
                case '\n':
                case '\r': {
                    break;
                }
                default: {
                    if (c < ' ' || this.shouldEncodeChar(c)) {
                        entity = "&#" + (int)c + ";";
                        break;
                    }
                    break;
                }
            }
            if (entity != null) {
                if (block == null) {
                    block = text.toCharArray();
                }
                this.buffer.append(block, last, i - last);
                this.buffer.append(entity);
                last = i + 1;
            }
        }
        if (last == 0) {
            return text;
        }
        if (last < size) {
            if (block == null) {
                block = text.toCharArray();
            }
            this.buffer.append(block, last, i - last);
        }
        final String answer = this.buffer.toString();
        this.buffer.setLength(0);
        return answer;
    }
    
    protected boolean shouldEncodeChar(final char c) {
        final int max = this.getMaximumAllowedCharacter();
        return max > 0 && c > max;
    }
    
    protected int defaultMaximumAllowedCharacter() {
        final String encoding = this.format.getEncoding();
        if (encoding != null && encoding.equals("US-ASCII")) {
            return 127;
        }
        return -1;
    }
    
    protected boolean isNamespaceDeclaration(final Namespace ns) {
        if (ns != null && ns != Namespace.XML_NAMESPACE) {
            final String uri = ns.getURI();
            if (uri != null && !this.namespaceStack.contains(ns)) {
                return true;
            }
        }
        return false;
    }
    
    protected void handleException(final IOException e) throws SAXException {
        throw new SAXException(e);
    }
    
    protected OutputFormat getOutputFormat() {
        return this.format;
    }
    
    public boolean resolveEntityRefs() {
        return this.resolveEntityRefs;
    }
    
    public void setResolveEntityRefs(final boolean resolve) {
        this.resolveEntityRefs = resolve;
    }
    
    static {
        LEXICAL_HANDLER_NAMES = new String[] { "http://xml.org/sax/properties/lexical-handler", "http://xml.org/sax/handlers/LexicalHandler" };
        DEFAULT_FORMAT = new OutputFormat();
    }
}
