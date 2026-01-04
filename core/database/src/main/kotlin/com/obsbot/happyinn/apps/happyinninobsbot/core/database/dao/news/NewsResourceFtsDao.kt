package com.obsbot.happyinn.apps.happyinninobsbot.core.database.dao.news

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.news.NewsResourceFtsEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for [NewsResourceFtsEntity] access.
 */
@Dao
interface NewsResourceFtsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(newsResources: List<NewsResourceFtsEntity>)

    @Query("SELECT NewsResourceId FROM NewsResourcesFts WHERE NewsResourcesFts MATCH :query")
    fun searchAllNewsResources(query: String): Flow<List<String>>

    @Query("SELECT count(*) FROM NewsResourcesFts")
    fun getCount(): Flow<Int>
}

