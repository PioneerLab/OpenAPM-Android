package com.hello2mao.openapm.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.hello2mao.openapm.plugin.tasks.ConfigTask
import org.gradle.api.Plugin
import org.gradle.api.Project

public class PluginImpl implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println "==OpenAPM-Plugin== Plugin loaded!"
        project.gradle.addListener(new TaskListener())
        project.extensions.create('OpenAPMConfig', OpenAPMConfig)
        project.task('ConfigTask', type: ConfigTask)
        /**
         * 注册transform接口
         *
         * <p>
         * ref: http://blog.csdn.net/sbsujjbcy/article/details/50839263
         */
        def isApp = project.plugins.hasPlugin(AppPlugin)
        if (isApp) {
            def android = project.extensions.getByType(AppExtension)
            def transform = new TransformImpl(project)
            android.registerTransform(transform)
        }
    }
}