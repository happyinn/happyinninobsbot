package com.google.samples.apps.nowinandroid.core.data.model

import com.obsbot.happyinn.apps.happyinninobsbot.core.database.model.TopicEntity
import com.obsbot.happyinn.apps.happyinninobsbot.core.network.model.NetworkTopic


fun NetworkTopic.asEntity() = TopicEntity(
    id = id,
    name = name,
    shortDescription = shortDescription,
    longDescription = longDescription,
    url = url,
    imageUrl = imageUrl,
)
