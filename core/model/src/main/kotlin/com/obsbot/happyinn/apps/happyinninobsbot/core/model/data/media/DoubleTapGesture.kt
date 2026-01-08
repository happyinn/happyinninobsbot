package com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media

/**
 * 双击手势枚举
 * 
 * 定义了视频播放器中双击屏幕时的不同手势行为选项
 */
enum class DoubleTapGesture {
    PLAY_PAUSE,                  // 双击播放/暂停视频
    FAST_FORWARD_AND_REWIND,     // 双击快进/快退
    BOTH,                       // 同时支持播放/暂停和快进/快退
    NONE,                       // 禁用双击手势
}