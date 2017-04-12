package com.hello2mao.openapm.rewriter;

import com.hello2mao.openapm.rewriter.util.LOG;

import java.io.IOException;
import java.util.Map;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.NotFoundException;


public class Rewriter {

    private static ClassPool pool = javassist.ClassPool.getDefault();
    private static String injectStr = "System.out.println(\"I Love MHB\" ); ";

    public static void injectDir(String path, Map<String, String> params) {
        LOG.debug("Rewriter injectDir: " + path);

        try {
            pool.appendClassPath(path);
            CtClass c = pool.getCtClass("com.hello2mao.openapm.sample.MainActivity");
            if (c.isFrozen()) {
                c.defrost();
            }

            CtConstructor[] cts = c.getDeclaredConstructors();
            if (cts == null || cts.length == 0) {
                //手动创建一个构造函数
                CtConstructor constructor = new CtConstructor(new CtClass[0], c);
                constructor.insertBeforeBody(injectStr);
                c.addConstructor(constructor);
            } else {
                cts[0].insertBeforeBody(injectStr);
            }
            c.writeFile(path);
            c.detach();
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (CannotCompileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static boolean needSkip(String filePath) {
        return !filePath.endsWith(".class") || filePath.contains("R$")
                || filePath.contains("R.class")
                || filePath.contains("BuildConfig.class");
    }
}
