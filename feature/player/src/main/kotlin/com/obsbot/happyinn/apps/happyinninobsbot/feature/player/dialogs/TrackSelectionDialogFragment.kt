package com.obsbot.happyinn.apps.happyinninobsbot.feature.player.dialogs
// 包声明：指定该类所在的包路径

import android.app.Dialog
// 导入 Dialog 类：Android 对话框的基类
import android.os.Bundle
// 导入 Bundle 类：用于存储和恢复数据
import androidx.fragment.app.DialogFragment
// 导入 DialogFragment 类：支持 Fragment 生命周期的对话框基类
import androidx.media3.common.C
// 导入 C 类：Media3 库的常量类，包含轨道类型等常量
import androidx.media3.common.Tracks
// 导入 Tracks 类：Media3 库中表示媒体轨道信息的类
import androidx.media3.common.util.UnstableApi
// 导入 UnstableApi 注解：标记使用了 Media3 库中不稳定的 API
import com.google.android.material.dialog.MaterialAlertDialogBuilder
// 导入 MaterialAlertDialogBuilder 类：用于创建符合 Material Design 的对话框
import com.obsbot.happyinn.apps.happyinninobsbot.core.ui.R
// 导入核心UI模块的资源类
import com.obsbot.happyinn.apps.happyinninobsbot.feature.player.extensions.getName
// 导入自定义扩展函数 getName：用于获取轨道的可读名称

@UnstableApi
// 注解标记：表示该类使用了 Media3 库中标记为不稳定的 API
/**
 * 轨道选择对话框
 * - 支持音轨/字幕轨道单选
 * - 提供禁用轨道、打开本地字幕的操作
 */
