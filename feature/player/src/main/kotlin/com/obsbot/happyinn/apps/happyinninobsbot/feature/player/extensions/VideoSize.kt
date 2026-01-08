package com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions

import androidx.media3.common.VideoSize

/**
 * VideoSize 扩展：判断当前分辨率是否为竖屏方向
 */
val VideoSize.isPortrait: Boolean
    get() = this.height > this.width
