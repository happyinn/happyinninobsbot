package com.obsbot.happyinn.apps.happyinninobsbot.core.ui.video

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyStaggeredGridScope
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.obsbot.happyinn.apps.happyinninobsbot.core.analytics.LocalAnalyticsHelper
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.MediaVideo


/**
 * LazyStaggeredGridScope的扩展函数，用于定义视频资源的推荐流
 * 根据[feedState]状态，此函数可能不会渲染任何项�?
 *
 * @param feedState 视频流的当前状态（加载中或加载成功)
 * @param onTopicClick 当视频中的主题标签被点击时调用的回调函数
 * @param onExpandedCardClick 当展开的视频卡片被点击时调用的可选回调函数
 */
fun LazyStaggeredGridScope.VideoFeed(
    feedState: VideoFeedUiState,
    onTopicClick: (String) -> Unit,
    onExpandedCardClick: () -> Unit = {},
) {
    when (feedState) {
        // 加载状态下不显示任何内容
        VideoFeedUiState.Loading -> Unit
        // 加载成功状态下，显示视频列表
        is VideoFeedUiState.Success -> {
            items(
                items = feedState.feed,
                key = { it.id },
                contentType = { "VideosFeedItem" },
            ) { videosResource ->
                // 获取当前上下文、分析助手和主题背景
                val context = LocalContext.current
                val analyticsHelper = LocalAnalyticsHelper.current
                val backgroundColor = MaterialTheme.colorScheme.background.toArgb()

                // 渲染视频资源卡片
                VideoResourceCardExpanded(
                    onClick = {

                    },
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .animateItem(),
                )
            }
        }
    }
}


/**
 * 描述视频资源推荐流状态的密封接口
 * 提供不同状态下的UI表示
 */
sealed interface VideoFeedUiState {
    /**
     * 视频流正在加载中
     * 表示数据尚未准备好显示
     */
    data object Loading : VideoFeedUiState

    /**
     * 视频流加载成功
     * 包含已加载的视频资源列表
     */
    data class Success(
        /**
         * 此推荐流中包含的视频资源列表
         */
        val feed: List<MediaVideo>,
    ) : VideoFeedUiState
}
