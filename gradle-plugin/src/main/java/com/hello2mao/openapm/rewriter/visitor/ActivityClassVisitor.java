package com.hello2mao.openapm.rewriter.visitor;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.hello2mao.openapm.rewriter.InstrumentationContext;
import com.hello2mao.openapm.rewriter.util.Log;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.GeneratorAdapter;
import org.objectweb.asm.commons.Method;

public class ActivityClassVisitor extends EventHookClassVisitor
{
    private static final ImmutableSet<String> ACTIVITY_CLASS_NAMES = ImmutableSet.of(
            "android/app/Activity",
            "android/app/Fragment",
            "android/app/ActivityGroup",
            "android/app/TabActivity",
            "android/app/AliasActivity",
            "android/app/ExpandableListActivity",
            "android/app/ListActivity",
            "android/app/LauncherActivity",
            "android/app/NativeActivity",
            "android/accounts/AccountAuthenticatorActivity",
            "android/preference/PreferenceActivity",
            "android/support/v4/app/Fragment",
            "android/support/v4/app/FragmentActivity",
            "android/support/v4/app/DialogFragment",
            "android/support/v4/app/ListFragment",
            "android/support/v4/app/BaseFragmentActivityDonut",
            "android/support/v4/app/BaseFragmentActivityGingerbread",
            "android/support/design/widget/BottomSheetDialogFragment",
            "android/support/v7/app/ActionBarActivity",
            "android/support/v7/app/AppCompatActivity",
            "android/support/v7/app/AppCompatDialogFragment",
            "android/support/v7/app/MediaRouteChooserDialogFragment",
            "android/support/v7/app/MediaRouteControllerDialogFragment",
            "android/support/v7/app/MediaRouteDiscoveryFragment",
            "android/support/v7/preference/PreferenceDialogFragmentCompat",
            "android/support/v7/preference/EditTextPreferenceDialogFragmentCompat",
            "android/support/v7/preference/ListPreferenceDialogFragmentCompat",
            "android/support/v7/preference/MultiSelectListPreferenceDialogFragmentCompat",
            "android/support/v7/preference/PreferenceDialogFragmentCompat",
            "android/support/v17/leanback/app/BrowseSupportFragment",
            "android/support/v17/leanback/app/BrandedSupportFragment",
            "android/support/v17/leanback/app/DetailsSupportFragment",
            "android.support/v17/leanback/app/ErrorSupportFragment",
            "android/support/v17/leanback/app/OnboardingSupportFragment",
            "android/support/v17/leanback/app/PlaybackOverlaySupportFragment",
            "android/support/v17/leanback/app/RowsSupportFragment",
            "android/support/v17/leanback/app/SearchSupportFragment",
            "android/support/v17/leanback/app/VerticalGridSupportFragment");
    private static final ImmutableSet<String> IGNORED_SDK_PACKAGES = ImmutableSet.of(
            "android/preference",
            "android/support/design",
            "android/support/v4",
            "android/support/v7",
            "android/support/v13",
            "android/support/v17");
    private static Type applicationStateMonitorType
            = Type.getObjectType("com/newrelic/agent/android/background/ApplicationStateMonitor");
    public static ImmutableMap<String, String> traceMethodMap = ImmutableMap.of(
            "onCreate",
            "(Landroid/os/Bundle;)V",
            "onCreateView",
            "(Landroid/view/LayoutInflater;Landroid/view/ViewGroup;Landroid/os/Bundle;)Landroid/view/View;");
    public static ImmutableSet<String> startTracingOn = ImmutableSet.of("onCreate");


    public ActivityClassVisitor(ClassVisitor cv, InstrumentationContext context, Log log) {
        super(cv, context, log, ActivityClassVisitor.ACTIVITY_CLASS_NAMES,
                ImmutableMap.of(new Method("onStart", "()V"), new Method("activityStarted", "()V"),
                        new Method("onStop", "()V"), new Method("activityStopped", "()V")));
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName,
                      String[] interfaces) {
//        if (this.baseClasses.contains(superName)) {
//            interfaces = TraceClassDecorator.addInterface(interfaces);
//        }
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    protected void injectCodeIntoMethod(GeneratorAdapter generatorAdapter, Method method, Method monitorMethod) {
        generatorAdapter.invokeStatic(ActivityClassVisitor.applicationStateMonitorType, new Method("getInstance", ActivityClassVisitor.applicationStateMonitorType, new Type[0]));
        generatorAdapter.invokeVirtual(ActivityClassVisitor.applicationStateMonitorType, monitorMethod);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (ActivityClassVisitor.ACTIVITY_CLASS_NAMES.contains(this.context.getClassName())) {
            this.log.debug("Ignoring Android support class method[" + this.context.getClassName() + "#" + name + "]");
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
        if (this.instrument && ActivityClassVisitor.traceMethodMap.containsKey(name) && ActivityClassVisitor.traceMethodMap.get(name).equals(desc)) {
            this.log.debug("Trace method [" + name + "]");
            MethodVisitor methodVisitor = super.visitMethod(access, name, desc, signature, exceptions);
//            TraceMethodVisitor traceMethodVisitor = new TraceMethodVisitor(methodVisitor, access, name, desc, this.context);
            if (ActivityClassVisitor.startTracingOn.contains(name)) {
                this.log.debug("Start new trace for [" + name + "]");
//                traceMethodVisitor.setStartTracing();
            }
//            return traceMethodVisitor;
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}
