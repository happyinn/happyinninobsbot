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

import com.obsbot.happyinn.apps.happyinninobsbot.configureKotlinJvm
import com.obsbot.happyinn.apps.happyinninobsbot.libs
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies

/**
 * JVM库项目约定插件
 *
 * 为纯JVM Kotlin库项目提供标准配置，包括应用必要的插件、
 * 配置Kotlin JVM编译选项和添加基础依赖
 */
class JvmLibraryConventionPlugin : Plugin<Project> {
    /**
     * 应用插件的核心方法
     *
     * @param target 需要应用此插件的Gradle项目对象
     */
    override fun apply(target: Project) {
        // 使用with语法简化对target项目的操作
        with(target) {
            // 应用Kotlin JVM插件，为项目提供Kotlin编译支持
            apply(plugin = "org.jetbrains.kotlin.jvm")
            // 应用Android Lint插件，提供代码质量检查功能
            apply(plugin = "happyinninobsbot.android.lint")

            // 调用自定义函数配置Kotlin JVM相关选项（如JVM目标版本、编译参数等）
            configureKotlinJvm()
            
            // 配置项目依赖
            dependencies {
                // 添加Kotlin测试库作为测试实现依赖
                "testImplementation"(libs.findLibrary("kotlin.test").get())
            }
        }
    }
}