package com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media

/**
 * 解码器优先级枚举
 * 
 * 定义了视频播放器解码器的优先级选项
 */
enum class DecoderPriority {
    PREFER_DEVICE,  // 优先使用设备硬件解码器
    PREFER_APP,     // 优先使用应用软件解码器
    DEVICE_ONLY,    // 仅使用设备硬件解码器
}