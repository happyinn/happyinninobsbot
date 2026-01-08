package com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media

/**
 * 媒体视图模式枚举
 * 
 * 定义了媒体播放器的不同视图模式选项
 */
enum class MediaViewMode {
    FOLDER_TREE,  // 文件夹树视图，显示完整的文件夹层次结构
    FOLDERS,      // 文件夹视图，只显示当前级别的文件夹
    VIDEOS,       // 视频视图，只显示视频文件
}