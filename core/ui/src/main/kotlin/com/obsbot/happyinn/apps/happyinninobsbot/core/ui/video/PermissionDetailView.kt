package com.obsbot.happyinn.apps.happyinninobsbot.core.ui.video

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.obsbot.happyinn.apps.happyinninobsbot.core.ui.R

/**
 * 权限详情视图组件
 * 
 * 当用户拒绝了权限请求且不再显示权限说明时，显示此视图
 * 引导用户前往系统设置页面手动开启权限
 * 
 * @param text 权限说明文本，告知用户需要开启的权限
 */
@Composable
fun PermissionDetailView(
    text: String,
) {
    // 获取当前上下文，用于启动系统设置页面
    val context = LocalContext.current
    // 垂直布局容器，内容居中显示
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally, // 水平居中
        verticalArrangement = Arrangement.Center, // 垂直居中
    ) {
        // 标题文本：提示用户权限未授予
        Text(
            text = stringResource(id = R.string.permission_not_granted),
            style = MaterialTheme.typography.titleLarge, // 使用大标题样式
            fontWeight = FontWeight.SemiBold, // 半粗体
            textAlign = TextAlign.Center, // 文本居中
            modifier = Modifier.padding(horizontal = 5.dp), // 水平内边距
        )
        Spacer(modifier = Modifier.height(10.dp)) // 间距
        // 说明文本：显示具体的权限说明
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge, // 使用大正文样式
            textAlign = TextAlign.Center, // 文本居中
            modifier = Modifier.padding(horizontal = 5.dp), // 水平内边距
        )
        Spacer(modifier = Modifier.height(10.dp)) // 间距
        // 打开设置按钮：点击后跳转到应用详情设置页面
        Button(
            onClick = {
                // 创建跳转到应用详情设置页面的 Intent
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    // 设置目标应用的包名
                    data = Uri.parse("package:" + context.packageName)
                    // 启动设置页面
                    context.startActivity(this)
                }
            },
        ) {
            Text(text = stringResource(R.string.open_settings))
        }
    }
}
