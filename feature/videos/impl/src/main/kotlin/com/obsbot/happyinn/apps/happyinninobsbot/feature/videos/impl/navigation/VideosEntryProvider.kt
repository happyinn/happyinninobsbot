package com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.impl.navigation

import android.net.Uri
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.obsbot.happyinn.apps.happyinninobsbot.core.navigation.Navigator
import com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.api.navigation.VideosNavKey
import com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.api.navigation.navigateToFolder
import com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.impl.screens.media.VideosScreen

/**
 * 视频列表入口，负责提供媒体选择主界面。
 *
 * @param navigator 全局导航器，用于跳转到文件夹详情等下一级页面
 * @param onPlayVideo 播放视频回调（从外部注入，保持现有行为）
 */
fun EntryProviderScope<NavKey>.videosEntry(
    navigator: Navigator,
    onPlayVideo: (uri: Uri) -> Unit,
) {
    entry<VideosNavKey> {
        VideosScreen(
            onSettingsClick = {
                // 实现导航到设置屏幕的逻辑
                TODO("Implement navigate to settings")
            },
            onPlayVideo = onPlayVideo,
            onFolderClick = { folderPath: String ->
                // 跳转到文件夹详情页，传递当前文件夹路径
                navigator.navigateToFolder(folderPath)
            }
        )
    }
}