class TrackSelectionDialogFragment(
    private val type: @C.TrackType Int, // 轨道类型：使用 C.TrackType 注解，只能是音频或文本类型
    private val tracks: Tracks, // 可用轨道列表：包含所有音频和字幕轨道信息
    private val onTrackSelected: (trackIndex: Int) -> Unit, // 轨道选择回调：接收选中的轨道索引
    private val onOpenLocalTrackClicked: () -> Unit = {}, // 打开本地轨道回调：默认空实现
) : DialogFragment() { // 类声明：继承自 DialogFragment，表示这是一个对话框Fragment
    // 重写 onCreateDialog 方法：DialogFragment 的生命周期方法，用于创建对话框
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // 使用 when 表达式根据轨道类型分支处理
        when (type) {
            // 如果是音频轨道类型
            C.TRACK_TYPE_AUDIO -> {
                // 音频轨道选择对话框创建逻辑
                // activity?.let：安全获取 Activity 上下文，避免空指针异常
                return activity?.let { activity ->
                    // 筛选可用的音频轨道：只保留类型为音频且支持的轨道组
                    val audioTracks = tracks.groups
                        .filter { it.type == C.TRACK_TYPE_AUDIO && it.isSupported }

                    // 获取音频轨道的可读名称：使用 getName 扩展函数转换轨道信息为可读名称
                    val trackNames = audioTracks.mapIndexed { index, trackGroup ->
                        trackGroup.mediaTrackGroup.getName(type, index)
                    }.toTypedArray() // 转换为数组，用于对话框列表显示

                    // 计算当前选中的轨道索引：找到第一个被选中的轨道组，如果没有则选中"禁用"选项
                    val selectedTrackIndex = audioTracks
                        .indexOfFirst { it.isSelected }.takeIf { it != -1 } ?: audioTracks.size

                    // 使用 MaterialAlertDialogBuilder 创建对话框
                    MaterialAlertDialogBuilder(activity).apply {
                        // 设置对话框标题：选择音频轨道
                        setTitle(getString(R.string.select_audio_track))
                        // 如果有可用的音频轨道
                        if (trackNames.isNotEmpty()) {
                            // 设置单选列表项：包含所有音频轨道名称和"禁用"选项
                            setSingleChoiceItems(
                                arrayOf(*trackNames, getString(R.string.disable)), // 扩展数组并添加"禁用"选项
                                selectedTrackIndex, // 当前选中的索引
                            ) { dialog, trackIndex -> // 选择项点击监听器
                                // 调用轨道选择回调：如果选择的是轨道则传递索引，否则传递-1表示禁用
                                onTrackSelected(trackIndex.takeIf { it < trackNames.size } ?: -1)
                                // 关闭对话框
                                dialog.dismiss()
                            }
                        } else {
                            // 如果没有可用的音频轨道，显示提示信息
                            setMessage(getString(R.string.no_audio_tracks_found))
                        }
                    }.create() // 创建对话框实例
                } ?: throw IllegalStateException("Activity cannot be null")
                // 如果 activity 为 null，抛出异常：确保对话框创建失败时有明确的错误信息
            }

            // 如果是文本轨道类型（字幕）
            C.TRACK_TYPE_TEXT -> {
                // 字幕轨道选择对话框创建逻辑
                // activity?.let：安全获取 Activity 上下文，避免空指针异常
                return activity?.let { activity ->
                    // 筛选可用的文本轨道：只保留类型为文本且支持的轨道组
                    val textTracks = tracks.groups
                        .filter { it.type == C.TRACK_TYPE_TEXT && it.isSupported }

                    // 获取文本轨道的可读名称：使用 getName 扩展函数转换轨道信息为可读名称
                    val trackNames = textTracks.mapIndexed { index, trackGroup ->
                        trackGroup.mediaTrackGroup.getName(type, index)
                    }.toTypedArray() // 转换为数组，用于对话框列表显示

                    // 计算当前选中的轨道索引：找到第一个被选中的轨道组，如果没有则选中"禁用"选项
                    val selectedTrackIndex = textTracks
                        .indexOfFirst { it.isSelected }.takeIf { it != -1 } ?: textTracks.size

                    // 使用 MaterialAlertDialogBuilder 创建对话框
                    MaterialAlertDialogBuilder(activity).apply {
                        // 设置对话框标题：选择字幕轨道
                        setTitle(getString(R.string.select_subtitle_track))
                        // 如果有可用的字幕轨道
                        if (trackNames.isNotEmpty()) {
                            // 设置单选列表项：包含所有字幕轨道名称和"禁用"选项
                            setSingleChoiceItems(
                                arrayOf(*trackNames, getString(R.string.disable)), // 扩展数组并添加"禁用"选项
                                selectedTrackIndex, // 当前选中的索引
                            ) { dialog, trackIndex -> // 选择项点击监听器
                                // 调用轨道选择回调：如果选择的是轨道则传递索引，否则传递-1表示禁用
                                onTrackSelected(trackIndex.takeIf { it < trackNames.size } ?: -1)
                                // 关闭对话框
                                dialog.dismiss()
                            }
                        } else {
                            // 如果没有可用的字幕轨道，显示提示信息
                            setMessage(getString(R.string.no_subtitle_tracks_found))
                        }
                        // 设置正面按钮：打开本地字幕
                        setPositiveButton(getString(R.string.open_subtitle)) { dialog, _ ->
                            // 关闭对话框
                            dialog.dismiss()
                            // 调用打开本地字幕回调
                            onOpenLocalTrackClicked()
                        }
                    }.create() // 创建对话框实例
                } ?: throw IllegalStateException("Activity cannot be null")
                // 如果 activity 为 null，抛出异常：确保对话框创建失败时有明确的错误信息
            }

            // 如果是不支持的轨道类型
            else -> {
                // 抛出非法参数异常：明确说明只支持音频和文本轨道类型
                throw IllegalArgumentException(
                    "Track type not supported. Track type must be either TRACK_TYPE_AUDIO or TRACK_TYPE_TEXT",
                )
            }
        }
    }
}
