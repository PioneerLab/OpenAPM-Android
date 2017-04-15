// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.javassist.expr;

import com.newrelic.javassist.compiler.JvstTypeChecker;
import com.newrelic.javassist.compiler.ast.ASTList;
import com.newrelic.javassist.compiler.JvstCodeGen;
import com.newrelic.javassist.bytecode.Bytecode;
import com.newrelic.javassist.bytecode.CodeAttribute;
import com.newrelic.javassist.ClassPool;
import com.newrelic.javassist.bytecode.BadBytecode;
import com.newrelic.javassist.compiler.CompileError;
import com.newrelic.javassist.CannotCompileException;
import com.newrelic.javassist.compiler.ProceedHandler;
import com.newrelic.javassist.compiler.Javac;
import com.newrelic.javassist.NotFoundException;
import com.newrelic.javassist.bytecode.ConstPool;
import com.newrelic.javassist.CtBehavior;
import com.newrelic.javassist.bytecode.MethodInfo;
import com.newrelic.javassist.CtClass;
import com.newrelic.javassist.bytecode.CodeIterator;

public class Cast extends Expr
{
    protected Cast(final int pos, final CodeIterator i, final CtClass declaring, final MethodInfo m) {
        super(pos, i, declaring, m);
    }
    
    public CtBehavior where() {
        return super.where();
    }
    
    public int getLineNumber() {
        return super.getLineNumber();
    }
    
    public String getFileName() {
        return super.getFileName();
    }
    
    public CtClass getType() throws NotFoundException {
        final ConstPool cp = this.getConstPool();
        final int pos = this.currentPos;
        final int index = this.iterator.u16bitAt(pos + 1);
        final String name = cp.getClassInfo(index);
        return this.thisClass.getClassPool().getCtClass(name);
    }
    
    public CtClass[] mayThrow() {
        return super.mayThrow();
    }
    
    public void replace(final String statement) throws CannotCompileException {
        this.thisClass.getClassFile();
        final ConstPool constPool = this.getConstPool();
        final int pos = this.currentPos;
        final int index = this.iterator.u16bitAt(pos + 1);
        final Javac jc = new Javac(this.thisClass);
        final ClassPool cp = this.thisClass.getClassPool();
        final CodeAttribute ca = this.iterator.get();
        try {
            final CtClass[] params = { cp.get("java.lang.Object") };
            final CtClass retType = this.getType();
            final int paramVar = ca.getMaxLocals();
            jc.recordParams("java.lang.Object", params, true, paramVar, this.withinStatic());
            final int retVar = jc.recordReturnType(retType, true);
            jc.recordProceed(new ProceedForCast(index, retType));
            Expr.checkResultValue(retType, statement);
            final Bytecode bytecode = jc.getBytecode();
            Expr.storeStack(params, true, paramVar, bytecode);
            jc.recordLocalVariables(ca, pos);
            bytecode.addConstZero(retType);
            bytecode.addStore(retVar, retType);
            jc.compileStmnt(statement);
            bytecode.addLoad(retVar, retType);
            this.replace0(pos, bytecode, 3);
        }
        catch (CompileError e) {
            throw new CannotCompileException(e);
        }
        catch (NotFoundException e2) {
            throw new CannotCompileException(e2);
        }
        catch (BadBytecode e3) {
            throw new CannotCompileException("broken method");
        }
    }
    
    static class ProceedForCast implements ProceedHandler
    {
        int index;
        CtClass retType;
        
        ProceedForCast(final int i, final CtClass t) {
            this.index = i;
            this.retType = t;
        }
        
        public void doit(final JvstCodeGen gen, final Bytecode bytecode, final ASTList args) throws CompileError {
            if (gen.getMethodArgsLength(args) != 1) {
                throw new CompileError("$proceed() cannot take more than one parameter for cast");
            }
            gen.atMethodArgs(args, new int[1], new int[1], new String[1]);
            bytecode.addOpcode(192);
            bytecode.addIndex(this.index);
            gen.setType(this.retType);
        }
        
        public void setReturnType(final JvstTypeChecker c, final ASTList args) throws CompileError {
            c.atMethodArgs(args, new int[1], new int[1], new String[1]);
            c.setType(this.retType);
        }
    }
}
