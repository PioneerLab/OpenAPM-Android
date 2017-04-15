// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.reflections;

import java.util.regex.Pattern;
import java.lang.reflect.Field;
import com.newrelic.com.google.common.collect.Sets;
import java.lang.reflect.Method;
import java.lang.annotation.Annotation;
import java.util.Set;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import java.io.File;
import java.io.InputStream;
import com.newrelic.org.reflections.util.Utils;
import java.io.IOException;
import java.util.Collection;
import com.newrelic.org.reflections.util.ClasspathHelper;
import com.newrelic.org.reflections.serializers.XmlSerializer;
import com.newrelic.com.google.common.base.Predicate;
import com.newrelic.org.reflections.serializers.Serializer;
import com.newrelic.org.reflections.util.FilterBuilder;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.Future;
import com.newrelic.com.google.common.collect.Lists;
import com.newrelic.org.reflections.vfs.Vfs;
import java.net.URL;
import com.newrelic.org.reflections.util.ConfigurationBuilder;
import java.util.Iterator;
import com.newrelic.org.reflections.scanners.Scanner;
import javax.annotation.Nullable;
import com.newrelic.org.slf4j.Logger;

public class Reflections extends ReflectionUtils
{
    @Nullable
    public static Logger log;
    protected final transient Configuration configuration;
    private Store store;
    
    public Reflections(final Configuration configuration) {
        this.configuration = configuration;
        this.store = new Store(configuration.getExecutorService() != null);
        if (configuration.getScanners() != null && !configuration.getScanners().isEmpty()) {
            for (final Scanner scanner : configuration.getScanners()) {
                scanner.setConfiguration(configuration);
                scanner.setStore(this.store.getOrCreate(scanner.getClass().getSimpleName()));
            }
            this.scan();
        }
    }
    
    public Reflections(final String prefix, @Nullable final Scanner... scanners) {
        this(new Object[] { prefix, scanners });
    }
    
    public Reflections(final Object... params) {
        this(ConfigurationBuilder.build(params));
    }
    
    protected Reflections() {
        this.configuration = new ConfigurationBuilder();
        this.store = new Store(false);
    }
    
    protected void scan() {
        if (this.configuration.getUrls() == null || this.configuration.getUrls().isEmpty()) {
            if (Reflections.log != null) {
                Reflections.log.error("given scan urls are empty. set urls in the configuration");
            }
            return;
        }
        if (Reflections.log != null && Reflections.log.isDebugEnabled()) {
            final StringBuilder urls = new StringBuilder();
            for (final URL url : this.configuration.getUrls()) {
                urls.append("\t").append(url.toExternalForm()).append("\n");
            }
            Reflections.log.debug("going to scan these urls:\n" + (Object)urls);
        }
        long time = System.currentTimeMillis();
        int scannedUrls = 0;
        final ExecutorService executorService = this.configuration.getExecutorService();
        if (executorService == null) {
            for (final URL url2 : this.configuration.getUrls()) {
                try {
                    for (final Vfs.File file : Vfs.fromURL(url2).getFiles()) {
                        this.scan(file);
                    }
                    ++scannedUrls;
                }
                catch (ReflectionsException e) {
                    if (Reflections.log == null) {
                        continue;
                    }
                    Reflections.log.error("could not create Vfs.Dir from url. ignoring the exception and continuing", e);
                }
            }
        }
        else {
            final List<Future<?>> futures = (List<Future<?>>)Lists.newArrayList();
            try {
                for (final URL url3 : this.configuration.getUrls()) {
                    try {
                        for (final Vfs.File file2 : Vfs.fromURL(url3).getFiles()) {
                            futures.add(executorService.submit(new Runnable() {
                                public void run() {
                                    Reflections.this.scan(file2);
                                }
                            }));
                        }
                        ++scannedUrls;
                    }
                    catch (ReflectionsException e2) {
                        if (Reflections.log == null) {
                            continue;
                        }
                        Reflections.log.error("could not create Vfs.Dir from url. ignoring the exception and continuing", e2);
                    }
                }
                for (final Future future : futures) {
                    try {
                        future.get();
                    }
                    catch (Exception e3) {
                        throw new RuntimeException(e3);
                    }
                }
            }
            finally {
                executorService.shutdown();
            }
        }
        time = System.currentTimeMillis() - time;
        final Integer keys = this.store.getKeysCount();
        final Integer values = this.store.getValuesCount();
        if (Reflections.log != null) {
            Reflections.log.info(String.format("Reflections took %d ms to scan %d urls, producing %d keys and %d values %s", time, scannedUrls, keys, values, (executorService != null && executorService instanceof ThreadPoolExecutor) ? String.format("[using %d cores]", ((ThreadPoolExecutor)executorService).getMaximumPoolSize()) : ""));
        }
    }
    
