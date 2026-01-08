package com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media

/**
 * 文件夹数据类
 * 
 * 用于表示包含视频文件和子文件夹的文件夹，包含了文件夹的基本信息和计算属性
 */

import java.io.Serializable

/**
 * @property name 文件夹名称
 * @property path 文件夹路径
 * @property dateModified 文件夹最后修改时间
 * @property parentPath 父文件夹路径，可为空
 * @property formattedMediaSize 格式化后的媒体总大小
 * @property mediaList 文件夹中的视频列表
 * @property folderList 文件夹中的子文件夹列表
 */
data class Folder(
    val name: String,                          // 文件夹名称
    val path: String,                          // 文件夹路径
    val dateModified: Long,                    // 文件夹最后修改时间
    val parentPath: String? = null,            // 父文件夹路径，可为空
    val formattedMediaSize: String = "",       // 格式化后的媒体总大小
    val mediaList: List<Video> = emptyList(),   // 文件夹中的视频列表
    val folderList: List<Folder> = emptyList(), // 文件夹中的子文件夹列表
) : Serializable {

    /** 计算属性：所有媒体文件的总大小 */
    val mediaSize: Long = mediaList.sumOf { it.size } + folderList.sumOf { it.mediaSize }
    
    /** 计算属性：所有媒体文件的总时长 */
    val mediaDuration: Long = mediaList.sumOf { it.duration } + folderList.sumOf { it.mediaDuration }
    
    /** 计算属性：递归获取所有子文件夹中的视频列表 */
    val allMediaList: List<Video> = mediaList + folderList.flatMap { it.allMediaList }
    
    /** 计算属性：最近播放的视频 */
    val recentlyPlayedVideo: Video? = allMediaList.recentPlayed()
    
    /** 计算属性：第一个视频 */
    val firstVideo: Video? = allMediaList.firstOrNull()

    /**
     * 检查视频是否是最近播放的视频
     * 
     * @param video 要检查的视频
     * @return 如果是最近播放的视频返回 true，否则返回 false
     */
    fun isRecentlyPlayedVideo(video: Video?): Boolean {
        if (recentlyPlayedVideo == null) return false
        if (video == null) return false
        return video.path == recentlyPlayedVideo.path
    }

    companion object {
        /** 根文件夹实例 */
        val rootFolder = Folder(
            name = "Root",
            path = "/",
            dateModified = System.currentTimeMillis(),
        )

        /** 示例文件夹实例 */
        val sample = Folder(
            name = "Folder 1",
            path = "/storage/emulated/0/DCIM/Camera/Live Photos",
            dateModified = 2000,
            formattedMediaSize = "1KB",
        )
    }
}