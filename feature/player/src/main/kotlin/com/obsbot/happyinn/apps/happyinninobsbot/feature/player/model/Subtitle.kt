package com.obsbot.happyinn.apps.happyinninobsbot.feature.player.model

import android.net.Uri

/**
 * 外部/内置字幕描述模型
 * @param name 字幕名称
 * @param uri 字幕文件 Uri
 * @param isSelected 是否默认选中
 */
data class Subtitle(
    val name: String?,
    val uri: Uri,
    val isSelected: Boolean,
)
