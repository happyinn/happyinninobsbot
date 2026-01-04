package com.obsbot.happyinn.apps.happyinninobsbot.core.service.media

import android.net.Uri

interface MediaInfoSynchronizer {

    suspend fun addMedia(uri: Uri)
}
