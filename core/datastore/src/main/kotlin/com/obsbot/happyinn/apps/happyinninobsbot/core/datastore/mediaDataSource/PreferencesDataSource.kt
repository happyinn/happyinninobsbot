package com.obsbot.happyinn.apps.happyinninobsbot.core.datastore.mediaDataSource

import kotlinx.coroutines.flow.Flow

interface PreferencesDataSource<T> {

    val preferences: Flow<T>

    suspend fun update(transform: suspend (T) -> T)
}
