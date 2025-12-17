/*
 * Copyright 2022 The Android Open Source Project
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
import com.android.build.api.dsl.CommonExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.kotlin.dsl.assign
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinBaseExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

/**
 * 配置Android项目的Kotlin基础选项
 *
 * @param commonExtension Android项目的通用扩展配置
 */
internal fun Project.configureKotlinAndroid(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    commonExtension.apply {
        // 设置编译SDK版本为36
        compileSdk = 36

        defaultConfig {
            // 设置最低支持SDK版本为23
            minSdk = 23
        }

        compileOptions {
            // 通过脱糖(desugaring)支持Java 11 API
            // 参考: https://developer.android.com/studio/write/java11-minimal-support-table
            sourceCompatibility = JavaVersion.VERSION_11
            targetCompatibility = JavaVersion.VERSION_11
            // 启用核心库脱糖功能
            isCoreLibraryDesugaringEnabled = true
        }
    }

    // 配置Kotlin Android项目扩展
    configureKotlin<KotlinAndroidProjectExtension>()

    dependencies {
        // 添加核心库脱糖依赖
        "coreLibraryDesugaring"(libs.findLibrary("android.desugarJdkLibs").get())
    }
}

/**
 * 配置JVM项目(非Android)的Kotlin基础选项
 */
internal fun Project.configureKotlinJvm() {
    extensions.configure<JavaPluginExtension> {
        // 通过脱糖(desugaring)支持Java 11 API
        // 参考: https://developer.android.com/studio/write/java11-minimal-support-table
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    // 配置Kotlin JVM项目扩展
    configureKotlin<KotlinJvmProjectExtension>()
}

/**
 * 配置Kotlin基础选项的通用函数
 *
 * @param T Kotlin基础扩展类型的泛型参数
 */
private inline fun <reified T : KotlinBaseExtension> Project.configureKotlin() = configure<T> {
    // 将所有Kotlin警告视为错误(默认禁用)
    // 可通过在~/.gradle/gradle.properties中设置warningsAsErrors=true来覆盖
    val warningsAsErrors = providers.gradleProperty("warningsAsErrors").map {
        it.toBoolean()
    }.orElse(false)

    // 根据不同的扩展类型获取编译器选项
    when (this) {
        is KotlinAndroidProjectExtension -> compilerOptions
        is KotlinJvmProjectExtension -> compilerOptions
        else -> TODO("Unsupported project extension $this ${T::class}")
    }.apply {
        // 设置JVM目标版本为11
        jvmTarget = JvmTarget.JVM_11
        // 设置是否将所有警告视为错误
        allWarningsAsErrors = warningsAsErrors

        // 添加编译器参数
        freeCompilerArgs.add(
            // 启用实验性的协程API，包括Flow
            "-opt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
        )

        freeCompilerArgs.add(
            /**
             * 在Phase 3之后移除此参数
             * 参考: https://kotlinlang.org/api/latest/jvm/stdlib/kotlin/-consistent-copy-visibility/#deprecation-timeline
             *
             * 弃用时间线
             * Phase 3. (预计Kotlin 2.2或Kotlin 2.3).
             * 默认行为会改变。
             * 除非使用ExposedCopyVisibility，否则生成的'copy'方法将具有与主构造函数相同的可见性。
             * 二进制签名会发生变化。声明上的错误将不再报告。
             * '-Xconsistent-data-class-copy-visibility'编译器标志和ConsistentCopyVisibility注解将不再必要。
             */
            "-Xconsistent-data-class-copy-visibility"
        )
    }
}