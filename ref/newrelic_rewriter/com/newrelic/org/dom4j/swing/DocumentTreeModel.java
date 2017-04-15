// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.swing;

import javax.swing.tree.TreeNode;
import com.newrelic.org.dom4j.Branch;
import com.newrelic.org.dom4j.Document;
import javax.swing.tree.DefaultTreeModel;

public class DocumentTreeModel extends DefaultTreeModel
{
    protected Document document;
    
    public DocumentTreeModel(final Document document) {
        super(new BranchTreeNode(document));
        this.document = document;
    }
    
    public Document getDocument() {
        return this.document;
    }
    
    public void setDocument(final Document document) {
        this.document = document;
        this.setRoot(new BranchTreeNode(document));
    }
}
