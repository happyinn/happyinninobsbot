/*
 * Copyright 2024 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.obsbot.happyinn.apps.happyinninobsbot

import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.AndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import com.android.build.api.variant.SourceDirectories
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.testing.Test
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.withType
import org.gradle.testing.jacoco.plugins.JacocoPluginExtension
import org.gradle.testing.jacoco.plugins.JacocoTaskExtension
import org.gradle.testing.jacoco.tasks.JacocoReport
import java.util.Locale
import kotlin.text.get
import kotlin.toString

/**
 * 代码覆盖率排除列表
 * 定义不需要进行代码覆盖率统计的文件模式
 */
private val coverageExclusions = listOf(
    // Android 自动生成的文件
    "**/R.class",           // R文件
    "**/R\$*.class",        // R文件内部类
    "**/BuildConfig.*",     // 构建配置文件
    "**/Manifest*.*",       // Manifest文件
    "**/*_Hilt*.class",     // Hilt生成的文件
    "**/Hilt_*.class",      // Hilt生成的文件
)

/**
 * 字符串首字母大写扩展函数
 */
private fun String.capitalize() = replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
}

/**
 * 创建一个新的任务，用于生成结合本地和仪器测试数据的综合覆盖率报告
 *
 * 生成的任务名称格式: `create{variant}CombinedCoverageReport`
 *
 * 注意：运行此任务前必须先生成覆盖率数据。这样我们可以使用不同的Github Action
 * 或外部设备农场在CI上运行设备测试。
 *JaCoCo (Java Code Coverage) 是一个开源的Java代码覆盖率工具，用于测量测试执行过程中代码的覆盖情况。
 * 它是目前Java生态系统中最流行的代码覆盖率工具之一，广泛应用于各类Java项目（包括Android项目）的质量保障流程中。
 * @param androidComponentsExtension Android组件扩展，用于访问变体信息
 */
internal fun Project.configureJacoco(
    androidComponentsExtension: AndroidComponentsExtension<*, *, *>,
) {
    // 配置JaCoCo插件扩展
    configure<JacocoPluginExtension> {
        // 从版本目录中获取JaCoCo工具版本
        toolVersion = libs.findVersion("jacoco").get().toString()
    }

    // 为每个变体配置覆盖率报告
    androidComponentsExtension.onVariants { variant ->
        // 获取项目对象工厂和构建目录
        val myObjFactory = project.objects
        val buildDir = layout.buildDirectory.get().asFile

        // 创建用于收集所有jar和目录的属性列表
        val allJars: ListProperty<RegularFile> = myObjFactory.listProperty(RegularFile::class.java)
        val allDirectories: ListProperty<Directory> =
            myObjFactory.listProperty(Directory::class.java)

        // 注册生成覆盖率报告的任务
        val reportTask =
            tasks.register(
                "create${variant.name.capitalize()}CombinedCoverageReport",
                JacocoReport::class,
            ) {

                // 设置类目录，包含所有jar和目录，并排除指定文件
                classDirectories.setFrom(
                    allJars,
                    allDirectories.map { dirs ->
                        dirs.map { dir ->
                            myObjFactory.fileTree().setDir(dir).exclude(coverageExclusions)
                        }
                    },
                )

                // 配置报告格式
                reports {
                    xml.required = true   // 生成XML格式报告
                    html.required = true  // 生成HTML格式报告
                }

                // 将源目录转换为文件路径的扩展函数
                fun SourceDirectories.Flat?.toFilePaths(): Provider<List<String>> = this
                    ?.all
                    ?.map { directories -> directories.map { it.asFile.path } }
                    ?: provider { emptyList() }

                // 设置源代码目录
                sourceDirectories.setFrom(
                    files(
                        variant.sources.java.toFilePaths(),    // Java源码路径
                        variant.sources.kotlin.toFilePaths()   // Kotlin源码路径
                    ),
                )

                // 设置执行数据来源
                executionData.setFrom(
                    // 本地单元测试的执行数据
                    project.fileTree("$buildDir/outputs/unit_test_code_coverage/${variant.name}UnitTest")
                        .matching { include("**/*.exec") },

                    // 仪器测试的执行数据
                    project.fileTree("$buildDir/outputs/code_coverage/${variant.name}AndroidTest")
                        .matching { include("**/*.ec") },
                )
            }

        // 配置变体构件作用域，将类文件提供给报告任务
        variant.artifacts.forScope(ScopedArtifacts.Scope.PROJECT)
            .use(reportTask)
            .toGet(
                ScopedArtifact.CLASSES,
                { _ -> allJars },      // 获取jar文件
                { _ -> allDirectories }, // 获取目录
            )
    }

    // 为所有测试任务配置JaCoCo任务扩展
    tasks.withType<Test>().configureEach {
        configure<JacocoTaskExtension> {
            // JaCoCo + Robolectric所需的配置
            // 参考: https://github.com/robolectric/robolectric/issues/2230
            isIncludeNoLocationClasses = true

            // JDK 11配合上述配置所需
            // 参考: https://github.com/gradle/gradle/issues/5184#issuecomment-391982009
            excludes = listOf("jdk.internal.*")
        }
    }
}
