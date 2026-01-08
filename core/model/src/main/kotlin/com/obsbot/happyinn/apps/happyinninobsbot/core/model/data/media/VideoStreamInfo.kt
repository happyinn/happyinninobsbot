package com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media

/**
 * 视频流信息数据类
 * 
 * 用于存储视频中视频流的详细信息，包括编解码器、分辨率、帧率等技术参数
 */

import java.io.Serializable

/**
 * 视频流信息数据类
 * 
 * @property index 视频流索引
 * @property title 视频流标题
 * @property codecName 视频编解码器名称
 * @property language 视频流语言
 * @property disposition 视频流属性标志
 * @property bitRate 比特率（bps）
 * @property frameRate 帧率
 * @property frameWidth 帧宽度
 * @property frameHeight 帧高度
 */
data class VideoStreamInfo(
    val index: Int,              // 视频流索引
    val title: String?,         // 视频流标题
    val codecName: String,      // 视频编解码器名称
    val language: String?,      // 视频流语言
    val disposition: Int,       // 视频流属性标志
    val bitRate: Long,          // 比特率（bps）
    val frameRate: Double,      // 帧率
    val frameWidth: Int,        // 帧宽度
    val frameHeight: Int,       // 帧高度
) : Serializable