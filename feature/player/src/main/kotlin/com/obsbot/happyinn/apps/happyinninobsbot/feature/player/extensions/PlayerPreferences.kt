package com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions

import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.FastSeek
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.PlayerPreferences

/**
 * 播放器偏好设置扩展函数，提供快速快进/后退的判断逻辑
 * 基于偏好配置（开启/关闭/自动）和最小时长阈值判断
 */

/**
 * 判断是否应该使用快速快进/后退功能
 * @param duration 视频总时长（毫秒）
 * @return 是否应该使用快速快进/后退
 */
fun PlayerPreferences.shouldFastSeek(duration: Long): Boolean {
    return when (fastSeek) {
        // 始终启用快速快进/后退
        FastSeek.ENABLE -> true
        // 始终禁用快速快进/后退
        FastSeek.DISABLE -> false
        // 自动模式：当视频时长大于等于最小快速快进/后退时长时启用
        FastSeek.AUTO -> duration >= minDurationForFastSeek
    }
}
