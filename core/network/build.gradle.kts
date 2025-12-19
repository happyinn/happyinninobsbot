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

import com.android.build.api.variant.BuildConfigField
import java.io.StringReader
import java.util.Properties

plugins {
    alias(libs.plugins.happyinninobsbot.android.library)
    alias(libs.plugins.happyinninobsbot.android.library.jacoco)
    alias(libs.plugins.happyinninobsbot.hilt)
    id("kotlinx-serialization")
}

android {
    buildFeatures {
        buildConfig = true
    }
    namespace = "com.obsbot.happyinn.apps.happyinninobsbot.core.network"
    testOptions.unitTests.isIncludeAndroidResources = true
}

dependencies {
    api(libs.kotlinx.datetime)
    api(projects.core.common)
    api(projects.core.model)

    implementation(libs.coil.kt)
    implementation(libs.coil.kt.svg)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.okhttp.logging)
    implementation(libs.retrofit.core)
    implementation(libs.retrofit.kotlin.serialization)

    testImplementation(libs.kotlinx.coroutines.test)
}

// 从项目根目录的 local.properties 文件中读取 BACKEND_URL 配置
val backendUrl = providers.fileContents(
    // 获取根项目的 local.properties 文件路径
    isolated.rootProject.projectDirectory.file("local.properties")
).asText.map { text ->
    // 将文件内容转换为 Properties 对象
    val properties = Properties()
    properties.load(StringReader(text))
    // 获取 BACKEND_URL 属性的值
    properties["BACKEND_URL"]
    // 如果没有配置 BACKEND_URL，则使用默认值 http://example.com
}.orElse("http://example.com")

// Android 组件配置块，在构建过程中配置组件相关属性
androidComponents {
    // 针对每个构建变体执行配置操作
    onVariants {
        // 将 BACKEND_URL 添加到 BuildConfig 字段中
        it.buildConfigFields!!.put("BACKEND_URL", backendUrl.map { value ->
            // 创建 BuildConfig 字段，指定类型为 String，值为读取到的 URL
            BuildConfigField(type = "String", value = """"$value"""", comment = null)
        })
    }
}
