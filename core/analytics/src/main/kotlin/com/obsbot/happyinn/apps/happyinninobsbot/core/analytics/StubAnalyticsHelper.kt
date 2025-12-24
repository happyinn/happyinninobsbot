package com.obsbot.happyinn.apps.happyinninobsbot.core.analytics

import android.util.Log
import javax.inject.Inject
import javax.inject.Singleton

// 定义日志标签常量，用于标识日志来源
private const val TAG = "StubAnalyticsHelper"

/**
 * AnalyticsHelper 的桩实现类
 *
 * 此实现类将分析事件写入到日志中，而不是发送到后端分析系统。
 * 主要用于以下场景：
 * - 调试构建：在开发过程中验证分析事件的生成
 * - 测试环境：避免在测试中发送实际的分析数据
 * - 不需要向后端发送分析事件的构建版本
 */
@Singleton
internal class StubAnalyticsHelper @Inject constructor() : AnalyticsHelper {
    /**
     * 记录分析事件到日志
     *
     * 将传入的分析事件以调试日志的形式输出到 Logcat，
     * 不会将事件发送到任何分析后端服务
     *
     * @param event 要记录的分析事件对象
     */
    override fun logEvent(event: AnalyticsEvent) {
        Log.d(TAG, "Received analytics event: $event")
    }
}
