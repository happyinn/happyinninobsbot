/*
 * Copyright 2023 The Android Open Source Project
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

import com.android.SdkConstants
import com.android.build.api.artifact.SingleArtifact
import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.google.common.truth.Truth
import com.google.common.truth.Truth.assertWithMessage
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Copy
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.register
import org.gradle.language.base.plugins.LifecycleBasePlugin
import org.gradle.process.ExecOperations
import javax.inject.Inject

/**
 * 生成APK清单信息的任务类
 *
 * 该任务使用aapt2工具从APK文件中提取应用的清单信息(badging信息)，
 * 包括包名、版本号、权限、Activity等关键应用信息
 */
@CacheableTask
abstract class GenerateBadgingTask : DefaultTask() {

    /**
     * 输出的badging信息文件
     * 包含从APK中提取的应用清单信息
     */
    @get:OutputFile
    abstract val badging: RegularFileProperty

    /**
     * 输入的APK文件
     * 需要从中提取badging信息的APK文件路径
     */
    @get:PathSensitive(PathSensitivity.NONE)
    @get:InputFile
    abstract val apk: RegularFileProperty

    /**
     * aapt2可执行文件路径
     * 用于执行APK信息提取的Android Asset Packaging Tool v2工具
     */
    @get:PathSensitive(PathSensitivity.NONE)
    @get:InputFile
    abstract val aapt2Executable: RegularFileProperty

    /**
     * 注入的执行操作服务
     * 用于执行外部命令行工具
     */
    @get:Inject
    abstract val execOperations: ExecOperations

    /**
     * 任务执行动作
     *
     * 调用aapt2 dump badging命令从APK中提取清单信息并保存到输出文件
     */
    @TaskAction
    fun taskAction() {
        execOperations.exec {
            commandLine(
                aapt2Executable.get().asFile.absolutePath,  // aapt2工具路径
                "dump",                                     // dump命令
                "badging",                                  // 提取badging信息
                apk.get().asFile.absolutePath,              // APK文件路径
            )
            standardOutput = badging.asFile.get().outputStream()  // 将输出重定向到badging文件
        }
    }
}

/**
 * 检查badging信息的任务类
 *
 * 对比生成的badging信息与基准(golden)badging信息，
 * 确保APK的清单信息符合预期，防止意外变更
 */
@CacheableTask
abstract class CheckBadgingTask : DefaultTask() {

    // 为了在输入未更改时使任务保持最新状态，
    // 任务必须声明一个输出，即使它未被使用。没有输出的任务无论输入是否更改都会运行
    @get:OutputDirectory
    abstract val output: DirectoryProperty

    /**
     * 基准badging信息文件
     * 作为比较标准的预期badging信息文件
     */
    @get:PathSensitive(PathSensitivity.NONE)
    @get:InputFile
    abstract val goldenBadging: RegularFileProperty

    /**
     * 生成的badging信息文件
     * 由GenerateBadgingTask生成的实际badging信息文件
     */
    @get:PathSensitive(PathSensitivity.NONE)
    @get:InputFile
    abstract val generatedBadging: RegularFileProperty

    /**
     * 更新badging任务名称
     * 当badging信息需要更新时，提示用户运行的更新任务名称
     */
    @get:Input
    abstract val updateBadgingTaskName: Property<String>

    /**
     * 获取任务分组
     * 将任务归类到验证任务组中
     */
    override fun getGroup(): String = LifecycleBasePlugin.VERIFICATION_GROUP

    /**
     * 任务执行动作
     *
     * 比较生成的badging信息与基准badging信息，
     * 如果不一致则抛出断言错误，并提示如何更新基准文件
     */
    @TaskAction
    fun taskAction() {
        Truth.assertWithMessage(
            "Generated badging is different from golden badging! " +
                    "If this change is intended, run ./gradlew ${updateBadgingTaskName.get()}",
        )
            .that(generatedBadging.get().asFile.readText())      // 实际生成的badging内容
            .isEqualTo(goldenBadging.get().asFile.readText())    // 基准badging内容
    }
}

/**
 * 将字符串首字母大写
 *
 * 辅助函数，用于将variant名称转换为首字母大写的格式，
 * 以便生成符合命名规范的任务名称
 */
private fun String.capitalized() = replaceFirstChar {
    if (it.isLowerCase()) it.titlecase() else it.toString()
}

/**
 * 配置badging相关任务的扩展函数
 *
 * 为每个应用变体(variant)配置完整的badging处理流程，
 * 包括生成、更新和检查三个任务
 *
 * @param baseExtension 应用扩展配置，提供构建工具版本等信息
 * @param componentsExtension 应用组件扩展，提供SDK组件和变体信息
 */
fun Project.configureBadgingTasks(
    baseExtension: ApplicationExtension,
    componentsExtension: ApplicationAndroidComponentsExtension,
) {
    // 注册回调函数，在新变体配置时调用
    componentsExtension.onVariants { variant ->
        // 注册新的任务来验证应用bundle
        val capitalizedVariantName = variant.name.capitalized()                           // 变体名称首字母大写
        val generateBadgingTaskName = "generate${capitalizedVariantName}Badging"         // 生成badging任务名称
        val generateBadging =
            tasks.register<GenerateBadgingTask>(generateBadgingTaskName) {
                apk = variant.artifacts.get(SingleArtifact.APK_FROM_BUNDLE)              // 获取变体的APK文件
                aapt2Executable.set(
                    // TODO: 当AGP中可用时替换为`sdkComponents.aapt2`
                    //       https://issuetracker.google.com/issues/376815836
                    componentsExtension.sdkComponents.sdkDirectory.map { directory ->
                        directory.file(
                            "${SdkConstants.FD_BUILD_TOOLS}/" +
                                "${baseExtension.buildToolsVersion}/" +                 // 构建工具版本目录
                                SdkConstants.FN_AAPT2,                                  // aapt2可执行文件名
                        )
                    }
                )
                badging = project.layout.buildDirectory.file(
                    "outputs/apk_from_bundle/${variant.name}/${variant.name}-badging.txt",  // 输出badging文件路径
                )

            }

        val updateBadgingTaskName = "update${capitalizedVariantName}Badging"             // 更新badging任务名称
        tasks.register<Copy>(updateBadgingTaskName) {
            from(generateBadging.map(GenerateBadgingTask::badging))                      // 从生成的badging文件复制
            into(project.layout.projectDirectory)                                        // 复制到项目根目录
        }

        val checkBadgingTaskName = "check${capitalizedVariantName}Badging"               // 检查badging任务名称
        tasks.register<CheckBadgingTask>(checkBadgingTaskName) {
            goldenBadging = project.layout.projectDirectory.file("${variant.name}-badging.txt")  // 基准badging文件路径

            generatedBadging.set(generateBadging.flatMap(GenerateBadgingTask::badging))          // 设置生成的badging文件

            this.updateBadgingTaskName = updateBadgingTaskName                                   // 设置更新任务名称

            output = project.layout.buildDirectory.dir("intermediates/$checkBadgingTaskName")    // 中间输出目录

        }
    }
}
