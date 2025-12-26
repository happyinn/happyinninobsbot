package com.obsbot.happyinn.apps.happyinninobsbot.core.datastore

import android.util.Log
import androidx.datastore.core.DataStore
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.DarkThemeConfig
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.ThemeBrand
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.UserData
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import kotlin.collections.associateWith
import kotlin.collections.forEach

/**
 * HIO偏好设置数据源，负责管理用户偏好设置的读取和更新
 *
 * @param userPreferences 用户偏好设置的数据存�?
 */
class HioPreferencesDataSource @Inject constructor(
    private val userPreferences: DataStore<UserPreferences>,
) {
    /**
     * 用户数据流，将偏好设置数据映射为用户数据模型
     */
    val userData = userPreferences.data
        .map {
            UserData(
                // 书签视频资源ID集合
                bookmarkedNewsResources = it.bookmarkedNewsResourceIdsMap.keys,
                // 已查看视频资源ID集合
                viewedNewsResources = it.viewedNewsResourceIdsMap.keys,
                // 关注的主题ID集合
                followedTopics = it.followedTopicIdsMap.keys,
                // 主题品牌设置
                themeBrand = when (it.themeBrand) {
                    null,
                    ThemeBrandProto.THEME_BRAND_UNSPECIFIED,
                    ThemeBrandProto.UNRECOGNIZED,
                    ThemeBrandProto.THEME_BRAND_DEFAULT,
                        -> ThemeBrand.DEFAULT

                    ThemeBrandProto.THEME_BRAND_ANDROID -> ThemeBrand.ANDROID
                },
                // 深色主题配置
                darkThemeConfig = when (it.darkThemeConfig) {
                    null,
                    DarkThemeConfigProto.DARK_THEME_CONFIG_UNSPECIFIED,
                    DarkThemeConfigProto.UNRECOGNIZED,
                    DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM,
                        ->
                        DarkThemeConfig.FOLLOW_SYSTEM

                    DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT ->
                        DarkThemeConfig.LIGHT

                    DarkThemeConfigProto.DARK_THEME_CONFIG_DARK -> DarkThemeConfig.DARK
                },
                // 是否使用动态颜�?
                useDynamicColor = it.useDynamicColor,
                // 是否隐藏引导页面
                shouldHideOnboarding = it.shouldHideOnboarding,
            )
        }

    /**
     * 设置关注的主题ID集合
     *
     * @param topicIds 主题ID集合
     */
    suspend fun setFollowedTopicIds(topicIds: Set<String>) {
        try {
            userPreferences.updateData {
                it.copy {
                    followedTopicIds.clear()
                    followedTopicIds.putAll(topicIds.associateWith { true })
                    updateShouldHideOnboardingIfNecessary()
                }
            }
        } catch (ioException: IOException) {
            Log.e("HioPreferences", "Failed to update user preferences", ioException)
        }
    }

    /**
     * 设置单个主题的关注状�?
     *
     * @param topicId 主题ID
     * @param followed 是否关注
     */
    suspend fun setTopicIdFollowed(topicId: String, followed: Boolean) {
        try {
            userPreferences.updateData {
                it.copy {
                    if (followed) {
                        followedTopicIds.put(topicId, true)
                    } else {
                        followedTopicIds.remove(topicId)
                    }
                    updateShouldHideOnboardingIfNecessary()
                }
            }
        } catch (ioException: IOException) {
            Log.e("HioPreferences", "Failed to update user preferences", ioException)
        }
    }

    /**
     * 设置主题品牌
     *
     * @param themeBrand 主题品牌
     */
    suspend fun setThemeBrand(themeBrand: ThemeBrand) {
        userPreferences.updateData {
            it.copy {
                this.themeBrand = when (themeBrand) {
                    ThemeBrand.DEFAULT -> ThemeBrandProto.THEME_BRAND_DEFAULT
                    ThemeBrand.ANDROID -> ThemeBrandProto.THEME_BRAND_ANDROID
                }
            }
        }
    }

    /**
     * 设置动态颜色偏�?
     *
     * @param useDynamicColor 是否使用动态颜�?
     */
    suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        userPreferences.updateData {
            it.copy { this.useDynamicColor = useDynamicColor }
        }
    }

    /**
     * 设置深色主题配置
     *
     * @param darkThemeConfig 深色主题配置
     */
    suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        userPreferences.updateData {
            it.copy {
                this.darkThemeConfig = when (darkThemeConfig) {
                    DarkThemeConfig.FOLLOW_SYSTEM ->
                        DarkThemeConfigProto.DARK_THEME_CONFIG_FOLLOW_SYSTEM
                    DarkThemeConfig.LIGHT -> DarkThemeConfigProto.DARK_THEME_CONFIG_LIGHT
                    DarkThemeConfig.DARK -> DarkThemeConfigProto.DARK_THEME_CONFIG_DARK
                }
            }
        }
    }

    /**
     * 设置视频资源的书签状�?
     *
     * @param newsResourceId 视频资源ID
     * @param bookmarked 是否添加书签
     */
    suspend fun setNewsResourceBookmarked(newsResourceId: String, bookmarked: Boolean) {
        try {
            userPreferences.updateData {
                it.copy {
                    if (bookmarked) {
                        bookmarkedNewsResourceIds.put(newsResourceId, true)
                    } else {
                        bookmarkedNewsResourceIds.remove(newsResourceId)
                    }
                }
            }
        } catch (ioException: IOException) {
            Log.e("HioPreferences", "Failed to update user preferences", ioException)
        }
    }

    /**
     * 设置单个视频资源的查看状�?
     *
     * @param newsResourceId 视频资源ID
     * @param viewed 是否已查�?
     */
    suspend fun setNewsResourceViewed(newsResourceId: String, viewed: Boolean) {
        setNewsResourcesViewed(listOf(newsResourceId), viewed)
    }

    /**
     * 设置多个视频资源的查看状�?
     *
     * @param newsResourceIds 视频资源ID列表
     * @param viewed 是否已查�?
     */
    suspend fun setNewsResourcesViewed(newsResourceIds: List<String>, viewed: Boolean) {
        userPreferences.updateData { prefs ->
            prefs.copy {
                newsResourceIds.forEach { id ->
                    if (viewed) {
                        viewedNewsResourceIds.put(id, true)
                    } else {
                        viewedNewsResourceIds.remove(id)
                    }
                }
            }
        }
    }

    /**
     * 获取变更列表版本信息
     *
     * @return 变更列表版本信息
     */
    suspend fun getChangeListVersions() = userPreferences.data
        .map {
            ChangeListVersions(
                topicVersion = it.topicChangeListVersion,
                newsResourceVersion = it.newsResourceChangeListVersion,
            )
        }
        .firstOrNull() ?: ChangeListVersions()

    /**
     * 更新变更列表版本信息
     *
     * @param update 版本信息更新函数
     */
    suspend fun updateChangeListVersion(update: ChangeListVersions.() -> ChangeListVersions) {
        try {
            userPreferences.updateData { currentPreferences ->
                val updatedChangeListVersions = update(
                    ChangeListVersions(
                        topicVersion = currentPreferences.topicChangeListVersion,
                        newsResourceVersion = currentPreferences.newsResourceChangeListVersion,
                    ),
                )

                currentPreferences.copy {
                    topicChangeListVersion = updatedChangeListVersions.topicVersion
                    newsResourceChangeListVersion = updatedChangeListVersions.newsResourceVersion
                }
            }
        } catch (ioException: IOException) {
            Log.e("HioPreferences", "Failed to update user preferences", ioException)
        }
    }

    /**
     * 设置是否隐藏引导页面
     *
     * @param shouldHideOnboarding 是否隐藏引导页面
     */
    suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        userPreferences.updateData {
            it.copy { this.shouldHideOnboarding = shouldHideOnboarding }
        }
    }
}

/**
 * 更新是否隐藏引导页面的辅助函�?
 * 如果没有关注任何主题和作者，则不应隐藏引导页�?
 */
private fun UserPreferencesKt.Dsl.updateShouldHideOnboardingIfNecessary() {
    if (followedTopicIds.isEmpty() && followedAuthorIds.isEmpty()) {
        shouldHideOnboarding = false
    }
}

