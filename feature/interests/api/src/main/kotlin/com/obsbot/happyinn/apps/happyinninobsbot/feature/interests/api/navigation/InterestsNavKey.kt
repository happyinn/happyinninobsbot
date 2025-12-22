package com.obsbot.happyinn.apps.happyinninobsbot.feature.interests.api.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data class InterestsNavKey(
    // The ID of the topic which will be initially selected at this destination
    val initialTopicId: String? = null,
) : NavKey
