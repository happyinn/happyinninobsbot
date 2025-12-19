package com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository

import androidx.annotation.VisibleForTesting
import com.obsbot.happyinn.apps.happyinninobsbot.core.datastore.HioPreferencesDataSource
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.DarkThemeConfig
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.ThemeBrand
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.UserData
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class OfflineFirstUserDataRepository @Inject constructor(
    private val hioPreferencesDataSource: HioPreferencesDataSource,
//    private val analyticsHelper: AnalyticsHelper,
) : UserDataRepository {

    override val userData: Flow<UserData> =
        hioPreferencesDataSource.userData

    @VisibleForTesting
    override suspend fun setFollowedTopicIds(followedTopicIds: Set<String>) =
        hioPreferencesDataSource.setFollowedTopicIds(followedTopicIds)

    override suspend fun setTopicIdFollowed(followedTopicId: String, followed: Boolean) {
        hioPreferencesDataSource.setTopicIdFollowed(followedTopicId, followed)
//        analyticsHelper.logTopicFollowToggled(followedTopicId, followed)
    }

    override suspend fun setVideosResourceBookmarked(videosResourceId: String, bookmarked: Boolean) {
        hioPreferencesDataSource.setvideosResourceBookmarked(videosResourceId, bookmarked)
        /*analyticsHelper.logvideosResourceBookmarkToggled(
            videosResourceId = videosResourceId,
            isBookmarked = bookmarked,
        )*/
    }

    override suspend fun setVideosResourceViewed(videosResourceId: String, viewed: Boolean) =
        hioPreferencesDataSource.setvideosResourceViewed(videosResourceId, viewed)

    override suspend fun setThemeBrand(themeBrand: ThemeBrand) {
        hioPreferencesDataSource.setThemeBrand(themeBrand)
//        analyticsHelper.logThemeChanged(themeBrand.name)
    }

    override suspend fun setDarkThemeConfig(darkThemeConfig: DarkThemeConfig) {
        hioPreferencesDataSource.setDarkThemeConfig(darkThemeConfig)
//        analyticsHelper.logDarkThemeConfigChanged(darkThemeConfig.name)
    }

    override suspend fun setDynamicColorPreference(useDynamicColor: Boolean) {
        hioPreferencesDataSource.setDynamicColorPreference(useDynamicColor)
//        analyticsHelper.logDynamicColorPreferenceChanged(useDynamicColor)
    }

    override suspend fun setShouldHideOnboarding(shouldHideOnboarding: Boolean) {
        hioPreferencesDataSource.setShouldHideOnboarding(shouldHideOnboarding)
//        analyticsHelper.logOnboardingStateChanged(shouldHideOnboarding)
    }
}
