package com.obsbot.happyinn.apps.happyinninobsbot.feature.player.dialogs
// 包声明：指定该类所在的包路径

import android.app.Dialog
// 导入 Dialog 类：Android 对话框的基类
import android.os.Bundle
// 导入 Bundle 类：用于存储和恢复数据
import androidx.fragment.app.DialogFragment
// 导入 DialogFragment 类：支持 Fragment 生命周期的对话框基类
import androidx.lifecycle.lifecycleScope
// 导入 lifecycleScope：与 Fragment 生命周期绑定的协程作用域
import androidx.media3.session.MediaController
// 导入 MediaController 类：Media3 媒体会话控制器，用于控制媒体播放
import com.google.android.material.dialog.MaterialAlertDialogBuilder
// 导入 MaterialAlertDialogBuilder 类：用于创建符合 Material Design 的对话框
import com.obsbot.happyinn.apps.happyinninobsbot.extensions.round
// 导入自定义扩展函数 round：用于将浮点数四舍五入到指定位数
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.databinding.PlaybackSpeedBinding
// 导入 PlaybackSpeedBinding 类：自动生成的布局绑定类，对应 playback_speed.xml
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.service.getSkipSilenceEnabled
// 导入扩展函数 getSkipSilenceEnabled：获取跳过静音功能的状态
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.service.setSkipSilenceEnabled
// 导入扩展函数 setSkipSilenceEnabled：设置跳过静音功能的状态
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.service.setSpeed
// 导入扩展函数 setSpeed：设置播放速度
import kotlinx.coroutines.launch
// 导入 launch 函数：用于启动协程
import  com.obsbot.happyinn.apps.happyinninobsbot.core.ui.R as coreUiR
// 导入核心UI模块的资源类，并指定别名为 coreUiR，避免与其他模块的R类冲突

/**
 * 播放速度控制对话框
 * - 调整播放速度（滑杆 + 常用快捷按钮）
 * - 控制跳过静音开关
 */
class PlaybackSpeedControlsDialogFragment(
    private val mediaController: MediaController, // 媒体控制器：用于控制媒体播放和获取播放状态
) : DialogFragment() {
    // 类声明：继承自 DialogFragment，表示这是一个对话框Fragment

    // 对话框布局绑定：使用 lateinit 延迟初始化，在 onCreateDialog 中进行初始化
    private lateinit var binding: PlaybackSpeedBinding

    /**
     * 创建对话框
     * @param savedInstanceState 保存的实例状态，用于恢复之前的状态
     * @return 创建的对话框实例
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // 重写 onCreateDialog 方法：DialogFragment 的生命周期方法，用于创建对话框
        // 初始化布局绑定：将 XML 布局转换为 View 对象，并获取绑定实例
        binding = PlaybackSpeedBinding.inflate(layoutInflater)

        // activity?.let：安全获取 Activity 上下文，避免空指针异常
        return activity?.let { activity ->
            // 使用 apply 作用域函数，简化对 binding 对象的多个属性操作
            binding.apply {
                // 获取当前播放速度：从媒体控制器的播放参数中获取当前速度
                val currentSpeed = mediaController.playbackParameters.speed
                // 显示当前播放速度：将当前速度转换为字符串并显示在文本视图中
                speedText.text = currentSpeed.toString()
                // 设置滑块初始值：将当前速度四舍五入到1位小数后设置到滑块上
                speed.value = currentSpeed.round(1)
                // 异步获取并设置跳过静音功能状态：使用协程避免阻塞主线程
                lifecycleScope.launch {
                    // 调用扩展函数获取跳过静音状态，并设置到开关上
                    skipSilence.isChecked = mediaController.getSkipSilenceEnabled()
                }

                // 滑块值变化监听器：当滑块值改变时触发
                speed.addOnChangeListener { _, _, _ ->
                    // 获取新的速度值，并四舍五入到1位小数
                    val newSpeed = speed.value.round(1)
                    // 设置新的播放速度：调用扩展函数设置媒体控制器的播放速度
                    mediaController.setSpeed(newSpeed)
                    // 更新速度文本显示：将新速度转换为字符串并更新文本视图
                    speedText.text = newSpeed.toString()
                }
                // 增加速度按钮点击事件：点击时增加播放速度
                incSpeed.setOnClickListener {
                    // 检查当前速度是否小于最大值4.0f
                    if (speed.value < 4.0f) {
                        // 增加0.1f速度，并四舍五入到1位小数
                        speed.value = (speed.value + 0.1f).round(1)
                    }
                }
                // 减少速度按钮点击事件：点击时减少播放速度
                decSpeed.setOnClickListener {
                    // 检查当前速度是否大于最小值0.2f
                    if (speed.value > 0.2f) {
                        // 减少0.1f速度，并四舍五入到1位小数
                        speed.value = (speed.value - 0.1f).round(1)
                    }
                }
                // 重置速度按钮点击事件：点击时恢复到正常速度1.0x
                resetSpeed.setOnClickListener { speed.value = 1.0f }
                // 0.2x 速度按钮点击事件：点击时设置播放速度为0.2x
                button02x.setOnClickListener { speed.value = 0.2f }
                // 0.5x 速度按钮点击事件：点击时设置播放速度为0.5x
                button05x.setOnClickListener { speed.value = 0.5f }
                // 1.0x 速度按钮点击事件：点击时设置播放速度为1.0x（正常速度）
                button10x.setOnClickListener { speed.value = 1.0f }
                // 1.5x 速度按钮点击事件：点击时设置播放速度为1.5x
                button15x.setOnClickListener { speed.value = 1.5f }
                // 2.0x 速度按钮点击事件：点击时设置播放速度为2.0x
                button20x.setOnClickListener { speed.value = 2.0f }
                // 2.5x 速度按钮点击事件：点击时设置播放速度为2.5x
                button25x.setOnClickListener { speed.value = 2.5f }
                // 3.0x 速度按钮点击事件：点击时设置播放速度为3.0x
                button30x.setOnClickListener { speed.value = 3.0f }
                // 3.5x 速度按钮点击事件：点击时设置播放速度为3.5x
                button35x.setOnClickListener { speed.value = 3.5f }
                // 4.0x 速度按钮点击事件：点击时设置播放速度为4.0x（最大值）
                button40x.setOnClickListener { speed.value = 4.0f }

                // 跳过静音开关状态变化监听器：当开关状态改变时触发
                skipSilence.setOnCheckedChangeListener { _, isChecked ->
                    // 调用扩展函数设置跳过静音功能的状态
                    mediaController.setSkipSilenceEnabled(isChecked)
                }
            }

            // 创建对话框并设置标题和布局：使用 MaterialAlertDialogBuilder 创建对话框
            val builder = MaterialAlertDialogBuilder(activity)
            // 设置对话框标题：从资源文件中获取标题字符串
            builder.setTitle(getString(coreUiR.string.select_playback_speed))
                // 设置对话框内容视图：使用绑定对象的根视图
                .setView(binding.root)
                // 创建对话框实例
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
        // 如果 activity 为 null，抛出异常：确保对话框创建失败时有明确的错误信息
    }
}
