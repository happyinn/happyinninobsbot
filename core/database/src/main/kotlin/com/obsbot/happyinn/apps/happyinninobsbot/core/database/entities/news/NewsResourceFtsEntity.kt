package com.obsbot.happyinn.apps.happyinninobsbot.core.database.entities.news

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4

/**
 * Fts entity for the News resources. See https://developer.android.com/reference/androidx/room/Fts4.
 */
@Entity(tableName = "NewsResourcesFts")
@Fts4
data class NewsResourceFtsEntity(

    @ColumnInfo(name = "NewsResourceId")
    val newsResourceId: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "content")
    val content: String,
)

