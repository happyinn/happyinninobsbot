package com.obsbot.happyinn.apps.happyinninobsbot.core.data.repository


import com.obsbot.happyinn.apps.happyinninobsbot.core.data.model.RecentSearchQuery
import com.obsbot.happyinn.apps.happyinninobsbot.core.data.model.asExternalModel
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.news.RecentSearchQueryDao
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.news.RecentSearchQueryEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.datetime.Clock
import javax.inject.Inject
import kotlin.collections.map

internal class DefaultRecentSearchRepository @Inject constructor(
    private val recentSearchQueryDao: RecentSearchQueryDao,
) : RecentSearchRepository {
    override suspend fun insertOrReplaceRecentSearch(searchQuery: String) {
        recentSearchQueryDao.insertOrReplaceRecentSearchQuery(
            RecentSearchQueryEntity(
                query = searchQuery,
                queriedDate = Clock.System.now(),
            ),
        )
    }

    override fun getRecentSearchQueries(limit: Int): Flow<List<RecentSearchQuery>> =
        recentSearchQueryDao.getRecentSearchQueryEntities(limit).map { searchQueries ->
            searchQueries.map { it.asExternalModel() }
        }

    override suspend fun clearRecentSearches() = recentSearchQueryDao.clearRecentSearchQueries()
}
