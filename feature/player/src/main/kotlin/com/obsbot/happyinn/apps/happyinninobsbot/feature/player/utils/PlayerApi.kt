/**
 * 播放器对外 API 访问工具
 * 
 * 本文件定义了 PlayerApi 类，用于处理播放器与外部应用之间的通信。
 * 主要功能包括解析外部启动参数、生成播放结果返回 Intent、处理字幕数据等。
 */
package com.obsbot.happyinn.apps.happyinninobsbot.feature.player.utils

import android.content.Intent
import android.net.Uri
import androidx.media3.common.C
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.PlayerActivity
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions.getParcelableUriArray
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.model.Subtitle

/**
 * 播放器对外 API 访问工具类
 * 
 * 功能说明：
 * - 解析外部启动时传入的播放参数（标题、进度、字幕等）
 * - 生成播放结束后返回结果的 Intent
 * - 提供字幕解析、播放结果封装等辅助能力
 * - 支持与其他应用进行播放器功能的交互
 * 
 * 适用场景：
 * - 外部应用通过 Intent 启动播放器时传递参数
 * - 播放器结束播放后返回结果给调用应用
 * - 实现跨应用的播放器控制
 * 
 * @param activity 播放器 Activity 实例
 */
class PlayerApi(val activity: PlayerActivity) {

    /** 外部 Intent 传递的额外参数 */
    private val extras = activity.intent.extras
    
    /**
     * 是否通过 API 方式访问播放器
     * 
     * @return true 表示外部应用通过 API 调用播放器，false 表示正常启动
     */
    val isApiAccess: Boolean get() = extras != null
    
    /**
     * 是否包含播放进度参数
     * 
     * @return true 表示外部传递了播放进度，false 表示未传递
     */
    val hasPosition: Boolean get() = extras?.containsKey(API_POSITION) == true
    
    /**
     * 是否包含视频标题参数
     * 
     * @return true 表示外部传递了视频标题，false 表示未传递
     */
    val hasTitle: Boolean get() = extras?.containsKey(API_TITLE) == true
    
    /**
     * 是否需要返回播放结果
     * 
     * @return true 表示需要返回播放结果，false 表示不需要
     */
    val shouldReturnResult: Boolean get() = extras?.containsKey(API_RETURN_RESULT) == true
    
    /**
     * 播放起始位置（毫秒）
     * 
     * @return 播放起始位置，null 表示从开头播放
     */
    val position: Int? get() = if (hasPosition) extras?.getInt(API_POSITION) else null
    
    /**
     * 视频标题
     * 
     * @return 视频标题，null 表示使用默认标题
     */
    val title: String? get() = if (hasTitle) extras?.getString(API_TITLE) else null

    /**
     * 获取外部传递的字幕列表
     * 
     * 功能说明：
     * 解析外部 Intent 中传递的字幕数据，生成 Subtitle 对象列表
     * 
     * 数据结构：
     * - API_SUBS: 字幕文件 URI 数组
     * - API_SUBS_NAME: 字幕名称数组
     * - API_SUBS_ENABLE: 默认启用的字幕 URI 数组
     * 
     * @return 字幕对象列表，无字幕时返回空列表
     */
    fun getSubs(): List<Subtitle> {
        // 检查是否存在额外参数
        if (extras == null) return emptyList()
        // 检查是否包含字幕参数
        if (!extras.containsKey(API_SUBS)) return emptyList()

        // 获取字幕 URI 数组
        val subs = extras.getParcelableUriArray(API_SUBS) ?: return emptyList()
        // 获取字幕名称数组
        val subsName = extras.getStringArray(API_SUBS_NAME)
        // 获取默认启用的字幕 URI
        val subsEnable = extras.getParcelableUriArray(API_SUBS_ENABLE)
        // 解析默认字幕 URI
        val defaultSub = if (!subsEnable.isNullOrEmpty()) subsEnable[0] as Uri else null

        // 将字幕 URI 转换为 Subtitle 对象列表
        return subs.mapIndexed { index, parcelable ->
            val subtitleUri = parcelable as Uri
            // 获取对应的字幕名称
            val subtitleName = subsName?.let { if (it.size > index) it[index] else null }
            Subtitle(
                name = subtitleName,
                uri = subtitleUri,
                isSelected = subtitleUri == defaultSub,
            )
        }
    }

    /**
     * 生成播放结束返回结果的 Intent
     * 
     * 功能说明：
     * 封装播放结果数据，生成可供返回给调用应用的 Intent
     * 
     * 参数说明：
     * @param isPlaybackFinished 是否播放完成
     * @param duration 媒体总时长（毫秒）
     * @param position 播放结束时的位置（毫秒）
     * 
     * @return 包含播放结果的 Intent 对象
     */
    fun getResult(isPlaybackFinished: Boolean, duration: Long, position: Long): Intent {
        return Intent(API_RESULT_INTENT).apply {
            if (isPlaybackFinished) {
                // 播放完成，标记结束原因
                putExtra(API_END_BY, API_END_BY_COMPLETION)
            } else {
                // 用户主动结束，标记结束原因并返回当前进度
                putExtra(API_END_BY, API_END_BY_USER)
                // 仅在时长有效时添加时长参数
                if (duration != C.TIME_UNSET) putExtra(API_DURATION, duration.toInt())
                // 仅在位置有效时添加位置参数
                if (position != C.TIME_UNSET) putExtra(API_POSITION, position.toInt())
            }
        }
    }

    /**
     * API 常量定义
     * 
     * 包含 PlayerApi 类使用的所有常量，用于外部应用与播放器之间的参数传递
     */
    companion object {
        /** 视频标题参数名 */
        const val API_TITLE = "title"
        /** 播放位置参数名（毫秒） */
        const val API_POSITION = "position"
        /** 媒体时长参数名（毫秒） */
        const val API_DURATION = "duration"
        /** 是否返回结果参数名 */
        const val API_RETURN_RESULT = "return_result"
        /** 播放结束原因参数名 */
        const val API_END_BY = "end_by"
        /** 字幕 URI 数组参数名 */
        const val API_SUBS = "subs"
        /** 默认启用字幕 URI 数组参数名 */
        const val API_SUBS_ENABLE = "subs.enable"
        /** 字幕名称数组参数名 */
        const val API_SUBS_NAME = "subs.name"
        /** 播放结果 Intent Action */
        const val API_RESULT_INTENT = "com.mxtech.intent.result.VIEW"

        /** 播放结束原因：用户主动结束 */
        private const val API_END_BY_USER = "user"
        /** 播放结束原因：播放完成 */
        private const val API_END_BY_COMPLETION = "playback_completion"
    }
}
