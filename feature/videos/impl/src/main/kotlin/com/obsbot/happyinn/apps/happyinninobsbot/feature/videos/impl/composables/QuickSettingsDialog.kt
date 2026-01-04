/**
 * 快速设置对话框相关组件
 */
package com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.impl.composables

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
 * 快速设置对话框，用于调整应用程序的各种设置
 * @param userData 当前应用偏好设置
 * @param onDismiss 对话框关闭回调
 * @param updatePreferences 更新偏好设置的回调
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
                DialogSectionTitle(text = stringResource(R.string.sort))
                SortOptions(
                    selectedSortBy = preferences.sortBy,
                    onOptionSelected = { preferences = preferences.copy(sortBy = it) },
                )
                Spacer(modifier = Modifier.height(8.dp))
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
                DialogSectionTitle(text = stringResource(R.string.fields))
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(align = Alignment.Top),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    FieldChip(
                        label = stringResource(id = R.string.duration),
                        selected = preferences.showDurationField,
                        onClick = { preferences = preferences.copy(showDurationField = !preferences.showDurationField) },
                    )
                    FieldChip(
                        label = stringResource(id = R.string.extension),
                        selected = preferences.showExtensionField,
                        onClick = { preferences = preferences.copy(showExtensionField = !preferences.showExtensionField) },
                    )
                    FieldChip(
                        label = stringResource(id = R.string.path),
                        selected = preferences.showPathField,
                        onClick = { preferences = preferences.copy(showPathField = !preferences.showPathField) },
                    )
                    FieldChip(
                        label = stringResource(id = R.string.played_progress),
                        selected = preferences.showPlayedProgress,
                        onClick = { preferences = preferences.copy(showPlayedProgress = !preferences.showPlayedProgress) },
                    )
                    FieldChip(
                        label = stringResource(id = R.string.resolution),
                        selected = preferences.showResolutionField,
                        onClick = { preferences = preferences.copy(showResolutionField = !preferences.showResolutionField) },
                    )
                    FieldChip(
                        label = stringResource(id = R.string.size),
                        selected = preferences.showSizeField,
                        onClick = { preferences = preferences.copy(showSizeField = !preferences.showSizeField) },
                    )
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
        TextIconToggleButton(
            text = stringResource(id = R.string.title),
            icon = HioIcons.Title,
            isSelected = selectedSortBy == Sort.By.TITLE,
            onClick = { onOptionSelected(Sort.By.TITLE) },
        )
        TextIconToggleButton(
            text = stringResource(id = R.string.duration),
            icon = HioIcons.Length,
            isSelected = selectedSortBy == Sort.By.LENGTH,
            onClick = { onOptionSelected(Sort.By.LENGTH) },
        )
        TextIconToggleButton(
            text = stringResource(id = R.string.date),
            icon = HioIcons.Calendar,
            isSelected = selectedSortBy == Sort.By.DATE,
            onClick = { onOptionSelected(Sort.By.DATE) },
        )
        TextIconToggleButton(
            text = stringResource(id = R.string.size),
            icon = HioIcons.Size,
            isSelected = selectedSortBy == Sort.By.SIZE,
            onClick = { onOptionSelected(Sort.By.SIZE) },
        )
        TextIconToggleButton(
            text = stringResource(id = R.string.location),
            icon = HioIcons.Location,
            isSelected = selectedSortBy == Sort.By.PATH,
            onClick = { onOptionSelected(Sort.By.PATH) },
        )
    }
}

@Composable
private fun DialogSectionTitle(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
    )
}

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
