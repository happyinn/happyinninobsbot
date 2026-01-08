package com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.impl.composables

/**
 * 媒体内容相关组件
 * 
 * 该文件包含用于媒体列表显示的通用组件，包括懒加载列表、进度指示器、
 * 空状态视图、删除确认对话框和底部操作面板等。
 */

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.component.CancelButton
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.component.DoneButton
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.component.HioDialog
import com.obsbot.happyinn.apps.happyinninobsbot.core.ui.video.ListItemComponent
import com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.impl.R
import com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.impl.screens.media.CIRCULAR_PROGRESS_INDICATOR_TEST_TAG

/**
 * 媒体懒加载列表组件
 * 
 * 使用LazyColumn实现的懒加载列表，用于高效显示大量媒体内容。
 * 支持自定义垂直和水平排列方式。
 * 
 * @param modifier 组件修饰符
 * @param verticalArrangement 垂直排列方式，默认顶部对齐
 * @param horizontalAlignment 水平对齐方式，默认左侧对齐
 * @param content 列表内容，通过LazyListScope DSL定义
 */
@Composable
fun MediaLazyList(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: LazyListScope.() -> Unit,
) {
    LazyColumn(
        contentPadding = PaddingValues(vertical = 10.dp),
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = verticalArrangement,
        content = content,
    )
}

/**
 * 中心圆形进度条组件
 * 
 * 在屏幕中心显示的加载进度指示器，用于表示数据正在加载中。
 */
@Composable
fun CenterCircularProgressBar() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.testTag(CIRCULAR_PROGRESS_INDICATOR_TEST_TAG),
        )
    }
}

/**
 * 未找到视频视图组件
 * 
 * 当媒体列表为空时显示的提示视图，提示用户没有找到视频内容。
 */
@Composable
fun NoVideosFound() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                horizontal = 20.dp,
                vertical = 40.dp,
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = stringResource(id = R.string.no_videos_found),
            style = MaterialTheme.typography.titleLarge,
        )
    }
}

/**
 * 删除确认对话框组件
 * 
 * 用于在执行删除操作前向用户确认，显示要删除的文件列表。
 * 
 * @param subText 删除操作的说明文字
 * @param onConfirm 确认删除的回调函数
 * @param onCancel 取消删除的回调函数
 * @param fileNames 要删除的文件名列表
 * @param modifier 组件修饰符
 */
@Composable
fun DeleteConfirmationDialog(
    subText: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    fileNames: List<String>,
    modifier: Modifier = Modifier,
) {
    HioDialog(
        onDismissRequest = onCancel,
        title = { Text(text = stringResource(R.string.delete), modifier = Modifier.fillMaxWidth()) },
        confirmButton = { DoneButton(onClick = onConfirm) },
        dismissButton = { CancelButton(onClick = onCancel) },
        modifier = modifier,
        content = {
            Text(
                text = subText,
                style = MaterialTheme.typography.titleSmall,
            )
            Spacer(modifier = Modifier.height(20.dp))
            LazyColumn {
                items(fileNames) {
                    Text(
                        text = it,
                        overflow = TextOverflow.Ellipsis,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            }
        },
    )
}

@Preview
@Composable
fun DeleteDialogPreview() {
    DeleteConfirmationDialog(
        subText = "The following files will be deleted permanently",
        onConfirm = { /*TODO*/ },
        onCancel = { /*TODO*/ },
        fileNames = listOf("Harry potter 1", "Harry potter 2", "Harry potter 3", "Harry potter 4"),
    )
}

/**
 * 选项底部面板组件
 * 
 * 从屏幕底部弹出的操作面板，用于显示一组操作选项。
 * 支持部分展开状态跳过。
 * 
 * @param title 面板标题
 * @param onDismiss 面板关闭回调
 * @param sheetState 面板状态，用于控制展开/折叠
 * @param content 面板内容，通过ColumnScope DSL定义
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OptionsBottomSheet(
    title: String,
    onDismiss: () -> Unit,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    content: @Composable ColumnScope.() -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(modifier = Modifier.padding(bottom = 8.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth(),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(modifier = Modifier.height(20.dp))
            content()
        }
    }
}

/**
 * 底部面板项组件
 * 
 * 底部操作面板中的单个选项项，包含图标和文字，点击可触发相应操作。
 * 
 * @param text 选项显示文字
 * @param icon 选项图标
 * @param onClick 点击回调函数
 * @param modifier 组件修饰符
 */
@Composable
fun BottomSheetItem(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItemComponent(
        colors = ListItemDefaults.colors(
            containerColor = Color.Transparent,
        ),
        leadingContent = { Icon(imageVector = icon, contentDescription = null, tint = MaterialTheme.colorScheme.secondary) },
        headlineContent = { Text(text = text) },
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
    )
}