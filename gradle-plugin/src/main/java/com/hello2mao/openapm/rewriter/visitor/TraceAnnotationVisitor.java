package com.hello2mao.openapm.rewriter.visitor;

import com.hello2mao.openapm.rewriter.InstrumentationContext;
import com.hello2mao.openapm.rewriter.util.AnnotationImpl;
import com.hello2mao.openapm.rewriter.util.Log;

import org.objectweb.asm.Type;


public class TraceAnnotationVisitor extends AnnotationImpl {

    private Log log;
    private InstrumentationContext context;
    
    public TraceAnnotationVisitor(String name, InstrumentationContext context) {
        super(name);
        this.context = context;
        this.log = context.getLog();
    }
    
    @Override
    public void visitEnum(String parameterName, String desc, String value) {
        super.visitEnum(parameterName, desc, value);
        String className = Type.getType(desc).getClassName();
        context.addTracedMethodParameter(getName(), parameterName, className, value);
    }
    
    @Override
    public void visit(String parameterName, Object value) {
        super.visit(parameterName, value);
        String className = value.getClass().getName();
        context.addTracedMethodParameter(getName(), parameterName, className, value.toString());
    }
}
