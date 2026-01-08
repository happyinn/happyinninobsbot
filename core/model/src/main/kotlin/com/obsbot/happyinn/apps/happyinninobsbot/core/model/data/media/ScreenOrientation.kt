package com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media

/**
 * 屏幕方向枚举
 * 
 * 定义了视频播放器的不同屏幕方向选项
 */
enum class ScreenOrientation {
    AUTOMATIC,           // 自动旋转，跟随设备方向
    LANDSCAPE,           // 固定横屏（正常横屏）
    LANDSCAPE_REVERSE,    // 固定反向横屏
    LANDSCAPE_AUTO,       // 自动横屏（根据设备方向在正常和反向横屏之间切换）
    PORTRAIT,             // 固定竖屏
    VIDEO_ORIENTATION,    // 跟随视频原始方向
}