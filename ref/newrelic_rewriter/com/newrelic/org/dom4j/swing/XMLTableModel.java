// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.swing;

import com.newrelic.org.dom4j.XPath;
import com.newrelic.org.dom4j.Document;
import com.newrelic.org.dom4j.Element;
import java.util.List;
import javax.swing.table.AbstractTableModel;

public class XMLTableModel extends AbstractTableModel
{
    private XMLTableDefinition definition;
    private Object source;
    private List rows;
    
    public XMLTableModel(final Element tableDefinition, final Object source) {
        this(XMLTableDefinition.load(tableDefinition), source);
    }
    
    public XMLTableModel(final Document tableDefinition, final Object source) {
        this(XMLTableDefinition.load(tableDefinition), source);
    }
    
    public XMLTableModel(final XMLTableDefinition definition, final Object source) {
        this.definition = definition;
        this.source = source;
    }
    
    public Object getRowValue(final int rowIndex) {
        return this.getRows().get(rowIndex);
    }
    
    public List getRows() {
        if (this.rows == null) {
            this.rows = this.definition.getRowXPath().selectNodes(this.source);
        }
        return this.rows;
    }
    
    public Class getColumnClass(final int columnIndex) {
        return this.definition.getColumnClass(columnIndex);
    }
    
    public int getColumnCount() {
        return this.definition.getColumnCount();
    }
    
    public String getColumnName(final int columnIndex) {
        final XPath xpath = this.definition.getColumnNameXPath(columnIndex);
        if (xpath != null) {
            System.out.println("Evaluating column xpath: " + xpath + " value: " + xpath.valueOf(this.source));
            return xpath.valueOf(this.source);
        }
        return this.definition.getColumnName(columnIndex);
    }
    
    public Object getValueAt(final int rowIndex, final int columnIndex) {
        try {
            final Object row = this.getRowValue(rowIndex);
            return this.definition.getValueAt(row, columnIndex);
        }
        catch (Exception e) {
            this.handleException(e);
            return null;
        }
    }
    
    public int getRowCount() {
        return this.getRows().size();
    }
    
    public XMLTableDefinition getDefinition() {
        return this.definition;
    }
    
    public void setDefinition(final XMLTableDefinition definition) {
        this.definition = definition;
    }
    
    public Object getSource() {
        return this.source;
    }
    
    public void setSource(final Object source) {
        this.source = source;
        this.rows = null;
    }
    
    protected void handleException(final Exception e) {
        System.out.println("Caught: " + e);
    }
}
