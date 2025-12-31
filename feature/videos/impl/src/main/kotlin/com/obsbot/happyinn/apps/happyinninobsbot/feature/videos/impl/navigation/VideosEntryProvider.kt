package com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.obsbot.happyinn.apps.happyinninobsbot.core.navigation.Navigator
import com.obsbot.happyinn.apps.happyinninobsbot.feature.topic.api.navigation.navigateToTopic
import com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.api.navigation.VideosNavKey
import com.obsbot.happyinn.apps.happyinninobsbot.feature.videos.impl.VideosScreen

fun EntryProviderScope<NavKey>.videosEntry(navigator: Navigator) {
    entry<VideosNavKey> {
        VideosScreen(
            onTopicClick = navigator::navigateToTopic,
        )
    }
}