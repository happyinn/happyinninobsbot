package com.obsbot.happyinn.apps.happyinninobsbot.feature.foryou.impl.navigation

import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.obsbot.happyinn.apps.happyinninobsbot.core.navigation.Navigator
import com.obsbot.happyinn.apps.happyinninobsbot.feature.foryou.api.navigation.ForYouNavKey
import com.obsbot.happyinn.apps.happyinninobsbot.feature.foryou.impl.ForYouScreen
import com.obsbot.happyinn.apps.happyinninobsbot.feature.topic.api.navigation.navigateToTopic

fun EntryProviderScope<NavKey>.forYouEntry(navigator: Navigator) {
    entry<ForYouNavKey> {
        ForYouScreen(
            onTopicClick = navigator::navigateToTopic,
        )
    }
}
