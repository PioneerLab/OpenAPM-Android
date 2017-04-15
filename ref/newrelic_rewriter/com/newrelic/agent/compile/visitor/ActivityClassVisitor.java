// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.compile.visitor;

import com.newrelic.org.objectweb.asm.MethodVisitor;
import com.newrelic.org.objectweb.asm.commons.GeneratorAdapter;
import java.util.Map;
import java.util.Set;
import com.newrelic.org.objectweb.asm.commons.Method;
import com.newrelic.agent.compile.Log;
import com.newrelic.agent.compile.InstrumentationContext;
import com.newrelic.org.objectweb.asm.ClassVisitor;
import com.newrelic.com.google.common.collect.ImmutableMap;
import com.newrelic.org.objectweb.asm.Type;
import com.newrelic.com.google.common.collect.ImmutableSet;

public class ActivityClassVisitor extends EventHookClassVisitor
{
    static final ImmutableSet<String> ACTIVITY_CLASS_NAMES;
    static final ImmutableSet<String> IGNORED_SDK_PACKAGES;
    static final Type applicationStateMonitorType;
    public static final ImmutableMap<String, String> traceMethodMap;
    public static final ImmutableSet<String> startTracingOn;
    
    public ActivityClassVisitor(final ClassVisitor cv, final InstrumentationContext context, final Log log) {
        super(cv, context, log, ActivityClassVisitor.ACTIVITY_CLASS_NAMES, ImmutableMap.of(new Method("onStart", "()V"), new Method("activityStarted", "()V"), new Method("onStop", "()V"), new Method("activityStopped", "()V")));
    }
    
    @Override
    public void visit(final int version, final int access, final String name, final String signature, final String superName, String[] interfaces) {
        if (this.baseClasses.contains(superName)) {
            interfaces = TraceClassDecorator.addInterface(interfaces);
        }
        super.visit(version, access, name, signature, superName, interfaces);
    }
    
    @Override
    protected void injectCodeIntoMethod(final GeneratorAdapter generatorAdapter, final Method method, final Method monitorMethod) {
        generatorAdapter.invokeStatic(ActivityClassVisitor.applicationStateMonitorType, new Method("getInstance", ActivityClassVisitor.applicationStateMonitorType, new Type[0]));
        generatorAdapter.invokeVirtual(ActivityClassVisitor.applicationStateMonitorType, monitorMethod);
    }
    
    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String desc, final String signature, final String[] exceptions) {
        if (ActivityClassVisitor.ACTIVITY_CLASS_NAMES.contains(this.context.getClassName())) {
            this.log.debug("Ignoring Android support class method[" + this.context.getClassName() + "#" + name + "]");
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
        if (this.instrument && ActivityClassVisitor.traceMethodMap.containsKey(name) && ActivityClassVisitor.traceMethodMap.get(name).equals(desc)) {
            this.log.debug("Trace method [" + name + "]");
            final MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
            final TraceMethodVisitor traceMethodVisitor = new TraceMethodVisitor(methodVisitor, access, name, desc, this.context);
            if (ActivityClassVisitor.startTracingOn.contains(name)) {
                this.log.debug("Start new trace for [" + name + "]");
                traceMethodVisitor.setStartTracing();
            }
            return traceMethodVisitor;
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
    
    static {
        ACTIVITY_CLASS_NAMES = ImmutableSet.of("android/app/Activity", "android/app/Fragment", "android/app/ActivityGroup", "android/app/TabActivity", "android/app/AliasActivity", "android/app/ExpandableListActivity", "android/app/ListActivity", "android/app/LauncherActivity", "android/app/NativeActivity", "android/accounts/AccountAuthenticatorActivity", "android/preference/PreferenceActivity", "android/support/v4/app/Fragment", "android/support/v4/app/FragmentActivity", "android/support/v4/app/DialogFragment", "android/support/v4/app/ListFragment", "android/support/v4/app/BaseFragmentActivityDonut", "android/support/v4/app/BaseFragmentActivityGingerbread", "android/support/design/widget/BottomSheetDialogFragment", "android/support/v7/app/ActionBarActivity", "android/support/v7/app/AppCompatActivity", "android/support/v7/app/AppCompatDialogFragment", "android/support/v7/app/MediaRouteChooserDialogFragment", "android/support/v7/app/MediaRouteControllerDialogFragment", "android/support/v7/app/MediaRouteDiscoveryFragment", "android/support/v7/preference/PreferenceDialogFragmentCompat", "android/support/v7/preference/EditTextPreferenceDialogFragmentCompat", "android/support/v7/preference/ListPreferenceDialogFragmentCompat", "android/support/v7/preference/MultiSelectListPreferenceDialogFragmentCompat", "android/support/v7/preference/PreferenceDialogFragmentCompat", "android/support/v17/leanback/app/BrowseSupportFragment", "android/support/v17/leanback/app/BrandedSupportFragment", "android/support/v17/leanback/app/DetailsSupportFragment", "android.support/v17/leanback/app/ErrorSupportFragment", "android/support/v17/leanback/app/OnboardingSupportFragment", "android/support/v17/leanback/app/PlaybackOverlaySupportFragment", "android/support/v17/leanback/app/RowsSupportFragment", "android/support/v17/leanback/app/SearchSupportFragment", "android/support/v17/leanback/app/VerticalGridSupportFragment");
        IGNORED_SDK_PACKAGES = ImmutableSet.of("android/preference", "android/support/design", "android/support/v4", "android/support/v7", "android/support/v13", "android/support/v17", new String[0]);
        applicationStateMonitorType = Type.getObjectType("com/newrelic/agent/android/background/ApplicationStateMonitor");
        traceMethodMap = ImmutableMap.of("onCreate", "(Landroid/os/Bundle;)V", "onCreateView", "(Landroid/view/LayoutInflater;Landroid/view/ViewGroup;Landroid/os/Bundle;)Landroid/view/View;");
        startTracingOn = ImmutableSet.of("onCreate");
    }
}
