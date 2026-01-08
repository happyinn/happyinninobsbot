package com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.impl.composables

/**
 * 信息芯片组件，用于显示小型信息标签
 */

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
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.theme.HioPlayerTheme

/**
 * 信息芯片组件，用于显示小型信息标签
 * 
 * 信息芯片是一种小型标签组件，通常用于显示数量、时长、大小等辅助信息。
 * 芯片具有背景色、内边距和圆角形状，可以自定义颜色和形状。
 * 
 * @param text 芯片显示的文本内容
 * @param modifier 组件修饰符，用于自定义组件外观和布局
 * @param backgroundColor 芯片背景颜色，默认使用主题的次要容器颜色
 * @param contentColor 芯片文本内容颜色，默认使用主题的次要容器上的文本颜色
 * @param shape 芯片形状，默认使用主题的超小形状并自定义圆角大小为2dp
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

//预览
@PreviewLightDark
@Composable
fun InfoChipPreview() {
    HioPlayerTheme {
        InfoChip(text = "123")
    }
}