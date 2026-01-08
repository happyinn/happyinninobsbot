package com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media

/**
 * 媒体配置数据类
 * 
 * 用于存储媒体播放器的各种配置设置，包括排序方式、显示选项和字段显示设置
 */

import kotlinx.serialization.Serializable

@Serializable
data class MediaConfig (
    // 播放器的一些设置
    val sortBy: Sort.By = Sort.By.TITLE,                      // 排序方式，默认按标题排序
    val sortOrder: Sort.Order = Sort.Order.ASCENDING,        // 排序顺序，默认升序
    val showFloatingPlayButton: Boolean = true,             // 是否显示悬浮播放按钮，默认显示
    val excludeFolders: List<String> = emptyList(),          // 排除的文件夹列表
    val mediaViewMode: MediaViewMode = MediaViewMode.FOLDERS, // 媒体视图模式，默认文件夹模式
    val mediaLayoutMode: MediaLayoutMode = MediaLayoutMode.LIST, // 媒体布局模式，默认列表模式

    // 字段显示设置
    val showDurationField: Boolean = true,                  // 是否显示时长字段，默认显示
    val showExtensionField: Boolean = false,                // 是否显示扩展名字段，默认不显示
    val showPathField: Boolean = true,                      // 是否显示路径字段，默认显示
    val showResolutionField: Boolean = false,               // 是否显示分辨率字段，默认不显示
    val showSizeField: Boolean = false,                     // 是否显示大小字段，默认不显示
    val showThumbnailField: Boolean = true,                 // 是否显示缩略图字段，默认显示
    val showPlayedProgress: Boolean = true,                 // 是否显示播放进度，默认显示
)