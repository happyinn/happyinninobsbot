package com.obsbot.happyinn.apps.happyinninobsbot.sync

import com.obsbot.happyinn.apps.happyinninobsbot.sync.status.StubSyncSubscriber
import com.obsbot.happyinn.apps.happyinninobsbot.sync.status.SyncSubscriber
import com.obsbot.happyinn.apps.happyinninobsbot.sync.status.WorkManagerSyncManager
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.util.SyncManager
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * 同步相关的依赖注入模块
 * 提供同步管理器和同步订阅器的绑定配置
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class SyncModule {
    /**
     * 绑定同步状态监视器
     * 将 WorkManagerSyncManager 实现绑定到 SyncManager 接口
     */
    @Binds
    internal abstract fun bindsSyncStatusMonitor(
        syncStatusMonitor: WorkManagerSyncManager,
    ): SyncManager

    /**
     * 绑定同步订阅器
     * 将 StubSyncSubscriber 实现绑定到 SyncSubscriber 接口
     */
    @Binds
    internal abstract fun bindsSyncSubscriber(
        syncSubscriber: StubSyncSubscriber,
    ): SyncSubscriber
}