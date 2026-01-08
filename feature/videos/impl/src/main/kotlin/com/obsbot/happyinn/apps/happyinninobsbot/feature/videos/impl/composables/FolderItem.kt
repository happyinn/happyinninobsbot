package com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.impl.composables

/**
 * 文件夹列表项组件
 * 
 * 该文件包含用于显示文件夹的UI组件，支持列表和网格两种布局模式。
 * 根据用户偏好的媒体布局模式选择相应的展示方式。
 */

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import com.obsbot.happyinn.apps.happyinninobsbot.Utils
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.theme.HioPlayerTheme
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.UserData
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.Folder
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.MediaLayoutMode
import com.obsbot.happyinn.apps.happyinninobsbot.core.ui.video.ListItemComponent
import com.obsbot.happyinn.apps.happyinninobsbot.core.ui.R

/**
 * 文件夹项组件
 * 
 * 根据用户偏好的媒体布局模式，以列表或网格方式显示文件夹信息。
 * 
 * @param folder 要显示的文件夹数据
 * @param isRecentlyPlayedFolder 是否为最近播放的文件夹
 * @param preferences 用户偏好设置，用于决定显示样式和内容
 * @param modifier 组件修饰符
 */
@Composable
fun FolderItem(
    folder: Folder,
    isRecentlyPlayedFolder: Boolean,
    preferences: UserData,
    modifier: Modifier = Modifier,
) {
    // 根据媒体布局模式选择不同的展示方式
    when (preferences.mediaLayoutMode) {
        MediaLayoutMode.LIST -> FolderListItem(
            folder = folder,
            isRecentlyPlayedFolder = isRecentlyPlayedFolder,
            preferences = preferences,
            modifier = modifier,
        )
        MediaLayoutMode.GRID -> FolderGridItem(
            folder = folder,
            isRecentlyPlayedFolder = isRecentlyPlayedFolder,
            preferences = preferences,
            modifier = modifier,
        )
    }
}

/**
 * 文件夹列表项组件
 * 
 * 以列表形式显示文件夹，包含文件夹图标、名称、路径和统计信息。
 * 
 * @param folder 要显示的文件夹数据
 * @param isRecentlyPlayedFolder 是否为最近播放的文件夹
 * @param preferences 用户偏好设置
 * @param modifier 组件修饰符
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FolderListItem(
    folder: Folder,
    isRecentlyPlayedFolder: Boolean,
    preferences: UserData,
    modifier: Modifier = Modifier,
) {
    ListItemComponent(
        // 根据是否最近播放设置标题和副标题颜色
        colors = ListItemDefaults.colors(
            headlineColor = if (isRecentlyPlayedFolder && preferences.markLastPlayedMedia) {
                MaterialTheme.colorScheme.primary
            } else {
                ListItemDefaults.colors().headlineColor
            },
            supportingColor = if (isRecentlyPlayedFolder && preferences.markLastPlayedMedia) {
                MaterialTheme.colorScheme.primary
            } else {
                ListItemDefaults.colors().supportingTextColor
            },
        ),
        // 头部内容：文件夹图标和时长信息
        leadingContent = {
            Box {
                // 文件夹图标
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.folder_thumb),
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.secondaryContainer,
                    modifier = Modifier
                        .width(min(90.dp, LocalConfiguration.current.screenWidthDp.dp * 0.3f))
                        .aspectRatio(20 / 17f),
                )

                // 如果用户偏好显示时长，则显示时长信息芯片
                if (preferences.showDurationField) {
                    InfoChip(
                        text = Utils.formatDurationMillis(folder.mediaDuration),
                        modifier = Modifier
                            .padding(5.dp)
                            .padding(bottom = 3.dp)
                            .align(Alignment.BottomEnd),
                        backgroundColor = Color.Black.copy(alpha = 0.6f),
                        contentColor = Color.White,
                        shape = MaterialTheme.shapes.extraSmall,
                    )
                }
            }
        },
        // 标题内容：文件夹名称
        headlineContent = {
            Text(
                text = folder.name,
                maxLines = 2,
                style = MaterialTheme.typography.titleMedium,
                overflow = TextOverflow.Ellipsis,
            )
        },
        // 副标题内容：路径和统计信息
        supportingContent = {
            // 如果用户偏好显示路径，则显示文件夹路径
            if (preferences.showPathField) {
                Text(
                    text = folder.path.substringBeforeLast("/"),
                    maxLines = 2,
                    style = MaterialTheme.typography.bodySmall,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(vertical = 2.dp),
                )
            }
            // 流式布局显示统计信息（视频数量、文件夹数量、文件大小）
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                // 显示视频数量
                if (folder.mediaList.isNotEmpty()) {
                    InfoChip(
                        text = "${folder.mediaList.size} " +
                                stringResource(id = R.string.video.takeIf { folder.mediaList.size == 1 }
                                    ?: R.string.videos),
                    )
                }
                // 显示子文件夹数量
                if (folder.folderList.isNotEmpty()) {
                    InfoChip(
                        text = "${folder.folderList.size} " +
                                stringResource(id = R.string.folder.takeIf { folder.folderList.size == 1 }
                                    ?: R.string.folders),
                    )
                }
                // 如果用户偏好显示大小，则显示文件大小
                if (preferences.showSizeField) {
                    InfoChip(text = Utils.formatFileSize(folder.mediaSize))
                }
            }
        },
        modifier = modifier,
    )
}

/**
 * 文件夹网格项组件
 * 
 * 以网格形式显示文件夹，主要显示文件夹图标和名称。
 * 
 * @param folder 要显示的文件夹数据
 * @param isRecentlyPlayedFolder 是否为最近播放的文件夹
 * @param preferences 用户偏好设置
 * @param modifier 组件修饰符
 */
