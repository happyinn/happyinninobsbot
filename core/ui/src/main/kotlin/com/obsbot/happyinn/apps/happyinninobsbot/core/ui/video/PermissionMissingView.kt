package com.obsbot.happyinn.apps.happyinninobsbot.core.ui.video

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.obsbot.happyinn.apps.happyinninobsbot.core.ui.R

/**
 * 权限缺失视图组件
 * 
 * 用于根据权限状态显示不同的UI内容：
 * 1. 当权限已授予时，显示正常内容
 * 2. 当需要显示权限说明时，显示权限说明对话框
 * 3. 当需要引导用户去设置中开启权限时，显示权限详情视图
 * 
 * @param isGranted 权限是否已授予
 * @param showRationale 是否需要显示权限说明
 * @param permission 权限名称，用于显示在提示信息中
 * @param launchPermissionRequest 启动权限请求的回调函数
 * @param content 权限授予时显示的内容
 */
@Composable
fun PermissionMissingView(
    isGranted: Boolean,
    showRationale: Boolean,
    permission: String,
    launchPermissionRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    // 如果权限已授予，直接显示内容
    if (isGranted) {
        content()
    } 
    // 如果需要显示权限说明，显示权限说明对话框
    else if (showRationale) {
        PermissionRationaleDialog(
            text = stringResource(
                id = R.string.permission_info,
                permission,
            ),
            onConfirmButtonClick = launchPermissionRequest,
        )
    } 
    // 否则显示权限详情视图，引导用户去设置中开启权限
    else {
        PermissionDetailView(
            text = stringResource(
                id = R.string.permission_settings,
                permission,
            ),
        )
    }
}
