package com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.impl.composables

/**
 * 媒体视图组件
 * 
 * 该文件包含媒体视图的核心组件，用于显示文件夹和视频的网格列表。
 * 支持长按弹出操作菜单、删除、重命名、分享等功能。
 */

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.obsbot.happyinn.apps.happyinninobsbot.Utils
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.component.CancelButton
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.component.DoneButton
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.component.HioDialog
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.icon.HioIcons
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.UserData
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.Folder
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.MediaLayoutMode
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.MediaViewMode
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.Video
import com.obsbot.happyinn.apps.happyinninobsbot.core.ui.R
import kotlin.math.abs
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 媒体视图主组件
 * 
 * 用于显示文件夹和视频列表的核心视图组件。根据用户偏好设置以列表或网格方式展示，
 * 支持点击和长按交互，可以进行文件夹导航、视频播放和各种操作。
 * 
 * @param isLoading 是否正在加载媒体数据
 * @param rootFolder 根文件夹，包含要显示的媒体内容
 * @param preferences 应用偏好设置，影响显示样式和内容
 * @param onFolderClick 文件夹点击回调，用于导航到子文件夹
 * @param onDeleteFolderClick 删除文件夹回调
 * @param onVideoClick 视频点击回调，用于播放视频
 * @param onRenameVideoClick 重命名视频回调
 * @param onDeleteVideoClick 删除视频回调
 * @param onVideoLoaded 视频加载完成回调，用于记录播放历史
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun MediaView(
    isLoading: Boolean,
    rootFolder: Folder?,
    preferences: UserData,
    onFolderClick: (String) -> Unit,
    onDeleteFolderClick: (Folder) -> Unit,
    onVideoClick: (Uri) -> Unit,
    onRenameVideoClick: (Uri, String) -> Unit,
    onDeleteVideoClick: (String) -> Unit,
    onVideoLoaded: (Uri) -> Unit,
) {
    // 获取触觉反馈控制器
    val haptic = LocalHapticFeedback.current
    // 存储当前长按选中的文件夹
    var showFolderActionsFor: Folder? by rememberSaveable { mutableStateOf(null) }
    // 存储待删除的文件夹
    var deleteFolderAction: Folder? by rememberSaveable { mutableStateOf(null) }
    // 协程作用域，用于执行异步操作
    val scope = rememberCoroutineScope()

    // 存储当前长按选中的视频
    var showMediaActionsFor: Video? by rememberSaveable { mutableStateOf(null) }
    // 存储待删除的视频
    var deleteAction: Video? by rememberSaveable { mutableStateOf(null) }
    // 存储待重命名的视频
    var renameAction: Video? by rememberSaveable { mutableStateOf(null) }
    // 存储待查看信息的视频
    var showInfoAction: Video? by rememberSaveable { mutableStateOf(null) }

    // 获取当前上下文和底部面板状态
    val context = LocalContext.current
    val bottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // 正在加载时显示进度条
    if (isLoading) {
        CenterCircularProgressBar()
    } else {
        // 定义最小尺寸常量
        val folderMinWidth = 90.dp
        val videoMinWidth = 130.dp
        BoxWithConstraints {
            // 根据布局模式计算内边距和间距
            val contentHorizontalPadding = when (preferences.mediaLayoutMode) {
                MediaLayoutMode.LIST -> 0.dp
                MediaLayoutMode.GRID -> 16.dp
            }
            val itemSpacing = when (preferences.mediaLayoutMode) {
                MediaLayoutMode.LIST -> 0.dp
                MediaLayoutMode.GRID -> 16.dp
            }
            // 计算网格列数
            val maxWidth = this.maxWidth - (contentHorizontalPadding * 2) - itemSpacing
            val maxFolders = (maxWidth / folderMinWidth).toInt()
            val maxVideos = (maxWidth / videoMinWidth).toInt()
            val spans = when (preferences.mediaLayoutMode) {
                MediaLayoutMode.LIST -> 1
                MediaLayoutMode.GRID -> lcm(maxFolders, maxVideos)
            }

            // 计算单个文件夹和视频占用的列数
            val singleFolderSpan = when (preferences.mediaLayoutMode) {
                MediaLayoutMode.LIST -> 1
                MediaLayoutMode.GRID -> spans / maxFolders
            }
            val singleVideoSpan = when (preferences.mediaLayoutMode) {
                MediaLayoutMode.LIST -> 1
                MediaLayoutMode.GRID -> spans / maxVideos
            }

            // 创建垂直网格列表
            LazyVerticalGrid(
                columns = GridCells.Fixed(spans),
                contentPadding = PaddingValues(horizontal = contentHorizontalPadding, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(itemSpacing),
                horizontalArrangement = Arrangement.spacedBy(itemSpacing),
            ) {
                // 如果没有文件夹和视频，显示空状态视图
                if (rootFolder == null || rootFolder.folderList.isEmpty() && rootFolder.mediaList.isEmpty()) {
                    item(
                        span = { GridItemSpan(maxLineSpan) },
                    ) { NoVideosFound() }
                    return@LazyVerticalGrid
                }

                // 如果是文件夹树视图且有文件夹，显示文件夹分区标题
                if (preferences.mediaViewMode == MediaViewMode.FOLDER_TREE && rootFolder.folderList.isNotEmpty()) {
                    item(
                        span = { GridItemSpan(maxLineSpan) },
                    ) {
                        SectionTitle(title = stringResource(id = R.string.folders))
                    }
                }

                // 渲染文件夹列表
                items(
                    items = rootFolder.folderList,
                    key = { it.path },
                    span = { GridItemSpan(singleFolderSpan) },
                ) { folder ->
                    FolderItem(
                        folder = folder,
                        isRecentlyPlayedFolder = rootFolder.isRecentlyPlayedVideo(folder.recentlyPlayedVideo),
                        preferences = preferences,
                        modifier = Modifier.combinedClickable(
                            onClick = { onFolderClick(folder.path) },
                            onLongClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                showFolderActionsFor = folder
                            },
                        ),
                    )
                }

                // 如果是文件夹树视图且有文件夹，添加间距
                if (preferences.mediaViewMode == MediaViewMode.FOLDER_TREE && rootFolder.folderList.isNotEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        Spacer(modifier = Modifier.size(12.dp))
                    }
                }

                // 如果是文件夹树视图且有视频，显示视频分区标题
                if (preferences.mediaViewMode == MediaViewMode.FOLDER_TREE && rootFolder.mediaList.isNotEmpty()) {
                    item(span = { GridItemSpan(maxLineSpan) }) {
                        SectionTitle(title = stringResource(id = R.string.videos))
                    }
                }
                // 渲染视频列表
                items(
                    items = rootFolder.mediaList,
                    key = { it.path },
                    span = { GridItemSpan(singleVideoSpan) },
                ) { video ->
                    // 视频加载完成后触发回调
                    LaunchedEffect(Unit) {
                        onVideoLoaded(Uri.parse(video.uriString))
                    }
                    VideoItem(
                        video = video,
                        preferences = preferences,
                        isRecentlyPlayedVideo = rootFolder.isRecentlyPlayedVideo(video),
                        modifier = Modifier.combinedClickable(
                            onClick = { onVideoClick(Uri.parse(video.uriString)) },
                            onLongClick = {
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                showMediaActionsFor = video
                            },
                        ),
                    )
                }
            }
        }
    }

    // 显示文件夹操作面板
    showFolderActionsFor?.let {
        OptionsBottomSheet(
            title = it.name,
            onDismiss = { showFolderActionsFor = null },
        ) {
            BottomSheetItem(
                text = stringResource(R.string.delete),
                icon = HioIcons.Delete,
                onClick = {
                    deleteFolderAction = it
                    scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                        if (!bottomSheetState.isVisible) showFolderActionsFor = null
                    }
                },
            )
        }
    }

    // 显示删除文件夹确认对话框
    deleteFolderAction?.let { folder ->
        DeleteConfirmationDialog(
            subText = stringResource(R.string.delete_folder),
            onCancel = { deleteFolderAction = null },
            onConfirm = {
                onDeleteFolderClick(folder)
                deleteFolderAction = null
            },
            fileNames = listOf(folder.name),
        )
    }

    // 显示视频操作面板
    showMediaActionsFor?.let {
        OptionsBottomSheet(
            title = it.nameWithExtension,
            onDismiss = { showMediaActionsFor = null },
        ) {
            // 重命名选项
            BottomSheetItem(
                text = stringResource(R.string.rename),
                icon = HioIcons.Edit,
                onClick = {
                    renameAction = it
                    scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                        if (!bottomSheetState.isVisible) showMediaActionsFor = null
                    }
                },
            )
            // 分享选项
            BottomSheetItem(
                text = stringResource(R.string.share),
                icon = HioIcons.Share,
                onClick = {
                    val mediaStoreUri = Uri.parse(it.uriString)
                    val intent = Intent.createChooser(
                        Intent().apply {
                            type = "video/*"
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_STREAM, mediaStoreUri)
                        },
                        null,
                    )
                    context.startActivity(intent)
                    scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                        if (!bottomSheetState.isVisible) showMediaActionsFor = null
                    }
                },
            )
            // 属性选项
            BottomSheetItem(
                text = stringResource(R.string.properties),
                icon = HioIcons.Info,
                onClick = {
                    showInfoAction = it
                    scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                        if (!bottomSheetState.isVisible) showMediaActionsFor = null
                    }
                },
            )
            // 删除选项
            BottomSheetItem(
                text = stringResource(R.string.delete),
                icon = HioIcons.Delete,
                onClick = {
                    deleteAction = it
                    scope.launch { bottomSheetState.hide() }.invokeOnCompletion {
                        if (!bottomSheetState.isVisible) showMediaActionsFor = null
                    }
                },
            )
        }
    }

    // 显示删除视频确认对话框
    deleteAction?.let {
        DeleteConfirmationDialog(
            subText = stringResource(id = R.string.delete_file),
            onCancel = { deleteAction = null },
            onConfirm = {
                onDeleteVideoClick(it.uriString)
                deleteAction = null
            },
            fileNames = listOf(it.nameWithExtension),
        )
    }

    // 显示视频信息对话框
    showInfoAction?.let {
        ShowVideoInfoDialog(
            video = it,
            onDismiss = { showInfoAction = null },
        )
    }

    // 显示重命名对话框
    renameAction?.let { video ->
        ShowRenameDialog(
            name = video.displayName,
            onDismiss = { renameAction = null },
            onDone = {
                onRenameVideoClick(
                    Uri.parse(video.uriString),
                    "$it.${video.nameWithExtension.substringAfterLast(".")}",
                )
                renameAction = null
            },
        )
    }
}

