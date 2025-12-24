package com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.model.PopulatedVideosResource
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.model.VideosResourceEntity
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.model.VideosResourceTopicCrossRef
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [VideosResource] and [VideosResourceEntity] access
 */
@Dao
interface VideosResourceDao {

    /**
     * Fetches videos resources that match the query parameters
     */
    @Transaction
    @Query(
        value = """
            SELECT * FROM videos_resources
            WHERE 
                CASE WHEN :useFilterVideosIds
                    THEN id IN (:filterVideosIds)
                    ELSE 1
                END
             AND
                CASE WHEN :useFilterTopicIds
                    THEN id IN
                        (
                            SELECT videos_resource_id FROM videos_resources_topics
                            WHERE topic_id IN (:filterTopicIds)
                        )
                    ELSE 1
                END
            ORDER BY publish_date DESC
    """,
    )
    fun getVideosResources(
        useFilterTopicIds: Boolean = false,
        filterTopicIds: Set<String> = emptySet(),
        useFilterVideosIds: Boolean = false,
        filterVideosIds: Set<String> = emptySet(),
    ): Flow<List<PopulatedVideosResource>>

    /**
     * Fetches ids of videos resources that match the query parameters
     */
    @Transaction
    @Query(
        value = """
            SELECT id FROM videos_resources
            WHERE 
                CASE WHEN :useFilterVideosIds
                    THEN id IN (:filterVideosIds)
                    ELSE 1
                END
             AND
                CASE WHEN :useFilterTopicIds
                    THEN id IN
                        (
                            SELECT videos_resource_id FROM videos_resources_topics
                            WHERE topic_id IN (:filterTopicIds)
                        )
                    ELSE 1
                END
            ORDER BY publish_date DESC
    """,
    )
    fun getVideosResourceIds(
        useFilterTopicIds: Boolean = false,
        filterTopicIds: Set<String> = emptySet(),
        useFilterVideosIds: Boolean = false,
        filterVideosIds: Set<String> = emptySet(),
    ): Flow<List<String>>

    /**
     * Inserts or updates [videosResourceEntities] in the db under the specified primary keys
     */
    @Upsert
    suspend fun upsertVideosResources(videosResourceEntities: List<VideosResourceEntity>)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreTopicCrossRefEntities(
        videosResourceTopicCrossReferences: List<VideosResourceTopicCrossRef>,
    )

    /**
     * Deletes rows in the db matching the specified [ids]
     */
    @Query(
        value = """
            DELETE FROM videos_resources
            WHERE id in (:ids)
        """,
    )
    suspend fun deleteVideosResources(ids: List<String>)
}
