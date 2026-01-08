package com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.media

import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.PlayerPreferences
import kotlinx.coroutines.flow.Flow

interface PreferencesRepository {


    /**
     * Stream of [PlayerPreferences].
     */
    val playerPreferences: Flow<PlayerPreferences>

    suspend fun updatePlayerPreferences(transform: suspend (PlayerPreferences) -> PlayerPreferences)
}
