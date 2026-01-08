package com.obsbot.happyinn.apps.happyinninobsbot.core.datastore.mediaDataSource

import androidx.datastore.core.DataStore
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.media.PlayerPreferences
import javax.inject.Inject
import timber.log.Timber

class PlayerPreferencesDataSource @Inject constructor(
    private val preferencesDataStore: DataStore<PlayerPreferences>,
) : PreferencesDataSource<PlayerPreferences> {

    override val preferences = preferencesDataStore.data

    override suspend fun update(transform: suspend (PlayerPreferences) -> PlayerPreferences) {
        try {
            preferencesDataStore.updateData(transform)
        } catch (ioException: Exception) {
            Timber.tag("HioPlayerPreferences").e("Failed to update app preferences: $ioException")
        }
    }
}
