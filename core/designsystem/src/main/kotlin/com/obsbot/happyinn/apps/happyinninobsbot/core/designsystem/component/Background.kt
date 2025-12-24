/*
 * 版权所有 2022 The Android Open Source Project
 *
 * 根据 Apache 许可证 2.0 版（"许可证"）授权；
 * 除非符合许可证要求，否则您不得使用此文件。
 * 您可以在以下位置获得许可证副本：
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * 除非适用法律要求或书面同意，根据许可证分发的软件
 * 是基于"按原样"的基础上分发的，不附带任何明示或暗示的担保条件。
 * 请参阅许可证了解特定语言 governing permissions 和 limitations。
 */

package com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.LocalAbsoluteTonalElevation
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.theme.GradientColors
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.theme.HioTheme
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.theme.LocalBackgroundTheme
import com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.theme.LocalGradientColors
import kotlin.math.tan
import kotlin.to


//TODO 待深入研究
/**
 * 应用的主要背景组件。
 * 使用 [LocalBackgroundTheme] 来设置 [Surface] 的颜色和色调高度。
 *
 * @param modifier 应用于背景的修饰符。
 * @param content 背景内容。
 */
@Composable
fun HioBackground(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    // 获取当前背景主题的颜色
    val color = LocalBackgroundTheme.current.color
    // 获取当前背景主题的色调高度
    val tonalElevation = LocalBackgroundTheme.current.tonalElevation
    Surface(
        // 如果颜色未指定，则使用透明色
        color = if (color == Color.Unspecified) Color.Transparent else color,
        // 如果色调高度未指定，则使用0.dp
        tonalElevation = if (tonalElevation == Dp.Unspecified) 0.dp else tonalElevation,
        modifier = modifier.fillMaxSize(),
    ) {
        // 提供绝对色调高度为0.dp的组合局部值
        CompositionLocalProvider(LocalAbsoluteTonalElevation provides 0.dp) {
            content()
        }
    }
}

/**
 * 用于特定屏幕的渐变背景。使用 [LocalBackgroundTheme] 在 [Surface] 内的 [Box] 中设置渐变颜色。
 *
 * @param modifier 应用于背景的修饰符。
 * @param gradientColors 要渲染的渐变颜色。
 * @param content 背景内容。
 */
@Composable
fun HioGradientBackground(
    modifier: Modifier = Modifier,
    gradientColors: GradientColors = LocalGradientColors.current,
    content: @Composable () -> Unit,
) {
    // 记住并更新顶部渐变颜色
    val currentTopColor by rememberUpdatedState(gradientColors.top)
    // 记住并更新底部渐变颜色
    val currentBottomColor by rememberUpdatedState(gradientColors.bottom)
    Surface(
        // 如果容器颜色未指定，则使用透明色
        color = if (gradientColors.container == Color.Unspecified) {
            Color.Transparent
        } else {
            gradientColors.container
        },
        modifier = modifier.fillMaxSize(),
    ) {
        Box(
            Modifier
                .fillMaxSize()
                .drawWithCache {
                    // 计算起始和结束坐标，使渐变角度偏离垂直轴11.06度
                    val offset = size.height * tan(
                        Math
                            .toRadians(11.06)
                            .toFloat(),
                    )

                    val start = Offset(size.width / 2 + offset / 2, 0f)
                    val end = Offset(size.width / 2 - offset / 2, size.height)

                    // 创建顶部渐变，在垂直方向一半点后淡出
                    val topGradient = Brush.linearGradient(
                        0f to if (currentTopColor == Color.Unspecified) {
                            Color.Transparent
                        } else {
                            currentTopColor
                        },
                        0.724f to Color.Transparent,
                        start = start,
                        end = end,
                    )
                    // 创建底部渐变，在垂直方向一半点前淡入
                    val bottomGradient = Brush.linearGradient(
                        0.2552f to Color.Transparent,
                        1f to if (currentBottomColor == Color.Unspecified) {
                            Color.Transparent
                        } else {
                            currentBottomColor
                        },
                        start = start,
                        end = end,
                    )

                    onDrawBehind {
                        // 这里有重叠，所以顺序很重要
                        drawRect(topGradient)
                        drawRect(bottomGradient)
                    }
                },
        ) {
            content()
        }
    }
}

/**
 * 多预览注解，表示浅色和深色主题。将此注解添加到可组合函数上以渲染两种主题。
 */
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO, name = "浅色主题")
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES, name = "深色主题")
annotation class ThemePreviews

// 默认背景预览 - 禁用动态主题
@ThemePreviews
@Composable
fun BackgroundDefault() {
    HioTheme(disableDynamicTheming = true) {
        HioBackground(Modifier.size(100.dp), content = {})
    }
}

// 动态主题背景预览 - 启用动态主题
@ThemePreviews
@Composable
fun BackgroundDynamic() {
    HioTheme(disableDynamicTheming = false) {
        HioBackground(Modifier.size(100.dp), content = {})
    }
}

// Android主题背景预览
@ThemePreviews
@Composable
fun BackgroundAndroid() {
    HioTheme(androidTheme = true) {
        HioBackground(Modifier.size(100.dp), content = {})
    }
}

// 默认渐变背景预览 - 禁用动态主题
@ThemePreviews
@Composable
fun GradientBackgroundDefault() {
    HioTheme(disableDynamicTheming = true) {
        HioGradientBackground(Modifier.size(100.dp), content = {})
    }
}

// 动态主题渐变背景预览 - 启用动态主题
@ThemePreviews
@Composable
fun GradientBackgroundDynamic() {
    HioTheme(disableDynamicTheming = false) {
        HioGradientBackground(Modifier.size(100.dp), content = {})
    }
}

// Android主题渐变背景预览
@ThemePreviews
@Composable
fun GradientBackgroundAndroid() {
    HioTheme(androidTheme = true) {
        HioGradientBackground(Modifier.size(100.dp), content = {})
    }
}
