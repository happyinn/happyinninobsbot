@file:OptIn(ExperimentalMaterial3Api::class)

package com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.component

import android.R
import androidx.annotation.StringRes
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.icon.HioIcons
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.theme.HioTheme

/**
 * 自定义顶部应用栏组件
 * 基于Material 3的CenterAlignedTopAppBar实现，居中标题样式
 * 
 * @param titleRes 标题文本资源ID
 * @param navigationIcon 左侧导航图标
 * @param navigationIconContentDescription 导航图标的无障碍描述
 * @param actionIcon 右侧操作图标
 * @param actionIconContentDescription 操作图标的无障碍描述
 * @param modifier Modifier对象，用于自定义组件样式和行为
 * @param colors 顶部应用栏的颜色配置
 * @param onNavigationClick 导航图标点击回调
 * @param onActionClick 操作图标点击回调
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HioTopAppBar(
    @StringRes titleRes: Int, // 标题文本的资源ID
    navigationIcon: ImageVector, // 左侧导航图标
    navigationIconContentDescription: String, // 导航图标的无障碍描述文本
    actionIcon: ImageVector, // 右侧操作图标
    actionIconContentDescription: String, // 操作图标的无障碍描述文本
    modifier: Modifier = Modifier, // 默认的Modifier
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(), // 默认颜色配置
    onNavigationClick: () -> Unit = {}, // 导航图标点击事件，默认为空实现
    onActionClick: () -> Unit = {}, // 操作图标点击事件，默认为空实现
) {
    // 使用Material 3的CenterAlignedTopAppBar作为基础组件
    CenterAlignedTopAppBar(
        // 标题部分，使用Text组件显示资源ID对应的字符串
        title = { Text(text = stringResource(id = titleRes)) },
        
        // 导航图标部分，使用IconButton包装的Icon组件
        navigationIcon = {
            IconButton(onClick = onNavigationClick) { // 点击事件绑定到onNavigationClick
                Icon(
                    imageVector = navigationIcon,
                    contentDescription = navigationIconContentDescription,
                    tint = MaterialTheme.colorScheme.onSurface, // 使用主题中的表面文字颜色
                )
            }
        },
        
        // 操作图标部分，右侧可以放置多个图标
        actions = {
            IconButton(onClick = onActionClick) { // 点击事件绑定到onActionClick
                Icon(
                    imageVector = actionIcon,
                    contentDescription = actionIconContentDescription,
                    tint = MaterialTheme.colorScheme.onSurface, // 使用主题中的表面文字颜色
                )
            }
        },
        
        // 应用颜色配置
        colors = colors,
        
        // 应用Modifier并添加测试标签，便于UI测试识别
        modifier = modifier.testTag("hioTopAppBar"),
    )
}

/**
 * 顶部应用栏的预览函数
 * 使用Preview注解标记，在Android Studio中可以直接预览组件效果
 */
@OptIn(ExperimentalMaterial3Api::class)
@Preview("Top App Bar") // 预览名称
@Composable
private fun HioTopAppBarPreview() {
    // 应用自定义主题进行预览
    HioTheme {
        // 创建预览实例，使用默认的资源和图标
        HioTopAppBar(
            titleRes = R.string.untitled,
            navigationIcon = HioIcons.Search, // 使用搜索图标作为导航图标
            navigationIconContentDescription = "Navigation icon",
            actionIcon = HioIcons.MoreVert, // 使用更多选项图标作为操作图标
            actionIconContentDescription = "Action icon",
        )
    }
}

