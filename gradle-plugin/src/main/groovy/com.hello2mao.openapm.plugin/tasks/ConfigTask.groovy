package com.hello2mao.openapm.plugin.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

public class ConfigTask extends DefaultTask {

    @TaskAction
    void getConfig() {
        println "param1 is ${project.OpenAPMConfig.param1}"
        println "param2 is ${project.OpenAPMConfig.param2}"
        println "param3 is ${project.OpenAPMConfig.param3}"
    }
}