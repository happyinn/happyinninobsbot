package com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.R
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.icon.HioIcons
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.theme.HioTheme

/**
 * Happyinn in Obsbot 填充按钮，具有通用内容插槽。封装了 Material 3 的 [Button] 组件。
 *
 * @param onClick 用户点击按钮时调用的回调函数
 * @param modifier 应用于按钮的修饰符
 * @param enabled 控制按钮的启用状态。当为 `false` 时，此按钮不可点击，并且对无障碍服务显示为禁用状态
 * @param contentPadding 应用于容器和内容之间的内部间距值
 * @param content 按钮的内容
 */
@Composable
fun HioButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit,
) {
    // 创建一个 Material 3 Button 组件实例
    Button(
        onClick = onClick,           // 设置点击事件处理器
        modifier = modifier,         // 应用传入的修饰符
        enabled = enabled,           // 设置启用状态
        colors = ButtonDefaults.buttonColors(
            // 设置按钮颜色为主题中的 onBackground 颜色
            containerColor = MaterialTheme.colorScheme.onBackground,
        ),
        contentPadding = contentPadding,  // 设置内容内边距
        content = content,           // 设置按钮内容
    )
}

/**
 * Happyinn in Obsbot 填充按钮，具有文本和图标内容插槽。
 *
 * @param onClick 用户点击按钮时调用的回调函数
 * @param modifier 应用于按钮的修饰符
 * @param enabled 控制按钮的启用状态。当为 `false` 时，此按钮不可点击，并且对无障碍服务显示为禁用状态
 * @param text 按钮文本标签内容
 * @param leadingIcon 按钮前导图标内容。传入 `null` 表示不显示前导图标
 */
@Composable
fun HioButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: @Composable () -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    // 调用上面定义的通用 HioButton 函数
    HioButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        // 根据是否有前导图标决定使用哪种内容内边距
        contentPadding = if (leadingIcon != null) {
            ButtonDefaults.ButtonWithIconContentPadding  // 有图标时使用带图标的内边距
        } else {
            ButtonDefaults.ContentPadding               // 无图标时使用默认内边距
        },
    ) {
        // 使用专门的内容布局函数来排列文本和图标
        HioButtonContent(
            text = text,
            leadingIcon = leadingIcon,
        )
    }
}

/**
 * Happyinn in Obsbot 描边按钮，具有通用内容插槽。封装了 Material 3 的 [OutlinedButton] 组件。
 *
 * @param onClick 用户点击按钮时调用的回调函数
 * @param modifier 应用于按钮的修饰符
 * @param enabled 控制按钮的启用状态。当为 `false` 时，此按钮不可点击，并且对无障碍服务显示为禁用状态
 * @param contentPadding 应用于容器和内容之间的内部间距值
 * @param content 按钮的内容
 */
@Composable
fun HioOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    content: @Composable RowScope.() -> Unit,
) {
    // 创建一个 Material 3 OutlinedButton 组件实例
    OutlinedButton(
        onClick = onClick,           // 设置点击事件处理器
        modifier = modifier,         // 应用传入的修饰符
        enabled = enabled,           // 设置启用状态
        colors = ButtonDefaults.outlinedButtonColors(
            // 设置按钮内容颜色为主题中的 onBackground 颜色
            contentColor = MaterialTheme.colorScheme.onBackground,
        ),
        // 设置按钮边框样式
        border = BorderStroke(
            width = HioButtonDefaults.OutlinedButtonBorderWidth,  // 边框宽度
            color = if (enabled) {
                MaterialTheme.colorScheme.outline                 // 启用状态下使用主题 outline 颜色
            } else {
                MaterialTheme.colorScheme.onSurface.copy(
                    // 禁用状态下使用 onSurface 颜色并设置透明度
                    alpha = HioButtonDefaults.DISABLED_OUTLINED_BUTTON_BORDER_ALPHA,
                )
            },
        ),
        contentPadding = contentPadding,  // 设置内容内边距
        content = content,           // 设置按钮内容
    )
}

/**
 * Happyinn in Obsbot 描边按钮，具有文本和图标内容插槽。
 *
 * @param onClick 用户点击按钮时调用的回调函数
 * @param modifier 应用于按钮的修饰符
 * @param enabled 控制按钮的启用状态。当为 `false` 时，此按钮不可点击，并且对无障碍服务显示为禁用状态
 * @param text 按钮文本标签内容
 * @param leadingIcon 按钮前导图标内容。传入 `null` 表示不显示前导图标
 */
@Composable
fun HioOutlinedButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: @Composable () -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    // 调用上面定义的通用 HioOutlinedButton 函数
    HioOutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        // 根据是否有前导图标决定使用哪种内容内边距
        contentPadding = if (leadingIcon != null) {
            ButtonDefaults.ButtonWithIconContentPadding  // 有图标时使用带图标的内边距
        } else {
            ButtonDefaults.ContentPadding               // 无图标时使用默认内边距
        },
    ) {
        // 使用专门的内容布局函数来排列文本和图标
        HioButtonContent(
            text = text,
            leadingIcon = leadingIcon,
        )
    }
}

/**
 * Happyinn in Obsbot 文本按钮，具有通用内容插槽。封装了 Material 3 的 [TextButton] 组件。
 *
 * @param onClick 用户点击按钮时调用的回调函数
 * @param modifier 应用于按钮的修饰符
 * @param enabled 控制按钮的启用状态。当为 `false` 时，此按钮不可点击，并且对无障碍服务显示为禁用状态
 * @param content 按钮的内容
 */
