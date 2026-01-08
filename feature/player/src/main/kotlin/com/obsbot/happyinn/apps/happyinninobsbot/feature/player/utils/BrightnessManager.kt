package com.obsbot.happyinn.apps.happyinninobsbot.feature.player.utils
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.PlayerActivity
import android.view.WindowManager
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions.currentBrightness
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions.swipeToShowStatusBars

/**
 * 亮度管理器
 * - 记录并设置当前屏幕亮度
 * - 将屏幕亮度控制在 0f~1f 范围
 * - 调整亮度后保持系统栏的隐藏状态
 */
class BrightnessManager(private val activity: PlayerActivity) {

    var currentBrightness = activity.currentBrightness
    val maxBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL

    val brightnessPercentage get() = (currentBrightness / maxBrightness).times(100).toInt()

    fun setBrightness(brightness: Float) {
        currentBrightness = brightness.coerceIn(0f, maxBrightness)
        val layoutParams = activity.window.attributes
        layoutParams.screenBrightness = currentBrightness
        activity.window.attributes = layoutParams

        // fixes a bug which makes the action bar reappear after changing the brightness
        activity.swipeToShowStatusBars()
    }
}
