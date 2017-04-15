// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.reflections.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.jar.Manifest;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.Iterator;
import javax.servlet.ServletContext;
import java.io.File;
import java.util.Collection;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.util.Enumeration;
import java.io.IOException;
import com.newrelic.com.google.common.collect.Sets;
import java.net.URL;
import java.util.Set;
import com.newrelic.org.reflections.Reflections;

public abstract class ClasspathHelper
{
    public static ClassLoader contextClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }
    
    public static ClassLoader staticClassLoader() {
        return Reflections.class.getClassLoader();
    }
    
    public static ClassLoader[] classLoaders(final ClassLoader... classLoaders) {
        if (classLoaders != null && classLoaders.length != 0) {
            return classLoaders;
        }
        final ClassLoader contextClassLoader = contextClassLoader();
        final ClassLoader staticClassLoader = staticClassLoader();
        final ClassLoader[] array3;
        if (contextClassLoader != staticClassLoader) {
            final ClassLoader[] array2;
            final ClassLoader[] array = array2 = new ClassLoader[2];
            array[0] = contextClassLoader;
            array[1] = staticClassLoader;
        }
        else {
            array3 = new ClassLoader[] { contextClassLoader };
        }
        return array3;
    }
    
    public static Set<URL> forPackage(final String name, final ClassLoader... classLoaders) {
        final Set<URL> result = (Set<URL>)Sets.newHashSet();
        final ClassLoader[] loaders = classLoaders(classLoaders);
        final String resourceName = resourceName(name);
        for (final ClassLoader classLoader : loaders) {
            try {
                final Enumeration<URL> urls = classLoader.getResources(resourceName);
                while (urls.hasMoreElements()) {
                    final URL url = urls.nextElement();
                    final int index = url.toExternalForm().lastIndexOf(resourceName);
                    if (index != -1) {
                        result.add(new URL(url.toExternalForm().substring(0, index)));
                    }
                    else {
                        result.add(url);
                    }
                }
            }
            catch (IOException e) {
                if (Reflections.log != null) {
                    Reflections.log.error("error getting resources for package " + name, e);
                }
            }
        }
        return result;
    }
    
    public static URL forClass(final Class<?> aClass, final ClassLoader... classLoaders) {
        final ClassLoader[] loaders = classLoaders(classLoaders);
        final String resourceName = aClass.getName().replace(".", "/") + ".class";
        for (final ClassLoader classLoader : loaders) {
            try {
                final URL url = classLoader.getResource(resourceName);
                if (url != null) {
                    final String normalizedUrl = url.toExternalForm().substring(0, url.toExternalForm().lastIndexOf(aClass.getPackage().getName().replace(".", "/")));
                    return new URL(normalizedUrl);
                }
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
    
    public static Set<URL> forClassLoader(final ClassLoader... classLoaders) {
        final Set<URL> result = (Set<URL>)Sets.newHashSet();
        final ClassLoader[] arr$;
        final ClassLoader[] loaders = arr$ = classLoaders(classLoaders);
        for (ClassLoader classLoader : arr$) {
            while (classLoader != null) {
                if (classLoader instanceof URLClassLoader) {
                    final URL[] urls = ((URLClassLoader)classLoader).getURLs();
                    if (urls != null) {
                        result.addAll(Sets.newHashSet(urls));
                    }
                }
                classLoader = classLoader.getParent();
            }
        }
        return result;
    }
    
    public static Set<URL> forJavaClassPath() {
        final Set<URL> urls = (Set<URL>)Sets.newHashSet();
        final String javaClassPath = System.getProperty("java.class.path");
        if (javaClassPath != null) {
            for (final String path : javaClassPath.split(File.pathSeparator)) {
                try {
                    urls.add(new File(path).toURI().toURL());
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return urls;
    }
    
    public static Set<URL> forWebInfLib(final ServletContext servletContext) {
        final Set<URL> urls = (Set<URL>)Sets.newHashSet();
        for (final Object urlString : servletContext.getResourcePaths("/WEB-INF/lib")) {
            try {
                urls.add(servletContext.getResource((String)urlString));
            }
            catch (MalformedURLException ex) {}
        }
        return urls;
    }
    
    public static URL forWebInfClasses(final ServletContext servletContext) {
        try {
            final String path = servletContext.getRealPath("/WEB-INF/classes");
            if (path == null) {
                return servletContext.getResource("/WEB-INF/classes");
            }
            final File file = new File(path);
            if (file.exists()) {
                return file.toURL();
            }
        }
        catch (MalformedURLException ex) {}
        return null;
    }
    
    public static Set<URL> forManifest() {
        return forManifest(forClassLoader(new ClassLoader[0]));
    }
    
    public static Set<URL> forManifest(final URL url) {
        final Set<URL> result = (Set<URL>)Sets.newHashSet();
        result.add(url);
        try {
            final String part = cleanPath(url);
            final File jarFile = new File(part);
            final JarFile myJar = new JarFile(part);
            URL validUrl = tryToGetValidUrl(jarFile.getPath(), new File(part).getParent(), part);
            if (validUrl != null) {
                result.add(validUrl);
            }
            final Manifest manifest = myJar.getManifest();
            if (manifest != null) {
                final String classPath = manifest.getMainAttributes().getValue(new Attributes.Name("Class-Path"));
                if (classPath != null) {
                    for (final String jar : classPath.split(" ")) {
                        validUrl = tryToGetValidUrl(jarFile.getPath(), new File(part).getParent(), jar);
                        if (validUrl != null) {
                            result.add(validUrl);
                        }
                    }
                }
            }
        }
        catch (IOException ex) {}
        return result;
    }
    
    public static Set<URL> forManifest(final Iterable<URL> urls) {
        final Set<URL> result = (Set<URL>)Sets.newHashSet();
        for (final URL url : urls) {
            result.addAll(forManifest(url));
        }
        return result;
    }
    
    static URL tryToGetValidUrl(final String workingDir, final String path, final String filename) {
        try {
            if (new File(filename).exists()) {
                return new File(filename).toURI().toURL();
            }
            if (new File(path + File.separator + filename).exists()) {
                return new File(path + File.separator + filename).toURI().toURL();
            }
            if (new File(workingDir + File.separator + filename).exists()) {
                return new File(workingDir + File.separator + filename).toURI().toURL();
            }
            if (new File(new URL(filename).getFile()).exists()) {
                return new File(new URL(filename).getFile()).toURI().toURL();
            }
        }
        catch (MalformedURLException ex) {}
        return null;
    }
    
    public static String cleanPath(final URL url) {
        String path = url.getPath();
        try {
            path = URLDecoder.decode(path, "UTF-8");
        }
        catch (UnsupportedEncodingException ex) {}
        if (path.startsWith("jar:")) {
            path = path.substring("jar:".length());
        }
        if (path.startsWith("file:")) {
            path = path.substring("file:".length());
        }
        if (path.endsWith("!/")) {
            path = path.substring(0, path.lastIndexOf("!/")) + "/";
        }
        return path;
    }
    
    private static String resourceName(final String name) {
        if (name != null) {
            String resourceName = name.replace(".", "/");
            resourceName = resourceName.replace("\\", "/");
            if (resourceName.startsWith("/")) {
                resourceName = resourceName.substring(1);
            }
            return resourceName;
        }
        return name;
    }
}