    private void scan(final Vfs.File file) {
        final String input = file.getRelativePath().replace('/', '.');
        if (this.configuration.acceptsInput(input)) {
            for (final Scanner scanner : this.configuration.getScanners()) {
                try {
                    if (!scanner.acceptsInput(input)) {
                        continue;
                    }
                    scanner.scan(file);
                }
                catch (Exception e) {
                    Reflections.log.warn("could not scan file " + file.toString() + " with scanner " + scanner.getClass().getSimpleName(), e);
                }
            }
        }
    }
    
    public static Reflections collect() {
        return collect("META-INF/reflections", new FilterBuilder().include(".*-reflections.xml"), new Serializer[0]);
    }
    
    public static Reflections collect(final String packagePrefix, final Predicate<String> resourceNameFilter, @Nullable final Serializer... optionalSerializer) {
        final Serializer serializer = (optionalSerializer != null && optionalSerializer.length == 1) ? optionalSerializer[0] : new XmlSerializer();
        final Reflections reflections = new Reflections();
        for (final Vfs.File file : Vfs.findFiles(ClasspathHelper.forPackage(packagePrefix, new ClassLoader[0]), packagePrefix, resourceNameFilter)) {
            InputStream inputStream = null;
            try {
                inputStream = file.openInputStream();
                reflections.merge(serializer.read(inputStream));
                if (Reflections.log == null) {
                    continue;
                }
                Reflections.log.info("Reflections collected metadata from " + file + " using serializer " + serializer.getClass().getName());
            }
            catch (IOException e) {
                throw new ReflectionsException("could not merge " + file, e);
            }
            finally {
                Utils.close(inputStream);
            }
        }
        return reflections;
    }
    
    public Reflections collect(final InputStream inputStream) {
        try {
            this.merge(this.configuration.getSerializer().read(inputStream));
            if (Reflections.log != null) {
                Reflections.log.info("Reflections collected metadata from input stream using serializer " + this.configuration.getSerializer().getClass().getName());
            }
        }
        catch (Exception ex) {
            throw new ReflectionsException("could not merge input stream", ex);
        }
        return this;
    }
    
