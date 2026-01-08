package com.obsbot.happyinn.apps.happyinninobsbot.feature.player.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import androidx.media3.session.MediaController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.obsbot.happyinn.apps.happyinninobsbot.extensions.round
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.databinding.PlaybackSpeedBinding
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.service.getSkipSilenceEnabled
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.service.setSkipSilenceEnabled
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.service.setSpeed
import kotlinx.coroutines.launch
import  com.obsbot.happyinn.apps.happyinninobsbot.core.ui.R as coreUiR

/**
 * 播放速度控制对话框
 * - 调整播放速度（滑杆 + 常用快捷按钮）
 * - 控制跳过静音开关
 */
class PlaybackSpeedControlsDialogFragment(
    private val mediaController: MediaController, // 媒体控制器
) : DialogFragment() {

    // 对话框布局绑定
    private lateinit var binding: PlaybackSpeedBinding

    /**
     * 创建对话框
     * @param savedInstanceState 保存的实例状态
     * @return 创建的对话框
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // 初始化布局绑定
        binding = PlaybackSpeedBinding.inflate(layoutInflater)

        return activity?.let { activity ->
            binding.apply {
                // 获取当前播放速度
                val currentSpeed = mediaController.playbackParameters.speed
                // 显示当前播放速度
                speedText.text = currentSpeed.toString()
                // 设置滑块初始值
                speed.value = currentSpeed.round(1)
                // 异步获取并设置跳过静音功能状态
                lifecycleScope.launch {
                    skipSilence.isChecked = mediaController.getSkipSilenceEnabled()
                }

                // 滑块值变化监听器
                speed.addOnChangeListener { _, _, _ ->
                    val newSpeed = speed.value.round(1)
                    // 设置新的播放速度
                    mediaController.setSpeed(newSpeed)
                    // 更新速度文本显示
                    speedText.text = newSpeed.toString()
                }
                // 增加速度按钮点击事件
                incSpeed.setOnClickListener {
                    if (speed.value < 4.0f) {
                        speed.value = (speed.value + 0.1f).round(1)
                    }
                }
                // 减少速度按钮点击事件
                decSpeed.setOnClickListener {
                    if (speed.value > 0.2f) {
                        speed.value = (speed.value - 0.1f).round(1)
                    }
                }
                // 重置速度按钮点击事件（恢复到1.0x）
                resetSpeed.setOnClickListener { speed.value = 1.0f }
                // 0.2x 速度按钮点击事件
                button02x.setOnClickListener { speed.value = 0.2f }
                // 0.5x 速度按钮点击事件
                button05x.setOnClickListener { speed.value = 0.5f }
                // 1.0x 速度按钮点击事件
                button10x.setOnClickListener { speed.value = 1.0f }
                // 1.5x 速度按钮点击事件
                button15x.setOnClickListener { speed.value = 1.5f }
                // 2.0x 速度按钮点击事件
                button20x.setOnClickListener { speed.value = 2.0f }
                // 2.5x 速度按钮点击事件
                button25x.setOnClickListener { speed.value = 2.5f }
                // 3.0x 速度按钮点击事件
                button30x.setOnClickListener { speed.value = 3.0f }
                // 3.5x 速度按钮点击事件
                button35x.setOnClickListener { speed.value = 3.5f }
                // 4.0x 速度按钮点击事件
                button40x.setOnClickListener { speed.value = 4.0f }

                // 跳过静音开关状态变化监听器
                skipSilence.setOnCheckedChangeListener { _, isChecked ->
                    mediaController.setSkipSilenceEnabled(isChecked)
                }
            }

            // 创建对话框并设置标题和布局
            val builder = MaterialAlertDialogBuilder(activity)
            builder.setTitle(getString(coreUiR.string.select_playback_speed))
                .setView(binding.root)
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}
