package com.obsbot.happyinn.apps.happyinninobsbot.core.ui.video

import android.net.Uri
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import com.obsbot.happyinn.apps.happyinninobsbot.core.analytics.LocalAnalyticsHelper
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.UserNewsResource
import com.obsbot.happyinn.apps.happyinninobsbot.core.ui.logNewsResourceOpened
import com.obsbot.happyinn.apps.happyinninobsbot.core.ui.news.NewsResourceCardExpanded
import com.obsbot.happyinn.apps.happyinninobsbot.core.ui.news.launchCustomChromeTab

/**
 * LazyListScope 的扩展函数，用于显示新闻资源卡片列表
 * 
 * 基于 [UserNewsResource] 列表显示 [NewsResourceCardExpanded] 卡片
 * 
 * @param items 新闻资源列表
 * @param onToggleBookmark 当用户点击书签按钮时的回调函数
 * @param onNewsResourceViewed 当新闻资源被查看时的回调函数
 * @param onTopicClick 当主题标签被点击时的回调函数
 * @param itemModifier 列表项的修饰器
 * 
 * 注意：当新闻资源卡片被点击时，会在 Chrome Custom Tab 中打开新闻资源 URL
 */
fun LazyListScope.userNewsResourceCardItems(
    items: List<UserNewsResource>,
    onToggleBookmark: (item: UserNewsResource) -> Unit,
    onNewsResourceViewed: (String) -> Unit,
    onTopicClick: (String) -> Unit,
    itemModifier: Modifier = Modifier,
) = items(
    items = items, // 新闻资源列表
    key = { it.id }, // 使用资源ID作为唯一键
    itemContent = { userNewsResource ->
        // 解析新闻资源的 URL
        val resourceUrl = Uri.parse(userNewsResource.url)
        // 获取主题背景色，用于 Chrome Custom Tab
        val backgroundColor = MaterialTheme.colorScheme.background.toArgb()
        // 获取当前上下文
        val context = LocalContext.current
        // 获取分析助手，用于记录用户行为
        val analyticsHelper = LocalAnalyticsHelper.current

        // 渲染展开的新闻资源卡片
        NewsResourceCardExpanded(
            userNewsResource = userNewsResource, // 新闻资源数据
            isBookmarked = userNewsResource.isSaved, // 是否已收藏
            hasBeenViewed = userNewsResource.hasBeenViewed, // 是否已查看
            onToggleBookmark = { onToggleBookmark(userNewsResource) }, // 切换收藏状态
            onClick = {
                // 记录新闻资源被打开的事件
                analyticsHelper.logNewsResourceOpened(
                    newsResourceId = userNewsResource.id,
                )
                // 在 Chrome Custom Tab 中打开新闻资源 URL
                launchCustomChromeTab(context, resourceUrl, backgroundColor)
                // 通知新闻资源已被查看
                onNewsResourceViewed(userNewsResource.id)
            },
            onTopicClick = onTopicClick, // 主题标签点击事件
            modifier = itemModifier, // 列表项修饰器
        )
    },
)
