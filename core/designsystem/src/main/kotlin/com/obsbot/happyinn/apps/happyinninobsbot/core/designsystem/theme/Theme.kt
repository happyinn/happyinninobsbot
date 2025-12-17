package com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.theme

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.annotation.VisibleForTesting
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

/**
 * 浅色默认主题颜色方案
 */
/**表示类、方法或字段的可见性已放宽，使其可见范围比原本必要的范围更广，目的是让代码可测试。
你可以选择性地指定如果不是为了测试，其可见性 “应该” 是什么；这能让工具捕获生产代码中意外的访问。*/
@VisibleForTesting
val LightDefaultColorScheme = lightColorScheme(
    primary = Purple40,                // 主色调
    onPrimary = Color.White,           // 主色调上的文字颜色
    primaryContainer = Purple90,       // 主色调容器背景色
    onPrimaryContainer = Purple10,     // 主色调容器内文字颜色
    secondary = Orange40,              // 次要色调
    onSecondary = Color.White,         // 次要色调上文字颜色
    secondaryContainer = Orange90,     // 次要色调容器背景色
    onSecondaryContainer = Orange10,   // 次要色调容器内文字颜色
    tertiary = Blue40,                 // 第三色调
    onTertiary = Color.White,          // 第三色调上文字颜色
    tertiaryContainer = Blue90,        // 第三色调容器背景色
    onTertiaryContainer = Blue10,      // 第三色调容器内文字颜色
    error = Red40,                     // 错误状态颜色
    onError = Color.White,             // 错误状态上文字颜色
    errorContainer = Red90,            // 错误状态容器背景色
    onErrorContainer = Red10,          // 错误状态容器内文字颜色
    background = DarkPurpleGray99,     // 背景色
    onBackground = DarkPurpleGray10,   // 背景上文字颜色
    surface = DarkPurpleGray99,        // 表面颜色
    onSurface = DarkPurpleGray10,      // 表面上文字颜色
    surfaceVariant = PurpleGray90,     // 变体表面颜色
    onSurfaceVariant = PurpleGray30,   // 变体表面上文字颜色
    inverseSurface = DarkPurpleGray20, // 反转表面颜色（深色）
    inverseOnSurface = DarkPurpleGray95,// 反转表面对应的文字颜色（浅色）
    outline = PurpleGray50,            // 边框线颜色
)

/**
 * 深色默认主题颜色方案
 */
@VisibleForTesting
val DarkDefaultColorScheme = darkColorScheme(
    primary = Purple80,
    onPrimary = Purple20,
    primaryContainer = Purple30,
    onPrimaryContainer = Purple90,
    secondary = Orange80,
    onSecondary = Orange20,
    secondaryContainer = Orange30,
    onSecondaryContainer = Orange90,
    tertiary = Blue80,
    onTertiary = Blue20,
    tertiaryContainer = Blue30,
    onTertiaryContainer = Blue90,
    error = Red80,
    onError = Red20,
    errorContainer = Red30,
    onErrorContainer = Red90,
    background = DarkPurpleGray10,
    onBackground = DarkPurpleGray90,
    surface = DarkPurpleGray10,
    onSurface = DarkPurpleGray90,
    surfaceVariant = PurpleGray30,
    onSurfaceVariant = PurpleGray80,
    inverseSurface = DarkPurpleGray90,
    inverseOnSurface = DarkPurpleGray10,
    outline = PurpleGray60,
)

/**
 * 浅色 Android 主题颜色方案
 */
@VisibleForTesting
val LightAndroidColorScheme = lightColorScheme(
    primary = Green40,
    onPrimary = Color.White,
    primaryContainer = Green90,
    onPrimaryContainer = Green10,
    secondary = DarkGreen40,
    onSecondary = Color.White,
    secondaryContainer = DarkGreen90,
    onSecondaryContainer = DarkGreen10,
    tertiary = Teal40,
    onTertiary = Color.White,
    tertiaryContainer = Teal90,
    onTertiaryContainer = Teal10,
    error = Red40,
    onError = Color.White,
    errorContainer = Red90,
    onErrorContainer = Red10,
    background = DarkGreenGray99,
    onBackground = DarkGreenGray10,
    surface = DarkGreenGray99,
    onSurface = DarkGreenGray10,
    surfaceVariant = GreenGray90,
    onSurfaceVariant = GreenGray30,
    inverseSurface = DarkGreenGray20,
    inverseOnSurface = DarkGreenGray95,
    outline = GreenGray50,
)

