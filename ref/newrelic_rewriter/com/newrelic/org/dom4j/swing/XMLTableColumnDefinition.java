// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.swing;

import com.newrelic.org.dom4j.DocumentHelper;
import com.newrelic.org.dom4j.XPath;
import java.io.Serializable;

public class XMLTableColumnDefinition implements Serializable
{
    public static final int OBJECT_TYPE = 0;
    public static final int STRING_TYPE = 1;
    public static final int NUMBER_TYPE = 2;
    public static final int NODE_TYPE = 3;
    private int type;
    private String name;
    private XPath xpath;
    private XPath columnNameXPath;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$java$lang$Number;
    static /* synthetic */ Class class$org$dom4j$Node;
    static /* synthetic */ Class class$java$lang$Object;
    
    public XMLTableColumnDefinition() {
    }
    
    public XMLTableColumnDefinition(final String name, final String expression, final int type) {
        this.name = name;
        this.type = type;
        this.xpath = this.createXPath(expression);
    }
    
    public XMLTableColumnDefinition(final String name, final XPath xpath, final int type) {
        this.name = name;
        this.xpath = xpath;
        this.type = type;
    }
    
    public XMLTableColumnDefinition(final XPath columnXPath, final XPath xpath, final int type) {
        this.xpath = xpath;
        this.columnNameXPath = columnXPath;
        this.type = type;
    }
    
    public static int parseType(final String typeName) {
        if (typeName != null && typeName.length() > 0) {
            if (typeName.equals("string")) {
                return 1;
            }
            if (typeName.equals("number")) {
                return 2;
            }
            if (typeName.equals("node")) {
                return 3;
            }
        }
        return 0;
    }
    
    public Class getColumnClass() {
        switch (this.type) {
            case 1: {
                return (XMLTableColumnDefinition.class$java$lang$String == null) ? (XMLTableColumnDefinition.class$java$lang$String = class$("java.lang.String")) : XMLTableColumnDefinition.class$java$lang$String;
            }
            case 2: {
                return (XMLTableColumnDefinition.class$java$lang$Number == null) ? (XMLTableColumnDefinition.class$java$lang$Number = class$("java.lang.Number")) : XMLTableColumnDefinition.class$java$lang$Number;
            }
            case 3: {
                return (XMLTableColumnDefinition.class$org$dom4j$Node == null) ? (XMLTableColumnDefinition.class$org$dom4j$Node = class$("com.newrelic.org.dom4j.Node")) : XMLTableColumnDefinition.class$org$dom4j$Node;
            }
            default: {
                return (XMLTableColumnDefinition.class$java$lang$Object == null) ? (XMLTableColumnDefinition.class$java$lang$Object = class$("java.lang.Object")) : XMLTableColumnDefinition.class$java$lang$Object;
            }
        }
    }
    
    public Object getValue(final Object row) {
        switch (this.type) {
            case 1: {
                return this.xpath.valueOf(row);
            }
            case 2: {
                return this.xpath.numberValueOf(row);
            }
            case 3: {
                return this.xpath.selectSingleNode(row);
            }
            default: {
                return this.xpath.evaluate(row);
            }
        }
    }
    
    public int getType() {
        return this.type;
    }
    
    public void setType(final int type) {
        this.type = type;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public XPath getXPath() {
        return this.xpath;
    }
    
    public void setXPath(final XPath xPath) {
        this.xpath = xPath;
    }
    
    public XPath getColumnNameXPath() {
        return this.columnNameXPath;
    }
    
    public void setColumnNameXPath(final XPath columnNameXPath) {
        this.columnNameXPath = columnNameXPath;
    }
    
    protected XPath createXPath(final String expression) {
        return DocumentHelper.createXPath(expression);
    }
    
    protected void handleException(final Exception e) {
        System.out.println("Caught: " + e);
    }
    
    static /* synthetic */ Class class$(final String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x) {
            throw new NoClassDefFoundError(x.getMessage());
        }
    }
}
