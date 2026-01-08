/**
 * 媒体文件夹选择器屏幕组件，用于显示特定文件夹中的媒体文件
 */
package com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.impl.screens.mediaFolder

import android.net.Uri
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.component.HioTopAppBar
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.icon.HioIcons
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.UserData
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.Folder
import com.obsbot.happyinn.apps.happyinninobsbot.extensions.prettyName
import com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.impl.composables.MediaView
import com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.impl.screens.MediaState
import java.io.File
import com.obsbot.happyinn.apps.happyinninobsbot.core.ui.R

/**
 * 媒体文件夹选择器路由组件，负责连接ViewModel和UI组件
 * @param viewModel 媒体文件夹选择器ViewModel，默认使用Hilt注入
 * @param onVideoClick 播放视频回调
 * @param onFolderClick 文件夹点击回调
 * @param onNavigateUp 返回上一级页面的回调
 */
@Composable
fun MediaPickerFolderScreen(
    viewModel: MediaPickerFolderViewModel = hiltViewModel(),
    onVideoClick: (uri: Uri) -> Unit,
    onFolderClick: (folderPath: String) -> Unit,
    onNavigateUp: () -> Unit,
) {
    // The app experiences jank when videosState updates before the initial render finishes.
    // By adding Lifecycle.State.RESUMED, we ensure that we wait until the first render completes.
    val mediaState by viewModel.mediaState.collectAsStateWithLifecycle(minActiveState = Lifecycle.State.RESUMED)
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    MediaPickerFolderScreen(
        folderPath = viewModel.folderPath,
        mediaState = mediaState,
        preferences = preferences,
        isRefreshing = uiState.refreshing,
        onPlayVideo = onVideoClick,
        onNavigateUp = onNavigateUp,
        onFolderClick = onFolderClick,
        onDeleteVideoClick = { viewModel.deleteVideos(listOf(it)) },
        onAddToSync = viewModel::addToMediaInfoSynchronizer,
        onRenameVideoClick = viewModel::renameVideo,
        onRefreshClicked = viewModel::onRefreshClicked,
        onDeleteFolderClick = { viewModel.deleteFolders(listOf(it)) },
    )
}

/**
 * 媒体文件夹选择器屏幕组件，显示特定文件夹中的媒体文件
 * @param folderPath 当前文件夹路径
 * @param mediaState 媒体状态
 * @param preferences 应用偏好设置
 * @param isRefreshing 是否正在刷新
 * @param onNavigateUp 返回上一级页面的回调
 * @param onPlayVideo 播放视频回调
 * @param onFolderClick 文件夹点击回调
 * @param onDeleteVideoClick 删除视频回调
 * @param onRenameVideoClick 重命名视频回调
 * @param onAddToSync 添加到同步回调
 * @param onRefreshClicked 刷新点击回调
 * @param onDeleteFolderClick 删除文件夹回调
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MediaPickerFolderScreen(
    folderPath: String,
    mediaState: MediaState,
    preferences: UserData,
    isRefreshing: Boolean = false,
    onNavigateUp: () -> Unit,
    onPlayVideo: (Uri) -> Unit,
    onFolderClick: (String) -> Unit = {},
    onDeleteVideoClick: (String) -> Unit,
    onRenameVideoClick: (Uri, String) -> Unit = { _, _ -> },
    onAddToSync: (Uri) -> Unit,
    onRefreshClicked: () -> Unit = {},
    onDeleteFolderClick: (Folder) -> Unit = {},
) {
    val pullToRefreshState = rememberPullToRefreshState()

    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)),
        topBar = {
            HioTopAppBar(
                title = File(folderPath).prettyName,
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            imageVector = HioIcons.ArrowBack,
                            contentDescription = stringResource(id = R.string.navigate_up),
                        )
                    }
                },
            )
        },
        floatingActionButton = {
            if (!preferences.showFloatingPlayButton) return@Scaffold
            FloatingActionButton(
                onClick = {
                    val state = mediaState as? MediaState.Success
                    val videoToPlay = state?.data?.recentlyPlayedVideo ?: state?.data?.firstVideo
                    if (videoToPlay != null) {
                        onPlayVideo(Uri.parse(videoToPlay.uriString))
                    }
                },
            ) {
                Icon(
                    imageVector = HioIcons.Play,
                    contentDescription = null,
                )
            }
        },
    ) { paddingValues ->
        PullToRefreshBox(
            modifier = Modifier.padding(paddingValues),
            state = pullToRefreshState,
            isRefreshing = isRefreshing,
            onRefresh = onRefreshClicked,
            contentAlignment = Alignment.Center,
        ) {
            MediaView(
                isLoading = mediaState is MediaState.Loading,
                rootFolder = (mediaState as? MediaState.Success)?.data,
                preferences = preferences,
                onFolderClick = onFolderClick,
                onDeleteFolderClick = onDeleteFolderClick,
                onVideoClick = onPlayVideo,
                onDeleteVideoClick = onDeleteVideoClick,
                onVideoLoaded = onAddToSync,
                onRenameVideoClick = onRenameVideoClick,
            )
        }
    }
}
