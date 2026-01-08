package com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media

/**
 * 视频缩放模式枚举
 * 
 * 定义了视频播放器的不同视频缩放选项，用于控制视频在屏幕上的显示方式
 */
enum class VideoZoom {
    BEST_FIT,           // 最佳适配，保持原始比例并适应屏幕
    STRETCH,            // 拉伸，填满整个屏幕，可能导致变形
    CROP,               // 裁剪，填满屏幕并保持比例，可能裁剪部分内容
    HUNDRED_PERCENT,     // 100%显示，按实际像素显示
}