@Composable
fun HioTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable RowScope.() -> Unit,
) {
    // 创建一个 Material 3 TextButton 组件实例
    TextButton(
        onClick = onClick,           // 设置点击事件处理器
        modifier = modifier,         // 应用传入的修饰符
        enabled = enabled,           // 设置启用状态
        colors = ButtonDefaults.textButtonColors(
            // 设置按钮内容颜色为主题中的 onBackground 颜色
            contentColor = MaterialTheme.colorScheme.onBackground,
        ),
        content = content,           // 设置按钮内容
    )
}

/**
 * Happyinn in Obsbot 文本按钮，具有文本和图标内容插槽。
 *
 * @param onClick 用户点击按钮时调用的回调函数
 * @param modifier 应用于按钮的修饰符
 * @param enabled 控制按钮的启用状态。当为 `false` 时，此按钮不可点击，并且对无障碍服务显示为禁用状态
 * @param text 按钮文本标签内容
 * @param leadingIcon 按钮前导图标内容。传入 `null` 表示不显示前导图标
 */
@Composable
fun HioTextButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    text: @Composable () -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    // 调用上面定义的通用 HioTextButton 函数
    HioTextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
    ) {
        // 使用专门的内容布局函数来排列文本和图标
        HioButtonContent(
            text = text,
            leadingIcon = leadingIcon,
        )
    }
}

/**
 * 内部使用的 Happyinn in Obsbot 按钮内容布局函数，用于排列文本标签和前导图标。
 *
 * @param text 按钮文本标签内容
 * @param leadingIcon 按钮前导图标内容，默认为 `null` 表示不显示前导图标
 */
@Composable
private fun HioButtonContent(
    text: @Composable () -> Unit,
    leadingIcon: @Composable (() -> Unit)? = null,
) {
    // 如果提供了前导图标，则显示它
    if (leadingIcon != null) {
        Box(Modifier.sizeIn(maxHeight = ButtonDefaults.IconSize)) {
            leadingIcon()  // 显示前导图标
        }
    }

    // 显示文本内容，并根据是否有前导图标添加相应的左边距
    Box(
        Modifier
            .padding(
                start = if (leadingIcon != null) {
                    ButtonDefaults.IconSpacing  // 有图标时添加图标间距作为左边距
                } else {
                    0.dp                        // 无图标时不添加左边距
                },
            ),
    ) {
        text()  // 显示文本内容
    }
}

// 主题预览注解，用于在 Android Studio 中预览组件效果
@ThemePreviews
@Composable
fun HioButtonPreview() {
    // 应用 Happyinn in Obsbot 主题
    HioTheme {
        // 创建背景容器用于预览
        HioBackground(modifier = Modifier.size(150.dp, 50.dp)) {
            // 预览标准填充按钮
            HioButton(onClick = {}, text = { Text("Test button") })
        }
    }
}

// 主题预览注解，用于在 Android Studio 中预览组件效果
@ThemePreviews
@Composable
fun HioOutlinedButtonPreview() {
    // 应用 Happyinn in Obsbot 主题
    HioTheme {
        // 创建背景容器用于预览
        HioBackground(modifier = Modifier.size(150.dp, 50.dp)) {
            // 预览描边按钮
            HioOutlinedButton(onClick = {}, text = { Text("Test button") })
        }
    }
}

// 主题预览注解，用于在 Android Studio 中预览组件效果
@ThemePreviews
@Composable
fun HioButtonLeadingIconPreview() {
    // 应用 Happyinn in Obsbot 主题
    HioTheme {
        // 创建背景容器用于预览
        HioBackground(modifier = Modifier.size(150.dp, 50.dp)) {
            // 预览带有前导图标的填充按钮
            HioButton(
                onClick = {},                           // 空的点击处理函数
                text = { Text("Test button") },         // 按钮文本
                leadingIcon = { Icon(imageVector = HioIcons.Add, contentDescription = null) },  // 前导加号图标
            )
        }
    }
}

/**
 * Happyinn in Obsbot 按钮默认值集合。
 */
object HioButtonDefaults {
    // TODO: 提交 bug 报告
    // OutlinedButton 的边框颜色默认情况下不尊重禁用状态
    const val DISABLED_OUTLINED_BUTTON_BORDER_ALPHA = 0.12f  // 禁用状态下的描边按钮边框透明度

    // TODO: 提交 bug 报告
    // OutlinedButton 默认边框宽度未通过 ButtonDefaults 暴露出来
    val OutlinedButtonBorderWidth = 1.dp  // 描边按钮的边框宽度
}


@Composable
fun DoneButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    TextButton(
        enabled = enabled,
        onClick = onClick,
        modifier = modifier,
    ) {
        Text(text = stringResource(R.string.done))
    }
}

@Composable
fun CancelButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    TextButton(
        enabled = enabled,
        onClick = onClick,
        modifier = modifier,
    ) {
        Text(text = stringResource(R.string.cancel))
    }
}


@Composable
fun RadioTextButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onClick,
                role = Role.RadioButton,
            )
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = text)
    }
}

@Composable
fun ShortcutChipButton(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .clip(CircleShape)
            .clickable { onClick() }
            .background(color = MaterialTheme.colorScheme.surfaceColorAtElevation(5.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.secondary,
        )
        Text(text = text, style = MaterialTheme.typography.labelLarge)
    }
}
