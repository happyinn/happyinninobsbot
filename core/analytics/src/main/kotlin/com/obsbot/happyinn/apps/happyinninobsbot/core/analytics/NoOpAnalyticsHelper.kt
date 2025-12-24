package com.obsbot.happyinn.apps.happyinninobsbot.core.analytics

/**
 * AnalyticsHelper 的空操作实现
 *
 * 这是一个不执行任何实际分析操作的实现类，主要用于：
 * - 测试环境：避免在单元测试中执行实际的分析事件
 * - 预览模式：在 Compose 预览中避免发送分析数据
 * - 开发调试：在开发过程中可以选择性地禁用分析功能
 */
class NoOpAnalyticsHelper : AnalyticsHelper {
    /**
     * 记录分析事件的空操作实现
     *
     * 此方法不执行任何实际操作，只是简单地返回 Unit，
     * 从而避免在不需要实际记录分析数据的场景中产生副作用
     *
     * @param event 要记录的分析事件，但在此实现中不会被实际处理
     */
    override fun logEvent(event: AnalyticsEvent) = Unit
}
