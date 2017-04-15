// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.javassist;

class FieldInitLink
{
    FieldInitLink next;
    CtField field;
    CtField.Initializer init;
    
    FieldInitLink(final CtField f, final CtField.Initializer i) {
        this.next = null;
        this.field = f;
        this.init = i;
    }
}
