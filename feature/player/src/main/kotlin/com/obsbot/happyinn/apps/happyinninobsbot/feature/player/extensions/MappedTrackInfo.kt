package com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions

import androidx.media3.common.C
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.trackselection.MappingTrackSelector

/**
 * Returns whether the specified renderer is available.
 *
 * @param type The type of the renderer.
 * @return Whether the specified renderer is available.
 *
 * 判断指定类型的渲染器是否可用（是否存在对应的轨道组）
 */
@UnstableApi
fun MappingTrackSelector.MappedTrackInfo.isRendererAvailable(
    type: @C.TrackType Int,
): Boolean {
    for (i in 0 until rendererCount) {
        if (getTrackGroups(i).length == 0) continue
        if (type == getRendererType(i)) return true
    }
    return false
}
