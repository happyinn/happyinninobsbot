package com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.impl.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.PermissionStatus
import com.google.accompanist.permissions.rememberPermissionState
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.UserData
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.Folder
import com.obsbot.happyinn.apps.happyinninobsbot.storagePermission
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.FloatingActionButton
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.shouldShowRationale
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.component.CancelButton
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.component.DoneButton
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.component.HioCenterAlignedTopAppBar
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.component.HioDialog
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.component.ShortcutChipButton
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.icon.HioIcons
import com.obsbot.happyinn.apps.happyinninobsbot.core.ui.video.PermissionMissingView
import com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.impl.composables.MediaView
import com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.impl.composables.QuickSettingsDialog
import com.obsbot.happyinn.apps.happyinninobsbot.core.ui.R

/**
 * 圆形进度指示器的测试标签，用于UI测试
 */
const val CIRCULAR_PROGRESS_INDICATOR_TEST_TAG = "circularProgressIndicator"

/**
 * 媒体选择器路由组件，负责连接ViewModel和UI组件
 * @param onSettingsClick 设置按钮点击回调
 * @param onPlayVideo 播放视频回调
 * @param onFolderClick 文件夹点击回调
 * @param viewModel 媒体选择器ViewModel，默认使用Hilt注入
 */
@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun VideosScreen(
    onSettingsClick: () -> Unit,
    onPlayVideo: (uri: Uri) -> Unit,
    onFolderClick: (folderPath: String) -> Unit,
    viewModel: VideosViewModel = hiltViewModel(),
) {
    // 从ViewModel收集偏好设置
    val preferences by viewModel.preferences.collectAsStateWithLifecycle()
    // 从ViewModel收集UI状态
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    // 从ViewModel收集媒体状态
    val mediaState by viewModel.mediaState.collectAsStateWithLifecycle()

    // 创建存储权限状态
    val permissionState = rememberPermissionState(permission = storagePermission)

    // 调用媒体选择器屏幕组件
    VideosScreen(
        mediaState = mediaState,
        preferences = preferences,
        isRefreshing = uiState.refreshing,
        permissionState = permissionState,
        onPlayVideo = onPlayVideo,
        onFolderClick = onFolderClick,
        onSettingsClick = onSettingsClick,
        updatePreferences = viewModel::updatePreferences,
        onDeleteVideoClick = { viewModel.deleteVideos(listOf(it)) },
        onDeleteFolderClick = { viewModel.deleteFolders(listOf(it)) },
        onAddToSync = viewModel::addToMediaInfoSynchronizer,
        onRenameVideoClick = viewModel::renameVideo,
        onRefreshClicked = viewModel::onRefreshClicked,
    )
}

