package com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.impl.composables

/**
 * 视频列表项组件
 * 
 * 该文件包含用于显示视频信息的UI组件，支持列表和网格两种布局模式。
 * 视频项可以显示缩略图、标题、路径、大小、分辨率、时长和播放进度等信息。
 */

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.icon.HioIcons
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.theme.HioPlayerTheme
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.UserData
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.MediaLayoutMode
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.Video
import com.obsbot.happyinn.apps.happyinninobsbot.core.ui.video.ListItemComponent


/**
 * 视频项组件
 * 
 * 根据用户偏好的媒体布局模式，以列表或网格方式显示视频信息。
 * 
 * @param video 要显示的视频数据
 * @param isRecentlyPlayedVideo 是否为最近播放的视频
 * @param preferences 用户偏好设置，用于决定显示样式和内容
 * @param modifier 组件修饰符
 */
@Composable
fun VideoItem(
    video: Video,
    isRecentlyPlayedVideo: Boolean,
    preferences: UserData,
    modifier: Modifier = Modifier,
) {
    // 根据媒体布局模式选择不同的展示方式
    when (preferences.mediaLayoutMode) {
        MediaLayoutMode.LIST -> VideoListItem(
            video = video,
            isRecentlyPlayedVideo = isRecentlyPlayedVideo,
            preferences = preferences,
            modifier = modifier,
        )
        MediaLayoutMode.GRID -> VideoGridItem(
            video = video,
            isRecentlyPlayedVideo = isRecentlyPlayedVideo,
            preferences = preferences,
            modifier = modifier,
        )
    }
}

/**
 * 视频列表项组件
 * 
 * 以列表形式显示视频，包含缩略图、标题、路径和统计信息。
 * 
 * @param video 要显示的视频数据
 * @param isRecentlyPlayedVideo 是否为最近播放的视频
 * @param preferences 用户偏好设置
 * @param modifier 组件修饰符
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun VideoListItem(
    video: Video,
    isRecentlyPlayedVideo: Boolean,
    preferences: UserData,
    modifier: Modifier = Modifier,
) {
    ListItemComponent(
        // 根据是否最近播放设置标题和副标题颜色
        colors = ListItemDefaults.colors(
            headlineColor = if (isRecentlyPlayedVideo && preferences.markLastPlayedMedia) {
                MaterialTheme.colorScheme.primary
            } else {
                ListItemDefaults.colors().headlineColor
            },
            supportingColor = if (isRecentlyPlayedVideo && preferences.markLastPlayedMedia) {
                MaterialTheme.colorScheme.primary
            } else {
                ListItemDefaults.colors().supportingTextColor
            },
        ),
        // 头部内容：视频缩略图
        leadingContent = {
            ThumbnailView(
                video = video,
                preferences = preferences,
                modifier = Modifier
                    .width(min(150.dp, LocalConfiguration.current.screenWidthDp.dp * 0.35f)),
            )
        },
        // 标题内容：视频名称
        headlineContent = {
            Text(
                text = if (preferences.showExtensionField) video.nameWithExtension else video.displayName,
                maxLines = 2,
                style = MaterialTheme.typography.titleMedium,
                overflow = TextOverflow.Ellipsis,
            )
        },
        // 副标题内容：路径和统计信息
        supportingContent = {
            // 如果用户偏好显示路径，则显示视频路径
            if (preferences.showPathField) {
                Text(
                    text = video.path.substringBeforeLast("/"),
                    maxLines = 2,
                    style = MaterialTheme.typography.bodySmall,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(vertical = 2.dp),
                )
            }
            // 流式布局显示统计信息（大小、分辨率等）
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp),
                horizontalArrangement = Arrangement.spacedBy(5.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                // 如果用户偏好显示大小，则显示文件大小
                if (preferences.showSizeField) {
                    InfoChip(text = video.formattedFileSize)
                }
                // 如果用户偏好显示分辨率且视频有高度，则显示分辨率
                if (preferences.showResolutionField && video.height > 0) {
                    InfoChip(text = "${video.height}p")
                }
            }
        },
        modifier = modifier,
    )
}

/**
 * 视频网格项组件
 * 
 * 以网格形式显示视频，主要显示缩略图和名称。
 * 
 * @param video 要显示的视频数据
 * @param isRecentlyPlayedVideo 是否为最近播放的视频
 * @param preferences 用户偏好设置
 * @param modifier 组件修饰符
 */
