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
 * 提供数据仓库和工具类的依赖绑�?
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    /**
     * 绑定主题仓库实现
     * �?OfflineFirstTopicsRepository 绑定�?TopicsRepository 接口
     */
    @Binds
    internal abstract fun bindsTopicRepository(
        topicsRepository: OfflineFirstTopicsRepository,
    ): TopicsRepository

    /**
     * 绑定新闻资源仓库实现
     * �?OfflineFirstNewsRepository 绑定�?NewsRepository 接口
     */
    @Binds
    internal abstract fun bindsNewsResourceRepository(
        newsRepository: OfflineFirstNewsRepository,
    ): NewsRepository

    /**
     * 绑定用户数据仓库实现
     * �?OfflineFirstUserDataRepository 绑定�?UserDataRepository 接口
     */
    @Binds
    internal abstract fun bindsUserDataRepository(
        userDataRepository: OfflineFirstUserDataRepository,
    ): UserDataRepository

    /**
     * 绑定最近搜索仓库实�?
     * �?DefaultRecentSearchRepository 绑定�?RecentSearchRepository 接口
     */
    @Binds
    internal abstract fun bindsRecentSearchRepository(
        recentSearchRepository: DefaultRecentSearchRepository,
    ): RecentSearchRepository

    /**
     * 绑定搜索内容仓库实现
     * �?DefaultSearchContentsRepository 绑定�?SearchContentsRepository 接口
     */
    @Binds
    internal abstract fun bindsSearchContentsRepository(
        searchContentsRepository: DefaultSearchContentsRepository,
    ): SearchContentsRepository

    /**
     * 绑定网络监控器实�?
     * �?ConnectivityManagerNetworkMonitor 绑定�?NetworkMonitor 接口
     */
    @Binds
    internal abstract fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor,
    ): NetworkMonitor

    /**
     * 绑定时区监控器实�?
     * �?TimeZoneBroadcastMonitor 绑定�?TimeZoneMonitor 接口
     */
    @Binds
    internal abstract fun binds(impl: TimeZoneBroadcastMonitor): TimeZoneMonitor
}

