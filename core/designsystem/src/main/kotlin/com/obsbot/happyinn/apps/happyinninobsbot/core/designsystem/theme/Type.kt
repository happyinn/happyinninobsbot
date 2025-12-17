package com.obsbot.happyinn.apps.happyinninobsbot.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.LineHeightStyle.Alignment
import androidx.compose.ui.text.style.LineHeightStyle.Trim
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.sp


/**
 * HappyinnInObsbot应用的排版样式定义
 * 基于Material Design 3的Typography规范，定义了应用中各种文本样式的标准配置
 */
internal val HioTypography = Typography(
    // 超大显示标题样式 - 用于最重要的大标题
    displayLarge = TextStyle(
        fontWeight = FontWeight.Normal,      // 字体粗细：正常
        fontSize = 57.sp,                    // 字体大小：57密度无关像素
        lineHeight = 64.sp,                  // 行高：64密度无关像素
        letterSpacing = (-0.25).sp,          // 字符间距：-0.25密度无关像素（紧缩）
        textDirection = TextDirection.Ltr,   // 文本方向：从左到右
        textAlign = TextAlign.Left,          // 文本对齐：左对齐
    ),
    // 大显示标题样式 - 用于重要标题
    displayMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp,
        textDirection = TextDirection.Ltr,
        textAlign = TextAlign.Left,
    ),
    // 小显示标题样式 - 用于次要标题
    displaySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp,
        textDirection = TextDirection.Ltr,
        textAlign = TextAlign.Left,
    ),
    // 大标题样式 - 用于页面主要标题
    headlineLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp,
        textDirection = TextDirection.Ltr,
        textAlign = TextAlign.Left,
    ),
    // 中标题样式 - 用于章节标题
    headlineMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp,
        textDirection = TextDirection.Ltr,
        textAlign = TextAlign.Left,
    ),
    // 小标题样式 - 用于小节标题
    headlineSmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp,
        lineHeightStyle = LineHeightStyle(           // 行高样式配置
            alignment = Alignment.Bottom,            // 行高对齐：底部对齐
            trim = Trim.None,                        // 行高修剪：不修剪
        ),
        textDirection = TextDirection.Ltr,
        textAlign = TextAlign.Left,
    ),
    // 大标题样式 - 用于卡片标题等
    titleLarge = TextStyle(
        fontWeight = FontWeight.Bold,                // 字体粗细：加粗
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp,
        lineHeightStyle = LineHeightStyle(
            alignment = Alignment.Bottom,
            trim = Trim.LastLineBottom,              // 修剪最后一行底部空白
        ),
        textDirection = TextDirection.Ltr,
        textAlign = TextAlign.Left,
    ),
    // 中标题样式 - 用于列表项标题等
    titleMedium = TextStyle(
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.1.sp,                      // 微小的字符间距增加
        textDirection = TextDirection.Ltr,
        textAlign = TextAlign.Left,
    ),
    // 小标题样式 - 用于辅助标题
    titleSmall = TextStyle(
        fontWeight = FontWeight.Medium,              // 字体粗细：中等
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
        textDirection = TextDirection.Ltr,
        textAlign = TextAlign.Left,
    ),
    // 默认正文大号样式 - 用于主要段落文本
    bodyLarge = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp,
        lineHeightStyle = LineHeightStyle(
            alignment = Alignment.Center,            // 行高对齐：居中对齐
            trim = Trim.None,
        ),
        textDirection = TextDirection.Ltr,
        textAlign = TextAlign.Left,
    ),
    // 默认正文中号样式 - 用于普通段落文本
    bodyMedium = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp,
        textDirection = TextDirection.Ltr,
        textAlign = TextAlign.Left,
    ),
    // 默认正文小号样式 - 用于辅助说明文本
    bodySmall = TextStyle(
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp,
        textDirection = TextDirection.Ltr,
        textAlign = TextAlign.Left,
    ),
    // 大标签样式 - 用于按钮文本
    labelLarge = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp,
        lineHeightStyle = LineHeightStyle(
            alignment = Alignment.Center,
            trim = Trim.LastLineBottom,
        ),
        textDirection = TextDirection.Ltr,
        textAlign = TextAlign.Left,
    ),
    // 中标签样式 - 用于导航项文本
    labelMedium = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp,
        lineHeightStyle = LineHeightStyle(
            alignment = Alignment.Center,
            trim = Trim.LastLineBottom,
        ),
        textDirection = TextDirection.Ltr,
        textAlign = TextAlign.Left,
    ),
    // 小标签样式 - 用于标签(Tag)文本
    labelSmall = TextStyle(
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 0.sp,
        lineHeightStyle = LineHeightStyle(
            alignment = Alignment.Center,
            trim = Trim.LastLineBottom,
        ),
        textDirection = TextDirection.Ltr,
        textAlign = TextAlign.Left,
    ),
)
