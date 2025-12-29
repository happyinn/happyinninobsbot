/*
 * 版权所�?2022 The Android Open Source Project
 *
 * 根据 Apache 许可�?2.0 版（"许可�?）授权；
 * 除非符合许可证要求，否则您不得使用此文件�?
 * 您可以在以下位置获得许可证副本：
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * 除非适用法律要求或书面同意，根据许可证分发的软件
 * 是基�?按原�?的基础上分发的，不附带任何明示或暗示的担保条件�?
 * 请参阅许可证了解特定语言 governing permissions �?limitations�?
 */

package com.obsbot.happyinn.apps.happyinninobsbot.core.model.data

/**
 * 汇总用户兴趣数据的�?
 */
data class UserData(
    //TODO 更改用户数据属性

    // 用户收藏的新闻资源ID集合
    val bookmarkedNewsResources: Set<String>,
    // 用户已查看的新闻资源ID集合
    val viewedNewsResources: Set<String>,
    // 用户关注的主题ID集合
    val followedTopics: Set<String>,
    // 主题品牌设置
    val themeBrand: ThemeBrand,
    // 深色主题配置
    val darkThemeConfig: DarkThemeConfig,
    // 是否使用动态颜�?
    val useDynamicColor: Boolean,
    // 是否隐藏引导页面
    val shouldHideOnboarding: Boolean,
)

