package com.obsbot.happyinn.apps.happyinninobsbot.core.analytics
/**
 * 用于记录分析事件的接口
 *
 * 该接口定义了应用中记录用户行为和事件的标准方法。
 *
 * 实现类参考：
 * - [FirebaseAnalyticsHelper](file://E:\pros\nowinandroid-main\core\analytics\src\prod\kotlin\com\google\samples\apps\nowinandroid\core\analytics\FirebaseAnalyticsHelper.kt#L25-L40): 基于 Firebase Analytics 的实现
 * - [StubAnalyticsHelper](file://E:\pros\nowinandroid-main\core\analytics\src\main\kotlin\com\google\samples\apps\nowinandroid\core\analytics\StubAnalyticsHelper.kt#L28-L33): 空实现，用于测试和预览环境
 */
interface AnalyticsHelper {
    /**
     * 记录一个分析事件
     *
     * @param event 要记录的分析事件对象，包含事件名称和相关参数
     */
    fun logEvent(event: AnalyticsEvent)
}

