package com.obsbot.happyinn.apps.happyinninobsbot.core.data.di
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.OfflineFirstUserDataRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.OfflineFirstVideosRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.UserDataRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.VideosRepository
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
 * 提供数据仓库和工具类的依赖绑定
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {

    /**
     * 绑定主题仓库实现
     * 将 OfflineFirstTopicsRepository 绑定到 TopicsRepository 接口
     */
/*
    @Binds
    internal abstract fun bindsTopicRepository(
        topicsRepository: OfflineFirstTopicsRepository,
    ): TopicsRepository
*/

    /**
     * 绑定新闻资源仓库实现
     * 将 OfflineFirstVideosRepository 绑定到 VideosRepository 接口
     */
    @Binds
    internal abstract fun bindsVideosResourceRepository(
        VideosRepository: OfflineFirstVideosRepository,
    ): VideosRepository

    /**
     * 绑定用户数据仓库实现
     * 将 OfflineFirstUserDataRepository 绑定到 UserDataRepository 接口
     */
    @Binds
    internal abstract fun bindsUserDataRepository(
        userDataRepository: OfflineFirstUserDataRepository,
    ): UserDataRepository

    /**
     * 绑定最近搜索仓库实现
     * 将 DefaultRecentSearchRepository 绑定到 RecentSearchRepository 接口
     */
/*    @Binds
    internal abstract fun bindsRecentSearchRepository(
        recentSearchRepository: DefaultRecentSearchRepository,
    ): RecentSearchRepository*/

    /**
     * 绑定搜索内容仓库实现
     * 将 DefaultSearchContentsRepository 绑定到 SearchContentsRepository 接口
     */
/*    @Binds
    internal abstract fun bindsSearchContentsRepository(
        searchContentsRepository: DefaultSearchContentsRepository,
    ): SearchContentsRepository*/

    /**
     * 绑定网络监控器实现
     * 将 ConnectivityManagerNetworkMonitor 绑定到 NetworkMonitor 接口
     */
    @Binds
    internal abstract fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor,
    ): NetworkMonitor

    /**
     * 绑定时区监控器实现
     * 将 TimeZoneBroadcastMonitor 绑定到 TimeZoneMonitor 接口
     */
    @Binds
    internal abstract fun binds(impl: TimeZoneBroadcastMonitor): TimeZoneMonitor
}
