package com.obsbot.happyinn.apps.happyinninobsbot.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTagsAsResourceId
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay


/**
 * Now in Android 应用的主 Composable 函数
 * 公共入口
 * 负责设置整体的应用 UI 环境和背景
 * 处理全局状态，如网络连接状态监控
 * 初始化并提供 SnackbarHostState 给内部组件使用
 * 包含背景组件 HioBackground 和 HioGradientBackground
 * 负责构建整个应用的 UI 结构，包括背景、导航、顶部应用栏等
 *
 * @param appState 应用状态对象，包含导航状态、网络状态等信息
 * @param modifier 修饰符
 * @param windowAdaptiveInfo 窗口自适应信息，用于适配不同屏幕尺寸
 */
@Composable
fun HioApp(
    appState: HioAppState,
    modifier: Modifier = Modifier,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
) {
    Text(
        text = "Hello OBSBOT!",
        modifier = modifier
    )
}

/**
 * 内部的 HioApp Composable 函数，实际构建应用 UI
 *
 * 负责构建实际的应用 UI 结构
 * 处理导航逻辑和导航组件显示
 * 管理设置对话框的显示状态
 * 构建 Scaffold、导航栏、顶部应用栏等具体 UI 组件
 * 集成导航系统和内容显示区域
 *
 * @param appState 应用状态对象
 * @param showSettingsDialog 是否显示设置对话框
 * @param onSettingsDismissed 设置对话框关闭回调
 * @param onTopAppBarActionClick 顶部应用栏操作按钮点击回调
 * @param modifier 修饰符
 * @param windowAdaptiveInfo 窗口自适应信息
 */
@Composable
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalComposeUiApi::class,
    ExperimentalMaterial3AdaptiveApi::class,
)
internal fun HioApp(
    appState: HioAppState,
    showSettingsDialog: Boolean,
    onSettingsDismissed: () -> Unit,
    onTopAppBarActionClick: () -> Unit,
    modifier: Modifier = Modifier,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
) {

}

/**
 * 为导航项添加通知红点的修饰符扩展函数
 */
private fun Modifier.notificationDot(): Modifier =
    composed {
        val tertiaryColor = MaterialTheme.colorScheme.tertiary
        drawWithContent {
            drawContent()
            // 绘制红色通知圆点
            drawCircle(
                tertiaryColor,
                radius = 5.dp.toPx(),
                // 基于 NavigationBar 的"指示器药丸"尺寸计算位置；
                // 然而，其参数是私有的，所以我们必须隐式依赖它们
                // (NavigationBarTokens.ActiveIndicatorWidth = 64.dp)
                center = center + Offset(
                    64.dp.toPx() * .45f,
                    32.dp.toPx() * -.45f - 6.dp.toPx(),
                ),
            )
        }
    }
