// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.io;

import java.io.ObjectInput;
import java.io.IOException;
import java.io.ObjectOutput;
import com.newrelic.org.dom4j.QName;
import com.newrelic.org.dom4j.Namespace;
import java.util.Iterator;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.ContentHandler;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.io.Externalizable;
import org.xml.sax.DTDHandler;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class SAXEventRecorder extends DefaultHandler implements LexicalHandler, DeclHandler, DTDHandler, Externalizable
{
    public static final long serialVersionUID = 1L;
    private static final byte STRING = 0;
    private static final byte OBJECT = 1;
    private static final byte NULL = 2;
    private List events;
    private Map prefixMappings;
    private static final String XMLNS = "xmlns";
    private static final String EMPTY_STRING = "";
    
    public SAXEventRecorder() {
        this.events = new ArrayList();
        this.prefixMappings = new HashMap();
    }
    
    public void replay(final ContentHandler handler) throws SAXException {
        for (final SAXEvent saxEvent : this.events) {
            switch (saxEvent.event) {
                case 1: {
                    handler.processingInstruction((String)saxEvent.getParm(0), (String)saxEvent.getParm(1));
                    continue;
                }
                case 2: {
                    handler.startPrefixMapping((String)saxEvent.getParm(0), (String)saxEvent.getParm(1));
                    continue;
                }
                case 3: {
                    handler.endPrefixMapping((String)saxEvent.getParm(0));
                    continue;
                }
                case 4: {
                    handler.startDocument();
                    continue;
                }
                case 5: {
                    handler.endDocument();
                    continue;
                }
                case 6: {
                    final AttributesImpl attributes = new AttributesImpl();
                    final List attParmList = (List)saxEvent.getParm(3);
                    if (attParmList != null) {
                        for (final String[] attParms : attParmList) {
                            attributes.addAttribute(attParms[0], attParms[1], attParms[2], attParms[3], attParms[4]);
                        }
                    }
                    handler.startElement((String)saxEvent.getParm(0), (String)saxEvent.getParm(1), (String)saxEvent.getParm(2), attributes);
                    continue;
                }
                case 7: {
                    handler.endElement((String)saxEvent.getParm(0), (String)saxEvent.getParm(1), (String)saxEvent.getParm(2));
                    continue;
                }
                case 8: {
                    final char[] chars = (char[])saxEvent.getParm(0);
                    final int start = (int)saxEvent.getParm(1);
                    final int end = (int)saxEvent.getParm(2);
                    handler.characters(chars, start, end);
                    continue;
                }
                case 9: {
                    ((LexicalHandler)handler).startDTD((String)saxEvent.getParm(0), (String)saxEvent.getParm(1), (String)saxEvent.getParm(2));
                    continue;
                }
                case 10: {
                    ((LexicalHandler)handler).endDTD();
                    continue;
                }
                case 11: {
                    ((LexicalHandler)handler).startEntity((String)saxEvent.getParm(0));
                    continue;
                }
                case 12: {
                    ((LexicalHandler)handler).endEntity((String)saxEvent.getParm(0));
                    continue;
                }
                case 13: {
                    ((LexicalHandler)handler).startCDATA();
                    continue;
                }
                case 14: {
                    ((LexicalHandler)handler).endCDATA();
                    continue;
                }
                case 15: {
                    final char[] cchars = (char[])saxEvent.getParm(0);
                    final int cstart = (int)saxEvent.getParm(1);
                    final int cend = (int)saxEvent.getParm(2);
                    ((LexicalHandler)handler).comment(cchars, cstart, cend);
                    continue;
                }
                case 16: {
                    ((DeclHandler)handler).elementDecl((String)saxEvent.getParm(0), (String)saxEvent.getParm(1));
                    continue;
                }
                case 17: {
                    ((DeclHandler)handler).attributeDecl((String)saxEvent.getParm(0), (String)saxEvent.getParm(1), (String)saxEvent.getParm(2), (String)saxEvent.getParm(3), (String)saxEvent.getParm(4));
                    continue;
                }
                case 18: {
                    ((DeclHandler)handler).internalEntityDecl((String)saxEvent.getParm(0), (String)saxEvent.getParm(1));
                    continue;
                }
                case 19: {
                    ((DeclHandler)handler).externalEntityDecl((String)saxEvent.getParm(0), (String)saxEvent.getParm(1), (String)saxEvent.getParm(2));
                    continue;
                }
                default: {
                    throw new SAXException("Unrecognized event: " + saxEvent.event);
                }
            }
        }
    }
    
    public void processingInstruction(final String target, final String data) throws SAXException {
        final SAXEvent saxEvent = new SAXEvent((byte)1);
        saxEvent.addParm(target);
        saxEvent.addParm(data);
        this.events.add(saxEvent);
    }
    
    public void startPrefixMapping(final String prefix, final String uri) throws SAXException {
        final SAXEvent saxEvent = new SAXEvent((byte)2);
        saxEvent.addParm(prefix);
        saxEvent.addParm(uri);
        this.events.add(saxEvent);
    }
    
    public void endPrefixMapping(final String prefix) throws SAXException {
        final SAXEvent saxEvent = new SAXEvent((byte)3);
        saxEvent.addParm(prefix);
        this.events.add(saxEvent);
    }
    
    public void startDocument() throws SAXException {
        final SAXEvent saxEvent = new SAXEvent((byte)4);
        this.events.add(saxEvent);
    }
    
    public void endDocument() throws SAXException {
        final SAXEvent saxEvent = new SAXEvent((byte)5);
        this.events.add(saxEvent);
    }
    
    public void startElement(final String namespaceURI, final String localName, final String qualifiedName, final Attributes attributes) throws SAXException {
        final SAXEvent saxEvent = new SAXEvent((byte)6);
        saxEvent.addParm(namespaceURI);
        saxEvent.addParm(localName);
        saxEvent.addParm(qualifiedName);
        QName qName = null;
        if (namespaceURI != null) {
            qName = new QName(localName, Namespace.get(namespaceURI));
        }
        else {
            qName = new QName(localName);
        }
        if (attributes != null && attributes.getLength() > 0) {
            final List attParmList = new ArrayList(attributes.getLength());
            String[] attParms = null;
            for (int i = 0; i < attributes.getLength(); ++i) {
                final String attLocalName = attributes.getLocalName(i);
                if (attLocalName.startsWith("xmlns")) {
                    String prefix = null;
                    if (attLocalName.length() > 5) {
                        prefix = attLocalName.substring(6);
                    }
                    else {
                        prefix = "";
                    }
                    final SAXEvent prefixEvent = new SAXEvent((byte)2);
                    prefixEvent.addParm(prefix);
                    prefixEvent.addParm(attributes.getValue(i));
                    this.events.add(prefixEvent);
                    List prefixes = this.prefixMappings.get(qName);
                    if (prefixes == null) {
                        prefixes = new ArrayList();
                        this.prefixMappings.put(qName, prefixes);
                    }
                    prefixes.add(prefix);
                }
                else {
                    attParms = new String[] { attributes.getURI(i), attLocalName, attributes.getQName(i), attributes.getType(i), attributes.getValue(i) };
                    attParmList.add(attParms);
                }
            }
            saxEvent.addParm(attParmList);
        }
        this.events.add(saxEvent);
    }
    
    public void endElement(final String namespaceURI, final String localName, final String qName) throws SAXException {
        final SAXEvent saxEvent = new SAXEvent((byte)7);
        saxEvent.addParm(namespaceURI);
        saxEvent.addParm(localName);
        saxEvent.addParm(qName);
        this.events.add(saxEvent);
        QName elementName = null;
        if (namespaceURI != null) {
            elementName = new QName(localName, Namespace.get(namespaceURI));
        }
        else {
            elementName = new QName(localName);
        }
        final List prefixes = this.prefixMappings.get(elementName);
        if (prefixes != null) {
            final Iterator itr = prefixes.iterator();
            while (itr.hasNext()) {
                final SAXEvent prefixEvent = new SAXEvent((byte)3);
                prefixEvent.addParm(itr.next());
                this.events.add(prefixEvent);
            }
        }
    }
    
    public void characters(final char[] ch, final int start, final int end) throws SAXException {
        final SAXEvent saxEvent = new SAXEvent((byte)8);
        saxEvent.addParm(ch);
        saxEvent.addParm(new Integer(start));
        saxEvent.addParm(new Integer(end));
        this.events.add(saxEvent);
    }
    
    public void startDTD(final String name, final String publicId, final String systemId) throws SAXException {
        final SAXEvent saxEvent = new SAXEvent((byte)9);
        saxEvent.addParm(name);
        saxEvent.addParm(publicId);
        saxEvent.addParm(systemId);
        this.events.add(saxEvent);
    }
    
    public void endDTD() throws SAXException {
        final SAXEvent saxEvent = new SAXEvent((byte)10);
        this.events.add(saxEvent);
    }
    
    public void startEntity(final String name) throws SAXException {
        final SAXEvent saxEvent = new SAXEvent((byte)11);
        saxEvent.addParm(name);
        this.events.add(saxEvent);
    }
    
    public void endEntity(final String name) throws SAXException {
        final SAXEvent saxEvent = new SAXEvent((byte)12);
        saxEvent.addParm(name);
        this.events.add(saxEvent);
    }
    
    public void startCDATA() throws SAXException {
        final SAXEvent saxEvent = new SAXEvent((byte)13);
        this.events.add(saxEvent);
    }
    
    public void endCDATA() throws SAXException {
        final SAXEvent saxEvent = new SAXEvent((byte)14);
        this.events.add(saxEvent);
    }
    
    public void comment(final char[] ch, final int start, final int end) throws SAXException {
        final SAXEvent saxEvent = new SAXEvent((byte)15);
        saxEvent.addParm(ch);
        saxEvent.addParm(new Integer(start));
        saxEvent.addParm(new Integer(end));
        this.events.add(saxEvent);
    }
    
    public void elementDecl(final String name, final String model) throws SAXException {
        final SAXEvent saxEvent = new SAXEvent((byte)16);
        saxEvent.addParm(name);
        saxEvent.addParm(model);
        this.events.add(saxEvent);
    }
    
    public void attributeDecl(final String eName, final String aName, final String type, final String valueDefault, final String value) throws SAXException {
        final SAXEvent saxEvent = new SAXEvent((byte)17);
        saxEvent.addParm(eName);
        saxEvent.addParm(aName);
        saxEvent.addParm(type);
        saxEvent.addParm(valueDefault);
        saxEvent.addParm(value);
        this.events.add(saxEvent);
    }
    
    public void internalEntityDecl(final String name, final String value) throws SAXException {
        final SAXEvent saxEvent = new SAXEvent((byte)18);
        saxEvent.addParm(name);
        saxEvent.addParm(value);
        this.events.add(saxEvent);
    }
    
    public void externalEntityDecl(final String name, final String publicId, final String sysId) throws SAXException {
        final SAXEvent saxEvent = new SAXEvent((byte)19);
        saxEvent.addParm(name);
        saxEvent.addParm(publicId);
        saxEvent.addParm(sysId);
        this.events.add(saxEvent);
    }
    
    public void writeExternal(final ObjectOutput out) throws IOException {
        if (this.events == null) {
            out.writeByte(2);
        }
        else {
            out.writeByte(1);
            out.writeObject(this.events);
        }
    }
    
    public void readExternal(final ObjectInput in) throws ClassNotFoundException, IOException {
        if (in.readByte() != 2) {
            this.events = (List)in.readObject();
        }
    }
    
    static class SAXEvent implements Externalizable
    {
        public static final long serialVersionUID = 1L;
        static final byte PROCESSING_INSTRUCTION = 1;
        static final byte START_PREFIX_MAPPING = 2;
        static final byte END_PREFIX_MAPPING = 3;
        static final byte START_DOCUMENT = 4;
        static final byte END_DOCUMENT = 5;
        static final byte START_ELEMENT = 6;
        static final byte END_ELEMENT = 7;
        static final byte CHARACTERS = 8;
        static final byte START_DTD = 9;
        static final byte END_DTD = 10;
        static final byte START_ENTITY = 11;
        static final byte END_ENTITY = 12;
        static final byte START_CDATA = 13;
        static final byte END_CDATA = 14;
        static final byte COMMENT = 15;
        static final byte ELEMENT_DECL = 16;
        static final byte ATTRIBUTE_DECL = 17;
        static final byte INTERNAL_ENTITY_DECL = 18;
        static final byte EXTERNAL_ENTITY_DECL = 19;
        protected byte event;
        protected List parms;
        
        public SAXEvent() {
        }
        
        SAXEvent(final byte event) {
            this.event = event;
        }
        
        void addParm(final Object parm) {
            if (this.parms == null) {
                this.parms = new ArrayList(3);
            }
            this.parms.add(parm);
        }
        
        Object getParm(final int index) {
            if (this.parms != null && index < this.parms.size()) {
                return this.parms.get(index);
            }
            return null;
        }
        
        public void writeExternal(final ObjectOutput out) throws IOException {
            out.writeByte(this.event);
            if (this.parms == null) {
                out.writeByte(2);
            }
            else {
                out.writeByte(1);
                out.writeObject(this.parms);
            }
        }
        
        public void readExternal(final ObjectInput in) throws ClassNotFoundException, IOException {
            this.event = in.readByte();
            if (in.readByte() != 2) {
                this.parms = (List)in.readObject();
            }
        }
    }
}
