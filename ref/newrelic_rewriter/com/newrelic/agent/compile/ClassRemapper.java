// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.compile;

import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import com.newrelic.agent.compile.visitor.ContextInitializationClassVisitor;
import com.newrelic.agent.compile.visitor.WrapMethodClassVisitor;
import com.newrelic.agent.compile.visitor.AnnotatingClassVisitor;
import com.newrelic.agent.compile.visitor.ActivityClassVisitor;
import com.newrelic.agent.compile.visitor.NewRelicClassVisitor;
import com.newrelic.org.objectweb.asm.ClassVisitor;
import com.newrelic.agent.compile.visitor.PrefilterClassVisitor;
import com.newrelic.org.objectweb.asm.ClassWriter;
import com.newrelic.org.objectweb.asm.ClassReader;
import java.io.FileOutputStream;
import java.text.MessageFormat;
import java.io.ByteArrayInputStream;
import com.newrelic.agent.util.Streams;
import java.io.BufferedInputStream;
import java.util.zip.ZipEntry;
import java.util.jar.JarEntry;
import java.io.OutputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.io.ByteArrayOutputStream;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.io.InputStream;
import java.util.Enumeration;
import java.net.URL;
import java.util.Properties;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.io.File;

public class ClassRemapper
{
    public static final String NEW_RELIC_APP_VERSION = "New-Relic-App-Version";
    public static final String NEW_RELIC_VERSION_MARKER = "New-Relic-Version";
    private static final String NEW_RELIC_BACKUP_HASH_MARKER = "New-Relic-Backup-Hash";
    private static final String MANIFEST_MF_PATH = "META-INF/MANIFEST.MF";
    private final Log log;
    private final File outputDirectory;
    private final File jarBackupDirectory;
    private final ClassRemapperConfig config;
    private String appVersion;
    private final InstrumentationContext context;
    volatile int modificationCount;
    public static final HashSet<String> EXCLUDED_PACKAGES;
    
    public ClassRemapper(final File directory) throws FileNotFoundException, IOException, ClassNotFoundException {
        this(directory, null);
    }
    
    public ClassRemapper(final File directory, final File jarBackupDirectory) throws FileNotFoundException, IOException, ClassNotFoundException {
        this(new DefaultLogImpl(), ClassLoader.getSystemClassLoader(), directory, jarBackupDirectory);
    }
    
    public ClassRemapper(Log log, final ClassLoader classLoader, final File directory, final File jarBackupDirectory) throws FileNotFoundException, IOException, ClassNotFoundException {
        if (log == null) {
            log = new DefaultLogImpl();
        }
        this.log = log;
        this.outputDirectory = directory;
        this.jarBackupDirectory = jarBackupDirectory;
        this.config = new ClassRemapperConfig(log);
        this.context = new InstrumentationContext(this.config, log);
        final Enumeration<URL> manifests = this.getClass().getClassLoader().getResources("META-INF/MANIFEST.MF");
        while (manifests.hasMoreElements()) {
            final Properties props = new Properties();
            final InputStream stream = manifests.nextElement().openStream();
            try {
                props.load(stream);
                this.appVersion = props.getProperty("New-Relic-App-Version");
                if (this.appVersion != null) {
                    break;
                }
                continue;
            }
            catch (IOException e) {}
            finally {
                stream.close();
            }
        }
        if (this.appVersion == null) {
            throw new FileNotFoundException("Could not find MANIFEST.MF with New-Relic-App-Version");
        }
    }
    
    public void rewriteClasses(final File dir) {
        if (!dir.isDirectory()) {
            throw new RuntimeException("Expected " + dir.getAbsolutePath() + " to be a directory");
        }
        for (final File file : dir.listFiles()) {
            if (file.isDirectory()) {
                this.rewriteClasses(file);
            }
            else if (file.getName().endsWith(".class")) {
                try {
                    this.rewriteClass(file);
                }
                catch (Throwable ex) {
                    this.log.error(ex.getMessage(), ex);
                }
            }
        }
    }
    
    public void rewriteJars(final File dir, final boolean recurse) {
        if (!dir.isDirectory()) {
            throw new RuntimeException("Expected " + dir.getAbsolutePath() + " to be a directory");
        }
        for (final File file : dir.listFiles()) {
            if (file.isDirectory() && recurse) {
                this.rewriteJars(dir, recurse);
            }
            else if (file.getName().endsWith(".jar")) {
                try {
                    this.rewriteJar(file);
                }
                catch (Throwable ex) {
                    this.log.error(ex.getMessage(), ex);
                }
            }
        }
    }
    
