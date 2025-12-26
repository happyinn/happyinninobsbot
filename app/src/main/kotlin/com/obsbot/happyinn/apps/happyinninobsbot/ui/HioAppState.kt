package com.obsbot.happyinn.apps.happyinninobsbot.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation3.runtime.NavKey
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.UserNewsResourceRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.util.NetworkMonitor
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.util.TimeZoneMonitor
import com.obsbot.happyinn.apps.happyinninobsbot.core.navigation.NavigationState
import com.obsbot.happyinn.apps.happyinninobsbot.core.navigation.rememberNavigationState
import com.obsbot.happyinn.apps.happyinninobsbot.feature.bookmarks.api.navigation.BookmarksNavKey
import com.obsbot.happyinn.apps.happyinninobsbot.feature.foryou.api.navigation.ForYouNavKey
import com.obsbot.happyinn.apps.happyinninobsbot.navigation.TOP_LEVEL_NAV_ITEMS
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.datetime.TimeZone
import kotlin.toString

/**
 * 记住并创�?HioAppState 实例�?Composable 函数
 *
 * @param networkMonitor 网络监控器，用于监控网络连接状�?
 * @param userNewsResourceRepository 用户新闻资源仓库，用于获取用户相关的新闻内容
 * @param timeZoneMonitor 时区监控器，用于监控时区变化
 * @param coroutineScope 协程作用域，默认使用 rememberCoroutineScope()
 * @return HioAppState 实例
 */
@Composable
fun rememberHioAppState(
    networkMonitor: NetworkMonitor,
    userNewsResourceRepository: UserNewsResourceRepository,
    timeZoneMonitor: TimeZoneMonitor,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
): HioAppState {
    // 记住导航状态，初始页面�?ForYouNavKey
    val navigationState = rememberNavigationState(ForYouNavKey, TOP_LEVEL_NAV_ITEMS.keys)

    // 导航跟踪副作用，用于性能监控
//    NavigationTrackingSideEffect(navigationState)

    // 记住并返�?HioAppState 实例
    return remember(
        navigationState,
        coroutineScope,
        networkMonitor,
        userNewsResourceRepository,
        timeZoneMonitor,
    ) {
        HioAppState(
            navigationState = navigationState,
            coroutineScope = coroutineScope,
            networkMonitor = networkMonitor,
            userNewsResourceRepository = userNewsResourceRepository,
            timeZoneMonitor = timeZoneMonitor,
        )
    }
}

/**
 * Now in Android 应用状态类，包含应用的各种状态信�?
 *
 * @param navigationState 导航状�?
 * @param coroutineScope 协程作用�?
 * @param networkMonitor 网络监控�?
 * @param userNewsResourceRepository 用户新闻资源仓库
 * @param timeZoneMonitor 时区监控�?
 */
@Stable
class HioAppState(
    val navigationState: NavigationState,
    coroutineScope: CoroutineScope,
    networkMonitor: NetworkMonitor,
    userNewsResourceRepository: UserNewsResourceRepository,
    timeZoneMonitor: TimeZoneMonitor,
) {
    /**
     * 网络离线状态流
     * 将网络在线状态取反得到离线状态，并在协程作用域内共享状�?
     */
    val isOffline = networkMonitor.isOnline
        .map(Boolean::not)
        .stateIn(
            scope = coroutineScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false,
        )

    /**
     * 包含未读新闻资源的顶级导航键集合
     * 结合关注主题的新闻资源和已收藏的新闻资源，判断哪些顶级导航页面有未读内容
     */
    val topLevelNavKeysWithUnreadResources: StateFlow<Set<NavKey>> =
        userNewsResourceRepository.observeAllForFollowedTopics()
            .combine(userNewsResourceRepository.observeAllBookmarked()) { forYouNewsResources, bookmarkedNewsResources ->
                setOfNotNull(
                    // 如果为你推荐页面有任何未查看的新闻资源，则包�?ForYouNavKey
                    ForYouNavKey.takeIf { forYouNewsResources.any { !it.hasBeenViewed } },
                    // 如果书签页面有任何未查看的新闻资源，则包�?BookmarksNavKey
                    BookmarksNavKey.takeIf { bookmarkedNewsResources.any { !it.hasBeenViewed } },
                )
            }
            .stateIn(
                coroutineScope,
                SharingStarted.WhileSubscribed(5_000),
                initialValue = emptySet(),
            )

    /**
     * 当前时区状态流
     * 监控时区变化并在协程作用域内共享状�?
     */
    val currentTimeZone = timeZoneMonitor.currentTimeZone
        .stateIn(
            coroutineScope,
            SharingStarted.WhileSubscribed(5_000),
            TimeZone.currentSystemDefault(),
        )
}


/**
 * 导航跟踪副作用函数，用于�?JankStats 配合使用存储导航事件信息
 *
 * @param navigationState 导航状�?
 */
/*
@Composable
private fun NavigationTrackingSideEffect(navigationState: NavigationState) {
    TrackDisposableJank(navigationState.currentKey) { metricsHolder ->
        metricsHolder.state?.putState("Navigation", navigationState.currentKey.toString())
        onDispose {}
    }
}*/

