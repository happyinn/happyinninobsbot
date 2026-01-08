package com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media

/**
 * 快速跳转模式枚举
 * 
 * 定义了视频播放器快速跳转功能的不同选项
 */
enum class FastSeek {
    AUTO,   // 自动模式（根据设备性能自动决定是否启用）
    ENABLE, // 启用快速跳转
    DISABLE,// 禁用快速跳转
}