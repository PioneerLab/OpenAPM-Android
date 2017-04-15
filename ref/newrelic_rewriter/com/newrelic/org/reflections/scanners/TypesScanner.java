// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.reflections.scanners;

import com.newrelic.com.google.common.collect.Lists;
import com.newrelic.org.reflections.serializers.JavaCodeSerializer;
import com.newrelic.org.reflections.ReflectionsException;
import com.newrelic.org.reflections.vfs.Vfs;
import java.util.List;

public class TypesScanner extends AbstractScanner
{
    private static final List<String> javaCodeSerializerInterfaces;
    
    public boolean acceptsInput(final String file) {
        return file.endsWith(".class") && !file.endsWith("package-info.class");
    }
    
    public void scan(final Vfs.File file) {
        try {
            final Object cls = this.getMetadataAdapter().getOfCreateClassObject(file);
            this.scan(cls, file);
        }
        catch (Exception e) {
            throw new ReflectionsException("could not create class file from " + file.getName(), e);
        }
    }
    
    private void scan(final Object cls, final Vfs.File file) {
        if (isJavaCodeSerializer(this.getMetadataAdapter().getInterfacesNames(cls))) {
            return;
        }
        final String className = this.getMetadataAdapter().getClassName(cls);
        this.getStore().put(className, className);
    }
    
    public void scan(final Object cls) {
        throw new UnsupportedOperationException("should not get here");
    }
    
    public static boolean isJavaCodeSerializer(final List<String> interfacesNames) {
        return interfacesNames.size() == 1 && TypesScanner.javaCodeSerializerInterfaces.contains(interfacesNames.get(0));
    }
    
    static {
        javaCodeSerializerInterfaces = Lists.newArrayList(JavaCodeSerializer.IElement.class.getName(), JavaCodeSerializer.IPackage.class.getName(), JavaCodeSerializer.IClass.class.getName(), JavaCodeSerializer.IField.class.getName(), JavaCodeSerializer.IMethod.class.getName());
    }
}
