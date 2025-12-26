package com.obsbot.happyinn.apps.happyinninobsbot.sync.workers

import android.content.Context
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlin.reflect.KClass

/**
 * Hilt入口点，用于在运行时检索 [HiltWorkerFactory]
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface HiltWorkerFactoryEntryPoint {
    fun hiltWorkerFactory(): HiltWorkerFactory
}

private const val WORKER_CLASS_NAME = "RouterWorkerDelegateClassName"

/**
 * 为 WorkRequest 添加元数据，用于标识 [DelegatingWorker] 应该委托给哪个 [CoroutineWorker]
 * @receiver KClass<out CoroutineWorker> CoroutineWorker的Kotlin类
 * @return Data 包含工作类名称的数据对象
 */
internal fun KClass<out CoroutineWorker>.delegatedData() =
    Data.Builder()
        .putString(WORKER_CLASS_NAME, qualifiedName)
        .build()

/**
 * 一个委托工作器，将同步任务委托给另一个通过 [HiltWorkerFactory] 构造的 [CoroutineWorker]。
 *
 * 这允许创建和使用具有扩展参数的 [CoroutineWorker] 实例，
 * 而无需提供应用程序模块需要使用的自定义 WorkManager 配置。
 *
 * 换句话说，它允许在库模块中使用自定义工作器，而无需拥有 WorkManager 单例的配置。
 */
class DelegatingWorker(
    appContext: Context,
    workerParams: WorkerParameters,
) : CoroutineWorker(appContext, workerParams) {

    // 从输入数据中获取委托工作器的类名
    private val workerClassName =
        workerParams.inputData.getString(WORKER_CLASS_NAME) ?: ""

    // 通过Hilt入口点获取工作器工厂并创建委托工作器实例
    private val delegateWorker =
        EntryPointAccessors.fromApplication<HiltWorkerFactoryEntryPoint>(appContext)
            .hiltWorkerFactory()
            .createWorker(appContext, workerClassName, workerParams)
            as? CoroutineWorker
            ?: throw IllegalArgumentException("Unable to find appropriate worker")

    /**
     * 获取前台信息服务信息
     * 将调用委托给实际的工作器
     */
    override suspend fun getForegroundInfo(): ForegroundInfo =
        delegateWorker.getForegroundInfo()

    /**
     * 执行工作的核心方法
     * 将实际工作委托给目标工作器执行
     */
    override suspend fun doWork(): Result =
        delegateWorker.doWork()
}