    public boolean rewriteJar(final File file) throws Exception {
        return this.rewriteJar(file, 0);
    }
    
    private String generateHash(final File file) throws Exception {
        final MessageDigest messageDigest = MessageDigest.getInstance("SHA");
        final FileInputStream fin = new FileInputStream(file);
        try {
            final byte[] buf = new byte[8192];
            while (true) {
                final int n = fin.read(buf);
                if (n <= 0) {
                    break;
                }
                messageDigest.update(buf, 0, n);
            }
            final StringBuilder sb = new StringBuilder();
            final byte[] digest = messageDigest.digest();
            for (int i = 0; i < digest.length; ++i) {
                sb.append(Integer.toHexString(digest[i] & 0xFF));
            }
            return sb.toString();
        }
        finally {
            fin.close();
        }
    }
    
    private boolean rewriteJar(final File file, final int depth) throws Exception {
        if (Pattern.matches("^android-support-v[^\\.]+\\.jar$", file.getName())) {
            this.log.info("skipping android support jar: " + file.getPath());
            return false;
        }
        this.log.debug("process jar file: " + file.getPath());
        if (this.jarBackupDirectory == null) {
            this.log.error("no jar backup directory specified! exiting ...");
            System.exit(1);
        }
        final JarFile jarFile = new JarFile(file);
        final ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        boolean anyModified = false;
        try {
            Manifest manifest = jarFile.getManifest();
            if (manifest == null) {
                this.log.debug("creating a manifest file");
                manifest = new Manifest();
                manifest.getMainAttributes().putValue("New-Relic-Version", this.appVersion);
                manifest.getMainAttributes().putValue("New-Relic-Backup-Hash", this.generateHash(file));
            }
            else if (manifest.getMainAttributes().getValue("New-Relic-Version") != null) {
                this.log.warning("jar has already been instrumented by New Relic: " + file);
                return false;
            }
            final JarOutputStream jos = new JarOutputStream(bytes);
            final Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                final JarEntry entry = entries.nextElement();
                final InputStream is = new BufferedInputStream(jarFile.getInputStream(entry));
                try {
                    if (entry.getName().equals("META-INF/MANIFEST.MF")) {
                        final JarEntry newEntry = new JarEntry(entry.getName());
                        jos.putNextEntry(newEntry);
                        manifest.write(jos);
                        jos.closeEntry();
                        continue;
                    }
                    final JarEntry newEntry = new JarEntry(entry.getName());
                    jos.putNextEntry(newEntry);
                    if (entry.getName().endsWith(".class")) {
                        final ByteArrayOutputStream classBytes = new ByteArrayOutputStream();
                        Streams.copy(is, classBytes);
                        classBytes.close();
                        final ClassData classData = this.visitClassBytes(classBytes.toByteArray());
                        if (classData != null && classData.getMainClassBytes() != null && classData.isModified()) {
                            final ByteArrayInputStream byteStream = new ByteArrayInputStream(classData.getMainClassBytes());
                            try {
                                Streams.copy(byteStream, jos);
                            }
                            finally {
                                byteStream.close();
                            }
                            anyModified |= classData.isModified();
                            if (classData.isModified()) {
                                ++this.modificationCount;
                            }
                            if (classData.isShimPresent()) {
                                jos.closeEntry();
                                final JarEntry shimEntry = new JarEntry(classData.getShimClassName() + ".class");
                                jos.putNextEntry(shimEntry);
                                final ByteArrayInputStream shimByteStream = new ByteArrayInputStream(classData.getShimClassBytes());
                                try {
                                    Streams.copy(shimByteStream, jos);
                                }
                                finally {
                                    shimByteStream.close();
                                }
                            }
                        }
                    }
                    else {
                        Streams.copy(is, jos);
                    }
                }
                finally {
                    is.close();
                }
                jos.flush();
                jos.closeEntry();
            }
            jos.close();
        }
        finally {
            jarFile.close();
        }
        if (anyModified) {
            final File jarF = new File(this.outputDirectory, file.getName());
            jarF.getParentFile().mkdirs();
            this.log.info("Rewritten jar file: " + jarF.getCanonicalPath());
            Streams.copyBytesToFile(jarF, bytes.toByteArray());
            return true;
        }
        this.log.debug("no classes modified: jar will not be written");
        return false;
    }
    
    public boolean rewriteClass(final File file) throws Exception {
        if (this.outputDirectory == null) {
            throw new RuntimeException("No output directory specified when attempting to process " + file.getAbsolutePath());
        }
        if (file.getAbsolutePath().contains("com/newrelic") || file.getAbsolutePath().endsWith("$$NewRelicShim$$1.class")) {
            return false;
        }
        final ClassData classData = this.visitClassBytes(this.getBytes(file));
        if (classData.getMainClassBytes() != null && classData.isModified()) {
            this.log.info(MessageFormat.format("[{0}] modified classfile {1}", this.context.getClassName().replaceAll("/", "."), file.getName()));
            Streams.copyBytesToFile(file, classData.getMainClassBytes());
            if (classData.isShimPresent()) {
                final FileOutputStream out = new FileOutputStream(this.outputDirectory.getAbsolutePath() + File.separator + classData.getShimClassName() + ".class");
                try {
                    out.write(classData.getShimClassBytes());
                }
                finally {
                    out.close();
                }
            }
            return true;
        }
        return false;
    }
    
    private byte[] getBytes(final File file) throws Exception {
        final ByteArrayOutputStream originalBytes = new ByteArrayOutputStream((int)file.length());
        InputStream inStream = null;
        try {
            inStream = new BufferedInputStream(new FileInputStream(file));
            Streams.copy(inStream, originalBytes, true);
        }
        catch (Throwable t) {
            throw new Exception("Unable to read file: " + file.getName(), t);
        }
        return originalBytes.toByteArray();
    }
    
    private ClassData visitClassBytes(final byte[] bytes) {
        try {
            final ClassReader cr = new ClassReader(bytes);
            final ClassWriter cw = new ClassWriter(cr, 1);
            this.context.reset();
            cr.accept(new PrefilterClassVisitor(this.context, this.log), 7);
            if (!this.context.hasTag("Lcom/newrelic/agent/android/instrumentation/Instrumented;")) {
                ClassVisitor cv = cw;
                if (this.context.getClassName().startsWith("com/newrelic/agent/android")) {
                    cv = new NewRelicClassVisitor(cv, this.context, this.log);
                }
                else if (this.context.getClassName().startsWith("android/support/")) {
                    cv = new ActivityClassVisitor(cv, this.context, this.log);
                }
                else {
                    cv = new AnnotatingClassVisitor(cw, this.context, this.log);
                    cv = new WrapMethodClassVisitor(cv, this.context, this.log);
                    cv = new ContextInitializationClassVisitor(cv, this.context);
                }
                cv = new ContextInitializationClassVisitor(cv, this.context);
                cr.accept(cv, 12);
            }
            else {
                this.log.warning(MessageFormat.format("[{0}] class is already instrumented! skipping ...", this.context.getFriendlyClassName()));
            }
            return this.context.newClassData(cw.toByteArray());
        }
        catch (Throwable t) {
            this.log.error(t.getMessage(), t);
            return new ClassData(bytes, false);
        }
    }
    
    private boolean isExcludedPackage(final String packageName) {
        for (final String name : ClassRemapper.EXCLUDED_PACKAGES) {
            if (packageName.contains(name)) {
                return true;
            }
        }
        return false;
    }
    
    static {
        EXCLUDED_PACKAGES = new HashSet<String>() {
            {
                this.add("com/newrelic/agent/android");
                this.add("com/google/gson");
                this.add("com/squareup/okhttp");
            }
        };
    }
    
    private static final class DefaultLogImpl extends Log
    {
        public DefaultLogImpl() {
            super(new HashMap<String, String>());
        }
        
        @Override
        protected void log(final String level, final String message) {
            System.err.println("[newrelic." + level.toLowerCase() + "] " + message);
        }
        
        @Override
        public void warning(final String message, final Throwable cause) {
            this.log("warn", message);
            cause.printStackTrace(System.err);
        }
        
        @Override
        public void error(final String message, final Throwable cause) {
            this.log("error", message);
            cause.printStackTrace(System.err);
        }
    }
}
