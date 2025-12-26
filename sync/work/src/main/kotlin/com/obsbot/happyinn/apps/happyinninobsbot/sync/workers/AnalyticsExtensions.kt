package com.obsbot.happyinn.apps.happyinninobsbot.sync.workers

import com.obsbot.happyinn.apps.happyinninobsbot.core.analytics.AnalyticsEvent
import com.obsbot.happyinn.apps.happyinninobsbot.core.analytics.AnalyticsHelper


/**
 * 记录同步开始事件的日志扩展函数
 * @receiver AnalyticsHelper 分析帮助器实例
 */
internal fun AnalyticsHelper.logSyncStarted() =
    logEvent(
        AnalyticsEvent(type = "network_sync_started"),
    )

/**
 * 记录同步完成事件的日志扩展函数
 * 根据同步是否成功记录不同的事件类型
 * @receiver AnalyticsHelper 分析帮助器实例
 * @param syncedSuccessfully 同步是否成功的标志
 */
internal fun AnalyticsHelper.logSyncFinished(syncedSuccessfully: Boolean) {
    val eventType = if (syncedSuccessfully) "network_sync_successful" else "network_sync_failed"
    logEvent(
        AnalyticsEvent(type = eventType),
    )
}
