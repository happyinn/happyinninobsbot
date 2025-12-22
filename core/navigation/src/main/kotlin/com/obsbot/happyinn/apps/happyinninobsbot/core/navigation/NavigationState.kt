package com.obsbot.happyinn.apps.happyinninobsbot.core.navigation
import androidx.annotation.VisibleForTesting
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberDecoratedNavEntries
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator

//TODO 待深入细看
/**
 * 创建一个能够持久化配置更改和进程死亡的导航状态
 *
 * @param startKey 启动导航键，应用将通过此键退出
 * @param topLevelKeys 顶层导航键集合
 * @return NavigationState 导航状态对象
 */
@Composable
fun rememberNavigationState(
    startKey: NavKey,
    topLevelKeys: Set<NavKey>,
): NavigationState {
    // 记住顶层导航栈，用于管理顶级页面的回退栈
    val topLevelStack = rememberNavBackStack(startKey)
    // 为每个顶层键创建对应的子导航栈
    val subStacks = topLevelKeys.associateWith { key -> rememberNavBackStack(key) }

    // 记住并返回导航状态实例
    return remember(startKey, topLevelKeys) {
        NavigationState(
            startKey = startKey,
            topLevelStack = topLevelStack,
            subStacks = subStacks,
        )
    }
}

/**
 * 导航状态持有者
 *
 * @param startKey 启动导航键，用户将通过此键退出应用
 * @param topLevelStack 顶层回退栈，仅包含顶层键
 * @param subStacks 每个顶层键对应的回退栈映射
 */
class NavigationState(
    val startKey: NavKey,
    val topLevelStack: NavBackStack<NavKey>,
    val subStacks: Map<NavKey, NavBackStack<NavKey>>,
) {
    // 当前顶层键，通过派生状态计算得出
    val currentTopLevelKey: NavKey by derivedStateOf { topLevelStack.last() }

    // 获取所有顶层键的集合
    val topLevelKeys
        get() = subStacks.keys

    // 仅供测试使用：获取当前子栈
    @get:VisibleForTesting
    val currentSubStack: NavBackStack<NavKey>
        get() = subStacks[currentTopLevelKey]
            ?: error("Sub stack for $currentTopLevelKey does not exist")

    // 仅供测试使用：获取当前导航键
    @get:VisibleForTesting
    val currentKey: NavKey by derivedStateOf { currentSubStack.last() }
}

/**
 * 将 NavigationState 转换为 NavEntries
 *
 * @param entryProvider 导航条目提供器函数，用于根据导航键创建导航条目
 * @return SnapshotStateList<NavEntry<NavKey>> 快照状态列表，包含装饰后的导航条目
 */
@Composable
fun NavigationState.toEntries(
    entryProvider: (NavKey) -> NavEntry<NavKey>,
): SnapshotStateList<NavEntry<NavKey>> {
    // 为每个子栈创建装饰后的导航条目
    val decoratedEntries = subStacks.mapValues { (_, stack) ->
        // 定义装饰器列表，包括可保存状态持有者装饰器和 ViewModel 存储装饰器
        val decorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator<NavKey>(),
            rememberViewModelStoreNavEntryDecorator<NavKey>(),
        )
        // 记住并创建装饰后的导航条目
        rememberDecoratedNavEntries(
            backStack = stack,
            entryDecorators = decorators,
            entryProvider = entryProvider,
        )
    }

    // 将顶层栈和装饰后的条目合并为可变状态列表返回
    return topLevelStack
        .flatMap { decoratedEntries[it] ?: emptyList() }
        .toMutableStateList()
}
