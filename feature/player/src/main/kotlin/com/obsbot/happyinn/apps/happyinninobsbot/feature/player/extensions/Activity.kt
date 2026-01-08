/**
 * 窗口管理扩展函数
 * 
 * 本文件提供 Activity 窗口系统栏控制、屏幕亮度获取等功能的扩展函数。
 * 主要用于视频播放器场景下的沉浸式体验和用户交互控制。
 * 
 * 功能列表：
 * 1. swipeToShowStatusBars(): 配置系统栏滑动临时显示行为
 * 2. toggleSystemBars(): 切换系统栏的显示/隐藏状态
 * 3. currentBrightness: 获取当前屏幕亮度值
 */
package com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions

import android.app.Activity
import android.provider.Settings
import android.view.WindowManager
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat.Type
import androidx.core.view.WindowInsetsControllerCompat

/**
 * 配置系统栏滑动显示行为
 * 
 * 功能说明：
 * 设置系统栏在用户滑动屏幕边缘时临时显示，滑动后自动隐藏。
 * 这种行为模式适用于视频播放等需要沉浸式体验的场景。
 * 
 * 使用场景：
 * - 视频全屏播放时，用户可以通过滑动临时查看状态栏和导航栏
 * - 适用于不想完全隐藏系统栏，但又希望获得沉浸式体验的情况
 * 
 * 技术原理：
 * 使用 WindowInsetsControllerCompat 配置系统栏行为模式为
 * BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE，表示通过滑动显示临时系统栏
 * 
 * 调用时机：
 * 应在 Activity 完成窗口配置后调用，以确保行为一致性
 */
fun Activity.swipeToShowStatusBars() {
    WindowCompat.getInsetsController(window, window.decorView).systemBarsBehavior =
        WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
}

/**
 * 切换系统栏可见性
 * 
 * 功能说明：
 * 控制 Activity 窗口中系统栏（状态栏、导航栏）的显示或隐藏。
 * 提供灵活的 API 允许控制特定类型的系统栏。
 * 
 * 参数说明：
 * - showBars: Boolean 类型
 *   true = 显示指定的系统栏
 *   false = 隐藏指定的系统栏
 * 
 * - types: Int 类型，系统栏类型（使用 WindowInsetsCompat.Type 常量）
 *   默认值 = Type.systemBars()（所有系统栏）
 *   可选值：
 *   - Type.statusBars(): 仅状态栏
 *   - Type.navigationBars(): 仅导航栏
 *   - Type.captionBar(): 标题栏
 *   - Type.systemBars(): 所有系统栏的组合
 * 
 * 返回值：
 * 无，直接修改窗口的系统栏显示状态
 * 
 * 使用示例：
 * // 全屏播放时隐藏所有系统栏
 * toggleSystemBars(showBars = false)
 * 
 * // 仅显示状态栏
 * toggleSystemBars(showBars = true, Type.statusBars())
 * 
 * // 恢复显示所有系统栏
 * toggleSystemBars(showBars = true)
 * 
 * 注意事项：
 * 当 showBars = false 时，用户仍可通过滑动边缘临时显示系统栏
 * 这是 Android 系统的默认行为，提供了一种折中的交互方案
 */
fun Activity.toggleSystemBars(showBars: Boolean, @Type.InsetsType types: Int = Type.systemBars()) {
    WindowCompat.getInsetsController(window, window.decorView).apply {
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        if (showBars) show(types) else hide(types)
    }
}

/**
 * 获取当前屏幕亮度值（扩展属性）
 * 
 * 功能说明：
 * 获取 Activity 当前窗口的屏幕亮度设置，返回值范围为 0.0 到 1.0。
 * 优先返回窗口自定义亮度设置，如果未设置则读取系统亮度。
 * 
 * 返回值说明：
 * - Float 类型，范围 0.0f（最暗/关闭）到 1.0f（最亮）
 * 
 * 亮度获取逻辑：
 * 1. 首先检查窗口是否设置了自定义亮度（screenBrightness 属性）
 * 2. 如果窗口有自定义亮度设置且在有效范围内，直接返回该值
 * 3. 如果窗口没有自定义设置或值无效，则读取系统当前亮度设置
 * 4. 系统亮度范围为 0-255，需要除以 255 转换为 0-1 范围
 * 
 * 系统亮度与自定义亮度的区别：
 * - 自定义亮度：通过 window.attributes.screenBrightness 设置
 * - 系统亮度：通过 Settings.System.SCREEN_BRIGHTNESS 读取
 * 
 * 使用示例：
 * // 获取当前亮度值
 * val brightness = currentBrightness
 * // brightness 范围 0.0f - 1.0f
 * 
 * // 根据亮度值调整 UI
 * if (currentBrightness > 0.5f) {
 *     // 亮度较高时的处理
 * }
 * 
 * // 设置自定义亮度（需配合 window.attributes 使用）
 * window.attributes = window.attributes.apply {
 *     screenBrightness = currentBrightness * 0.8f
 * }
 * 
 * 注意事项：
 * - 读取系统亮度需要 READ_SETTINGS 权限（在 Android 6.0+）
 * - 自定义亮度设置优先级高于系统设置
 * - 设置 screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_OFF 表示跟随系统
 * - 设置 screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE 表示使用系统默认
 */
val Activity.currentBrightness: Float
    get() = when (val brightness = window.attributes.screenBrightness) {
        in WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_OFF..WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL -> brightness
        else -> Settings.System.getFloat(contentResolver, Settings.System.SCREEN_BRIGHTNESS) / 255
    }
