// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.compile.visitor;

import com.newrelic.agent.util.ClassAnnotationImpl;
import com.newrelic.org.objectweb.asm.AnnotationVisitor;
import com.newrelic.org.objectweb.asm.ClassReader;
import java.util.ArrayList;
import com.newrelic.agent.util.ClassAnnotation;
import java.util.Collection;
import com.newrelic.org.objectweb.asm.ClassVisitor;

public class ClassAnnotationVisitor extends ClassVisitor
{
    private final Collection<ClassAnnotation> annotations;
    private String className;
    private final String annotationDescription;
    
    public ClassAnnotationVisitor(final String annotationDescription) {
        super(327680);
        this.annotations = new ArrayList<ClassAnnotation>();
        this.annotationDescription = annotationDescription;
    }
    
    public Collection<ClassAnnotation> getAnnotations() {
        return this.annotations;
    }
    
    public static Collection<ClassAnnotation> getAnnotations(final ClassReader cr, final String annotationDescription) {
        final ClassAnnotationVisitor visitor = new ClassAnnotationVisitor(annotationDescription);
        cr.accept(visitor, 0);
        return visitor.getAnnotations();
    }
    
    @Override
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        this.className = name;
    }
    
    @Override
    public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
        if (this.annotationDescription.equals(desc)) {
            final ClassAnnotationImpl annotationVisitor = new ClassAnnotationImpl(this.className, desc);
            this.annotations.add(annotationVisitor);
            return annotationVisitor;
        }
        return null;
    }
}
