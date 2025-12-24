package com.obsbot.happyinn.apps.happyinninobsbot.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4

/**
 * Fts entity for the videos resources. See https://developer.android.com/reference/androidx/room/Fts4.
 */
@Entity(tableName = "videosResourcesFts")
@Fts4
data class VideosResourceFtsEntity(

    @ColumnInfo(name = "videosResourceId")
    val videosResourceId: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "content")
    val content: String,
)
