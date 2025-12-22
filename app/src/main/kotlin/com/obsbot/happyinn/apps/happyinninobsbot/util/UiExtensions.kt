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

package com.obsbot.happyinn.apps.happyinninobsbot.util

import android.content.res.Configuration
import androidx.activity.ComponentActivity
import androidx.core.util.Consumer
import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * 便捷包装器，用于检查深色模式状态
 * 扩展属性，可以直接通过 Configuration 实例访问
 */
val Configuration.isSystemInDarkTheme
    get() = (uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES

/**
 * 注册配置变化监听器以获取系统是否处于深色主题状态
 * 订阅时立即发送当前值，然后注册变化监听器
 * @receiver ComponentActivity 组件活动实例
 * @return Flow<Boolean> 返回一个布尔值流，表示系统深色主题状态
 */
//TODO 待深入学习
fun ComponentActivity.isSystemInDarkTheme() = callbackFlow {
    // 发送当前配置的深色主题状态
    channel.trySend(resources.configuration.isSystemInDarkTheme)

    // 创建配置变化监听器
    val listener = Consumer<Configuration> {
        channel.trySend(it.isSystemInDarkTheme)
    }

    // 添加配置变化监听器
    addOnConfigurationChangedListener(listener)

    // 在关闭时移除监听器
    awaitClose { removeOnConfigurationChangedListener(listener) }
}
    .distinctUntilChanged()  // 只有当值真正改变时才发射
    .conflate()              // 处理背压，新值到来时丢弃旧值
