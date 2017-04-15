// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.slf4j.impl;

import com.newrelic.org.slf4j.helpers.NOPMDCAdapter;
import com.newrelic.org.slf4j.spi.MDCAdapter;

public class StaticMDCBinder
{
    public static final StaticMDCBinder SINGLETON;
    static /* synthetic */ Class class$org$slf4j$helpers$NOPMDCAdapter;
    
    public MDCAdapter getMDCA() {
        return new NOPMDCAdapter();
    }
    
    public String getMDCAdapterClassStr() {
        return ((StaticMDCBinder.class$org$slf4j$helpers$NOPMDCAdapter == null) ? (StaticMDCBinder.class$org$slf4j$helpers$NOPMDCAdapter = class$("com.newrelic.org.slf4j.helpers.NOPMDCAdapter")) : StaticMDCBinder.class$org$slf4j$helpers$NOPMDCAdapter).getName();
    }
    
    static /* synthetic */ Class class$(final String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x) {
            throw new NoClassDefFoundError(x.getMessage());
        }
    }
    
    static {
        SINGLETON = new StaticMDCBinder();
    }
}
