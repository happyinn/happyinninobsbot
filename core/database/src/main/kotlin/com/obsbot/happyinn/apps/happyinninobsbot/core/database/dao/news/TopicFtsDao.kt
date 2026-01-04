package com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.news

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.news.TopicFtsEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [TopicFtsEntity] access.
 */
@Dao
interface TopicFtsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(topics: List<TopicFtsEntity>)

    @Query("SELECT topicId FROM topicsFts WHERE topicsFts MATCH :query")
    fun searchAllTopics(query: String): Flow<List<String>>

    @Query("SELECT count(*) FROM topicsFts")
    fun getCount(): Flow<Int>
}
