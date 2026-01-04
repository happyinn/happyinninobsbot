package com.obsbot.happyinn.apps.happyinninobsbot.core.service.media

import android.net.Uri
import androidx.activity.ComponentActivity

interface MediaService {
    fun initialize(activity: ComponentActivity)
    suspend fun deleteMedia(uris: List<Uri>): Boolean
    suspend fun renameMedia(uri: Uri, to: String): Boolean
}
