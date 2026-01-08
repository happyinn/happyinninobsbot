package com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media

/**
 * 音频流信息数据类
 * 
 * 用于存储音频流的详细信息，包括编解码器、语言、采样率、声道等信息
 * 
 * @property index 音频流索引
 * @property title 音频流标题
 * @property codecName 编解码器名称
 * @property language 音频流语言
 * @property disposition 音频流属性标志
 * @property bitRate 比特率（bps）
 * @property sampleFormat 采样格式
 * @property sampleRate 采样率（Hz）
 * @property channels 声道数量
 * @property channelLayout 声道布局描述
 */

import java.io.Serializable

data class AudioStreamInfo(
    val index: Int,                  // 音频流索引
    val title: String?,             // 音频流标题
    val codecName: String,          // 编解码器名称
    val language: String?,          // 音频流语言
    val disposition: Int,           // 音频流属性标志
    val bitRate: Long,              // 比特率（bps）
    val sampleFormat: String?,      // 采样格式
    val sampleRate: Int,            // 采样率（Hz）
    val channels: Int,              // 声道数量
    val channelLayout: String?,     // 声道布局描述
) : Serializable