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

import com.android.build.api.dsl.CommonExtension
import com.android.build.api.dsl.DeviceGroup
import com.android.build.api.dsl.ManagedDevices
import com.android.build.api.dsl.ManagedVirtualDevice
import com.android.build.api.dsl.TestOptions
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.invoke


/**
 * 配置项目使用Gradle托管设备
 *
 * Gradle托管设备是一种由Gradle自动管理的虚拟设备，用于自动化测试
 */
internal fun configureGradleManagedDevices(
    commonExtension: CommonExtension<*, *, *, *, *, *>,
) {
    // 定义三种测试设备配置
    val pixel4 = DeviceConfig("Pixel 4", 30, "aosp-atd")   // Pixel 4设备，API 30，使用ATD系统镜像
    val pixel6 = DeviceConfig("Pixel 6", 31, "aosp")       // Pixel 6设备，API 31，使用标准AOSP系统镜像
    val pixelC = DeviceConfig("Pixel C", 30, "aosp-atd")   // Pixel C平板，API 30，使用ATD系统镜像

    // 所有设备列表
    val allDevices = listOf(pixel4, pixel6, pixelC)
    // CI环境中使用的设备列表（性能优化的ATD设备）
    val ciDevices = listOf(pixel4, pixelC)

    // 配置测试选项中的托管设备
    commonExtension.testOptions {
        managedDevices {
            // 配置单个设备
            devices {
                allDevices.forEach { deviceConfig ->
                    // 创建或获取设备配置实例
                    maybeCreate(deviceConfig.taskName, ManagedVirtualDevice::class.java).apply {
                        device = deviceConfig.device                    // 设备型号
                        apiLevel = deviceConfig.apiLevel               // API级别
                        systemImageSource = deviceConfig.systemImageSource // 系统镜像源
                    }
                }
            }
            // 配置设备组
            groups {
                // 创建CI测试设备组
                maybeCreate("ci").apply {
                    ciDevices.forEach { deviceConfig ->
                        // 将设备添加到CI组中
                        targetDevices.add(devices[deviceConfig.taskName])
                    }
                }
            }
        }
    }
}

/**
 * 设备配置数据类
 * 用于存储设备的相关配置信息
 */
private data class DeviceConfig(
    val device: String,           // 设备型号名称
    val apiLevel: Int,            // API级别
    val systemImageSource: String, // 系统镜像源
) {
    /**
     * 生成设备的任务名称
     * 格式：设备名(小写去空格) + api + API级别 + 系统镜像源(去连字符)
     * 例如："pixel4api30aospatd"
     */
    val taskName = buildString {
        append(device.lowercase().replace(" ", ""))      // 设备名转小写并去除空格
        append("api")                                    // 添加api前缀
        append(apiLevel.toString())                      // 添加API级别
        append(systemImageSource.replace("-", ""))       // 添加系统镜像源并去除连字符
    }
}