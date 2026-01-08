package com.obsbot.happyinn.apps.happyinninobsbot.core.ui.video

import android.Manifest
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.theme.HioPlayerTheme
import com.obsbot.happyinn.apps.happyinninobsbot.core.ui.R
import com.obsbot.happyinn.apps.happyinninobsbot.core.ui.preview.DayNightPreview

/**
 * 权限请求说明对话框组件
 * 
 * 用于向用户展示权限请求的说明信息，并提供一个确认按钮来触发权限请求
 * 
 * @param text 权限说明文本内容
 * @param modifier 修饰器，用于自定义对话框样式
 * @param onConfirmButtonClick 确认按钮点击时的回调函数，用于触发权限请求
 */
@Composable
fun PermissionRationaleDialog(
    text: String,
    modifier: Modifier = Modifier,
    onConfirmButtonClick: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = {}, // 不允许通过点击外部区域关闭对话框
        modifier = modifier,
        title = {
            // 对话框标题：显示"权限请求"
            Text(
                text = stringResource(R.string.permission_request),
            )
        },
        text = {
            // 对话框内容：显示权限说明文本
            Text(text = text)
        },
        confirmButton = {
            // 确认按钮：点击后触发权限请求
            Button(onClick = onConfirmButtonClick) {
                Text(stringResource(R.string.grant_permission))
            }
        },
    )
}

/**
 * 权限请求说明对话框的预览组件
 * 用于在 Android Studio 的预览窗口中查看对话框样式
 */
@DayNightPreview
@Composable
fun PermissionRationaleDialogPreview() {
    HioPlayerTheme {
        Surface {
            // 预览示例：使用读取外部存储权限作为示例
            PermissionRationaleDialog(
                text = stringResource(
                    id = R.string.permission_info,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                ),
                onConfirmButtonClick = {},
            )
        }
    }
}
