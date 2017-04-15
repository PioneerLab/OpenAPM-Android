// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.dom4j.io;

import com.newrelic.org.dom4j.ElementPath;

class PruningDispatchHandler extends DispatchHandler
{
    public void onEnd(final ElementPath elementPath) {
        super.onEnd(elementPath);
        if (this.getActiveHandlerCount() == 0) {
            elementPath.getCurrent().detach();
        }
    }
}
