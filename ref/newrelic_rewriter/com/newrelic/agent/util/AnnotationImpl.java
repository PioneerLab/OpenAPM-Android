// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import com.newrelic.org.objectweb.asm.AnnotationVisitor;

public class AnnotationImpl extends AnnotationVisitor
{
    private final String name;
    private Map<String, Object> attributes;
    
    public AnnotationImpl(final String name) {
        super(327680);
        this.name = name;
    }
    
    @Override
    public void visitEnum(final String name, final String desc, final String value) {
        if (this.attributes == null) {
            this.attributes = new HashMap<String, Object>();
        }
        this.attributes.put(name, value);
    }
    
    @Override
    public void visitEnd() {
    }
    
    @Override
    public AnnotationVisitor visitArray(final String name) {
        return new ArrayVisitor(name);
    }
    
    @Override
    public AnnotationVisitor visitAnnotation(final String name, final String desc) {
        return null;
    }
    
    @Override
    public void visit(final String name, final Object value) {
        if (this.attributes == null) {
            this.attributes = new HashMap<String, Object>();
        }
        this.attributes.put(name, value);
    }
    
    public String getName() {
        return this.name;
    }
    
    public Map<String, Object> getAttributes() {
        return (this.attributes == null) ? Collections.emptyMap() : this.attributes;
    }
    
    private final class ArrayVisitor extends AnnotationVisitor
    {
        private final String name;
        private final ArrayList<Object> values;
        
        public ArrayVisitor(final String name) {
            super(327680);
            this.values = new ArrayList<Object>();
            this.name = name;
        }
        
        @Override
        public void visit(final String name, final Object value) {
            this.values.add(value);
        }
        
        @Override
        public AnnotationVisitor visitAnnotation(final String arg0, final String arg1) {
            return null;
        }
        
        @Override
        public AnnotationVisitor visitArray(final String name) {
            return null;
        }
        
        @Override
        public void visitEnd() {
            AnnotationImpl.this.visit(this.name, this.values.toArray(new String[0]));
        }
        
        @Override
        public void visitEnum(final String arg0, final String arg1, final String arg2) {
        }
    }
}
