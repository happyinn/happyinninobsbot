package com.obsbot.happyinn.apps.happyinninobsbot.core.notifications

import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.NewsResource

/**
 * Interface for creating notifications in the app
 */
interface Notifier {
    fun postNewsNotifications(newsResources: List<NewsResource>)
}

