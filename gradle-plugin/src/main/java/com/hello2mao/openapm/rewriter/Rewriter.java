package com.hello2mao.openapm.rewriter;

import com.hello2mao.openapm.rewriter.util.FileUtil;
import com.hello2mao.openapm.rewriter.util.Log;
import com.hello2mao.openapm.rewriter.visitor.ActivityClassVisitor;
import com.hello2mao.openapm.rewriter.visitor.OpenAPMClassVisitor;
import com.hello2mao.openapm.rewriter.visitor.PrefilterClassVisitor;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.Map;


public class Rewriter {

    private volatile static Rewriter instance = null;
    private Log log = new Log();
    private ClassRemapperConfig config;
    private InstrumentationContext context;

    private Rewriter() {
//        try {
//            this.config = new ClassRemapperConfig(log);
//        } catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
        this.context = new InstrumentationContext(config, log);
    }

    public static Rewriter getInstance() {
        if (instance == null) {
            synchronized (Rewriter.class) {
                if (instance == null) {
                    instance = new Rewriter();
                }
            }
        }
        return instance;
    }

    /**
     * 代码注入的入口
     * @param filePath String
     * @param rewriterOptions Map<String, String>
     */
    public void injectFile(String filePath, Map<String, String> rewriterOptions) {
        if (needSkip(filePath)) {
            return;
        }
        try {
            byte[] bytes = FileUtil.readFileToByteArray(filePath);
            log.debug("Try to Inject file: " + filePath + "fileSize: " + bytes.length + "bytes");
            synchronized (context) {
                ClassData classData = visitClassBytes(bytes);
                // class被修改过，则重新写入文件
                if (classData != null && classData.getMainClassBytes() != null && classData.isModified()) {
                    if (bytes.length != classData.getMainClassBytes().length) {
                        log.debug("ClassTransformer transformed bytes[" + classData.getMainClassBytes().length + "]");
                    }
                    FileUtil.writeByteArrayToFile(filePath, classData.getMainClassBytes());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ClassData visitClassBytes(byte[] bytes) {
        String className = "an unknown class";
        try {
            ClassReader cr = new ClassReader(bytes);
            ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_MAXS);
            context.reset();
            int classReaderFlags = ClassReader.SKIP_CODE + ClassReader.SKIP_DEBUG + ClassReader.SKIP_FRAMES;
            cr.accept(new PrefilterClassVisitor(context, log), classReaderFlags);
            className = context.getClassName();
            if (context.hasTag("Lcom/hello2mao/openapm/agent/instrumentation/Instrumented;")) {
                log.warning(MessageFormat.format("[{0}] class is already instrumented! skipping ...",
                        context.getFriendlyClassName()));
            } else {
                ClassVisitor cv = cw;
                if (context.getClassName().startsWith("com/hello2mao/openapm/agent")) {
                    // Agent isInstrumented = true
                    cv = new OpenAPMClassVisitor(cv, context, log);
                } else if (context.getClassName().startsWith("android/support/")) {
                    cv = new ActivityClassVisitor(cv, context, log);
                } else {
                    if (isExcludedPackage(context.getClassName())) {
                        return null;
                    }
//                    cv = new AnnotatingClassVisitor(cv, context, log);
//                    cv = new ActivityClassVisitor(cv, context, log);
//                    cv = new AsyncTaskClassVisitor(cv, context, log);
//                    cv = new TraceAnnotationClassVisitor(cv, context, log);
//                    cv = new WrapMethodClassVisitor(cv, context, log);
                }
                cr.accept(cv, ClassReader.SKIP_FRAMES + ClassReader.EXPAND_FRAMES);
            }
            return context.newClassData(cw.toByteArray());
        } catch (SkipException ex) {
            return null;
        } catch (HaltBuildException e) {
            throw new RuntimeException(e);
        } catch (Throwable t) {
            log.error("Unfortunately, an error has occurred while processing " + className, t);
            t.printStackTrace();
            return new ClassData(bytes, false);
        }
    }

    private boolean isExcludedPackage(final String packageName) {
        return false;
    }

    private boolean needSkip(String filePath) {
        return !filePath.endsWith(".class") || filePath.contains("R$") || filePath.contains("R.class") ||
                filePath.contains("BuildConfig.class");
    }
}
