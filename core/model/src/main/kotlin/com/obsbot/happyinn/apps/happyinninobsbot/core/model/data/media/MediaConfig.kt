package com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media

import kotlinx.serialization.Serializable

@Serializable
data class MediaConfig (
    //播放器的一些设置
    val sortBy: Sort.By = Sort.By.TITLE,
    val sortOrder: Sort.Order = Sort.Order.ASCENDING,
    val showFloatingPlayButton: Boolean = true,
    val excludeFolders: List<String> = emptyList(),
    val mediaViewMode: MediaViewMode = MediaViewMode.FOLDERS,
    val mediaLayoutMode: MediaLayoutMode = MediaLayoutMode.LIST,

    // Fields
    val showDurationField: Boolean = true,
    val showExtensionField: Boolean = false,
    val showPathField: Boolean = true,
    val showResolutionField: Boolean = false,
    val showSizeField: Boolean = false,
    val showThumbnailField: Boolean = true,
    val showPlayedProgress: Boolean = true,
)
