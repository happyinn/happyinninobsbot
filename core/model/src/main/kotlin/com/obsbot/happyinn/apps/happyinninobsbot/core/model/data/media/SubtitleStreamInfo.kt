package com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media

/**
 * 字幕流信息数据类
 * 
 * 用于存储视频中字幕流的详细信息，包括编解码器、语言、标题等信息
 */

import java.io.Serializable

/**
 * @property index 字幕流索引
 * @property title 字幕流标题
 * @property codecName 字幕编解码器名称
 * @property language 字幕语言
 * @property disposition 字幕流属性标志
 */
data class SubtitleStreamInfo(
    val index: Int,              // 字幕流索引
    val title: String?,         // 字幕流标题
    val codecName: String,      // 字幕编解码器名称
    val language: String?,      // 字幕语言
    val disposition: Int,       // 字幕流属性标志
) : Serializable