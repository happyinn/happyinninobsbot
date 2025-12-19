package com.obsbot.happyinn.apps.happyinninobsbot

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.UserDataRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.DarkThemeConfig
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.ThemeBrand
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.UserData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import com.obsbot.happyinn.apps.happyinninobsbot.MainActivityUiState.Loading
import com.obsbot.happyinn.apps.happyinninobsbot.MainActivityUiState.Success



/**
 * 主活动的 ViewModel，负责管理应用的主题配置和用户数据状态
 *
 * @param userDataRepository 用户数据仓库，用于获取用户主题偏好设置
 */
@HiltViewModel
class MainActivityViewModel @Inject constructor(
    userDataRepository: UserDataRepository,
) : ViewModel() {
    /**
     * UI 状态流，映射用户数据为 MainActivityUiState 并在 ViewModel 作用域内共享状态
     */
    val uiState: StateFlow<MainActivityUiState> = userDataRepository.userData.map {
        Success(it)
    }.stateIn(
        scope = viewModelScope,
        initialValue = Loading,
        started = SharingStarted.WhileSubscribed(5_000),
    )
}

/**
 * 主活动的 UI 状态密封接口
 * 定义了应用启动时的加载状态和成功状态
 */
sealed interface MainActivityUiState {
    /**
     * 加载状态 - 表示用户数据尚未加载完成
     */
    data object Loading : MainActivityUiState

    /**
     * 成功状态 - 包含用户数据并提供主题配置相关的计算属性
     *
     * @param userData 用户数据，包括主题品牌、深色主题配置和动态色彩使用偏好
     */
    data class Success(val userData: UserData) : MainActivityUiState {
        /**
         * 是否禁用动态色彩功能
         * 基于用户数据中的 useDynamicColor 字段取反
         */
        override val shouldDisableDynamicTheming = !userData.useDynamicColor

        /**
         * 是否使用 Android 主题
         * 根据用户选择的主题品牌决定：
         * - DEFAULT: 使用 Material Design 默认主题
         * - ANDROID: 使用 Android 主题
         */
        override val shouldUseAndroidTheme: Boolean = when (userData.themeBrand) {
            ThemeBrand.DEFAULT -> false
            ThemeBrand.ANDROID -> true
        }

        /**
         * 是否使用深色主题
         * 根据用户的深色主题配置决定：
         * - FOLLOW_SYSTEM: 跟随系统设置
         * - LIGHT: 始终使用浅色主题
         * - DARK: 始终使用深色主题
         */
        override fun shouldUseDarkTheme(isSystemDarkTheme: Boolean) =
            when (userData.darkThemeConfig) {
                DarkThemeConfig.FOLLOW_SYSTEM -> isSystemDarkTheme
                DarkThemeConfig.LIGHT -> false
                DarkThemeConfig.DARK -> true
            }
    }

    /**
     * 返回 true 如果状态尚未加载完成，应继续显示启动画面
     */
    fun shouldKeepSplashScreen() = this is Loading

    /**
     * 返回 true 如果动态色彩功能被禁用
     * 默认值为 true，在 Success 状态中会被重写
     */
    val shouldDisableDynamicTheming: Boolean get() = true

    /**
     * 返回 true 如果应该使用 Android 主题
     * 默认值为 false，在 Success 状态中会被重写
     */
    val shouldUseAndroidTheme: Boolean get() = false

    /**
     * 返回 true 如果应该使用深色主题
     * 默认实现是跟随系统深色主题设置，在 Success 状态中会被重写
     */
    fun shouldUseDarkTheme(isSystemDarkTheme: Boolean) = isSystemDarkTheme
}
