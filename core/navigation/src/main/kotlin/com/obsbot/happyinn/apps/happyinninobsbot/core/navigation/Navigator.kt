
package com.obsbot.happyinn.apps.happyinninobsbot.core.navigation

import androidx.navigation3.runtime.NavKey

//TODO 待深入细看
/**
 * 导航器类，通过更新导航状态来处理导航事件（前进和后退）
 *
 * @param state 导航状态，将在响应导航事件时被更新
 */
class Navigator(val state: NavigationState) {

    /**
     * 导航到指定的导航键
     *
     * @param key 要导航到的导航键
     */
    fun navigate(key: NavKey) {
        // 根据导航键的类型执行不同的导航逻辑
        when (key) {
            // 如果是当前顶层键，则清空子栈
            state.currentTopLevelKey -> clearSubStack()
            // 如果是顶层键列表中的键，则导航到该顶层键
            in state.topLevelKeys -> goToTopLevel(key)
            // 其他情况，导航到非顶层键
            else -> goToKey(key)
        }
    }

    /**
     * 返回到上一个导航键
     */
    fun goBack() {
        // 根据当前键的位置执行不同的回退逻辑
        when (state.currentKey) {
            // 如果是起始键，则不允许回退
            state.startKey -> error("You cannot go back from the start route")
            // 如果是当前顶层键，则移除顶层栈的最后一个元素
            state.currentTopLevelKey -> {
                // We're at the base of the current sub stack, go back to the previous top level
                // stack.
                state.topLevelStack.removeLastOrNull()
            }
            // 其他情况，移除当前子栈的最后一个元素
            else -> state.currentSubStack.removeLastOrNull()
        }
    }

    /**
     * 导航到非顶层键
     *
     * @param key 要导航到的非顶层导航键
     */
    private fun goToKey(key: NavKey) {
        // 处理导航到非顶层键的逻辑
        state.currentSubStack.apply {
            // 如果键已存在于栈中，则先移除再添加到末尾，确保栈的顺序正确
            remove(key)
            add(key)
        }
    }

    /**
     * 导航到顶层栈
     *
     * @param key 要导航到的顶层导航键
     */
    private fun goToTopLevel(key: NavKey) {
        // 处理导航到顶层键的逻辑
        state.topLevelStack.apply {
            if (key == state.startKey) {
                // This is the start key. Clear the stack so it's added as the only key.
                // 如果是起始键，则清空栈，使其成为唯一的键
                clear()
            } else {
                // Remove it if it's already in the stack so it's added at the end.
                // 如果键已存在于栈中，则先移除再添加到末尾
                remove(key)
            }
            // 将键添加到栈末尾
            add(key)
        }
    }

    /**
     * 清空当前子栈中除了根键之外的所有键
     */
    private fun clearSubStack() {
        // 清空当前子栈中除了根键之外的所有键
        state.currentSubStack.run {
            // 如果子栈大小大于1，则清空从索引1开始到末尾的所有元素
            if (size > 1) subList(1, size).clear()
        }
    }
}
