package com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.model.VideosResourceFtsEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [VideosResourceFtsEntity] access.
 */
@Dao
interface VideosResourceFtsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(videosResources: List<VideosResourceFtsEntity>)

    @Query("SELECT videosResourceId FROM videosResourcesFts WHERE videosResourcesFts MATCH :query")
    fun searchAllVideosResources(query: String): Flow<List<String>>

    @Query("SELECT count(*) FROM videosResourcesFts")
    fun getCount(): Flow<Int>
}
