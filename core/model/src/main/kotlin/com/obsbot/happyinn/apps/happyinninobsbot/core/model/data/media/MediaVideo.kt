package com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media
import android.net.Uri
/**
 * 视频资源数据类
 *
 * 外部数据层表示的一个完整的NiA新闻资源（视频形式）
 * 包含视频的基本信息、内容和关联话题等数据
 */
data class MediaVideo(
    val id: Long,
    val uri: Uri,
    val size: Long,
    val width: Int,
    val height: Int,
    val data: String,
    val duration: Long,
    val dateModified: Long,
)