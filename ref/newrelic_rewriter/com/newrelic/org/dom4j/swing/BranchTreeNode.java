// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.swing;

import com.newrelic.org.dom4j.CharacterData;
import java.util.ArrayList;
import java.util.Enumeration;
import javax.swing.tree.TreeNode;
import com.newrelic.org.dom4j.Node;
import com.newrelic.org.dom4j.Branch;
import java.util.List;

public class BranchTreeNode extends LeafTreeNode
{
    protected List children;
    
    public BranchTreeNode() {
    }
    
    public BranchTreeNode(final Branch xmlNode) {
        super(xmlNode);
    }
    
    public BranchTreeNode(final TreeNode parent, final Branch xmlNode) {
        super(parent, xmlNode);
    }
    
    public Enumeration children() {
        return new Enumeration() {
            private int index = -1;
            
            public boolean hasMoreElements() {
                return this.index + 1 < BranchTreeNode.this.getChildCount();
            }
            
            public Object nextElement() {
                return BranchTreeNode.this.getChildAt(++this.index);
            }
        };
    }
    
    public boolean getAllowsChildren() {
        return true;
    }
    
    public TreeNode getChildAt(final int childIndex) {
        return this.getChildList().get(childIndex);
    }
    
    public int getChildCount() {
        return this.getChildList().size();
    }
    
    public int getIndex(final TreeNode node) {
        return this.getChildList().indexOf(node);
    }
    
    public boolean isLeaf() {
        return this.getXmlBranch().nodeCount() <= 0;
    }
    
    public String toString() {
        return this.xmlNode.getName();
    }
    
    protected List getChildList() {
        if (this.children == null) {
            this.children = this.createChildList();
        }
        return this.children;
    }
    
    protected List createChildList() {
        final Branch branch = this.getXmlBranch();
        final int size = branch.nodeCount();
        final List childList = new ArrayList(size);
        for (int i = 0; i < size; ++i) {
            final Node node = branch.node(i);
            if (node instanceof CharacterData) {
                String text = node.getText();
                if (text == null) {
                    continue;
                }
                text = text.trim();
                if (text.length() <= 0) {
                    continue;
                }
            }
            childList.add(this.createChildTreeNode(node));
        }
        return childList;
    }
    
    protected TreeNode createChildTreeNode(final Node xmlNode) {
        if (xmlNode instanceof Branch) {
            return new BranchTreeNode(this, (Branch)xmlNode);
        }
        return new LeafTreeNode(this, xmlNode);
    }
    
    protected Branch getXmlBranch() {
        return (Branch)this.xmlNode;
    }
}
