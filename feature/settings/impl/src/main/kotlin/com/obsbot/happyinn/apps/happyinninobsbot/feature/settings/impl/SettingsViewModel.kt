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

package com.obsbot.happyinn.apps.happyinninobsbot.feature.settings.impl

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.UserDataRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.DarkThemeConfig
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.ThemeBrand
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted.Companion.WhileSubscribed
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

/**
 * 设置界面的 ViewModel，负责管理设置相关的数据和状态
 */
//TODO 待细看
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userDataRepository: UserDataRepository, // 用户数据仓库，用于获取和更新用户设置
) : ViewModel() {

    /**
     * 设置界面的 UI 状态流
     * 从用户数据仓库获取用户数据，并映射为可编辑的设置项
     */
    val settingsUiState: StateFlow<SettingsUiState> =
        userDataRepository.userData
            .map { userData ->
                SettingsUiState.Success(
                    settings = UserEditableSettings(
                        brand = userData.themeBrand,           // 主题品牌（默认/Android）
                        useDynamicColor = userData.useDynamicColor, // 是否使用动态颜色
                        darkThemeConfig = userData.darkThemeConfig, // 深色主题配置
                    ),
                )
            }
            .stateIn(
                scope = viewModelScope,
                started = WhileSubscribed(5.seconds.inWholeMilliseconds), // 当有订阅者时才活跃，5秒后无订阅者则停止
                initialValue = SettingsUiState.Loading, // 初始状态为加载中
            )

    /**
     * 更新主题品牌设置
     * @param themeBrand 新的主题品牌配置
     */
    fun updateThemeBrand(themeBrand: ThemeBrand) {
        viewModelScope.launch {
            userDataRepository.setThemeBrand(themeBrand)
        }
    }

    /**
     * 更新深色主题配置
     * @param darkThemeConfig 新的深色主题配置
     */
    fun updateDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        viewModelScope.launch {
            userDataRepository.setDarkThemeConfig(darkThemeConfig)
        }
    }

    /**
     * 更新动态颜色偏好设置
     * @param useDynamicColor 是否使用动态颜色
     */
    fun updateDynamicColorPreference(useDynamicColor: Boolean) {
        viewModelScope.launch {
            userDataRepository.setDynamicColorPreference(useDynamicColor)
        }
    }
}

/**
 * 表示用户可以在应用内编辑的设置项
 * @param brand 主题品牌
 * @param useDynamicColor 是否使用动态颜色
 * @param darkThemeConfig 深色主题配置
 */
data class UserEditableSettings(
    val brand: ThemeBrand,
    val useDynamicColor: Boolean,
    val darkThemeConfig: DarkThemeConfig,
)

/**
 * 设置界面的 UI 状态密封接口
 */
sealed interface SettingsUiState {
    data object Loading : SettingsUiState  // 加载状态
    data class Success(val settings: UserEditableSettings) : SettingsUiState // 成功状态，包含设置数据
}
