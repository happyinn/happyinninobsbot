package com.obsbot.happyinn.apps.happyinninobsbot.sync.status

import android.util.Log
import javax.inject.Inject

private const val TAG = "StubSyncSubscriber"

/**
 * [SyncSubscriber] 的存根实现
 * 用于提供同步订阅功能的基本实现
 */
class StubSyncSubscriber @Inject constructor() : SyncSubscriber {
    /**
     * 实现同步订阅逻辑
     * 目前只是简单记录日志，实际项目中可能需要实现具体的订阅逻辑
     */
    override suspend fun subscribe() {
        Log.d(TAG, "Subscribing to sync")
    }
}
