/*
 * Copyright 2023 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.obsbot.happyinn.apps.happyinninobsbot.core.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import com.obsbot.happyinn.apps.happyinninobsbot.core.analytics.AnalyticsEvent
import com.obsbot.happyinn.apps.happyinninobsbot.core.analytics.AnalyticsHelper
import com.obsbot.happyinn.apps.happyinninobsbot.core.analytics.AnalyticsEvent.Param
import com.obsbot.happyinn.apps.happyinninobsbot.core.analytics.AnalyticsEvent.ParamKeys
import com.obsbot.happyinn.apps.happyinninobsbot.core.analytics.AnalyticsEvent.Types
import com.obsbot.happyinn.apps.happyinninobsbot.core.analytics.LocalAnalyticsHelper

/**
 * 与UI相关的分析事件的类和函数
 */

/**
 * 记录页面浏览事件
 *
 * 此函数用于记录用户浏览特定页面的事件，使用标准的SCREEN_VIEW事件类型
 *
 * @param screenName 被浏览的页面名称，用于标识具体的页面
 */
fun AnalyticsHelper.logScreenView(screenName: String) {
    logEvent(
        AnalyticsEvent(
            type = Types.SCREEN_VIEW,
            extras = listOf(
                Param(ParamKeys.SCREEN_NAME, screenName),
            ),
        ),
    )
}

/**
 * 记录新闻资源打开事件
 *
 * 此函数用于记录用户打开新闻资源的事件，使用自定义的News_resource_opened事件类型
 *
 * @param newsResourceId 被打开的新闻资源ID，用于唯一标识新闻资源
 */
fun AnalyticsHelper.logNewsResourceOpened(newsResourceId: String) {
    logEvent(
        event = AnalyticsEvent(
            type = "News_resource_opened",
            extras = listOf(
                Param("opened_News_resource", newsResourceId),
            ),
        ),
    )
}

/**
 * 用于记录页面浏览事件的副作用函数
 *
 * 在可组合函数中使用此函数可以自动记录页面浏览事件，当组件进入组合时触发事件记�?
 *
 * @param screenName 要跟踪的页面名称
 * @param analyticsHelper 分析助手实例，默认使用[LocalAnalyticsHelper]提供的实�?
 */
@Composable
fun TrackScreenViewEvent(
    screenName: String,
    analyticsHelper: AnalyticsHelper = LocalAnalyticsHelper.current,
) = DisposableEffect(Unit) {
    analyticsHelper.logScreenView(screenName)
    onDispose {}
}