@Composable
private fun VideoGridItem(
    video: Video,
    isRecentlyPlayedVideo: Boolean,
    preferences: UserData,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(IntrinsicSize.Min),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // 视频缩略图
        ThumbnailView(
            video = video,
            preferences = preferences,
        )
        // 视频名称
        Text(
            text = if (preferences.showExtensionField) video.nameWithExtension else video.displayName,
            maxLines = 2,
            style = MaterialTheme.typography.titleMedium,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
            color = if (isRecentlyPlayedVideo && preferences.markLastPlayedMedia) {
                MaterialTheme.colorScheme.primary
            } else {
                ListItemDefaults.colors().headlineColor
            },
        )
    }
}

/**
 * 缩略图视图组件
 * 
 * 显示视频的缩略图，支持显示时长标签和播放进度条。
 * 如果没有缩略图，则显示默认视频图标。
 * 
 * @param modifier 组件修饰符
 * @param video 视频数据
 * @param preferences 用户偏好设置
 */
@Composable
private fun ThumbnailView(
    modifier: Modifier = Modifier,
    video: Video,
    preferences: UserData,
) {
    val context = LocalContext.current
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
            .aspectRatio(16f / 10f),
    ) {
        // 默认视频图标（当没有缩略图时显示）
        Icon(
            imageVector = HioIcons.Video,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.surfaceColorAtElevation(100.dp),
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize(0.5f),
        )
        // 如果用户偏好显示缩略图且缩略图路径有效，则异步加载缩略图
        if (preferences.showThumbnailField && !video.thumbnailPath.isNullOrBlank()) {
            AsyncImage(
                model = ImageRequest.Builder(context)
                    .data(video.thumbnailPath)
                    .crossfade(true)
                    .build(),
                contentDescription = null,
                alignment = Alignment.Center,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
        }
        // 如果用户偏好显示时长，则在右下角显示时长信息芯片
        if (preferences.showDurationField) {
            InfoChip(
                text = video.formattedDuration,
                modifier = Modifier
                    .padding(5.dp)
                    .align(Alignment.BottomEnd),
                backgroundColor = Color.Black.copy(alpha = 0.6f),
                contentColor = Color.White,
                shape = MaterialTheme.shapes.extraSmall,
            )
        }

        // 如果用户偏好显示播放进度且视频有播放进度，则显示进度条
        if (preferences.showPlayedProgress && video.playedPercentage > 0) {
            Box(
                modifier = Modifier
                    .height(4.dp)
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
            ) {
                // 进度条背景
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                )
                // 实际进度（使用圆形剪辑）
                Box(
                    modifier = Modifier
                        .fillMaxWidth(video.playedPercentage)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                )
            }
        }
    }
}

@PreviewLightDark
@Composable
fun VideoItemRecentlyPlayedPreview() {
    HioPlayerTheme {
        Surface {
            VideoListItem(
                video = Video.sample,
                preferences = UserData.DEFAULT,
                isRecentlyPlayedVideo = true,
            )
        }
    }
}

@PreviewLightDark
@Composable
fun VideoItemPreview() {
    HioPlayerTheme {
        Surface {
            VideoListItem(
                video = Video.sample,
                preferences = UserData.DEFAULT,
                isRecentlyPlayedVideo = false,
            )
        }
    }
}

@Preview
@PreviewLightDark
@Composable
fun VideoGridItemPreview() {
    HioPlayerTheme {
        VideoGridItem(
            video = Video.sample,
            preferences = UserData.DEFAULT,
            isRecentlyPlayedVideo = true,
        )
    }
}

