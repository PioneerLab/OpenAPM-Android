package com.hello2mao.openapm.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.gradle.api.Project
import com.hello2mao.openapm.rewriter.Rewriter

public class TransformImpl extends Transform {

    private Project project

    // 构造函数，我们将Project保存下来备用
    public TransformImpl(Project project) {
        this.project = project
    }

    // 设置我们自定义的Transform对应的Task名称
    // 类似：TransformClassesWithPreDexForXXX
    @Override
    String getName() {
        return "OpenAPM"
    }

    // 指定输入的类型，通过这里的设定，可以指定我们要处理的文件类型
    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    // 指定Transform的作用范围
    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }


    @Override
    boolean isIncremental() {
        return false;
    }


    @Override
    void transform(Context context,
                   Collection<TransformInput> inputs,
                   Collection<TransformInput> referencedInputs,
                   TransformOutputProvider outputProvider,
                   boolean isIncremental)
            throws IOException, TransformException, InterruptedException {
        // Transform的inputs有两种类型，一种是目录，一种是jar包，要分开遍历
        inputs.each {TransformInput input ->

            //对类型为“文件夹”的input进行遍历
            input.directoryInputs.each {DirectoryInput directoryInput->
                //文件夹里面包含的是我们手写的类以及R.class、BuildConfig.class以及R$XXX.class等

                //TODO 注入代码
                Rewriter.injectDir(directoryInput.file.absolutePath, null)

                // 获取output目录
                File dest = outputProvider.getContentLocation(directoryInput.name,
                        directoryInput.contentTypes, directoryInput.scopes,
                        Format.DIRECTORY)
                String buildTypes = directoryInput.file.name
                String productFlavors = directoryInput.file.parentFile.name
                //这里进行我们的处理 TODO
                project.logger.error "Copying ${directoryInput.name} to ${dest.absolutePath}"
                // 将input的目录复制到output指定目录
                FileUtils.copyDirectory(directoryInput.file, dest)
            }

            //对类型为jar文件的input进行遍历
            input.jarInputs.each {JarInput jarInput->
                //jar文件一般是第三方依赖库jar文件

                //TODO 注入代码
//                String jarPath = jarInput.file.absolutePath;
//                String projectName = project.rootProject.name;
//                if(jarPath.endsWith("classes.jar")
//                        && jarPath.contains("exploded-aar\\"+projectName)
//                        // hotpatch module是用来加载dex，无需注入代码
//                        && !jarPath.contains("exploded-aar\\"+projectName+"\\hotpatch")) {
//                    Inject.injectJar(jarPath)
//                }


                // 重命名输出文件（同目录copyFile会冲突）
                def jarName = jarInput.name
                def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
                if(jarName.endsWith(".jar")) {
                    jarName = jarName.substring(0,jarName.length()-4)
                }
                //生成输出路径
                File dest = outputProvider.getContentLocation(jarName + md5Name,
                        jarInput.contentTypes, jarInput.scopes, Format.JAR)
                //处理jar进行字节码注入处理TODO
                project.logger.error "Copying ${jarInput.file.absolutePath} to ${dest.absolutePath}"
                //将输入内容复制到输出
                FileUtils.copyFile(jarInput.file, dest)
            }
        }
    }

}