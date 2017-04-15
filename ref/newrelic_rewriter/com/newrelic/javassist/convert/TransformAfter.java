// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.javassist.convert;

import com.newrelic.javassist.bytecode.BadBytecode;
import com.newrelic.javassist.bytecode.CodeIterator;
import com.newrelic.javassist.NotFoundException;
import com.newrelic.javassist.CtMethod;

public class TransformAfter extends TransformBefore
{
    public TransformAfter(final Transformer next, final CtMethod origMethod, final CtMethod afterMethod) throws NotFoundException {
        super(next, origMethod, afterMethod);
    }
    
    protected int match2(int pos, final CodeIterator iterator) throws BadBytecode {
        iterator.move(pos);
        iterator.insert(this.saveCode);
        iterator.insert(this.loadCode);
        int p = iterator.insertGap(3);
        iterator.setMark(p);
        iterator.insert(this.loadCode);
        pos = iterator.next();
        p = iterator.getMark();
        iterator.writeByte(iterator.byteAt(pos), p);
        iterator.write16bit(iterator.u16bitAt(pos + 1), p + 1);
        iterator.writeByte(184, pos);
        iterator.write16bit(this.newIndex, pos + 1);
        iterator.move(p);
        return iterator.next();
    }
}
