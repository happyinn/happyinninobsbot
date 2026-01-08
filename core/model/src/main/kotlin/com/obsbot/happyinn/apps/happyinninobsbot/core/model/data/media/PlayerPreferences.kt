package com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media

import kotlinx.serialization.Serializable

/**
 * 播放器偏好设置数据类
 * 
 * 用于存储视频播放器的各种偏好设置，包括播放、显示、控制、音频、字幕和解码器等方面
 */



@Serializable
data class PlayerPreferences(
    // 播放相关设置
    val resume: Resume = Resume.YES,                           // 恢复播放模式，默认总是恢复
    val rememberPlayerBrightness: Boolean = false,            // 是否记住播放器亮度，默认不记住
    val playerBrightness: Float = 0.5f,                       // 播放器亮度，默认50%
    val fastSeek: FastSeek = FastSeek.AUTO,                   // 快速跳转模式，默认自动
    val minDurationForFastSeek: Long = 120000L,               // 快速跳转的最小视频时长，默认120秒
    val rememberSelections: Boolean = true,                   // 是否记住用户选择，默认记住
    val playerScreenOrientation: ScreenOrientation = ScreenOrientation.VIDEO_ORIENTATION, // 播放器屏幕方向，默认跟随视频方向
    val controlButtonsPosition: ControlButtonsPosition = ControlButtonsPosition.LEFT, // 控制按钮位置，默认左侧
    val playerVideoZoom: VideoZoom = VideoZoom.BEST_FIT,      // 视频缩放模式，默认最佳适配
    val defaultPlaybackSpeed: Float = 1.0f,                  // 默认播放速度，默认1.0x
    val controllerAutoHideTimeout: Int = 2,                  // 控制器自动隐藏超时时间，默认2秒
    val seekIncrement: Int = 10,                             // 每次跳转的增量，默认10秒
    val autoplay: Boolean = true,                            // 是否自动播放，默认自动播放
    val autoPip: Boolean = true,                             // 是否自动进入画中画模式，默认开启
    val autoBackgroundPlay: Boolean = false,                 // 是否自动后台播放，默认关闭
    val loopMode: LoopMode = LoopMode.OFF,                   // 循环播放模式，默认关闭

    // 控制（手势）设置
    val useSwipeControls: Boolean = true,                    // 是否使用滑动控制，默认开启
    val useSeekControls: Boolean = true,                     // 是否使用跳转控制，默认开启
    val useZoomControls: Boolean = true,                     // 是否使用缩放控制，默认开启
    val doubleTapGesture: DoubleTapGesture = DoubleTapGesture.BOTH, // 双击手势功能，默认同时支持播放/暂停和快进/快退
    val useLongPressControls: Boolean = false,               // 是否使用长按控制，默认关闭
    val longPressControlsSpeed: Float = 2.0f,                // 长按控制速度，默认2.0x

    // 音频偏好设置
    val preferredAudioLanguage: String = "",                 // 首选音频语言，默认空
    val pauseOnHeadsetDisconnect: Boolean = true,            // 耳机断开时是否暂停，默认暂停
    val requireAudioFocus: Boolean = true,                  // 是否需要音频焦点，默认需要
    val showSystemVolumePanel: Boolean = true,              // 是否显示系统音量面板，默认显示
    val shouldUseVolumeBoost: Boolean = false,               // 是否使用音量增强，默认关闭

    // 字幕偏好设置
    val useSystemCaptionStyle: Boolean = false,              // 是否使用系统字幕样式，默认不使用
    val preferredSubtitleLanguage: String = "",             // 首选字幕语言，默认空
    val subtitleTextEncoding: String = "",                  // 字幕文本编码，默认空
    val subtitleTextSize: Int = 20,                         // 字幕文本大小，默认20
    val subtitleBackground: Boolean = false,                 // 是否显示字幕背景，默认不显示
    val subtitleFont: Font = Font.DEFAULT,                   // 字幕字体，默认系统字体
    val subtitleTextBold: Boolean = true,                   // 字幕文本是否加粗，默认加粗
    val applyEmbeddedStyles: Boolean = true,                // 是否应用内嵌样式，默认应用

    // 解码器偏好设置
    val decoderPriority: DecoderPriority = DecoderPriority.PREFER_DEVICE, // 解码器优先级，默认优先使用设备硬件解码器
)