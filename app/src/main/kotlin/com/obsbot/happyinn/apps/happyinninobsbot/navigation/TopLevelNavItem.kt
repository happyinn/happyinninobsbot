/*
 * 版权所有 2025 The Android Open Source Project
 *
 * 根据 Apache 许可证 2.0 版（"许可证"）授权；
 * 除非符合许可证要求，否则您不得使用此文件。
 * 您可以在以下位置获得许可证副本：
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * 除非适用法律要求或书面同意，根据许可证分发的软件
 * 是基于"按原样"的基础上分发的，不附带任何明示或暗示的担保条件。
 * 请参阅许可证了解特定语言 governing permissions 和 limitations。
 */
package com.obsbot.happyinn.apps.happyinninobsbot.navigation

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.vector.ImageVector
import com.obsbot.happyinn.apps.happyinninobsbot.R
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.icon.HioIcons
import com.obsbot.happyinn.apps.happyinninobsbot.feature.bookmarks.api.navigation.BookmarksNavKey
import com.obsbot.happyinn.apps.happyinninobsbot.feature.foryou.api.navigation.ForYouNavKey
import com.obsbot.happyinn.apps.happyinninobsbot.feature.interests.api.navigation.InterestsNavKey
import com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.api.navigation.VideosNavKey

import com.obsbot.happyinn.apps.happyinninobsbot.feature.bookmarks.api.R as bookmarksR
import com.obsbot.happyinn.apps.happyinninobsbot.feature.foryou.api.R as forYouR
import com.obsbot.happyinn.apps.happyinninobsbot.feature.search.api.R as searchR
import com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.api.R as videosR

/**
 * 应用程序顶级导航项的数据类型。包含有关当前路由的UI信息，
 * 这些信息用于顶部应用栏和通用导航UI中。
 *
 * @param selectedIcon 当前目的地被选中时在导航UI中显示的图标
 * @param unselectedIcon 当前目的地未被选中时在导航UI中显示的图标
 * @param iconTextId 在导航UI中显示的文本资源ID
 * @param titleTextId 在顶部应用栏中显示的文本资源ID
 */
data class TopLevelNavItem(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    @StringRes val iconTextId: Int,
    @StringRes val titleTextId: Int,
)

/**
 * "为你推荐"导航项
 * 使用 Upcoming 图标，标题为应用程序名称
 */
val FOR_YOU = TopLevelNavItem(
    selectedIcon = HioIcons.Upcoming,
    unselectedIcon = HioIcons.UpcomingBorder,
    iconTextId = forYouR.string.feature_foryou_api_title,
    titleTextId = R.string.app_name,
)

/**
 * "书签"导航项
 * 使用 Bookmarks 图标，图标文本和标题都显示为"书签"
 */
val BOOKMARKS = TopLevelNavItem(
    selectedIcon = HioIcons.Bookmarks,
    unselectedIcon = HioIcons.BookmarksBorder,
    iconTextId = bookmarksR.string.feature_bookmarks_api_title,
    titleTextId = bookmarksR.string.feature_bookmarks_api_title,
)


/**
 * "兴趣"导航项
 * 使用 Grid3x3 图标，图标文本和标题都显示为"兴趣"
 */
val VIDEOS = TopLevelNavItem(
    selectedIcon = HioIcons.Video,
    unselectedIcon = HioIcons.Video,
    iconTextId = videosR.string.feature_videos_api_title,
    titleTextId = videosR.string.feature_videos_api_title,
)

/**
 * "兴趣"导航项
 * 使用 Grid3x3 图标，图标文本和标题都显示为"兴趣"
 */
val INTERESTS = TopLevelNavItem(
    selectedIcon = HioIcons.Grid3x3,
    unselectedIcon = HioIcons.Grid3x3,
    iconTextId = searchR.string.feature_search_api_interests,
    titleTextId = searchR.string.feature_search_api_interests,
)




/**
 * 顶级导航项映射表
 * 将导航键与对应的导航项进行映射关联
 */
val TOP_LEVEL_NAV_ITEMS = mapOf(
    ForYouNavKey to FOR_YOU,
    BookmarksNavKey to BOOKMARKS,
    InterestsNavKey(null) to INTERESTS,
    VideosNavKey to VIDEOS
)
