// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.io;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLReaderFactory;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

class SAXHelper
{
    private static boolean loggedWarning;
    
    public static boolean setParserProperty(final XMLReader reader, final String propertyName, final Object value) {
        try {
            reader.setProperty(propertyName, value);
            return true;
        }
        catch (SAXNotSupportedException e) {}
        catch (SAXNotRecognizedException ex) {}
        return false;
    }
    
    public static boolean setParserFeature(final XMLReader reader, final String featureName, final boolean value) {
        try {
            reader.setFeature(featureName, value);
            return true;
        }
        catch (SAXNotSupportedException e) {}
        catch (SAXNotRecognizedException ex) {}
        return false;
    }
    
    public static XMLReader createXMLReader(final boolean validating) throws SAXException {
        XMLReader reader = null;
        if (reader == null) {
            reader = createXMLReaderViaJAXP(validating, true);
        }
        if (reader == null) {
            try {
                reader = XMLReaderFactory.createXMLReader();
            }
            catch (Exception e) {
                if (isVerboseErrorReporting()) {
                    System.out.println("Warning: Caught exception attempting to use SAX to load a SAX XMLReader ");
                    System.out.println("Warning: Exception was: " + e);
                    System.out.println("Warning: I will print the stack trace then carry on using the default SAX parser");
                    e.printStackTrace();
                }
                throw new SAXException(e);
            }
        }
        if (reader == null) {
            throw new SAXException("Couldn't create SAX reader");
        }
        return reader;
    }
    
    protected static XMLReader createXMLReaderViaJAXP(final boolean validating, final boolean namespaceAware) {
        try {
            return JAXPHelper.createXMLReader(validating, namespaceAware);
        }
        catch (Throwable e) {
            if (!SAXHelper.loggedWarning) {
                SAXHelper.loggedWarning = true;
                if (isVerboseErrorReporting()) {
                    System.out.println("Warning: Caught exception attempting to use JAXP to load a SAX XMLReader");
                    System.out.println("Warning: Exception was: " + e);
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
    
    protected static boolean isVerboseErrorReporting() {
        try {
            final String flag = System.getProperty("com.newrelic.org.dom4j.verbose");
            if (flag != null && flag.equalsIgnoreCase("true")) {
                return true;
            }
        }
        catch (Exception ex) {}
        return true;
    }
    
    static {
        SAXHelper.loggedWarning = true;
    }
}