/**
 * 分区标题组件
 * 
 * 用于在媒体列表中显示分区标题，如"文件夹"、"视频"等。
 * 
 * @param title 标题文字
 */
@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 4.dp),
        color = MaterialTheme.colorScheme.primary,
    )
}

/**
 * 重命名对话框组件
 * 
 * 用于让用户输入新的文件名。
 * 
 * @param name 原始文件名
 * @param onDismiss 对话框关闭回调
 * @param onDone 确认重命名的回调，参数为新文件名
 */
@Composable
fun ShowRenameDialog(
    name: String,
    onDismiss: () -> Unit,
    onDone: (String) -> Unit,
) {
    // 用于存储编辑后的文件名
    var mediaName by rememberSaveable { mutableStateOf(name) }
    // 用于请求焦点的控制器
    val focusRequester = remember { FocusRequester() }
    HioDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.rename_to)) },
        content = {
            OutlinedTextField(
                value = mediaName,
                onValueChange = { mediaName = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
            )
        },
        confirmButton = {
            DoneButton(
                enabled = mediaName.isNotBlank(),
                onClick = { onDone(mediaName) },
            )
        },
        dismissButton = { CancelButton(onClick = onDismiss) },
    )

    // 延迟请求焦点，解决屏幕旋转时焦点未初始化的问题
    LaunchedEffect(key1 = Unit) {
        delay(200.milliseconds)
        focusRequester.requestFocus()
    }
}

