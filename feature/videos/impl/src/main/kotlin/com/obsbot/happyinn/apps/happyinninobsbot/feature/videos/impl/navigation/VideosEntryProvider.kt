package com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.impl.navigation

import android.net.Uri
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.obsbot.happyinn.apps.happyinninobsbot.core.navigation.Navigator
import com.obsbot.happyinn.apps.happyinninobsbot.feature.topic.api.navigation.navigateToTopic
import com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.api.navigation.VideosNavKey
import com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.impl.screens.VideosScreen

fun EntryProviderScope<NavKey>.videosEntry(navigator: Navigator) {
    entry<VideosNavKey> {
        VideosScreen(
            onSettingsClick = {
                // 实现导航到设置屏幕的逻辑
                TODO("Implement navigate to settings")
            },
            onPlayVideo = { uri: Uri ->
                // 实现播放视频的逻辑，可能导航到视频播放器
                TODO("Implement play video")
            },
            onFolderClick = { folderPath: String ->
                // 实现导航到文件夹详情的逻辑
                TODO("Implement navigate to folder")
            }
        )
    }
}