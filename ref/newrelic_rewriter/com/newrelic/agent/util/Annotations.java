// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.util;

import java.io.FileFilter;
import java.net.URLClassLoader;
import com.newrelic.agent.android.Agent;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.io.UnsupportedEncodingException;
import java.io.File;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.regex.Pattern;
import com.newrelic.agent.compile.visitor.MethodAnnotationVisitor;
import com.newrelic.org.objectweb.asm.Type;
import java.util.Iterator;
import java.io.IOException;
import com.newrelic.agent.compile.visitor.ClassAnnotationVisitor;
import com.newrelic.org.objectweb.asm.ClassReader;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.util.Map;
import java.util.ArrayList;
import java.util.Collection;
import java.net.URL;
import java.util.Set;

public class Annotations
{
    public static Collection<ClassAnnotation> getClassAnnotations(final Class annotationClass, final String packageSearchPath, final Set<URL> classpathURLs) {
        final String annotationDescription = 'L' + annotationClass.getName().replace('.', '/') + ';';
        final Map<String, URL> fileNames = getMatchingFiles(packageSearchPath, classpathURLs);
        final Collection<ClassAnnotation> list = new ArrayList<ClassAnnotation>();
        for (final Map.Entry<String, URL> entry : fileNames.entrySet()) {
            final String fileName = entry.getKey();
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                Streams.copy(Annotations.class.getResourceAsStream('/' + fileName), out, true);
                final ClassReader cr = new ClassReader(out.toByteArray());
                final Collection<ClassAnnotation> annotations = ClassAnnotationVisitor.getAnnotations(cr, annotationDescription);
                list.addAll(annotations);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
    
    public static Collection<MethodAnnotation> getMethodAnnotations(final Class annotationClass, final String packageSearchPath, final Set<URL> classpathURLs) {
        final String annotationDescription = Type.getType(annotationClass).getDescriptor();
        final Map<String, URL> fileNames = getMatchingFiles(packageSearchPath, classpathURLs);
        final Collection<MethodAnnotation> list = new ArrayList<MethodAnnotation>();
        for (final Map.Entry<String, URL> entry : fileNames.entrySet()) {
            final String fileName = entry.getKey();
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            try {
                Streams.copy(Annotations.class.getResourceAsStream('/' + fileName), out, true);
                final ClassReader cr = new ClassReader(out.toByteArray());
                final Collection<MethodAnnotation> annotations = MethodAnnotationVisitor.getAnnotations(cr, annotationDescription);
                list.addAll(annotations);
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return list;
    }
    
    private static Map<String, URL> getMatchingFiles(String packageSearchPath, final Set<URL> classpathURLs) {
        if (!packageSearchPath.endsWith("/")) {
            packageSearchPath += "/";
        }
        final Pattern pattern = Pattern.compile("(.*).class");
        final Map<String, URL> fileNames = getMatchingFileNames(pattern, classpathURLs);
        for (final String file : fileNames.keySet().toArray(new String[0])) {
            if (!file.startsWith(packageSearchPath)) {
                fileNames.remove(file);
            }
        }
        return fileNames;
    }
    
    static Map<String, URL> getMatchingFileNames(final Pattern pattern, final Collection<URL> urls) {
        final Map<String, URL> names = new HashMap<String, URL>();
        for (URL url : urls) {
            url = fixUrl(url);
            File file;
            try {
                file = new File(URLDecoder.decode(url.getFile(), "UTF-8"));
            }
            catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                System.exit(1);
                return names;
            }
            if (file.isDirectory()) {
                final List<File> files = PatternFileMatcher.getMatchingFiles(file, pattern);
                for (final File f : files) {
                    String path = f.getAbsolutePath();
                    path = path.substring(file.getAbsolutePath().length() + 1);
                    names.put(path, url);
                }
            }
            else {
                if (!file.isFile()) {
                    continue;
                }
                JarFile jarFile = null;
                try {
                    jarFile = new JarFile(file);
                    final Enumeration<JarEntry> entries = jarFile.entries();
                    while (entries.hasMoreElements()) {
                        final JarEntry jarEntry = entries.nextElement();
                        if (pattern.matcher(jarEntry.getName()).matches()) {
                            names.put(jarEntry.getName(), url);
                        }
                    }
                }
                catch (IOException e2) {
                    e2.printStackTrace();
                    System.exit(1);
                }
                finally {
                    if (jarFile != null) {
                        try {
                            jarFile.close();
                        }
                        catch (IOException ex) {}
                    }
                }
            }
        }
        return names;
    }
    
    private static URL fixUrl(URL url) {
        final String protocol = url.getProtocol();
        if ("jar".equals(protocol)) {
            try {
                String urlString = url.toString().substring(4);
                final int index = urlString.indexOf("!/");
                if (index > 0) {
                    urlString = urlString.substring(0, index);
                }
                url = new URL(urlString);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return url;
    }
    
    static URL[] getClasspathURLs() {
        final ClassLoader classLoader = Agent.class.getClassLoader();
        if (classLoader instanceof URLClassLoader) {
            return ((URLClassLoader)classLoader).getURLs();
        }
        return new URL[0];
    }
    
    static class PatternFileMatcher
    {
        private final FileFilter filter;
        private final List<File> files;
        
        public static List<File> getMatchingFiles(final File directory, final Pattern pattern) {
            final PatternFileMatcher matcher = new PatternFileMatcher(pattern);
            directory.listFiles(matcher.filter);
            return matcher.files;
        }
        
        private PatternFileMatcher(final Pattern pattern) {
            this.files = new ArrayList<File>();
            this.filter = new FileFilter() {
                @Override
                public boolean accept(final File f) {
                    if (f.isDirectory()) {
                        f.listFiles(this);
                    }
                    final boolean match = pattern.matcher(f.getAbsolutePath()).matches();
                    if (match) {
                        PatternFileMatcher.this.files.add(f);
                    }
                    return match;
                }
            };
        }
    }
}
