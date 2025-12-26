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
import androidx.compose.material3.SnackbarDuration.Indefinite
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
import com.obsbot.happyinn.apps.happyinninobsbot.R
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.component.HioBackground
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.component.HioGradientBackground
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.component.HioNavigationSuiteScaffold
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.component.HioTopAppBar
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.icon.HioIcons
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.theme.GradientColors
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.theme.LocalGradientColors
import com.obsbot.happyinn.apps.happyinninobsbot.core.navigation.Navigator
import com.obsbot.happyinn.apps.happyinninobsbot.core.navigation.toEntries
import com.obsbot.happyinn.apps.happyinninobsbot.feature.bookmarks.impl.navigation.bookmarksEntry
import com.obsbot.happyinn.apps.happyinninobsbot.feature.foryou.api.navigation.ForYouNavKey
import com.obsbot.happyinn.apps.happyinninobsbot.feature.foryou.impl.navigation.forYouEntry
import com.obsbot.happyinn.apps.happyinninobsbot.feature.interests.impl.navigation.interestsEntry
import com.obsbot.happyinn.apps.happyinninobsbot.feature.search.api.navigation.SearchNavKey
import com.obsbot.happyinn.apps.happyinninobsbot.feature.settings.impl.SettingsDialog
import com.obsbot.happyinn.apps.happyinninobsbot.navigation.TOP_LEVEL_NAV_ITEMS
import com.obsbot.happyinn.apps.happyinninobsbot.feature.settings.impl.R as settingsR
import kotlin.text.get

