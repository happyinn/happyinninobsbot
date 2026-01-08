package com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media

/**
 * 主题配置枚举
 * 
 * 定义了应用程序的不同主题配置选项
 */
enum class ThemeConfig {
    SYSTEM, // 跟随系统主题（自动切换浅色/深色）
    OFF,    // 始终使用浅色主题
    ON,     // 始终使用深色主题
}