package com.obsbot.happyinn.apps.happyinninobsbot.core.ui.video

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/**
 * 列表项组件
 * 
 * 一个通用的列表项组件，支持自定义头部内容、支持内容、前置内容、后置内容
 * 使用 Material3 设计规范，提供统一的列表项样式
 * 
 * @param headlineContent 头部主要内容，必填
 * @param modifier 修饰器，用于自定义组件样式
 * @param supportingContent 支持内容，可选，显示在头部内容下方
 * @param leadingContent 前置内容，可选，显示在左侧
 * @param trailingContent 后置内容，可选，显示在右侧
 * @param colors 列表项颜色配置，默认使用 Material3 默认颜色
 */
@Composable
fun ListItemComponent(
    headlineContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    supportingContent: @Composable (() -> Unit)? = null,
    leadingContent: @Composable (() -> Unit)? = null,
    trailingContent: @Composable (() -> Unit)? = null,
    colors: ListItemColors = ListItemDefaults.colors(),
) {
    // 主容器：水平布局的行
    Row(
        modifier = modifier
            .fillMaxWidth() // 填充最大宽度
            .background(color = colors.containerColor) // 设置背景颜色
            .padding(horizontal = 16.dp, vertical = 8.dp) // 设置内边距
            .semantics(mergeDescendants = true) {}, // 合并语义信息，用于无障碍支持
        horizontalArrangement = Arrangement.spacedBy(16.dp), // 水平方向元素间距
        verticalAlignment = Alignment.CenterVertically, // 垂直居中对齐
    ) {
        // 前置内容（左侧）
        leadingContent?.invoke()
        // 中间内容区域（可伸缩）
        Column(
            modifier = Modifier.weight(1f), // 占据剩余空间
        ) {
            // 头部内容：使用主题的 bodyLarge 样式和头部颜色
            CompositionLocalProvider(
                LocalContentColor provides colors.headlineColor,
                LocalTextStyle provides MaterialTheme.typography.bodyLarge,
            ) {
                headlineContent.invoke()
            }
            // 支持内容：使用主题的 bodyMedium 样式和支持文本颜色
            CompositionLocalProvider(
                LocalContentColor provides colors.supportingTextColor,
                LocalTextStyle provides MaterialTheme.typography.bodyMedium,
            ) {
                supportingContent?.invoke()
            }
        }
        // 后置内容（右侧）
        trailingContent?.invoke()
    }
}
