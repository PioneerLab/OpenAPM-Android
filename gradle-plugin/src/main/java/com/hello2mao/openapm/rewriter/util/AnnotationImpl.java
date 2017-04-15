package com.hello2mao.openapm.rewriter.util;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class AnnotationImpl extends AnnotationVisitor {
    
    private String name;
    private Map<String, Object> attributes;
    
    public AnnotationImpl(String name) {
        super(Opcodes.ASM5);
        this.name = name;
    }
    
    @Override
    public void visitEnum(String name, String desc, String value) {
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        attributes.put(name, value);
    }
    
    @Override
    public AnnotationVisitor visitArray(String name) {
        return new ArrayVisitor(name);
    }
    
    @Override
    public void visit(String name, Object value) {
        if (attributes == null) {
            attributes = new HashMap<>();
        }
        attributes.put(name, value);
    }
    
    public String getName() {
        return name;
    }
    
    public Map<String, Object> getAttributes() {
        return (attributes == null) ? Collections.emptyMap() : attributes;
    }
    
    private class ArrayVisitor extends AnnotationVisitor {

        private String name;
        private ArrayList<Object> values;
        
        public ArrayVisitor(String name) {
            super(Opcodes.ASM5);
            values = new ArrayList<>();
            this.name = name;
        }
        
        @Override
        public void visit(String name, Object value) {
            values.add(value);
        }
        
        @Override
        public void visitEnd() {
            visit(name, values.toArray(new String[0]));
        }
    }
}
