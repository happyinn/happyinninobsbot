 package com.obsbot.happyinn.apps.happyinninobsbot.core.model.data
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.MediaLayoutMode
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.MediaViewMode
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.Sort
import kotlinx.serialization.Serializable

/**
 * 用户数据模型
 *
 * @param bookmarkedNewsResources 用户收藏的新闻资源ID集合
 * @param viewedNewsResources 用户已查看的新闻资源ID集合
 * @param followedTopics 用户关注的主题ID集合
 * @param themeBrand 主题品牌设置
 * @param darkThemeConfig 深色主题配置
 * @param useDynamicColor 是否使用动态颜?
 * @param shouldHideOnboarding 是否隐藏引导页面
 */

@Serializable
data class UserData(

    // 用户收藏的新闻资源ID集合
    val bookmarkedNewsResources: Set<String>,
    // 用户已查看的新闻资源ID集合
    val viewedNewsResources: Set<String>,
    // 用户关注的主题ID集合
    val followedTopics: Set<String>,
    // 主题品牌设置
    val themeBrand: ThemeBrand,
    // 深色主题配置
    val darkThemeConfig: DarkThemeConfig,
    // 是否使用动态颜�?
    val useDynamicColor: Boolean,
    // 是否隐藏引导页面
    val shouldHideOnboarding: Boolean,

    val sortBy: Sort.By = Sort.By.TITLE,
    val sortOrder: Sort.Order = Sort.Order.ASCENDING,
    val markLastPlayedMedia: Boolean = true,
    val showFloatingPlayButton: Boolean = true,
    val excludeFolders: List<String> = emptyList(),
    val mediaViewMode: MediaViewMode = MediaViewMode.FOLDERS,
    val mediaLayoutMode: MediaLayoutMode = MediaLayoutMode.LIST,

    // Fields
    val showDurationField: Boolean = true,
    val showExtensionField: Boolean = false,
    val showPathField: Boolean = true,
    val showResolutionField: Boolean = false,
    val showSizeField: Boolean = false,
    val showThumbnailField: Boolean = true,
    val showPlayedProgress: Boolean = true,
    ) {
    companion object {
        val DEFAULT = UserData(
            bookmarkedNewsResources = emptySet(),
            viewedNewsResources = emptySet(),
            followedTopics = emptySet(),
            themeBrand = ThemeBrand.DEFAULT,
            darkThemeConfig = DarkThemeConfig.FOLLOW_SYSTEM,
            useDynamicColor = false,
            shouldHideOnboarding = false,
        )
    }
}

