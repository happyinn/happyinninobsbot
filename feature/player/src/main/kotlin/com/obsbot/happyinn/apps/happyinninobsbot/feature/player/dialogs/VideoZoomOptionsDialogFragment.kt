package com.obsbot.happyinn.apps.happyinninobsbot.feature.player.dialogs

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.media3.common.util.UnstableApi
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.VideoZoom
import com.obsbot.happyinn.apps.happyinninobsbot.core.ui.R

/**
 * 视频缩放选项对话框，用于选择视频的缩放模式
 * - 最佳适配
 * - 拉伸填充
 * - 裁剪填充
 * - 100% 原始大小
 */

@UnstableApi
/**
 * 视频缩放选项对话框片段
 * @param currentVideoZoom 当前选中的视频缩放模式
 * @param onVideoZoomOptionSelected 缩放选项选中回调
 */
class VideoZoomOptionsDialogFragment(
    private val currentVideoZoom: VideoZoom,                     // 当前选中的视频缩放模式
    private val onVideoZoomOptionSelected: (videoZoom: VideoZoom) -> Unit, // 缩放选项选中回调
) : DialogFragment() {
    /**
     * 创建对话框
     * @param savedInstanceState 保存的实例状态
     * @return 创建的对话框
     */
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // 获取所有视频缩放模式
        val videoZoomValues = VideoZoom.entries.toTypedArray()

        return activity?.let { activity ->
            MaterialAlertDialogBuilder(activity)
                .setTitle(getString(R.string.video_zoom)) // 设置对话框标题
                .setSingleChoiceItems(                     // 设置单选列表
                    videoZoomValues.map { getString(it.nameRes()) }.toTypedArray(), // 列表项文本
                    videoZoomValues.indexOfFirst { it == currentVideoZoom },        // 当前选中项索引
                ) { dialog, trackIndex ->                                            // 选中回调
                    onVideoZoomOptionSelected(videoZoomValues[trackIndex])          // 调用外部回调
                    dialog.dismiss()                                                // 关闭对话框
                }.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}

/**
 * 获取 VideoZoom 枚举对应的字符串资源 ID
 * @return 字符串资源 ID
 */
fun VideoZoom.nameRes(): Int {
    val stringRes = when (this) {
        VideoZoom.BEST_FIT -> R.string.best_fit          // 最佳适配
        VideoZoom.STRETCH -> R.string.stretch            // 拉伸填充
        VideoZoom.CROP -> R.string.crop                  // 裁剪填充
        VideoZoom.HUNDRED_PERCENT -> R.string.hundred_percent // 100% 原始大小
    }

    return stringRes
}
