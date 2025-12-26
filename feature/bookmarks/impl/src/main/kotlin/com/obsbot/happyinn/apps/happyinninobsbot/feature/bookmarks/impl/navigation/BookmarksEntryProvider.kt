package com.obsbot.happyinn.apps.happyinninobsbot.feature.bookmarks.impl.navigation

import androidx.compose.material3.SnackbarDuration.Short
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult.ActionPerformed
import androidx.compose.runtime.compositionLocalOf
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.obsbot.happyinn.apps.happyinninobsbot.feature.bookmarks.impl.BookmarksScreen
import com.obsbot.happyinn.apps.happyinninobsbot.core.navigation.Navigator
import com.obsbot.happyinn.apps.happyinninobsbot.feature.bookmarks.api.navigation.BookmarksNavKey
import com.obsbot.happyinn.apps.happyinninobsbot.feature.topic.api.navigation.navigateToTopic


/**
 * 书签功能的导航条目提供器函数
 * 用于在导航框架中注册书签页面的展示逻辑
 *
 * @param navigator 导航器实例，用于处理页面间跳转
 */
fun EntryProviderScope<NavKey>.bookmarksEntry(navigator: Navigator) {
    // 注册 BookmarksNavKey 对应的导航条目
    entry<BookmarksNavKey> {
        // 获取本地的 SnackbarHostState 实例
        val snackbarHostState = LocalSnackbarHostState.current
        // 显示书签屏幕组件
        BookmarksScreen(
            // 处理主题点击事件，跳转到对应主题页面
            onTopicClick = navigator::navigateToTopic,
            // 处理 Snackbar 显示请求
            onShowSnackbar = { message, action ->
                // 显示 Snackbar 并检查用户是否点击了操作按钮
                snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = action,
                    duration = Short,
                ) == ActionPerformed
            },
        )
    }
}

// TODO: Why is this here?

/**
 * 本地 SnackbarHostState 组合值
 * 用于在 Compose 组件树中传递 SnackbarHostState 实例
 */
val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> {
    error("SnackbarHostState state should be initialized at runtime")
}
