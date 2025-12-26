package com.obsbot.happyinn.apps.happyinninobsbot.core.ui

import android.content.Context
import android.net.Uri
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import androidx.compose.foundation.layout.padding
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
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.UserNewsResource

/**
 * LazyStaggeredGridScope的扩展函数，用于定义视频资源的推荐流
 * 根据[feedState]状态，此函数可能不会渲染任何项�?
 *
 * @param feedState 视频流的当前状态（加载中或加载成功�?
 * @param onNewsResourcesCheckedChanged 当视频资源的收藏状态改变时调用的回调函�?
 * @param onNewsResourceViewed 当视频资源被查看时调用的回调函数
 * @param onTopicClick 当视频中的主题标签被点击时调用的回调函数
 * @param onExpandedCardClick 当展开的视频卡片被点击时调用的可选回调函�?
 */
fun LazyStaggeredGridScope.newsFeed(
    feedState: NewsFeedUiState,
    onNewsResourcesCheckedChanged: (String, Boolean) -> Unit,
    onNewsResourceViewed: (String) -> Unit,
    onTopicClick: (String) -> Unit,
    onExpandedCardClick: () -> Unit = {},
) {
    when (feedState) {
        // 加载状态下不显示任何内�?
        NewsFeedUiState.Loading -> Unit
        // 加载成功状态下，显示视频列�?
        is NewsFeedUiState.Success -> {
            items(
                items = feedState.feed,
                key = { it.id },
                contentType = { "NewsFeedItem" },
            ) { userNewsResource ->
                // 获取当前上下文、分析助手和主题背景�?
                val context = LocalContext.current
                val analyticsHelper = LocalAnalyticsHelper.current
                val backgroundColor = MaterialTheme.colorScheme.background.toArgb()

                // 渲染视频资源卡片
                NewsResourceCardExpanded(
                    userNewsResource = userNewsResource,
                    isBookmarked = userNewsResource.isSaved,
                    onClick = {
                        onExpandedCardClick()
                        // 记录视频资源打开事件
                        analyticsHelper.logNewsResourceOpened(
                            newsResourceId = userNewsResource.id,
                        )
                        // 使用自定义Chrome标签打开视频URL
                        launchCustomChromeTab(context, Uri.parse(userNewsResource.url), backgroundColor)

                        // 更新视频资源的已观看状�?
                        onNewsResourceViewed(userNewsResource.id)
                    },
                    hasBeenViewed = userNewsResource.hasBeenViewed,
                    onToggleBookmark = {
                        // 更新视频资源的收藏状�?
                        onNewsResourcesCheckedChanged(
                            userNewsResource.id,
                            !userNewsResource.isSaved,
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
 * @param context 上下文对�?
 * @param uri 要打开的URI链接
 * @param toolbarColor 自定义标签栏的颜�?
 */
fun launchCustomChromeTab(context: Context, uri: Uri, @ColorInt toolbarColor: Int) {
    // 创建自定义标签颜色方案参�?
    val customTabBarColor = CustomTabColorSchemeParams.Builder()
        .setToolbarColor(toolbarColor).build()
    // 构建自定义标签意�?
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
sealed interface NewsFeedUiState {
    /**
     * 视频流正在加载中
     * 表示数据尚未准备好显�?
     */
    data object Loading : NewsFeedUiState

    /**
     * 视频流加载成�?
     * 包含已加载的视频资源列表
     */
    data class Success(
        /**
         * 此推荐流中包含的视频资源列表
         */
        val feed: List<UserNewsResource>,
    ) : NewsFeedUiState
}

/**
 * 视频流加载状态的预览组件
 * 用于在Compose预览中展示加载状态的UI
 */
@Preview
@Composable
private fun NewsFeedLoadingPreview() {
    HioTheme {
        LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Adaptive(300.dp)) {
            newsFeed(
                feedState = NewsFeedUiState.Loading,
                onNewsResourcesCheckedChanged = { _, _ -> },
                onNewsResourceViewed = {},
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
private fun NewsFeedContentPreview(
    @PreviewParameter(UserNewsResourcePreviewParameterProvider::class)
    userNewsResources: List<UserNewsResource>,
) {
    HioTheme {
        LazyVerticalStaggeredGrid(columns = StaggeredGridCells.Adaptive(300.dp)) {
            newsFeed(
                feedState = NewsFeedUiState.Success(userNewsResources),
                onNewsResourcesCheckedChanged = { _, _ -> },
                onNewsResourceViewed = {},
                onTopicClick = {},
            )
        }
    }
}
