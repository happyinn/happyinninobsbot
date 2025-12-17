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

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

/**
 * 为Project类提供一个扩展属性，用于快速访问名为"libs"的版本目录
 *
 * 这个扩展属性简化了从Gradle项目中获取预定义依赖库版本信息的过程。
 * 在Gradle中，VersionCatalog提供了对libs.versions.toml文件中定义的依赖和版本的访问。
 *
 * 使用示例：
 *   project.libs.findLibrary("kotlin.test").get()
 *
 * @return VersionCatalog 返回名为"libs"的版本目录实例
 */
val Project.libs
    get(): VersionCatalog = extensions.getByType<VersionCatalogsExtension>().named("libs")
