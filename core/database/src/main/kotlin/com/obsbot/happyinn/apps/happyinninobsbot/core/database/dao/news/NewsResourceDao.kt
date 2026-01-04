package com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.news

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.news.PopulatedNewsResource
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.news.NewsResourceEntity
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.news.NewsResourceTopicCrossRef
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [NewsResource] and [NewsResourceEntity] access
 */
@Dao
interface NewsResourceDao {

    /**
     * Fetches News resources that match the query parameters
     */
    @Transaction
    @Query(
        value = """
            SELECT * FROM News_resources
            WHERE 
                CASE WHEN :useFilterNewsIds
                    THEN id IN (:filterNewsIds)
                    ELSE 1
                END
             AND
                CASE WHEN :useFilterTopicIds
                    THEN id IN
                        (
                            SELECT News_resource_id FROM News_resources_topics
                            WHERE topic_id IN (:filterTopicIds)
                        )
                    ELSE 1
                END
            ORDER BY publish_date DESC
    """,
    )
    fun getNewsResources(
        useFilterTopicIds: Boolean = false,
        filterTopicIds: Set<String> = emptySet(),
        useFilterNewsIds: Boolean = false,
        filterNewsIds: Set<String> = emptySet(),
    ): Flow<List<PopulatedNewsResource>>

    /**
     * Fetches ids of News resources that match the query parameters
     */
    @Transaction
    @Query(
        value = """
            SELECT id FROM News_resources
            WHERE 
                CASE WHEN :useFilterNewsIds
                    THEN id IN (:filterNewsIds)
                    ELSE 1
                END
             AND
                CASE WHEN :useFilterTopicIds
                    THEN id IN
                        (
                            SELECT News_resource_id FROM News_resources_topics
                            WHERE topic_id IN (:filterTopicIds)
                        )
                    ELSE 1
                END
            ORDER BY publish_date DESC
    """,
    )
    fun getNewsResourceIds(
        useFilterTopicIds: Boolean = false,
        filterTopicIds: Set<String> = emptySet(),
        useFilterNewsIds: Boolean = false,
        filterNewsIds: Set<String> = emptySet(),
    ): Flow<List<String>>

    /**
     * Inserts or updates [newsResourceEntities] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertNewsResources(newsResourceEntities: List<NewsResourceEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreTopicCrossRefEntities(
        newsResourceTopicCrossReferences: List<NewsResourceTopicCrossRef>,
    )

    /**
     * Deletes rows in the db matching the specified [ids]
     */
    @Query(
        value = """
            DELETE FROM News_resources
            WHERE id in (:ids)
        """,
    )
    suspend fun deleteNewsResources(ids: List<String>)
}

