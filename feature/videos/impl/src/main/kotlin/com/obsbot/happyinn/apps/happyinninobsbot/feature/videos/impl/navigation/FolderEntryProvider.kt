package com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.impl.navigation

import android.net.Uri
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.obsbot.happyinn.apps.happyinninobsbot.core.navigation.Navigator
import com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.api.navigation.FolderNavKey
import com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.impl.screens.mediaFolder.MediaPickerFolderScreen
import com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.impl.screens.mediaFolder.MediaPickerFolderViewModel
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.api.navigation.navigateToFolder

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
fun EntryProviderScope<NavKey>.FolderEntry(
    navigator: Navigator,
    onPlayVideo: (uri: Uri) -> Unit,
) {
    entry<FolderNavKey>(
    ) { key ->
        // 使用 NavKey 创建对应的 ViewModel 实例
        val viewModel = hiltViewModel<MediaPickerFolderViewModel, MediaPickerFolderViewModel.Factory> {
            it.create(key)
        }
        MediaPickerFolderScreen(
            onNavigateUp = navigator::goBack,
            viewModel = viewModel,
            onVideoClick = onPlayVideo,
            onFolderClick = navigator::navigateToFolder,
        )
    }
}