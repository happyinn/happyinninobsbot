package com.obsbot.happyinn.apps.happyinninobsbot.sync.status

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkInfo
import androidx.work.WorkInfo.State
import androidx.work.WorkManager
import com.obsbot.happyinn.apps.happyinninobsbot.sync.initializers.SYNC_WORK_NAME
import com.obsbot.happyinn.apps.happyinninobsbot.sync.workers.SyncWorker
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.util.SyncManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * 基于 [WorkManager] 的 [WorkInfo] 实现的 [SyncManager]
 * 用于管理应用的同步状态
 */
internal class WorkManagerSyncManager @Inject constructor(
    @ApplicationContext private val context: Context,
) : SyncManager {
    /**
     * 同步状态流
     * 监听工作管理器中指定唯一工作名称的工作信息变化
     * 当有任何工作处于运行状态时返回true，否则返回false
     */
    override val isSyncing: Flow<Boolean> =
        WorkManager.getInstance(context).getWorkInfosForUniqueWorkFlow(SYNC_WORK_NAME)
            .map(List<WorkInfo>::anyRunning)
            .conflate()

    /**
     * 请求同步操作
     * 通过工作管理器启动同步工作
     */
    override fun requestSync() {
        val workManager = WorkManager.getInstance(context)
        // 在应用启动时运行同步，并确保任何时候只有一个同步工作器在运行
        workManager.enqueueUniqueWork(
            SYNC_WORK_NAME,
            ExistingWorkPolicy.KEEP,
            SyncWorker.startUpSyncWork(),
        )
    }
}

/**
 * 检查工作信息列表中是否有任何工作正在运行
 * @receiver List<WorkInfo> 工作信息列表
 * @return Boolean 如果有任何工作处于运行状态则返回true，否则返回false
 */
private fun List<WorkInfo>.anyRunning() = any { it.state == State.RUNNING }
