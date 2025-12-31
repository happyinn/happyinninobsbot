package com.obsbot.happyinn.apps.happyinninobsbot.core.model.data

import kotlinx.datetime.Instant
//TODO 待完善
/**
 * 视频资源数据�?
 *
 * 外部数据层表示的一个完整的NiA新闻资源（视频形式）
 * 包含视频的基本信息、内容和关联话题等数据
 */
data class VideoResource(
    /**
     * 视频资源的唯一标识�?
     */
    val id: String,

    /**
     * 视频标题
     */
    val title: String,

    /**
     * 视频内容描述
     */
    val content: String,

    /**
     * 视频的访问URL地址
     */
    val url: String,

    /**
     * 视频头部图片的URL地址，可能为空
     */
    val headerImageUrl: String?,

    /**
     * 视频发布时间
     */
    val publishDate: Instant,

    /**
     * 视频资源类型标识
     */
    val type: String,

    /**
     * 与该视频相关联的话题列表
     */
    val topics: List<Topic>,
)
