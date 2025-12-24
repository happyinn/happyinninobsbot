package com.obsbot.happyinn.apps.happyinninobsbot.core.notifications

import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.VideosResource

/**
 * Interface for creating notifications in the app
 */
interface Notifier {
    fun postVideosNotifications(videosResources: List<VideosResource>)
}
