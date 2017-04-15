// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.swing;

import com.newrelic.org.dom4j.DocumentHelper;
import java.util.HashMap;
import java.util.Iterator;
import com.newrelic.org.dom4j.Element;
import com.newrelic.org.dom4j.Document;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import com.newrelic.org.dom4j.XPath;
import org.jaxen.VariableContext;
import java.io.Serializable;

public class XMLTableDefinition implements Serializable, VariableContext
{
    private XPath rowXPath;
    private List columns;
    private XMLTableColumnDefinition[] columnArray;
    private Map columnNameIndex;
    private VariableContext variableContext;
    private Object rowValue;
    
    public XMLTableDefinition() {
        this.columns = new ArrayList();
    }
    
    public static XMLTableDefinition load(final Document definition) {
        return load(definition.getRootElement());
    }
    
    public static XMLTableDefinition load(final Element definition) {
        final XMLTableDefinition answer = new XMLTableDefinition();
        answer.setRowExpression(definition.attributeValue("select"));
        final Iterator iter = definition.elementIterator("column");
        while (iter.hasNext()) {
            final Element element = iter.next();
            final String expression = element.attributeValue("select");
            final String name = element.getText();
            final String typeName = element.attributeValue("type", "string");
            final String columnXPath = element.attributeValue("columnNameXPath");
            final int type = XMLTableColumnDefinition.parseType(typeName);
            if (columnXPath != null) {
                answer.addColumnWithXPathName(columnXPath, expression, type);
            }
            else {
                answer.addColumn(name, expression, type);
            }
        }
        return answer;
    }
    
    public Class getColumnClass(final int columnIndex) {
        return this.getColumn(columnIndex).getColumnClass();
    }
    
    public int getColumnCount() {
        return this.columns.size();
    }
    
    public String getColumnName(final int columnIndex) {
        return this.getColumn(columnIndex).getName();
    }
    
    public XPath getColumnXPath(final int columnIndex) {
        return this.getColumn(columnIndex).getXPath();
    }
    
    public XPath getColumnNameXPath(final int columnIndex) {
        return this.getColumn(columnIndex).getColumnNameXPath();
    }
    
    public synchronized Object getValueAt(final Object row, final int columnIndex) {
        final XMLTableColumnDefinition column = this.getColumn(columnIndex);
        Object answer = null;
        synchronized (this) {
            this.rowValue = row;
            answer = column.getValue(row);
            this.rowValue = null;
        }
        return answer;
    }
    
    public void addColumn(final String name, final String expression) {
        this.addColumn(name, expression, 0);
    }
    
    public void addColumn(final String name, final String expression, final int type) {
        final XPath xpath = this.createColumnXPath(expression);
        this.addColumn(new XMLTableColumnDefinition(name, xpath, type));
    }
    
    public void addColumnWithXPathName(final String columnNameXPathExpression, final String expression, final int type) {
        final XPath columnNameXPath = this.createColumnXPath(columnNameXPathExpression);
        final XPath xpath = this.createColumnXPath(expression);
        this.addColumn(new XMLTableColumnDefinition(columnNameXPath, xpath, type));
    }
    
    public void addStringColumn(final String name, final String expression) {
        this.addColumn(name, expression, 1);
    }
    
    public void addNumberColumn(final String name, final String expression) {
        this.addColumn(name, expression, 2);
    }
    
    public void addColumn(final XMLTableColumnDefinition column) {
        this.clearCaches();
        this.columns.add(column);
    }
    
    public void removeColumn(final XMLTableColumnDefinition column) {
        this.clearCaches();
        this.columns.remove(column);
    }
    
    public void clear() {
        this.clearCaches();
        this.columns.clear();
    }
    
    public XMLTableColumnDefinition getColumn(final int index) {
        if (this.columnArray == null) {
            this.columnArray = new XMLTableColumnDefinition[this.columns.size()];
            this.columns.toArray(this.columnArray);
        }
        return this.columnArray[index];
    }
    
    public XMLTableColumnDefinition getColumn(final String columnName) {
        if (this.columnNameIndex == null) {
            this.columnNameIndex = new HashMap();
            for (final XMLTableColumnDefinition column : this.columns) {
                this.columnNameIndex.put(column.getName(), column);
            }
        }
        return this.columnNameIndex.get(columnName);
    }
    
    public XPath getRowXPath() {
        return this.rowXPath;
    }
    
    public void setRowXPath(final XPath rowXPath) {
        this.rowXPath = rowXPath;
    }
    
    public void setRowExpression(final String xpath) {
        this.setRowXPath(this.createXPath(xpath));
    }
    
    public Object getVariableValue(final String namespaceURI, final String prefix, final String localName) {
        final XMLTableColumnDefinition column = this.getColumn(localName);
        if (column != null) {
            return column.getValue(this.rowValue);
        }
        return null;
    }
    
    protected XPath createXPath(final String expression) {
        return DocumentHelper.createXPath(expression);
    }
    
    protected XPath createColumnXPath(final String expression) {
        final XPath xpath = this.createXPath(expression);
        xpath.setVariableContext((VariableContext)this);
        return xpath;
    }
    
    protected void clearCaches() {
        this.columnArray = null;
        this.columnNameIndex = null;
    }
    
    protected void handleException(final Exception e) {
        System.out.println("Caught: " + e);
    }
}