/**
 * 媒体选择器屏幕组件，负责显示媒体文件和相关功能
 * @param mediaState 当前媒体状态，包含加载、成功或错误状态
 * @param preferences 应用程序偏好设置
 * @param isRefreshing 是否正在刷新
 * @param permissionState 存储权限状态
 * @param onPlayVideo 播放视频回调
 * @param onFolderClick 文件夹点击回调
 * @param onSettingsClick 设置按钮点击回调
 * @param updatePreferences 更新偏好设置回调
 * @param onDeleteVideoClick 删除视频回调
 * @param onRenameVideoClick 重命名视频回调
 * @param onDeleteFolderClick 删除文件夹回调
 * @param onAddToSync 添加到同步回调
 * @param onRefreshClicked 刷新点击回调
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
internal fun VideosScreen(
    mediaState: MediaState,
    preferences: UserData,
    isRefreshing: Boolean = false,
    permissionState: PermissionState = GrantedPermissionState,
    onPlayVideo: (uri: Uri) -> Unit = {},
    onFolderClick: (folderPath: String) -> Unit = {},
    onSettingsClick: () -> Unit = {},
    updatePreferences: (UserData) -> Unit = {},
    onDeleteVideoClick: (String) -> Unit,
    onRenameVideoClick: (Uri, String) -> Unit = { _, _ -> },
    onDeleteFolderClick: (Folder) -> Unit,
    onAddToSync: (Uri) -> Unit = {},
    onRefreshClicked: () -> Unit = {},
) {
    // 是否显示快速设置对话框
    var showQuickSettingsDialog by rememberSaveable { mutableStateOf(false) }
    // 是否显示URL对话框
    var showUrlDialog by rememberSaveable { mutableStateOf(false) }

    // 创建文件选择启动器，用于选择本地视频文件
    val selectVideoFileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
        onResult = { uri -> uri?.let(onPlayVideo) },
    )

    // 下拉刷新状态
    val pullToRefreshState = rememberPullToRefreshState()

    // 脚手架组件，提供顶部应用栏和浮动操作按钮
    Scaffold(
        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal)),
        // 顶部应用栏
        topBar = {
            HioCenterAlignedTopAppBar(
                title = stringResource(id = R.string.app_name),
                // 导航图标（设置按钮）
                navigationIcon = {
                    IconButton(onClick = onSettingsClick) {
                        Icon(
                            imageVector = HioIcons.Settings,
                            contentDescription = stringResource(id = R.string.settings),
                        )
                    }
                },
                // 操作按钮（快速设置按钮）
                actions = {
                    IconButton(onClick = { showQuickSettingsDialog = true }) {
                        Icon(
                            imageVector = HioIcons.DashBoard,
                            contentDescription = stringResource(id = R.string.menu),
                        )
                    }
                },
            )
        },
        // 浮动操作按钮（播放最近视频）
        floatingActionButton = {
            // 如果不显示浮动播放按钮或没有存储权限，则不显示
            if (!preferences.showFloatingPlayButton) return@Scaffold
            if (!permissionState.status.isGranted) return@Scaffold

            FloatingActionButton(
                onClick = {
                    // 获取成功状态下的媒体数据
                    val state = mediaState as? MediaState.Success
                    // 优先选择最近播放的视频，否则选择第一个视频
                    val videoToPlay = state?.data?.recentlyPlayedVideo ?: state?.data?.firstVideo
                    // 如果找到视频，则播放
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
        // 下拉刷新容器
        PullToRefreshBox(
            modifier = Modifier.padding(paddingValues),
            state = pullToRefreshState,
            isRefreshing = isRefreshing,
            onRefresh = onRefreshClicked,
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
            ) {
                // 快捷操作按钮行
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    // 打开本地视频按钮
                    item {
                        ShortcutChipButton(
                            text = stringResource(id = R.string.open_local_video),
                            icon = HioIcons.FileOpen,
                            onClick = { selectVideoFileLauncher.launch("video/*") },
                        )
                    }
                    // 打开网络流按钮
                    item {
                        ShortcutChipButton(
                            text = stringResource(id = R.string.open_network_stream),
                            icon = HioIcons.Link,
                            onClick = { showUrlDialog = true },
                        )
                    }
                }

                // 权限缺失视图，如果没有存储权限则显示权限请求界面
                PermissionMissingView(
                    isGranted = permissionState.status.isGranted,
                    showRationale = permissionState.status.shouldShowRationale,
                    permission = permissionState.permission,
                    launchPermissionRequest = { permissionState.launchPermissionRequest() },
                ) {
                    // 媒体视图，显示文件夹和视频列表
                    MediaView(
                        isLoading = mediaState is MediaState.Loading,
                        rootFolder = (mediaState as? MediaState.Success)?.data,
                        preferences = preferences,
                        onFolderClick = onFolderClick,
                        onDeleteFolderClick = onDeleteFolderClick,
                        onVideoClick = onPlayVideo,
                        onRenameVideoClick = onRenameVideoClick,
                        onDeleteVideoClick = onDeleteVideoClick,
                        onVideoLoaded = onAddToSync,
                    )
                }
            }
        }
    }

    // 快速设置对话框，当showQuickSettingsDialog为true时显示
    if (showQuickSettingsDialog) {
        QuickSettingsDialog(
            userData = preferences,
            onDismiss = { showQuickSettingsDialog = false },
            updatePreferences = updatePreferences,
        )
    }

    // 网络URL对话框，当showUrlDialog为true时显示
    if (showUrlDialog) {
        NetworkUrlDialog(
            onDismiss = { showUrlDialog = false },
            onDone = { url -> onPlayVideo(Uri.parse(url)) },
        )
    }
}

/**
 * 网络URL对话框，用于输入和播放网络视频流
 * @param onDismiss 对话框关闭回调
 * @param onDone 确认按钮点击回调，返回输入的URL
 */
@Composable
fun NetworkUrlDialog(
    onDismiss: () -> Unit,
    onDone: (String) -> Unit,
) {
    // 存储输入的URL
    var url by rememberSaveable { mutableStateOf("") }

    // 自定义对话框组件
    HioDialog(
        onDismissRequest = onDismiss,
        // 对话框标题
        title = { Text(stringResource(R.string.network_stream)) },
        // 对话框内容
        content = {
            Text(text = stringResource(R.string.enter_a_network_url))
            Spacer(modifier = Modifier.height(10.dp))
            // URL输入框
            OutlinedTextField(
                value = url,
                onValueChange = { url = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text(text = stringResource(R.string.example_url)) },
            )
        },
        // 确认按钮
        confirmButton = {
            DoneButton(
                enabled = url.isNotBlank(), // 只有URL不为空时才启用
                onClick = { onDone(url) },
            )
        },
        // 取消按钮
        dismissButton = { CancelButton(onClick = onDismiss) },
    )
}


/**
 * 已授予权限状态的实现，用于测试和预览
 * 始终返回已授予状态，不执行任何权限请求
 */
@ExperimentalPermissionsApi
private val GrantedPermissionState = object : PermissionState {
    override val permission: String
        get() = ""
    override val status: PermissionStatus
        get() = PermissionStatus.Granted

    override fun launchPermissionRequest() {}
}
