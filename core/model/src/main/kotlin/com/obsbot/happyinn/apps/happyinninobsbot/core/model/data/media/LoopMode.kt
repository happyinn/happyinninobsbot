package com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media

/**
 * 循环播放模式枚举
 * 
 * 定义了视频播放器的不同循环播放模式选项
 */

import kotlinx.serialization.Serializable

@Serializable
enum class LoopMode {
    OFF,    // 关闭循环播放
    ONE,    // 单曲循环
    ALL,    // 全部循环
}