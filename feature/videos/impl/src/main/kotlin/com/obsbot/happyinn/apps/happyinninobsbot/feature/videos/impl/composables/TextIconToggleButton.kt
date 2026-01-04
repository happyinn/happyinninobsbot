/**
 * 带有文本和图标的切换按钮组件
 */
package com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.impl.composables

import androidx.compose.foundation.Indication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.theme.HioPlayerTheme

/**
 * 带有文本和图标的切换按钮组件
 * @param text 按钮下方显示的文本
 * @param icon 按钮上显示的图标
 * @param modifier 修改器，用于自定义组件外观
 * @param isSelected 当前是否选中状态
 * @param interactionSource 交互源，用于处理用户交互
 * @param indication 指示效果，用于显示用户交互反馈
 * @param onClick 点击回调，返回新的选中状态
 */
@Composable
fun TextIconToggleButton(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    indication: Indication? = null,
    onClick: (Boolean) -> Unit = {},
) {
    // 垂直排列的列，包含图标按钮和文本
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .clickable(
                interactionSource = interactionSource,
                indication = indication,
                onClick = { onClick(!isSelected) },
            )
            .padding(10.dp),
    ) {
        // 填充式图标切换按钮
        FilledIconToggleButton(
            checked = isSelected,
            onCheckedChange = onClick,
            interactionSource = interactionSource,
        ) {
            Icon(imageVector = icon, contentDescription = text)
        }
        // 按钮下方的文本
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

/**
 * 文本图标切换按钮的预览
 */
@Preview
@Composable
fun TextIconToggleButtonPreview() {
    HioPlayerTheme {
        Surface {
            TextIconToggleButton(
                text = "Text",
                icon = Icons.Rounded.Search,
            )
        }
    }
}
