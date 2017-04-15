// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.javassist.bytecode.annotation;

import java.io.IOException;
import java.lang.reflect.Method;
import com.newrelic.javassist.ClassPool;
import com.newrelic.javassist.bytecode.ConstPool;

public class StringMemberValue extends MemberValue
{
    int valueIndex;
    static /* synthetic */ Class class$java$lang$String;
    
    public StringMemberValue(final int index, final ConstPool cp) {
        super('s', cp);
        this.valueIndex = index;
    }
    
    public StringMemberValue(final String str, final ConstPool cp) {
        super('s', cp);
        this.setValue(str);
    }
    
    public StringMemberValue(final ConstPool cp) {
        super('s', cp);
        this.setValue("");
    }
    
    Object getValue(final ClassLoader cl, final ClassPool cp, final Method m) {
        return this.getValue();
    }
    
    Class getType(final ClassLoader cl) {
        return (StringMemberValue.class$java$lang$String == null) ? (StringMemberValue.class$java$lang$String = class$("java.lang.String")) : StringMemberValue.class$java$lang$String;
    }
    
    public String getValue() {
        return this.cp.getUtf8Info(this.valueIndex);
    }
    
    public void setValue(final String newValue) {
        this.valueIndex = this.cp.addUtf8Info(newValue);
    }
    
    public String toString() {
        return "\"" + this.getValue() + "\"";
    }
    
    public void write(final AnnotationsWriter writer) throws IOException {
        writer.constValueIndex(this.getValue());
    }
    
    public void accept(final MemberValueVisitor visitor) {
        visitor.visitStringMemberValue(this);
    }
    
    static /* synthetic */ Class class$(final String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x) {
            throw new NoClassDefFoundError().initCause(x);
        }
    }
}
