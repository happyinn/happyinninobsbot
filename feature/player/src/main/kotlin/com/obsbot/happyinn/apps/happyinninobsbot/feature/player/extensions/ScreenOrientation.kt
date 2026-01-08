package com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions

import android.content.pm.ActivityInfo
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.ScreenOrientation

/**
 * 屏幕方向扩展函数，用于将自定义的 ScreenOrientation 枚举转换为 Android 系统的 ActivityInfo 屏幕方向常量
 */

/**
 * 将 ScreenOrientation 枚举转换为 Android 系统的 ActivityInfo 屏幕方向常量
 * @param videoOrientation 视频方向，仅在 ScreenOrientation.VIDEO_ORIENTATION 模式下使用
 * @return Android 系统的 ActivityInfo 屏幕方向常量
 */
fun ScreenOrientation.toActivityOrientation(videoOrientation: Int? = null): Int {
    return when (this) {
        // 自动模式，使用传感器检测屏幕方向
        ScreenOrientation.AUTOMATIC -> ActivityInfo.SCREEN_ORIENTATION_SENSOR
        // 横屏模式
        ScreenOrientation.LANDSCAPE -> ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        // 反向横屏模式
        ScreenOrientation.LANDSCAPE_REVERSE -> ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE
        // 自动横屏模式，使用传感器检测横屏方向
        ScreenOrientation.LANDSCAPE_AUTO -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        // 竖屏模式
        ScreenOrientation.PORTRAIT -> ActivityInfo.SCREEN_ORIENTATION_SENSOR_PORTRAIT
        // 视频方向模式，使用提供的视频方向或默认值
        ScreenOrientation.VIDEO_ORIENTATION -> videoOrientation ?: ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
    }
}
