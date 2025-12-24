
package com.obsbot.happyinn.apps.happyinninobsbot.core.database.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.VideosResource

/**
 * External data layer representation of a fully populated NiA videos resource
 */
data class PopulatedVideosResource(
    @Embedded
    val entity: VideosResourceEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = VideosResourceTopicCrossRef::class,
            parentColumn = "videos_resource_id",
            entityColumn = "topic_id",
        ),
    )
    val topics: List<TopicEntity>,
)

fun PopulatedVideosResource.asExternalModel() = VideosResource(
    id = entity.id,
    title = entity.title,
    content = entity.content,
    url = entity.url,
    headerImageUrl = entity.headerImageUrl,
    publishDate = entity.publishDate,
    type = entity.type,
    topics = topics.map(TopicEntity::asExternalModel),
)

fun PopulatedVideosResource.asFtsEntity() = VideosResourceFtsEntity(
    videosResourceId = entity.id,
    title = entity.title,
    content = entity.content,
)
