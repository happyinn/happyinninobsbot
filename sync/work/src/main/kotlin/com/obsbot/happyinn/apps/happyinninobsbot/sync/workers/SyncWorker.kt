package com.obsbot.happyinn.apps.happyinninobsbot.sync.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.tracing.traceAsync
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import com.obsbot.happyinn.apps.happyinninobsbot.core.analytics.AnalyticsHelper
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.Synchronizer
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.NewsRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.SearchContentsRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.TopicsRepository
import com.obsbot.happyinn.apps.happyinninobsbot.core.datastore.ChangeListVersions
import com.obsbot.happyinn.apps.happyinninobsbot.core.datastore.HioPreferencesDataSource
import com.obsbot.happyinn.apps.happyinninobsbot.network.Dispatcher
import com.obsbot.happyinn.apps.happyinninobsbot.network.HioDispatchers.IO
import com.obsbot.happyinn.apps.happyinninobsbot.sync.initializers.SyncConstraints
import com.obsbot.happyinn.apps.happyinninobsbot.sync.initializers.syncForegroundInfo
import com.obsbot.happyinn.apps.happyinninobsbot.sync.status.SyncSubscriber
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

/**
 * 应用数据同步工作器类
 * 负责将应用的各个数据仓库与远程服务器进行同步
 * 通过委派给具有同步功能的相应仓库实例来同步整个数据层
 */
@HiltWorker
internal class SyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,           // 应用上下文，由WorkerFactory提供
    @Assisted workerParams: WorkerParameters,            // Worker参数，包含输入数据和配置信息
    private val hioPreferences: HioPreferencesDataSource, // HappyInn应用首选项数据源，用于存储同步状态
    private val topicRepository: TopicsRepository,        // 话题仓库，负责话题数据的同步
    private val newsRepository: NewsRepository,           // 新闻仓库，负责新闻数据的同步
    private val searchContentsRepository: SearchContentsRepository, // 搜索内容仓库，用于搜索功能的数据准备
    @Dispatcher(IO) private val ioDispatcher: CoroutineDispatcher,  // IO调度器，用于执行网络和数据库操作
    private val analyticsHelper: AnalyticsHelper,         // 分析助手，用于记录同步事件
    private val syncSubscriber: SyncSubscriber,           // 同步状态订阅者，用于通知同步状态变化
) : CoroutineWorker(appContext, workerParams), Synchronizer { // 继承自CoroutineWorker并实现Synchronizer接口

    /**
     * 获取前台服务信息
     * 前台服务用于在同步过程中保持应用可见，防止被系统杀死
     * @return ForegroundInfo 包含通知信息的前台服务配置
     */
    override suspend fun getForegroundInfo(): ForegroundInfo =
        appContext.syncForegroundInfo() // 调用扩展函数获取预定义的前台服务信息

    /**
     * 执行同步工作的核心方法
     * 这是Worker类的主要执行方法，由WorkManager调用
     * @return Result 工作执行结果，表示成功、失败或需要重试
     */
    override suspend fun doWork(): Result = withContext(ioDispatcher) { // 使用IO调度器执行工作
        traceAsync("Sync", 0) { // 开始异步性能追踪，标记为"Sync"
            analyticsHelper.logSyncStarted() // 记录同步开始事件到分析系统

            syncSubscriber.subscribe() // 订阅同步状态变化，允许其他组件监听同步过程
            //TODO debug发现不会执行这个函数
            // 首先并行同步各个仓库，提高同步效率
            val syncedSuccessfully = awaitAll(
                async { topicRepository.sync() }, // 并行执行话题仓库同步
                async { newsRepository.sync() },  // 并行执行新闻仓库同步
            ).all { it } // 检查所有同步操作是否都成功完成

            analyticsHelper.logSyncFinished(syncedSuccessfully) // 记录同步完成事件及结果

            // 根据同步结果返回相应的工作状态
            if (syncedSuccessfully) {
                searchContentsRepository.populateFtsData() // 如果同步成功，更新全文搜索数据
                Result.success() // 返回成功结果
            } else {
                Result.retry() // 返回重试结果，由WorkManager决定何时重试
            }
        }
    }

    /**
     * 获取变更列表版本信息
     * 变更列表版本用于增量同步，只获取上次同步后的变更数据
     * @return ChangeListVersions 包含各数据类型的最新变更列表版本数据
     */
    override suspend fun getChangeListVersions(): ChangeListVersions =
        hioPreferences.getChangeListVersions() // 从应用首选项获取变更列表版本

    /**
     * 更新变更列表版本信息
     * 同步完成后更新本地记录的变更列表版本
     * @param update 更新函数，用于转换当前版本数据为新版本数据
     */
    override suspend fun updateChangeListVersions(
        update: ChangeListVersions.() -> ChangeListVersions, // 函数式参数，提供版本更新逻辑
    ) = hioPreferences.updateChangeListVersion(update) // 通过应用首选项更新版本信息

    companion object {
        /**
         * 创建应用启动时快速执行的一次性同步工作请求
         * 用于应用首次启动或冷启动时执行必要的数据同步
         * @return OneTimeWorkRequestBuilder 构建好的一次性工作请求，可直接提交给WorkManager
         */
        fun startUpSyncWork() = OneTimeWorkRequestBuilder<DelegatingWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST) // 设置为加急任务，如果配额不足则降级为普通任务
            .setConstraints(SyncConstraints) // 应用预定义的同步约束条件（如网络状态、充电状态等）
            .setInputData(SyncWorker::class.delegatedData()) // 设置输入数据，指定委派给SyncWorker执行
            .build() // 构建工作请求实例
    }
}