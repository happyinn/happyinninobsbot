package com.obsbot.happyinn.apps.happyinninobsbot.core.ui

import android.content.Context
import android.net.Uri
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.obsbot.happyinn.apps.happyinninobsbot.core.analytics.LocalAnalyticsHelper
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.theme.HioTheme
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.UserVideosResource

/**
 * LazyStaggeredGridScope的扩展函数，用于定义视频资源的推荐流
 * 根据[feedState]状态，此函数可能不会渲染任何项目
 *
 * @param feedState 视频流的当前状态（加载中或加载成功）
 * @param onVideosResourcesCheckedChanged 当视频资源的收藏状态改变时调用的回调函数
 * @param onVideosResourceViewed 当视频资源被查看时调用的回调函数
 * @param onTopicClick 当视频中的主题标签被点击时调用的回调函数
 * @param onExpandedCardClick 当展开的视频卡片被点击时调用的可选回调函数
 */
fun LazyStaggeredGridScope.videosFeed(
    feedState: VideosFeedUiState,
    onVideosResourcesCheckedChanged: (String, Boolean) -> Unit,
    onVideosResourceViewed: (String) -> Unit,
    onTopicClick: (String) -> Unit,
    onExpandedCardClick: () -> Unit = {},
) {
    when (feedState) {
        // 加载状态下不显示任何内容
        VideosFeedUiState.Loading -> Unit
        // 加载成功状态下，显示视频列表
        is VideosFeedUiState.Success -> {
            items(
                items = feedState.feed,
                key = { it.id },
                contentType = { "videosFeedItem" },
            ) { userVideosResource ->
                // 获取当前上下文、分析助手和主题背景色
                val context = LocalContext.current
                val analyticsHelper = LocalAnalyticsHelper.current
                val backgroundColor = MaterialTheme.colorScheme.background.toArgb()

                // 渲染视频资源卡片
                VideosResourceCardExpanded(
                    userVideosResource = userVideosResource,
                    isBookmarked = userVideosResource.isSaved,
                    onClick = {
                        onExpandedCardClick()
                        // 记录视频资源打开事件
                        analyticsHelper.logVideosResourceOpened(
                            videosResourceId = userVideosResource.id,
                        )
                        // 使用自定义Chrome标签打开视频URL
                        launchCustomChromeTab(context, Uri.parse(userVideosResource.url), backgroundColor)

                        // 更新视频资源的已观看状态
                        onVideosResourceViewed(userVideosResource.id)
                    },
                    hasBeenViewed = userVideosResource.hasBeenViewed,
                    onToggleBookmark = {
                        // 更新视频资源的收藏状态
                        onVideosResourcesCheckedChanged(
                            userVideosResource.id,
                            !userVideosResource.isSaved,
                        )
                    },
                    onTopicClick = onTopicClick,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .animateItem(),
                )
            }
        }
    }
}

/**
 * 使用自定义Chrome标签打开指定的URI链接
 *
 * @param context 上下文对象
 * @param uri 要打开的URI链接
 * @param toolbarColor 自定义标签栏的颜色
 */
fun launchCustomChromeTab(context: Context, uri: Uri, @ColorInt toolbarColor: Int) {
    // 创建自定义标签颜色方案参数
    val customTabBarColor = CustomTabColorSchemeParams.Builder()
        .setToolbarColor(toolbarColor).build()
    // 构建自定义标签意图
    val customTabsIntent = CustomTabsIntent.Builder()
        .setDefaultColorSchemeParams(customTabBarColor)
        .build()

    // 启动自定义标签打开URL
    customTabsIntent.launchUrl(context, uri)
}

/**
 * 描述视频资源推荐流状态的密封接口
 * 提供不同状态下的UI表示
 */
sealed interface VideosFeedUiState {
    /**
     * 视频流正在加载中
     * 表示数据尚未准备好显示
     */
    data object Loading : VideosFeedUiState

    /**
     * 视频流加载成功
     * 包含已加载的视频资源列表
     */
    data class Success(
        /**
         * 此推荐流中包含的视频资源列表
         */
        val feed: List<UserVideosResource>,
    ) : VideosFeedUiState
}

/**
 * 视频流加载状态的预览组件
 * 用于在Compose预览中展示加载状态的UI
 */
@Preview
@Composable
private fun VideosFeedLoadingPreview() {
    HioTheme {
        LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Adaptive(300.dp)) {
            videosFeed(
                feedState = VideosFeedUiState.Loading,
                onVideosResourcesCheckedChanged = { _, _ -> },
                onVideosResourceViewed = {},
                onTopicClick = {},
            )
        }
    }
}

/**
 * 视频流内容的预览组件
 * 用于在Compose预览中展示加载成功状态的UI，支持手机和平板设备预览
 */
@Preview
@Preview(device = Devices.TABLET)
@Composable
private fun VideosFeedContentPreview(
    @PreviewParameter(UserVideosResourcePreviewParameterProvider::class)
    userVideosResources: List<UserVideosResource>,
) {
    HioTheme {
        LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Adaptive(300.dp)) {
            videosFeed(
                feedState = VideosFeedUiState.Success(userVideosResources),
                onVideosResourcesCheckedChanged = { _, _ -> },
                onVideosResourceViewed = {},
                onTopicClick = {},
            )
        }
    }
}