/**
 * 深色 Android 主题颜色方案
 */
@VisibleForTesting
val DarkAndroidColorScheme = darkColorScheme(
    primary = Green80,
    onPrimary = Green20,
    primaryContainer = Green30,
    onPrimaryContainer = Green90,
    secondary = DarkGreen80,
    onSecondary = DarkGreen20,
    secondaryContainer = DarkGreen30,
    onSecondaryContainer = DarkGreen90,
    tertiary = Teal80,
    onTertiary = Teal20,
    tertiaryContainer = Teal30,
    onTertiaryContainer = Teal90,
    error = Red80,
    onError = Red20,
    errorContainer = Red30,
    onErrorContainer = Red90,
    background = DarkGreenGray10,
    onBackground = DarkGreenGray90,
    surface = DarkGreenGray10,
    onSurface = DarkGreenGray90,
    surfaceVariant = GreenGray30,
    onSurfaceVariant = GreenGray80,
    inverseSurface = DarkGreenGray90,
    inverseOnSurface = DarkGreenGray10,
    outline = GreenGray60,
)

/**
 * 浅色 Android 渐变颜色配置
 */
val LightAndroidGradientColors = GradientColors(container = DarkGreenGray95)

/**
 * 深色 Android 渐变颜色配置
 */
val DarkAndroidGradientColors = GradientColors(container = Color.Black)

/**
 * 浅色 Android 背景主题配置
 */
val LightAndroidBackgroundTheme = BackgroundTheme(color = DarkGreenGray95)

/**
 * 深色 Android 背景主题配置
 */
val DarkAndroidBackgroundTheme = BackgroundTheme(color = Color.Black)

/**
 * Hio 主题入口函数
 *
 * @param darkTheme 是否使用深色主题，默认跟随系统设置
 * @param androidTheme 是否使用 Android 风格的主题配色而非默认主题
 * @param disableDynamicTheming 如果为 true，则禁用动态主题功能（即使支持）。当 androidTheme 为 true 时不生效
 */
@Composable
fun HioTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    androidTheme: Boolean = false,
    disableDynamicTheming: Boolean = true,
    content: @Composable () -> Unit,
) {
    // 根据参数决定使用的颜色方案
    val colorScheme = when {
        // 使用 Android 风格主题
        androidTheme -> if (darkTheme) DarkAndroidColorScheme else LightAndroidColorScheme
        // 启用动态主题且设备支持
        !disableDynamicTheming && supportsDynamicTheming() -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        // 使用默认主题
        else -> if (darkTheme) DarkDefaultColorScheme else LightDefaultColorScheme
    }

    // 定义渐变颜色配置
    // 动态主题下使用空的渐变色（基于基础表面颜色）
    val emptyGradientColors = GradientColors(container = colorScheme.surfaceColorAtElevation(2.dp))
    // 默认渐变色配置
    val defaultGradientColors = GradientColors(
        top = colorScheme.inverseOnSurface,
        bottom = colorScheme.primaryContainer,
        container = colorScheme.surface,
    )

    // 最终使用的渐变色配置
    val gradientColors = when {
        androidTheme -> if (darkTheme) DarkAndroidGradientColors else LightAndroidGradientColors
        !disableDynamicTheming && supportsDynamicTheming() -> emptyGradientColors
        else -> defaultGradientColors
    }

    // 背景主题配置
    val defaultBackgroundTheme = BackgroundTheme(
        color = colorScheme.surface,
        tonalElevation = 2.dp,
    )

    val backgroundTheme = when {
        androidTheme -> if (darkTheme) DarkAndroidBackgroundTheme else LightAndroidBackgroundTheme
        else -> defaultBackgroundTheme
    }

    // 着色主题配置
    val tintTheme = when {
        androidTheme -> TintTheme()
        !disableDynamicTheming && supportsDynamicTheming() -> TintTheme(colorScheme.primary)
        else -> TintTheme()
    }

    // 提供本地组合值给子组件使用
    CompositionLocalProvider(
        LocalGradientColors provides gradientColors,
        LocalBackgroundTheme provides backgroundTheme,
        LocalTintTheme provides tintTheme,
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = HioTypography,
            content = content,
        )
    }
}

// 判断是否支持动态主题（API 31 及以上版本）
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
fun supportsDynamicTheming() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