@Composable
private fun FolderGridItem(
    folder: Folder,
    isRecentlyPlayedFolder: Boolean,
    preferences: UserData,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.width(IntrinsicSize.Min),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // 文件夹图标容器
        Box {
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.folder_thumb),
                contentDescription = "",
                tint = MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier
                    .width(min(90.dp, LocalConfiguration.current.screenWidthDp.dp * 0.3f))
                    .aspectRatio(20 / 17f),
            )

            // 如果用户偏好显示时长，则显示时长信息芯片
            if (preferences.showDurationField) {
                InfoChip(
                    text = Utils.formatDurationMillis(folder.mediaDuration),
                    modifier = Modifier
                        .padding(5.dp)
                        .padding(bottom = 3.dp)
                        .align(Alignment.BottomEnd),
                    backgroundColor = Color.Black.copy(alpha = 0.6f),
                    contentColor = Color.White,
                    shape = MaterialTheme.shapes.extraSmall,
                )
            }
        }

        // 文件夹名称和统计信息
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // 文件夹名称
            Text(
                text = folder.name,
                maxLines = 2,
                style = MaterialTheme.typography.titleMedium,
                overflow = TextOverflow.Ellipsis,
                color = if (isRecentlyPlayedFolder && preferences.markLastPlayedMedia) {
                    MaterialTheme.colorScheme.primary
                } else {
                    ListItemDefaults.colors().headlineColor
                },
                textAlign = TextAlign.Center,
            )
            // 构建统计信息文本
            val mediaCount = if (folder.mediaList.isNotEmpty()) {
                "${folder.mediaList.size} " + stringResource(id = R.string.video.takeIf { folder.mediaList.size == 1 } ?: R.string.videos)
            } else {
                null
            }
            val folderCount = if (folder.folderList.isNotEmpty()) {
                "${folder.folderList.size} " + stringResource(id = R.string.folder.takeIf { folder.folderList.size == 1 } ?: R.string.folders)
            } else {
                null
            }

            // 显示统计信息
            Text(
                text = buildString {
                    mediaCount?.let {
                        append(it)
                        folderCount?.let {
                            append(", ")
                            append("\u00A0")
                        }
                    }
                    folderCount?.let {
                        append(it)
                    }
                },
                maxLines = 2,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Normal),
                textAlign = TextAlign.Center,
            )
        }
    }
}
@PreviewLightDark
@Composable
fun FolderItemRecentlyPlayedPreview() {
    HioPlayerTheme {
        FolderListItem(
            folder = Folder.sample,
            preferences = UserData.DEFAULT,
            isRecentlyPlayedFolder = true,
        )
    }
}

@PreviewLightDark
@Composable
fun FolderItemPreview() {
    HioPlayerTheme {
        FolderListItem(
            folder = Folder.sample.copy(folderList = listOf(Folder.sample)),
            preferences = UserData.DEFAULT,
            isRecentlyPlayedFolder = false,
        )
    }
}

@PreviewLightDark
@Composable
fun FolderGridViewPreview() {
    HioPlayerTheme {
        FolderGridItem(
            folder = Folder.sample.copy(folderList = listOf(Folder.sample)),
            preferences = UserData.DEFAULT,
            isRecentlyPlayedFolder = true,
        )
    }
}

