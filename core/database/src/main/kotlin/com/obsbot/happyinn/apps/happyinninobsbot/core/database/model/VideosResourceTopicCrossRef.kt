package com.obsbot.happyinn.apps.happyinninobsbot.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Cross reference for many to many relationship between [com.obsbot.happyinn.apps.happyinninobsbot.core.database.model.VideosResourceEntity] and [TopicEntity]
 */
@Entity(
    tableName = "videos_resources_topics",
    primaryKeys = ["videos_resource_id", "topic_id"],
    foreignKeys = [
        ForeignKey(
            entity = VideosResourceEntity::class,
            parentColumns = ["id"],
            childColumns = ["videos_resource_id"],
            onDelete = ForeignKey.CASCADE,
        ),
        ForeignKey(
            entity = TopicEntity::class,
            parentColumns = ["id"],
            childColumns = ["topic_id"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["videos_resource_id"]),
        Index(value = ["topic_id"]),
    ],
)
data class VideosResourceTopicCrossRef(
    @ColumnInfo(name = "videos_resource_id")
    val videosResourceId: String,
    @ColumnInfo(name = "topic_id")
    val topicId: String,
)