    public Reflections collect(final File file) {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            return this.collect(inputStream);
        }
        catch (FileNotFoundException e) {
            throw new ReflectionsException("could not obtain input stream from file " + file, e);
        }
        finally {
            Utils.close(inputStream);
        }
    }
    
    public Reflections merge(final Reflections reflections) {
        this.store.merge(reflections.store);
        return this;
    }
    
    @Nullable
    public <T extends Scanner> T get(final Class<T> scannerClass) {
        for (final Scanner scanner : this.configuration.getScanners()) {
            if (scanner.getClass().equals(scannerClass)) {
                return (T)scanner;
            }
        }
        return null;
    }
    
    public <T> Set<Class<? extends T>> getSubTypesOf(final Class<T> type) {
        final Set<String> subTypes = this.store.getSubTypesOf(type.getName());
        return (Set<Class<? extends T>>)this.toClasses(subTypes);
    }
    
    public Set<Class<?>> getTypesAnnotatedWith(final Class<? extends Annotation> annotation) {
        final Set<String> typesAnnotatedWith = this.store.getTypesAnnotatedWith(annotation.getName());
        return this.toClasses(typesAnnotatedWith);
    }
    
    public Set<Class<?>> getTypesAnnotatedWith(final Class<? extends Annotation> annotation, final boolean honorInherited) {
        final Set<String> typesAnnotatedWith = this.store.getTypesAnnotatedWith(annotation.getName(), honorInherited);
        return this.toClasses(typesAnnotatedWith);
    }
    
    public Set<Class<?>> getTypesAnnotatedWith(final Annotation annotation) {
        return this.getTypesAnnotatedWith(annotation, true);
    }
    
    public Set<Class<?>> getTypesAnnotatedWith(final Annotation annotation, final boolean honorInherited) {
        final Set<String> types = this.store.getTypesAnnotatedWithDirectly(annotation.annotationType().getName());
        final Set<Class<?>> annotated = ReflectionUtils.getAll((Iterable<? extends Class<?>>)this.toClasses(types), ReflectionUtils.withAnnotation(annotation));
        final Set<String> inherited = this.store.getInheritedSubTypes(ReflectionUtils.names(annotated), annotation.annotationType().getName(), honorInherited);
        return this.toClasses(inherited);
    }
    
    public Set<Method> getMethodsAnnotatedWith(final Class<? extends Annotation> annotation) {
        final Set<String> annotatedWith = this.store.getMethodsAnnotatedWith(annotation.getName());
        final Set<Method> result = (Set<Method>)Sets.newHashSet();
        for (final String annotated : annotatedWith) {
            result.add(Utils.getMethodFromDescriptor(annotated, this.configuration.getClassLoaders()));
        }
        return result;
    }
    
    public Set<Method> getMethodsAnnotatedWith(final Annotation annotation) {
        return ReflectionUtils.getAll((Iterable<? extends Method>)this.getMethodsAnnotatedWith(annotation.annotationType()), ReflectionUtils.withAnnotation(annotation));
    }
    
    public Set<Field> getFieldsAnnotatedWith(final Class<? extends Annotation> annotation) {
        final Set<Field> result = (Set<Field>)Sets.newHashSet();
        final Collection<String> annotatedWith = this.store.getFieldsAnnotatedWith(annotation.getName());
        for (final String annotated : annotatedWith) {
            result.add(Utils.getFieldFromString(annotated, this.configuration.getClassLoaders()));
        }
        return result;
    }
    
    public Set<Field> getFieldsAnnotatedWith(final Annotation annotation) {
        return ReflectionUtils.getAll((Iterable<? extends Field>)this.getFieldsAnnotatedWith(annotation.annotationType()), ReflectionUtils.withAnnotation(annotation));
    }
    
    public Set<String> getResources(final Predicate<String> namePredicate) {
        return this.store.getResources(namePredicate);
    }
    
    public Set<String> getResources(final Pattern pattern) {
        return this.getResources(new Predicate<String>() {
            public boolean apply(final String input) {
                return pattern.matcher(input).matches();
            }
        });
    }
    
    private <T> Set<Class<? extends T>> toClasses(final Set<String> names) {
        return (Set<Class<? extends T>>)Sets.newHashSet((Iterable<?>)ReflectionUtils.forNames(names, this.configuration.getClassLoaders()));
    }
    
    public Store getStore() {
        return this.store;
    }
    
    public File save(final String filename) {
        return this.save(filename, this.configuration.getSerializer());
    }
    
    public File save(final String filename, final Serializer serializer) {
        final File file = serializer.save(this, filename);
        if (Reflections.log != null) {
            Reflections.log.info("Reflections successfully saved in " + file.getAbsolutePath() + " using " + serializer.getClass().getSimpleName());
        }
        return file;
    }
    
    static {
        Reflections.log = Utils.findLogger(Reflections.class);
    }
}
