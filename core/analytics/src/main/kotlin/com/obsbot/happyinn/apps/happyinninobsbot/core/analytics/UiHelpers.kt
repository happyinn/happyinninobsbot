package com.obsbot.happyinn.apps.happyinninobsbot.core.analytics

import androidx.compose.runtime.staticCompositionLocalOf

/**
 * 全局键，用于通过 CompositionLocal 获取 AnalyticsHelper 实例
 *
 * 这个 CompositionLocal 提供了一种在 Compose 层级结构中传递 AnalyticsHelper 的方式，
 * 允许应用中的任何组件访问分析功能而无需显式传递依赖项
 */
val LocalAnalyticsHelper = staticCompositionLocalOf<AnalyticsHelper> {
    // 提供一个默认的 AnalyticsHelper 实现，该实现不执行任何操作
    // 这样做是为了让测试和预览不需要提供真正的分析助手实现
    // 在实际的应用构建中，应该提供一个不同的、功能完整的实现
    NoOpAnalyticsHelper()
}
