package com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions

import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.TrackSelectionOverride
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.SeekParameters
import timber.log.Timber

/**
 * 切换指定类型的轨道（音频/字幕）
 *
 * @param trackType 要切换的轨道类型
 * @param trackIndex 要切换到的轨道索引，负数表示禁用该类型轨道
 *
 * 功能说明：
 * - trackIndex < 0 时禁用该类型轨道
 * - trackIndex 合法时切换到对应轨道
 */
fun Player.switchTrack(trackType: @C.TrackType Int, trackIndex: Int) {
    // 根据轨道类型确定日志输出文本
    val trackTypeText = when (trackType) {
        C.TRACK_TYPE_AUDIO -> "audio"      // 音频轨道
        C.TRACK_TYPE_TEXT -> "subtitle"    // 字幕轨道
        else -> throw IllegalArgumentException("Invalid track type: $trackType")  // 无效轨道类型抛出异常
    }

    if (trackIndex < 0) {
        // 当轨道索引为负数时，禁用该类型的轨道
        Timber.d("Disabling $trackTypeText")  // 记录禁用轨道的日志
        trackSelectionParameters = trackSelectionParameters
            .buildUpon()  // 构建当前轨道选择参数的副本
            .setTrackTypeDisabled(trackType, true)  // 设置指定类型的轨道为禁用状态
            .build()  // 构建新的轨道选择参数
    } else {
        // 获取当前播放项目中指定类型的轨道组
        val tracks = currentTracks.groups.filter { it.type == trackType }

        // 检查轨道是否存在以及索引是否有效
        if (tracks.isEmpty() || trackIndex >= tracks.size) {
            // 轨道不存在或索引超出范围时输出错误日志并返回
            Timber.d("Operation failed: Invalid track index: $trackIndex")
            return
        }

        // 记录设置轨道的日志
        Timber.d("Setting $trackTypeText track: $trackIndex")
        // 创建轨道选择覆盖对象，指定要选择的轨道组和轨道索引
        val trackSelectionOverride = TrackSelectionOverride(tracks[trackIndex].mediaTrackGroup, 0)

        // 更新轨道选择参数以强制选择指定轨道
        trackSelectionParameters = trackSelectionParameters
            .buildUpon()  // 构建当前轨道选择参数的副本
            .setTrackTypeDisabled(trackType, false)  // 确保该类型轨道未被禁用
            .setOverrideForType(trackSelectionOverride)  // 设置轨道选择覆盖
            .build()  // 构建新的轨道选择参数
    }
}

/**
 * 设置播放器的 Seek 参数（是否对齐关键帧）
 *
 * @param seekParameters 要设置的 Seek 参数
 */
@UnstableApi  // 标记为不稳定 API
fun Player.setSeekParameters(seekParameters: SeekParameters) {
    when (this) {
        // 仅当播放器实例为 ExoPlayer 时才设置 Seek 参数
        is ExoPlayer -> this.setSeekParameters(seekParameters)
    }
}

/**
 * 后退到指定位置，支持快速对齐关键帧
 *
 * @param positionMs 要后退到的位置（毫秒）
 * @param shouldFastSeek 是否快速对齐到最近的关键帧
 */
@UnstableApi  // 标记为不稳定 API
fun Player.seekBack(positionMs: Long, shouldFastSeek: Boolean = false) {
    // 检查当前是否有媒体项目正在播放
    if (currentMediaItem == null) return

    // 根据是否需要快速对齐关键帧设置不同的 Seek 参数
    setSeekParameters(if (shouldFastSeek) SeekParameters.PREVIOUS_SYNC else SeekParameters.DEFAULT)
    // 执行后退操作到指定位置
    this.seekTo(positionMs)
}

/**
 * 前进到指定位置，支持快速对齐关键帧
 *
 * @param positionMs 要前进到的位置（毫秒）
 * @param shouldFastSeek 是否快速对齐到最近的关键帧
 */
@UnstableApi  // 标记为不稳定 API
fun Player.seekForward(positionMs: Long, shouldFastSeek: Boolean = false) {
    // 检查当前是否有媒体项目正在播放
    if (currentMediaItem == null) return

    // 根据是否需要快速对齐关键帧设置不同的 Seek 参数
    setSeekParameters(if (shouldFastSeek) SeekParameters.NEXT_SYNC else SeekParameters.DEFAULT)
    // 执行前进操作到指定位置
    this.seekTo(positionMs)
}

/**
 * 为当前媒体项目添加额外的字幕配置
 *
 * @param subtitle 要添加的字幕配置
 */
fun Player.addAdditionalSubtitleConfiguration(subtitle: MediaItem.SubtitleConfiguration) {
    // 获取当前媒体项目，如果为空则返回
    val currentMediaItemLocal = currentMediaItem ?: return
    // 获取当前媒体项目的现有字幕配置列表
    val existingSubConfigurations = currentMediaItemLocal.localConfiguration?.subtitleConfigurations ?: emptyList()

    // 检查是否已经存在相同 ID 的字幕配置，如果存在则不重复添加
    if (existingSubConfigurations.any { it.id == subtitle.id }) {
        return
    }

    // 构建更新后的媒体项目，将新的字幕配置添加到现有配置列表中
    val updateMediaItem = currentMediaItemLocal
        .buildUpon()  // 构建当前媒体项目的副本
        .setSubtitleConfigurations(existingSubConfigurations + listOf(subtitle))  // 添加新的字幕配置
        .build()  // 构建更新后的媒体项目

    // 获取当前媒体项目的索引
    val index = currentMediaItemIndex
    // 在当前项目之后插入更新后的媒体项目
    addMediaItem(index + 1, updateMediaItem)
    // 删除原来的媒体项目
    removeMediaItem(index)
}
