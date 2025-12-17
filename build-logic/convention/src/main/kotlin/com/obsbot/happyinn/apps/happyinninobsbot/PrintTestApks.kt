/*
 * Copyright 2022 The Android Open Source Project
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       https://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package com.obsbot.happyinn.apps.happyinninobsbot

import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.BuiltArtifactsLoader
import com.android.build.api.variant.HasAndroidTest
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.assign
import org.gradle.work.DisableCachingByDefault
import java.io.File

/**
 * 配置打印测试APK位置的任务
 *
 * @param extension Android组件扩展，用于访问变体信息
 */
internal fun Project.configurePrintApksTask(extension: AndroidComponentsExtension<*, *, *>) {
    // 为每个变体配置任务
    extension.onVariants { variant ->
        // 只处理包含Android测试的变体
        if (variant is HasAndroidTest) {
            // 获取构建产物加载器
            val loader = variant.artifacts.getBuiltArtifactsLoader()
            // 获取Android测试的APK构件
            val artifact = variant.androidTest?.artifacts?.get(SingleArtifact.APK)
            // 获取Java源码目录
            val javaSources = variant.androidTest?.sources?.java?.all
            // 获取Kotlin源码目录
            val kotlinSources = variant.androidTest?.sources?.kotlin?.all

            // 合并Java和Kotlin源码目录
            val testSources = if (javaSources != null && kotlinSources != null) {
                javaSources.zip(kotlinSources) { javaDirs, kotlinDirs ->
                    javaDirs + kotlinDirs
                }
            } else javaSources ?: kotlinSources

            // 如果APK构件和源码都存在，则注册任务
            if (artifact != null && testSources != null) {
                tasks.register(
                    "${variant.name}PrintTestApk",  // 任务名称
                    PrintApkLocationTask::class.java, // 任务类
                ) {
                    apkFolder = artifact                    // 设置APK文件夹
                    builtArtifactsLoader = loader          // 设置构建产物加载器
                    variantName = variant.name             // 设置变体名称
                    sources = testSources                  // 设置源码目录
                }
            }
        }
    }
}

/**
 * 打印APK位置的任务类
 * 禁用缓存因为该任务主要用于输出信息
 */
@DisableCachingByDefault(because = "Prints output")
internal abstract class PrintApkLocationTask : DefaultTask() {

    /**
     * APK文件夹输入目录
     * 使用相对路径敏感性以提高缓存效率
     */
    @get:PathSensitive(PathSensitivity.RELATIVE)
    @get:InputDirectory
    abstract val apkFolder: DirectoryProperty

    /**
     * 源码目录输入文件列表
     * 使用相对路径敏感性以提高缓存效率
     */
    @get:PathSensitive(PathSensitivity.RELATIVE)
    @get:InputFiles
    abstract val sources: ListProperty<Directory>

    /**
     * 构建产物加载器（内部使用，不作为输入）
     */
    @get:Internal
    abstract val builtArtifactsLoader: Property<BuiltArtifactsLoader>

    /**
     * 变体名称输入
     */
    @get:Input
    abstract val variantName: Property<String>

    /**
     * 任务执行动作
     * 检查是否有测试源文件，如果有则打印APK位置
     */
    @TaskAction
    fun taskAction() {
        // 检查是否存在非生成目录的源文件
        val hasFiles = sources.orNull?.any { directory ->
            directory.asFileTree.files.any {
                it.isFile && "build${File.separator}generated" !in it.parentFile.path
            }
        } ?: throw RuntimeException("Cannot check androidTest sources")

        // 如果没有androidTest源文件，则不打印APK位置
        if (!hasFiles) return

        // 加载构建的APK构件
        val builtArtifacts = builtArtifactsLoader.get().load(apkFolder.get())
            ?: throw RuntimeException("Cannot load APKs")
        // 确保只有一个APK
        if (builtArtifacts.elements.size != 1)
            throw RuntimeException("Expected one APK !")
        // 获取APK文件路径并打印
        val apk = File(builtArtifacts.elements.single().outputFile).toPath()
        println(apk)
    }
}
