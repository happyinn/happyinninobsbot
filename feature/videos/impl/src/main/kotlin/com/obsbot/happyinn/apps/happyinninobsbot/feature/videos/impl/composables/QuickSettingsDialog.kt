package com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.impl.composables

/**
 * 快速设置对话框组件
 * 
 * 该文件包含用于快速调整应用程序设置的对话框组件，
 * 包括媒体视图模式、布局模式、排序选项和字段显示设置等。
 */

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.component.CancelButton
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.component.DoneButton
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.component.HioDialog
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.component.HioSwitch
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.icon.HioIcons
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.UserData
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.MediaLayoutMode
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.MediaViewMode
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.Sort
import com.obsbot.happyinn.apps.happyinninobsbot.core.ui.R
import com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.impl.extensions.name


/**
 * 快速设置对话框
 * 
 * 用于调整应用程序各种设置的对话框组件。用户可以在此对话框中
 * 修改媒体视图模式、布局方式、排序规则和字段显示偏好。
 * 
 * @param userData 当前应用偏好设置，用于初始化对话框中的值
 * @param onDismiss 对话框关闭回调函数
 * @param updatePreferences 更新偏好设置的回调函数，参数为修改后的UserData
 */
@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun QuickSettingsDialog(
    userData: UserData,
    onDismiss: () -> Unit,
    updatePreferences: (UserData) -> Unit,
) {
    // 本地状态，用于存储当前编辑的偏好设置
    var preferences by remember { mutableStateOf(userData) }

    // 自定义对话框组件
    HioDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.quick_settings))
        },
        content = {
            HorizontalDivider()
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
            ) {
                // 媒体视图模式部分
                DialogSectionTitle(text = stringResource(R.string.media_view_mode))
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    // 遍历所有媒体视图模式，创建分段按钮
                    MediaViewMode.entries.forEachIndexed { index, viewMode ->
                        SegmentedButton(
                            selected = preferences.mediaViewMode == viewMode,
                            onClick = { preferences = preferences.copy(mediaViewMode = viewMode) },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = MediaViewMode.entries.size),
                            colors = SegmentedButtonDefaults.colors(
                                activeContentColor = MaterialTheme.colorScheme.primary,
                                activeBorderColor = MaterialTheme.colorScheme.primary,
                            ),
                        ) {
                            Text(text = viewMode.name())
                        }
                    }
                }
                // 媒体布局部分
                DialogSectionTitle(text = stringResource(R.string.media_layout))
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    // 遍历所有媒体布局模式，创建分段按钮
                    MediaLayoutMode.entries.forEachIndexed { index, layoutMode ->
                        SegmentedButton(
                            selected = preferences.mediaLayoutMode == layoutMode,
                            onClick = { preferences = preferences.copy(mediaLayoutMode = layoutMode) },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = MediaLayoutMode.entries.size),
                            colors = SegmentedButtonDefaults.colors(
                                activeContentColor = MaterialTheme.colorScheme.primary,
                                activeBorderColor = MaterialTheme.colorScheme.primary,
                            ),
                        ) {
                            Text(text = layoutMode.name())
                        }
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(top = 16.dp))
                // 排序部分
                DialogSectionTitle(text = stringResource(R.string.sort))
                SortOptions(
                    selectedSortBy = preferences.sortBy,
                    onOptionSelected = { preferences = preferences.copy(sortBy = it) },
                )
                Spacer(modifier = Modifier.height(8.dp))
                // 排序顺序（升序/降序）
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Sort.Order.entries.forEachIndexed { index, sortOrder ->
                        SegmentedButton(
                            selected = preferences.sortOrder == sortOrder,
                            onClick = { preferences = preferences.copy(sortOrder = sortOrder) },
                            shape = SegmentedButtonDefaults.itemShape(index = index, count = Sort.Order.entries.size),
                            colors = SegmentedButtonDefaults.colors(
                                activeContentColor = MaterialTheme.colorScheme.primary,
                                activeBorderColor = MaterialTheme.colorScheme.primary,
                            ),
                            icon = {
                                Icon(
                                    imageVector = if (sortOrder == Sort.Order.ASCENDING) HioIcons.ArrowUpward else HioIcons.ArrowDownward,
                                    contentDescription = stringResource(R.string.ascending),
                                    modifier = Modifier.size(FilterChipDefaults.IconSize),
                                )
                            },
                        ) {
                            Text(text = sortOrder.name(sortBy = preferences.sortBy))
                        }
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(top = 16.dp))
                // 字段显示设置部分
                DialogSectionTitle(text = stringResource(R.string.fields))
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(align = Alignment.Top),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    // 时长字段
                    FieldChip(
                        label = stringResource(id = R.string.duration),
                        selected = preferences.showDurationField,
                        onClick = { preferences = preferences.copy(showDurationField = !preferences.showDurationField) },
                    )
                    // 扩展名字段
                    FieldChip(
                        label = stringResource(id = R.string.extension),
                        selected = preferences.showExtensionField,
                        onClick = { preferences = preferences.copy(showExtensionField = !preferences.showExtensionField) },
                    )
                    // 路径字段
                    FieldChip(
                        label = stringResource(id = R.string.path),
                        selected = preferences.showPathField,
                        onClick = { preferences = preferences.copy(showPathField = !preferences.showPathField) },
                    )
                    // 播放进度字段
                    FieldChip(
                        label = stringResource(id = R.string.played_progress),
                        selected = preferences.showPlayedProgress,
                        onClick = { preferences = preferences.copy(showPlayedProgress = !preferences.showPlayedProgress) },
                    )
                    // 分辨率字段
                    FieldChip(
                        label = stringResource(id = R.string.resolution),
                        selected = preferences.showResolutionField,
                        onClick = { preferences = preferences.copy(showResolutionField = !preferences.showResolutionField) },
                    )
                    // 大小字段
                    FieldChip(
                        label = stringResource(id = R.string.size),
                        selected = preferences.showSizeField,
                        onClick = { preferences = preferences.copy(showSizeField = !preferences.showSizeField) },
                    )
                    // 缩略图字段
                    FieldChip(
                        label = stringResource(id = R.string.thumbnail),
                        selected = preferences.showThumbnailField,
                        onClick = { preferences = preferences.copy(showThumbnailField = !preferences.showThumbnailField) },
                    )
                }
            }
        },
        confirmButton = {
            DoneButton(
                onClick = {
                    updatePreferences(preferences)
                    onDismiss()
                },
            )
        },
        dismissButton = {
            CancelButton(onClick = onDismiss)
        },
    )
}

