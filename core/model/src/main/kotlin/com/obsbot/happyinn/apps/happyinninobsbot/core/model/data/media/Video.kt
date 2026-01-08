package com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media

/**
 * 视频数据类
 * 
 * 用于表示视频文件的详细信息，包括基本属性、播放状态、媒体流信息等
 */

import java.io.Serializable
import java.util.Date

/**
 * 视频文件数据类
 * 
 * @property id 视频唯一标识符
 * @property path 视频文件路径
 * @property parentPath 父文件夹路径
 * @property duration 视频时长（毫秒）
 * @property uriString 视频Uri字符串
 * @property nameWithExtension 带扩展名的文件名
 * @property width 视频宽度
 * @property height 视频高度
 * @property size 视频文件大小（字节）
 * @property playbackPosition 上次播放位置（毫秒）
 * @property dateModified 文件最后修改时间（毫秒）
 * @property formattedDuration 格式化后的时长字符串
 * @property formattedFileSize 格式化后的文件大小字符串
 * @property format 视频格式
 * @property thumbnailPath 缩略图路径
 * @property lastPlayedAt 最后播放时间
 * @property videoStream 视频流信息
 * @property audioStreams 音频流列表
 * @property subtitleStreams 字幕流列表
 */
data class Video(
    val id: Long,                          // 视频唯一标识符
    val path: String,                      // 视频文件路径
    val parentPath: String = "",           // 父文件夹路径
    val duration: Long,                    // 视频时长（毫秒）
    val uriString: String,                 // 视频Uri字符串
    val nameWithExtension: String,         // 带扩展名的文件名
    val width: Int,                        // 视频宽度
    val height: Int,                       // 视频高度
    val size: Long,                        // 视频文件大小（字节）
    val playbackPosition: Long = 200,      // 上次播放位置（毫秒）
    val dateModified: Long = 0,            // 文件最后修改时间（毫秒）
    val formattedDuration: String = "",     // 格式化后的时长字符串
    val formattedFileSize: String = "",     // 格式化后的文件大小字符串
    val format: String? = null,            // 视频格式
    val thumbnailPath: String? = null,     // 缩略图路径
    val lastPlayedAt: Date? = null,         // 最后播放时间
    val videoStream: VideoStreamInfo? = null, // 视频流信息
    val audioStreams: List<AudioStreamInfo> = emptyList(), // 音频流列表
    val subtitleStreams: List<SubtitleStreamInfo> = emptyList(), // 字幕流列表
) : Serializable {

    /** 计算属性：不带扩展名的文件名 */
    val displayName: String = nameWithExtension.substringBeforeLast(".")
    
    /** 计算属性：播放进度百分比（0.0-1.0） */
    val playedPercentage: Float = (playbackPosition.toFloat() / duration.toFloat()).takeIf { playbackPosition >= 0 } ?: 1f

    companion object {
        /** 示例视频对象 */
        val sample = Video(
            id = 8,
            path = "/storage/emulated/0/Download/Avengers Endgame (2019) BluRay x264.mp4",
            parentPath = "/storage/emulated/0/Download",
            uriString = "",
            nameWithExtension = "Avengers Endgame (2019) BluRay x264.mp4",
            duration = 1000,
            width = 1920,
            height = 1080,
            size = 1000,
            formattedDuration = "29.36",
            formattedFileSize = "320KB",
            playbackPosition = 200,
        )
    }
}

/**
 * 从视频列表中获取最近播放的视频
 * 
 * @return 最近播放的视频，如果没有则返回null
 */
fun List<Video>.recentPlayed(): Video? = 
    filter { it.lastPlayedAt != null }.sortedByDescending { it.lastPlayedAt?.time }.firstOrNull()