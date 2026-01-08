package com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions

import android.graphics.Typeface
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.Font

/**
 * Font 扩展函数，用于将自定义的 Font 枚举转换为 Android 系统的 Typeface 对象
 */

/**
 * 将 Font 枚举转换为 Android 系统的 Typeface 对象
 * @return 对应的 Typeface 对象
 */
fun Font.toTypeface(): Typeface = when (this) {
    Font.DEFAULT -> Typeface.DEFAULT      // 默认字体
    Font.MONOSPACE -> Typeface.MONOSPACE  // 等宽字体
    Font.SANS_SERIF -> Typeface.SANS_SERIF // 无衬线字体
    Font.SERIF -> Typeface.SERIF          // 衬线字体
}
