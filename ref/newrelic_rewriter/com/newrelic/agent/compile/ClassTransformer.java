// 
// Decompiled by Procyon v0.5.30
// 

package com.newrelic.agent.compile;

import java.util.regex.Pattern;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.JarInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import com.newrelic.agent.util.Streams;
import java.util.Iterator;
import java.text.MessageFormat;
import java.util.jar.JarFile;
import java.util.Map;
import java.util.ArrayList;
import java.io.File;
import java.util.List;

public final class ClassTransformer
{
    private final Log log;
    public List<File> classes;
    private File inputFile;
    private File outputFile;
    private File transformFile;
    
    public ClassTransformer() {
        final String props = System.getProperty("NewRelic.AgentArgs");
        final Map<String, String> agentOptions = RewriterAgent.parseAgentArgs(props);
        this.log = new SystemErrLog(agentOptions);
        this.classes = new ArrayList<File>();
        this.inputFile = new File(".");
        this.outputFile = new File(".");
        this.transformFile = new File(".");
    }
    
    public ClassTransformer(final File classPath, final File outputDir) {
        this();
        this.classes.add(classPath);
        this.inputFile = classPath;
        this.outputFile = outputDir;
        if (classPath.isDirectory()) {
            this.inputFile = classPath;
        }
    }
    
    public ClassTransformer(final JarFile jarFile, final File outputJar) {
        this();
        final File jar = new File(jarFile.getName());
        this.inputFile = jar.getParentFile();
        this.outputFile = outputJar;
    }
    
    public static void main(final String[] args) {
        final ClassTransformer classTransformer = new ClassTransformer();
        for (int i = 0; i < args.length; ++i) {
            if (args[i].toLowerCase().startsWith("--classpath")) {
                classTransformer.classes = new ArrayList<File>();
                for (final String classFile : args[i].substring("--classpath=".length()).split(";")) {
                    classTransformer.classes.add(new File(classFile));
                }
            }
            else if (args[i].toLowerCase().startsWith("--outputdir=")) {
                classTransformer.outputFile = new File(args[i].substring("--outputdir=".length()));
            }
            else if (args[i].toLowerCase().startsWith("--transformdir=")) {
                classTransformer.transformFile = new File(args[i].substring("--transformdir=".length()));
            }
        }
        classTransformer.inputFile = classTransformer.outputFile;
        classTransformer.doTransform();
    }
    
    protected void doTransform() {
        final long tStart = System.currentTimeMillis();
        this.log.info("[ClassTransformer] Version: " + RewriterAgent.getVersion());
        for (final File classFile : this.classes) {
            this.inputFile = (FileUtils.isClass(classFile) ? classFile.getParentFile() : classFile);
            this.log.info("[ClassTransformer] Transforming classpath[" + classFile.getAbsolutePath() + "]");
            this.log.info("[ClassTransformer] InputFile[" + this.inputFile.getAbsolutePath() + "]");
            this.log.info("[ClassTransformer] OutputFile[" + this.outputFile.getAbsolutePath() + "]");
            this.log.info("[ClassTransformer] TransformFile[" + this.transformFile.getAbsolutePath() + "]");
            this.transformClass(classFile);
        }
        this.log.info(MessageFormat.format("[ClassTransformer] transformer took {0} sec.", (System.currentTimeMillis() - tStart) / 1000.0f));
    }
    
    public byte[] transformClassBytes(final String destClassPath, final byte[] bytes) {
        if (FileUtils.isClass(destClassPath)) {
            try {
                if (bytes != null) {
                    this.log.debug("[ClassTransformer] Writing transformed[" + destClassPath + "]");
                    final File classFile = new File(this.outputFile, destClassPath);
                    classFile.getParentFile().mkdirs();
                    Streams.copyBytesToFile(classFile, bytes);
                }
            }
            catch (IOException e) {
                e.printStackTrace();
                this.log.error("[ClassTransformer] " + e);
            }
        }
        return bytes;
    }
    
    private ByteArrayInputStream processClassBytes(final File file, final InputStream inStrm) throws IOException {
        final byte[] classBytes = Streams.slurpBytes(inStrm);
        final byte[] rewrittenClassBytes = this.transformClassBytes(file.getPath(), classBytes);
        ByteArrayInputStream byteStream = new ByteArrayInputStream(classBytes);
        if (rewrittenClassBytes != null) {
            byteStream = new ByteArrayInputStream(rewrittenClassBytes);
        }
        return byteStream;
    }
    
