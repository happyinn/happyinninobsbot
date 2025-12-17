/*
 * Copyright 2023 The Android Open Source Project
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

package com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color


/**
 * 用于建模HappyInn机器人应用图标着色主题的数据类
 *
 * 该类定义了应用中图标的着色配置，主要用于统一管理图标颜色主题
 */
@Immutable
data class TintTheme(
    // 图标着色颜色，默认为未指定状态
    val iconTint: Color = Color.Unspecified,
)

/**
 * [TintTheme] 的组合局部状态
 *
 * 用于在Compose组件树中传递图标着色主题配置，
 * 使得子组件可以访问和使用统一的图标着色方案
 */
val LocalTintTheme = staticCompositionLocalOf { TintTheme() }


