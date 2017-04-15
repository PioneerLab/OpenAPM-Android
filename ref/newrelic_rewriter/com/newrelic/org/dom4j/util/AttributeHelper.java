// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.util;

import com.newrelic.org.dom4j.Attribute;
import com.newrelic.org.dom4j.QName;
import com.newrelic.org.dom4j.Element;

public class AttributeHelper
{
    public static boolean booleanValue(final Element element, final String attributeName) {
        return booleanValue(element.attribute(attributeName));
    }
    
    public static boolean booleanValue(final Element element, final QName attributeQName) {
        return booleanValue(element.attribute(attributeQName));
    }
    
    protected static boolean booleanValue(final Attribute attribute) {
        if (attribute == null) {
            return false;
        }
        final Object value = attribute.getData();
        if (value == null) {
            return false;
        }
        if (value instanceof Boolean) {
            final Boolean b = (Boolean)value;
            return b;
        }
        return "true".equalsIgnoreCase(value.toString());
    }
}
