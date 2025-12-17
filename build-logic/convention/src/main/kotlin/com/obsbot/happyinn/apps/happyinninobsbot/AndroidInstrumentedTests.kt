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

import com.android.build.api.variant.LibraryAndroidComponentsExtension
import org.gradle.api.Project


/**
 * 为没有`androidTest`文件夹的项目禁用不必要的Android仪器测试
 *
 * 如果不进行此优化，这些项目仍会被编译、打包、安装和运行，但最终只会显示：
 * > Starting 0 tests on AVD
 *
 * 注意：可以通过检查基于buildTypes和flavors的其他潜在sourceSets来改进此功能
 *
 * @param project 需要处理的Project对象
 */
internal fun LibraryAndroidComponentsExtension.disableUnnecessaryAndroidTests(
    project: Project,
) = beforeVariants {
    // 仅当原来已启用androidTest且项目目录下存在src/androidTest文件夹时才启用测试
    it.androidTest.enable = it.androidTest.enable
            && project.projectDir.resolve("src/androidTest").exists()
}