package com.obsbot.happyinn.apps.happyinninobsbot.feature.player.service

import android.net.Uri
import android.os.Bundle
import androidx.annotation.OptIn
import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.session.MediaController
import androidx.media3.session.SessionCommand
import kotlinx.coroutines.guava.await

enum class CustomCommands(val customAction: String) {
    // 添加外部字幕轨道的命令
    ADD_SUBTITLE_TRACK(customAction = "ADD_SUBTITLE_TRACK"),
    // 切换音频轨道的命令
    SWITCH_AUDIO_TRACK(customAction = "SWITCH_AUDIO_TRACK"),
    // 切换字幕轨道的命令
    SWITCH_SUBTITLE_TRACK(customAction = "SWITCH_SUBTITLE_TRACK"),
    // 设置是否启用跳过静音功能的命令
    SET_SKIP_SILENCE_ENABLED(customAction = "SET_SKIP_SILENCE_ENABLED"),
    // 获取跳过静音功能启用状态的命令
    GET_SKIP_SILENCE_ENABLED(customAction = "GET_SKIP_SILENCE_ENABLED"),
    // 设置播放速度的命令
    SET_PLAYBACK_SPEED(customAction = "SET_PLAYBACK_SPEED"),
    // 获取音频会话ID的命令
    GET_AUDIO_SESSION_ID(customAction = "GET_AUDIO_SESSION_ID"),
    // 停止播放器会话的命令
    STOP_PLAYER_SESSION(customAction = "STOP_PLAYER_SESSION"),
    ;

    // 将自定义动作字符串转换为 SessionCommand 对象
    val sessionCommand = SessionCommand(customAction, Bundle.EMPTY)

    companion object {
        /**
         * 根据 SessionCommand 对象查找对应的 CustomCommands 枚举值
         * @param sessionCommand 要查找的 SessionCommand 对象
         * @return 对应的 CustomCommands 枚举值，如果未找到则返回 null
         */
        fun fromSessionCommand(sessionCommand: SessionCommand): CustomCommands? {
            return entries.find { it.customAction == sessionCommand.customAction }
        }

        /**
         * 获取所有自定义命令的 SessionCommand 列表
         * @return 所有自定义命令的 SessionCommand 列表
         */
        fun asSessionCommands(): List<SessionCommand> {
            return entries.map { it.sessionCommand }
        }

        // 用于传递参数的键值常量
        const val SUBTITLE_TRACK_URI_KEY = "subtitle_track_uri"  // 外部字幕文件的 URI
        const val AUDIO_TRACK_INDEX_KEY = "audio_track_index"    // 音频轨道索引
        const val SUBTITLE_TRACK_INDEX_KEY = "subtitle_track_index" // 字幕轨道索引
        const val SKIP_SILENCE_ENABLED_KEY = "skip_silence_enabled" // 跳过静音功能启用状态
        const val PLAYBACK_SPEED_KEY = "playback_speed"          // 播放速度
        const val AUDIO_SESSION_ID_KEY = "audio_session_id"      // 音频会话 ID
    }
}


/**
 * 向播放器添加外部字幕轨道
 * @param uri 外部字幕文件的 URI
 */
fun MediaController.addSubtitleTrack(uri: Uri) {
    val args = Bundle().apply {
        // 将字幕文件 URI 转换为字符串并放入参数包中
        putString(CustomCommands.SUBTITLE_TRACK_URI_KEY, uri.toString())
    }
    // 发送添加字幕轨道的自定义命令
    sendCustomCommand(CustomCommands.ADD_SUBTITLE_TRACK.sessionCommand, args)
}


/**
 * 切换播放器的音频轨道
 * @param trackIndex 要切换到的音频轨道索引
 */
fun MediaController.switchAudioTrack(trackIndex: Int) {
    val args = Bundle().apply {
        // 将音频轨道索引放入参数包中
        putInt(CustomCommands.AUDIO_TRACK_INDEX_KEY, trackIndex)
    }
    // 发送切换音频轨道的自定义命令
    sendCustomCommand(CustomCommands.SWITCH_AUDIO_TRACK.sessionCommand, args)
}


/**
 * 切换播放器的字幕轨道
 * @param trackIndex 要切换到的字幕轨道索引
 */
fun MediaController.switchSubtitleTrack(trackIndex: Int) {
    val args = Bundle().apply {
        // 将字幕轨道索引放入参数包中
        putInt(CustomCommands.SUBTITLE_TRACK_INDEX_KEY, trackIndex)
    }
    // 发送切换字幕轨道的自定义命令
    sendCustomCommand(CustomCommands.SWITCH_SUBTITLE_TRACK.sessionCommand, args)
}


/**
 * 设置播放器是否启用跳过静音功能
 * @param enabled 是否启用跳过静音功能
 */
fun MediaController.setSkipSilenceEnabled(enabled: Boolean) {
    val args = Bundle().apply {
        // 将启用状态放入参数包中
        putBoolean(CustomCommands.SKIP_SILENCE_ENABLED_KEY, enabled)
    }
    // 发送设置跳过静音功能的自定义命令
    sendCustomCommand(CustomCommands.SET_SKIP_SILENCE_ENABLED.sessionCommand, args)
}


/**
 * 获取播放器跳过静音功能的启用状态
 * @return 跳过静音功能是否已启用
 */
suspend fun MediaController.getSkipSilenceEnabled(): Boolean {
    // 发送获取跳过静音启用状态的自定义命令
    val result = sendCustomCommand(CustomCommands.GET_SKIP_SILENCE_ENABLED.sessionCommand, Bundle.EMPTY)
    // 等待异步结果并从返回的数据中提取启用状态，默认值为 false
    return result.await().extras.getBoolean(CustomCommands.SKIP_SILENCE_ENABLED_KEY, false)
}


/**
 * 设置播放器的播放速度
 * @param speed 播放速度，例如 1.0f 表示正常速度，2.0f 表示两倍速
 */
fun MediaController.setSpeed(speed: Float) {
    val args = Bundle().apply {
        // 将播放速度放入参数包中
        putFloat(CustomCommands.PLAYBACK_SPEED_KEY, speed)
    }
    // 发送设置播放速度的自定义命令
    sendCustomCommand(CustomCommands.SET_PLAYBACK_SPEED.sessionCommand, args)
}


/**
 * 获取播放器的音频会话ID
 * @return 音频会话ID，如果未设置则返回 C.AUDIO_SESSION_ID_UNSET
 */
@OptIn(UnstableApi::class)  // 标记使用不稳定 API
suspend fun MediaController.getAudioSessionId(): Int {
    // 发送获取音频会话ID的自定义命令
    val result = sendCustomCommand(CustomCommands.GET_AUDIO_SESSION_ID.sessionCommand, Bundle.EMPTY)
    // 等待异步结果并从返回的数据中提取音频会话ID，默认值为未设置状态
    return result.await().extras.getInt(CustomCommands.AUDIO_SESSION_ID_KEY, C.AUDIO_SESSION_ID_UNSET)
}


/**
 * 停止播放器会话
 * 发送停止播放器会话的命令，通常用于完全停止播放并清理资源
 */
fun MediaController.stopPlayerSession() {
    // 发送停止播放器会话的自定义命令，无需参数
    sendCustomCommand(CustomCommands.STOP_PLAYER_SESSION.sessionCommand, Bundle.EMPTY)
}
