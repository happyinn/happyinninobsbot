package com.obsbot.happyinn.apps.happyinninobsbot.sync.initializers

import android.content.Context
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import com.obsbot.happyinn.apps.happyinninobsbot.sync.workers.SyncWorker

object Sync {
    // 此方法初始化同步功能，该过程保持应用程序数据的最新状态。
    // 它从应用模块的 Application.onCreate() 调用，并且应该只执行一次。
    fun initialize(context: Context) {
        WorkManager.getInstance(context).apply {
            // 在应用启动时运行同步，并确保任何时候只有一个同步工作器在运行
            enqueueUniqueWork(
                SYNC_WORK_NAME,
                ExistingWorkPolicy.KEEP,
                SyncWorker.startUpSyncWork(),
            )
        }
    }
}

// 此名称不应更改，否则应用程序可能会同时运行多个同步请求
internal const val SYNC_WORK_NAME = "SyncWorkName"
