package com.obsbot.happyinn.apps.happyinninobsbot.core.notifications

import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.VideosResource
import javax.inject.Inject

/**
 * Implementation of [Notifier] which does nothing. Useful for tests and previews.
 */
internal class NoOpNotifier @Inject constructor() : Notifier {
    override fun postVideosNotifications(videosResources: List<VideosResource>) = Unit
}
