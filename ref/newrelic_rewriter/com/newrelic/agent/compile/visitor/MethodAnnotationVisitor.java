// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.compile.visitor;

import com.newrelic.agent.util.AnnotationImpl;
import com.newrelic.org.objectweb.asm.AnnotationVisitor;
import com.newrelic.org.objectweb.asm.MethodVisitor;
import java.util.ArrayList;
import com.newrelic.org.objectweb.asm.ClassVisitor;
import com.newrelic.agent.util.MethodAnnotation;
import java.util.Collection;
import com.newrelic.org.objectweb.asm.ClassReader;

public class MethodAnnotationVisitor
{
    public static Collection<MethodAnnotation> getAnnotations(final ClassReader cr, final String annotationDescription) {
        final MethodAnnotationClassVisitor visitor = new MethodAnnotationClassVisitor(annotationDescription);
        cr.accept(visitor, 0);
        return visitor.getAnnotations();
    }
    
    private static class MethodAnnotationClassVisitor extends ClassVisitor
    {
        String className;
        private final String annotationDescription;
        private final Collection<MethodAnnotation> annotations;
        
        public MethodAnnotationClassVisitor(final String annotationDescription) {
            super(327680);
            this.annotations = new ArrayList<MethodAnnotation>();
            this.annotationDescription = annotationDescription;
        }
        
        public Collection<MethodAnnotation> getAnnotations() {
            return this.annotations;
        }
        
        @Override
        public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
            this.className = name;
        }
        
        @Override
        public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
            return new MethodAnnotationVisitorImpl(name, desc);
        }
        
        private class MethodAnnotationVisitorImpl extends MethodVisitor
        {
            private final String methodName;
            private final String methodDesc;
            
            public MethodAnnotationVisitorImpl(final String name, final String desc) {
                super(327680);
                this.methodName = name;
                this.methodDesc = desc;
            }
            
            @Override
            public AnnotationVisitor visitAnnotation(final String desc, final boolean visible) {
                if (MethodAnnotationClassVisitor.this.annotationDescription.equals(desc)) {
                    final MethodAnnotationImpl annotation = new MethodAnnotationImpl(desc);
                    MethodAnnotationClassVisitor.this.annotations.add(annotation);
                    return annotation;
                }
                return null;
            }
            
            private class MethodAnnotationImpl extends AnnotationImpl implements MethodAnnotation
            {
                public MethodAnnotationImpl(final String desc) {
                    super(desc);
                }
                
                @Override
                public String getMethodName() {
                    return MethodAnnotationVisitorImpl.this.methodName;
                }
                
                @Override
                public String getMethodDesc() {
                    return MethodAnnotationVisitorImpl.this.methodDesc;
                }
                
                @Override
                public String getClassName() {
                    return MethodAnnotationClassVisitor.this.className;
                }
            }
        }
    }
}
