// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.objectweb.asm.commons;

import com.newrelic.org.objectweb.asm.TypePath;
import com.newrelic.org.objectweb.asm.AnnotationVisitor;
import com.newrelic.org.objectweb.asm.FieldVisitor;

public class FieldRemapper extends FieldVisitor
{
    private final Remapper remapper;
    
    public FieldRemapper(final FieldVisitor fieldVisitor, final Remapper remapper) {
        this(327680, fieldVisitor, remapper);
    }
    
    protected FieldRemapper(final int n, final FieldVisitor fieldVisitor, final Remapper remapper) {
        super(n, fieldVisitor);
        this.remapper = remapper;
    }
    
    public AnnotationVisitor visitAnnotation(final String s, final boolean b) {
        final AnnotationVisitor visitAnnotation = this.fv.visitAnnotation(this.remapper.mapDesc(s), b);
        return (visitAnnotation == null) ? null : new AnnotationRemapper(visitAnnotation, this.remapper);
    }
    
    public AnnotationVisitor visitTypeAnnotation(final int n, final TypePath typePath, final String s, final boolean b) {
        final AnnotationVisitor visitTypeAnnotation = super.visitTypeAnnotation(n, typePath, this.remapper.mapDesc(s), b);
        return (visitTypeAnnotation == null) ? null : new AnnotationRemapper(visitTypeAnnotation, this.remapper);
    }
}