/**
 * 外部 HioApp：负责全局设置、状态管理和环境配置 内部 HioAppContent：专注于实际 UI 组件的构建和导航逻辑
 * 外部函数负责：
 *
 * - 背景设置（普通背景和渐变背景）
 * - 初始化全局状态（ snackbarHostState 、 showSettingsDialog ）
 * - 处理网络连接状态监听和提示
 * 内部函数则负责：
 *
 * - 接收并使用这些状态
 * - 构建 UI 界面
 * - 处理用户交互事件
 *
 *
 * - 应用初始化 ：调用外部 HioApp 函数
 * - 环境设置 ：
 * - 设置应用背景
 * - 根据当前页面决定是否显示渐变背景
 * - 初始化 snackbarHostState
 * - 状态监听 ：
 * - 监听网络状态变化并显示提示信息
 * - 环境提供 ：
 * - 使用 CompositionLocalProvider 提供全局状态
 * - UI 构建 ：
 * - 调用内部 HioApp 函数构建具体 UI
 * - 事件处理 ：
 * - 通过回调函数处理对话框显示/隐藏等交互
 * */

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
    // 判断是否应该显示渐变背景（仅在"为你推荐"页面显示）
    val shouldShowGradientBackground = appState.navigationState.currentTopLevelKey == ForYouNavKey
    // 设置对话框显示状态
    var showSettingsDialog by rememberSaveable { mutableStateOf(false) }

    HioBackground(modifier = modifier) {
        HioGradientBackground(
            gradientColors = if (shouldShowGradientBackground) {
                LocalGradientColors.current
            } else {
                GradientColors()
            },
        ) {
            // 记住 SnackbarHostState 实例
            val snackbarHostState = remember { SnackbarHostState() }

            // 收集网络离线状态
            val isOffline by appState.isOffline.collectAsStateWithLifecycle()

            // 如果用户未连接到互联网，显示提示信息
            val notConnectedMessage = stringResource(R.string.not_connected)
            LaunchedEffect(isOffline) {
                if (isOffline) {
                    snackbarHostState.showSnackbar(
                        message = notConnectedMessage,
                        duration = Indefinite,
                    )
                }
            }
            // 提供本地 SnackbarHostState 组合值
            CompositionLocalProvider(LocalSnackbarHostState provides snackbarHostState) {
                // 调用内部 HioApp 函数
                HioAppContent(
                    appState = appState,

                    // TODO: 设置应该是一个对话框屏幕
                    showSettingsDialog = showSettingsDialog,
                    onSettingsDismissed = { showSettingsDialog = false },
                    onTopAppBarActionClick = { showSettingsDialog = true },
                    windowAdaptiveInfo = windowAdaptiveInfo,
                )
            }
        }
    }
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
internal fun HioAppContent(
    appState: HioAppState,
    showSettingsDialog: Boolean,
    onSettingsDismissed: () -> Unit,
    onTopAppBarActionClick: () -> Unit,
    modifier: Modifier = Modifier,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
) {
    // 收集包含未读资源的顶级导航键
    val unreadNavKeys by appState.topLevelNavKeysWithUnreadResources
        .collectAsStateWithLifecycle()

    // 如果需要显示设置对话框，则显示它
    if (showSettingsDialog) {
        SettingsDialog(
            onDismiss = { onSettingsDismissed() },
        )
    }

    // 获取当前 SnackbarHostState
    val snackbarHostState = LocalSnackbarHostState.current

    // 创建导航器实例
    val navigator = remember { Navigator(appState.navigationState) }

    HioNavigationSuiteScaffold(
        navigationSuiteItems = {
            // 遍历所有顶级导航项
            TOP_LEVEL_NAV_ITEMS.forEach { (navKey, navItem) ->
                // 判断是否有未读内容
                val hasUnread = unreadNavKeys.contains(navKey)
                // 判断是否为当前选中项
                val selected = navKey == appState.navigationState.currentTopLevelKey
                // 添加导航项
                item(
                    selected = selected,
                    onClick = { navigator.navigate(navKey) },
                    icon = {
                        Icon(
                            imageVector = navItem.unselectedIcon,
                            contentDescription = null,
                        )
                    },
                    selectedIcon = {
                        Icon(
                            imageVector = navItem.selectedIcon,
                            contentDescription = null,
                        )
                    },
                    label = { Text(stringResource(navItem.iconTextId)) },
                    modifier = Modifier
                        .testTag("HioNavItem")
                        .then(if (hasUnread) Modifier.notificationDot() else Modifier),
                )
            }
        },
        windowAdaptiveInfo = windowAdaptiveInfo,
    ){
        Scaffold(
            modifier = modifier.semantics {
                testTagsAsResourceId = true
            },
            containerColor = Color.Transparent,
            contentColor = MaterialTheme.colorScheme.onBackground,
            contentWindowInsets = WindowInsets(0, 0, 0, 0),
            snackbarHost = {
                // Snackbar 显示区域
                SnackbarHost(
                    snackbarHostState,
                    modifier = Modifier.windowInsetsPadding(
                        WindowInsets.safeDrawing.exclude(
                            WindowInsets.ime,
                        ),
                    ),
                )
            },
        ){
                padding ->
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .consumeWindowInsets(padding)
                    .windowInsetsPadding(
                        WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Horizontal,
                        )
                    )
            ) {
                // 仅在顶级目标页面显示顶部应用栏
                var shouldShowTopAppBar = false
                // 判断当前页面是否为顶级页面
                if (appState.navigationState.currentKey in appState.navigationState.topLevelKeys) {
                    shouldShowTopAppBar = true

                    // 获取当前目标页面的信息
                    val destination = TOP_LEVEL_NAV_ITEMS[appState.navigationState.currentTopLevelKey]
                        ?: error("Top level nav item not found for ${appState.navigationState.currentTopLevelKey}")

                    // 顶部应用栏组件
                    HioTopAppBar(
                        titleRes = destination.titleTextId,
                        navigationIcon = HioIcons.Search,
                        navigationIconContentDescription = stringResource(
                            id = settingsR.string.feature_settings_impl_top_app_bar_navigation_icon_description,
                        ),
                        actionIcon = HioIcons.Settings,
                        actionIconContentDescription = stringResource(
                            id = settingsR.string.feature_settings_impl_top_app_bar_action_icon_description,
                        ),
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = Color.Transparent,
                        ),
                        onActionClick = { onTopAppBarActionClick() },
                        onNavigationClick = { navigator.navigate(SearchNavKey) },
                    )
                }

                Box(
                    // 解决 https://issuetracker.google.com/338478720 问题的 workaround
                    modifier = Modifier.consumeWindowInsets(
                        if (shouldShowTopAppBar) {
                            WindowInsets.safeDrawing.only(WindowInsetsSides.Top)
                        } else {
                            WindowInsets(0, 0, 0, 0)
                        },
                    ),
                ){
                    // 列表详情场景策略
                    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()

                    // 导航条目提供器
                    val entryProvider = entryProvider {
                        forYouEntry(navigator)
                        bookmarksEntry(navigator)
                        interestsEntry(navigator)
                        /*topicEntry(navigator)
                        searchEntry(navigator)*/
                    }

                    // 导航显示组件
                    NavDisplay(
                        entries = appState.navigationState.toEntries(entryProvider),
                        sceneStrategy = listDetailStrategy,
                        onBack = { navigator.goBack() },
                    )
                }
            }
        }

    }
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


// TODO: Why is this here?

/**
 * 本地 SnackbarHostState 组合值
 * 用于在 Compose 组件树中传递 SnackbarHostState 实例
 */
val LocalSnackbarHostState = compositionLocalOf<SnackbarHostState> {
    error("SnackbarHostState state should be initialized at runtime")
}
