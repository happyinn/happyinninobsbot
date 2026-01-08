package com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions

import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.PlayerView

@UnstableApi
/**
 * 切换播放/暂停，同时保持控制器显示状态一致
 */
fun PlayerView.togglePlayPause() {
    this.controllerAutoShow = this.isControllerFullyVisible
    if (this.player?.isPlaying == true) {
        this.player?.pause()
    } else {
        this.player?.play()
    }
}
