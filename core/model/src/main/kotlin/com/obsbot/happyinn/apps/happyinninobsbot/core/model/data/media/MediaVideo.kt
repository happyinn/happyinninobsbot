package com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media
import android.net.Uri
/**
 * 视频资源数据类
 *
 * 外部数据层表示的一个完整的视频资源
 * 包含视频的基本信息、内容和关联话题等数据
 * 
 * @property id 视频资源唯一标识符
 * @property uri 视频资源的Uri
 * @property size 视频文件大小
 * @property width 视频宽度
 * @property height 视频高度
 * @property data 视频数据路径
 * @property duration 视频时长
 * @property dateModified 视频最后修改时间
 */
data class MediaVideo(
    val id: Long,                // 视频资源唯一标识符
    val uri: Uri,                // 视频资源的Uri
    val size: Long,              // 视频文件大小
    val width: Int,              // 视频宽度
    val height: Int,             // 视频高度
    val data: String,            // 视频数据路径
    val duration: Long,          // 视频时长
    val dateModified: Long,      // 视频最后修改时间
)