package com.obsbot.happyinn.apps.happyinninobsbot.core.network.model

import com.obsbot.happyinn.apps.happyinninobsbot.core.model.data.Topic
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * Network representation of [Topic]
 */
@Serializable
@OptIn(InternalSerializationApi::class)
data class NetworkTopic(
    val id: String,
    val name: String = "",
    val shortDescription: String = "",
    val longDescription: String = "",
    val url: String = "",
    val imageUrl: String = "",
    val followed: Boolean = false,
)

fun NetworkTopic.asExternalModel(): Topic =
    Topic(
        id = id,
        name = name,
        shortDescription = shortDescription,
        longDescription = longDescription,
        url = url,
        imageUrl = imageUrl,
    )
