package com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository.media

import com.obsbot.happyinn.apps.happyinninobsbot.core.datastore.mediaDataSource.PlayerPreferencesDataSource
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.PlayerPreferences
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class LocalPreferencesRepository @Inject constructor(
    private val playerPreferencesDataSource: PlayerPreferencesDataSource,
) : PreferencesRepository {

    override val playerPreferences: Flow<PlayerPreferences>
        get() = playerPreferencesDataSource.preferences

    override suspend fun updatePlayerPreferences(
        transform: suspend (PlayerPreferences) -> PlayerPreferences,
    ) {
        playerPreferencesDataSource.update(transform)
    }
}