/**
 * 视频信息对话框组件
 * 
 * 显示视频的详细信息，包括文件信息、视频轨道、音频轨道和字幕轨道等。
 * 
 * @param video 要显示信息的视频对象
 * @param onDismiss 对话框关闭回调
 */
@Composable
fun ShowVideoInfoDialog(
    video: Video,
    onDismiss: () -> Unit,
) {
    HioDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = video.displayName, maxLines = 1, overflow = TextOverflow.Ellipsis) },
        content = {
            HorizontalDivider()
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp),
                modifier = Modifier.verticalScroll(rememberScrollState()),
            ) {
                // 文件信息部分
                MediaInfoTitle(text = stringResource(R.string.file))
                MediaInfoText(
                    title = stringResource(id = R.string.file),
                    subText = video.nameWithExtension,
                )
                MediaInfoText(
                    title = stringResource(id = R.string.location),
                    subText = video.parentPath,
                )
                MediaInfoText(
                    title = stringResource(id = R.string.size),
                    subText = video.formattedFileSize,
                )
                MediaInfoText(
                    title = stringResource(id = R.string.duration),
                    subText = video.formattedDuration,
                )
                // 可选的视频格式信息
                video.format?.let {
                    MediaInfoText(
                        title = stringResource(id = R.string.format),
                        subText = it,
                    )
                }
                // 视频轨道信息
                video.videoStream?.let { videoStream ->
                    MediaInfoTitle(text = stringResource(id = R.string.video_track))
                    videoStream.title?.let {
                        MediaInfoText(
                            title = stringResource(id = R.string.title),
                            subText = it,
                        )
                    }
                    MediaInfoText(
                        title = stringResource(id = R.string.codec),
                        subText = videoStream.codecName,
                    )
                    MediaInfoText(
                        title = stringResource(id = R.string.resolution),
                        subText = "${videoStream.frameWidth} x ${videoStream.frameHeight}",
                    )
                    MediaInfoText(
                        title = stringResource(id = R.string.frame_rate),
                        subText = videoStream.frameRate.toInt().toString(),
                    )
                    Utils.formatBitrate(videoStream.bitRate)?.let {
                        MediaInfoText(
                            title = stringResource(id = R.string.bitrate),
                            subText = it,
                        )
                    }
                }
                // 音频轨道信息
                video.audioStreams.forEachIndexed { index, audioStream ->
                    MediaInfoTitle(text = "${stringResource(id = R.string.audio_track)} #${index + 1}")
                    audioStream.title?.let {
                        MediaInfoText(
                            title = stringResource(id = R.string.title),
                            subText = it,
                        )
                    }
                    MediaInfoText(
                        title = stringResource(id = R.string.codec),
                        subText = audioStream.codecName,
                    )
                    MediaInfoText(
                        title = stringResource(id = R.string.sample_rate),
                        subText = "${audioStream.sampleRate} Hz",
                    )
                    MediaInfoText(
                        title = stringResource(id = R.string.sample_format),
                        subText = audioStream.sampleFormat.toString(),
                    )
                    Utils.formatBitrate(audioStream.bitRate)?.let {
                        MediaInfoText(
                            title = stringResource(id = R.string.bitrate),
                            subText = it,
                        )
                    }
                    MediaInfoText(
                        title = stringResource(id = R.string.channels),
                        subText = audioStream.channelLayout ?: audioStream.channels.toString(),
                    )
                    Utils.formatLanguage(audioStream.language)?.let {
                        MediaInfoText(
                            title = stringResource(id = R.string.language),
                            subText = it,
                        )
                    }
                }
                // 字幕轨道信息
                video.subtitleStreams.forEachIndexed { index, subtitleStream ->
                    MediaInfoTitle(text = "${stringResource(id = R.string.subtitle_track)} #${index + 1}")
                    subtitleStream.title?.let {
                        MediaInfoText(
                            title = stringResource(id = R.string.title),
                            subText = it,
                        )
                    }
                    MediaInfoText(
                        title = stringResource(id = R.string.codec),
                        subText = subtitleStream.codecName,
                    )
                    Utils.formatLanguage(subtitleStream.language)?.let {
                        MediaInfoText(
                            title = stringResource(id = R.string.language),
                            subText = it,
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.okay))
            }
        },
    )
}

