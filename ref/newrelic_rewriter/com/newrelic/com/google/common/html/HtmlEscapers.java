// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.com.google.common.html;

import com.newrelic.com.google.common.escape.Escapers;
import com.newrelic.com.google.common.escape.Escaper;
import com.newrelic.com.google.common.annotations.GwtCompatible;
import com.newrelic.com.google.common.annotations.Beta;

@Beta
@GwtCompatible
public final class HtmlEscapers
{
    private static final Escaper HTML_ESCAPER;
    
    public static Escaper htmlEscaper() {
        return HtmlEscapers.HTML_ESCAPER;
    }
    
    static {
        HTML_ESCAPER = Escapers.builder().addEscape('\"', "&quot;").addEscape('\'', "&#39;").addEscape('&', "&amp;").addEscape('<', "&lt;").addEscape('>', "&gt;").build();
    }
}
