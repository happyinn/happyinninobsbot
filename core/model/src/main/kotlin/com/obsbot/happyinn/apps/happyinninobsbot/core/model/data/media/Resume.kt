package com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media

/**
 * 恢复播放模式枚举
 * 
 * 定义了视频播放时是否恢复上次播放位置的选项
 */
enum class Resume {
    YES,    // 总是恢复上次播放位置
    NO,     // 不恢复，总是从头开始播放
}