/**
 * 媒体信息标题组件
 * 
 * 用于在视频信息对话框中显示信息分类标题。
 * 
 * @param text 标题文字
 * @param paddingValues 内边距值
 */
@Composable
fun MediaInfoTitle(
    text: String,
    paddingValues: PaddingValues = PaddingValues(top = 16.dp, bottom = 2.dp),
) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.padding(paddingValues),
    )
}

/**
 * 媒体信息文本组件
 * 
 * 用于在视频信息对话框中显示键值对形式的信息。
 * 
 * @param title 信息标题（键）
 * @param subText 信息内容（值）
 * @param modifier 组件修饰符
 */
@Composable
fun MediaInfoText(
    title: String,
    subText: String,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier) {
        Text(text = "$title: ", style = MaterialTheme.typography.titleSmall)
        Text(text = subText)
    }
}

/**
 * 计算最小公倍数
 * 
 * @param a 第一个整数
 * @param b 第二个整数
 * @return 最小公倍数
 */
fun lcm(a: Int, b: Int): Int {
    return abs(a * b) / gcd(a, b)
}

/**
 * 计算最大公约数（欧几里得算法）
 * 
 * @param a 第一个整数
 * @param b 第二个整数
 * @return 最大公约数
 */
fun gcd(a: Int, b: Int): Int {
    return if (b == 0) a else gcd(b, a % b)
}