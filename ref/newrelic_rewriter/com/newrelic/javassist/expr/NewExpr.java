// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.javassist.expr;

import com.newrelic.javassist.compiler.JvstTypeChecker;
import com.newrelic.javassist.compiler.MemberResolver;
import com.newrelic.javassist.compiler.ast.ASTList;
import com.newrelic.javassist.compiler.JvstCodeGen;
import com.newrelic.javassist.bytecode.Bytecode;
import com.newrelic.javassist.bytecode.CodeAttribute;
import com.newrelic.javassist.ClassPool;
import com.newrelic.javassist.bytecode.BadBytecode;
import com.newrelic.javassist.compiler.CompileError;
import com.newrelic.javassist.compiler.ProceedHandler;
import com.newrelic.javassist.bytecode.Descriptor;
import com.newrelic.javassist.compiler.Javac;
import com.newrelic.javassist.CannotCompileException;
import com.newrelic.javassist.CtConstructor;
import com.newrelic.javassist.bytecode.ConstPool;
import com.newrelic.javassist.NotFoundException;
import com.newrelic.javassist.CtBehavior;
import com.newrelic.javassist.bytecode.MethodInfo;
import com.newrelic.javassist.CtClass;
import com.newrelic.javassist.bytecode.CodeIterator;

public class NewExpr extends Expr
{
    String newTypeName;
    int newPos;
    
    protected NewExpr(final int pos, final CodeIterator i, final CtClass declaring, final MethodInfo m, final String type, final int np) {
        super(pos, i, declaring, m);
        this.newTypeName = type;
        this.newPos = np;
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
    
    private CtClass getCtClass() throws NotFoundException {
        return this.thisClass.getClassPool().get(this.newTypeName);
    }
    
    public String getClassName() {
        return this.newTypeName;
    }
    
    public String getSignature() {
        final ConstPool constPool = this.getConstPool();
        final int methodIndex = this.iterator.u16bitAt(this.currentPos + 1);
        return constPool.getMethodrefType(methodIndex);
    }
    
    public CtConstructor getConstructor() throws NotFoundException {
        final ConstPool cp = this.getConstPool();
        final int index = this.iterator.u16bitAt(this.currentPos + 1);
        final String desc = cp.getMethodrefType(index);
        return this.getCtClass().getConstructor(desc);
    }
    
    public CtClass[] mayThrow() {
        return super.mayThrow();
    }
    
    private int canReplace() throws CannotCompileException {
        final int op = this.iterator.byteAt(this.newPos + 3);
        if (op == 89) {
            return 4;
        }
        if (op == 90 && this.iterator.byteAt(this.newPos + 4) == 95) {
            return 5;
        }
        return 3;
    }
    
    public void replace(final String statement) throws CannotCompileException {
        this.thisClass.getClassFile();
        final int bytecodeSize = 3;
        int pos = this.newPos;
        final int newIndex = this.iterator.u16bitAt(pos + 1);
        final int codeSize = this.canReplace();
        for (int end = pos + codeSize, i = pos; i < end; ++i) {
            this.iterator.writeByte(0, i);
        }
        final ConstPool constPool = this.getConstPool();
        pos = this.currentPos;
        final int methodIndex = this.iterator.u16bitAt(pos + 1);
        final String signature = constPool.getMethodrefType(methodIndex);
        final Javac jc = new Javac(this.thisClass);
        final ClassPool cp = this.thisClass.getClassPool();
        final CodeAttribute ca = this.iterator.get();
        try {
            final CtClass[] params = Descriptor.getParameterTypes(signature, cp);
            final CtClass newType = cp.get(this.newTypeName);
            final int paramVar = ca.getMaxLocals();
            jc.recordParams(this.newTypeName, params, true, paramVar, this.withinStatic());
            final int retVar = jc.recordReturnType(newType, true);
            jc.recordProceed(new ProceedForNew(newType, newIndex, methodIndex));
            Expr.checkResultValue(newType, statement);
            final Bytecode bytecode = jc.getBytecode();
            Expr.storeStack(params, true, paramVar, bytecode);
            jc.recordLocalVariables(ca, pos);
            bytecode.addConstZero(newType);
            bytecode.addStore(retVar, newType);
            jc.compileStmnt(statement);
            if (codeSize > 3) {
                bytecode.addAload(retVar);
            }
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
    
    static class ProceedForNew implements ProceedHandler
    {
        CtClass newType;
        int newIndex;
        int methodIndex;
        
        ProceedForNew(final CtClass nt, final int ni, final int mi) {
            this.newType = nt;
            this.newIndex = ni;
            this.methodIndex = mi;
        }
        
        public void doit(final JvstCodeGen gen, final Bytecode bytecode, final ASTList args) throws CompileError {
            bytecode.addOpcode(187);
            bytecode.addIndex(this.newIndex);
            bytecode.addOpcode(89);
            gen.atMethodCallCore(this.newType, "<init>", args, false, true, -1, null);
            gen.setType(this.newType);
        }
        
        public void setReturnType(final JvstTypeChecker c, final ASTList args) throws CompileError {
            c.atMethodCallCore(this.newType, "<init>", args);
            c.setType(this.newType);
        }
    }
}