/**
 * 字段选择芯片组件
 * 
 * 用于快速设置对话框中的字段选择，每个芯片代表一个可显示的字段。
 * 点击芯片可以切换该字段的显示/隐藏状态。
 * 
 * @param label 芯片显示的标签文字
 * @param selected 是否选中状态
 * @param onClick 点击回调函数
 * @param modifier 组件修饰符
 * @param selectedIcon 选中状态显示的图标
 * @param unselectedIcon 未选中状态显示的图标
 */
@Composable
fun FieldChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selectedIcon: ImageVector = HioIcons.CheckBox,
    unselectedIcon: ImageVector = HioIcons.CheckBoxOutline,
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text = label) },
        leadingIcon = {
            Icon(
                imageVector = if (selected) selectedIcon else unselectedIcon,
                contentDescription = "",
                modifier = Modifier.size(FilterChipDefaults.IconSize),
                tint = MaterialTheme.colorScheme.secondary,
            )
        },
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = selected,
            selectedBorderWidth = 1.dp,
            selectedBorderColor = MaterialTheme.colorScheme.primary,
        ),
        modifier = modifier,
    )
}

/**
 * 排序选项组件
 * 
 * 显示一组排序选项按钮，用户可以选择不同的排序方式。
 * 
 * @param selectedSortBy 当前选中的排序方式
 * @param onOptionSelected 排序选项选择回调
 * @param modifier 组件修饰符
 */
@Composable
private fun SortOptions(
    selectedSortBy: Sort.By,
    onOptionSelected: (Sort.By) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
    ) {
        // 标题排序
        TextIconToggleButton(
            text = stringResource(id = R.string.title),
            icon = HioIcons.Title,
            isSelected = selectedSortBy == Sort.By.TITLE,
            onClick = { onOptionSelected(Sort.By.TITLE) },
        )
        // 时长排序
        TextIconToggleButton(
            text = stringResource(id = R.string.duration),
            icon = HioIcons.Length,
            isSelected = selectedSortBy == Sort.By.LENGTH,
            onClick = { onOptionSelected(Sort.By.LENGTH) },
        )
        // 日期排序
        TextIconToggleButton(
            text = stringResource(id = R.string.date),
            icon = HioIcons.Calendar,
            isSelected = selectedSortBy == Sort.By.DATE,
            onClick = { onOptionSelected(Sort.By.DATE) },
        )
        // 大小排序
        TextIconToggleButton(
            text = stringResource(id = R.string.size),
            icon = HioIcons.Size,
            isSelected = selectedSortBy == Sort.By.SIZE,
            onClick = { onOptionSelected(Sort.By.SIZE) },
        )
        // 路径排序
        TextIconToggleButton(
            text = stringResource(id = R.string.location),
            icon = HioIcons.Location,
            isSelected = selectedSortBy == Sort.By.PATH,
            onClick = { onOptionSelected(Sort.By.PATH) },
        )
    }
}

/**
 * 对话框分区标题组件
 * 
 * 用于在设置对话框中显示各部分的标题。
 * 
 * @param text 标题文字
 */
@Composable
private fun DialogSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
    )
}

/**
 * 对话框偏好设置开关组件
 * 
 * 用于在对话框中显示带开关的偏好设置项。
 * 
 * @param text 设置项显示文字
 * @param isChecked 当前开关状态
 * @param onClick 开关点击回调
 * @param modifier 组件修饰符
 * @param enabled 是否启用
 */
@Composable
fun DialogPreferenceSwitch(
    text: String,
    isChecked: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .toggleable(
                value = isChecked,
                enabled = enabled,
                onValueChange = { onClick() },
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = text,
            maxLines = 1,
            style = MaterialTheme.typography.titleMedium,
        )
        HioSwitch(
            checked = isChecked,
            onCheckedChange = null,
            modifier = Modifier.padding(start = 20.dp),
            enabled = enabled,
        )
    }
}
/*@Preview
@Composable
fun QuickSettingsPreview() {
    Surface {
        QuickSettingsDialog(userData = UserData(), onDismiss = { }, updatePreferences = {})
    }
}*/