    public boolean transformClass(final File file) {
        boolean didProcessClass = false;
        try {
            if (FileUtils.isArchive(file)) {
                didProcessClass = this.transformArchive(file);
            }
            else if (file.isDirectory()) {
                didProcessClass = this.transformDirectory(file);
            }
            else if (FileUtils.isClass(file)) {
                String classpath = file.getAbsolutePath();
                if (classpath.startsWith(this.inputFile.getAbsolutePath())) {
                    classpath = classpath.substring(this.inputFile.getAbsolutePath().length() + 1);
                }
                final ByteArrayInputStream byteStream = this.processClassBytes(new File(classpath), new FileInputStream(file));
                try {
                    final File transformedClass = new File(this.outputFile, classpath);
                    transformedClass.getParentFile().mkdirs();
                    Streams.copy(byteStream, new FileOutputStream(transformedClass));
                    didProcessClass = true;
                }
                catch (Exception e) {
                    this.log.error("[ClassTransformer] transformClass: " + e);
                    didProcessClass = false;
                }
                finally {
                    byteStream.close();
                }
            }
            else {
                this.log.debug("[ClassTransformer] Class ignored: " + file.getName());
            }
        }
        catch (Exception e2) {
            e2.printStackTrace();
            System.err.print(e2);
        }
        return didProcessClass;
    }
    
    public boolean transformDirectory(final File directory) {
        boolean didProcessDirectory = false;
        if (directory.isDirectory()) {
            for (final File f : directory.listFiles()) {
                didProcessDirectory |= this.transformClass(f);
            }
        }
        return didProcessDirectory;
    }
    
    public boolean transformArchive(final File archiveFile) throws IOException {
        boolean didProcessArchive = true;
        if (this.isSupportJar(archiveFile)) {
            this.log.debug("[ClassTransformer] Skipping support jar [" + archiveFile.getPath() + "]");
            return false;
        }
        this.log.debug("[ClassTransformer] Transforming archive[" + archiveFile.getCanonicalPath() + "]");
        final ByteArrayOutputStream byteStrm = new ByteArrayOutputStream();
        final JarInputStream jarInStrm = new JarInputStream(new FileInputStream(archiveFile));
        final JarOutputStream jarOutStream = new JarOutputStream(byteStrm);
        final JarFile jarFile = new JarFile(archiveFile);
        try {
            final JarEntry manifest = new JarEntry("META-INF/MANIFEST.MF");
            jarOutStream.putNextEntry(manifest);
            final Manifest realManifest = jarFile.getManifest();
            if (realManifest != null) {
                realManifest.write(jarOutStream);
            }
            jarOutStream.flush();
            jarOutStream.closeEntry();
            for (JarEntry entry = jarInStrm.getNextJarEntry(); entry != null; entry = jarInStrm.getNextJarEntry()) {
                final String jarEntryPath = entry.getName();
                if (!entry.isDirectory() && FileUtils.isClass(jarEntryPath)) {
                    final JarEntry jarEntry = new JarEntry(jarEntryPath);
                    final InputStream inputStrm = jarFile.getInputStream(entry);
                    final File archiveClass = new File(jarEntryPath);
                    jarEntry.setTime(entry.getTime());
                    jarOutStream.putNextEntry(jarEntry);
                    final ByteArrayInputStream byteStream = this.processClassBytes(archiveClass, inputStrm);
                    try {
                        Streams.copy(byteStream, jarOutStream);
                        didProcessArchive = true;
                    }
                    catch (Exception e) {
                        this.log.error("[ClassTransformer] transformArchive: " + e);
                        didProcessArchive = false;
                    }
                    finally {
                        byteStream.close();
                    }
                    jarOutStream.flush();
                    jarOutStream.closeEntry();
                }
            }
            if (didProcessArchive) {
                jarOutStream.close();
            }
        }
        finally {
            jarFile.close();
            jarOutStream.close();
            jarInStrm.close();
        }
        return didProcessArchive;
    }
    
    private boolean isSupportJar(final File archiveFile) {
        boolean matches = false;
        try {
            final CharSequence canonicalPath = archiveFile.getCanonicalPath().toLowerCase();
            matches |= Pattern.matches("^.*\\/jre\\/lib\\/rt\\.jar$", canonicalPath);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return matches;
    }
    
    private static final class FileUtils
    {
        public static boolean isArchive(final String fileName) {
            final String lowerPath = fileName.toLowerCase();
            return lowerPath.endsWith(".zip") || lowerPath.endsWith(".jar") || lowerPath.endsWith(".aar");
        }
        
        public static boolean isArchive(final File f) {
            return isArchive(f.getAbsolutePath());
        }
        
        public static boolean isClass(final String fileName) {
            final String lowerPath = fileName.toLowerCase();
            return lowerPath.endsWith(".class");
        }
        
        public static boolean isClass(final File f) {
            return isClass(f.getAbsolutePath());
        }
    }
}
