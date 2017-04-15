// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.org.reflections.vfs;

import java.io.InputStream;
import java.net.URLConnection;
import java.net.JarURLConnection;
import java.util.jar.JarFile;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URISyntaxException;
import java.io.File;
import com.newrelic.com.google.common.collect.Iterables;
import java.util.ArrayList;
import com.newrelic.org.reflections.util.Utils;
import com.newrelic.com.google.common.base.Predicate;
import java.util.Collection;
import com.newrelic.com.google.common.collect.Lists;
import java.util.Iterator;
import com.newrelic.org.reflections.ReflectionsException;
import com.newrelic.org.reflections.Reflections;
import java.net.URL;
import java.util.List;

public abstract class Vfs
{
    private static List<UrlType> defaultUrlTypes;
    
    public static List<UrlType> getDefaultUrlTypes() {
        return Vfs.defaultUrlTypes;
    }
    
    public static void setDefaultURLTypes(final List<UrlType> urlTypes) {
        Vfs.defaultUrlTypes = urlTypes;
    }
    
    public static void addDefaultURLTypes(final UrlType urlType) {
        Vfs.defaultUrlTypes.add(urlType);
    }
    
    public static Dir fromURL(final URL url) {
        return fromURL(url, Vfs.defaultUrlTypes);
    }
    
    public static Dir fromURL(final URL url, final List<UrlType> urlTypes) {
        for (final UrlType type : urlTypes) {
            try {
                if (!type.matches(url)) {
                    continue;
                }
                final Dir dir = type.createDir(url);
                if (dir != null) {
                    return dir;
                }
                continue;
            }
            catch (Throwable e) {
                if (Reflections.log == null) {
                    continue;
                }
                Reflections.log.warn("could not create Dir using " + type + " from url " + url.toExternalForm() + ". skipping.", e);
            }
        }
        throw new ReflectionsException("could not create Vfs.Dir from url, no matching UrlType was found [" + url.toExternalForm() + "]\n" + "either use fromURL(final URL url, final List<UrlType> urlTypes) or " + "use the static setDefaultURLTypes(final List<UrlType> urlTypes) or addDefaultURLTypes(UrlType urlType) " + "with your specialized UrlType.");
    }
    
    public static Dir fromURL(final URL url, final UrlType... urlTypes) {
        return fromURL(url, Lists.newArrayList(urlTypes));
    }
    
    public static Iterable<File> findFiles(final Collection<URL> inUrls, final String packagePrefix, final Predicate<String> nameFilter) {
        final Predicate<File> fileNamePredicate = new Predicate<File>() {
            public boolean apply(final File file) {
                final String path = file.getRelativePath();
                if (path.startsWith(packagePrefix)) {
                    final String filename = path.substring(path.indexOf(packagePrefix) + packagePrefix.length());
                    return !Utils.isEmpty(filename) && nameFilter.apply(filename.substring(1));
                }
                return false;
            }
        };
        return findFiles(inUrls, fileNamePredicate);
    }
    
    public static Iterable<File> findFiles(final Collection<URL> inUrls, final Predicate<File> filePredicate) {
        Iterable<File> result = new ArrayList<File>();
        for (final URL url : inUrls) {
            try {
                result = Iterables.concat((Iterable<? extends File>)result, (Iterable<? extends File>)Iterables.filter((Iterable<? extends T>)new Iterable<File>() {
                    public Iterator<File> iterator() {
                        return Vfs.fromURL(url).getFiles().iterator();
                    }
                }, (Predicate<? super T>)filePredicate));
            }
            catch (Throwable e) {
                if (Reflections.log == null) {
                    continue;
                }
                Reflections.log.error("could not findFiles for url. continuing. [" + url + "]", e);
            }
        }
        return result;
    }
    
    static java.io.File getFile(final URL url) {
        try {
            final String path = url.toURI().getSchemeSpecificPart();
            final java.io.File file;
            if ((file = new java.io.File(path)).exists()) {
                return file;
            }
        }
        catch (URISyntaxException ex) {}
        try {
            String path = URLDecoder.decode(url.getPath(), "UTF-8");
            if (path.contains(".jar!")) {
                path = path.substring(0, path.lastIndexOf(".jar!") + ".jar".length());
            }
            final java.io.File file;
            if ((file = new java.io.File(path)).exists()) {
                return file;
            }
        }
        catch (UnsupportedEncodingException ex2) {}
        try {
            String path = url.toExternalForm();
            if (path.startsWith("jar:")) {
                path = path.substring("jar:".length());
            }
            if (path.startsWith("file:")) {
                path = path.substring("file:".length());
            }
            if (path.contains(".jar!")) {
                path = path.substring(0, path.indexOf(".jar!") + ".jar".length());
            }
            java.io.File file;
            if ((file = new java.io.File(path)).exists()) {
                return file;
            }
            path = path.replace("%20", " ");
            if ((file = new java.io.File(path)).exists()) {
                return file;
            }
        }
        catch (Exception ex3) {}
        return null;
    }
    
    static {
        Vfs.defaultUrlTypes = (List<UrlType>)Lists.newArrayList(DefaultUrlTypes.values());
    }
    
    public enum DefaultUrlTypes implements UrlType
    {
        jarFile {
            public boolean matches(final URL url) {
                return url.getProtocol().equals("file") && url.toExternalForm().contains(".jar");
            }
            
            public Dir createDir(final URL url) throws Exception {
                return new ZipDir(new JarFile(Vfs.getFile(url)));
            }
        }, 
        jarUrl {
            public boolean matches(final URL url) {
                return "jar".equals(url.getProtocol());
            }
            
            public Dir createDir(final URL url) throws Exception {
                final URLConnection urlConnection = url.openConnection();
                return (urlConnection instanceof JarURLConnection) ? new ZipDir(((JarURLConnection)urlConnection).getJarFile()) : null;
            }
        }, 
        directory {
            public boolean matches(final URL url) {
                return url.getProtocol().equals("file") && !url.toExternalForm().contains(".jar");
            }
            
            public Dir createDir(final URL url) throws Exception {
                return new SystemDir(Vfs.getFile(url));
            }
        };
    }
    
    public interface UrlType
    {
        boolean matches(final URL p0) throws Exception;
        
        Dir createDir(final URL p0) throws Exception;
    }
    
    public interface Dir
    {
        String getPath();
        
        Iterable<File> getFiles();
        
        void close();
    }
    
    public interface File
    {
        String getName();
        
        String getRelativePath();
        
        InputStream openInputStream() throws IOException;
    }
}
