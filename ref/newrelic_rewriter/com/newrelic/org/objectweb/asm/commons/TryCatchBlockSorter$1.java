// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.objectweb.asm.commons;

import com.newrelic.org.objectweb.asm.tree.AbstractInsnNode;
import com.newrelic.org.objectweb.asm.tree.TryCatchBlockNode;
import java.util.Comparator;

class TryCatchBlockSorter$1 implements Comparator
{
    final /* synthetic */ TryCatchBlockSorter this$0;
    
    TryCatchBlockSorter$1(final TryCatchBlockSorter this$0) {
        this.this$0 = this$0;
    }
    
    public int compare(final TryCatchBlockNode tryCatchBlockNode, final TryCatchBlockNode tryCatchBlockNode2) {
        return this.blockLength(tryCatchBlockNode) - this.blockLength(tryCatchBlockNode2);
    }
    
    private int blockLength(final TryCatchBlockNode tryCatchBlockNode) {
        return this.this$0.instructions.indexOf((AbstractInsnNode)tryCatchBlockNode.end) - this.this$0.instructions.indexOf((AbstractInsnNode)tryCatchBlockNode.start);
    }
}
