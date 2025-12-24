package com.obsbot.happyinn.apps.happyinninobsbot.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.VideosResource
import kotlinx.datetime.Instant

/**
 * Defines an NiA videos resource.
 */
@Entity(
    tableName = "videos_resources",
)
data class VideosResourceEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val content: String,
    val url: String,
    @ColumnInfo(name = "header_image_url")
    val headerImageUrl: String?,
    @ColumnInfo(name = "publish_date")
    val publishDate: Instant,
    val type: String,
)

fun VideosResourceEntity.asExternalModel() = VideosResource(
    id = id,
    title = title,
    content = content,
    url = url,
    headerImageUrl = headerImageUrl,
    publishDate = publishDate,
    type = type,
    topics = emptyList(),
)
