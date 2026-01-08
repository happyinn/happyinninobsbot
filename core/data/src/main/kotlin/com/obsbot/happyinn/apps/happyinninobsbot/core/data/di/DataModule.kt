package com.obsbot.happyinn.apps.happyinninobsbot.core.data.di
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.DefaultRecentSearchRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.DefaultSearchContentsRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.OfflineFirstUserDataRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.OfflineFirstNewsRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.UserDataRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.NewsRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.OfflineFirstTopicsRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.RecentSearchRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.SearchContentsRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.TopicsRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.media.LocalMediaRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.media.LocalPreferencesRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.media.MediaRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.media.PreferencesRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.util.ConnectivityManagerNetworkMonitor
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.util.NetworkMonitor
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.util.TimeZoneBroadcastMonitor
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.util.TimeZoneMonitor
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * 数据层的 Dagger Hilt 模块
 * 提供数据仓库和工具类的依赖绑现
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    /**
     * 绑定主题仓库实现
     * 现OfflineFirstTopicsRepository 绑定现TopicsRepository 接口
     */
    @Binds
    internal abstract fun bindsTopicRepository(
        topicsRepository: OfflineFirstTopicsRepository,
    ): TopicsRepository

    /**
     * 绑定新闻资源仓库实现
     * 现OfflineFirstNewsRepository 绑定现NewsRepository 接口
     */
    @Binds
    internal abstract fun bindsNewsResourceRepository(
        newsRepository: OfflineFirstNewsRepository,
    ): NewsRepository

    /**
     * 绑定用户数据仓库实现
     * 现OfflineFirstUserDataRepository 绑定现UserDataRepository 接口
     */
    @Binds
    internal abstract fun bindsUserDataRepository(
        userDataRepository: OfflineFirstUserDataRepository,
    ): UserDataRepository

    /**
     * 绑定最近搜索仓库实现
     * 把DefaultRecentSearchRepository 绑定现RecentSearchRepository 接口
     */
    @Binds
    internal abstract fun bindsRecentSearchRepository(
        recentSearchRepository: DefaultRecentSearchRepository,
    ): RecentSearchRepository

    /**
     * 绑定搜索内容仓库实现
     * 把DefaultSearchContentsRepository 绑定现SearchContentsRepository 接口
     */
    @Binds
    internal abstract fun bindsSearchContentsRepository(
        searchContentsRepository: DefaultSearchContentsRepository,
    ): SearchContentsRepository

    /**
     * 绑定视频内容仓库实现
     * 把DefaultSearchContentsRepository 绑定现SearchContentsRepository 接口
     */
    @Binds
    internal abstract fun bindMediaRepository(
        mediaRepository: LocalMediaRepository,
    ): MediaRepository

    /**
     * 绑定视频内容仓库实现
     * 现LocalPreferencesRepository 绑定现PreferencesRepository 接口
     */
    @Binds
    internal abstract fun bindPreferencesRepository(
        mediaRepository: LocalPreferencesRepository,
    ): PreferencesRepository


    /**
     * 绑定网络监控器实现
     * 现ConnectivityManagerNetworkMonitor 绑定现NetworkMonitor 接口
     */
    @Binds
    internal abstract fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor,
    ): NetworkMonitor



    /**
     * 绑定时区监控器实现
     * 现TimeZoneBroadcastMonitor 绑定现TimeZoneMonitor 接口
     */
    @Binds
    internal abstract fun binds(impl: TimeZoneBroadcastMonitor): TimeZoneMonitor
}

