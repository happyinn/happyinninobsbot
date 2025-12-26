package com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository

import android.app.appsearch.SearchResult
import kotlinx.coroutines.flow.Flow

/**
 * Data layer interface for the search feature.
 */
interface SearchContentsRepository {

    /**
     * Populate the fts tables for the search contents.
     */
    suspend fun populateFtsData()

    /**
     * Query the contents matched with the [searchQuery] and returns it as a [Flow] of [SearchResult]
     */
    fun searchContents(searchQuery: String): Flow<com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.SearchResult>

    fun getSearchContentsCount(): Flow<Int>
}
