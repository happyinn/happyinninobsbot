/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.WindowAdaptiveInfo
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteItemColors
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffoldDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.icon.HioIcons
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.theme.HioTheme
import kotlin.collections.forEachIndexed
import kotlin.run

/**
 * Happyinn in Obsbot 底部导航栏项目，包含图标和标签内容插槽。
 * 对 Material 3 的 [NavigationBarItem] 进行封装。
 *
 * @param selected 此项目是否被选中
 * @param onClick 当此项目被选中时调用的回调函数
 * @param icon 项目图标内容
 * @param modifier 应用于此项的修饰符
 * @param selectedIcon 选中时的项目图标内容
 * @param enabled 控制此项目的启用状态。当为 `false` 时，此项目将不可点击，
 *               并且对无障碍服务显示为已禁用
 * @param label 项目文本标签内容
 * @param alwaysShowLabel 是否始终显示此项目的标签。如果为 false，则标签仅在
 *                        此项目被选中时显示
 */
@Composable
fun RowScope.HioNavigationBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    alwaysShowLabel: Boolean = true,
    icon: @Composable () -> Unit,
    selectedIcon: @Composable () -> Unit = icon,
    label: @Composable (() -> Unit)? = null,
) {
    NavigationBarItem(
        selected = selected,
        onClick = onClick,
        icon = if (selected) selectedIcon else icon,
        modifier = modifier,
        enabled = enabled,
        label = label,
        alwaysShowLabel = alwaysShowLabel,
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = HioNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = HioNavigationDefaults.navigationContentColor(),
            selectedTextColor = HioNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = HioNavigationDefaults.navigationContentColor(),
            indicatorColor = HioNavigationDefaults.navigationIndicatorColor(),
        ),
    )
}

/**
 * Happyinn in Obsbot 底部导航栏，包含内容插槽。
 * 对 Material 3 的 [NavigationBar] 进行封装。
 *
 * @param modifier 应用于导航栏的修饰符
 * @param content 导航栏内的目标页面。应包含多个 [NavigationBarItem]
 */
@Composable
fun HioNavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit,
) {
    NavigationBar(
        modifier = modifier,
        contentColor = HioNavigationDefaults.navigationContentColor(),
        tonalElevation = 0.dp,
        content = content,
    )
}

/**
 * Happyinn in Obsbot 侧边导航栏项目，包含图标和标签内容插槽。
 * 对 Material 3 的 [NavigationRailItem] 进行封装。
 *
 * @param selected 此项目是否被选中
 * @param onClick 当此项目被选中时调用的回调函数
 * @param icon 项目图标内容
 * @param modifier 应用于此项的修饰符
 * @param selectedIcon 选中时的项目图标内容
 * @param enabled 控制此项目的启用状态。当为 `false` 时，此项目将不可点击，
 *               并且对无障碍服务显示为已禁用
 * @param label 项目文本标签内容
 * @param alwaysShowLabel 是否始终显示此项目的标签。如果为 false，则标签仅在
 *                        此项目被选中时显示
 */
@Composable
fun HioNavigationRailItem(
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    alwaysShowLabel: Boolean = true,
    icon: @Composable () -> Unit,
    selectedIcon: @Composable () -> Unit = icon,
    label: @Composable (() -> Unit)? = null,
) {
    NavigationRailItem(
        selected = selected,
        onClick = onClick,
        icon = if (selected) selectedIcon else icon,
        modifier = modifier,
        enabled = enabled,
        label = label,
        alwaysShowLabel = alwaysShowLabel,
        colors = NavigationRailItemDefaults.colors(
            selectedIconColor = HioNavigationDefaults.navigationSelectedItemColor(),
            unselectedIconColor = HioNavigationDefaults.navigationContentColor(),
            selectedTextColor = HioNavigationDefaults.navigationSelectedItemColor(),
            unselectedTextColor = HioNavigationDefaults.navigationContentColor(),
            indicatorColor = HioNavigationDefaults.navigationIndicatorColor(),
        ),
    )
}

/**
 * Happyinn in Obsbot 侧边导航栏，包含头部和内容插槽。
 * 对 Material 3 的 [NavigationRail] 进行封装。
 *
 * @param modifier 应用于导航栏的修饰符
 * @param header 可选的头部，可能包含浮动操作按钮或 Logo
 * @param content 导航栏内的目标页面。应包含多个 [NavigationRailItem]
 */
