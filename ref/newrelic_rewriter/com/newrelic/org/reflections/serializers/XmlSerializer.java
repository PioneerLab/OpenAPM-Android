// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.reflections.serializers;

import java.util.Map;
import com.newrelic.com.google.common.collect.Multimap;
import com.newrelic.org.dom4j.DocumentFactory;
import java.io.Writer;
import java.io.StringWriter;
import java.io.IOException;
import com.newrelic.org.reflections.ReflectionsException;
import java.io.OutputStream;
import com.newrelic.org.dom4j.io.XMLWriter;
import com.newrelic.org.dom4j.io.OutputFormat;
import java.io.FileOutputStream;
import com.newrelic.org.reflections.util.Utils;
import java.io.File;
import java.util.Iterator;
import com.newrelic.org.dom4j.Document;
import com.newrelic.org.dom4j.Element;
import com.newrelic.org.dom4j.DocumentException;
import com.newrelic.org.dom4j.io.SAXReader;
import com.newrelic.org.reflections.Configuration;
import com.newrelic.org.reflections.util.ConfigurationBuilder;
import com.newrelic.org.reflections.Reflections;
import java.io.InputStream;

public class XmlSerializer implements Serializer
{
    public Reflections read(final InputStream inputStream) {
        final Reflections reflections = new Reflections(new ConfigurationBuilder());
        Document document;
        try {
            document = new SAXReader().read(inputStream);
        }
        catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        for (final Object e2 : document.getRootElement().elements()) {
            final Element index = (Element)e2;
            for (final Object e3 : index.elements()) {
                final Element entry = (Element)e3;
                final Element key = entry.element("key");
                final Element values = entry.element("values");
                for (final Object o3 : values.elements()) {
                    final Element value = (Element)o3;
                    reflections.getStore().getOrCreate(index.getName()).put(key.getText(), value.getText());
                }
            }
        }
        return reflections;
    }
    
    public File save(final Reflections reflections, final String filename) {
        final File file = Utils.prepareFile(filename);
        final Document document = this.createDocument(reflections);
        try {
            final XMLWriter xmlWriter = new XMLWriter(new FileOutputStream(file), OutputFormat.createPrettyPrint());
            xmlWriter.write(document);
            xmlWriter.close();
        }
        catch (IOException e) {
            throw new ReflectionsException("could not save to file " + filename, e);
        }
        return file;
    }
    
    public String toString(final Reflections reflections) {
        final Document document = this.createDocument(reflections);
        try {
            final StringWriter writer = new StringWriter();
            final XMLWriter xmlWriter = new XMLWriter(writer, OutputFormat.createPrettyPrint());
            xmlWriter.write(document);
            xmlWriter.close();
            return writer.toString();
        }
        catch (IOException e) {
            throw new RuntimeException();
        }
    }
    
    private Document createDocument(final Reflections reflections) {
        final Map<String, Multimap<String, String>> map = reflections.getStore().getStoreMap();
        final Document document = DocumentFactory.getInstance().createDocument();
        final Element root = document.addElement("Reflections");
        for (final String indexName : map.keySet()) {
            final Element indexElement = root.addElement(indexName);
            for (final String key : map.get(indexName).keySet()) {
                final Element entryElement = indexElement.addElement("entry");
                entryElement.addElement("key").setText(key);
                final Element valuesElement = entryElement.addElement("values");
                for (final String value : map.get(indexName).get(key)) {
                    valuesElement.addElement("value").setText(value);
                }
            }
        }
        return document;
    }
}
