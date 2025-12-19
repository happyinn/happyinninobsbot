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

// 导入Android应用扩展DSL
import com.android.build.api.dsl.ApplicationExtension
// 导入Android通用扩展DSL
import com.android.build.api.dsl.CommonExtension
// 导入Firebase Crashlytics扩展
import com.google.firebase.crashlytics.buildtools.gradle.CrashlyticsExtension
// 导入项目依赖库配置
import com.obsbot.happyinn.apps.happyinninobsbot.libs
// 导入Gradle插件基础类
import org.gradle.api.Plugin
// 导入Gradle项目类
import org.gradle.api.Project
// 导入Gradle Kotlin DSL扩展函数
import org.gradle.kotlin.dsl.apply
// 导入Gradle Kotlin DSL配置函数
import org.gradle.kotlin.dsl.configure
// 导入Gradle依赖配置函数
import org.gradle.kotlin.dsl.dependencies
// 导入依赖排除函数
import org.gradle.kotlin.dsl.exclude

/**
 * Android应用Firebase约定插件
 * 
 * 该插件用于统一配置Android应用项目的Firebase相关服务，
 * 包括Google服务、性能监控、Crashlytics崩溃报告等功能
 */
class AndroidApplicationFirebaseConventionPlugin : Plugin<Project> {
    /**
     * 应用插件的主要入口函数
     *
     * @param target 需要应用此插件的Gradle项目
     */
    override fun apply(target: Project) {
        with(target) {
            // 应用Google服务插件，用于Firebase认证和配置
            apply(plugin = "com.google.gms.google-services")
            // 应用Firebase性能监控插件
            apply(plugin = "com.google.firebase.firebase-perf")
            // 应用Firebase Crashlytics插件，用于崩溃报告
            apply(plugin = "com.google.firebase.crashlytics")

            // 配置项目依赖
            dependencies {
                // 获取Firebase BOM(物料清单)，用于统一Firebase库版本
                val bom = libs.findLibrary("firebase-bom").get()
                // 添加Firebase BOM平台依赖
                "implementation"(platform(bom))
                // 添加Firebase Analytics分析服务依赖
                "implementation"(libs.findLibrary("firebase.analytics").get())
                // 添加Firebase Performance性能监控依赖
                "implementation"(libs.findLibrary("firebase.performance").get()) {
                    /*
                    排除protobuf / protolite依赖是必要的，因为datastore-proto引入了protobuf依赖。
                    这些是Now in Android的可信数据源。
                    因此排除以下依赖中的重复类。
                    */
                    // 修改排除规则，只排除可能导致冲突的特定模块，保留运行时必要的protobuf类
//                    exclude(group = "com.google.protobuf", module = "protobuf-javalite")
                    exclude(group = "com.google.firebase", module = "protolite-well-known-types")
                }
                // 添加Firebase Crashlytics崩溃报告依赖
                "implementation"(libs.findLibrary("firebase.crashlytics").get())
            }

            // 配置Android应用扩展
            extensions.configure<ApplicationExtension> {
                // 为每个构建类型配置
                buildTypes.configureEach {
                    // 禁用Crashlytics映射文件上传功能。
                    // 此功能只有在google-services.json中配置了Firebase后端时才应启用。
                    configure<CrashlyticsExtension> {
                        mappingFileUploadEnabled = false
                    }
                }
            }
        }
    }
}