@Composable
fun HioNavigationRail(
    modifier: Modifier = Modifier,
    header: @Composable (ColumnScope.() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    NavigationRail(
        modifier = modifier,
        containerColor = Color.Transparent,
        contentColor = HioNavigationDefaults.navigationContentColor(),
        header = header,
        content = content,
    )
}

/**
 * Happyinn in Obsbot 导航套件脚手架，包含项目和内容插槽。
 * 对 Material 3 的 [NavigationSuiteScaffold] 进行封装。
 *
 * @param modifier 应用于导航套件脚手架的修饰符
 * @param navigationSuiteItems 通过 [HioNavigationSuiteScope] 显示多个项目的插槽
 * @param windowAdaptiveInfo 窗口自适应信息
 * @param content 脚手架内的应用内容
 */
@Composable
fun HioNavigationSuiteScaffold(
    navigationSuiteItems: HioNavigationSuiteScope.() -> Unit, //类型为HioNavigationSuiteScope的扩展函数，无返回值的高阶函数
    modifier: Modifier = Modifier,
    windowAdaptiveInfo: WindowAdaptiveInfo = currentWindowAdaptiveInfo(),
    content: @Composable () -> Unit,
) {
    // 根据窗口自适应信息计算应该采用的布局类型
    // 例如在手机上使用底部导航，在平板上使用侧边导航，在大屏设备上使用抽屉导航
    val layoutType = NavigationSuiteScaffoldDefaults
        .calculateFromAdaptiveInfo(windowAdaptiveInfo)

    // 定义导航套件中各种导航项目的颜色配置，确保在不同类型的导航中保持一致的视觉效果
    val navigationSuiteItemColors = NavigationSuiteItemColors(
        // 配置底部导航栏(NavigationBar)项目颜色
        navigationBarItemColors = NavigationBarItemDefaults.colors(
            // 选中状态下的图标颜色 - 使用主题中的onPrimaryContainer色
            selectedIconColor = HioNavigationDefaults.navigationSelectedItemColor(),
            // 未选中状态下的图标颜色 - 使用主题中的onSurfaceVariant色
            unselectedIconColor = HioNavigationDefaults.navigationContentColor(),
            // 选中状态下的文字颜色 - 使用主题中的onPrimaryContainer色
            selectedTextColor = HioNavigationDefaults.navigationSelectedItemColor(),
            // 未选中状态下的文字颜色 - 使用主题中的onSurfaceVariant色
            unselectedTextColor = HioNavigationDefaults.navigationContentColor(),
            // 选中项目的指示器(背景)颜色 - 使用主题中的primaryContainer色
            indicatorColor = HioNavigationDefaults.navigationIndicatorColor(),
        ),
        // 配置侧边导航栏(NavigationRail)项目颜色
        navigationRailItemColors = NavigationRailItemDefaults.colors(
            // 选中状态下的图标颜色 - 使用主题中的onPrimaryContainer色
            selectedIconColor = HioNavigationDefaults.navigationSelectedItemColor(),
            // 未选中状态下的图标颜色 - 使用主题中的onSurfaceVariant色
            unselectedIconColor = HioNavigationDefaults.navigationContentColor(),
            // 选中状态下的文字颜色 - 使用主题中的onPrimaryContainer色
            selectedTextColor = HioNavigationDefaults.navigationSelectedItemColor(),
            // 未选中状态下的文字颜色 - 使用主题中的onSurfaceVariant色
            unselectedTextColor = HioNavigationDefaults.navigationContentColor(),
            // 选中项目的指示器(背景)颜色 - 使用主题中的primaryContainer色
            indicatorColor = HioNavigationDefaults.navigationIndicatorColor(),
        ),
        // 配置抽屉导航(NavigationDrawer)项目颜色
        navigationDrawerItemColors = NavigationDrawerItemDefaults.colors(
            // 选中状态下的图标颜色 - 使用主题中的onPrimaryContainer色
            selectedIconColor = HioNavigationDefaults.navigationSelectedItemColor(),
            // 未选中状态下的图标颜色 - 使用主题中的onSurfaceVariant色
            unselectedIconColor = HioNavigationDefaults.navigationContentColor(),
            // 选中状态下的文字颜色 - 使用主题中的onPrimaryContainer色
            selectedTextColor = HioNavigationDefaults.navigationSelectedItemColor(),
            // 未选中状态下的文字颜色 - 使用主题中的onSurfaceVariant色
            unselectedTextColor = HioNavigationDefaults.navigationContentColor(),
        ),
    )

    // 使用 Material 3 提供的自适应导航套件脚手架组件  组合而非继承
    NavigationSuiteScaffold(
        // 定义导航项目，通过自定义的 HioNavigationSuiteScope 包装原始的 NavigationSuiteScope
        navigationSuiteItems = {
            HioNavigationSuiteScope(
                navigationSuiteScope = this,  // 传入原始的 NavigationSuiteScope
                navigationSuiteItemColors = navigationSuiteItemColors,  // 传入上面定义的颜色配置
            ).run(navigationSuiteItems)  // 执行用户传入的导航项配置代码块
        },
        // 设置布局类型，决定了使用哪种导航模式(底部/侧边/抽屉)
        layoutType = layoutType,
        // 设置容器背景色为透明，让内容可以透出
        containerColor = Color.Transparent,
        // 配置导航套件的整体颜色方案
        navigationSuiteColors = NavigationSuiteDefaults.colors(
            // 设置底部导航栏的内容颜色
            navigationBarContentColor = HioNavigationDefaults.navigationContentColor(),
            // 设置侧边导航栏的容器背景色为透明
            navigationRailContainerColor = Color.Transparent,
        ),
        // 应用外部传入的修饰符
        modifier = modifier,
    ) {
        // 显示主要内容区域
        content()
    }
}


/**
 * [NavigationSuiteScope] 的包装类，用于声明导航项目。
 */
class HioNavigationSuiteScope internal constructor(
    private val navigationSuiteScope: NavigationSuiteScope,
    private val navigationSuiteItemColors: NavigationSuiteItemColors,
) {
    /**
     * 添加一个导航项目
     *
     * @param selected 此项目是否被选中
     * @param onClick 当此项目被选中时调用的回调函数
     * @param modifier 应用于此项的修饰符
     * @param icon 项目图标内容
     * @param selectedIcon 选中时的项目图标内容
     * @param label 项目文本标签内容
     */
    fun item(
        selected: Boolean,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        icon: @Composable () -> Unit,
        selectedIcon: @Composable () -> Unit = icon,
        label: @Composable (() -> Unit)? = null,
    ) = navigationSuiteScope.item(
        selected = selected,
        onClick = onClick,
        icon = {
            if (selected) {
                selectedIcon()
            } else {
                icon()
            }
        },
        label = label,
        colors = navigationSuiteItemColors,
        modifier = modifier,
    )
}

/**
 * Happyinn in Obsbot 底部导航栏预览
 */
@ThemePreviews
@Composable
fun HioNavigationBarPreview() {
    // 定义导航项标签
    val items = listOf("For you", "Saved", "Interests")
    // 定义未选中状态的图标
    val icons = listOf(
        HioIcons.UpcomingBorder,
        HioIcons.BookmarksBorder,
        HioIcons.Grid3x3,
    )
    // 定义选中状态的图标
    val selectedIcons = listOf(
        HioIcons.Upcoming,
        HioIcons.Bookmarks,
        HioIcons.Grid3x3,
    )

    HioTheme {
        HioNavigationBar {
            items.forEachIndexed { index, item ->
                HioNavigationBarItem(
                    icon = {
                        Icon(
                            imageVector = icons[index],
                            contentDescription = item,
                        )
                    },
                    selectedIcon = {
                        Icon(
                            imageVector = selectedIcons[index],
                            contentDescription = item,
                        )
                    },
                    label = { Text(item) },
                    selected = index == 0,
                    onClick = { },
                )
            }
        }
    }
}

/**
 * Happyinn in Obsbot 侧边导航栏预览
 */
@ThemePreviews
@Composable
fun HioNavigationRailPreview() {
    // 定义导航项标签
    val items = listOf("For you", "Saved", "Interests")
    // 定义未选中状态的图标
    val icons = listOf(
        HioIcons.UpcomingBorder,
        HioIcons.BookmarksBorder,
        HioIcons.Grid3x3,
    )
    // 定义选中状态的图标
    val selectedIcons = listOf(
        HioIcons.Upcoming,
        HioIcons.Bookmarks,
        HioIcons.Grid3x3,
    )

    HioTheme {
        HioNavigationRail {
            items.forEachIndexed { index, item ->
                HioNavigationRailItem(
                    icon = {
                        Icon(
                            imageVector = icons[index],
                            contentDescription = item,
                        )
                    },
                    selectedIcon = {
                        Icon(
                            imageVector = selectedIcons[index],
                            contentDescription = item,
                        )
                    },
                    label = { Text(item) },
                    selected = index == 0,
                    onClick = { },
                )
            }
        }
    }
}

/**
 * Happyinn in Obsbot 导航默认值配置。
 */
object HioNavigationDefaults {
    /**
     * 导航内容颜色
     */
    @Composable
    fun navigationContentColor() = MaterialTheme.colorScheme.onSurfaceVariant

    /**
     * 导航选中项目颜色
     */
    @Composable
    fun navigationSelectedItemColor() = MaterialTheme.colorScheme.onPrimaryContainer

    /**
     * 导航指示器颜色
     */
    @Composable
    fun navigationIndicatorColor() = MaterialTheme.colorScheme.primaryContainer
}
