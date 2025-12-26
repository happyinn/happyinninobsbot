package com.obsbot.happyinn.apps.happyinninobsbot.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

/**
 * Cross reference for many to many relationship between [com.obsbot.happyinn.apps.happyinninobsbot.core.database.model.NewsResourceEntity] and [TopicEntity]
 */
@Entity(
    tableName = "News_resources_topics",
    primaryKeys = ["News_resource_id", "topic_id"],
    foreignKeys = [
        ForeignKey(
            entity = NewsResourceEntity::class,
            parentColumns = ["id"],
            childColumns = ["News_resource_id"],
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
        Index(value = ["News_resource_id"]),
        Index(value = ["topic_id"]),
    ],
)
data class NewsResourceTopicCrossRef(
    @ColumnInfo(name = "News_resource_id")
    val newsResourceId: String,
    @ColumnInfo(name = "topic_id")
    val topicId: String,
)

