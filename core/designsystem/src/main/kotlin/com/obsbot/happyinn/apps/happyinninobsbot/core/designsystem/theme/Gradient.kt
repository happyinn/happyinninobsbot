package com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.theme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * 用于建模HappyInn机器人应用渐变色值的数据类
 *
 * @param top 渐变的顶部颜色
 * @param bottom 渐变的底部颜色
 * @param container 渐变渲染所在的容器背景色
 */
@Immutable
data class GradientColors(
    //默认无颜色 @Stable val Unspecified = Color(0f, 0f, 0f, 0f, ColorSpaces.Unspecified)
    val top: Color = Color.Unspecified,
    val bottom: Color = Color.Unspecified,
    val container: Color = Color.Unspecified,
)

/**
 * [GradientColors] 的组合局部状态(Local Composition State)
 * 用于在Compose组件树中传递渐变色配置
 */
val LocalGradientColors = staticCompositionLocalOf { GradientColors() }
