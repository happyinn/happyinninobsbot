/*
 * 版权所有 2022 The Android Open Source Project
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

package com.obsbot.happyinn.apps.happyinninobsbot.core.data.di

import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.OfflineFirstUserDataRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.UserDataRepository
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
     * 将 OfflineFirstNewsRepository 绑定到 NewsRepository 接口
     */
/*    @Binds
    internal abstract fun bindsNewsResourceRepository(
        newsRepository: OfflineFirstNewsRepository,
    ): NewsRepository*/

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
/*
    @Binds
    internal abstract fun bindsNetworkMonitor(
        networkMonitor: ConnectivityManagerNetworkMonitor,
    ): NetworkMonitor
*/

    /**
     * 绑定时区监控器实现
     * 将 TimeZoneBroadcastMonitor 绑定到 TimeZoneMonitor 接口
     */
   /* @Binds
    internal abstract fun binds(impl: TimeZoneBroadcastMonitor): TimeZoneMonitor
*/
}
