// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.util;

import org.xml.sax.SAXParseException;
import com.newrelic.org.dom4j.DocumentHelper;
import com.newrelic.org.dom4j.Element;
import com.newrelic.org.dom4j.QName;
import org.xml.sax.ErrorHandler;

public class XMLErrorHandler implements ErrorHandler
{
    protected static final QName ERROR_QNAME;
    protected static final QName FATALERROR_QNAME;
    protected static final QName WARNING_QNAME;
    private Element errors;
    private QName errorQName;
    private QName fatalErrorQName;
    private QName warningQName;
    
    public XMLErrorHandler() {
        this.errorQName = XMLErrorHandler.ERROR_QNAME;
        this.fatalErrorQName = XMLErrorHandler.FATALERROR_QNAME;
        this.warningQName = XMLErrorHandler.WARNING_QNAME;
        this.errors = DocumentHelper.createElement("errors");
    }
    
    public XMLErrorHandler(final Element errors) {
        this.errorQName = XMLErrorHandler.ERROR_QNAME;
        this.fatalErrorQName = XMLErrorHandler.FATALERROR_QNAME;
        this.warningQName = XMLErrorHandler.WARNING_QNAME;
        this.errors = errors;
    }
    
    public void error(final SAXParseException e) {
        final Element element = this.errors.addElement(this.errorQName);
        this.addException(element, e);
    }
    
    public void fatalError(final SAXParseException e) {
        final Element element = this.errors.addElement(this.fatalErrorQName);
        this.addException(element, e);
    }
    
    public void warning(final SAXParseException e) {
        final Element element = this.errors.addElement(this.warningQName);
        this.addException(element, e);
    }
    
    public Element getErrors() {
        return this.errors;
    }
    
    public void setErrors(final Element errors) {
        this.errors = errors;
    }
    
    public QName getErrorQName() {
        return this.errorQName;
    }
    
    public void setErrorQName(final QName errorQName) {
        this.errorQName = errorQName;
    }
    
    public QName getFatalErrorQName() {
        return this.fatalErrorQName;
    }
    
    public void setFatalErrorQName(final QName fatalErrorQName) {
        this.fatalErrorQName = fatalErrorQName;
    }
    
    public QName getWarningQName() {
        return this.warningQName;
    }
    
    public void setWarningQName(final QName warningQName) {
        this.warningQName = warningQName;
    }
    
    protected void addException(final Element element, final SAXParseException e) {
        element.addAttribute("column", Integer.toString(e.getColumnNumber()));
        element.addAttribute("line", Integer.toString(e.getLineNumber()));
        final String publicID = e.getPublicId();
        if (publicID != null && publicID.length() > 0) {
            element.addAttribute("publicID", publicID);
        }
        final String systemID = e.getSystemId();
        if (systemID != null && systemID.length() > 0) {
            element.addAttribute("systemID", systemID);
        }
        element.addText(e.getMessage());
    }
    
    static {
        ERROR_QNAME = QName.get("error");
        FATALERROR_QNAME = QName.get("fatalError");
        WARNING_QNAME = QName.get("warning");
    }
}
