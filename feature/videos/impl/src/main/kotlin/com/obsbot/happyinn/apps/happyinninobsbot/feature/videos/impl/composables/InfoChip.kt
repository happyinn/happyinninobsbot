/**
 * 信息芯片组件，用于显示小型信息标签
 */
package com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.impl.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * 信息芯片组件，用于显示小型信息标签
 * @param text 芯片显示的文本
 * @param modifier 修改器，用于自定义组件外观
 * @param backgroundColor 芯片背景颜色，默认使用主题的次要容器颜色
 * @param contentColor 芯片内容颜色，默认使用主题的次要容器上的文本颜色
 * @param shape 芯片形状，默认使用主题的超小形状并自定义圆角大小
 */
@Composable
fun InfoChip(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.secondaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onSecondaryContainer,
    shape: Shape = MaterialTheme.shapes.extraSmall.copy(CornerSize(2.dp)),
) {
    // 信息芯片的文本组件
    Text(
        text = text,
        // 使用小型标签样式，并设置字重为正常
        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Normal),
        color = contentColor,
        // 应用修改器：裁剪形状、设置背景色、添加内边距
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .padding(vertical = 1.dp, horizontal = 3.dp),
    )
}
