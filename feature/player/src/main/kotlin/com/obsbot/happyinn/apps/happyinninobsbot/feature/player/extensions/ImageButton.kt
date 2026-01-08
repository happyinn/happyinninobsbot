package com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions

import android.content.Context
import android.widget.ImageButton
import androidx.core.content.ContextCompat

/**
 * ImageButton 扩展：便捷设置图标
 */
fun ImageButton.setImageDrawable(context: Context, id: Int) {
    setImageDrawable(ContextCompat.getDrawable(context, id))
}
