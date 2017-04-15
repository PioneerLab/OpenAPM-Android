// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.util;

import com.newrelic.org.dom4j.QName;
import com.newrelic.org.dom4j.Branch;
import com.newrelic.org.dom4j.Namespace;
import com.newrelic.org.dom4j.DocumentType;
import com.newrelic.org.dom4j.Comment;
import com.newrelic.org.dom4j.ProcessingInstruction;
import com.newrelic.org.dom4j.Entity;
import com.newrelic.org.dom4j.CDATA;
import com.newrelic.org.dom4j.CharacterData;
import com.newrelic.org.dom4j.Text;
import com.newrelic.org.dom4j.Attribute;
import com.newrelic.org.dom4j.Document;
import com.newrelic.org.dom4j.Element;
import com.newrelic.org.dom4j.Node;
import java.util.Comparator;

public class NodeComparator implements Comparator
{
    public int compare(final Object o1, final Object o2) {
        if (o1 == o2) {
            return 0;
        }
        if (o1 == null) {
            return -1;
        }
        if (o2 == null) {
            return 1;
        }
        if (o1 instanceof Node) {
            if (o2 instanceof Node) {
                return this.compare((Node)o1, (Node)o2);
            }
            return 1;
        }
        else {
            if (o2 instanceof Node) {
                return -1;
            }
            if (o1 instanceof Comparable) {
                final Comparable c1 = (Comparable)o1;
                return c1.compareTo(o2);
            }
            final String name1 = o1.getClass().getName();
            final String name2 = o2.getClass().getName();
            return name1.compareTo(name2);
        }
    }
    
    public int compare(final Node n1, final Node n2) {
        final int nodeType1 = n1.getNodeType();
        final int nodeType2 = n2.getNodeType();
        final int answer = nodeType1 - nodeType2;
        if (answer != 0) {
            return answer;
        }
        switch (nodeType1) {
            case 1: {
                return this.compare((Element)n1, (Element)n2);
            }
            case 9: {
                return this.compare((Document)n1, (Document)n2);
            }
            case 2: {
                return this.compare((Attribute)n1, (Attribute)n2);
            }
            case 3: {
                return this.compare((CharacterData)n1, (CharacterData)n2);
            }
            case 4: {
                return this.compare((CharacterData)n1, (CharacterData)n2);
            }
            case 5: {
                return this.compare((Entity)n1, (Entity)n2);
            }
            case 7: {
                return this.compare((ProcessingInstruction)n1, (ProcessingInstruction)n2);
            }
            case 8: {
                return this.compare((CharacterData)n1, (CharacterData)n2);
            }
            case 10: {
                return this.compare((DocumentType)n1, (DocumentType)n2);
            }
            case 13: {
                return this.compare((Namespace)n1, (Namespace)n2);
            }
            default: {
                throw new RuntimeException("Invalid node types. node1: " + n1 + " and node2: " + n2);
            }
        }
    }
    
    public int compare(final Document n1, final Document n2) {
        int answer = this.compare(n1.getDocType(), n2.getDocType());
        if (answer == 0) {
            answer = this.compareContent(n1, n2);
        }
        return answer;
    }
    
    public int compare(final Element n1, final Element n2) {
        int answer = this.compare(n1.getQName(), n2.getQName());
        if (answer == 0) {
            final int c1 = n1.attributeCount();
            final int c2 = n2.attributeCount();
            answer = c1 - c2;
            if (answer == 0) {
                for (int i = 0; i < c1; ++i) {
                    final Attribute a1 = n1.attribute(i);
                    final Attribute a2 = n2.attribute(a1.getQName());
                    answer = this.compare(a1, a2);
                    if (answer != 0) {
                        return answer;
                    }
                }
                answer = this.compareContent(n1, n2);
            }
        }
        return answer;
    }
    
    public int compare(final Attribute n1, final Attribute n2) {
        int answer = this.compare(n1.getQName(), n2.getQName());
        if (answer == 0) {
            answer = this.compare(n1.getValue(), n2.getValue());
        }
        return answer;
    }
    
    public int compare(final QName n1, final QName n2) {
        int answer = this.compare(n1.getNamespaceURI(), n2.getNamespaceURI());
        if (answer == 0) {
            answer = this.compare(n1.getQualifiedName(), n2.getQualifiedName());
        }
        return answer;
    }
    
    public int compare(final Namespace n1, final Namespace n2) {
        int answer = this.compare(n1.getURI(), n2.getURI());
        if (answer == 0) {
            answer = this.compare(n1.getPrefix(), n2.getPrefix());
        }
        return answer;
    }
    
    public int compare(final CharacterData t1, final CharacterData t2) {
        return this.compare(t1.getText(), t2.getText());
    }
    
    public int compare(final DocumentType o1, final DocumentType o2) {
        if (o1 == o2) {
            return 0;
        }
        if (o1 == null) {
            return -1;
        }
        if (o2 == null) {
            return 1;
        }
        int answer = this.compare(o1.getPublicID(), o2.getPublicID());
        if (answer == 0) {
            answer = this.compare(o1.getSystemID(), o2.getSystemID());
            if (answer == 0) {
                answer = this.compare(o1.getName(), o2.getName());
            }
        }
        return answer;
    }
    
    public int compare(final Entity n1, final Entity n2) {
        int answer = this.compare(n1.getName(), n2.getName());
        if (answer == 0) {
            answer = this.compare(n1.getText(), n2.getText());
        }
        return answer;
    }
    
    public int compare(final ProcessingInstruction n1, final ProcessingInstruction n2) {
        int answer = this.compare(n1.getTarget(), n2.getTarget());
        if (answer == 0) {
            answer = this.compare(n1.getText(), n2.getText());
        }
        return answer;
    }
    
    public int compareContent(final Branch b1, final Branch b2) {
        final int c1 = b1.nodeCount();
        final int c2 = b2.nodeCount();
        int answer = c1 - c2;
        if (answer == 0) {
            for (int i = 0; i < c1; ++i) {
                final Node n1 = b1.node(i);
                final Node n2 = b2.node(i);
                answer = this.compare(n1, n2);
                if (answer != 0) {
                    break;
                }
            }
        }
        return answer;
    }
    
    public int compare(final String o1, final String o2) {
        if (o1 == o2) {
            return 0;
        }
        if (o1 == null) {
            return -1;
        }
        if (o2 == null) {
            return 1;
        }
        return o1.compareTo(o2);
    }
}
